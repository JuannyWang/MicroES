package com.s890510.microfilm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class GLDraw_A extends GLDraw {
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];

    private FloatBuffer mVertices;
    private int mProgram;
    private int mMVPMatrixHandle;

	@Override
	public void prepare() {
		// Disable depth testing -- we're 2D only.
        GLES20.glDisable(GLES20.GL_DEPTH_TEST);

        // Don't need backface culling.  (If you're feeling pedantic, you can turn it on to
        // make sure we're defining our shapes correctly.)
        GLES20.glDisable(GLES20.GL_CULL_FACE);
        
        final String vertexShaderSource =
                "uniform mat4 uMVPMatrix;" +
                "attribute vec4 aPosition;" +
                "void main() {" +
                "    gl_Position = uMVPMatrix * aPosition;" +
                "}";

        final String fragmentShaderSource =
                "precision mediump float;" +
                "void main() {" +
                "    gl_FragColor = vec4(1.0, 0.0, 1.0, 1.0);" +
                "}";
        
        final int vertexShaderHandle = GLUtil.compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderSource);
        final int fragmentShaderHandle = GLUtil.compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource);

        mProgram = GLUtil.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle);
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        
        final float[] mVerticesData = {
                0.0f, 0.5f, 0.0f,
                -0.5f, -0.5f, 0.0f,
                0.5f, -0.5f, 0.0f
        };
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * 4)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
	}

	@Override
	public void draw() {
		GLES20.glUseProgram(mProgram);

		int attribPosition = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(attribPosition);

        mVertices.position(0);
        GLES20.glVertexAttribPointer(attribPosition, 3, GLES20.GL_FLOAT, false, 0, mVertices);

        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix, 0, mProjectionMatrix, 0);
        
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        //GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);
    }

	@Override
	public void setView(int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		
		final float ratio = (float) width/height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		
		Matrix.orthoM(mProjectionMatrix, 0, left, right, bottom, top, -1, 1);
	}

	@Override
	public void setEye() {
		// TODO Auto-generated method stub
		
	}
}
