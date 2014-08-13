package com.s890510.microfilm.script.effects;

import java.util.ArrayList;

import android.opengl.Matrix;

public class EffectShowInCenter extends BasicEffect
{
    private final static int DURATION = 3000;
    private float[] mMVPMatrix = new float[16]; //the texture
    private float mScale = 1;
    private float transX = 0;
    private float transY = 0;
    public EffectShowInCenter() {
        mDuration = DURATION;
        mSleep = DURATION;
    }

    public EffectShowInCenter(String shader) {
        mDuration = DURATION;
        mSleep = DURATION;
        setShader(shader);
    }

    public EffectShowInCenter(int duration, String shader) {
        mDuration = DURATION;
        mSleep = DURATION;
        setShader(shader);
    }

    public EffectShowInCenter(int duration, ArrayList<String> str, String shader) {
        mDuration = duration;
        mSleep = duration;
        mString = str;
        setShader(shader);
    }

    public EffectShowInCenter(int duration) {
        mDuration = duration;
        mSleep = duration;
    }

    public EffectShowInCenter(int duration, float scale) {
        mDuration = duration;
        mScale = scale;
        mSleep = duration;
    }

    public EffectShowInCenter(int duration, int mask) {
        mDuration = duration;
        mSleep = duration;
        mMask = mask;
    }

    public EffectShowInCenter(int duration, float scale, float x, float y) {
        mDuration = duration;
        mScale = scale;
        mSleep = duration;
        transX = x;
        transY = y;
    }

    public EffectShowInCenter(int duration, float scale, float x, float y, int mask) {
        mDuration = duration;
        mScale = scale;
        mSleep = duration;
        transX = x;
        transY = y;
        mMask = mask;
    }

    @Override
    public int getEffectType() {
        return EFFECT_SHOW;
    }

    @Override
    public float[] getMVPMatrixByElapse(long elapse) {
        Matrix.setIdentityM(mMVPMatrix, 0);
        Matrix.scaleM(mMVPMatrix, 0, mScale, mScale, 0);
        Matrix.translateM(mMVPMatrix, 0, transX, transY, 0);
        return mMVPMatrix;
    }

    @Override
    public float getScaleSize(long elapse) {
        return mScale;
    }
}
