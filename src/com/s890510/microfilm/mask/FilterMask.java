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
import com.s890510.microfilm.script.effects.Effect;

public class FilterMask extends Mask {
    private static final String TAG        = "SquareBorderMask";

    private int                 mProgram;
    private int                 mPositionHandle;
    private int                 mColorHandle;
    private int                 mAlphaHandle;
    private int                 mMVPMatrixHandle;
    private float[]             mMVPMatrix = new float[16];

    public FloatBuffer          mVertices  = null;
    private ProcessGL           mProcessGL;

    public FilterMask(MicroMovieActivity activity, ProcessGL processGL) {
        super(activity);
        mProcessGL = processGL;
        CreateProgram();
    }

    void DrawRandar(float[] mModelMatrix, float[] mViewMatrix, float[] mProjectionMatrix, ElementInfo mElementInfo) {
        long mElapseTime;
        long timer = mElementInfo.timer.getElapse();
        Effect mEffect = mElementInfo.effect.getEffect(timer);

        if(mEffect == null)
            return;
        else
            mElapseTime = mElementInfo.effect.getElapseTime(timer);

        GLES20.glUseProgram(mProgram);

        mVertices.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mVertices);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniform1f(mAlphaHandle, mEffect.getAlpha(mElapseTime));

        float[] mBGColor = mEffect.getBGColor(mElapseTime);

        GLES20.glUniform4f(mColorHandle, mBGColor[0], mBGColor[1], mBGColor[2], mBGColor[3]);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisable(GLES20.GL_BLEND);
    }

    private void CreateProgram() {
        final int vertexShaderHandle = GLUtil.compileShader(GLES20.GL_VERTEX_SHADER, VertexShader());
        final int fragmentShaderHandle = GLUtil.compileShader(GLES20.GL_FRAGMENT_SHADER, FragmentShader());

        checkGlError("BorderMask");
        // Create the new program
        mProgram = GLUtil.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle);
        if(mProgram == 0) {
            Log.e(TAG, "mProgram is 0");
            return;
        }

        // Set program handles. These will later be used to pass in values to
        // the program.
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "mColor");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mAlphaHandle = GLES20.glGetUniformLocation(mProgram, "mAlpha");

        checkGlError("BorderMaskCreateProgram");
    }

    public void CalcVertices() {
        float mRatio = mProcessGL.ScreenRatio;
        float[] mVerticesData = new float[] { -mRatio, -1.0f, 0.0f, mRatio, -1.0f, 0.0f, -mRatio, 1.0f, 0.0f, mRatio, 1.0f, 0.0f };

        mVertices = ByteBuffer.allocateDirect(mVerticesData.length * ProcessGL.FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);
    }

    private String VertexShader() {
        return "uniform mat4 uMVPMatrix;                               \n" + "attribute vec4 aPosition;                              \n"
                + "void main() {                                          \n" + "    gl_Position = uMVPMatrix * aPosition;              \n"
                + "}                                                      \n";
    }

    private String FragmentShader() {
        return "precision mediump float;                                                                                   \n"
                + "uniform vec4 mColor;                                                                                       \n"
                + "uniform float mAlpha;                                                                                      \n"
                + "void main() {                                                                                              \n"
                + "    gl_FragColor = mColor;                                                                                 \n"
                + "    gl_FragColor.w = mAlpha;                                                                               \n"
                + "}                                                                                                          \n";
    }

    public void Reset() {

    }
}
