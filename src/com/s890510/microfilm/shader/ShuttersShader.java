package com.s890510.microfilm.shader;

import java.util.ArrayList;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.s890510.microfilm.ElementInfo;
import com.s890510.microfilm.MicroFilmActivity;
import com.s890510.microfilm.R;
import com.s890510.microfilm.draw.GLDraw;
import com.s890510.microfilm.draw.GLUtil;
import com.s890510.microfilm.script.effects.Effect;

public class ShuttersShader extends Shader {
    private static final String TAG = "ShuttersShader";
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    public static final int FLOAT_SIZE_BYTES = 4; //float = 4bytes
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;

    private int mProgram;
    private int mPositionHandle;
    private int[] mTextureHandle = new int[5];
    private int[] mSamplerHandle = new int[5];
    private int mNumTexture;
    private int mYHandle;
    private int mSizeHandle;
    private int mMVPMatrixHandle;
    private float[] mMVPMatrix = new float[16];
    private float[] mModelMatrix = new float[16];

    private float mYPos = 2.0f;
    private float mSize = 0.0f;
    
    private GLDraw mGLDraw;

    public ShuttersShader(MicroFilmActivity activity, GLDraw gldraw) {
        super(activity);
        mGLDraw = gldraw;
        CreateProgram();
    }

    public void DrawRandar(float[] mViewMatrix, float[] mProjectionMatrix,
            ArrayList<Integer> mTextureId, ArrayList<ElementInfo> mElementInfo) {

        if(mYPos > 0.0 && mElementInfo.size() > 1 && (!mActivity.checkPause() || mGLDraw.isEncode())) {
            Effect mEffect = mElementInfo.get(0).effect;
            float percent = (float)(mEffect.getDuration() - mElementInfo.get(0).timer.getElapse()) / (mEffect.getDuration() - mEffect.getSleep());
            mYPos = percent*2.0f;
            mSize = percent*0.25f;
        }

        GLES20.glUseProgram(mProgram);

        for(int i=0; i<mElementInfo.size(); i++) {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0+mTextureId.get(i));
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId.get(i));
            GLES20.glUniform1i(mSamplerHandle[i], mTextureId.get(i));

            mElementInfo.get(i).mSTextureCoords.position(0);
            GLES20.glVertexAttribPointer(mTextureHandle[i], 2, GLES20.GL_FLOAT, false, 0, mElementInfo.get(i).mSTextureCoords);
            GLES20.glEnableVertexAttribArray(mTextureHandle[i]);
        }

        mElementInfo.get(0).mVertices.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mElementInfo.get(0).mVertices);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glUniform1f(mNumTexture, (float)mElementInfo.size());

        GLES20.glUniform1f(mYHandle, mYPos-1.0f);
        //GLES20.glVertexAttrib1f(mSizeHandle, mSize);

        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        checkGlError("LatticeShader");
    }

    private void CreateProgram() {
        final int vertexShaderHandle = GLUtil.compileShader(GLES20.GL_VERTEX_SHADER, VertexShader());
        final int fragmentShaderHandle = GLUtil.compileShader(GLES20.GL_FRAGMENT_SHADER, FragmentShader());

        checkGlError("LatticeShader");
        //Create the new program
        mProgram = GLUtil.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle);
        if (mProgram == 0) {
            Log.e(TAG, "mProgram is 0");
            return;
        }

        // Set program handles. These will later be used to pass in values to the program.
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        for(int i=0; i<mTextureHandle.length; i++)
            mTextureHandle[(mTextureHandle.length-1)-i] = GLES20.glGetAttribLocation(mProgram, "aTextureCoord_" + (mTextureHandle.length-i));

        for(int i=0; i<mSamplerHandle.length; i++)
            mSamplerHandle[(mSamplerHandle.length-1)-i] = GLES20.glGetUniformLocation(mProgram, "Texture_" + (mSamplerHandle.length-i));

        mNumTexture = GLES20.glGetUniformLocation(mProgram, "numTexture");
        mYHandle = GLES20.glGetUniformLocation(mProgram, "mYValue");
        mSizeHandle = GLES20.glGetUniformLocation(mProgram, "mSize");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        checkGlError("LatticeCreateProgram");
    }

    private String VertexShader() {
        return getShaderRaw(R.raw.bitmap_vertex_shutters_shader);
    }

    private String FragmentShader() {
        return getShaderRaw(R.raw.bitmap_fragment_shutters_shader);
    }

    @Override
    public void Reset() {
        mSize = 2.0f;
    }
}
