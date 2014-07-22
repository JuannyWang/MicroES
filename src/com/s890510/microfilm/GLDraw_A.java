package com.s890510.microfilm;

import android.opengl.GLES20;
import android.opengl.GLES30;

public class GLDraw_A extends GLDraw {

	@Override
	public void prepare() {
		// Disable depth testing -- we're 2D only.
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        // Don't need backface culling.  (If you're feeling pedantic, you can turn it on to
        // make sure we're defining our shapes correctly.)
        GLES20.glDisable(GLES20.GL_CULL_FACE);		
	}

	@Override
	public void draw() {
		GLES20.glClearColor(1.0f, 0.5f, 0.8f, 1.0f);
        GLES20.glClear(GLES30.GL_COLOR_BUFFER_BIT);	
        
        GLES20.glEnable(GLES20.GL_SCISSOR_TEST);
        GLES20.glScissor(80 / 2, 300, 180, 180);
        GLES20.glClearColor(1.0f, 0.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glScissor(200, 80 / 2, 180, 180);
        GLES20.glClearColor(0.0f, 1.0f, 0.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glDisable(GLES20.GL_SCISSOR_TEST);
	}
}
