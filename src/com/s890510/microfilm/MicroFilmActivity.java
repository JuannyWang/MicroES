package com.s890510.microfilm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

public class MicroFilmActivity extends Activity {
    private static final String TAG = "MainActivity";

    private static final int SELECT_PHOTO = 100;
    private ArrayList<String> mUriPath = new ArrayList<String>();
    private ArrayList<Map<String, Object>> mItems = new ArrayList<Map<String,Object>>();
    private GridView mGridView;
    private LoadStatus mLoadStatus;
    private int mInitBitmapCount = 0;
    private int mDoneBitmapCount = 0;
    private ProgressDialog mProgressDialog;
    private MicroFilmAdapter mAdapter;
    private boolean IsPhotoEdit = false;
    private MenuItem mMakeMovie;
    private View mAddItemView;

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
        
        mLoadStatus = new LoadStatus();
        
        //addItem();
        mAdapter = new MicroFilmAdapter(getApplicationContext());
        mGridView.setAdapter(mAdapter);
        
        Log.d(TAG, "onCreate done");
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.asus_micromovie_editmenu, menu);
        mMakeMovie = (MenuItem) menu.findItem(R.id.MicroMovie_makemovie_button);
        
        if(((MediaPool)getApplicationContext()).getCount() == 0) {
        	mMakeMovie.setEnabled(false);
        	mMakeMovie.getIcon().setAlpha(130);
        }
    	return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    		case R.id.MicroMovie_makemovie_button:
    			if(IsPhotoEdit) {
    				finish();
    			} else {
	    			Intent intent = new Intent();
			        intent.setClass(getApplicationContext(), MicroMovieActivity.class);
			        startActivity(intent);
    			}
		        return true;
	        default:
	        	return super.onOptionsItemSelected(item);
    	}    	
    }
    
    @Override
    public void onBackPressed() {
    	super.onBackPressed();
    }

    private void PhotoEdit() {
    	IsPhotoEdit = true;
    	setContentView(R.layout.asus_micromovie_edit);

    	MicroFilmAdapter mAdapter = new MicroFilmAdapter(getApplicationContext());
    	mGridView = (GridView)findViewById(R.id.asus_micromovie_editshow);
    	mGridView.setNumColumns(5);
    	mGridView.setAdapter(mAdapter);
    }
    
    private void PhotoSelect() {
    	setContentView(R.layout.activity_main);
    	
    	mGridView = (GridView)findViewById(R.id.asus_micromovie_startshow);
    	mGridView.setNumColumns(5);
    	
    	Button btn = (Button) findViewById(R.id.click_buttonA);
        btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onAddMedia();
			}
		});
    }
    
    private void setImage(Uri uri) {
    	if(mUriPath.contains(uri.getPath())) return;

    	MediaInfo mInfo = new MediaInfo(this);
		mInfo.setup(uri, mLoadStatus);

		mInitBitmapCount++;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) { 
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent); 

        switch(requestCode) { 
	        case SELECT_PHOTO:
	            if(resultCode == RESULT_OK){
	            	mProgressDialog = ProgressDialog.show(this, null, getResources().getString(R.string.loading));

	            	mInitBitmapCount = 0;
	            	mDoneBitmapCount = 0;
	            	mUriPath = ((MediaPool)getApplicationContext()).getUriPath();
	            	Log.e(TAG, "size:" + mUriPath.size());
	            	
	            	//Asus Gallery return is very special...
	            	ArrayList<String> imageList = imageReturnedIntent.getStringArrayListExtra("multi-select-picker");
		            if (imageList != null) {
		                for (String imageUriString : imageList) {
		                	setImage(Uri.parse(imageUriString));
		                }
		            } else {
		            	Uri SingleImage = imageReturnedIntent.getData();
		                if(SingleImage == null) {
		                	ClipData MultiImage = imageReturnedIntent.getClipData();
		                	for(int i=0; i<MultiImage.getItemCount(); i++) {
		                		setImage(MultiImage.getItemAt(i).getUri());	                		
		                	}
		                } else {
		                	setImage(imageReturnedIntent.getData());
		                }
		            }
	                
	                if(mInitBitmapCount == 0) mProgressDialog.dismiss();
	            }
        }
    }
    
    public void updateAdapter() {
        mAdapter.notifyDataSetChanged();
        mGridView.invalidateViews();
    }
    
    public void addItem() {
    	mAddItemView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.asus_micromovie_additem, null);
    	mAddItemView.setPadding(1, 1, 1, 1);
        
        
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int edge = metrics.widthPixels / 5;
        
        ImageView mImageView = (ImageView) mAddItemView.findViewById(R.id.micromovie_additem);
        mImageView.setImageResource(R.drawable.asus_albumpicker_add);
    }
    
    private class LoadStatus implements LoadControl {
	    public void DoneLoadBitmap(MediaInfo mInfo) {
	        mDoneBitmapCount++;
	        
	        if(mInfo.mIsInitial && mInfo.getImage() != null) {
	        	((MediaPool)getApplicationContext()).addInfo(mInfo);
	        	updateAdapter();
	        	
	        	if(((MediaPool)getApplicationContext()).getCount() > 0) {
	        		mMakeMovie.setEnabled(true);
	        		mMakeMovie.getIcon().setAlpha(255);
	        	}
	        }

	        if(mInitBitmapCount == mDoneBitmapCount) {
	            mProgressDialog.dismiss();
	        }
	    }
    }
    
    private void onAddMedia() {
    	Intent mIntent = new Intent(Intent.ACTION_GET_CONTENT);
    	mIntent.setType("image/*");
    	mIntent.addCategory(Intent.CATEGORY_OPENABLE);
        
        List<ResolveInfo> infoList = getPackageManager().queryIntentActivities(mIntent, 0);
        
        if (infoList.isEmpty()) return;
        
        List<Intent> packageIntents = new ArrayList<Intent>();
        
        for (ResolveInfo info : infoList) {
        	final String packageName = info.activityInfo.packageName;
        	Intent packageIntent;
        	Log.e(TAG, "String:" + packageName);
        	if (("com.android.documentsui".equals(packageName) || "com.google.android.apps.docs".equals(packageName)) &&
        			Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        		continue;
        	} else if("com.asus.gallery".equals(packageName)) {
        		packageIntent = new Intent("ASUS_MULTI_SELECT_PICKER");
        		packageIntent.setType("image/*");
        		packageIntent.setClassName("com.asus.gallery", "com.asus.gallery.app.EPhotoActivity");
        	} else {
        		packageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                packageIntent.setType("image/*");
                packageIntent.setPackage(packageName);
        	}

        	packageIntent.addCategory(Intent.CATEGORY_OPENABLE);
        	packageIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        	packageIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        	packageIntents.add(packageIntent);
        }
        
        if(packageIntents.size() <= 0) return;
        
        Intent chooserIntent = Intent.createChooser(packageIntents.remove(packageIntents.size()-1), null);
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, packageIntents.toArray(new Parcelable[packageIntents.size()-1]));
        startActivityForResult(chooserIntent, SELECT_PHOTO);
    }
}
