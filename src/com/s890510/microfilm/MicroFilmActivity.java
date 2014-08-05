/*
 * Copyright 2013 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.s890510.microfilm;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.s890510.microfilm.util.ThreadPool;

public class MicroFilmActivity extends Activity {
    private static final String TAG = "MainActivity";
    
    private ArrayList<FileInfo> mFileList = new ArrayList<FileInfo>();
    private ArrayList<ElementInfo> mFileOrder = null;
    private Object mAttachView = new Object();

    private MicroFilmSurfaceView mMicroView = null;
    private boolean mIsSaving = false;
    private boolean isPause = false;
    private boolean isPlaying = false;
    
    private ProgressDialog mProgressDialog;
    public LoadTexture mLoadTexture;
    public MicroFilmOrder mMicroMovieOrder;
    public long mStartDate = 0;
    public long mEndDate = 0;
    public int mItemCount = 0;
    
    private int mInitBitmapCount = 0;
    private int mDoneBitmapCount = 0;
    
    private ThreadPool mLocationThreadPool;
    private ThreadPool mBitmapThreadPool;
    
    public int mVisioWidth = 1280;
    public int mVisioHeight = 720;
    
    interface ISaveCallback{
        void onSaveDone();
    }
    private SaveCallback mSaveCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        SurfaceView sv = (SurfaceView) findViewById(R.id.fboActivity_surfaceView);
        sv.getHolder().addCallback(this);
        sv.getHolder().setFormat(PixelFormat.TRANSPARENT);
        */

        mSaveCallback = new SaveCallback();
        mLoadTexture = new LoadTexture(this);

        Log.d(TAG, "onCreate done");
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
    	final DoingEncoder mEncoder = new DoingEncoder();
    	mProgressDialog = ProgressDialog.show(this, "Encoding", "Please wait...");
    	new Thread(new Runnable() {
			
			@Override
			public void run() {
				mEncoder.Start(mSaveCallback);				
			}
		}).start();
    	
    	return super.onMenuItemSelected(featureId, item);
    }

    public boolean checkPause() {
        return isPause;
    }

    public boolean checkPlay() {
        return isPlaying;
    }

    public long getStartDate() {
        return mStartDate;
    }

    public long getEndDate() {
        return mEndDate;
    }

    public int getItemCount() {
        return mItemCount;
    }

    public boolean isSaving(){
        return mIsSaving;
    }

    public void DoneLoadBitmap() {
        mDoneBitmapCount++;
        synchronized (mAttachView) {
            if(!mIsSetView) SetupMicroView();
        }
        if(mInitBitmapCount == mDoneBitmapCount) {
            //Here we need to quickly check again about bitmap
            for(int i=0; i<mFileList.size(); i++) {
                if(!mFileList.get(i).mIsInitial || mFileList.get(i).mBitmap == null) {
                    mFileList.remove(i);
                    i--;
                } else {
                    mFileList.get(i).CountId = i;
                }
            }
            mMicroView.setFiles(mFileList);
            mMicroView.InitData();
            mIsLoadBitmapDone = true;
            if(mMicroView.mReadyInit)
                mMicroView.SendMSG(MicroMovieSurfaceView.MSG_STOPPROGRASS);
        }
    }
    
    public class SaveCallback implements ISaveCallback {

		@Override
		public void onSaveDone() {
			mProgressDialog.dismiss();			
		}
    }
    
    public synchronized ThreadPool getLocationThreadPool() {
        if (mLocationThreadPool == null) {
            mLocationThreadPool = new ThreadPool(1, 1, "Event-Location-pool");
        }
        return mLocationThreadPool;
    }

    public synchronized ThreadPool getBitmapThreadPool() {
        if (mBitmapThreadPool == null) {
            mBitmapThreadPool = new ThreadPool(4, 8, "Event-Bitmap-pool");
        }
        return mBitmapThreadPool;
    }
}
