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
import com.s890510.microfilm.util.Easing;

public class LatticeShader extends Shader {
    private static final String TAG              = "LatticeShader";
    public static final int     FLOAT_SIZE_BYTES = 4;              // float =
                                                                    // 4bytes

    private int                 mProgram;
    private int                 mPositionHandle;
    private int                 mTextureHandle;
    private int                 mSamplerHandle;
    private int                 mAlphaHandle;
    private int                 mSizeHandle;
    private int                 mMVPMatrixHandle;
    private int                 mMotionHandle;
    private int                 mThemeHandle;
    private int                 mResolutionHandle;
    private int                 mRadiusHandle;
    private int                 mLeftFilterHandle;
    private int                 mRightFilterHandle;
    private float[]             mMVPMatrix       = new float[16];
    private ProcessGL           mProcessGL;

    public LatticeShader(MicroMovieActivity activity, ProcessGL processGL) {
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

        float[] mLeft = mProcessGL.getLeftFilter();
        float[] mRight = mProcessGL.getRightFilter();

        if(mEffect.getMaskType(mElapseTime) != 0 && mType != Shader.LATTICE_BLUE_BAR_GONE_STRING) {
            mType = mEffect.getMaskType(mElapseTime);
        }

        GLES20.glUseProgram(mProgram);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + mTextureId);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glUniform1i(mSamplerHandle, mTextureId);

        if(mType == Shader.LATTICE_BLUE_BAR_GONE_STRING) {
            mProcessGL.mStringLoader.mStringTextureCoords.position(0);
            GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 0, mProcessGL.mStringLoader.mStringTextureCoords);
            GLES20.glEnableVertexAttribArray(mTextureHandle);

            mProcessGL.mStringLoader.mStringVertices.position(0);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mProcessGL.mStringLoader.mStringVertices);
            GLES20.glEnableVertexAttribArray(mPositionHandle);

            mType = Shader.LATTICE_BLUE_BAR_GONE;
        } else {
            mElementInfo.mSTextureCoords.position(0);
            GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 0, mElementInfo.mSTextureCoords);
            GLES20.glEnableVertexAttribArray(mTextureHandle);

            mElementInfo.mVertices.position(0);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mElementInfo.mVertices);
            GLES20.glEnableVertexAttribArray(mPositionHandle);
        }

        GLES20.glUniform1f(mSizeHandle, 0);
        GLES20.glUniform1f(mThemeHandle, mProcessGL.getScriptFilter());
        GLES20.glVertexAttrib2f(mResolutionHandle, mProcessGL.ScreenWidth, mProcessGL.ScreenHeight);
        GLES20.glUniform4f(mLeftFilterHandle, mLeft[0], mLeft[1], mLeft[2], mLeft[3]);
        GLES20.glUniform4f(mRightFilterHandle, mRight[0], mRight[1], mRight[2], mRight[3]);

        if(mEffect.getTransition(mElapseTime)) {
            float progress = mEffect.getProgressByElapse(mElapseTime);
            float duration = mEffect.getDuration(mElapseTime);

            if(mType == Shader.LATTICE_TILTED_RIGHT_R) {
                GLES20.glUniform1f(mRadiusHandle, (mProcessGL.ScreenWidth + mProcessGL.ScreenHeight) / 8);
                if(progress < 0.45) {
                    GLES20.glUniform1f(mMotionHandle, 1);
                    GLES20.glUniform1f(mSizeHandle, Easing.easeOutCubic(progress * 10 / 4.5f * duration, 0, 4, duration) - 2.0f);
                } else if(progress > 0.55) {
                    GLES20.glUniform1f(mMotionHandle, 2);
                    GLES20.glUniform1f(mSizeHandle, Easing.easeOutCubic((progress - 0.55f) * 10 / 4.5f * duration, 0, 4, duration) - 2.0f);
                } else {
                    GLES20.glUniform1f(mMotionHandle, 3);
                }
            } else if(mType == Shader.LATTICE_CROSS_4) {
                GLES20.glUniform1f(mRadiusHandle, (mProcessGL.ScreenWidth + mProcessGL.ScreenHeight) / 4);
                GLES20.glUniform1f(mMotionHandle, 4);
                GLES20.glUniform1f(mSizeHandle, Easing.easeOutCubic(progress * duration, 0, 4, duration) - 2.0f);
            } else if(mType == Shader.LATTICE_CROSS_2) {
                GLES20.glUniform1f(mRadiusHandle, (mProcessGL.ScreenWidth + mProcessGL.ScreenHeight) / 2);
                GLES20.glUniform1f(mMotionHandle, 5);
                GLES20.glUniform1f(mSizeHandle, Easing.easeOutCubic(progress * duration, 0, 4, duration) - 2.0f);
            } else if(mType == Shader.LATTICE_BLUE_BAR) {
                GLES20.glUniform1f(mMotionHandle, 7);
                GLES20.glUniform1f(mSizeHandle, progress * 6.0f - 3.0f);
            } else if(mType == Shader.LATTICE_BLUE_BAR_GONE) {
                GLES20.glUniform1f(mMotionHandle, 8);
                GLES20.glUniform1f(mSizeHandle, progress * 6.0f - 3.0f);
            } else {
                GLES20.glUniform1f(mMotionHandle, 6);
                GLES20.glUniform1f(mSizeHandle, (1 - progress) * 2.0f);
                GLES20.glUniform1f(mRadiusHandle, mProcessGL.ScreenHeight / 20);
            }
        } else {
            GLES20.glUniform1f(mMotionHandle, 0);
        }

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniform1f(mAlphaHandle, mEffect.getAlpha(mElapseTime));

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisable(GLES20.GL_BLEND);

        checkGlError("LatticeShader");
    }

    private void CreateProgram() {
        final int vertexShaderHandle = GLUtil.compileShader(GLES20.GL_VERTEX_SHADER, VertexShader());
        final int fragmentShaderHandle = GLUtil.compileShader(GLES20.GL_FRAGMENT_SHADER, FragmentShader());

        checkGlError("LatticeShader");
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

        mSizeHandle = GLES20.glGetUniformLocation(mProgram, "mSize");
        mAlphaHandle = GLES20.glGetUniformLocation(mProgram, "mAlpha");
        mMotionHandle = GLES20.glGetUniformLocation(mProgram, "mMotion");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mThemeHandle = GLES20.glGetUniformLocation(mProgram, "mTheme");
        mResolutionHandle = GLES20.glGetAttribLocation(mProgram, "resolution");
        mRadiusHandle = GLES20.glGetUniformLocation(mProgram, "mRadius");
        mLeftFilterHandle = GLES20.glGetUniformLocation(mProgram, "mLeft");
        mRightFilterHandle = GLES20.glGetUniformLocation(mProgram, "mRight");
        checkGlError("LatticeCreateProgram");
    }

    private String VertexShader() {
        return getShaderRaw(R.raw.bitmap_vertex_lattice_shader);
    }

    private String FragmentShader() {
        return getShaderRaw(R.raw.bitmap_fragment_lattice_shader);
    }

    @Override
    public void Reset() {

    }
}
