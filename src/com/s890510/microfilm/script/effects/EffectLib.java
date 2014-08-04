package com.s890510.microfilm.script.effects;

import java.util.ArrayList;

import com.asus.gallery.micromovie.ProcessGL;
import com.s890510.microfilm.shader.Shader;

public class EffectLib {
    public static Effect String(int[] Duration, int Sleep, boolean[] Count, String mShader, ArrayList<String> str, boolean[] Trans, int[] mUtil,
            float[] StartScale, float[] EndScale, float[] StartAlpha, float[] EndAlpha, float WRatio, float HRatio, int[] mMask, int[] mType, int size, int sType) {
        ArrayList<Effect> mElements = new ArrayList<Effect>();

        for(int i=0; i<Duration.length; i++){
            Effect mEffect = null;
            if(mType[i] == 1) {
                mEffect = new EffectShowInCenter(Duration[i], mMask[i]);
            } else if(mType[i] == 2) {
                mEffect = new EffectScale(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], mMask[i], mUtil[i]);
            } else if(mType[i] == 3) {
                mEffect = new EffectScale(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], mMask[i], mUtil[i]);
                mEffect.setshowBackground(true);
                mEffect.setBGColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f}); //white
            } else if(mType[i] == 4) {
                mEffect = new EffectScale(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], mMask[i], mUtil[i]);
                mEffect.setshowBackground(true);
                mEffect.setBGColor(new float[]{0.56f, 0.37f, 0.54f, 1.0f});
            } else if(mType[i] == 5) {
                mEffect = new EffectScale(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], mMask[i], mUtil[i]);
                mEffect.setshowBackground(true);
                mEffect.setBGColor(new float[]{0.45f, 0.45f, 0.427f, 1.0f});
            } else if(mType[i] == 6) {
                mEffect = new EffectScale(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], mMask[i], mUtil[i]);
                mEffect.setshowBackground(true);
                mEffect.setBGColor(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
            } else if(mType[i] == 7) {
                mEffect = new EffectTranslateY(Duration[i], -1.5f, 0.0f, StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], 0.0f, mMask[i]);
            } else if(mType[i] == 8) {
                mEffect = new EffectScale(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], mMask[i], mUtil[i]);
                mEffect.setshowBackground(true);
                mEffect.setBGColor(new float[]{0.035f, 0.3058f, 0.75294f, 1.0f});
            } else if(mType[i] == 9) {
                mEffect = new EffectScale(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], mMask[i], mUtil[i]);
                mEffect.setshowBackground(true);
                mEffect.setBGColor(new float[]{0.34f, 0.72f, 0.99f, 1.0f});
            } else if(mType[i] == 10) {
                mEffect = new EffectShowInCenter(Duration[i], mMask[i]);
                mEffect.setshowBackground(true);
                mEffect.setBGColor(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
            }

            if(Trans[i]) {
                mEffect.setTransition(true);
            }

            if(Count[i]) {
                mEffect.setCount(i+1);
            }

            if(size > 0) {
                mEffect.setTextSize(size);
            }
            mElements.add(mEffect);
        }

        ComboEffect mComboEffect = new ComboEffect(mElements);
        mComboEffect.setSleep(Sleep);
        mComboEffect.setShader(mShader);
        mComboEffect.setString(str);
        mComboEffect.setTextureRatio(WRatio, HRatio);
        mComboEffect.setIsNoItem(true);

        //0 -> None, 1 -> Need Info
        mComboEffect.setStringType(sType);

        return mComboEffect;
    }

    public static Effect String_Translate(int[] Duration, int Sleep, boolean[] Count, String mShader, ArrayList<String> str, boolean[] Trans, int[] mUtil, int ConvertType, int ConvertSize,
            float[] StartPosX, float[] EndPosX, float[] StartPosY, float[] EndPosY, float[] StartScale, float[] EndScale,
            float[] StartAlpha, float[] EndAlpha, float WRatio, float HRatio, int[] mMask, int[] mType, int size, int sType) {
        ArrayList<Effect> mElements = new ArrayList<Effect>();

        for(int i=0; i<Duration.length; i++){
            Effect mEffect = null;
            if(mType[i] == 1) {
                mEffect = new EffectBySetting(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], StartPosX[i], StartPosY[i], EndPosX[i], EndPosY[i], mUtil[i], mMask[i]);
            }

            if(Trans[i]) {
                mEffect.setTransition(true);
            }

            if(Count[i]) {
                mEffect.setCount(i+1);
            }

            if(size > 0) {
                mEffect.setTextSize(size);
            }
            mElements.add(mEffect);
        }

        ComboEffect mComboEffect = new ComboEffect(mElements);
        mComboEffect.setSleep(Sleep);
        mComboEffect.setShader(mShader);
        mComboEffect.setString(str);
        mComboEffect.setTextureRatio(WRatio, HRatio);
        mComboEffect.setIsNoItem(true);

        //0 -> None, 1 -> Need Info
        mComboEffect.setStringType(sType);

        if(ConvertType > 0) {
            mComboEffect.setConvertInfo(ConvertType, ConvertSize);
        }

        return mComboEffect;
    }

    public static Effect Scale_Fade(ProcessGL processGL, int[] Duration, int Sleep, String mShader, boolean[] Trans, int[] mUtil, int ConvertType, int ConvertSize,
            float[] StartScale, float[] EndScale, float[] StartAlpha, float[] EndAlpha, float WRatio, float HRatio, int[] mMask, int[] mType) {
        ArrayList<Effect> mElements = new ArrayList<Effect>();

        for(int i=0; i<Duration.length; i++){
            Effect mEffect = null;

            if(mType[i] == 1) {
                mEffect = new EffectShowInCenter(Duration[i], mMask[i]);
            } else if(mType[i] == 2) {
                mEffect = new EffectScale(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], mMask[i], mUtil[i]);
            } else if(mType[i] == 3) {
                mEffect = new EffectShowInLeftHalf(processGL, Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i]);
            } else if(mType[i] == 4) {
                mEffect = new EffectShowInRightHalf(processGL, Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i]);
            } else if(mType[i] == 5) {
                mEffect = new EffectScale(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], mMask[i], mUtil[i]);
                mEffect.setshowBackground(true);
                mEffect.setBGColor(new float[]{0.34f, 0.72f, 0.99f, 1.0f});
            } else if(mType[i] == 7) {
                mEffect = new EffectScale(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], mMask[i], mUtil[i]);
                mEffect.setshowBackground(true);
                mEffect.setBGColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f}); //white
            } else if(mType[i] >= 8 && mType[i] <= 16) {
                float PosX = 0;
                float ratio = processGL.ScreenRatio;
                if(mType[i] == 8 || mType[i] == 11 || mType[i] == 14) {
                    PosX = -ratio * 2.0f/3.0f;
                } else if(mType[i] == 10 || mType[i] == 13 || mType[i] == 16) {
                    PosX = ratio * 2.0f/3.0f;
                }
                mEffect = new EffectTranslateY(Duration[i], StartScale[i], EndScale[i], 0.0f, 0.0f, StartAlpha[i], EndAlpha[i], PosX);
                mEffect.setFixBound(Shader.BOUNDING);

                if(mType[i] >= 11 && mType[i] <= 13){
                    mEffect.setshowBackground(true);
                    mEffect.setBGColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f}); //white
                }

                if(mType[i] >= 14 && mType[i] <= 16){
                    mEffect.setshowBackground(true);
                    mEffect.setBGColor(new float[]{0.45f, 0.45f, 0.427f, 1.0f});
                }
            }

            if(Trans[i]) {
                mEffect.setTransition(true);
            }

            mElements.add(mEffect);
        }

        ComboEffect mComboEffect = new ComboEffect(mElements);
        mComboEffect.setSleep(Sleep);
        mComboEffect.setShader(mShader);
        mComboEffect.setTextureRatio(WRatio, HRatio);

        if(ConvertType > 0) {
            mComboEffect.setConvertInfo(ConvertType, ConvertSize);
        }

        return mComboEffect;
    }

    public static Effect Rotate_Translate(int[] Duration, int Sleep, String mShader, boolean[] Trans, int[] mUtil, int ConvertType, int ConvertSize,
            float[] StartPosX, float[] EndPosX, float[] StartPosY, float[] EndPosY, float[] StartScale, float[] EndScale,
            float[] StartAlpha, float[] EndAlpha, float[] StartRotate, float[] EndRotate, float WRatio, float HRatio, int[] mMask, int[] mRType, int[] mType) {
        ArrayList<Effect> mElements = new ArrayList<Effect>();

        for(int i=0; i<Duration.length; i++){
            Effect mEffect = null;

            if(mType[i] == 1) {
                mEffect = new EffectScale(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], mMask[i], mUtil[i]);
            } else if(mType[i] == 2) {
                mEffect = new EffectRotate(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], StartRotate[i], EndRotate[i], mMask[i], mUtil[i], mRType[i]);
            } else if(mType[i] == 3) {
                mEffect = new EffectRotate(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], StartRotate[i], EndRotate[i], mMask[i], mUtil[i], mRType[i]);
                mEffect.setshowBackground(true);
                mEffect.setBGColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f}); //white
            } else if(mType[i] == 4) {
                mEffect = new EffectBySetting(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], StartPosX[i], StartPosY[i], EndPosX[i], EndPosY[i], mUtil[i]);
            } else if(mType[i] == 5) {
                mEffect = new EffectBySetting(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], StartPosX[i], StartPosY[i], EndPosX[i], EndPosY[i], mUtil[i]);
                mEffect.setshowBackground(true);
                mEffect.setBGColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f}); //white
            }

            if(Trans[i]) {
                mEffect.setTransition(true);
            }

            mElements.add(mEffect);
        }

        ComboEffect mComboEffect = new ComboEffect(mElements);
        mComboEffect.setSleep(Sleep);
        mComboEffect.setShader(mShader);
        mComboEffect.setTextureRatio(WRatio, HRatio);

        if(ConvertType > 0) {
            mComboEffect.setConvertInfo(ConvertType, ConvertSize);
        }

        return mComboEffect;
    }

    public static Effect Translate(ProcessGL processGL, int[] Duration, int Sleep, String mShader, boolean[] Trans, int[] mUtil, int ConvertType, int ConvertSize,
            boolean IsInCount, float[] StartPos, float[] EndPos, float[] StartScale, float[] EndScale, float[] StartAlpha, float[] EndAlpha, float WRatio,
            float HRatio, int[] mMask, int[] mType) {
        ArrayList<Effect> mElements = new ArrayList<Effect>();

        for(int i=0; i<Duration.length; i++){
            Effect mEffect = null;
            if(mType[i] == 1) {
                mEffect = new EffectTranslateX(Duration[i], StartPos[i], EndPos[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], mMask[i], mUtil[i]);
            } else if(mType[i] == 2) {
                mEffect = new EffectScale(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], mMask[i], mUtil[i]);
            } else if(mType[i] == 3) {
                mEffect = new EffectShowInCenter(Duration[i], 1, StartPos[i], 0, mMask[i]);
            } else if(mType[i] == 4) {
                mEffect = new EffectTranslateOutToLeft(processGL, Duration[i], mUtil[i]);
            } else if(mType[i] == 5) {
                mEffect = new EffectTransInFromLeftBottom(Duration[i], StartPos[i], EndPos[i]);
            } else if(mType[i] == 6) {
                mEffect = new EffectTranslateY(Duration[i], StartPos[i], EndPos[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], 0, mMask[i]);
            } else if(mType[i] >= 8 && mType[i] <= 13) {
                float PosX = 0;
                float ratio = processGL.ScreenRatio;
                if(mType[i] == 8 || mType[i] == 11) {
                    PosX = -ratio * 2.0f/3.0f;
                } else if(mType[i] == 10 || mType[i] == 13) {
                    PosX = ratio * 2.0f/3.0f;
                }
                if(mType[i] >= 8 && mType[i] <= 10)
                    mEffect = new EffectTranslateY(Duration[i], StartPos[i], EndPos[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], PosX, mMask[i]);
                else if(mType[i] >= 11 && mType[i] <= 13)
                    mEffect = new EffectTranslateX(Duration[i], StartPos[i], EndPos[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], mMask[i], mUtil[i]);

                mEffect.setFixBound(Shader.BOUNDING);
            }

            if(Trans[i]) {
                mEffect.setTransition(true);
            }

            mElements.add(mEffect);
        }

        ComboEffect mComboEffect = new ComboEffect(mElements);
        mComboEffect.setSleep(Sleep);
        mComboEffect.setShader(mShader);
        mComboEffect.setTextureRatio(WRatio, HRatio);

        if(!IsInCount) {
            mComboEffect.setIsInCount(false);
        }

        if(ConvertType > 0) {
            mComboEffect.setConvertInfo(ConvertType, ConvertSize);
        }

        return mComboEffect;
    }

    public static Effect Cover(int[] Duration, int Sleep, String mShader, boolean[] Trans, int[] mUtil, int ConvertType, int ConvertSize, float x, float y,
            float[] StartScale, float[] EndScale, float[] StartAlpha, float[] EndAlpha, float WRatio, float HRatio, int[] mMask, int[] mType) {
        ArrayList<Effect> mElements = new ArrayList<Effect>();

        for(int i=0; i<Duration.length; i++){
            Effect mEffect = null;

            if(mType[i] == 1) {
                mEffect = new EffectShowInCenter(Duration[i], StartScale[i], x, y, mMask[i]);
            }

            if(Trans[i]) {
                mEffect.setTransition(true);
            }

            mElements.add(mEffect);
        }

        ComboEffect mComboEffect = new ComboEffect(mElements);
        mComboEffect.setSleep(Sleep);
        mComboEffect.setShader(mShader);
        mComboEffect.setTextureRatio(WRatio, HRatio);

        if(ConvertType > 0) {
            mComboEffect.setConvertInfo(ConvertType, ConvertSize);
        }

        return mComboEffect;
    }

    public static Effect Bound(ProcessGL processGL, int[] Duration, int Sleep, String mShader, int ConvertType, int ConvertSize, float[] StartPosX, float[] EndPosX, float[] StartPosY, float[] EndPosY, float ShowPos, float EndPos,
            float[] StartScale, float[] EndScale, float[] StartAlpha, float[] EndAlpha, float WRatio, float HRatio, boolean[] Trans, int[] Bound, int[] mType) {
        ArrayList<Effect> mElements = new ArrayList<Effect>();

        for(int i=0; i<Duration.length; i++){
            Effect mEffect = null;
            if(mType[i] == 1) {
                mEffect = new EffectBySetting(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], StartPosX[i], StartPosY[i], EndPosX[i], EndPosY[i]);
            } else if(mType[i] == 2) {
                mEffect = new EffectBySetting(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], StartPosX[i], StartPosY[i], EndPosX[i], EndPosY[i]);
                mEffect.setRunPos(ShowPos, EndPos);
            } else if(mType[i] == 3) {
                mEffect = new EffectBySetting(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], StartPosX[i], StartPosY[i], EndPosX[i], EndPosY[i]);
                mEffect.setshowBackground(true);
                mEffect.setBGColor(new float[]{1.0f, 0.84f, 0.52f, 1.0f});
            } else if(mType[i] == 4) {
                mEffect = new EffectShowInRightHalf(processGL, Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i]);
            } else if(mType[i] == 5) {
                mEffect = new EffectBySetting(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], StartPosX[i], StartPosY[i], EndPosX[i], EndPosY[i]);
                mEffect.setRunPos(ShowPos, EndPos);
                mEffect.setshowBackground(true);
                mEffect.setBGColor(new float[]{0.0f, 0.0f, 0.0f, 1.0f});
            } else if(mType[i] == 6) {
                mEffect = new EffectBySetting(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], StartPosX[i], StartPosY[i], EndPosX[i], EndPosY[i]);
                mEffect.setRunPos(ShowPos, EndPos);
                mEffect.setshowBackground(true);
                mEffect.setBGColor(new float[]{0.035f, 0.3058f, 0.75294f, 1.0f});
            } else if(mType[i] == 7) {
                mEffect = new EffectBySetting(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i], StartPosX[i], StartPosY[i], EndPosX[i], EndPosY[i]);
                mEffect.setshowBackground(true);
                mEffect.setBGColor(new float[]{1.0f, 1.0f, 1.0f, 1.0f});
            }

            if(Trans[i]) {
                mEffect.setTransition(true);
            }

            if(Bound[i] != 0) {
                mEffect.setFixBound(Bound[i]);
            }

            mElements.add(mEffect);
        }

        ComboEffect mComboEffect = new ComboEffect(mElements);
        mComboEffect.setSleep(Sleep);
        mComboEffect.setShader(mShader);

        mComboEffect.setTextureRatio(WRatio, HRatio);

        if(ConvertType > 0) {
            mComboEffect.setConvertInfo(ConvertType, ConvertSize);
        }

        return mComboEffect;
    }

    public static Effect Filter(int[] Duration, int Sleep, String mShader, float[] StartScale, float[] EndScale, float[] StartAlpha, float[] EndAlpha,
            float mRed, float mGreen, float mBlue, int[] mType) {
        ArrayList<Effect> mElements = new ArrayList<Effect>();

        for(int i=0; i<Duration.length; i++){
            Effect mEffect = null;
            if(mType[i] == 1) {
                mEffect = new EffectScale(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i]);
                mEffect.setBGColor(new float[]{mRed, mGreen, mBlue, 1.0f});
            } else if(mType[i] == 2) {
                mEffect = new EffectScale(Duration[i], StartScale[i], EndScale[i], StartAlpha[i], EndAlpha[i]);
                mEffect.setshowBackground(true);
                mEffect.setBGColor(new float[]{mRed, mGreen, mBlue, 1.0f});
            }

            mElements.add(mEffect);
        }
        ComboEffect mComboEffect = new ComboEffect(mElements);
        mComboEffect.setSleep(Sleep);
        mComboEffect.setShader(mShader);
        mComboEffect.setIsNoItem(true);

        return mComboEffect;
    }

    public static Effect Slogan(int Duration[], int Sleep, String mShader, float[] StartAlpha, float[] EndAlpha,
            int[] mMask, boolean[] Trans) {
        ArrayList<Effect> mElements = new ArrayList<Effect>();

        for(int i=0; i<Duration.length; i++) {
            Effect mEffect = new EffectScale(Duration[i], 1.0f, 1.0f, StartAlpha[i], EndAlpha[i], mMask[i]);

            if(Trans[i]) {
                mEffect.setTransition(true);
            }

            mElements.add(mEffect);
        }

        ComboEffect mComboEffect = new ComboEffect(mElements);
        mComboEffect.setSleep(Sleep);
        mComboEffect.setShader(mShader);
        mComboEffect.setIsNoItem(true);

        return mComboEffect;
    }
}
