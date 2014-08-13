package com.s890510.microfilm.script.effects;

import android.opengl.Matrix;

public class EffectFlashScale extends BasicEffect {
    private final static int   DEFAUILT_DURATION          = 5000;         // |------
                                                                           // DURATION
                                                                           // ----|***
                                                                           // SCALE_DURATION
                                                                           // ***|-------
                                                                           // DURATION
                                                                           // ----|
    private final static int   DEFAUILT_START_FLASH_SCALE = 2000;
    private final static int   DEFAUILT_SCALE_DURATION    = 500;
    private final static float START_SCALE_RATE           = 2.1f;
    private float[]            mMVPMatrix                 = new float[16];
    private int                mStartFlashScale, mScaleDuration;
    private float              mScaleRate;

    public EffectFlashScale() {
        initDefault();
    }

    public EffectFlashScale(int duration, int startFlashScale, int scaleDuration, float scaleRate) {
        if(startFlashScale > duration)
            initDefault();
        else {
            mDuration = duration;
            mStartFlashScale = startFlashScale;
            mScaleDuration = scaleDuration;
            mScaleRate = scaleRate;
        }

    }

    private void initDefault() {
        mDuration = DEFAUILT_DURATION;
        mStartFlashScale = DEFAUILT_START_FLASH_SCALE;
        mScaleDuration = DEFAUILT_SCALE_DURATION;
        mScaleRate = START_SCALE_RATE;
    }

    @Override
    public int getEffectType() {
        return EFFECT_FLASH_SCALE;
    }

    @Override
    public float[] getMVPMatrixByElapse(long elapse) {
        Matrix.setIdentityM(mMVPMatrix, 0);
        if(elapse > mStartFlashScale && elapse < mStartFlashScale + mScaleDuration)
            Matrix.scaleM(mMVPMatrix, 0, mScaleRate, mScaleRate, 0);
        return mMVPMatrix;
    }

    @Override
    public float getScaleSize(long elapse) {
        return mScaleRate;
    }
}
