package com.s890510.microfilm.render;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;

import com.s890510.microfilm.draw.GLDraw;
import com.s890510.microfilm.gles.EglCore;
import com.s890510.microfilm.gles.FlatShadedProgram;
import com.s890510.microfilm.gles.GlUtil;
import com.s890510.microfilm.gles.WindowSurface;

/**
 * This class handles all OpenGL rendering.
 * <p>
 * We use Choreographer to coordinate with the device vsync. We deliver one
 * frame per vsync. We can't actually know when the frame we render will be
 * drawn, but at least we get a consistent frame interval.
 * <p>
 * Start the render thread after the Surface has been created.
 */
public class RenderThread extends Thread {
    private static final String    TAG        = "RenderThread";

    // Object must be created on render thread to get correct Looper, but is
    // used from
    // UI thread, so we need to declare it volatile to ensure the UI thread sees
    // a fully
    // constructed object.
    private volatile RenderHandler mHandler;

    // Used to wait for the thread to start.
    private Object                 mStartLock = new Object();
    private boolean                mReady     = false;
    private GLDraw                 mGLDraw;

    private volatile SurfaceHolder mSurfaceHolder;             // may be
                                                                // updated by UI
                                                                // thread
    private EglCore                mEglCore;
    private WindowSurface          mWindowSurface;
    private FlatShadedProgram      mProgram;

    private final float[]          mIdentityMatrix;

    // FPS / drop counter.
    private long                   mRefreshPeriodNanos;
    private long                   mFpsCountStartNanos;
    private int                    mFpsCountFrame;

    /**
     * Pass in the SurfaceView's SurfaceHolder. Note the Surface may not yet
     * exist.
     */
    public RenderThread(SurfaceHolder holder, long refreshPeriodNs, GLDraw glDraw) {
        mSurfaceHolder = holder;
        mRefreshPeriodNanos = refreshPeriodNs;
        mGLDraw = glDraw;

        mIdentityMatrix = new float[16];
        Matrix.setIdentityM(mIdentityMatrix, 0);
    }

    /**
     * Thread entry point.
     * <p>
     * The thread should not be started until the Surface associated with the
     * SurfaceHolder has been created. That way we don't have to wait for a
     * separate "surface created" message to arrive.
     */
    @Override
    public void run() {
        Looper.prepare();
        mHandler = new RenderHandler(this);
        mEglCore = new EglCore(null, EglCore.FLAG_RECORDABLE | EglCore.FLAG_TRY_GLES3);
        synchronized(mStartLock) {
            mReady = true;
            mStartLock.notify(); // signal waitUntilReady()
        }

        Looper.loop();

        Log.d(TAG, "looper quit");
        releaseGl();
        mEglCore.release();

        synchronized(mStartLock) {
            mReady = false;
        }
    }

    /**
     * Waits until the render thread is ready to receive messages.
     * <p>
     * Call from the UI thread.
     */
    public void waitUntilReady() {
        synchronized(mStartLock) {
            while(!mReady) {
                try {
                    mStartLock.wait();
                } catch(InterruptedException ie) { /* not expected */
                }
            }
        }
    }

    /**
     * Shuts everything down.
     */
    public void shutdown() {
        Log.d(TAG, "shutdown");
        Looper.myLooper().quit();
    }

    /**
     * Returns the render thread's Handler. This may be called from any thread.
     */
    public RenderHandler getHandler() {
        return mHandler;
    }

    /**
     * Prepares the surface.
     */
    public void surfaceCreated() {
        Surface surface = mSurfaceHolder.getSurface();
        prepareGl(surface);
    }

    /**
     * Prepares window surface and GL state.
     */
    private void prepareGl(Surface surface) {
        Log.d(TAG, "prepareGl");

        mWindowSurface = new WindowSurface(mEglCore, surface, false);
        mWindowSurface.makeCurrent();

        mGLDraw.prepare();
    }

    /**
     * Handles changes to the size of the underlying surface. Adjusts viewport
     * as needed. Must be called before we start drawing. (Called from
     * RenderHandler.)
     */
    public void surfaceChanged(int width, int height) {
        mGLDraw.setView(width, height);
        mGLDraw.setEye();
    }

    /**
     * Releases most of the GL resources we currently hold.
     * <p>
     * Does not release EglCore.
     */
    private void releaseGl() {
        GlUtil.checkGlError("releaseGl start");

        if(mWindowSurface != null) {
            mWindowSurface.release();
            mWindowSurface = null;
        }
        if(mProgram != null) {
            mProgram.release();
            mProgram = null;
        }

        GlUtil.checkGlError("releaseGl done");

        mEglCore.makeNothingCurrent();
    }

    /**
     * Advance state and draw frame in response to a vsync event.
     */
    public void doFrame(long timeStampNanos) {
        // If we're not keeping up 60fps -- maybe something in the system is
        // busy, maybe
        // recording is too expensive, maybe the CPU frequency governor thinks
        // we're
        // not doing and wants to drop the clock frequencies -- we need to drop
        // frames
        // to catch up. The "timeStampNanos" value is based on the system
        // monotonic
        // clock, as is System.nanoTime(), so we can compare the values
        // directly.
        //
        // Our clumsy collision detection isn't sophisticated enough to deal
        // with large
        // time gaps, but it's nearly cost-free, so we go ahead and do the
        // computation
        // either way.
        //
        // We can reduce the overhead of recording, as well as the size of the
        // movie,
        // by recording at ~30fps instead of the display refresh rate. As a
        // quick hack
        // we just record every-other frame, using a "recorded previous" flag.

        long diff = System.nanoTime() - timeStampNanos;
        long max = mRefreshPeriodNanos - 2000000; // if we're within 2ms, don't
                                                  // bother
        if(diff > max) {
            // too much, drop a frame
            Log.d(TAG, "diff is " + (diff / 1000000.0) + " ms, max " + (max / 1000000.0) + ", skipping render");
            return;
        }

        boolean swapResult;

        // Render the scene, swap back to front.
        mGLDraw.draw();
        swapResult = mWindowSurface.swapBuffers();

        if(!swapResult) {
            // This can happen if the Activity stops without waiting for us to
            // halt.
            Log.w(TAG, "swapBuffers failed, killing renderer thread");
            shutdown();
            return;
        }

        // Update the FPS counter.
        //
        // Ideally we'd generate something approximate quickly to make the UI
        // look
        // reasonable, then ease into longer sampling periods.
        final int NUM_FRAMES = 120;
        final long ONE_TRILLION = 1000000000000L;
        if(mFpsCountStartNanos == 0) {
            mFpsCountStartNanos = timeStampNanos;
            mFpsCountFrame = 0;
        } else {
            mFpsCountFrame++;
            if(mFpsCountFrame == NUM_FRAMES) {
                // compute thousands of frames per second
                long elapsed = timeStampNanos - mFpsCountStartNanos;

                // reset
                mFpsCountStartNanos = timeStampNanos;
                mFpsCountFrame = 0;
            }
        }
    }
}
