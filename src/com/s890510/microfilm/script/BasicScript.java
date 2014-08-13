package com.s890510.microfilm.script;

import java.util.ArrayList;

import android.opengl.Matrix;

import com.s890510.microfilm.ElementInfo;
import com.s890510.microfilm.MicroMovieActivity;
import com.s890510.microfilm.MusicManager;
import com.s890510.microfilm.ProcessGL;
import com.s890510.microfilm.filter.FilterChooser;
import com.s890510.microfilm.script.effects.Effect;

public class BasicScript extends Script {
    private String            TAG           = "BasicScript";
    protected int             mStatus       = 0;
    protected int             totalDuration = 0;
    protected int             totalSleep    = 0;
    protected boolean         mIsFromEncode = false;
    protected int             mNoItem       = 0;
    protected int             mNoCount      = 0;
    ArrayList<Effect>         mEffects      = new ArrayList<Effect>();
    public MicroMovieActivity mActivity;
    private ProcessGL         mProcessGL;

    public BasicScript(MicroMovieActivity activity, ProcessGL processGL) {
        mActivity = activity;
        mProcessGL = processGL;
    }

    @Override
    public float[] getMVPMatrixByTimeElapse(long elapse, boolean wait, int type, int num) {
        if(mMVPMatrix == null)
            mMVPMatrix = new float[16];
        Matrix.setIdentityM(mMVPMatrix, 0);

        elapse = elapse % totalSleep;
        int effectElapse = 0;
        for(int i = 0; i < mEffects.size(); i++) {
            effectElapse += mEffects.get(i).getDuration();
            if(elapse < effectElapse || wait) {
                if(type == 1) {
                    mMVPMatrix = mEffects.get(i).getMVPMatrixByElapse(elapse - effectElapse + mEffects.get(i).getDuration());
                } else if(type == 2) {
                    mMVPMatrix = mEffects.get(i).getMultipleMVPMatrixByElapse(elapse - effectElapse + mEffects.get(i).getDuration(), num);
                }
                break;
            }
        }
        if(elapse > effectElapse && !wait)
            Matrix.translateM(mMVPMatrix, 0, -2.0f, 0, 0);
        return mMVPMatrix;
    }

    @Override
    public int geteffectsize() {
        return mEffects.size();
    }

    @Override
    public int getNoItemSize() {
        return mNoItem;
    }

    @Override
    public int getNoCountSize() {
        return mNoCount;
    }

    @Override
    public int getItemIndexByElapse(long elapse) {
        int index = (int) (mEffects.size() * (elapse / totalSleep));

        if(elapse > totalSleep) {
            return mEffects.size();
        }

        elapse = elapse % totalSleep;
        for(int i = 0; i < mEffects.size(); i++) {
            elapse -= mEffects.get(i).getSleep();
            if(elapse < 0) {
                index += i;
                break;
            }
        }
        return index;
    }

    @Override
    public int getTotalDuration() {
        return totalDuration;
    }

    protected void init() {
        for(int i = 0; i < mEffects.size(); i++) {
            totalDuration += mEffects.get(i).getDuration();
            totalSleep += mEffects.get(i).getSleep();
            if(mEffects.get(i).getIsNoItem()) {
                mNoItem++;
            }
            if(!mEffects.get(i).getIsInCount()) {
                mNoCount++;
            }
        }
    }

    @Override
    public ArrayList<ElementInfo> setElementInfoTime(ArrayList<ElementInfo> info) {
        int effNum = mEffects.size();
        for(int i = 0; i < info.size(); i++) {
            info.get(i).effect = mEffects.get(i % effNum);
            info.get(i).time = mEffects.get(i % effNum).getSleep();
            info.get(i).timer = new Timer(mEffects.get(i % effNum).getDuration(), mActivity, mProcessGL);
            info.get(i).scaleH = mEffects.get(i % effNum).getTextureHightScaleRatio();
            info.get(i).scaleW = mEffects.get(i % effNum).getTextureWidthScaleRatio();
            if(mEffects.get(i % effNum).getEffectType() == Effect.VIDEO_EFFECT)
                info.get(i).isVideo = true;
            if(info.get(i).effect.getStringType() == 1) {
                info.get(i).mDate = info.get(i - 1).mDate;
                info.get(i).mLocation = info.get(i - 1).mLocation;
            }
        }
        return info;
    }

    @Override
    public void updateTextureScaleRatio(ElementInfo info, int i) {
        int effNum = mEffects.size();
        info.scaleH = mEffects.get(i % effNum).getTextureHightScaleRatio();
        info.scaleW = mEffects.get(i % effNum).getTextureWidthScaleRatio();
    }

    @Override
    public long getSleepByElapse(long elapse) {
        elapse = elapse % totalSleep;
        for(int i = 0; i < mEffects.size(); i++) {
            elapse -= mEffects.get(i).getSleep();
            if(elapse < 0) {
                elapse += mEffects.get(i).getSleep();
                elapse = mEffects.get(i).getSleep() - elapse;
                break;
            }
        }
        return elapse;
    }

    @Override
    public int getMusicId() {
        return MusicManager.MUISC_ASUS_BEACH;
    }

    @Override
    public int getFilterId() {
        return FilterChooser.DEFAULT;
    }

    @Override
    public ArrayList<ElementInfo> getElementInfoByElapseTime(long elapse, ArrayList<ElementInfo> info) {
        ArrayList<ElementInfo> data = new ArrayList<ElementInfo>();
        int i = 0;

        if(elapse > totalSleep)
            return data;

        do {
            if(elapse <= info.get(i).effect.getDuration()) {
                info.get(i).timer.setElapse(elapse);
                data.add(info.get(i));
            }
            elapse -= info.get(i).effect.getSleep();
            i++;
        } while((int) elapse >= 0);
        return data;
    }

    @Override
    public void resetItemElapse(long elapse, ArrayList<ElementInfo> info) {
        int i = 0;
        if(elapse <= totalSleep) {
            do {
                if(elapse <= info.get(i).effect.getDuration()) {
                    info.get(i).timer.setElapse(elapse);
                }
                elapse -= info.get(i).effect.getSleep();
                i++;
            } while((int) elapse >= 0 && i < info.size());
        }
    }

    @Override
    public int getFilterNumber() {
        return 0;
    }

    @Override
    public float ColorRed() {
        return 1.0f;
    }

    @Override
    public float ColorGreen() {
        return 1.0f;
    }

    @Override
    public float ColorBlue() {
        return 1.0f;
    }

    @Override
    public float GetRed() {
        return 255;
    }

    @Override
    public float GetGreen() {
        return 255;
    }

    @Override
    public float GetBlue() {
        return 255;
    }

    @Override
    public int GetScriptId() {
        return -1;
    }

    protected boolean shouldReturnDefaultColor() {
        if(mIsFromEncode) {
            return false;
        } else {
            if(mActivity.checkPlay())
                return false;
            else
                return true;
        }
    }

    @Override
    public int GetSloganType() {
        // 0 -> Black, 1 -> White
        return 0;
    }

    @Override
    public boolean CheckNoItem(int Id) {
        return mEffects.get(Id).getIsNoItem();
    }

    @Override
    public boolean CheckInCount(int Id) {
        return mEffects.get(Id).getIsInCount();
    }

    @Override
    public float[] getFilterLeft() {
        return new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
    }

    @Override
    public float[] getFilterRight() {
        return new float[] { 1.0f, 1.0f, 1.0f, 1.0f };
    }
}
