/*
 * Copyright 2013 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.s890510.microfilm;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Choreographer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.s890510.microfilm.draw.GLDraw;
import com.s890510.microfilm.draw.GLDraw_A;
import com.s890510.microfilm.render.RenderHandler;
import com.s890510.microfilm.render.RenderThread;

/**
 * Demonstrates efficient display + recording of OpenGL rendering using an FBO.  This
 * records only the GL surface (i.e. not the app UI, nav bar, status bar, or alert dialog).
 * <p>
 * This uses a plain SurfaceView, rather than GLSurfaceView, so we have full control
 * over the EGL config and rendering.  When available, we use GLES 3, which allows us
 * to do recording with one extra copy instead of two.
 * <p>
 * We use Choreographer so our animation matches vsync, and a separate rendering
 * thread to keep the heavy lifting off of the UI thread.  Ideally we'd let the render
 * thread receive the Choreographer events directly, but that appears to be creating
 * a permanent JNI global reference to the render thread object, preventing it from
 * being garbage collected (which, in turn, causes the Activity to be retained).  So
 * instead we receive the vsync on the UI thread and forward it.
 * <p>
 * If the rendering is fairly simple, it may be more efficient to just render the scene
 * twice (i.e. configure for display, call draw(), configure for video, call draw()).  If
 * the video being created is at a lower resolution than the display, rendering at the lower
 * resolution may produce better-looking results than a downscaling blit.
 * <p>
 * To reduce the impact of recording on rendering (which is probably a fancy-looking game),
 * we want to perform the recording tasks on a separate thread.  The actual video encoding
 * is performed in a separate process by the hardware H.264 encoder, so feeding input into
 * the encoder requires little effort.  The MediaMuxer step runs on the CPU and performs
 * disk I/O, so we really want to drain the encoder on a separate thread.
 * <p>
 * Some other examples use a pair of EGL contexts, configured to share state.  We don't want
 * to do that here, because GLES3 allows us to improve performance by using glBlitFramebuffer(),
 * and framebuffer objects aren't shared.  So we use a single EGL context for rendering to
 * both the display and the video encoder.
 * <p>
 * It might appear that shifting the rendering for the encoder input to a different thread
 * would be advantageous, but in practice all of the work is done by the GPU, and submitting
 * the requests from different CPU cores isn't going to matter.
 * <p>
 * As always, we have to be careful about sharing state across threads.  By fully configuring
 * the encoder before starting the encoder thread, we ensure that the new thread sees a
 * fully-constructed object.  The encoder object then "lives" in the encoder thread.  The main
 * thread doesn't need to talk to it directly, because all of the input goes through Surface.
 * <p>
 * TODO: add another bouncing rect that uses decoded video as a texture.  Useful for
 * evaluating simultaneous video playback and recording.
 * <p>
 * TODO: show the MP4 file name somewhere in the UI so people can find it in the player
 */
public class MainActivity extends Activity implements SurfaceHolder.Callback,
        Choreographer.FrameCallback {
    private static final String TAG = "RecordFBOActivity";

    private RenderThread mRenderThread;
    private GLDraw mGLDraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SurfaceView sv = (SurfaceView) findViewById(R.id.fboActivity_surfaceView);
        sv.getHolder().addCallback(this);
        sv.getHolder().setFormat(PixelFormat.TRANSPARENT);
        
        mGLDraw = new GLDraw_A();

        Log.d(TAG, "onCreate done");
    }

    @Override
    protected void onPause() {
        super.onPause();

        // TODO: we might want to stop recording here.  As it is, we continue "recording",
        //       which is pretty boring since we're not outputting any frames (test this
        //       by blanking the screen with the power button).

        // If the callback was posted, remove it.  This stops the notifications.  Ideally we
        // would send a message to the thread letting it know, so when it wakes up it can
        // reset its notion of when the previous Choreographer event arrived.
        Log.d(TAG, "onPause unhooking choreographer");
        Choreographer.getInstance().removeFrameCallback(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If we already have a Surface, we just need to resume the frame notifications.
        if (mRenderThread != null) {
            Log.d(TAG, "onResume re-hooking choreographer");
            Choreographer.getInstance().postFrameCallback(this);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated holder=" + holder);

        SurfaceView sv = (SurfaceView) findViewById(R.id.fboActivity_surfaceView);
        mRenderThread = new RenderThread(sv.getHolder(), MiscUtils.getDisplayRefreshNsec(this), mGLDraw);
        mRenderThread.setName("RecordFBO GL render");
        mRenderThread.start();
        mRenderThread.waitUntilReady();

        RenderHandler rh = mRenderThread.getHandler();
        if (rh != null) {
            rh.sendSurfaceCreated();
        }

        // start the draw events
        Choreographer.getInstance().postFrameCallback(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.d(TAG, "surfaceChanged fmt=" + format + " size=" + width + "x" + height +
                " holder=" + holder);
        RenderHandler rh = mRenderThread.getHandler();
        if (rh != null) {
            rh.sendSurfaceChanged(format, width, height);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "surfaceDestroyed holder=" + holder);

        // We need to wait for the render thread to shut down before continuing because we
        // don't want the Surface to disappear out from under it mid-render.  The frame
        // notifications will have been stopped back in onPause(), but there might have
        // been one in progress.
        //
        // TODO: the RenderThread doesn't currently wait for the encoder / muxer to stop,
        //       so we can't use this as an indication that the .mp4 file is complete.

        RenderHandler rh = mRenderThread.getHandler();
        if (rh != null) {
            rh.sendShutdown();
            try {
                mRenderThread.join();
            } catch (InterruptedException ie) {
                // not expected
                throw new RuntimeException("join was interrupted", ie);
            }
        }
        mRenderThread = null;

        // If the callback was posted, remove it.  Without this, we could get one more
        // call on doFrame().
        Choreographer.getInstance().removeFrameCallback(this);
        Log.d(TAG, "surfaceDestroyed complete");
    }

    /*
     * Choreographer callback, called near vsync.
     *
     * @see android.view.Choreographer.FrameCallback#doFrame(long)
     */
    @Override
    public void doFrame(long frameTimeNanos) {
        RenderHandler rh = mRenderThread.getHandler();
        if (rh != null) {
            Choreographer.getInstance().postFrameCallback(this);
            rh.sendDoFrame(frameTimeNanos);
        }
    }
}
