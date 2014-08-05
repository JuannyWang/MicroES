package com.s890510.microfilm.mask;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.s890510.microfilm.ElementInfo;
import com.s890510.microfilm.MicroMovieActivity;
import com.s890510.microfilm.ProcessGL;
import com.s890510.microfilm.draw.GLUtil;
import com.s890510.microfilm.util.Easing;

public class SquareBorderMask extends Mask {
    private static final String TAG = "SquareBorderMask";
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;

    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private int mSizeHandle;
    private int mRatioHandle;
    private FloatBuffer mTriangleVertices;
    private float[] mMVPMatrix = new float[16];
    private float[] mModelMatrix = new float[16];

    private float mScale = 0.0f;
    private ProcessGL mProcessGL;

    public SquareBorderMask(MicroMovieActivity activity, ProcessGL processGL) {
        super(activity);
        mProcessGL = processGL;
        CreateProgram();
    }

    void DrawRandar(float[] mViewMatrix, float[] mProjectionMatrix, ElementInfo mElementInfo) {

        if(mElementInfo.effect.getMaskType(mElementInfo.timer.getElapse()) == Mask.GONE) {
            return;
        }

        long timer = mElementInfo.timer.getElapse();
        float duration = mElementInfo.effect.getDuration(timer);

        GLES20.glUseProgram(mProgram);

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
            28, mTriangleVertices);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false,
                28, mTriangleVertices);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        if(mElementInfo.effect.getMaskType(timer) == Mask.TRANS_OUT) {
            float mProgress = mElementInfo.effect.getProgressByElapse(timer);
            if(mProgress < 0.5f) {
                mScale = -Easing.easeOutExpo((mProgress*10/5)*duration, 0.0f, 0.2f, duration);
            } else {
                mScale = Easing.easeInExpo((mProgress*10/5)*duration, 0.0f, 0.45f, duration);
            }
            GLES20.glUniform1f(mSizeHandle, 0.75f + mScale);
        } else {
            GLES20.glUniform1f(mSizeHandle, 0.75f);
        }

        GLES20.glUniform1f(mRatioHandle, mProcessGL.ScreenRatio);

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
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mSizeHandle = GLES20.glGetUniformLocation(mProgram, "mSize");
        mRatioHandle = GLES20.glGetUniformLocation(mProgram, "mRatio");

        checkGlError("BorderMaskCreateProgram");
    }

    public void CalcVertices() {
        float mRatio = mProcessGL.ScreenRatio;
        final float[] mTriangleVerticesData = {
                // X, Y, Z,
                // R, G, B, A
                -mRatio, -1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f,

                mRatio, -1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f,

                -mRatio, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f,

                mRatio, 1.0f, 0.0f,
                1.0f, 1.0f, 1.0f, 1.0f
        };

        // Initialize the buffers.
        mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length * ProcessGL.FLOAT_SIZE_BYTES)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();

        mTriangleVertices.put(mTriangleVerticesData).position(0);
    }

    private String VertexShader() {
        return
                "uniform mat4 uMVPMatrix;                               \n" +
                "attribute vec4 aPosition;                              \n" +
                "attribute vec4 aColor;                                 \n" +
                "varying vec4 vColor;                                   \n" +
                "varying float vXClipDist;                              \n" +
                "varying float vYClipDist;                              \n" +
                "void main() {                                          \n" +
                "    vColor = aColor;                                   \n" +
                "    vec4 aXClipDist = vec4(1.0, 0.0, 0.0, 0.0);        \n" +
                "    vec4 aYClipDist = vec4(0.0, 1.0, 0.0, 0.0);        \n" +
                "    vXClipDist = dot(aPosition.xyz, aXClipDist.xyz);   \n" +
                "    vYClipDist = dot(aPosition.xyz, aYClipDist.xyz);   \n" +
                "    gl_Position = uMVPMatrix * aPosition;              \n" +
                "}                                                      \n";
    }

    private String FragmentShader() {
        return
                "precision mediump float;                                                                                   \n" +
                "uniform float mSize;                                                                                       \n" +
                "uniform float mRatio;                                                                                   \n" +
                "varying vec4 vColor;                                                                                       \n" +
                "varying float vXClipDist;                                                                                  \n" +
                "varying float vYClipDist;                                                                                  \n" +
                "void main() {                                                                                              \n" +
                "    gl_FragColor = vColor;                                                                                 \n" +
                "    if((vXClipDist >= -mRatio && vXClipDist < -mSize*mRatio) || (vXClipDist >= mSize*mRatio && vXClipDist < mRatio) ||         \n" +
                "      (vYClipDist >= -1.0 && vYClipDist < -mSize) || (vYClipDist >= mSize && vYClipDist < 1.0)) {          \n" +
                "        gl_FragColor.w = 1.0;                                                                              \n" +
                "    } else {                                                                                               \n" +
                "       gl_FragColor.w = 0.0;                                                                               \n" +
                "    }                                                                                                      \n" +
                "}                                                                                                          \n";
    }

    public void Reset() {

    }
}
