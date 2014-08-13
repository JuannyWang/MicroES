package com.s890510.microfilm.script.effects;

import android.opengl.Matrix;

public class EffectSlideScaleFromRight extends BasicEffect {
    private final static int DURATION   = 5000;
    private final float      SCALE_RATE = 0.12f;
    private final float      SLIDE_RATE = 0.1f;          // SCALE_RATE must
                                                         // bigger than
                                                         // SLIDE_RATE
    private float[]          mMVPMatrix = new float[16];
    private float            scaleX     = 0;

    public EffectSlideScaleFromRight() {
        mDuration = DURATION;
    }

    public EffectSlideScaleFromRight(String shader) {
        mDuration = DURATION;
        setShader(shader);
    }

    public EffectSlideScaleFromRight(int duration) {
        mDuration = duration;
        mSleep = duration;
    }

    @Override
    public int getEffectType() {
        return EFFECT_SLIDE_SCALE_FORM_LEFT;
    }

    @Override
    public float[] getMVPMatrixByElapse(long elapse) {
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.scaleM(mMVPMatrix, 0, 1 + SCALE_RATE, 1 + SCALE_RATE, 0);
        scaleX = SLIDE_RATE - SLIDE_RATE * 2 * getProgressByElapse(elapse);// -SLIDE_RATE
                                                                           // ~
                                                                           // SLIDE_RATE
        Matrix.translateM(mMVPMatrix, 0, scaleX, 0, 0);
        return mMVPMatrix;
    }

    @Override
    public float getScaleSize(long elapse) {
        return 1.12f;
    }
}