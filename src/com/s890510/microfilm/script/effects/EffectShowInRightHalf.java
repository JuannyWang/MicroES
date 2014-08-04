package com.s890510.microfilm.script.effects;

import android.opengl.Matrix;

import com.asus.gallery.micromovie.ProcessGL;

public class EffectShowInRightHalf extends BasicEffect
{
    private final static int DURATION = 2000;
    private final static int SLEEP = 2000;
    private float[] mMVPMatrix = new float[16]; //the texture

    private boolean NeedScale = false;
    private float mStartScale = 0.5f;
    private float mEndScale = 0.5f;
    private float mStartAlpha = 1.0f;
    private float mEndAlpha = 1.0f;
    private float scale = 1.0f;
    private ProcessGL mProcessGL;

    public EffectShowInRightHalf(ProcessGL processGL) {
        mDuration = DURATION;
        mSleep = SLEEP;
        mProcessGL = processGL;
    }

    public EffectShowInRightHalf(ProcessGL processGL, int duration) {
        mDuration = duration;
        mSleep = SLEEP;
        mProcessGL = processGL;
    }

    public EffectShowInRightHalf(ProcessGL processGL, int duration, float sScale, float eScale, float sAlpha, float eAlpha) {
        mDuration = duration;
        mSleep = SLEEP;
        mStartScale = sScale;
        mEndScale = eScale;
        mStartAlpha = sAlpha;
        mEndAlpha = eAlpha;
        NeedScale = true;
        mProcessGL = processGL;
    }

    public EffectShowInRightHalf(ProcessGL processGL, int duration, float sScale, float eScale, float sAlpha, float eAlpha, int mask) {
        mDuration = duration;
        mSleep = SLEEP;
        mStartScale = sScale;
        mEndScale = eScale;
        mStartAlpha = sAlpha;
        mEndAlpha = eAlpha;
        NeedScale = true;
        mMask = mask;
        mProcessGL = processGL;
    }

    @Override
    public int getEffectType() {
        return EFFECT_SHOW_IN_RIGHT_HALF;
    }

    @Override
    public float[] getMVPMatrixByElapse(long elapse) {
        Matrix.setIdentityM(mMVPMatrix, 0);
        if(NeedScale) {
            scale = mStartScale + getProgressByElapse(elapse) * (mEndScale - mStartScale);
            Matrix.scaleM(mMVPMatrix, 0, scale, scale, 0);
        }
        Matrix.translateM(mMVPMatrix, 0, mProcessGL.ScreenRatio/2, 0, 0);
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