package com.s890510.microfilm.shader;

import java.util.Random;

import android.opengl.GLES20;
import android.opengl.Matrix;
import android.util.Log;

import com.asus.gallery.R;
import com.asus.gallery.micromovie.ElementInfo;
import com.asus.gallery.micromovie.MicroMovieActivity;
import com.asus.gallery.micromovie.ProcessGL;
import com.asus.gallery.micromovie.ShaderHelper;

public class OldfilmShader extends Shader {
    private static final String TAG = "OldfilmShader";
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
    private int mSepiaValueHandle;
    private int mInnerVignettingHandle;
    private int mOuterVignettingHandle;
    private int mRandomValueHandle;
    private float[] mMVPMatrix = new float[16];

    private float mAlpha = 1.0f;
    private final float SEPIA = 0.4f;
    private final float IVIGNETTING = 0.75f;
    private final float OVIGNETTING = 0.85f;
    private float mRandomVaule;
    private Random mRandom = new Random();
    
    private ProcessGL mProcessGL;

    public OldfilmShader(MicroMovieActivity activity, ProcessGL processGL) {
        super(activity);
        mProcessGL = processGL;
        CreateProgram();
    }

    public void DrawRandar(float[] mModelMatrix, float[] mViewMatrix, float[] mProjectionMatrix,
            int mTextureId, ElementInfo mElementInfo) {

        GLES20.glUseProgram(mProgram);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0+mTextureId);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLES20.glUniform1i(mSamplerHandle, mTextureId);

        mElementInfo.mSTextureCoords.position(0);
        GLES20.glVertexAttribPointer(mTextureHandle, 2, GLES20.GL_FLOAT, false, 0, mElementInfo.mSTextureCoords);
        GLES20.glEnableVertexAttribArray(mTextureHandle);

        mElementInfo.mVertices.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 0, mElementInfo.mVertices);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        GLES20.glUniform1f(mAlphaHandle, mAlpha);
        GLES20.glUniform2f(mResolutionHandle, mProcessGL.ScreenWidth, mProcessGL.ScreenHeight);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        setRandomValues();

        GLES20.glUniform1f(mSepiaValueHandle,SEPIA);
        GLES20.glUniform1f(mInnerVignettingHandle,IVIGNETTING);
        GLES20.glUniform1f(mOuterVignettingHandle,OVIGNETTING);
        GLES20.glUniform1f(mRandomValueHandle, mRandomVaule);

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisable(GLES20.GL_BLEND);

        checkGlError("DefaultShader");
    }

    private void CreateProgram() {
        final int vertexShaderHandle = ShaderHelper.compileShader(GLES20.GL_VERTEX_SHADER, VertexShader());
        final int fragmentShaderHandle = ShaderHelper.compileShader(GLES20.GL_FRAGMENT_SHADER, FragmentShader());

        checkGlError("OldfilmShader");
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
        mResolutionHandle = GLES20.glGetUniformLocation(mProgram, "resolution");

        mSepiaValueHandle = GLES20.glGetUniformLocation(mProgram, "SepiaValue");
        mInnerVignettingHandle = GLES20.glGetUniformLocation(mProgram, "InnerVignetting");
        mOuterVignettingHandle = GLES20.glGetUniformLocation(mProgram, "OuterVignetting");
        mRandomValueHandle = GLES20.glGetUniformLocation(mProgram, "RandomValue");

        checkGlError("OldfilmCreateProgram");
    }

    private String VertexShader() {
        return getShaderRaw(R.raw.bitmap_vertex_shader);
    }

    private String FragmentShader() {
        return getShaderRaw(R.raw.bitmap_fragment_oldfilm_shader);
    }

    private void setRandomValues(){
        if(!mActivity.checkPause() || mActivity.isSaving()){
            mRandomVaule = (float)mRandom.nextInt(1001) + 1000f;
        }
    }

    @Override
    public void Reset() {
        mAlpha = 1.0f;
    }
}
