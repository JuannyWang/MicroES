package com.s890510.microfilm.script.effects;

import android.opengl.Matrix;

import com.asus.gallery.micromovie.Util;

public class EffectBySetting extends BasicEffect
{
    private String TAG = "EffectBySetting";
    private float[] mMVPMatrix = new float[16]; //the texture
    private float mStartScale = 1;
    private float mFinalScale = 1;
    private float mStartAlpha = 1.0f;
    private float mEndAlpha = 1.0f;
    private float mStartX = 0;
    private float mStartY = 0;
    private float mFinalX = 0;
    private float mFinalY = 0;
    private int mUtil = 0;
    private float scale = 1.0f;

    public EffectBySetting(int duration, int sleep, float startScale, float finalScale , float startX, float startY, float finalX, float finalY) {
        mDuration = duration;
        mSleep = sleep;
        mStartScale = startScale;
        mFinalScale = finalScale;
        mStartX = startX;
        mStartY = startY;
        mFinalX = finalX;
        mFinalY = finalY;
    }

    public EffectBySetting(int duration, int sleep, float startScale, float finalScale , float startX, float startY, float finalX, float finalY, int mask) {
        mDuration = duration;
        mSleep = sleep;
        mStartScale = startScale;
        mFinalScale = finalScale;
        mStartX = startX;
        mStartY = startY;
        mFinalX = finalX;
        mFinalY = finalY;
        mMask = mask;
    }

    public EffectBySetting(int duration, float startScale, float finalScale, float startalpha, float endalpha, float startX, float startY, float finalX, float finalY) {
        mDuration = duration;
        mSleep = duration;
        mStartScale = startScale;
        mFinalScale = finalScale;
        mStartAlpha = startalpha;
        mEndAlpha = endalpha;
        mStartX = startX;
        mStartY = startY;
        mFinalX = finalX;
        mFinalY = finalY;
    }

    public EffectBySetting(int duration, float startScale, float finalScale, float startalpha, float endalpha, float startX, float startY, float finalX, float finalY, int util) {
        mDuration = duration;
        mSleep = duration;
        mStartScale = startScale;
        mFinalScale = finalScale;
        mStartAlpha = startalpha;
        mEndAlpha = endalpha;
        mStartX = startX;
        mStartY = startY;
        mFinalX = finalX;
        mFinalY = finalY;
        mUtil = util;
    }

    public EffectBySetting(int duration, float startScale, float finalScale, float startalpha, float endalpha, float startX, float startY, float finalX, float finalY, int util, int mask) {
        mDuration = duration;
        mSleep = duration;
        mStartScale = startScale;
        mFinalScale = finalScale;
        mStartAlpha = startalpha;
        mEndAlpha = endalpha;
        mStartX = startX;
        mStartY = startY;
        mFinalX = finalX;
        mFinalY = finalY;
        mUtil = util;
        mMask = mask;
    }

    public EffectBySetting(int duration, int sleep) {
        mDuration = duration;
        mSleep = sleep;
    }

    @Override
    public int getEffectType() {
        return EFFECT_BY_SETTING;
    }

    @Override
    public float[] getMVPMatrixByElapse(long elapse) {
        float progress, transX, transY;
        progress = getProgressByElapse(elapse);
        if(mUtil == 0) {
            transX = (mFinalX - mStartX) * progress + mStartX;
            transY = (mFinalY - mStartY) * progress + mStartY;
            scale = (mFinalScale - mStartScale ) * progress + mStartScale;
        } else {
            transX = (mFinalX - mStartX) * Util.Easing(mUtil, progress*mDuration, 0, 1, mDuration) + mStartX;
            transY = (mFinalY - mStartY) * Util.Easing(mUtil, progress*mDuration, 0, 1, mDuration) + mStartY;
            scale = (mFinalScale - mStartScale ) * Util.Easing(mUtil, progress*mDuration, 0, 1, mDuration) + mStartScale;
        }

        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.translateM(mMVPMatrix, 0, transX, transY, 0);
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
