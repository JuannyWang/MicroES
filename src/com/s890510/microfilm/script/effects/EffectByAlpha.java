package com.s890510.microfilm.script.effects;

import android.opengl.Matrix;

public class EffectByAlpha extends BasicEffect
{
    private float[] mMVPMatrix = new float[16]; //the texture
    private float mStartAlpha = 1.0f;
    private float mEndAlpha = 0.0f;
    
    public EffectByAlpha(int duration, float startalpha, float endalpha) {
        mDuration = duration;
        mSleep = duration;
        mStartAlpha = startalpha;
        mEndAlpha = endalpha;
    }

    @Override
    public int getEffectType() {
        return EFFECT_BY_ALPHA;
    }

    @Override
    public float[] getMVPMatrixByElapse(long elapse) {
        Matrix.setIdentityM(mMVPMatrix, 0);
        return mMVPMatrix;
    }
    
    @Override
    public float getAlpha(long elapse) {
    	return mStartAlpha - (mStartAlpha - mEndAlpha) * getProgressByElapse(elapse);
    }
}
