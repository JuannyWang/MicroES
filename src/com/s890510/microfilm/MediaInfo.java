package com.s890510.microfilm;

import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

public class MediaInfo {
	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public static final int MEDIA_TYPE_UNKNOW = 3;

	private Activity mActivity;
	private Uri mUri;
	private int mType;
	private String mPath;
	private int mRotate = 0;
	private long mDate;
	private float[] mLatLong = new float[2];
	private boolean mHaveLatLong = false;
	private boolean mIsUTC = false;
	
	public MediaInfo(Activity activity) {
		mActivity = activity;
	}
	
	public void setup(Uri uri) {
		mUri = uri;
		mPath = getRealPath(uri);
		
		String[] mSplit = getMimeType(uri).split("/");
		if(mSplit[0].equals("image")) mType =  MEDIA_TYPE_IMAGE;
		else if(mSplit[0].equals("video")) mType = MEDIA_TYPE_VIDEO;
		else mType = MEDIA_TYPE_UNKNOW;

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
            
            
            mDate=getTime(uri, mExif.getAttribute(ExifInterface.TAG_DATETIME));
            
            if(mExif.getLatLong(mLatLong))
            	mHaveLatLong = true;
            
		} catch (IOException e) {
			e.printStackTrace();
		}   		
	}
	
	public boolean IsUTC() {
		return mIsUTC;
	}
	
	public boolean HaveLatLong() {
		return mHaveLatLong;
	}
	
	public long getDate() {
		return mDate;
	}
	
	public Uri getUri() {
		return mUri;
	}
	
	public String getPath() {
		return mPath;
	}
	
	public int getType() {
		return mType;
	}
	
	public int getRotate() {
		return mRotate;
	}
	
	private String getMimeType(Uri uri) {
        Cursor cursor = mActivity.getApplicationContext().getContentResolver().query(uri,
        		new String[] { MediaStore.MediaColumns.MIME_TYPE }, null, null, null);

        if (cursor != null && cursor.moveToNext()) {
            return cursor.getString(0);
        }

        return null;
    }
    
    private String getRealPath(Uri uri) {
        Cursor cursor = mActivity.getApplicationContext().getContentResolver().query(uri,
        		new String[] { MediaStore.Images.Media.DATA }, null, null, null);
        
        if (cursor != null && cursor.moveToNext()) {
            return cursor.getString(0);
        }
        
        return null;
    }

    private long getTime(Uri uri) {
    	Cursor cursor = mActivity.getApplicationContext().getContentResolver().query(uri,
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
