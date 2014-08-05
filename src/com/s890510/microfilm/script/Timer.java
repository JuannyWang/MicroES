package com.s890510.microfilm.script;

import com.s890510.microfilm.MicroFilmActivity;
import com.s890510.microfilm.draw.GLDraw;


public class Timer {
    private static String TAG = "Timer";
    private long mStartTime;
    private long mTotal;
    private long mElpaseOffset = 0;
    private long mSystemTime = 0;
    private MicroFilmActivity mActivity;

    private long mElapseForEncode = 0;
    private GLDraw mGLDraw;

    public Timer(long total, MicroFilmActivity activity, GLDraw processGL) {
        mActivity = activity;
        mTotal =  total;
        mGLDraw = processGL;
        resetTimer();
    }

    public void setElapseForEncode(long elapse){
        mElapseForEncode = elapse;
    }


    public void setElapse(long elpase) {
        mStartTime = System.currentTimeMillis();
        mElpaseOffset = elpase;
    }

    public void resetTimer() {
        mStartTime = System.currentTimeMillis();
        mElpaseOffset = 0;
    }

    public long getElapse() {
        if(mGLDraw.isEncode()){     	
            return mElapseForEncode;
        } else if(mActivity.checkPause()) {
            mSystemTime = System.currentTimeMillis();
            return mSystemTime - mStartTime + mElpaseOffset;
        } else if(mGLDraw.getVideoWait()) {      	
            return mTotal;
        } else {
            mSystemTime = System.currentTimeMillis();
            return mSystemTime - mStartTime + mElpaseOffset;
        }
    }

    public long SeekBarElapse() {
        if(mActivity.checkPause()) {
            return mSystemTime - mStartTime + mElpaseOffset;
        } else {
            mSystemTime = System.currentTimeMillis();
            return mSystemTime - mStartTime + mElpaseOffset;
        }
    }

    public boolean isAlive() {
        return getElapse() < mTotal;
    }
}
