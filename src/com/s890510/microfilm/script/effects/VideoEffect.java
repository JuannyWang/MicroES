package com.s890510.microfilm.script.effects;

import android.opengl.Matrix;

public class VideoEffect extends BasicEffect
{
    private float[] mMVPMatrix = new float[16]; //the texture
    private float mStartScale = 1;
    private float mFinalScale = 1;
    private float mStartX = 0;
    private float mStartY = 0;
    private float mFinalX = 0;
    private float mFinalY = 0;
    
    public VideoEffect(int duration, float startScale, float finalScale , float startX, float startY, float finalX, float finalY) {
        mDuration = duration;
        mSleep = duration;
        mStartScale = startScale;
        mFinalScale = finalScale;
        mStartX = startX;
        mStartY = startY;
        mFinalX = finalX;
        mFinalY = finalY;
    }

    @Override
    public int getEffectType() {
        return VIDEO_EFFECT;
    }

    @Override
    public float[] getMVPMatrixByElapse(long elapse) {
        float transX = (mFinalX - mStartX) * getProgressByElapse(elapse) + mStartX;
        float transY = (mFinalY - mStartY) * getProgressByElapse(elapse) + mStartY;
        float scaleX = (mFinalScale - mStartScale ) * getProgressByElapse(elapse) + mStartScale;
        float scaleY = (mFinalScale - mStartScale ) * getProgressByElapse(elapse) + mStartScale;
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.scaleM(mMVPMatrix, 0, scaleX, scaleY, 0);
        Matrix.translateM(mMVPMatrix, 0, transX, transY, 0);
        return mMVPMatrix;
    }
}
