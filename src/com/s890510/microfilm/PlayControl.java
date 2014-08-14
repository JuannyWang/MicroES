package com.s890510.microfilm;

import java.util.ArrayList;

import com.s890510.microfilm.script.Timer;
public class PlayControl {
    private static String TAG = "PlayControl";
    private MicroMovieSurfaceView mSurfaceView;
    private ThemeControl mThemeControl = null;

    public PlayControl(MicroMovieSurfaceView surfaceview) {
        mSurfaceView = surfaceview;
    }

    public void CreateControl(ArrayList<ElementInfo> FileOrder) {
        if(mThemeControl != null) {
            mThemeControl = null;
        }
        mThemeControl = new ThemeControl(FileOrder, mSurfaceView);
    }

    public boolean isAlive() {
        if(mThemeControl == null) {
            return false;
        } else {
            return mThemeControl.isAlive();
        }
    }

    public void terminate() {
        mThemeControl.terminate();
    }

    public void interrupt() {
        mThemeControl.interrupt();
    }

    public void join() {
        try {
            mThemeControl.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        mThemeControl.start();
    }

    public void onResume() {
        mThemeControl.onResume();
    }

    public void setElapse(int elapse) {
        mThemeControl.setElapse(elapse);
    }

    public void setSleep(int sleep) {
        mThemeControl.setSleep(sleep);
    }

    public void onDestory() {
        if(mThemeControl != null) {
            mThemeControl = null;
        }
    }

    private static class ThemeControl extends Thread {
        private Object mPauseLock;
        private int mSleep = 0;
        private Timer mTimer;
        private MicroMovieSurfaceView mSurfaceView;
        private ArrayList<ElementInfo> mFileOrder;
        private boolean firstStrat = true;
        private boolean mIsDone = false;
        private boolean mSetProgress = false;
        public int playIndex = 0;
        private long mTime = 0;
        private long mElapseTime = 0;
        private int mLossTime = 0;

        public ThemeControl(ArrayList<ElementInfo> FileOrder, MicroMovieSurfaceView surfaceview) {
            mPauseLock = new Object();
            mSurfaceView = surfaceview;
            mFileOrder = FileOrder;
            mTimer = surfaceview.getTimer();
        }

        public void setSleep(int sleep)
        {
            mSleep = sleep;
        }

        public void setElapse(int elapse) {
            mTimer.setElapse(elapse);
            playIndex = mSurfaceView.getScript().getItemIndexByElapse(elapse);
            mSetProgress = true;
            mLossTime = 0;
        }

        private void doSleep(int time) {
            try {
                mTime = System.currentTimeMillis();
                sleep(time);
                if(mLossTime > 0) {
                    mLossTime = 0;
                }
            } catch (InterruptedException e) {
                mLossTime = time - (int) (System.currentTimeMillis() - mTime);
            } catch (IllegalArgumentException e) {
                //do nothing
            }
        }

        @Override
        public void run() {
            ElementInfo eInfo;
            int effectsize = mSurfaceView.getScript().geteffectsize();

            /*
            for(int i=0; i<mFileOrder.size(); i++) {
                ElementInfo a = mFileOrder.get(i);
                Log.e(TAG, "mFileOrder(" + i + ") [0]=>" + a.Type + ", [1]=>" + a.TextureId);
            }
            */

            if(firstStrat) {
                firstStrat = false;
                mTimer.resetTimer();
            }

            do {
                if(mIsDone) break;

                if(mLossTime == 0) {
                    if(playIndex < effectsize) {
                        if(!mSetProgress) {
                            eInfo = mFileOrder.get(playIndex % mFileOrder.size());
                            playIndex++;

                            //Need to find sometime to rewrite...
                            if(eInfo.Type == MediaInfo.MEDIA_TYPE_IMAGE) {
                                mSurfaceView.changeBitmap(eInfo, true);
                            }

                            //here we need to check again for mSetProgress
                            if(!mSetProgress && !mSurfaceView.checkPause()) {
                                if(mElapseTime == 0)
                                    doSleep(eInfo.time);
                                else
                                    doSleep(eInfo.time - (int)(System.currentTimeMillis() - mElapseTime));
                                mElapseTime = System.currentTimeMillis();
                            }
                        }

                        if(mSetProgress && !mSurfaceView.checkPause()) {
                            mSetProgress = false;
                            doSleep(mSleep);
                            mElapseTime = System.currentTimeMillis();
                            mSleep = 0;
                            playIndex++;
                        }
                    }
                }

                if(mSurfaceView.checkPause()) {
                    synchronized (mPauseLock) {
                        try {
                            mPauseLock.wait();
                        } catch (InterruptedException e) {

                        }
                    }

                    //If mLossTime > 0 it's means actually sleep times != need sleep times
                    if(mLossTime > 0 && !mSetProgress && !mIsDone) {
                        doSleep(mLossTime);
                        mElapseTime = System.currentTimeMillis();
                    }
                }

            } while(playIndex < effectsize && !mIsDone);

            if(!mIsDone) {
                mSurfaceView.IsPlayFin(true);
                mSurfaceView.SendMSG(MicroMovieSurfaceView.MSG_PLAYFIN);
            }

            mSurfaceView = null;
            mFileOrder = null;
            mTimer = null;

            try {
                super.finalize();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        public void terminate() {
            mIsDone = true;
            synchronized (mPauseLock) {
                mPauseLock.notifyAll();
            }
        }

        public void onResume() {
            synchronized (mPauseLock) {
                mPauseLock.notifyAll();
            }
        }
    }
}
