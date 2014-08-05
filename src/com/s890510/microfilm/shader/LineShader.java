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
import com.s890510.microfilm.MicroMovieActivity;
import com.s890510.microfilm.ProcessGL;
import com.s890510.microfilm.R;
import com.s890510.microfilm.draw.GLUtil;
import com.s890510.microfilm.mask.Mask;
import com.s890510.microfilm.script.effects.Effect;

public class LineShader extends Shader {
    private static final String TAG = "LineShader";
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;
    public static final int FLOAT_SIZE_BYTES = 4; //float = 4bytes

    private int mProgram;
    private int mPositionHandle;
    private int mTextureHandle;
    private int mSamplerHandle;
    private int mAlphaHandle;
    private int mMVPMatrixHandle;
    private int mResolutionHandle;
    private int mThemeHandle;
    private int mXPosHandle;
    private int mAreaHandle;
    private int mTransHandle;
    private int mNPosHandle;
    private int mStartHandle;
    private int mEndHandle;
    private int mReverseHandle;
    private int mIsStringHandle;
    private int mLeftFilterHandle;
    private int mRightFilterHandle;
    private FloatBuffer mTriangleVertices;
    private float[] mMVPMatrix = new float[16];

    private Bitmap mBitmap = null;
    private int mColor = Color.WHITE;
    private int mHashCode = 0;
    private boolean mIsReverse = false;

    private boolean mIsGone = false;
    private boolean mIsInit = false;
    private float ratio;
    private float mAlpha = 1.0f;
    private float mDist;
    private float[] mXPos = new float[10];
    private float[] mArea = new float[10];
    private float[] mWidth = {0.41f, 0.217f, 0.142f, 0.081f, 0.048f, 0.031f, 0.023f, 0.02f, 0.017f, 0.009f};
    private float[] mSTime = {0f, 0.052f, 0.105f, 0.157f, 0.210f, 0.263f, 0.315f, 0.368f, 0.421f, 0.473f};
    private float[] mETime = {0.526f, 0.578f, 0.631f, 0.684f, 0.736f, 0.789f, 0.842f, 0.894f, 0.947f, 1f};
    private float[] mStartPos = new float[10];
    private float[] mEndPos = new float[10];
    private ProcessGL mProcessGL;

    public LineShader(MicroMovieActivity activity, ProcessGL processGL) {
        super(activity);
        mProcessGL = processGL;
        CreateProgram();
    }

    public void init() {
        float length = 0;
        ratio = ((float)mProcessGL.ScreenWidth/(float)mProcessGL.ScreenHeight);
        mDist = ratio*2;

        for(int i=0; i<10; i++) {
            mArea[i] = ratio * 2 * mWidth[i];
            mXPos[i] = ratio;

            if(i == 0) {
                mStartPos[i] = ratio;
                mEndPos[i] = -ratio;
            } else {
                mStartPos[i] = ratio + length;
                mEndPos[i] = -ratio + length;
            }

            length += mArea[i];
        }

        mIsInit = true;

        CalcVertices();
    }

    public void DrawRandar(float[] mModelMatrix, float[] mViewMatrix, float[] mProjectionMatrix,
            int mTextureId, ElementInfo mElementInfo, int mType) {

        long mElapseTime;
        long timer = mElementInfo.timer.getElapse();
        Effect mEffect = mElementInfo.effect.getEffect(timer);

        if(mEffect == null) return;
        else mElapseTime = mElementInfo.effect.getElapseTime(timer);

        boolean IsGone = mEffect.getMaskType(mElapseTime) == Mask.GONE;

        boolean mIsTrans = mEffect.getTransition(mElapseTime);
        float progress = mEffect.getProgressByElapse(mElapseTime);

        float[] mLeft = mProcessGL.getLeftFilter();
        float[] mRight = mProcessGL.getRightFilter();

        if((!mIsTrans && !mIsInit) || IsGone != mIsGone || mHashCode != mElementInfo.hashCode() ||
                mIsReverse != IsGone) {
            Log.e(TAG, "Do reset");
            CreateProgram();
            mIsGone = IsGone;
            mIsInit = true;
            mHashCode = mElementInfo.hashCode();
            mIsReverse = IsGone;
        }

        if(!mIsGone && progress < 0.05f && mIsTrans) return;

        GLES20.glUseProgram(mProgram);

        if(mType == Shader.EMPTY) {
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
        } else {
            GLES20.glActiveTexture(GLES20.GL_TEXTURE0+mTextureId);
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
            GLES20.glUniform1i(mSamplerHandle, mTextureId);

            if(mType == Shader.STRING) {
            	mProcessGL.mStringLoader.mStringTextureCoords.position(0);
                GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 0, mProcessGL.mStringLoader.mStringTextureCoords);
                GLES20.glEnableVertexAttribArray(mTextureHandle);

                mProcessGL.mStringLoader.mStringVertices.position(0);
                GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mProcessGL.mStringLoader.mStringVertices);
                GLES20.glEnableVertexAttribArray(mPositionHandle);

                GLES20.glUniform1f(mIsStringHandle, 1.0f);
            } else {
                mElementInfo.mSTextureCoords.position(0);
                GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 0, mElementInfo.mSTextureCoords);
                GLES20.glEnableVertexAttribArray(mTextureHandle);

                mElementInfo.mVertices.position(0);
                GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mElementInfo.mVertices);
                GLES20.glEnableVertexAttribArray(mPositionHandle);

                GLES20.glUniform1f(mIsStringHandle, 0.0f);
            }
        }

        GLES20.glUniform1f(mAlphaHandle, mEffect.getAlpha(mElapseTime));
        GLES20.glUniform2f(mResolutionHandle, mProcessGL.ScreenWidth, mProcessGL.ScreenHeight);
        GLES20.glUniform4f(mLeftFilterHandle, mLeft[0], mLeft[1], mLeft[2], mLeft[3]);
        GLES20.glUniform4f(mRightFilterHandle, mRight[0], mRight[1], mRight[2], mRight[3]);
        GLES20.glUniform1f(mThemeHandle, mProcessGL.getScriptFilter());

        if(mIsTrans && ((mIsGone && progress > 0.05f) || !mIsGone)) {
            progress = progress*10/9.5f;
            int mStart = 0, mEnd = 0;
            float mPos = -ratio;
            for(int i=0; i<10; i++) {
                if(progress < mSTime[i]) {
                    mEnd = i;
                    break;
                }

                if(progress < mETime[i]) {
                    float eprogress = (progress - mSTime[i])/(mETime[i]-mSTime[i]);
                    mXPos[i] = mStartPos[i] - (mDist * eprogress);
                } else {
                    mXPos[i] = mEndPos[i];
                    if(i < 9) {
                        mPos = mEndPos[i+1];
                    } else {
                        mPos = ratio*2-mArea[9];
                    }
                    mStart = i+1;
                }

                if(IsGone)
                    mXPos[i] = -mXPos[i];
            }

            if(IsGone) {
                GLES20.glUniform1f(mNPosHandle, -mPos);
                GLES20.glUniform1f(mReverseHandle, 1.0f);
            } else {
                GLES20.glUniform1f(mNPosHandle, mPos);
                GLES20.glUniform1f(mReverseHandle, 0.0f);
            }
            GLES20.glUniform1i(mStartHandle, mStart);

            if(mEnd == 0) mEnd = 10;
            GLES20.glUniform1i(mEndHandle, mEnd);

            GLES20.glUniform1f(mTransHandle, 1.0f);
            GLES20.glUniform1fv(mXPosHandle, mXPos.length, mXPos, 0);
            GLES20.glUniform1fv(mAreaHandle, mArea.length, mArea, 0);

            mIsInit = false;
        }

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisable(GLES20.GL_BLEND);

        checkGlError("LineShader");
    }

    private void CreateProgram() {
        if(mProgram != 0) {
            GLES20.glDeleteProgram(mProgram);
        }
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
        mAlphaHandle = GLES20.glGetUniformLocation(mProgram, "mAlpha");
        mResolutionHandle = GLES20.glGetUniformLocation(mProgram, "resolution");
        mThemeHandle = GLES20.glGetUniformLocation(mProgram, "mTheme");

        mXPosHandle = GLES20.glGetUniformLocation(mProgram, "mXPos");
        mAreaHandle = GLES20.glGetUniformLocation(mProgram, "mArea");

        mTransHandle = GLES20.glGetUniformLocation(mProgram, "mTrans");
        mNPosHandle = GLES20.glGetUniformLocation(mProgram, "mNPos");
        mStartHandle = GLES20.glGetUniformLocation(mProgram, "mStart");
        mEndHandle = GLES20.glGetUniformLocation(mProgram, "mEnd");

        mReverseHandle = GLES20.glGetUniformLocation(mProgram, "mReverse");
        mIsStringHandle = GLES20.glGetUniformLocation(mProgram, "mIsString");
        mLeftFilterHandle = GLES20.glGetUniformLocation(mProgram, "mLeft");
        mRightFilterHandle = GLES20.glGetUniformLocation(mProgram, "mRight");

        checkGlError("DefaultCreateProgram");

        Reset();
    }

    private void CalcVertices() {
        float mRatio = mProcessGL.ScreenRatio;
        final float[] mTriangleVerticesData = {
            // X, Y, Z, U, V
            -mRatio, -1.0f, 0.0f, 0.0f, 0.0f,
             mRatio, -1.0f, 0.0f, 1.0f, 0.0f,
            -mRatio,  1.0f, 0.0f, 0.0f, 1.0f,
             mRatio,  1.0f, 0.0f, 1.0f, 1.0f
        };

        // Initialize the buffers.
        mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length * ProcessGL.FLOAT_SIZE_BYTES)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();

        mTriangleVertices.put(mTriangleVerticesData).position(0);
    }

    private void CreateBitmap() {
        if(mBitmap == null)
            mBitmap = Bitmap.createBitmap(mProcessGL.ScreenWidth, mProcessGL.ScreenHeight, Bitmap.Config.ARGB_8888);

        Canvas canvasTemp = new Canvas(mBitmap);
        canvasTemp.drawColor(mColor);
    }

    private String VertexShader() {
        return getShaderRaw(R.raw.bitmap_vertex_line_shader);
    }

    private String FragmentShader() {
        return getShaderRaw(R.raw.bitmap_fragment_line_shader);
    }

    @Override
    public void Reset() {
        for(int i=0; i<mXPos.length; i++) {
            mXPos[i] = 2;
        }
    }
}
