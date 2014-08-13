package com.s890510.microfilm;

import java.util.ArrayList;

import android.app.Application;
import android.database.Cursor;
import android.provider.MediaStore;

import com.s890510.microfilm.util.ThreadPool;

public class MediaPool extends Application {
    private static final String  TAG         = "MediaPool";
    private ThreadPool           mLocationThreadPool, mBitmapThreadPool;
    private ArrayList<MediaInfo> mMediaInfo  = new ArrayList<MediaInfo>();

    private ArrayList<String>    mPath       = new ArrayList<String>();
    private ArrayList<String>    mUriPath    = new ArrayList<String>();
    private ArrayList<Integer>   mType       = new ArrayList<Integer>();
    private ArrayList<Integer>   mRotate     = new ArrayList<Integer>();
    private ArrayList<Long>      mDate       = new ArrayList<Long>();
    private ArrayList<Double>    mLatitude   = new ArrayList<Double>();
    private ArrayList<Double>    mLongitude  = new ArrayList<Double>();

    private int                  ItemCount   = 0;
    public int                   InfoCounter = 0;

    public ArrayList<MediaInfo> getMediaInfo() {
        return mMediaInfo;
    }

    public void setMediaInfo(ArrayList<MediaInfo> info) {
        mMediaInfo = info;

        if(mMediaInfo.size() > 0) {
            for(int i = 0; i < mMediaInfo.size(); i++) {
                mPath.add(mMediaInfo.get(i).getPath());
                mUriPath.add(mMediaInfo.get(i).getUri().getPath());
                mType.add(mMediaInfo.get(i).getType());
                mRotate.add(mMediaInfo.get(i).getRotate());
                mDate.add(mMediaInfo.get(i).getDate());
                mLatitude.add(mMediaInfo.get(i).getLatitude());
                mLongitude.add(mMediaInfo.get(i).getLongitude());
            }
        }
    }

    public void addInfo(MediaInfo info) {
        mMediaInfo.add(info);

        mPath.add(info.getPath());
        mUriPath.add(info.getUri().getPath());
        mType.add(info.getType());
        mRotate.add(info.getRotate());
        mDate.add(info.getDate());
        mLatitude.add(info.getLatitude());
        mLongitude.add(info.getLongitude());
    }

    public void setCount(int count) {
        ItemCount = count;
    }

    public int getImageCount() {
        if(ItemCount == 0) {
            Cursor cursor = getApplicationContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    new String[] { MediaStore.Images.Media._ID }, null, null, null);

            if(cursor != null && cursor.moveToNext()) {
                return cursor.getCount();
            }
        }
        return ItemCount;
    }

    public int getCount() {
        return mMediaInfo.size();
    }

    public ArrayList<String> getPath() {
        return mPath;
    }

    public ArrayList<String> getUriPath() {
        return mUriPath;
    }

    public ArrayList<Integer> getType() {
        return mType;
    }

    public ArrayList<Integer> getRotate() {
        return mRotate;
    }

    public ArrayList<Long> getDate() {
        return mDate;
    }

    public ArrayList<Double> getLatitude() {
        return mLatitude;
    }

    public ArrayList<Double> getLongitude() {
        return mLongitude;
    }

    public synchronized ThreadPool getLocationThreadPool() {
        if(mLocationThreadPool == null) {
            mLocationThreadPool = new ThreadPool(1, 1, "Event-Location-pool");
        }
        return mLocationThreadPool;
    }

    public synchronized ThreadPool getBitmapThreadPool() {
        if(mBitmapThreadPool == null) {
            mBitmapThreadPool = new ThreadPool(4, 8, "Event-Bitmap-pool");
        }
        return mBitmapThreadPool;
    }
}
