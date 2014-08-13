package com.s890510.microfilm.script.effects;

import android.opengl.Matrix;

import com.s890510.microfilm.util.Easing;

public class EffectScale extends BasicEffect {
    private final static int DURATION    = 5000;
    private float            mStartScale = 1.0f;
    private float            mEndScale   = 1.3f;          // SCALE_RATE must
                                                          // bigger than
                                                          // SLIDE_RATE
    private float            mStartAlpha = 1.0f;
    private float            mEndAlpha   = 1.0f;
    private float[]          mMVPMatrix  = new float[16];
    private float            scale       = 0;
    private int              mUtil       = 0;

    public EffectScale() {
        mDuration = DURATION;
        mSleep = DURATION;
    }

    public EffectScale(String shader) {
        mDuration = DURATION;
        mSleep = DURATION;
        setShader(shader);
    }

    public EffectScale(int duration) {
        mDuration = duration;
        mSleep = duration;
    }

    public EffectScale(int duration, float startrate, float endrate) {
        mDuration = duration;
        mSleep = duration;
        mStartScale = startrate;
        mEndScale = endrate;
    }

    public EffectScale(int duration, float startrate, float endrate, float startalpha, float endalpha) {
        mDuration = duration;
        mSleep = duration;
        mStartScale = startrate;
        mEndScale = endrate;
        mStartAlpha = startalpha;
        mEndAlpha = endalpha;
    }

    public EffectScale(int duration, float startrate, float endrate, float startalpha, float endalpha, int mask) {
        mDuration = duration;
        mSleep = duration;
        mStartScale = startrate;
        mEndScale = endrate;
        mStartAlpha = startalpha;
        mEndAlpha = endalpha;
        mMask = mask;
    }

    public EffectScale(int duration, float startrate, float endrate, float startalpha, float endalpha, int mask, int util) {
        mDuration = duration;
        mSleep = duration;
        mStartScale = startrate;
        mEndScale = endrate;
        mStartAlpha = startalpha;
        mEndAlpha = endalpha;
        mMask = mask;
        mUtil = util;
    }

    public EffectScale(int duration, float startrate, float endrate, String mShader) {
        mDuration = duration;
        mSleep = duration;
        mStartScale = startrate;
        mEndScale = endrate;
        setShader(mShader);
    }

    @Override
    public int getEffectType() {
        return EFFECT_SCALE_OUT;
    }

    @Override
    public float[] getMVPMatrixByElapse(long elapse) {
        Matrix.setIdentityM(mMVPMatrix, 0);
        if(mUtil != 0 && mStartScale != mEndScale) {
            if(mStartScale < mEndScale) {
                scale = Easing.Easing(mUtil, getProgressByElapse(elapse) * mDuration, mStartScale, (mEndScale - mStartScale), mDuration);
            } else {
                scale = mStartScale - Easing.Easing(mUtil, getProgressByElapse(elapse) * mDuration, mEndScale, mStartScale, mDuration);
            }
        } else {
            scale = mStartScale + getProgressByElapse(elapse) * (mEndScale - mStartScale);
        }
        Matrix.scaleM(mMVPMatrix, 0, scale, scale, 0);
        return mMVPMatrix;
    }

    @Override
    public float getAlpha(long elapse) {
        return mStartAlpha - (mStartAlpha - mEndAlpha) * getProgressByElapse(elapse);
    }

    @Override
    public float getScaleSize(long elapse) {
        return scale;
    }
}