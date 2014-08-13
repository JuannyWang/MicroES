package com.s890510.microfilm;

import java.util.ArrayList;
import java.util.Random;

import android.util.Log;

import com.s890510.microfilm.script.Script;

public class MicroMovieOrder {
    private static String                     TAG        = "MicroMovieOrder";
    private int[][]                           mOrderList = new int[ThemeAdapter.TYPE_Count][];
    private float[][]                         mCenterX   = new float[ThemeAdapter.TYPE_Count][];
    private float[][]                         mCenterY   = new float[ThemeAdapter.TYPE_Count][];
    private ArrayList<ArrayList<ElementInfo>> mOrderInfo = new ArrayList<ArrayList<ElementInfo>>();
    private final int                         mRange     = 3;
    private final int                         mSpacing   = (mRange * 2) + 1;

    public MicroMovieOrder(MicroMovieActivity activity) {
        Reset();
    }

    public void Reset() {
        mOrderList = new int[ThemeAdapter.TYPE_Count][];
        mCenterX = new float[ThemeAdapter.TYPE_Count][];
        mCenterY = new float[ThemeAdapter.TYPE_Count][];

        mOrderInfo.clear();
        for(int i = 0; i < ThemeAdapter.TYPE_Count; i++) {
            mOrderInfo.add(new ArrayList<ElementInfo>());
        }
    }

    public int[] getOrderList(int i) {
        return mOrderList[i];
    }

    public float[] getCenterX(int i) {
        return mCenterX[i];
    }

    public float[] getCenterY(int i) {
        return mCenterY[i];
    }

    public boolean IsOrder(int i) {
        return (mOrderInfo.get(i).size() > 0) ? true : false;
    }

    public ArrayList<ElementInfo> getOrderInfo(int i) {
        return mOrderInfo.get(i);
    }

    public void setOrderInfo(int i, ArrayList<ElementInfo> info) {
        mOrderInfo.set(i, info);

        mCenterX[i] = new float[info.size()];
        mCenterY[i] = new float[info.size()];
        for(int j = 0; j < info.size(); j++) {
            mCenterX[i][j] = info.get(j).centerX;
            mCenterY[i][j] = info.get(j).centerY;
        }
    }

    public void setOrderList(int i, int[] list, float[] x, float[] y) {
        mOrderList[i] = list;
        mCenterX[i] = x;
        mCenterY[i] = y;
    }

    public ArrayList<ElementInfo> gettimeandorder(ProcessGL processGL, ArrayList<MediaInfo> mMediaInfo, Script mScript, boolean IsShuffle) {
        ArrayList<ElementInfo> mFileOrder = new ArrayList<ElementInfo>();
        ArrayList<Integer> mFileBucket = new ArrayList<Integer>();
        ArrayList<Integer> mRandomBucket = new ArrayList<Integer>();
        ArrayList<Integer> mSpaceBucket = new ArrayList<Integer>();
        Random mRandom = new Random(System.currentTimeMillis());
        int mFileTotalCount = mMediaInfo.size(); // means remove fake file size
        int mEffectCount = mScript.geteffectsize();
        int mNoItemCount = mScript.getNoItemSize();
        int mNoCountSize = mScript.getNoCountSize();
        int mScriptId = mScript.GetScriptId();
        int mRunCount = 0;
        int mCenterCount = 0;
        int mStartCount = 0;
        int mEndCount = 0;
        int mFHalfCount = 0;
        int mLHalfCount = 0;
        int mAvgCount = 1;
        boolean mPastVideo = false;
        int[] mOrder = new int[mEffectCount];

        /*
         * here we have several thing to do, 1.Do not let vedeo play after
         * another video 2.First one not video
         */

        // remove the fake info
        Log.e(TAG, "mMediaInfo.size():" + mMediaInfo.size());
        for(int i = 0; i < mMediaInfo.size(); i++) {
            mRandomBucket.add(i);
        }

        int[] ElementCount = new int[mMediaInfo.size()];
        for(int i = 0; i < ElementCount.length; i++)
            ElementCount[i] = 0;

        mRunCount = mEffectCount - mNoItemCount - mNoCountSize;

        // if file count can't fill into head and tile we random it
        if(!IsShuffle) {
            if(mFileTotalCount < mRunCount / 2) {
                IsShuffle = true;
            } else {
                mStartCount = (int) Math.floor(mRunCount / 6);
                mEndCount = (int) Math.floor(mRunCount / 6);
                mCenterCount = (int) Math.floor(mRunCount / 6);
                mFHalfCount = (int) Math.floor((mRunCount - mStartCount - mEndCount - mCenterCount) / 2);
                mLHalfCount = mRunCount - mCenterCount - mFHalfCount - mStartCount - mEndCount;

                int sortcount = mStartCount + mEndCount + mCenterCount;
                int recount = mFileBucket.size() - sortcount;
                for(int i = 0; i < (int) Math.floor(recount / 2); i++) {
                    mFileBucket.remove(mStartCount);
                    recount--;
                }
                for(int i = 0; i < recount; i++) {
                    mFileBucket.remove(mStartCount + mCenterCount);
                }
            }
        }
        mAvgCount = (int) (Math.ceil((float) (mEffectCount - mNoItemCount - mNoCountSize) / mFileTotalCount)) - 1;

        Log.e(TAG, "mFileTotalCount:" + mFileTotalCount + ", mStartCount:" + mStartCount + ", mFHalfCount:" + mFHalfCount + ", mCenterCount:"
                + mCenterCount + ", mLHalfCount:" + mLHalfCount + ", mEndCount:" + mEndCount + ", mRunCount:" + mRunCount + ", IsShuffle:"
                + IsShuffle);

        // now we need start to put order
        // Default order is sort by date
        for(int i = 0; i < mEffectCount; i++) {
            ElementInfo eInfo = new ElementInfo();
            MediaInfo info = null;

            if(mScript.CheckNoItem(i)) {
                eInfo.Type = MediaInfo.MEDIA_TYPE_IMAGE;
                eInfo.height = processGL.ScreenHeight;
                eInfo.width = processGL.ScreenWidth;
                mOrder[i] = -1;
            } else if(!mScript.CheckInCount(i)) {
                // Random and push in...
                info = mMediaInfo.get(mRandom.nextInt(mMediaInfo.size()));
                eInfo.Type = MediaInfo.MEDIA_TYPE_IMAGE;
                eInfo.height = info.getImage().getHeight();
                eInfo.width = info.getImage().getWidth();
                eInfo.mFaceCount = info.mFaceCount;
                eInfo.mFBorder = info.mFBorder;
                eInfo.InfoId = info.CountId;
                eInfo.mDate = info.getDate();
                eInfo.mFaceRect = info.mFaceRect;

                mOrder[i] = info.CountId;
            } else {
                if(!IsShuffle) {
                    if(mOrderList[mScriptId] == null) {
                        if(mFileTotalCount >= mEffectCount) {
                            info = mMediaInfo.get(i);
                        } else {
                            if(mFileBucket.size() > 0
                                    && (mStartCount > 0 || (mFHalfCount == 0 && mCenterCount > 0) || (mLHalfCount == 0 && mEndCount > 0))) {
                                info = mMediaInfo.get(mFileBucket.get(0));
                                mFileBucket.remove(0);

                                if(mStartCount > 0)
                                    mStartCount--;
                                else if(mFHalfCount == 0 && mCenterCount > 0)
                                    mCenterCount--;
                                else if(mLHalfCount == 0 && mEndCount > 0)
                                    mEndCount--;
                            } else {
                                mSpaceBucket.clear();
                                int count = 0;
                                for(int j = i - 1; j >= 0; j--) {
                                    if(count == mRange)
                                        break;
                                    if(mFileOrder.get(j).InfoId != -1) {
                                        mSpaceBucket.add(mFileOrder.get(j).InfoId);
                                        count++;
                                    }
                                }

                                int mPos = 0;
                                if(mFHalfCount > 0) {
                                    if(mFHalfCount - 1 < mRange) {
                                        mPos = mRange - (mFHalfCount - 1);
                                    }

                                    if(mCenterCount < mPos) {
                                        mPos = mCenterCount;
                                    }
                                } else {
                                    if(mLHalfCount - 1 < mRange) {
                                        mPos = mRange - (mLHalfCount - 1);
                                    }

                                    if(mEndCount < mPos) {
                                        mPos = mEndCount;
                                    }
                                }

                                for(int j = 0; j < mPos; j++) {
                                    mSpaceBucket.add(mMediaInfo.get(mFileBucket.get(j)).CountId);
                                }

                                int tmp;
                                do {
                                    tmp = mRandom.nextInt(mRandomBucket.size());
                                    info = mMediaInfo.get(mRandomBucket.get(tmp));
                                } while(mSpaceBucket.contains(info.CountId) && mFileTotalCount > mSpacing);

                                if(ElementCount[info.CountId] >= mAvgCount) {
                                    mRandomBucket.remove(tmp);
                                }
                                if(mFHalfCount > 0)
                                    mFHalfCount--;
                                else if(mLHalfCount > 0)
                                    mLHalfCount--;
                            }
                        }
                    } else {
                        info = mMediaInfo.get(mOrderList[mScriptId][i]);
                        eInfo.centerX = mCenterX[mScriptId][i];
                        eInfo.centerY = mCenterY[mScriptId][i];
                        eInfo.mIsRestore = true;
                    }
                } else {
                    if(mSpaceBucket.size() > mSpacing) {
                        mSpaceBucket.remove(0);
                    }

                    int tmp;
                    do {
                        tmp = mRandom.nextInt(mRandomBucket.size());
                        info = mMediaInfo.get(mRandomBucket.get(tmp));
                    } while(mSpaceBucket.contains(info.CountId) && mFileTotalCount > mSpacing);

                    if(ElementCount[info.CountId] >= mAvgCount) {
                        mRandomBucket.remove(tmp);
                    }
                    mSpaceBucket.add(info.CountId);
                }

                if(info.getType() == MediaInfo.MEDIA_TYPE_IMAGE) {
                    eInfo.Type = MediaInfo.MEDIA_TYPE_IMAGE;
                    eInfo.height = info.getImage().getHeight();
                    eInfo.width = info.getImage().getWidth();
                    eInfo.mFaceCount = info.mFaceCount;
                    eInfo.mFBorder = info.mFBorder;
                    eInfo.InfoId = info.CountId;
                    eInfo.mDate = info.getDate();
                    eInfo.mFaceRect = info.mFaceRect;

                    if(info.mGeoInfo != null)
                        eInfo.mLocation = info.mGeoInfo.getLocation();
                    mPastVideo = false;
                }

                ElementCount[info.CountId]++;

                mOrder[i] = info.CountId;

                info = null;
            }

            mFileOrder.add(eInfo);
        }

        // for(int i=0; i<ElementCount.length; i++) Log.e(TAG, "i:" + i + ": " +
        // ElementCount[i]);
        mOrderList[mScriptId] = mOrder;

        return mFileOrder;
    }

    public ArrayList<ElementInfo> gettimeandorderForEncode(ArrayList<ElementInfo> elementInfo, ArrayList<MediaInfo> mMediaInfo, Script mScript) {
        ArrayList<ElementInfo> mFileOrder = new ArrayList<ElementInfo>();
        int ElementCount = elementInfo.size();

        // now we need start to put order
        // the textureId ==> 1:movie, 2-11:bitmap, 12-21:videoBitmap
        for(int i = 0; i < ElementCount; i++) {
            ElementInfo eInfo = new ElementInfo();
            ElementInfo oldEInfo = elementInfo.get(i);

            int infoId = elementInfo.get(i).InfoId;

            for(int j = 0; j < mMediaInfo.size(); j++) {
                if(mMediaInfo.get(j).CountId == infoId) {
                    MediaInfo tmp = mMediaInfo.get(j);
                    if(tmp.getType() == MediaInfo.MEDIA_TYPE_IMAGE) {
                        if(oldEInfo.Type == MediaInfo.MEDIA_TYPE_IMAGE) {

                            eInfo.height = tmp.getImage().getHeight();
                            eInfo.width = tmp.getImage().getWidth();

                            if(tmp.mGeoInfo != null)
                                eInfo.mLocation = tmp.mGeoInfo.getLocation();
                        }

                        eInfo.Type = MediaInfo.MEDIA_TYPE_IMAGE;
                        eInfo.mFaceCount = tmp.mFaceCount;
                        eInfo.mFBorder = tmp.mFBorder;
                        eInfo.InfoId = tmp.CountId;
                        eInfo.mDate = tmp.getDate();
                        eInfo.mFaceRect = tmp.mFaceRect;

                    }

                    break;
                }
            }

            mFileOrder.add(eInfo);
        }
        return mFileOrder;
    }
}
