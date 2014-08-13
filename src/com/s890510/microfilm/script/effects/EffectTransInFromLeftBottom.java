package com.s890510.microfilm.script.effects;

import android.opengl.Matrix;

public class EffectTransInFromLeftBottom extends BasicEffect {
    private final static int DURATION    = 1000;
    private float            scaleX      = -2;
    private float            START_SCALE = 2.0f;
    private float            END_SCALE   = 2.0f;
    private float[]          mMVPMatrix  = new float[16]; // the texture

    public EffectTransInFromLeftBottom() {
        mDuration = DURATION;
        mSleep = DURATION;
    }

    public EffectTransInFromLeftBottom(int duration) {
        mDuration = duration;
        mSleep = duration;
    }

    public EffectTransInFromLeftBottom(int duration, float Start, float End) {
        mDuration = duration;
        mSleep = duration;
        START_SCALE = Start;
        END_SCALE = End;
    }

    @Override
    public int getEffectType() {
        return EFFECT_TRANSLATE_IN_FROM_LEFT_BOTTOM;
    }

    @Override
    public float[] getMVPMatrixByElapse(long elapse) {
        Matrix.setIdentityM(mMVPMatrix, 0);
        scaleX = START_SCALE * getProgressByElapse(elapse) - END_SCALE;
        Matrix.translateM(mMVPMatrix, 0, scaleX, scaleX, 0);
        return mMVPMatrix;
    }

}
