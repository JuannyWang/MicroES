package com.s890510.microfilm;


public class MusicManager {
    public final static int MUISC_ASUS_CARNIVAL         = 0;
    public final static int MUISC_ASUS_CITY             = 1;
    public final static int MUISC_ASUS_KIDS             = 2;
    public final static int MUISC_ASUS_LIFE             = 3;
    public final static int MUISC_ASUS_MEMORY           = 4;
    public final static int MUISC_ASUS_LOVER            = 5;
    public final static int MUISC_ASUS_COUNTRY          = 6;
    public final static int MUISC_ASUS_SPORTS           = 7;

    public static String getFilePath(int musicId){
	    switch(musicId){
            case MUISC_ASUS_CARNIVAL: {
            	return "themes/Carnival/asus_carnival.mfim";
            }
            case MUISC_ASUS_CITY: {
            	return "themes/City/asus_city.mfim";
            }
            case MUISC_ASUS_KIDS: {
            	return "themes/Kids/asus_kids.mfim";
            }
            case MUISC_ASUS_LIFE: {
            	return "themes/Life/asus_life.mfim";
            }
            case MUISC_ASUS_MEMORY: {
            	return "themes/Memory/asus_memory.mfim";
            }
            case MUISC_ASUS_LOVER: {
            	return "themes/Lover/asus_lover.mfim";
            }
            case MUISC_ASUS_COUNTRY: {
            	return "themes/Country/asus_country.mfim";
            }
            case MUISC_ASUS_SPORTS: {
            	return "themes/Sports/asus_sports.mfim";
            }
            default: {
                throw new IllegalArgumentException(
                        "Invalid audio track raw resource id");
            }
	    }
    }
    
    public static String getFileName(int musicId){
	    switch(musicId){
            case MUISC_ASUS_CARNIVAL: {
            	return ".asus_carnival.m4a";
            }
            case MUISC_ASUS_CITY: {
            	return ".asus_city.m4a";
            }
            case MUISC_ASUS_KIDS: {
            	return ".asus_kids.m4a";
            }
            case MUISC_ASUS_LIFE: {
            	return ".asus_life.m4a";
            }
            case MUISC_ASUS_MEMORY: {
            	return ".asus_memory.m4a";
            }
            case MUISC_ASUS_LOVER: {
            	return ".asus_lover.m4a";
            }
            case MUISC_ASUS_COUNTRY: {
            	return ".asus_country.m4a";
            }
            case MUISC_ASUS_SPORTS: {
            	return ".asus_sports.m4a";
            }
            default: {
                throw new IllegalArgumentException(
                        "Invalid audio track raw resource id");
            }
	    }
    }
}
