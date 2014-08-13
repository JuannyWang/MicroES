package com.s890510.microfilm;

import com.s890510.microfilm.draw.GLDraw;
import com.s890510.microfilm.render.RenderHandler;
import com.s890510.microfilm.render.RenderThread;

import android.app.Activity;
import android.util.Log;
import android.view.Choreographer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class MicroFilmSurfaceView extends Activity implements SurfaceHolder.Callback, Choreographer.FrameCallback {
	private static final String TAG = "MicroFilmSurfaceView";
	
	private RenderThread mRenderThread;
	private GLDraw mGLDraw;
	
	public static final int MSG_STARTPROGRASS = 1;
    public static final int MSG_STOPPROGRASS = 2;
    public static final int MSG_FINCHANGESURFACE = 3;
    public static final int MSG_PLAYFIN = 4;

	public MicroFilmSurfaceView() {
		
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mRenderThread != null) {
            Log.d(TAG, "onResume re-hooking choreographer");
            Choreographer.getInstance().postFrameCallback(this);
        }
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		Choreographer.getInstance().removeFrameCallback(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
    @Override
    public void doFrame(long frameTimeNanos) {
        RenderHandler rh = mRenderThread.getHandler();
        if (rh != null) {
            Choreographer.getInstance().postFrameCallback(this);
            rh.sendDoFrame(frameTimeNanos);
        }
    }

	@Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d(TAG, "surfaceCreated holder=" + holder);

        /*
        SurfaceView sv = (SurfaceView) findViewById(R.id.fboActivity_surfaceView);
        mRenderThread = new RenderThread(sv.getHolder(), MiscUtils.getDisplayRefreshNsec(this), mGLDraw);
        mRenderThread.setName("GL render");
        mRenderThread.start();
        mRenderThread.waitUntilReady();
        */

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
}
