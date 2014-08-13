package com.s890510.microfilm.script.effects;

import android.opengl.Matrix;

import com.s890510.microfilm.ProcessGL;

public class EffectTranslateY extends BasicEffect {
    private final static int DURATION    = 1000;
    private float            mTransY;
    private float            mStartTrans = 1.0f;
    private float            mEndTrans   = 1.0f;
    private float            mStartAlpha = 1.0f;
    private float            mEndAlpha   = 1.0f;
    private float            mStartScale = -1;
    private float            mEndScale   = -1;
    private float            mPosX       = 0;
    private float[]          mMVPMatrix  = new float[16]; // the texture
    private float            scale       = 1.0f;

    public EffectTranslateY(ProcessGL processGL) {
        mDuration = DURATION;
        mSleep = DURATION;
        mStartTrans = processGL.ScreenRatio;
        mEndTrans = processGL.ScreenRatio;
    }

    public EffectTranslateY(ProcessGL processGL, int duration) {
        mDuration = duration;
        mSleep = duration;
        mStartTrans = processGL.ScreenRatio;
        mEndTrans = processGL.ScreenRatio;
    }

    public EffectTranslateY(int duration, float StartPos, float EndPos) {
        mDuration = duration;
        mSleep = duration;
        mStartTrans = StartPos;
        mEndTrans = EndPos;
    }

    public EffectTranslateY(int duration, float scale, float StartPos, float EndPos) {
        mDuration = duration;
        mSleep = duration;
        mStartTrans = StartPos;
        mEndTrans = EndPos;
        mStartScale = scale;
    }

    public EffectTranslateY(int duration, float scale, float StartPos, float EndPos, int mask) {
        mDuration = duration;
        mSleep = duration;
        mStartTrans = StartPos;
        mEndTrans = EndPos;
        mStartScale = scale;
        mMask = mask;
    }

    public EffectTranslateY(int duration, float Scale, float StartPos, float EndPos, float StartAlpha, float EndAlpha, float PosX) {
        mDuration = duration;
        mSleep = duration;
        mStartTrans = StartPos;
        mEndTrans = EndPos;
        mStartScale = Scale;
        mStartAlpha = StartAlpha;
        mEndAlpha = EndAlpha;
        mPosX = PosX;
    }

    public EffectTranslateY(int duration, float StartScale, float EndScale, float StartPos, float EndPos, float StartAlpha, float EndAlpha, float PosX) {
        mDuration = duration;
        mSleep = duration;
        mStartTrans = StartPos;
        mEndTrans = EndPos;
        mStartScale = StartScale;
        mEndScale = EndScale;
        mStartAlpha = StartAlpha;
        mEndAlpha = EndAlpha;
        mPosX = PosX;
    }

    public EffectTranslateY(int duration, float StartPos, float EndPos, float StartScale, float EndScale, float StartAlpha, float EndAlpha,
            float PosX, int mask) {
        mDuration = duration;
        mSleep = duration;
        mStartTrans = StartPos;
        mEndTrans = EndPos;
        mStartScale = StartScale;
        mEndScale = EndScale;
        mStartAlpha = StartAlpha;
        mEndAlpha = EndAlpha;
        mPosX = PosX;
        mMask = mask;
    }

    @Override
    public int getEffectType() {
        return EFFECT_TRANSLATE_IN_FROM_LEFT;
    }

    @Override
    public float[] getMVPMatrixByElapse(long elapse) {
        Matrix.setIdentityM(mMVPMatrix, 0);
        mTransY = mStartTrans * getProgressByElapse(elapse) - mEndTrans;
        Matrix.translateM(mMVPMatrix, 0, mPosX, mTransY, 0);
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
