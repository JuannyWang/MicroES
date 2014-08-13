
package com.s890510.microfilm.script.effects;

import java.util.ArrayList;

public abstract class Effect
{
    public static final int VIDEO_EFFECT                         = 0;
    public static final int EFFECT_DEFAULT                       = 10;
    public static final int EFFECT_SHOW                          = 11;
    public static final int EFFECT_TRANSLATE_IN_FROM_LEFT        = 12;
    public static final int EFFECT_TRANSLATE_OUT_TO_RIGHT        = 13;
    public static final int EFFECT_SLIDE_SCALE_FORM_LEFT         = 14;
    public static final int EFFECT_SLIDE_SCALE_FORM_RIGTH        = 15;
    public static final int EFFECT_SCALE_IN                      = 16;
    public static final int EFFECT_SCALE_OUT                     = 17;
    public static final int EFFECT_FLASH_SCALE                   = 18;
    public static final int EFFECT_MIRROR                        = 19;
    public static final int EFFECT_COMBO                         = 20;
    public static final int EFFECT_NOT_SHOW                      = 21;
    public static final int EFFECT_TRANSLATE_IN_FROM_LEFT_BOTTOM = 22;
    public static final int EFFECT_SHOW_IN_LEFT_HALF             = 23;
    public static final int EFFECT_SHOW_IN_RIGHT_HALF            = 24;
    public static final int EFFECT_BY_SETTING                    = 30;
    public static final int EFFECT_BY_ALPHA                      = 31;

    abstract public int getDuration();

    abstract public int getDuration(long elapse);

    abstract public int getSleep();

    abstract public long getElapseTime(long elapse);

    abstract public Effect getEffect(long elapse);

    abstract public void setSleep(int sleep);

    abstract public void setDuration(int duration);

    abstract public void setShader(String shader);

    abstract public int getEffectType();

    abstract public float[] getMVPMatrixByElapse(long elapse);

    abstract public float[] getMultipleMVPMatrixByElapse(long elapse, int num);

    abstract public float getTextureWidthScaleRatio();

    abstract public float getTextureHightScaleRatio();

    abstract public void setTextureRatio(float w, float h);

    abstract public float getAlpha(long elapse);

    abstract public boolean showBackground();

    abstract public String getShader();

    abstract public ArrayList<String> getString();

    abstract public void setString(ArrayList<String> str);

    abstract public float getProgressByElapse(long elapse);

    abstract public int getMaskType(long elapse);

    abstract public boolean getTransition(long elapse);

    abstract public void setTransition(boolean set);

    abstract public void setCount(int count);

    abstract public int getCount(long elapse);

    abstract public void setshowBackground(boolean show);

    abstract public void setBGColor(float[] color);

    abstract public float[] getBGColor(long elapse);

    abstract public void setTextSize(float size);

    abstract public float getTextSize(long elapse);

    abstract public void setFixBound(int Fix);

    abstract public int getFixBound(long elapse);

    abstract public void setRunPos(float mStart, float mEnd);

    abstract public float[] getRunPos(long elapse);

    abstract public void setConvertInfo(int mType, int mSize);

    abstract public int getConvertType();

    abstract public int getConvertSize();

    abstract public void setIsNoItem(boolean set);

    abstract public boolean getIsNoItem();

    abstract public void setStringType(int type);

    abstract public int getStringType();

    abstract public void setIsInCount(boolean set);

    abstract public boolean getIsInCount();

    abstract public float getScaleSize(long elapse);
}
