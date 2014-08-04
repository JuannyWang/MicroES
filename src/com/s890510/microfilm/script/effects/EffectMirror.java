package com.s890510.microfilm.script.effects;

import android.opengl.Matrix;

public class EffectMirror extends BasicEffect
{
    private float[] mMVPMatrix = new float[16];
    private final static int DURATION = 5000;
    public EffectMirror() {
        mDuration = DURATION;
    }
    
    public EffectMirror(int duration) {
        mDuration = duration;
        mSleep = duration;
    }
    
    @Override
    public int getEffectType() {
        return EFFECT_MIRROR;
    }

    @Override
    public float[] getMVPMatrixByElapse(long elapse) {
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.rotateM(mMVPMatrix, 0, 180, 0, 1, 0);
        return mMVPMatrix;
    }
}
