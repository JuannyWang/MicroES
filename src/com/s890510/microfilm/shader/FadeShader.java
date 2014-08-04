package com.s890510.microfilm.shader;

import java.util.ArrayList;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.asus.gallery.R;
import com.asus.gallery.micromovie.ElementInfo;
import com.asus.gallery.micromovie.MicroMovieActivity;
import com.asus.gallery.micromovie.ProcessGL;
import com.asus.gallery.micromovie.ShaderHelper;
import com.s890510.microfilm.script.effects.Effect;

public class FadeShader extends Shader {
    private static final String TAG = "FadeShader";
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    public static final int FLOAT_SIZE_BYTES = 4; //float = 4bytes
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;

    private int mProgram;
    private int mPositionHandle;
    private int[] mTextureHandle = new int[2];
    private int[] mSamplerHandle = new int[2];
    private int mNumTexture;
    private int mAlphaHandle;
    private int mMVPMatrixHandle;
    private float[] mMVPMatrix = new float[16];
    private float[] mModelMatrix = new float[16];

    private float mAlpha = 1.0f;
    private ProcessGL mProcessGL;

    public FadeShader(MicroMovieActivity activity, ProcessGL processGL) {
        super(activity);
        mProcessGL = processGL;
        CreateProgram();
    }

    public void DrawRandar(float[] mViewMatrix, float[] mProjectionMatrix,
            ArrayList<Integer> mTextureId, ArrayList<ElementInfo> mElementInfo) {
        if(mAlpha >= 0.0 && mElementInfo.size() > 1 && (!mActivity.checkPause() || mProcessGL.isEncode())) {
            Effect mEffect = mElementInfo.get(0).effect;
            mAlpha =  (float)((1-(float)(mEffect.getDuration() - mElementInfo.get(0).timer.getElapse()) / (mEffect.getDuration() - mEffect.getSleep())));
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

        GLES20.glUniform1f(mAlphaHandle, mAlpha);

        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisable(GLES20.GL_BLEND);

        checkGlError("FadeShader");
    }

    private void CreateProgram() {
        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, VertexShader());
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, FragmentShader());

        checkGlError("FadeShader");
        //Create the new program
        mProgram = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle);
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
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mAlphaHandle = GLES20.glGetUniformLocation(mProgram, "mAlpha");
        checkGlError("FadeCreateProgram");
    }

    private String VertexShader() {
        return getShaderRaw(R.raw.bitmap_vertex_fade_shader);
    }

    private String FragmentShader() {
        return getShaderRaw(R.raw.bitmap_fragment_fade_shader);
    }

    @Override
    public void Reset() {
        mAlpha = 1.0f;
    }
}
