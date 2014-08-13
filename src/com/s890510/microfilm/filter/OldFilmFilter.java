package com.s890510.microfilm.filter;

import java.util.Random;

import android.graphics.Bitmap;
import android.opengl.GLES20;

import com.s890510.microfilm.MicroMovieActivity;
import com.s890510.microfilm.R;

public class OldFilmFilter extends DefaultFilter {
    private final float SEPIA         = 0.4f;
    private final float NOISE         = 0.2f;
    private final float SCRATCH       = 0.6f;
    private final float IVIGNETTING   = 0.75f;
    private final float OVIGNETTING   = 0.85f;

    private int         mBSepiaValueHandle;
    private int         mBNoiseValueHandle;
    private int         mBScratchValueHandle;
    private int         mBInnerVignettingHandle;
    private int         mBOuterVignettingHandle;
    private int         mBRandomValueHandle;
    private int         mBTimeLapseHandle;

    /*
     * private int mBRandomCoord1Handle; private int mBRandomCoord2Handle;
     * private int mBNoiseSamplerHandle; private int mBNoiseTextureID;
     * 
     * private int mVRandomCoord1Handle; private int mVRandomCoord2Handle;
     * private int mVNoiseSamplerHandle; private int mVNoiseTextureID;
     */

    private int         mVSepiaValueHandle;
    private int         mVNoiseValueHandle;
    private int         mVScratchValueHandle;
    private int         mVInnerVignettingHandle;
    private int         mVOuterVignettingHandle;
    private int         mVRandomValueHandle;
    private int         mVTimeLapseHandle;

    private int         mBRandomGrainyHandle;
    private int         mVRandomGrainyHandle;

    private float       mTimeLapse    = 0;
    private float       mRandomVaule1 = 0;
    private float       mRandomVaule2 = 0;

    public OldFilmFilter(MicroMovieActivity activity) {
        super(activity);
    }

    @Override
    public String getBitmapSingleFragment() {
        return getShaderRaw(R.raw.bitmap_fragment_oldfilm_shader);
    }

    @Override
    public String getVideoFragment() {
        return getShaderRaw(R.raw.video_fragment_oldfilm_shader);
    }

    @Override
    public void setSingleBitmapParams(int bProgram) {
        mBSepiaValueHandle = GLES20.glGetUniformLocation(bProgram, "SepiaValue");
        mBNoiseValueHandle = GLES20.glGetUniformLocation(bProgram, "NoiseValue");
        mBScratchValueHandle = GLES20.glGetUniformLocation(bProgram, "ScratchValue");
        mBInnerVignettingHandle = GLES20.glGetUniformLocation(bProgram, "InnerVignetting");
        mBOuterVignettingHandle = GLES20.glGetUniformLocation(bProgram, "OuterVignetting");
        mBRandomValueHandle = GLES20.glGetUniformLocation(bProgram, "RandomValue");
        mBTimeLapseHandle = GLES20.glGetUniformLocation(bProgram, "TimeLapse");

        mBRandomGrainyHandle = GLES20.glGetUniformLocation(bProgram, "rand_seed");

        /*
         * int textureId[] = new int[1]; GLES20.glGenTextures(1, textureId, 0);
         * mBNoiseTextureID = textureId[0];
         * 
         * GLES20.glActiveTexture(GLES20.GL_TEXTURE0+mBNoiseTextureID);
         * LoadTexture.BindTexture(GLES20.GL_TEXTURE_2D, mBNoiseTextureID);
         * GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, getNoiseBitmap(), 0);
         * 
         * 
         * mBRandomCoord1Handle = GLES20.glGetUniformLocation(bProgram,
         * "RandomCoord1"); mBRandomCoord2Handle =
         * GLES20.glGetUniformLocation(bProgram, "RandomCoord2");
         * mBNoiseSamplerHandle = GLES20.glGetUniformLocation(bProgram,
         * "NoiseSampler");
         */
    }

    private Random mRandom = new Random();

    private Bitmap getNoiseBitmap() {
        Bitmap bitmap = Bitmap.createBitmap(1280, 720, Bitmap.Config.ARGB_8888);
        for(int i = 0; i < 1280; i++) {
            for(int j = 0; j < 720; j++) {
                int color = mRandom.nextInt(255);
                bitmap.setPixel(i, j, color);
                // bitmap.setPixel(i, j, 255);
            }
        }
        return bitmap;
    }

    @Override
    public void setVideoParams(int vProgram) {
        mVSepiaValueHandle = GLES20.glGetUniformLocation(vProgram, "SepiaValue");
        mVNoiseValueHandle = GLES20.glGetUniformLocation(vProgram, "NoiseValue");
        mVScratchValueHandle = GLES20.glGetUniformLocation(vProgram, "ScratchValue");
        mVInnerVignettingHandle = GLES20.glGetUniformLocation(vProgram, "InnerVignetting");
        mVOuterVignettingHandle = GLES20.glGetUniformLocation(vProgram, "OuterVignetting");
        mVRandomValueHandle = GLES20.glGetUniformLocation(vProgram, "RandomValue");
        mVTimeLapseHandle = GLES20.glGetUniformLocation(vProgram, "TimeLapse");

        mVRandomGrainyHandle = GLES20.glGetUniformLocation(vProgram, "rand_seed");
    }

    @Override
    public void drawBitmap() {
        GLES20.glUniform1f(mBSepiaValueHandle, SEPIA);
        GLES20.glUniform1f(mBNoiseValueHandle, NOISE);
        GLES20.glUniform1f(mBScratchValueHandle, SCRATCH);
        GLES20.glUniform1f(mBInnerVignettingHandle, IVIGNETTING);
        GLES20.glUniform1f(mBOuterVignettingHandle, OVIGNETTING);

        setRandomValues();

        GLES20.glUniform1f(mBRandomValueHandle, mRandomVaule1);
        GLES20.glUniform1f(mBTimeLapseHandle, mTimeLapse);
        GLES20.glUniform1f(mBRandomGrainyHandle, mRandomVaule2);
    }

    @Override
    public void drawVideo() {
        GLES20.glUniform1f(mVSepiaValueHandle, SEPIA);
        GLES20.glUniform1f(mVNoiseValueHandle, NOISE);
        GLES20.glUniform1f(mVScratchValueHandle, SCRATCH);
        GLES20.glUniform1f(mVInnerVignettingHandle, IVIGNETTING);
        GLES20.glUniform1f(mVOuterVignettingHandle, OVIGNETTING);

        setRandomValues();

        GLES20.glUniform1f(mVRandomValueHandle, mRandomVaule1);
        GLES20.glUniform1f(mVTimeLapseHandle, mTimeLapse);
        GLES20.glUniform1f(mVRandomGrainyHandle, mRandomVaule2);
    }

    private void setRandomValues() {
        if(!mActivity.checkPause() || mActivity.isSaving()) {
            mRandomVaule1 = (float) Math.random();
            if(mRandomVaule1 > 0.9)
                mTimeLapse = (float) Math.random();
            mRandomVaule2 = (float) mRandom.nextInt(1001) + 1000f;
        }
    }
}
