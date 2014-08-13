package com.s890510.microfilm.script.effects;

import android.opengl.Matrix;

import com.s890510.microfilm.ProcessGL;
import com.s890510.microfilm.util.Easing;

public class EffectTranslateX extends BasicEffect {
    private final static int DURATION    = 1000;
    private float            mTransX;
    private float            mStartTrans = 1.0f;
    private float            mEndTrans   = 1.0f;
    private float            mStartAlpha = 1.0f;
    private float            mEndAlpha   = 1.0f;
    private float            mStartScale = -1;
    private float            mEndScale   = -1;
    private int              mUtil       = 0;
    private float[]          mMVPMatrix  = new float[16]; // the texture
    private float            scale       = 1.0f;

    public EffectTranslateX(ProcessGL processGL) {
        mDuration = DURATION;
        mSleep = DURATION;
        mStartTrans = processGL.ScreenRatio;
        mEndTrans = processGL.ScreenRatio;
    }

    public EffectTranslateX(ProcessGL processGL, int duration) {
        mDuration = duration;
        mSleep = duration;
        mStartTrans = processGL.ScreenRatio;
        mEndTrans = processGL.ScreenRatio;
    }

    public EffectTranslateX(int duration, float StartPos, float EndPos) {
        mDuration = duration;
        mSleep = duration;
        mStartTrans = StartPos;
        mEndTrans = EndPos;
    }

    public EffectTranslateX(int duration, float scale, float StartPos, float EndPos) {
        mDuration = duration;
        mSleep = duration;
        mStartTrans = StartPos;
        mEndTrans = EndPos;
        mStartScale = scale;
    }

    public EffectTranslateX(int duration, float scale, float StartPos, float EndPos, int mask) {
        mDuration = duration;
        mSleep = duration;
        mStartTrans = StartPos;
        mEndTrans = EndPos;
        mStartScale = scale;
        mMask = mask;
    }

    public EffectTranslateX(int duration, float scale, float StartPos, float EndPos, float StartAlpha, float EndAlpha) {
        mDuration = duration;
        mSleep = duration;
        mStartTrans = StartPos;
        mEndTrans = EndPos;
        mStartScale = scale;
        mStartAlpha = StartAlpha;
        mEndAlpha = EndAlpha;
    }

    public EffectTranslateX(int duration, float StartPos, float EndPos, float Startscale, float Endscale, float StartAlpha, float EndAlpha, int mask) {
        mDuration = duration;
        mSleep = duration;
        mStartTrans = StartPos;
        mEndTrans = EndPos;
        mStartScale = Startscale;
        mEndScale = Endscale;
        mStartAlpha = StartAlpha;
        mEndAlpha = EndAlpha;
        mMask = mask;
    }

    public EffectTranslateX(int duration, float StartPos, float EndPos, float Startscale, float Endscale, float StartAlpha, float EndAlpha, int mask,
            int util) {
        mDuration = duration;
        mSleep = duration;
        mStartTrans = StartPos;
        mEndTrans = EndPos;
        mStartScale = Startscale;
        mEndScale = Endscale;
        mStartAlpha = StartAlpha;
        mEndAlpha = EndAlpha;
        mMask = mask;
        mUtil = util;
    }

    @Override
    public int getEffectType() {
        return EFFECT_TRANSLATE_IN_FROM_LEFT;
    }

    @Override
    public float[] getMVPMatrixByElapse(long elapse) {
        Matrix.setIdentityM(mMVPMatrix, 0);
        if(mUtil != 0) {
            // mTransX = mStartTrans * getProgressByElapse(elapse) - mEndTrans;
            if(mEndTrans < 0) {
                mTransX = -Easing.Easing(mUtil, getProgressByElapse(elapse) * mDuration, 0, Math.abs(mEndTrans), mDuration) + mStartTrans;
            } else {
                mTransX = Easing.Easing(mUtil, getProgressByElapse(elapse) * mDuration, 0, Math.abs(mEndTrans), mDuration) + mStartTrans;
            }
        } else {
            mTransX = mStartTrans * getProgressByElapse(elapse) - mEndTrans;
        }
        Matrix.translateM(mMVPMatrix, 0, mTransX, 0, 0);
        if(mStartScale != -1) {
            if(mEndScale != -1) {
                scale = mStartScale + getProgressByElapse(elapse) * (mEndScale - mStartScale);
            } else {
                scale = mStartScale;
            }
            Matrix.scaleM(mMVPMatrix, 0, scale, scale, 0);
        }
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
