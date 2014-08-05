package com.s890510.microfilm.shader;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.s890510.microfilm.ElementInfo;
import com.s890510.microfilm.MicroFilmActivity;
import com.s890510.microfilm.R;
import com.s890510.microfilm.draw.GLDraw;
import com.s890510.microfilm.draw.GLUtil;
import com.s890510.microfilm.mask.Mask;
import com.s890510.microfilm.script.effects.Effect;
import com.s890510.microfilm.util.Easing;

public class CoverShader extends Shader {
    private static final String TAG = "CoverShader";
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    public static final int FLOAT_SIZE_BYTES = 4; //float = 4bytes

    private int mProgram;
    private int mPositionHandle;
    private int mTextureHandle;
    private int mSamplerHandle;
    private int mAlphaHandle;
    private int mMVPMatrixHandle;
    private int mMVMMatrixHandle;
    private int mResolutionHandle;
    private int mSizeHandle;
    private int mTransHandle;
    private int mDirectHandle;
    private int mReverseHandle;
    private int mThemeHandle;
    private int mIsEmptyHandle;
    private int mBoundHandle;
    private int mSetBoundHandle;
    private int mLeftFilterHandle;
    private int mRightFilterHandle;
    private FloatBuffer mTriangleVertices;
    private float[] mMVPMatrix = new float[16];

    private Bitmap mBitmap = null;
    private int mColor = Color.WHITE;

    private GLDraw mGLDraw;

    public CoverShader(MicroFilmActivity activity, GLDraw gldraw) {
        super(activity);
        mGLDraw = gldraw;
        CreateProgram();
    }

    public void DrawRandar(float[] mModelMatrix, float[] mViewMatrix, float[] mProjectionMatrix,
            int mTextureId, ElementInfo mElementInfo, int mCoverType) {

        long mElapseTime;
        long timer = mElementInfo.timer.getElapse();
        Effect mEffect = mElementInfo.effect.getEffect(timer);

        if(mEffect == null) return;
        else mElapseTime = mElementInfo.effect.getElapseTime(timer);

        float[] mLeft = mGLDraw.getLeftFilter();
        float[] mRight = mGLDraw.getRightFilter();

        GLES20.glUseProgram(mProgram);

        if(mCoverType == Shader.EMPTY_LEFT) {

            if(mBitmap == null)
                CreateBitmap();

            GLES20.glActiveTexture(GLES20.GL_TEXTURE0+mTextureId);
            mActivity.mLoadTexture.BindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);

            GLES20.glUniform1i(mSamplerHandle, mTextureId);
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0+mTextureId);

            mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
            GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
                    20, mTriangleVertices);
            GLES20.glEnableVertexAttribArray(mPositionHandle);

            mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
            GLES20.glVertexAttribPointer(mTextureHandle, 3, GLES20.GL_FLOAT, false,
                    20, mTriangleVertices);
            GLES20.glEnableVertexAttribArray(mTextureHandle);

            GLES20.glUniform1f(mIsEmptyHandle, 1.0f);

            mCoverType = Shader.LEFT;
        } else {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0+mTextureId);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
            GLES20.glUniform1i(mSamplerHandle, mTextureId);

            if(mCoverType == Shader.STRING_LEFT) {
            	mGLDraw.mStringLoader.mStringTextureCoords.position(0);
                GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 0, mGLDraw.mStringLoader.mStringTextureCoords);
                GLES20.glEnableVertexAttribArray(mTextureHandle);

                mGLDraw.mStringLoader.mStringVertices.position(0);
                GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mGLDraw.mStringLoader.mStringVertices);
                GLES20.glEnableVertexAttribArray(mPositionHandle);

                mCoverType = Shader.LEFT;
            } else {
                mElementInfo.mSTextureCoords.position(0);
                GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 0, mElementInfo.mSTextureCoords);
                GLES20.glEnableVertexAttribArray(mTextureHandle);

                mElementInfo.mVertices.position(0);
                GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mElementInfo.mVertices);
                GLES20.glEnableVertexAttribArray(mPositionHandle);
            }

            GLES20.glUniform1f(mIsEmptyHandle, 0.0f);
        }

        GLES20.glUniform2f(mResolutionHandle, mGLDraw.ScreenWidth, mGLDraw.ScreenHeight);
        GLES20.glUniform4f(mLeftFilterHandle, mLeft[0], mLeft[1], mLeft[2], mLeft[3]);
        GLES20.glUniform4f(mRightFilterHandle, mRight[0], mRight[1], mRight[2], mRight[3]);
        if(mEffect.getTransition(mElapseTime)) {
            float duration = mEffect.getDuration(mElapseTime);
            float progress = mEffect.getProgressByElapse(mElapseTime);
            float elapse = progress*duration;

            if(mCoverType == Shader.LEFT || mCoverType == Shader.HALF_LEFT ||
                    mCoverType == Shader.RIGHT || mCoverType == Shader.HALF_RIGHT ||
                    mCoverType == Shader.GFRAG_LEFT || mCoverType == Shader.HALF_LEFT_Q || mCoverType == Shader.HALF_RIGHT_Q) {
                if(mCoverType == Shader.LEFT || mCoverType == Shader.HALF_LEFT || mCoverType == Shader.HALF_LEFT_Q) {
                    GLES20.glUniform1f(mDirectHandle, 0.0f);
                } else if(mCoverType == Shader.GFRAG_LEFT) {
                    GLES20.glUniform1f(mDirectHandle, 4.0f);
                } else {
                    GLES20.glUniform1f(mDirectHandle, 1.0f);
                }

                if(mCoverType == Shader.LEFT || mCoverType == Shader.GFRAG_LEFT) {
                    GLES20.glUniform1f(mSizeHandle, Easing.easeInOutCubic(elapse, 0, mGLDraw.ScreenRatio*2, duration) - mGLDraw.ScreenRatio);
                } else if(mCoverType == Shader.RIGHT) {
                    GLES20.glUniform1f(mSizeHandle, -(Easing.easeInOutCubic(elapse, 0, mGLDraw.ScreenRatio*2, duration) - mGLDraw.ScreenRatio));
                } else if(mCoverType == Shader.HALF_LEFT) {
                    GLES20.glUniform1f(mSizeHandle, Easing.easeInOutCubic(elapse, 0, mGLDraw.ScreenRatio, duration) - mGLDraw.ScreenRatio/2.0f);
                } else if(mCoverType == Shader.HALF_LEFT_Q) {
                    GLES20.glUniform1f(mSizeHandle, Easing.easeOutCubic(elapse, 0, mElementInfo.x*2, duration) - mElementInfo.x);
                } else if(mCoverType == Shader.HALF_RIGHT) {
                    GLES20.glUniform1f(mSizeHandle, -(Easing.easeInOutCubic(elapse, 0, mGLDraw.ScreenRatio, duration) - mGLDraw.ScreenRatio/2.0f));
                } else if(mCoverType == Shader.HALF_RIGHT_Q) {
                    GLES20.glUniform1f(mSizeHandle, -(Easing.easeOutCubic(elapse, 0, mElementInfo.x*2, duration) - mElementInfo.x));
                }
            } else if(mCoverType == Shader.TOP || mCoverType == Shader.HALF_TOP ||
                    mCoverType == Shader.BOTTOM || mCoverType == Shader.HALF_BOTTOM) {
                if(mCoverType == Shader.TOP || mCoverType == Shader.HALF_TOP) {
                    GLES20.glUniform1f(mDirectHandle, 2.0f);
                } else {
                    GLES20.glUniform1f(mDirectHandle, 3.0f);
                }

                if(mCoverType == Shader.TOP) {
                    GLES20.glUniform1f(mSizeHandle, Easing.easeInOutCubic(elapse, 0, 2, duration) - 1);
                } else if(mCoverType == Shader.BOTTOM) {
                    GLES20.glUniform1f(mSizeHandle, -(Easing.easeInOutCubic(elapse, 0, 2, duration) - 1));
                } else if(mCoverType == Shader.HALF_TOP) {
                    GLES20.glUniform1f(mSizeHandle, Easing.easeInOutCubic(elapse, 0, 1, duration) - 0.5f);
                } else if(mCoverType == Shader.HALF_BOTTOM) {
                    GLES20.glUniform1f(mSizeHandle, -(Easing.easeInOutCubic(elapse, 0, 1, duration) - 0.5f));
                }
            } else if(mCoverType == Shader.CENTER_H) {
                GLES20.glUniform1f(mDirectHandle, 5.0f);
                GLES20.glUniform1f(mSizeHandle, 1-Easing.easeInOutCubic(elapse, 0, 1, duration));
            } else if(mCoverType == Shader.PERCENT_L) {
                GLES20.glUniform1f(mDirectHandle, 0.0f);
                float[] mPos = mEffect.getRunPos(mElapseTime);
                GLES20.glUniform1f(mSizeHandle, (mPos[0] + ((mPos[1] - mPos[0])*Easing.easeInOutCubic(elapse, 0, 1, duration)))*mGLDraw.ScreenRatio*2 - mPos[1]*mGLDraw.ScreenRatio);
            } else if(mCoverType == Shader.PERCENT_B) {
                GLES20.glUniform1f(mDirectHandle, 3.0f);
                float[] mPos = mEffect.getRunPos(mElapseTime);
                GLES20.glUniform1f(mSizeHandle, -((mPos[0] + ((mPos[1] - mPos[0])*Easing.easeInOutCubic(elapse, 0, 1, duration)))*2 - mPos[1]));
            } else if(mCoverType == Shader.PERCENT_R) {
                GLES20.glUniform1f(mDirectHandle, 1.0f);
            }

            GLES20.glUniform1f(mTransHandle, 1.0f);

            if(mEffect.getMaskType(mElapseTime) == Mask.GONE) {
                GLES20.glUniform1f(mReverseHandle, 1.0f);
            } else {
                GLES20.glUniform1f(mReverseHandle, 0.0f);
            }
        } else if(mCoverType == Shader.PERCENT_B) {
            GLES20.glUniform1f(mDirectHandle, 3.0f);
            GLES20.glUniform1f(mTransHandle, 1.0f);
            float[] mPos = mEffect.getRunPos(mElapseTime);
            GLES20.glUniform1f(mSizeHandle, -(mPos[0]*2 - mPos[1]));
        } else if(mCoverType == Shader.PERCENT_L) {
            GLES20.glUniform1f(mDirectHandle, 0.0f);
            GLES20.glUniform1f(mTransHandle, 1.0f);
            float[] mPos = mEffect.getRunPos(mElapseTime);
            GLES20.glUniform1f(mSizeHandle, mPos[0]*mGLDraw.ScreenRatio*2 - mPos[1]*mGLDraw.ScreenRatio);
        } else {
            GLES20.glUniform1f(mTransHandle, 0.0f);
        }

        //Set show bound
        int mBound = mEffect.getFixBound(mElapseTime);
        if(mBound == Shader.BOUNDING) {
            float[] bound = new float[2]; //0: X, 1: Y
            bound[0] = mElementInfo.x;
            bound[1] = mElementInfo.y;
            GLES20.glUniform1f(mSetBoundHandle, 1.0f);
            GLES20.glUniform1fv(mBoundHandle, bound.length, bound, 0);
        } else if(mBound == Shader.LIMIT_COVER_X) {
            float[] mPos = mEffect.getRunPos(mElapseTime);
            float[] bound = new float[2];
            bound[0] = mPos[0]*mGLDraw.ScreenRatio*2 - mGLDraw.ScreenRatio;
            bound[1] = 0;
            GLES20.glUniform1f(mSetBoundHandle, 4.0f);
            GLES20.glUniform1fv(mBoundHandle, bound.length, bound, 0);
        } else {
            GLES20.glUniform1f(mSetBoundHandle, 0.0f);
        }

        GLES20.glUniform1f(mAlphaHandle, mEffect.getAlpha(mElapseTime));
        GLES20.glUniform1f(mThemeHandle, mGLDraw.getScriptFilter());

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVMMatrixHandle, 1, false, mModelMatrix, 0);
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisable(GLES20.GL_BLEND);

        checkGlError("CoverShader");
    }

    private void CreateProgram() {
        final int vertexShaderHandle = GLUtil.compileShader(GLES20.GL_VERTEX_SHADER, VertexShader());
        final int fragmentShaderHandle = GLUtil.compileShader(GLES20.GL_FRAGMENT_SHADER, FragmentShader());

        checkGlError("CoverShader");
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
        mSizeHandle = GLES20.glGetUniformLocation(mProgram, "mSize");
        mResolutionHandle = GLES20.glGetUniformLocation(mProgram, "resolution");

        mTransHandle = GLES20.glGetUniformLocation(mProgram, "mTrans");
        mDirectHandle = GLES20.glGetUniformLocation(mProgram, "mDirect");
        mReverseHandle = GLES20.glGetUniformLocation(mProgram, "mReverse");
        mThemeHandle = GLES20.glGetUniformLocation(mProgram, "mTheme");
        mIsEmptyHandle = GLES20.glGetUniformLocation(mProgram, "mIsEmpty");
        mSetBoundHandle = GLES20.glGetUniformLocation(mProgram, "mSetBound");
        mBoundHandle = GLES20.glGetUniformLocation(mProgram, "mBound");
        mLeftFilterHandle = GLES20.glGetUniformLocation(mProgram, "mLeft");
        mRightFilterHandle = GLES20.glGetUniformLocation(mProgram, "mRight");

        checkGlError("CoverCreateProgram");
    }

    public void CalcVertices() {
        float mRatio = mGLDraw.ScreenRatio;
        final float[] mTriangleVerticesData = {
            // X, Y, Z, U, V
            -mRatio, -1.0f, 0.0f, 0.0f, 0.0f,
             mRatio, -1.0f, 0.0f, 1.0f, 0.0f,
            -mRatio,  1.0f, 0.0f, 0.0f, 1.0f,
             mRatio,  1.0f, 0.0f, 1.0f, 1.0f
        };

        // Initialize the buffers.
        mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length * GLDraw.FLOAT_SIZE_BYTES)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();

        mTriangleVertices.put(mTriangleVerticesData).position(0);
    }

    private void CreateBitmap() {
        if(mBitmap == null)
            mBitmap = Bitmap.createBitmap(mGLDraw.ScreenWidth, mGLDraw.ScreenHeight, Bitmap.Config.ARGB_8888);

        Canvas canvasTemp = new Canvas(mBitmap);
        canvasTemp.drawColor(mColor);
    }

    private String VertexShader() {
        return getShaderRaw(R.raw.bitmap_vertex_cover_shader);
    }

    private String FragmentShader() {
        return getShaderRaw(R.raw.bitmap_fragment_cover_shader);
    }

    @Override
    public void Reset() {

    }
}
