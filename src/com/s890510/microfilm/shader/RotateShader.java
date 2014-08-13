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
import com.s890510.microfilm.R;
import com.s890510.microfilm.draw.GLUtil;
import com.s890510.microfilm.script.effects.Effect;

public class RotateShader extends Shader {
    private static final String TAG              = "DefaultShader";
    public static final int     FLOAT_SIZE_BYTES = 4;              // float =
                                                                    // 4bytes

    private int                 mProgram;
    private int                 mPositionHandle;
    private int                 mTextureHandle;
    private int                 mSamplerHandle;
    private int                 mAlphaHandle;
    private int                 mMVPMatrixHandle;
    private int                 mResolutionHandle;
    private int                 mThemeHandle;
    private int                 mLeftFilterHandle;
    private int                 mRightFilterHandle;
    private FloatBuffer         mCircleVertices;
    private float[]             mMVPMatrix       = new float[16];
    private ProcessGL           mProcessGL;

    private int                 vCount           = 3 * 72;

    public RotateShader(MicroMovieActivity activity, ProcessGL processGL) {
        super(activity);
        mProcessGL = processGL;
        CreateProgram();
        Vertices();
    }

    public void DrawRandar(float[] mModelMatrix, float[] mViewMatrix, float[] mProjectionMatrix, int mTextureId, ElementInfo mElementInfo) {

        long mElapseTime;
        long timer = mElementInfo.timer.getElapse();
        Effect mEffect = mElementInfo.effect.getEffect(timer);

        if(mEffect == null)
            return;
        else
            mElapseTime = mElementInfo.effect.getElapseTime(timer);

        float duration = mEffect.getDuration(mElapseTime);
        float progress = mEffect.getProgressByElapse(mElapseTime);
        float elapse = progress * duration;
        boolean trans = mEffect.getTransition(mElapseTime);

        float[] mLeft = mProcessGL.getLeftFilter();
        float[] mRight = mProcessGL.getRightFilter();

        GLES20.glUseProgram(mProgram);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + mTextureId);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glUniform1i(mSamplerHandle, mTextureId);

        mElementInfo.mCTextureCoords.position(0);
        GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 0, mElementInfo.mCTextureCoords);
        GLES20.glEnableVertexAttribArray(mTextureHandle);

        mCircleVertices.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mCircleVertices);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glUniform1f(mAlphaHandle, mEffect.getAlpha(mElapseTime));
        GLES20.glUniform2f(mResolutionHandle, mProcessGL.ScreenWidth, mProcessGL.ScreenHeight);
        GLES20.glUniform1f(mThemeHandle, mProcessGL.getScriptFilter());
        GLES20.glUniform4f(mLeftFilterHandle, mLeft[0], mLeft[1], mLeft[2], mLeft[3]);
        GLES20.glUniform4f(mRightFilterHandle, mRight[0], mRight[1], mRight[2], mRight[3]);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vCount);

        GLES20.glDisable(GLES20.GL_BLEND);

        checkGlError("DefaultShader");
    }

    private void CreateProgram() {
        final int vertexShaderHandle = GLUtil.compileShader(GLES20.GL_VERTEX_SHADER, VertexShader());
        final int fragmentShaderHandle = GLUtil.compileShader(GLES20.GL_FRAGMENT_SHADER, FragmentShader());

        checkGlError("DefaultShader");
        // Create the new program
        mProgram = GLUtil.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle);
        if(mProgram == 0) {
            Log.e(TAG, "mProgram is 0");
            return;
        }

        // Set program handles. These will later be used to pass in values to
        // the program.
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        mSamplerHandle = GLES20.glGetUniformLocation(mProgram, "Texture");

        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mAlphaHandle = GLES20.glGetUniformLocation(mProgram, "mAlpha");
        mResolutionHandle = GLES20.glGetUniformLocation(mProgram, "resolution");
        mThemeHandle = GLES20.glGetUniformLocation(mProgram, "mTheme");

        mLeftFilterHandle = GLES20.glGetUniformLocation(mProgram, "mLeft");
        mRightFilterHandle = GLES20.glGetUniformLocation(mProgram, "mRight");

        checkGlError("DefaultCreateProgram");
    }

    private void Vertices() {
        // For Circle
        float CRadiusX = 0.8f;
        float CRadiusY = 0.8f;
        float angdegSpan = 360.0f / 72;

        float[] vertices = new float[vCount * 3];

        int count = 0;
        int stCount = 0;

        for(float angdeg = 0; Math.ceil(angdeg) < 360; angdeg += angdegSpan) {
            double angrad = Math.toRadians(angdeg);
            double angradNext = Math.toRadians(angdeg + angdegSpan);

            vertices[count++] = 0;
            vertices[count++] = 0;
            vertices[count++] = 0;

            vertices[count++] = (float) (-CRadiusX * Math.sin(angrad));
            vertices[count++] = (float) (CRadiusY * Math.cos(angrad));
            vertices[count++] = 0;

            vertices[count++] = (float) (-CRadiusX * Math.sin(angradNext));
            vertices[count++] = (float) (CRadiusY * Math.cos(angradNext));
            vertices[count++] = 0;
        }

        mCircleVertices = ByteBuffer.allocateDirect(vertices.length * ProcessGL.FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCircleVertices.put(vertices).position(0);
    }

    private String VertexShader() {
        return getShaderRaw(R.raw.bitmap_vertex_shader);
    }

    private String FragmentShader() {
        return getShaderRaw(R.raw.bitmap_fragment_shader);
    }

    @Override
    public void Reset() {

    }
}
