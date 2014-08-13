package com.s890510.microfilm.script;

import com.s890510.microfilm.MicroMovieActivity;
import com.s890510.microfilm.ProcessGL;

public class Timer {
    private static String TAG = "Timer";
    private long mStartTime;
    private long mTotal;
    private long mElpaseOffset = 0;
    private long mSystemTime = 0;
    private MicroMovieActivity mActivity;

    private long mElapseForEncode = 0;
    private ProcessGL mProcessGL;

    public Timer(long total, MicroMovieActivity activity, ProcessGL processGL) {
        mActivity = activity;
        mTotal =  total;
        mProcessGL = processGL;
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
        if(mProcessGL.isEncode()){     	
            return mElapseForEncode;
        } else if(mActivity.checkPause()) {
            mSystemTime = System.currentTimeMillis();
            return mSystemTime - mStartTime + mElpaseOffset;
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
