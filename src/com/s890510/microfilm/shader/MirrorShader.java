package com.s890510.microfilm.shader;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.asus.gallery.R;
import com.asus.gallery.micromovie.ElementInfo;
import com.asus.gallery.micromovie.MicroMovieActivity;
import com.asus.gallery.micromovie.ProcessGL;
import com.asus.gallery.micromovie.ShaderHelper;
import com.asus.gallery.micromovie.Util;
import com.s890510.microfilm.script.effects.Effect;

public class MirrorShader extends Shader {
    private static final String TAG = "MirrorShader";
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    public static final int FLOAT_SIZE_BYTES = 4; //float = 4bytes
    private static final int TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;

    private int mProgram;
    private int mPositionHandle;
    private int mTextureHandle;
    private int mSamplerHandle;
    private int mAlphaHandle;
    private int mMVPMatrixHandle;
    private int mResolutionHandle;
    private int mSizeHandle;
    private int mTransHandle;
    private int mDirectHandle;
    private int mCoverHandle;
    private int mThemeHandle;
    private int mLeftFilterHandle;
    private int mRightFilterHandle;
    private float[] mMVPMatrix = new float[16];
    private ProcessGL mProcessGL;

    public MirrorShader(MicroMovieActivity activity, ProcessGL processGL) {
        super(activity);
        mProcessGL = processGL;
        CreateProgram();
    }

    public void DrawRandar(float[] mModelMatrix, float[] mViewMatrix, float[] mProjectionMatrix,
            int mTextureId, ElementInfo mElementInfo, int mMirrorType) {

        long mElapseTime;
        long timer = mElementInfo.timer.getElapse();
        Effect mEffect = mElementInfo.effect.getEffect(timer);

        if(mEffect == null) return;
        else mElapseTime = mElementInfo.effect.getElapseTime(timer);

        float[] mLeft = mProcessGL.getLeftFilter();
        float[] mRight = mProcessGL.getRightFilter();

        float duration = mEffect.getDuration(mElapseTime);
        float progress = mEffect.getProgressByElapse(mElapseTime);
        float elapse = progress*duration;
        boolean mTrans = mEffect.getTransition(mElapseTime);

        GLES20.glUseProgram(mProgram);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+mTextureId);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glUniform1i(mSamplerHandle, mTextureId);

        //First
        mElementInfo.mSTextureCoords.position(0);
        GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 0, mElementInfo.mSTextureCoords);
        GLES20.glEnableVertexAttribArray(mTextureHandle);

        mElementInfo.mVertices.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mElementInfo.mVertices);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glUniform2f(mResolutionHandle, mProcessGL.ScreenWidth, mProcessGL.ScreenHeight);
        GLES20.glUniform1f(mAlphaHandle, mEffect.getAlpha(mElapseTime));
        GLES20.glUniform4f(mLeftFilterHandle, mLeft[0], mLeft[1], mLeft[2], mLeft[3]);
        GLES20.glUniform4f(mRightFilterHandle, mRight[0], mRight[1], mRight[2], mRight[3]);

        GLES20.glUniform1f(mSizeHandle, 0);

        if(mMirrorType == Shader.MIRROR_VERTICAL) { //Left
            GLES20.glUniform1f(mDirectHandle, 0.0f);
            GLES20.glUniform1f(mTransHandle, 0.0f);
        } else if(mMirrorType == Shader.MIRROR_VERTICAL_TB) {
            GLES20.glUniform1f(mDirectHandle, 0.0f);

            if(mEffect.getTransition(mElapseTime) && progress < 0.6) {
                GLES20.glUniform1f(mTransHandle, 1.0f);

                GLES20.glUniform1f(mCoverHandle, 3.0f);
                GLES20.glUniform1f(mSizeHandle, -(Util.easeInOutCubic(elapse, 0, 2, duration*0.6f) - 1));
            } else {
                GLES20.glUniform1f(mTransHandle, 0.0f);
            }
        } else if(mMirrorType == Shader.MIRROR_TILTED_LEFT) {
            GLES20.glUniform1f(mDirectHandle, 3.0f);
            GLES20.glUniform1f(mTransHandle, 0.0f);
        } else if(mMirrorType == Shader.MIRROR_TILTED_MASK || mMirrorType == Shader.MIRROR_TILTED) {
            if(mTrans) {
                GLES20.glUniform1f(mDirectHandle, 4.0f);
                GLES20.glUniform1f(mTransHandle, 0.0f);
            } else {
                GLES20.glUniform1f(mDirectHandle, -1.0f);
                GLES20.glUniform1f(mTransHandle, 0.0f);
            }
        }

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisable(GLES20.GL_BLEND);

        if((mMirrorType == Shader.MIRROR_TILTED_MASK && mTrans) || mMirrorType != Shader.MIRROR_TILTED_MASK) {
            //Second
            mElementInfo.mSTextureCoords.position(0);
            GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 0, mElementInfo.mSTextureCoords);
            GLES20.glEnableVertexAttribArray(mTextureHandle);

            mElementInfo.mVertices.position(0);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mElementInfo.mVertices);
            GLES20.glEnableVertexAttribArray(mPositionHandle);

            GLES20.glUniform2f(mResolutionHandle, mProcessGL.ScreenWidth, mProcessGL.ScreenHeight);
            GLES20.glUniform1f(mAlphaHandle, mEffect.getAlpha(mElapseTime));
            GLES20.glUniform4f(mLeftFilterHandle, mLeft[0], mLeft[1], mLeft[2], mLeft[3]);
            GLES20.glUniform4f(mRightFilterHandle, mRight[0], mRight[1], mRight[2], mRight[3]);
            GLES20.glUniform1f(mSizeHandle, 0);
            GLES20.glUniform1f(mThemeHandle, mProcessGL.getScriptFilter());

            if(mMirrorType == Shader.MIRROR_VERTICAL) {
                GLES20.glUniform1f(mDirectHandle, 1.0f);
                GLES20.glUniform1f(mTransHandle, 0.0f);
            } else if(mMirrorType == Shader.MIRROR_VERTICAL_TB) {
                GLES20.glUniform1f(mDirectHandle, 1.0f);

                if(mEffect.getTransition(mElapseTime) && progress < 0.6) {
                    GLES20.glUniform1f(mTransHandle, 1.0f);

                    GLES20.glUniform1f(mCoverHandle, 2.0f);
                    GLES20.glUniform1f(mSizeHandle, Util.easeInOutCubic(elapse, 0, 2, duration*0.6f) - 1);
                } else {
                    GLES20.glUniform1f(mTransHandle, 0.0f);
                }
            } else if(mMirrorType == Shader.MIRROR_TILTED_LEFT) {
                GLES20.glUniform1f(mDirectHandle, 2.0f);
                GLES20.glUniform1f(mTransHandle, 0.0f);
            } else if(mMirrorType == Shader.MIRROR_TILTED_MASK || mMirrorType == Shader.MIRROR_TILTED) {
                GLES20.glUniform1f(mDirectHandle, 5.0f);
                GLES20.glUniform1f(mTransHandle, 0.0f);
            }

            Matrix.invertM(mModelMatrix, 0, mModelMatrix, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
            Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

            GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

            GLES20.glEnable(GLES20.GL_BLEND);
            GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);

            GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

            GLES20.glDisable(GLES20.GL_BLEND);
        }

        checkGlError("MirrorShader");
    }

    private void CreateProgram() {
        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, VertexShader());
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, FragmentShader());

        checkGlError("MirrorShader");
        //Create the new program
        mProgram = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle);
        if (mProgram == 0) {
            Log.e(TAG, "mProgram is 0");
            return;
        }

        // Set program handles. These will later be used to pass in values to the program.
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        mSamplerHandle = GLES20.glGetUniformLocation(mProgram, "Texture");

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mAlphaHandle = GLES20.glGetUniformLocation(mProgram, "mAlpha");
        mSizeHandle = GLES20.glGetUniformLocation(mProgram, "mSize");
        mResolutionHandle = GLES20.glGetUniformLocation(mProgram, "resolution");

        mTransHandle = GLES20.glGetUniformLocation(mProgram, "mTrans");
        mDirectHandle = GLES20.glGetUniformLocation(mProgram, "mDirect");
        mCoverHandle = GLES20.glGetUniformLocation(mProgram, "mCover");
        mThemeHandle = GLES20.glGetUniformLocation(mProgram, "mTheme");
        mLeftFilterHandle = GLES20.glGetUniformLocation(mProgram, "mLeft");
        mRightFilterHandle = GLES20.glGetUniformLocation(mProgram, "mRight");

        checkGlError("MirrorCreateProgram");
    }

    private String VertexShader() {
        return getShaderRaw(R.raw.bitmap_vertex_mirror_shader);
    }

    private String FragmentShader() {
        return getShaderRaw(R.raw.bitmap_fragment_mirror_shader);
    }

    @Override
    public void Reset() {

    }
}
