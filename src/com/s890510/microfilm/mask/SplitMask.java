package com.s890510.microfilm.mask;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.asus.gallery.micromovie.MicroMovieActivity;
import com.asus.gallery.micromovie.ProcessGL;
import com.asus.gallery.micromovie.ShaderHelper;

public class SplitMask extends Mask {
    private static final String TAG = "SplitMask";
    private static final int TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int TRIANGLE_VERTICES_DATA_UV_OFFSET = 3;

    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private FloatBuffer mTriangleVertices;
    private float[] mMVPMatrix = new float[16];
    private float[] mModelMatrix = new float[16];

    private float YPosition = 1.0f;
    private float RPosition = 0.0f; //Rotate
    private boolean mInit = false;

    public SplitMask(MicroMovieActivity activity) {
        super(activity);
    }

    public void DrawRandar(float[] mViewMatrix, float[] mProjectionMatrix) {
        if(!mInit) CreateProgram();
        RPosition -= 2.0;

        DrawCube(mViewMatrix, mProjectionMatrix, 1);
        DrawCube(mViewMatrix, mProjectionMatrix, -1);

        checkGlError("drawBackGround");
    }

    private void DrawCube(float[] mViewMatrix, float[] mProjectionMatrix, int side) {
        GLES20.glUseProgram(mProgram);

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false,
            28, mTriangleVertices);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false,
                28, mTriangleVertices);
        GLES20.glEnableVertexAttribArray(mColorHandle);

        checkGlError("aa");

        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.translateM(mModelMatrix, 0, 0.0f, YPosition*side, 0.0f);
        Matrix.scaleM(mModelMatrix, 0, 2.0f, 1.0f, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, RPosition*side, 1.0f, 0.0f, 0.0f);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        checkGlError("bb");
    }

    private void CreateProgram() {
        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, VertexShader());
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, FragmentShader());

        checkGlError("SplitMaskshader");
        //Create the new program
        mProgram = ShaderHelper.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle);
        if (mProgram == 0) {
            Log.e(TAG, "mProgram is 0");
            return;
        }

        // Set program handles. These will later be used to pass in values to the program.
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mColorHandle = GLES20.glGetAttribLocation(mProgram, "aColor");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        checkGlError("SplitMaskCreateProgram");

        CalcBKVertices();

        mInit = true;
    }

    private void CalcBKVertices() {
        final float[] mTriangleVerticesData = {
                // X, Y, Z,
                // R, G, B, A
                -1.0f, -1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f,

                1.0f, -1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f,

                -1.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f,

                1.0f, 1.0f, 0.0f,
                0.0f, 0.0f, 0.0f, 1.0f
        };

        // Initialize the buffers.
        mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length * ProcessGL.FLOAT_SIZE_BYTES)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();

        mTriangleVertices.put(mTriangleVerticesData).position(0);
    }

    private String VertexShader() {
        return
                "uniform mat4 uMVPMatrix;       \n" +
                "attribute vec4 aPosition;      \n" +
                "attribute vec4 aColor;         \n" +
                "varying vec4 vColor;           \n" +
                "void main() {                  \n" +
                "    vColor = aColor;           \n" +
                "    gl_Position = uMVPMatrix   \n" +
                "                * aPosition;   \n" +
                "}                              \n";
    }

    private String FragmentShader() {
        return
                "precision mediump float;       \n" +
                "varying vec4 vColor;           \n" +
                "void main() {                  \n" +
                "    gl_FragColor = vColor;     \n" +
                "}                              \n";
    }

    public boolean NeedDraw() {
        if(Math.abs(RPosition) > 90) return false;
        else return true;
    }

    public void Reset() {
        YPosition = 1.0f;
        RPosition = 0.0f;
    }
}
