package com.s890510.microfilm.util;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

public class Util {
	public static long getAvailableSpace(){
    	//File path = Environment.getExternalStorageDirectory();
    	File path = Environment.getDataDirectory();
    	StatFs stat = new StatFs(path.getPath());
    	return stat.getAvailableBytes();
    }
}
