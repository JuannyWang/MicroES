package com.s890510.microfilm.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import com.s890510.microfilm.MusicManager;
import com.s890510.microfilm.R;

public class MD5Util {
	public static String fileToMD5(String filePath) {
		InputStream inputStream = null;
		try {
		    inputStream = new FileInputStream(filePath);
		    byte[] buffer = new byte[1024];
		    MessageDigest digest = MessageDigest.getInstance("MD5");
		    int numRead = 0;
		    while (numRead != -1) {
		        numRead = inputStream.read(buffer);
		        if (numRead > 0)
		            digest.update(buffer, 0, numRead);
		    }
		    byte [] md5Bytes = digest.digest();
		    return convertHashToString(md5Bytes);
		} catch (Exception e) {
		    return null;
		} finally {
		    if (inputStream != null) {
		        try {
		            inputStream.close();
		        } catch (Exception e) { }
		    }
		}
	}

	private static String convertHashToString(byte[] md5Bytes) {
		String returnVal = "";
		for (int i = 0; i < md5Bytes.length; i++) {
		    returnVal += Integer.toString(( md5Bytes[i] & 0xff ) + 0x100, 16).substring(1);
		}
		if(returnVal.equals(""))
			return null;
		else return returnVal;
	}
	
	public static String getCorrectMD5(int musicId){
		switch(musicId){
			case MusicManager.MUISC_ASUS_CARNIVAL:
				return "66dc7416ee0caafd2489c4888403b591";
			case MusicManager.MUISC_ASUS_CITY:
				return "545af57b3ba14f4343888fcf6da77e51";
			case MusicManager.MUISC_ASUS_KIDS:
				return "079705b3b418c44a32c4bc01cc3aa5bc";
			case MusicManager.MUISC_ASUS_LIFE:
				return "382efd8f3930f06181e7411ef70d41cc";
			case MusicManager.MUISC_ASUS_MEMORY:
				return "6b2a6db052d5c7db90742ebbaa7aedf7";
			case MusicManager.MUISC_ASUS_LOVER:
				return "51ea05ce252288ebad40ff3961ac1d3b";
			case MusicManager.MUISC_ASUS_COUNTRY:
				return "ed8cc34b1023a2d4dbbf83d1cd72c3c4";
			case MusicManager.MUISC_ASUS_SPORTS:
				return "76e4643df0d53cb6a8ee9ebf46885295";							
		}
		
		return null;
	}
}
