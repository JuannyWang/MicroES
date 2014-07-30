package com.s890510.microfilm.gles;

import com.s890510.microfilm.GLDraw;

import android.opengl.GLES20;
import android.os.Looper;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;


public class EglRender extends Thread {
	private static final String TAG = "EglRander";

	// Object must be created on render thread to get correct Looper, but is used from
    // UI thread, so we need to declare it volatile to ensure the UI thread sees a fully
    // constructed object.
    private volatile EglRenderHandler mHandler;

    // Used to wait for the thread to start.
    private Object mStartLock = new Object();
    
    private volatile SurfaceHolder mSurfaceHolder;  // may be updated by UI thread
    private EglCore mEglCore;
    private EglWindowSurface mWindowSurface;
    private boolean mReady = false;
    private GLDraw mGLDraw;

    /**
     * Pass in the SurfaceView's SurfaceHolder.  Note the Surface may not yet exist.
     */
    public EglRender(SurfaceHolder holder, GLDraw mDraw) {
        mSurfaceHolder = holder;
        mGLDraw = mDraw;
    }

    /**
     * Thread entry point.
     * <p>
     * The thread should not be started until the Surface associated with the SurfaceHolder
     * has been created.  That way we don't have to wait for a separate "surface created"
     * message to arrive.
     */
    @Override
    public void run() {
        Looper.prepare();
        mHandler = new EglRenderHandler(this);
        mEglCore = new EglCore(0);
        synchronized (mStartLock) {
            mReady = true;
            mStartLock.notify();    // signal waitUntilReady()
        }

        Looper.loop();

        Log.d(TAG, "looper quit");
        releaseGL();
        mEglCore.release();

        synchronized (mStartLock) {
            mReady = false;
        }
    }

	/**
     * Waits until the render thread is ready to receive messages.
     * <p>
     * Call from the UI thread.
     */
    public void waitUntilReady() {
        synchronized (mStartLock) {
            while (!mReady) {
                try {
                    mStartLock.wait();
                } catch (InterruptedException ie) { /* not expected */ }
            }
        }
    }

    /**
     * Prepares window surface and GL state.
     */
    private void prepareGL(Surface surface) {
        Log.d(TAG, "prepareGL");

        mWindowSurface = new EglWindowSurface(mEglCore, surface, false);
        mWindowSurface.makeCurrent();

        mGLDraw.prepare();
    }

	public void surfaceCreated() {
		Surface surface = mSurfaceHolder.getSurface();
        prepareGL(surface);
	}

	public void surfaceChanged(int width, int height) {
		Log.d(TAG, "surfaceChanged " + width + "x" + height);
		
		// Use full window.
		mGLDraw.setView(width, height);
		mGLDraw.setEye();
	}

	public void doFrame() {
		boolean swapResult;
		Log.w(TAG, "doFrame");
		
		EglUtil.checkEglError("draw start");
		
		mGLDraw.draw();
        
        swapResult = mWindowSurface.swapBuffers();
        
        if (!swapResult) {
            // This can happen if the Activity stops without waiting for us to halt.
            Log.w(TAG, "swapBuffers failed, killing renderer thread");
            shutdown();
            return;
        }
	}

	public void shutdown() {
		Log.d(TAG, "shutdown");
        Looper.myLooper().quit();
	}

	/**
     * Returns the render thread's Handler.  This may be called from any thread.
     */
    public EglRenderHandler getHandler() {
        return mHandler;
    }

    private void releaseGL() {
    	int[] values = new int[1];

        if (mWindowSurface != null) {
            mWindowSurface.release();
            mWindowSurface = null;
        }
        
        mEglCore.makeNothingCurrent();		
	}
}
