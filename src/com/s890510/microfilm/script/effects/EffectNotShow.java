package com.s890510.microfilm.script.effects;

import android.opengl.Matrix;

public class EffectNotShow extends BasicEffect {
    private float[]          mMVPMatrix = new float[16];
    private final static int DURATION   = 1000;

    public EffectNotShow() {
        mDuration = DURATION;
        mSleep = DURATION;
    }

    public EffectNotShow(int duration) {
        mDuration = duration;
        mSleep = duration;
    }

    @Override
    public int getEffectType() {
        return EFFECT_NOT_SHOW;
    }

    @Override
    public float[] getMVPMatrixByElapse(long elapse) {
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.scaleM(mMVPMatrix, 0, 0, 0, 0);
        return mMVPMatrix;
    }

    @Override
    public boolean showBackground() {
        return false;
    }

    @Override
    public float getScaleSize(long elapse) {
        return 0;
    }
}