package com.s890510.microfilm;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Choreographer;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.s890510.microfilm.gles.EglRender;
import com.s890510.microfilm.gles.EglRenderHandler;

public class MainActivity extends Activity implements SurfaceHolder.Callback, Choreographer.FrameCallback {
	private static final String TAG = "MainActivity";
	private EglRender mEglRander;
	private GLDraw mGLDraw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        SurfaceView mSurfaceView = (SurfaceView) findViewById(R.id.Activity_surfaceView);
        mSurfaceView.getHolder().addCallback(this);
        
        mGLDraw = new GLDraw_A(); 
        
        Log.e(TAG, "Finish MainActivity onCreate");
    }

    @Override
    protected void onResume() {
    	super.onResume();
    	if (mEglRander != null) {
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
	public void doFrame(long frameTimeNanos) {
		EglRenderHandler rh = mEglRander.getHandler();
        if (rh != null) {
            Choreographer.getInstance().postFrameCallback(this);
            rh.sendDoFrame();
        }
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		Log.d(TAG, "surfaceCreated holder=" + holder);
		SurfaceView mSurfaceView = (SurfaceView) findViewById(R.id.Activity_surfaceView);
		mEglRander = new EglRender(mSurfaceView.getHolder(), mGLDraw);
		mEglRander.start();
		mEglRander.waitUntilReady();
		
		EglRenderHandler rh = mEglRander.getHandler();
        if (rh != null) {
            rh.sendSurfaceCreated();
        }

        // start the draw events
        Choreographer.getInstance().postFrameCallback(this);
	}


	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		EglRenderHandler rh = mEglRander.getHandler();
        if (rh != null) {
            rh.sendSurfaceChanged(format, width, height);
        }
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		EglRenderHandler rh = mEglRander.getHandler();
        if (rh != null) {
            rh.sendShutdown();
            try {
            	mEglRander.join();
            } catch (InterruptedException ie) {
                // not expected
                throw new RuntimeException("join was interrupted", ie);
            }
        }
        mEglRander = null;

        // If the callback was posted, remove it.  Without this, we could get one more
        // call on doFrame().
        Choreographer.getInstance().removeFrameCallback(this);
	}
    
}
