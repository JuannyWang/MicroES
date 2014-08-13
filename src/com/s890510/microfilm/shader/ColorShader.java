package com.s890510.microfilm.shader;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.s890510.microfilm.ElementInfo;
import com.s890510.microfilm.MicroMovieActivity;
import com.s890510.microfilm.ProcessGL;
import com.s890510.microfilm.R;
import com.s890510.microfilm.draw.GLUtil;

public class ColorShader extends Shader {
    private static final String TAG              = "DefaultShader";
    public static final int     FLOAT_SIZE_BYTES = 4;                                 // float
                                                                                       // =
                                                                                       // 4bytes

    private int                 mProgram;
    private int                 mPositionHandle;
    private int                 mTextureHandle;
    private int                 mSamplerHandle;
    private int                 mAlphaHandle;
    private int                 mMVPMatrixHandle;
    private int                 mResolutionHandle;
    private int                 mMatrixHandle;
    private int                 mColorTypeHandle;
    private float[]             mMVPMatrix       = new float[16];

    private float               mAlpha           = 1.0f;
    private ProcessGL           mProcessGL;

    private float[]             mSepiaWeight     = { 805.0f / 2048.0f, 715.0f / 2048.0f, 557.0f / 2048.0f, 1575.0f / 2048.0f, 1405.0f / 2048.0f,
            1097.0f / 2048.0f, 387.0f / 2048.0f, 344.0f / 2048.0f, 268.0f / 2048.0f };

    public ColorShader(MicroMovieActivity activity, ProcessGL processGL) {
        super(activity);
        mProcessGL = processGL;
        CreateProgram();
    }

    public void DrawRandar(float[] mModelMatrix, float[] mViewMatrix, float[] mProjectionMatrix, int mTextureId, ElementInfo mElementInfo,
            int mColorType) {

        GLES20.glUseProgram(mProgram);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + mTextureId);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glUniform1i(mSamplerHandle, mTextureId);

        mElementInfo.mSTextureCoords.position(0);
        GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 0, mElementInfo.mSTextureCoords);
        GLES20.glEnableVertexAttribArray(mTextureHandle);

        mElementInfo.mVertices.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mElementInfo.mVertices);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glUniform1f(mAlphaHandle, mAlpha);
        GLES20.glUniform2f(mResolutionHandle, mProcessGL.ScreenWidth, mProcessGL.ScreenHeight);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        if(mColorType == SEPIA) {
            GLES20.glUniformMatrix3fv(mMatrixHandle, 1, false, mSepiaWeight, 0);
        }

        GLES20.glUniform1f(mColorTypeHandle, mColorType);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisable(GLES20.GL_BLEND);

        checkGlError("DefaultShader");
    }

    private void CreateProgram() {
        final int vertexShaderHandle = GLUtil.compileShader(GLES20.GL_VERTEX_SHADER, VertexShader());
        final int fragmentShaderHandle = GLUtil.compileShader(GLES20.GL_FRAGMENT_SHADER, FragmentShader());

        checkGlError("DefaultShader");
        // Create the new program
        mProgram = GLUtil.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle);
        if(mProgram == 0) {
            Log.e(TAG, "mProgram is 0");
            return;
        }

        // Set program handles. These will later be used to pass in values to
        // the program.
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        mSamplerHandle = GLES20.glGetUniformLocation(mProgram, "Texture");

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mAlphaHandle = GLES20.glGetUniformLocation(mProgram, "mAlpha");
        mResolutionHandle = GLES20.glGetUniformLocation(mProgram, "resolution");
        mMatrixHandle = GLES20.glGetUniformLocation(mProgram, "matrix");
        mColorTypeHandle = GLES20.glGetUniformLocation(mProgram, "mColorType");

        checkGlError("DefaultCreateProgram");
    }

    private String VertexShader() {
        return getShaderRaw(R.raw.bitmap_vertex_shader);
    }

    private String FragmentShader() {
        return getShaderRaw(R.raw.bitmap_fragment_color_shader);
    }

    @Override
    public void Reset() {
        mAlpha = 1.0f;
    }
}
