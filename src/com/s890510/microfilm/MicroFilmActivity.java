package com.s890510.microfilm;

import java.util.ArrayList;
import java.util.Map;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;

public class MicroFilmActivity extends Activity {
    private static final String TAG = "MainActivity";

    private static final int SELECT_PHOTO = 100;
    private ArrayList<MediaInfo> mMediaInfo = new ArrayList<MediaInfo>();
    private ArrayList<Map<String, Object>> mItems = new ArrayList<Map<String,Object>>();
    private GridView mGridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        /*
        SurfaceView sv = (SurfaceView) findViewById(R.id.fboActivity_surfaceView);
        sv.getHolder().addCallback(this);
        sv.getHolder().setFormat(PixelFormat.TRANSPARENT);
        */

        //mSaveCallback = new SaveCallback();
        
        if(getIntent().getBooleanExtra("ToEdit", false)) {
        	PhotoEdit();
        } else {
        	PhotoSelect();
        }
        
        Log.d(TAG, "onCreate done");
    }

    private void PhotoEdit() {
    	setContentView(R.layout.asus_micromovie_edit);

    	MicroFilmAdapter mAdapter = new MicroFilmAdapter(getApplicationContext());
    	mGridView = (GridView)findViewById(R.id.asus_micromovie_editshow);
    	mGridView.setNumColumns(4);
    	mGridView.setAdapter(mAdapter);
    }
    
    private void PhotoSelect() {
    	setContentView(R.layout.activity_main);
    	
    	mGridView = (GridView)findViewById(R.id.asus_micromovie_startshow);
    	mGridView.setNumColumns(4);
    	
    	Button btn = (Button) findViewById(R.id.click_buttonA);
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
        
        Button btn1 = (Button) findViewById(R.id.click_buttonB);
        btn1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
		        intent.setClass(getApplicationContext(), MicroMovieActivity.class);
		        startActivity(intent);
		        finish();
			}
		});
    }
    
    private void setImage(Uri uri) {
    	Log.e(TAG, "Uri:" + uri);
    	MediaInfo mInfo = new MediaInfo(this);
		mInfo.setup(uri);
		mMediaInfo.add(mInfo);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

        switch(requestCode) { 
	        case SELECT_PHOTO:
	            if(resultCode == RESULT_OK){
	            	mMediaInfo = ((MediaPool)getApplicationContext()).getMediaInfo();

	            	Uri SingleImage = imageReturnedIntent.getData();
	                if(SingleImage == null) {
	                	ClipData MultiImage = imageReturnedIntent.getClipData();
	                	for(int i=0; i<MultiImage.getItemCount(); i++) {
	                		setImage(MultiImage.getItemAt(i).getUri());	                		
	                	}
	                } else {
	                	setImage(imageReturnedIntent.getData());
	                }
	                
	                ((MediaPool)getApplicationContext()).setMediaInfo(mMediaInfo);
	                MicroFilmAdapter mAdapter = new MicroFilmAdapter(getApplicationContext());
	                mGridView.setAdapter(mAdapter);
	            }
        }
    }
}
