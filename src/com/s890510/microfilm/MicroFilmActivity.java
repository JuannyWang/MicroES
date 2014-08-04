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
import android.app.ProgressDialog;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.Choreographer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.s890510.microfilm.draw.GLDraw;
import com.s890510.microfilm.render.RenderHandler;
import com.s890510.microfilm.render.RenderThread;
import com.s890510.microfilm.util.ThreadPool;

public class MicroFilmActivity extends Activity implements SurfaceHolder.Callback,
        Choreographer.FrameCallback {
    private static final String TAG = "MainActivity";

    private RenderThread mRenderThread;
    private GLDraw mGLDraw;
    
    private ProgressDialog mProgressDialog;
    
    private ThreadPool mLocationThreadPool;
    private ThreadPool mBitmapThreadPool;
    
    interface ISaveCallback{
        void onSaveDone();
    }
    private SaveCallback mSaveCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SurfaceView sv = (SurfaceView) findViewById(R.id.fboActivity_surfaceView);
        sv.getHolder().addCallback(this);
        sv.getHolder().setFormat(PixelFormat.TRANSPARENT);
        
        mGLDraw = new GLDraw();
        mSaveCallback = new SaveCallback();

        Log.d(TAG, "onCreate done");
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	final DoingEncoder mEncoder = new DoingEncoder();
    	mProgressDialog = ProgressDialog.show(this, "Encoding", "Please wait...");
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				mEncoder.Start(mSaveCallback);				
			}
		}).start();
    	
    	return super.onMenuItemSelected(featureId, item);
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
        mRenderThread.setName("GL render");
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
    
    public class SaveCallback implements ISaveCallback {

		@Override
		public void onSaveDone() {
			mProgressDialog.dismiss();			
		}
    }
    
    public synchronized ThreadPool getLocationThreadPool() {
        if (mLocationThreadPool == null) {
            mLocationThreadPool = new ThreadPool(1, 1, "Event-Location-pool");
        }
        return mLocationThreadPool;
    }

    public synchronized ThreadPool getBitmapThreadPool() {
        if (mBitmapThreadPool == null) {
            mBitmapThreadPool = new ThreadPool(4, 8, "Event-Bitmap-pool");
        }
        return mBitmapThreadPool;
    }
}
