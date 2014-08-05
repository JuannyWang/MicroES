package com.s890510.microfilm.mask;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.s890510.microfilm.ElementInfo;
import com.s890510.microfilm.MicroMovieActivity;
import com.s890510.microfilm.ProcessGL;
import com.s890510.microfilm.R;
import com.s890510.microfilm.draw.GLUtil;
import com.s890510.microfilm.util.Easing;

public class CircleBorderMask extends Mask {
    private static final String TAG = "CircleBorderMask";
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;

    private int mProgram;
    private int mPositionHandle;
    private int mSamplerHandle;
    private int mMVPMatrixHandle;
    private FloatBuffer mTriangleVertices;
    private float[] mMVPMatrix = new float[16];
    private float[] mModelMatrix = new float[16];
    private Bitmap mBitmap = null;
    private int LType;

    private int mTextureHandle;
    private ProcessGL mProcessGL;

    public CircleBorderMask(MicroMovieActivity activity, ProcessGL processGL) {
        super(activity);
        mProcessGL = processGL;
        CreateProgram();
    }

    void DrawRandar(float[] mViewMatrix, float[] mProjectionMatrix, ElementInfo mElementInfo, int mTextureId) {
        long timer = mElementInfo.timer.getElapse();

        int mMask = mElementInfo.effect.getMaskType(timer);
        if(mMask == Mask.GONE || mMask == Mask.NONE) {
            return;
        }

        float duration = mElementInfo.effect.getDuration(timer);
        float progress = mElementInfo.effect.getProgressByElapse(timer);
        float elapse = progress*duration;
        boolean transition = mElementInfo.effect.getTransition(timer);

        if(mMask == TRANS_IN_SMALL || mMask == TRANS_IN_BIG || mMask == TRANS_OUT ||
                mMask == TRANS_OUT_BACK || mMask == TRANS_OUT_FULL || LType != mMask || mBitmap == null) {
            generateMask(progress, elapse, duration, transition, mMask);
        }

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+mTextureId);
        mActivity.mLoadTexture.BindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);

        GLES20.glUseProgram(mProgram);

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

        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    private void CreateProgram() {
        final int vertexShaderHandle = GLUtil.compileShader(GLES20.GL_VERTEX_SHADER, VertexShader());
        final int fragmentShaderHandle = GLUtil.compileShader(GLES20.GL_FRAGMENT_SHADER, FragmentShader());

        checkGlError("BorderMask");
        //Create the new program
        mProgram = GLUtil.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle);
        if (mProgram == 0) {
            Log.e(TAG, "mProgram is 0");
            return;
        }

        // Set program handles. These will later be used to pass in values to the program.
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mSamplerHandle = GLES20.glGetUniformLocation(mProgram, "Texture");

        checkGlError("BorderMaskCreateProgram");
    }

    public void CalcVertices() {
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

    private void generateMask(float progress, float elapse, float duration, boolean transition, int mMask) {

        if(mBitmap == null)
            mBitmap = Bitmap.createBitmap(mProcessGL.ScreenWidth, mProcessGL.ScreenHeight, Bitmap.Config.ARGB_8888);

        Canvas canvasTemp = new Canvas(mBitmap);
        canvasTemp.drawARGB(255, 255, 255, 255);

        Paint mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setAntiAlias(true);

        float radius;
        if(mMask == TRANS_IN_SMALL) {
            radius = mProcessGL.ScreenHeight/2*Easing.easeInOutBack(elapse, 0.0f, 0.8f, duration);
        } else if(mMask == TRANS_IN_BIG) {
            radius = mProcessGL.ScreenHeight/2*(mProcessGL.ScreenRatio - Easing.easeOutExpo(elapse, 0.0f, mProcessGL.ScreenRatio, duration));
        } else if(mMask == TRANS_OUT) {
            radius = mProcessGL.ScreenHeight/2*Easing.easeInExpo(elapse, 0.8f, mProcessGL.ScreenRatio, duration);
        } else if(mMask == TRANS_OUT_FULL) {
            radius = mProcessGL.ScreenHeight/2*Easing.easeInExpo(elapse, 0.0f, mProcessGL.ScreenRatio, duration);
        } else if(mMask == TRANS_OUT_BACK) {
            if(progress < 0.2) {
                radius = mProcessGL.ScreenHeight/2*(0.8f-Easing.easeInExpo((progress*10/2)*duration, 0.0f, 0.1f, duration));
            } else {
                radius = mProcessGL.ScreenHeight/2*Easing.easeInExpo(((progress-0.2f)*10/8)*duration, 0.7f, mProcessGL.ScreenRatio, duration);
            }
        } else {
            radius = mProcessGL.ScreenHeight/2*0.8f;
        }

        canvasTemp.drawCircle(mProcessGL.ScreenWidth/2, mProcessGL.ScreenHeight/2, radius, mPaint);

        LType = mMask;
    }

    public String VertexShader() {
        return getShaderRaw(R.raw.bitmap_vertex_cover_shader);
    }

    private String FragmentShader() {
        return getShaderRaw(R.raw.bitmap_fragment_circle_shader);
    }

    public void Reset() {

    }
}
