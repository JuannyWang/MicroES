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
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MicroFilmActivity extends Activity {
    private static final String TAG = "MainActivity";
    
    private ProgressDialog mProgressDialog;
    private static final int SELECT_PHOTO = 100;
    private ArrayList<MediaInfo> mMediaInfo = new ArrayList<MediaInfo>();

    /*
    interface ISaveCallback{
        void onSaveDone();
    }
    private SaveCallback mSaveCallback;
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        SurfaceView sv = (SurfaceView) findViewById(R.id.fboActivity_surfaceView);
        sv.getHolder().addCallback(this);
        sv.getHolder().setFormat(PixelFormat.TRANSPARENT);
        */

        //mSaveCallback = new SaveCallback();
        
        Button btn = (Button) findViewById(R.id.click_buttonAAA);
        btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//Asus Gallery not support multiple select
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
				startActivityForResult(photoPickerIntent, SELECT_PHOTO);        
			}
		});
        
        Button btn1 = (Button) findViewById(R.id.click_buttonBBB);
        btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
		        intent.setClass(getApplicationContext(), MicroMovieActivity.class);
		        startActivity(intent);		        
			}
		});

        Log.d(TAG, "onCreate done");
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

        switch(requestCode) { 
	        case SELECT_PHOTO:
	            if(resultCode == RESULT_OK){  
	                Uri selectedImage = imageReturnedIntent.getData();
	                MediaInfo mInfo = new MediaInfo(this);
	                mInfo.setup(selectedImage);
	                mMediaInfo.add(mInfo);
	                MediaPool mPool = (MediaPool)getApplicationContext();
	                mPool.setMediaInfo(mMediaInfo);
	            }
        }
        
        for(int i=0; i<mMediaInfo.size(); i++)
        	Log.e(TAG, "i:" + i + ", Path:" + mMediaInfo.get(i).getPath());
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

    /*
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

    public class SaveCallback implements ISaveCallback {

		@Override
		public void onSaveDone() {
			mProgressDialog.dismiss();			
		}
    }
    */
}
