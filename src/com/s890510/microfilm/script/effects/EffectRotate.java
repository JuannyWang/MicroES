package com.s890510.microfilm.script.effects;

import android.opengl.Matrix;

import com.s890510.microfilm.util.Easing;

public class EffectRotate extends BasicEffect {
    private final static int DURATION     = 5000;
    private float            mStartScale  = 1.0f;
    private float            mEndScale    = 1.3f;          // SCALE_RATE must
                                                           // bigger than
                                                           // SLIDE_RATE
    private float            mStartAlpha  = 1.0f;
    private float            mEndAlpha    = 1.0f;
    private float            mStartRotate = 0.0f;
    private float            mEndRotate   = 180.0f;
    private float[]          mMVPMatrix   = new float[16];
    private float            scale        = 0;
    private int              mUtil        = 0;
    private int              mRType       = -1;           // 0->z 1->x 2->y
    private boolean          mRotate      = false;

    public EffectRotate() {
        mDuration = DURATION;
        mSleep = DURATION;
    }

    public EffectRotate(int duration, float startrate, float endrate, float startalpha, float endalpha, float startrotate, float endrotate, int mask,
            int util, int rtype) {
        mDuration = duration;
        mSleep = duration;
        mStartScale = startrate;
        mEndScale = endrate;
        mStartAlpha = startalpha;
        mEndAlpha = endalpha;
        mStartRotate = startrotate;
        mEndRotate = endrotate;
        mMask = mask;
        mUtil = util;
        mRType = rtype;
        mRotate = true;
    }

    @Override
    public int getEffectType() {
        return EFFECT_SCALE_OUT;
    }

    @Override
    public float[] getMVPMatrixByElapse(long elapse) {
        float progress = getProgressByElapse(elapse);
        Matrix.setIdentityM(mMVPMatrix, 0);
        if(mUtil != 0) {
            scale = mStartScale + Easing.Easing(mUtil, progress * mDuration, 0, 1, mDuration) * (mEndScale - mStartScale);
        } else {
            scale = mStartScale + progress * (mEndScale - mStartScale);
        }
        Matrix.scaleM(mMVPMatrix, 0, scale, scale, 0);

        if(mRotate) {
            float rotate;
            if(mUtil != 0) {
                rotate = mStartRotate + Easing.Easing(mUtil, progress * mDuration, 0, 1, mDuration) * (mEndRotate - mStartRotate);
            } else {
                rotate = mStartRotate + progress * (mEndRotate - mStartRotate);
            }

            if(mRType == 0) {
                Matrix.rotateM(mMVPMatrix, 0, rotate, 0, 0, 1);
            } else if(mRType == 1) {
                Matrix.rotateM(mMVPMatrix, 0, rotate, 1, 0, 0);
            } else if(mRType == 2) {
                Matrix.rotateM(mMVPMatrix, 0, rotate, 0, 1, 0);
            }
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