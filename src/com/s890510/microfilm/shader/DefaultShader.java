package com.s890510.microfilm.shader;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.s890510.microfilm.ElementInfo;
import com.s890510.microfilm.MicroFilmActivity;
import com.s890510.microfilm.R;
import com.s890510.microfilm.draw.GLDraw;
import com.s890510.microfilm.draw.GLUtil;
import com.s890510.microfilm.draw.StringLoader;
import com.s890510.microfilm.script.effects.Effect;

public class DefaultShader extends Shader {
    private static final String TAG = "DefaultShader";
    public static final int FLOAT_SIZE_BYTES = 4; //float = 4bytes

    private int mProgram;
    private int mPositionHandle;
    private int mTextureHandle;
    private int mSamplerHandle;
    private int mAlphaHandle;
    private int mMVPMatrixHandle;
    private int mMVMMatrixHandle;
    private int mResolutionHandle;
    private int mThemeHandle;
    private int mStringBKHandle;
    private int mStringBKRHandle;
    private int mStringBKGHandle;
    private int mStringBKBHandle;
    private int mBoundHandle;
    private int mSetBoundHandle;
    private int mLeftFilterHandle;
    private int mRightFilterHandle;
    private float[] mMVPMatrix = new float[16];
    private GLDraw mGLDraw;

    private float mAlpha = 1.0f;

    public DefaultShader(MicroFilmActivity activity, GLDraw gldraw) {
        super(activity);
        mGLDraw = gldraw;
        CreateProgram();
    }

    public void DrawRandar(float[] mModelMatrix, float[] mViewMatrix, float[] mProjectionMatrix,
            int mTextureId, ElementInfo mElementInfo, int mType) {

        long mElapseTime;
        long timer = mElementInfo.timer.getElapse();
        Effect mEffect = mElementInfo.effect.getEffect(timer);

        if(mEffect == null) return;
        else mElapseTime = mElementInfo.effect.getElapseTime(timer);

        float[] mLeft = mGLDraw.getLeftFilter();
        float[] mRight = mGLDraw.getRightFilter();

        GLES20.glUseProgram(mProgram);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+mTextureId);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glUniform1i(mSamplerHandle, mTextureId);

        if(mType == Shader.STRING) {
        	mGLDraw.mStringLoader.mStringTextureCoords.position(0);
            GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 0, mGLDraw.mStringLoader.mStringTextureCoords);
            GLES20.glEnableVertexAttribArray(mTextureHandle);

            mGLDraw.mStringLoader.mStringVertices.position(0);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mGLDraw.mStringLoader.mStringVertices);
            GLES20.glEnableVertexAttribArray(mPositionHandle);
        } else {
            mElementInfo.mSTextureCoords.position(0);
            GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 0, mElementInfo.mSTextureCoords);
            GLES20.glEnableVertexAttribArray(mTextureHandle);

            mElementInfo.mVertices.position(0);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mElementInfo.mVertices);
            GLES20.glEnableVertexAttribArray(mPositionHandle);
        }

        GLES20.glUniform1f(mAlphaHandle, mEffect.getAlpha(mElapseTime));
        GLES20.glUniform2f(mResolutionHandle, mGLDraw.ScreenWidth, mGLDraw.ScreenHeight);
        GLES20.glUniform4f(mLeftFilterHandle, mLeft[0], mLeft[1], mLeft[2], mLeft[3]);
        GLES20.glUniform4f(mRightFilterHandle, mRight[0], mRight[1], mRight[2], mRight[3]);

        if(mType == Shader.STRING) {
            GLES20.glUniform1f(mThemeHandle, 0);

            int SType = mEffect.getMaskType(mElapseTime);
            if(SType == StringLoader.STRING_NOBK || SType == StringLoader.STRING_WHITE_NOBK || SType == StringLoader.STRING_WHITE_NOBK_ANIM ||
                    SType == StringLoader.STRING_WHITE_NOBK_GEO || SType == StringLoader.STRING_DATE_CITY || SType == StringLoader.STRING_NOBK_SCALE ||
                    SType == StringLoader.STRING_YEAR_COUNTRY || SType == StringLoader.STRING_YEAR_COUNTRY_FADEIN || SType == StringLoader.STRING_YEAR_COUNTRY_FADEOUT ||
                    SType == StringLoader.STRING_WHITE_NOBK_LINE || SType == StringLoader.STRING_WHITE_NOBK_LOVER || SType == StringLoader.STRING_DATE_LOVER || SType == StringLoader.STRING_DATE_CITY_TRANS ||
                    SType == StringLoader.STRING_KIDS_CIRCLE_DATE || SType == StringLoader.STRING_KIDS_ICON_A || SType == StringLoader.STRING_KIDS_ICON_B) {
                if(SType == StringLoader.STRING_WHITE_NOBK || SType == StringLoader.STRING_WHITE_NOBK_ANIM || SType == StringLoader.STRING_DATE_CITY ||
                        SType == StringLoader.STRING_YEAR_COUNTRY || SType == StringLoader.STRING_YEAR_COUNTRY_FADEIN || SType == StringLoader.STRING_YEAR_COUNTRY_FADEOUT ||
                        SType == StringLoader.STRING_WHITE_NOBK_LINE || SType == StringLoader.STRING_WHITE_NOBK_LOVER || SType == StringLoader.STRING_DATE_CITY_TRANS ||
                        SType == StringLoader.STRING_KIDS_ICON_A || SType == StringLoader.STRING_KIDS_ICON_B) {
                    GLES20.glUniform1f(mStringBKHandle, 2);
                } else if(SType == StringLoader.STRING_DATE_LOVER || SType == StringLoader.STRING_KIDS_CIRCLE_DATE) {
                    GLES20.glUniform1f(mStringBKHandle, 3);
                } else {
                    GLES20.glUniform1f(mStringBKHandle, 1);
                }

                GLES20.glUniform1f(mStringBKRHandle, mGLDraw.mScript.ColorRed());
                GLES20.glUniform1f(mStringBKGHandle, mGLDraw.mScript.ColorGreen());
                GLES20.glUniform1f(mStringBKBHandle, mGLDraw.mScript.ColorBlue());
            } else if(SType == StringLoader.STRING_FADE || SType == StringLoader.STRING_FADE_LIGHT) {
                GLES20.glUniform1f(mStringBKHandle, 4);
            } else {
                GLES20.glUniform1f(mStringBKHandle, 0);
            }
            Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        } else {
            GLES20.glUniform1f(mThemeHandle, mGLDraw.getScriptFilter());
            GLES20.glUniform1f(mStringBKHandle, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        }

        //Set show bound
        int mBound = mEffect.getFixBound(mElapseTime);
        if(mBound == Shader.BOUNDING) {
            float[] bound = new float[2]; //0: X, 1: Y
            bound[0] = mElementInfo.x;
            bound[1] = mElementInfo.y;
            GLES20.glUniform1f(mSetBoundHandle, 1.0f);
            GLES20.glUniform1fv(mBoundHandle, bound.length, bound, 0);
        } else if(mBound == Shader.LIMIT_X) {
            float[] mPos = mEffect.getRunPos(mElapseTime);
            GLES20.glUniform1f(mSetBoundHandle, 2.0f);
            GLES20.glUniform1fv(mBoundHandle, mPos.length, mPos, 0);
        } else if(mBound == Shader.LIMIT_Y) {
            float[] mPos = mEffect.getRunPos(mElapseTime);
            GLES20.glUniform1f(mSetBoundHandle, 3.0f);
            GLES20.glUniform1fv(mBoundHandle, mPos.length, mPos, 0);
        } else if(mBound == Shader.LIMIT_Y_TOP) {
            float[] mPos = mEffect.getRunPos(mElapseTime);
            GLES20.glUniform1f(mSetBoundHandle, 4.0f);
            GLES20.glUniform1fv(mBoundHandle, mPos.length, mPos, 0);
        } else if(mBound == Shader.LIMIT_Y_TB) {
            float[] mPos = mEffect.getRunPos(mElapseTime);
            GLES20.glUniform1f(mSetBoundHandle, 5.0f);
            GLES20.glUniform1fv(mBoundHandle, mPos.length, mPos, 0);
        } else {
            GLES20.glUniform1f(mSetBoundHandle, 0.0f);
        }

        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVMMatrixHandle, 1, false, mModelMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

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
        //Create the new program
        mProgram = GLUtil.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle);
        if (mProgram == 0) {
            Log.e(TAG, "mProgram is 0");
            return;
        }

        // Set program handles. These will later be used to pass in values to the program.
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        mSamplerHandle = GLES20.glGetUniformLocation(mProgram, "Texture");

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mMVMMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVMMatrix");
        mAlphaHandle = GLES20.glGetUniformLocation(mProgram, "mAlpha");
        mResolutionHandle = GLES20.glGetUniformLocation(mProgram, "resolution");
        mThemeHandle = GLES20.glGetUniformLocation(mProgram, "mTheme");
        mStringBKHandle = GLES20.glGetUniformLocation(mProgram, "mString_BK");
        mStringBKRHandle = GLES20.glGetUniformLocation(mProgram, "mString_BKR");
        mStringBKGHandle = GLES20.glGetUniformLocation(mProgram, "mString_BKG");
        mStringBKBHandle = GLES20.glGetUniformLocation(mProgram, "mString_BKB");
        mSetBoundHandle = GLES20.glGetUniformLocation(mProgram, "mSetBound");
        mBoundHandle = GLES20.glGetUniformLocation(mProgram, "mBound");
        mLeftFilterHandle = GLES20.glGetUniformLocation(mProgram, "mLeft");
        mRightFilterHandle = GLES20.glGetUniformLocation(mProgram, "mRight");

        checkGlError("DefaultCreateProgram");
    }

    private String VertexShader() {
        return getShaderRaw(R.raw.bitmap_vertex_shader);
    }

    private String FragmentShader() {
        return getShaderRaw(R.raw.bitmap_fragment_shader);
    }

    @Override
    public void Reset() {
        mAlpha = 1.0f;
    }
}
