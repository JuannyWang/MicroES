package com.s890510.microfilm;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;

public class MusicManager {
    public final static int MUISC_ASUS_BEACH            = 0;
    public final static int MUISC_ASUS_CITY_STREET      = 1;
    public final static int MUISC_ASUS_KIDS             = 2;
    public final static int MUISC_ASUS_LIFE             = 3;
    public final static int MUISC_ASUS_MEMORY           = 4;
    public final static int MUISC_ASUS_ROMENTIC         = 5;
    public final static int MUISC_ASUS_VINTAGE          = 6;
    public final static int MUISC_ASUS_YOUNG            = 7;
    
    private final static String PkgName = "com.asus.ephotomusicprovider";
    
    public static boolean isMusicProviderAvailable(Context context){
		PackageManager pm= context.getPackageManager();
		try {
			pm.getPackageInfo(PkgName,PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			return false;
		}  
		return true;
    }
    
    public static int getResourceId(Context context, int musicId){
    	PackageManager pm = context.getPackageManager();
    	try {
    	    Resources resources = pm.getResourcesForApplication(PkgName);
    	    String name;
    	    switch(musicId){
	            case MUISC_ASUS_BEACH: {
	            	name = "asus_beach_new_20140110";
	            	break;
	            }
	            case MUISC_ASUS_CITY_STREET: {
	            	name = "asus_city_street";
	            	break;
	            }
	            case MUISC_ASUS_KIDS: {
	            	name = "asus_kids_new_20140113";
	            	break;
	            }
	            case MUISC_ASUS_LIFE: {
	            	name = "asus_life_new_20130113";
	            	break;
	            }
	            case MUISC_ASUS_MEMORY: {
	            	name = "asus_memory_new_20130113";
	            	break;
	            }
	            case MUISC_ASUS_ROMENTIC: {
	            	name = "asus_romentic_new_20140110";
	            	break;
	            }
	            case MUISC_ASUS_VINTAGE: {
	            	name = "asus_vintage_new_20140110";
	            	break;
	            }
	            case MUISC_ASUS_YOUNG: {
	            	name = "asus_young_30s_20131213";
	            	break;
	            }
	            default: {
	                throw new IllegalArgumentException(
	                        "Invalid audio track raw resource id");
	            }
    	    }
    	    return resources.getIdentifier(name, "raw", PkgName);
    	} catch (NameNotFoundException e) {
    	    e.printStackTrace();
    	}
    	return 0; // 0 is not a valid resource id
    }
    
    public static String getFileName(Context context, int musicId){
    	PackageManager pm = context.getPackageManager();
    	try {
    	    Resources resources = pm.getResourcesForApplication(PkgName);
    	    String filename;
    	    switch(musicId){
	            case MUISC_ASUS_BEACH: {
	            	filename = ".asus_beach_new_20140110.m4a";
	            	break;
	            }
	            case MUISC_ASUS_CITY_STREET: {
	            	filename = ".asus_city_street.m4a";
	            	break;
	            }
	            case MUISC_ASUS_KIDS: {
	            	filename = ".asus_kids_new_20140113.m4a";
	            	break;
	            }
	            case MUISC_ASUS_LIFE: {
	            	filename = ".asus_life_new_20130113.m4a";
	            	break;
	            }
	            case MUISC_ASUS_MEMORY: {
	            	filename = ".asus_memory_new_20130113.m4a";
	            	break;
	            }
	            case MUISC_ASUS_ROMENTIC: {
	            	filename = ".asus_romentic_new_20140110.m4a";
	            	break;
	            }
	            case MUISC_ASUS_VINTAGE: {
	            	filename = ".asus_vintage_new_20140110.m4a";
	            	break;
	            }
	            case MUISC_ASUS_YOUNG: {
	            	filename = ".asus_young_30s_20131213.m4a";
	            	break;
	            }
	            default: {
	                throw new IllegalArgumentException(
	                        "Invalid audio track raw resource id");
	            }
    	    }
    	    if(getResourceId(context, musicId) != 0) // check if the resource exists
    	    	return filename;
    	    else return null;
    	} catch (NameNotFoundException e) {
    	    e.printStackTrace();
    	}
    	return null;
    }
    
    public static Resources getResources(Context context){
    	PackageManager pm = context.getPackageManager();
    	try {
    	    Resources resources = pm.getResourcesForApplication(PkgName);
    	    return resources;
    	} catch (NameNotFoundException e) {
    	    e.printStackTrace();
    	}
    	return null;
    }
    
    public static String getPackageName(){
    	return PkgName;
    }
}
