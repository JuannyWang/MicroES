package com.s890510.microfilm.script;

import java.util.ArrayList;

import com.asus.gallery.micromovie.ElementInfo;

public abstract class Script
{
    protected float[] mMVPMatrix = new float[16];
    abstract public float[] getMVPMatrixByTimeElapse(long elapse, boolean wait, int type, int num);
    abstract public int getItemIndexByElapse(long elapse);
    abstract public long getSleepByElapse(long elapse);
    abstract public int getTotalDuration();
    abstract public ArrayList<ElementInfo> setElementInfoTime(ArrayList<ElementInfo> info);
    abstract public ArrayList<ElementInfo> getElementInfoByElapseTime(long elapse, ArrayList<ElementInfo> info);
    abstract public int getMusicId();
    abstract public int getFilterId();
    abstract public void updateTextureScaleRatio(ElementInfo info, int i);
    abstract public int geteffectsize();
    abstract public void resetItemElapse(long elapse, ArrayList<ElementInfo> info);
    abstract public int getFilterNumber();
    abstract public float ColorRed();
    abstract public float ColorGreen();
    abstract public float ColorBlue();
    abstract public float GetRed();
    abstract public float GetGreen();
    abstract public float GetBlue();
    abstract public int GetScriptId();
    abstract public int GetSloganType();
    abstract public boolean CheckNoItem(int Id);
    abstract public boolean CheckInCount(int Id);
    abstract public int getNoItemSize();
    abstract public int getNoCountSize();
    abstract public float[] getFilterLeft();
    abstract public float[] getFilterRight();
}
