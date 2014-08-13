package com.s890510.microfilm.script.effects;

import android.opengl.Matrix;

import com.s890510.microfilm.ProcessGL;
import com.s890510.microfilm.util.Easing;

public class EffectTranslateOutToLeft extends BasicEffect {
    private final static int DURATION   = 1000;
    private float            scaleX     = 0;
    private float[]          mMVPMatrix = new float[16]; // the texture
    private int              mUtil;
    private ProcessGL        mProcessGL;

    public EffectTranslateOutToLeft(ProcessGL processGL) {
        mDuration = DURATION;
        mSleep = DURATION;
        mProcessGL = processGL;
    }

    public EffectTranslateOutToLeft(ProcessGL processGL, int duration, int util) {
        mDuration = duration;
        mSleep = duration;
        mUtil = util;
        mProcessGL = processGL;
    }

    @Override
    public int getEffectType() {
        return EFFECT_TRANSLATE_OUT_TO_RIGHT;
    }

    @Override
    public float[] getMVPMatrixByElapse(long elapse) {
        Matrix.setIdentityM(mMVPMatrix, 0);
        scaleX = -Easing.Easing(mUtil, getProgressByElapse(elapse) * mDuration, 0, mProcessGL.ScreenRatio * 2, mDuration);
        Matrix.translateM(mMVPMatrix, 0, scaleX, 0, 0);
        return mMVPMatrix;
    }

}
