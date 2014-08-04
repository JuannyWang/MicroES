package com.s890510.microfilm.script.effects;

import java.util.ArrayList;

/************************************************
 * Effect<----BasicEffect<----ComboEffect
 *
 * ComboEffect can combo other effects to one effect
 *
 ************************************************/

public class ComboEffect extends BasicEffect {
    private static final String TAG = "ComboEffect";
    protected ArrayList<Effect> mElements = new ArrayList<Effect>();
    private boolean mShowBK = false;

    public ComboEffect(){
    }

    public ComboEffect(ArrayList<Effect> elements){
        mElements = elements;
        initCombo();
    }

    @Override
    public int getEffectType() {
        return EFFECT_COMBO;
    }

    @Override
    public Effect getEffect(long elapse) {
        for(int i = 0 ; i < mElements.size() ; i++ )
        {
            if(elapse > mElements.get(i).getDuration()) {
                elapse -= mElements.get(i).getDuration();
                continue;
            } else {
                return mElements.get(i);
            }
        }

        return null;
    }

    @Override
    public long getElapseTime(long elapse) {
        for(int i = 0 ; i < mElements.size() ; i++ )
        {
            if(elapse > mElements.get(i).getDuration()) {
                elapse -= mElements.get(i).getDuration();
                continue;
            }
            else {
                return elapse;
            }
        }

        return elapse;
    }

    @Override
    public float[] getMVPMatrixByElapse(long elapse) {
        for(int i = 0 ; i < mElements.size() ; i++ )
        {
            if(elapse > mElements.get(i).getDuration()) {
                elapse -= mElements.get(i).getDuration();
                continue;
            }
            else {
                return mElements.get(i).getMVPMatrixByElapse(elapse);
            }
        }
        return null;
    }

    @Override
    public float getScaleSize(long elapse) {
        for(int i = 0 ; i < mElements.size() ; i++ )
        {
            if(elapse > mElements.get(i).getDuration()) {
                elapse -= mElements.get(i).getDuration();
                continue;
            }
            else {
                return mElements.get(i).getScaleSize(elapse);
            }
        }
        return super.getScaleSize(elapse);
    }

    @Override
    public float getProgressByElapse(long elapse) {
        for(int i = 0 ; i < mElements.size() ; i++ )
        {
            if(elapse > mElements.get(i).getDuration()) {
                elapse -= mElements.get(i).getDuration();
                continue;
            }
            else {
                return mElements.get(i).getProgressByElapse(elapse);
            }
        }
        return 1.0f;
    }

    @Override
    public float getAlpha(long elapse) {
        for(int i = 0 ; i < mElements.size() ; i++ )
        {
            if(elapse > mElements.get(i).getDuration()) {
                elapse -= mElements.get(i).getDuration();
                continue;
            }
            else {
                return mElements.get(i).getAlpha(elapse);
            }
        }
        return 1.0f;
    }

    @Override
    public int getMaskType(long elapse) {
        for(int i = 0 ; i < mElements.size() ; i++ )
        {
            if(elapse > mElements.get(i).getDuration()) {
                elapse -= mElements.get(i).getDuration();
                continue;
            }
            else {
                return mElements.get(i).getMaskType(elapse);
            }
        }
        return 0;
    }

    @Override
    public int getDuration(long elapse) {
        for(int i = 0 ; i < mElements.size() ; i++ )
        {
            if(elapse > mElements.get(i).getDuration()) {
                elapse -= mElements.get(i).getDuration();
                continue;
            }
            else {
                return mElements.get(i).getDuration();
            }
        }
        return 0;
    }

    @Override
    public boolean getTransition(long elapse) {
        for(int i = 0 ; i < mElements.size() ; i++ )
        {
            if(elapse > mElements.get(i).getDuration()) {
                elapse -= mElements.get(i).getDuration();
                continue;
            }
            else {
                return mElements.get(i).getTransition(elapse);
            }
        }
        return false;
    }

    @Override
    public int getCount(long elapse) {
        for(int i = 0 ; i < mElements.size() ; i++ )
        {
            if(elapse > mElements.get(i).getDuration()) {
                elapse -= mElements.get(i).getDuration();
                continue;
            }
            else {
                return mElements.get(i).getCount(elapse);
            }
        }
        return 0;
    }

    @Override
    public float[] getBGColor(long elapse) {
        for(int i = 0 ; i < mElements.size() ; i++ )
        {
            if(elapse > mElements.get(i).getDuration()) {
                elapse -= mElements.get(i).getDuration();
                continue;
            }
            else {
                return mElements.get(i).getBGColor(elapse);
            }
        }
        return mColor;
    }

    @Override
    public float getTextSize(long elapse) {
        for(int i = 0 ; i < mElements.size() ; i++ )
        {
            if(elapse > mElements.get(i).getDuration()) {
                elapse -= mElements.get(i).getDuration();
                continue;
            }
            else {
                return mElements.get(i).getTextSize(elapse);
            }
        }
        return mTextSize;
    }

    @Override
    public int getFixBound(long elapse) {
        for(int i = 0 ; i < mElements.size() ; i++ )
        {
            if(elapse > mElements.get(i).getDuration()) {
                elapse -= mElements.get(i).getDuration();
                continue;
            }
            else {
                return mElements.get(i).getFixBound(elapse);
            }
        }
        return mFixBound;
    }

    @Override
    public float[] getRunPos(long elapse) {
        for(int i = 0 ; i < mElements.size() ; i++ )
        {
            if(elapse > mElements.get(i).getDuration()) {
                elapse -= mElements.get(i).getDuration();
                continue;
            }
            else {
                return mElements.get(i).getRunPos(elapse);
            }
        }
        return mPos;
    }

    @Override
    public int getDuration() {
        return mDuration;
    }

    private void initCombo(){
        mDuration = 0;
        mSleep = 0;

        for(Effect e : mElements){
            mDuration += e.getDuration();
            mSleep += e.getSleep();
            if(e.showBackground()) {
                mShowBK = true;
            }
        }
    }

    @Override
    public boolean showBackground() {
        return mShowBK;
    }
}
