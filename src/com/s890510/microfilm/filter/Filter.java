package com.s890510.microfilm.filter;

import com.s890510.microfilm.MicroFilmActivity;
import com.s890510.microfilm.draw.GLUtil;


public abstract class Filter {
	public static int TYPE_BITMAP = 0;
	public static int TYPE_EOS = 1;
	
	protected MicroFilmActivity mActivity;
	protected long mTimer;
	
	public Filter(MicroFilmActivity activity){
		mActivity = activity;
	}
	
	abstract public void setSingleBitmapParams(int bProgram);
	abstract public void setMultipleBitmapParams(int bProgram);
	abstract public void setVideoParams(int vProgram);
	abstract public void drawBitmap();
	abstract public void drawVideo();
	abstract public String getBitmapSingleFragment();
	abstract public String getBitmapMultiFragment();
	abstract public String getVideoFragment();
	abstract public String getBitmapSingleVertex();
	abstract public String getBitmapMultiVertex();
	abstract public String getVideoVertex();
	
    protected String getShaderRaw(final int ResourcesId) {
        return GLUtil.readTextFileFromRawResource(mActivity.getApplicationContext(), ResourcesId);
    }
    
    public void setTimer(long timer){
    	mTimer = timer;
    }
}
