package com.s890510.microfilm.draw;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.opengl.GLES20;
import android.opengl.Matrix;

import com.s890510.microfilm.FileInfo;
import com.s890510.microfilm.MicroFilmActivity;
import com.s890510.microfilm.script.Script;

public class GLDraw {
	public static final String TAG = "GLDraw";

	public static final int FLOAT_SIZE_BYTES = 4; //float = 4bytes
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    
    private ArrayList<FileInfo> mFileList = new ArrayList<FileInfo>();

    private MicroFilmActivity mActivity;
    public int mSpecialHash = 0;
    public Script mScript;
    
	private float[] mProjectionMatrix = new float[16];
	private float[] mMVPMatrix = new float[16];

	public int ScreenHeight;
    public int ScreenWidth;
    public float ScreenScale = 1.0f;
    public float ScreenRatio;
    private boolean mIsEncode = false;
    private boolean mVideoWaitUpdate = false;
    public StringLoader mStringLoader;

    private FloatBuffer mVertices;
    private int mProgram;
    private int mMVPMatrixHandle;
    private int mAlphaHandle;
    private float mAlpha = 1;
    private boolean mAlphaAdd = false;

    public GLDraw(MicroFilmActivity activity, boolean isEncode) {
    	mActivity = activity;
    	mIsEncode = isEncode;
	}

    public void setSpecialHash(int hash) {
        mSpecialHash = hash;
    }

    public boolean getVideoWait() {
        return mVideoWaitUpdate;
    }

    public boolean isEncode(){
        return mIsEncode;
    }

    public float[] getLeftFilter() {
        return mScript.getFilterLeft();
    }

    public float[] getRightFilter() {
        return mScript.getFilterRight();
    }

    public int getScriptFilter() {
        return mScript.getFilterNumber();
    }

    public String getFirstLocation() {
        if(mFileList.get(0).mGeoInfo != null) {
            if(mFileList.get(0).mGeoInfo.getLocation() != null) {
                return mFileList.get(0).mGeoInfo.getLocation().get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

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
                "uniform float mAlpha;	 " +
                "void main() {" +
                "    gl_FragColor = vec4(1.0, 0.0, 1.0, mAlpha);" +
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
        
        mAlphaHandle = GLES20.glGetUniformLocation(mProgram, "mAlpha");
	}

	public void draw() {
		GLES20.glUseProgram(mProgram);

		int attribPosition = GLES20.glGetAttribLocation(mProgram, "aPosition");
        GLES20.glEnableVertexAttribArray(attribPosition);

        mVertices.position(0);
        GLES20.glVertexAttribPointer(attribPosition, 3, GLES20.GL_FLOAT, false, 0, mVertices);

        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mMVPMatrix, 0, mProjectionMatrix, 0);
        
        if(mAlphaAdd) {
        	mAlpha += 0.01;
        	if(mAlpha >= 1) {
        		mAlphaAdd = false;
        	}
        } else {
        	mAlpha -= 0.01;
        	if(mAlpha <= 0) {
        		mAlphaAdd = true;
        	}
        }
        
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniform1f(mAlphaHandle, mAlpha);

        GLES20.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_DST_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 3);       
        
        GLES20.glDisable(GLES20.GL_BLEND);
    }

	public void setView(int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		
		final float ratio = (float) width/height;
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		
		Matrix.orthoM(mProjectionMatrix, 0, left, right, bottom, top, -1, 1);
	}

	public void setEye() {
		// TODO Auto-generated method stub
		
	}
}
