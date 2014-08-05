package com.s890510.microfilm.shader;

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

public class BackgroundShader extends Shader {
    private static final String TAG = "BackgroundShader";
    public static final int FLOAT_SIZE_BYTES = 4; //float = 4bytes

    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;
    private float[] mMVPMatrix = new float[16];
    private float[] mModelMatrix = new float[16];

    private FloatBuffer mVertices = null;
    
    private ProcessGL mProcessGL;

    public BackgroundShader(MicroMovieActivity activity, ProcessGL processGL) {
        super(activity);
        mProcessGL = processGL;
        CreateProgram();
    }

    public void DrawRandar(ElementInfo mElementInfo, float[] mViewMatrix, float[] mProjectionMatrix) {

        long timer = mElementInfo.timer.getElapse();

        GLES20.glUseProgram(mProgram);

        mVertices.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertices);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glUniform1fv(mColorHandle, mElementInfo.effect.getBGColor(timer).length, mElementInfo.effect.getBGColor(timer), 0);

        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        checkGlError("BackgroundShader");
    }

    private void CreateProgram() {
        final int vertexShaderHandle = GLUtil.compileShader(GLES20.GL_VERTEX_SHADER, VertexShader());
        final int fragmentShaderHandle = GLUtil.compileShader(GLES20.GL_FRAGMENT_SHADER, FragmentShader());

        checkGlError("BackgroundShader");
        //Create the new program
        mProgram = GLUtil.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle);
        if (mProgram == 0) {
            Log.e(TAG, "mProgram is 0");
            return;
        }

        // Set program handles. These will later be used to pass in values to the program.
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

        mColorHandle = GLES20.glGetUniformLocation(mProgram, "Color");

        checkGlError("BackgroundCreateProgram");
    }

    public static String VertexShader() {
        return
                "uniform mat4 uMVPMatrix;       \n" +
                "attribute vec4 aPosition;      \n" +
                "void main() {                  \n" +
                "    gl_Position = uMVPMatrix   \n" +
                "                * aPosition;   \n" +
                "}                              \n";
    }

    public static String FragmentShader() {
        return
                "precision mediump float;                       \n" +
                "uniform float Color[4];                        \n" +
                "void main() {                                  \n" +
                "    gl_FragColor = vec4(Color[0], Color[1], Color[2], Color[3]);   \n" +
                "}                                              \n";
    }

    public void CalcVertices() {
        float mRatio = mProcessGL.ScreenRatio;
        float[] mVerticesData = new float[]{
                -mRatio, -1.0f, 0.0f,
                 mRatio, -1.0f, 0.0f,
                -mRatio,  1.0f, 0.0f,
                 mRatio,  1.0f, 0.0f
        };

        // Initialize the buffers.
        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * ProcessGL.FLOAT_SIZE_BYTES)
        .order(ByteOrder.nativeOrder()).asFloatBuffer();

        mVertices.put(mVerticesData).position(0);
    }

    @Override
    public void Reset() {

    }
}
