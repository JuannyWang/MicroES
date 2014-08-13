package com.s890510.microfilm.shader;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.s890510.microfilm.ElementInfo;
import com.s890510.microfilm.MicroMovieActivity;
import com.s890510.microfilm.ProcessGL;
import com.s890510.microfilm.R;
import com.s890510.microfilm.draw.GLUtil;
import com.s890510.microfilm.script.effects.Effect;

public class PhotoShader extends Shader {
    private static final String TAG        = "PhotoShader";

    private int                 mProgram;
    private int                 mPositionHandle;
    private int                 mTextureHandle;
    private int                 mSamplerHandle;
    private int                 mAlphaHandle;
    private int                 mMVPMatrixHandle;
    private int                 mMVMMatrixHandle;
    private int                 mResolutionHandle;
    private int                 mGapHandle;
    private int                 mBoundHandle;
    private int                 mSetBoundHandle;
    private float[]             mMVPMatrix = new float[16];

    private ProcessGL           mProcessGL;

    public PhotoShader(MicroMovieActivity activity, ProcessGL processGL) {
        super(activity);
        mProcessGL = processGL;
        CreateProgram();
    }

    public void DrawRandar(float[] mModelMatrix, float[] mViewMatrix, float[] mProjectionMatrix, int mTextureId, ElementInfo mElementInfo, int mType) {

        long mElapseTime;
        long timer = mElementInfo.timer.getElapse();
        Effect mEffect = mElementInfo.effect.getEffect(timer);

        if(mEffect == null)
            return;
        else
            mElapseTime = mElementInfo.effect.getElapseTime(timer);

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

        GLES20.glUniform1f(mAlphaHandle, mEffect.getAlpha(mElapseTime));
        GLES20.glUniform2f(mResolutionHandle, mProcessGL.ScreenWidth, mProcessGL.ScreenHeight);

        float scale = mElementInfo.effect.getScaleSize(mElementInfo.timer.getElapse());
        float ratio_a = (float) mProcessGL.ScreenHeight / (float) mProcessGL.ScreenWidth;
        int mBound = mEffect.getFixBound(mElapseTime);
        if(mBound == Shader.BOUNDING) {
            float[] mGap = new float[2]; // 0: X, 1: Y
            mGap[0] = mElementInfo.x * (1.0f - 0.04f * ratio_a);
            mGap[1] = mElementInfo.y * (1.0f - 0.04f);
            GLES20.glUniform1fv(mGapHandle, mGap.length, mGap, 0);

            float[] bound = new float[2]; // 0: X, 1: Y
            bound[0] = mElementInfo.x;
            bound[1] = mElementInfo.y;
            GLES20.glUniform1f(mSetBoundHandle, 1.0f);
            GLES20.glUniform1fv(mBoundHandle, bound.length, bound, 0);
        } else {
            float[] mGap = new float[2]; // 0: X, 1: Y
            if(mElementInfo.x * ratio_a < mElementInfo.y) {
                mGap[0] = mElementInfo.x * (1.0f - 0.04f) * scale;
                mGap[1] = mElementInfo.y * (1.0f - 0.04f) * scale;
            } else {
                mGap[0] = mElementInfo.x * (1.0f - 0.04f * ratio_a) * scale;
                mGap[1] = mElementInfo.y * (1.0f - 0.04f) * scale;
            }
            GLES20.glUniform1f(mSetBoundHandle, 0.0f);
            GLES20.glUniform1fv(mGapHandle, mGap.length, mGap, 0);
        }

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVMMatrixHandle, 1, false, mModelMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisable(GLES20.GL_BLEND);

        checkGlError("PhotoShader");
    }

    private void CreateProgram() {
        final int vertexShaderHandle = GLUtil.compileShader(GLES20.GL_VERTEX_SHADER, VertexShader());
        final int fragmentShaderHandle = GLUtil.compileShader(GLES20.GL_FRAGMENT_SHADER, FragmentShader());

        checkGlError("PhotoShader");
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
        mMVMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVMMatrix");
        mAlphaHandle = GLES20.glGetUniformLocation(mProgram, "mAlpha");
        mResolutionHandle = GLES20.glGetUniformLocation(mProgram, "resolution");
        mSetBoundHandle = GLES20.glGetUniformLocation(mProgram, "mSetBound");
        mGapHandle = GLES20.glGetUniformLocation(mProgram, "mGap");
        mBoundHandle = GLES20.glGetUniformLocation(mProgram, "mBound");

        checkGlError("PhotoCreateProgram");
    }

    private String VertexShader() {
        return getShaderRaw(R.raw.bitmap_vertex_photo_shader);
    }

    private String FragmentShader() {
        return getShaderRaw(R.raw.bitmap_fragment_photo_shader);
    }

    @Override
    public void Reset() {

    }
}
