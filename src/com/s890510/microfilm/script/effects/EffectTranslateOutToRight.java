package com.s890510.microfilm.script.effects;

import android.opengl.Matrix;

public class EffectTranslateOutToRight extends BasicEffect
{
    private final static int DURATION = 1000;
    private float scaleX = 0;
    private float[] mMVPMatrix = new float[16]; //the texture
    
    public EffectTranslateOutToRight() {
        mDuration = DURATION;
        mSleep = DURATION;
    }
    
    public EffectTranslateOutToRight(int duration) {
        mDuration = duration;
        mSleep = duration;
    }

    @Override
    public int getEffectType() {
        return EFFECT_TRANSLATE_OUT_TO_RIGHT;
    }

    @Override
    public float[] getMVPMatrixByElapse(long elapse) {
        Matrix.setIdentityM(mMVPMatrix, 0);
        scaleX = 2.0f * getProgressByElapse(elapse);
        Matrix.translateM(mMVPMatrix, 0, scaleX, 0, 0);
        return mMVPMatrix;
    }


}
