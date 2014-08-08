package com.s890510.microfilm;

import java.util.ArrayList;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MicroFilmActivity extends Activity {
    private static final String TAG = "MainActivity";

    private static final int SELECT_PHOTO = 100;
    private ArrayList<MediaInfo> mMediaInfo = new ArrayList<MediaInfo>();

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
	                if(selectedImage == null) {
	                	ClipData pickedImage = imageReturnedIntent.getClipData();
		            	Log.e(TAG, "pickedImage:" + pickedImage.getItemCount());
	                } else {
	                	Log.e(TAG, "AAAAAAAAAAAAAA");
	                }
	                
	                
	            	//ClipData pickedImage = imageReturnedIntent.getClipData();
	            	//Log.e(TAG, "pickedImage:" + pickedImage.getItemCount());
	            	/*
	                Uri selectedImage = imageReturnedIntent.getData();
	                MediaInfo mInfo = new MediaInfo(this);
	                
	                mInfo.setup(selectedImage);
	                mMediaInfo.add(mInfo);
	                MediaPool mPool = (MediaPool)getApplicationContext();
	                mPool.setMediaInfo(mMediaInfo);
	                */
	            }
        }
        
        for(int i=0; i<mMediaInfo.size(); i++)
        	Log.e(TAG, "i:" + i + ", Path:" + mMediaInfo.get(i).getPath());
    }
}
