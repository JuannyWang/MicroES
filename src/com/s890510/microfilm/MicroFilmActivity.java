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

import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MicroFilmActivity extends Activity {
    private static final String TAG = "MainActivity";
    
    private ProgressDialog mProgressDialog;
    private static final int SELECT_PHOTO = 100;

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
        
        Button btn = (Button) findViewById(R.id.click_button);
        btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//Asus Gallery not support multiple select
				Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
				photoPickerIntent.setType("image/*");
				photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
				startActivityForResult(photoPickerIntent, SELECT_PHOTO);
				/*
				Intent intent = new Intent();
		        intent.setClass(getApplicationContext(), MicroMovieActivity.class);
		        startActivity(intent);
		        */
			}
		});

        Log.d(TAG, "onCreate done");
    }

    

    private String[] getImageInfo(Uri uri) {
    	//We need some information like path, type, orientation, date, geo info.
    	
    	
    	
    	return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

        switch(requestCode) { 
        case SELECT_PHOTO:
            if(resultCode == RESULT_OK){  
                Uri selectedImage = imageReturnedIntent.getData();
                MediaInfo mInfo = new MediaInfo();
                mInfo.setup(selectedImage);
            }
        }
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
    
    public class MediaInfo {
    	private Uri mUri;
    	private String mType;
    	private String mPath;
    	private int mRotate = 0;
    	private long mDate;
    	private float[] mLatLong = new float[2];
    	private boolean mHaveLatLong = false;
    	private boolean mIsUTC = false;
    	
    	public void setup(Uri uri) {
    		mUri = uri;
    		mPath = getRealPath(uri);
    		mType = getMimeType(uri);

			try {
				ExifInterface mExif = new ExifInterface(mPath);
				int result = mExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED); 
				mRotate = 0; 
	            switch(result) { 
		            case ExifInterface.ORIENTATION_ROTATE_90: 
		            	mRotate = 90; 
		                break; 
		            case ExifInterface.ORIENTATION_ROTATE_180: 
		            	mRotate = 180; 
		                break; 
		            case ExifInterface.ORIENTATION_ROTATE_270: 
		            	mRotate = 270; 
		                break; 
		            default: 
		                break; 
	            }
	            
	            
	            Date mDate=new Date(getTime(uri, mExif.getAttribute(ExifInterface.TAG_DATETIME)));
	            SimpleDateFormat sFormatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
	            if(mIsUTC) sFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
	            Log.e(TAG, "Time:" + sFormatter.format(mDate));
	            
	            if(mExif.getLatLong(mLatLong))
	            	mHaveLatLong = true;
	            
			} catch (IOException e) {
				e.printStackTrace();
			}   		
    	}
    	
    	private String getMimeType(Uri uri) {
            Cursor cursor = getApplicationContext().getContentResolver().query(uri,
            		new String[] { MediaStore.MediaColumns.MIME_TYPE }, null, null, null);

            if (cursor != null && cursor.moveToNext()) {
                return cursor.getString(0);
            }

            return null;
        }
        
        private String getRealPath(Uri uri) {
            Cursor cursor = getApplicationContext().getContentResolver().query(uri,
            		new String[] { MediaStore.Images.Media.DATA }, null, null, null);
            
            if (cursor != null && cursor.moveToNext()) {
                return cursor.getString(0);
            }
            
            return null;
        }

        private long getTime(Uri uri) {
        	Cursor cursor = getApplicationContext().getContentResolver().query(uri,
            		new String[] { MediaStore.Images.Media.DATE_TAKEN }, null, null, null);
            
            if (cursor != null && cursor.moveToNext()) {
                return (long)cursor.getLong(0);
            }
            
            return -1;
        }
        
        private long getTime(Uri uri, String datetime) {
        	if(datetime == null) return getTime(uri);

        	SimpleDateFormat sFormatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        	sFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

        	ParsePosition pos = new ParsePosition(0);
            try {
                Date mDate = sFormatter.parse(datetime, pos);
                if(mDate == null) {
                	return getTime(uri);
                } else {
                	mIsUTC = true;
                	return mDate.getTime();
                }
            } catch (Exception e) {
            	return getTime(uri);
            }
        }
    }
}
