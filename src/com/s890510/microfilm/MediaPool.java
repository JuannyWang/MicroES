package com.s890510.microfilm;

import java.util.ArrayList;

import android.app.Application;

public class MediaPool extends Application {
	private static final String TAG = "MediaPool";
	private ArrayList<MediaInfo> mMediaInfo = new ArrayList<MediaInfo>();
	
	public ArrayList<MediaInfo> getMediaInfo() {
		return mMediaInfo;
	}
	
	public void setMediaInfo(ArrayList<MediaInfo> info) {
		mMediaInfo = info;
	}
}
