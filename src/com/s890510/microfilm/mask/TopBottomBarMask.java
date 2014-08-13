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

public class TopBottomBarMask extends Mask {
    private static final String TAG                               = "TopBottomBarMask";
    private static final int    TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int    TRIANGLE_VERTICES_DATA_UV_OFFSET  = 3;

    private int                 mProgram;
    private int                 mPositionHandle;
    private int                 mColorHandle;
    private int                 mMVPMatrixHandle;
    private FloatBuffer         mTriangleVertices;
    private float[]             mMVPMatrix                        = new float[16];
    private float[]             mModelMatrix                      = new float[16];
    private ProcessGL           mProcessGL;

    public TopBottomBarMask(MicroMovieActivity activity, ProcessGL processGL) {
        super(activity);
        mProcessGL = processGL;
        CreateProgram();
    }

    void DrawRandar(float[] mViewMatrix, float[] mProjectionMatrix, ElementInfo mElementInfo, int mType) {

        GLES20.glUseProgram(mProgram);

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 28, mTriangleVertices);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 28, mTriangleVertices);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        Matrix.setIdentityM(mModelMatrix, 0);

        if(mType == Mask.TRANS_IN) {
            float progress = ((float) mElementInfo.timer.getElapse() * 1.5f) / (float) mElementInfo.effect.getDuration();
            if(progress > 1)
                progress = 1;

            float scale = (1.18f - (0.20f * progress));
            Matrix.translateM(mModelMatrix, 0, 0, scale, 0);
        } else if(mType == Mask.SHOWN) {
            Matrix.translateM(mModelMatrix, 0, 0, 0.98f, 0);
        }

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        // -----

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 28, mTriangleVertices);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 28, mTriangleVertices);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        Matrix.invertM(mModelMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        checkGlError("TopBottomBarMask");
    }

    private void CreateProgram() {
        final int vertexShaderHandle = GLUtil.compileShader(GLES20.GL_VERTEX_SHADER, VertexShader());
        final int fragmentShaderHandle = GLUtil.compileShader(GLES20.GL_FRAGMENT_SHADER, FragmentShader());

        checkGlError("TopBottomBarMask");
        // Create the new program
        mProgram = GLUtil.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle);
        if(mProgram == 0) {
            Log.e(TAG, "mProgram is 0");
            return;
        }

        // Set program handles. These will later be used to pass in values to
        // the program.
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        checkGlError("SplitMaskCreateProgram");
    }

    public void CalcVertices() {
        float mRatio = mProcessGL.ScreenRatio;
        float mheight = 0.12f;
        final float[] mTriangleVerticesData = {
                // X, Y, Z,
                // R, G, B, A
                -mRatio, -mheight, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,

                mRatio, -mheight, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,

                -mRatio, mheight, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,

                mRatio, mheight, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f };

        // Initialize the buffers.
        mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length * ProcessGL.FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        mTriangleVertices.put(mTriangleVerticesData).position(0);
    }

    private String VertexShader() {
        return "uniform mat4 uMVPMatrix;       \n" + "attribute vec4 aPosition;      \n" + "attribute vec4 aColor;         \n"
                + "varying vec4 vColor;           \n" + "void main() {                  \n" + "    vColor = aColor;           \n"
                + "    gl_Position = uMVPMatrix   \n" + "                * aPosition;   \n" + "}                              \n";
    }

    private String FragmentShader() {
        return "precision mediump float;       \n" + "varying vec4 vColor;           \n" + "void main() {                  \n"
                + "    gl_FragColor = vColor;     \n" + "}                              \n";
    }

    public void Reset() {

    }
}
