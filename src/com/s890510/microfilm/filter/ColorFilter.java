package com.s890510.microfilm.filter;

import android.opengl.GLES20;

import com.s890510.microfilm.MicroFilmActivity;
import com.s890510.microfilm.R;

public class ColorFilter extends DefaultFilter {
    private int mBxAPositionHandle;
    private int mByAPositionHandle;
    private int mBxBPositionHandle;
    private int mByBPositionHandle;
    
    private int mVxAPositionHandle;
    private int mVyAPositionHandle;
    private int mVxBPositionHandle;
    private int mVyBPositionHandle;

    private long StartTime = 0;
    private float TimaA = 3.5f;
    private float TimeB = -0.2f;

    public ColorFilter(MicroFilmActivity activity) {
        super(activity);
    }

    @Override
    public void setSingleBitmapParams(int bProgram) {
        mBxAPositionHandle = GLES20.glGetUniformLocation(bProgram, "xAPosition");
        mByAPositionHandle = GLES20.glGetUniformLocation(bProgram, "yAPosition");
        mBxBPositionHandle = GLES20.glGetUniformLocation(bProgram, "xBPosition");
        mByBPositionHandle = GLES20.glGetUniformLocation(bProgram, "yBPosition");
    }

    @Override
    public void setVideoParams(int vProgram) {
        mVxAPositionHandle = GLES20.glGetUniformLocation(vProgram, "xAPosition");
        mVyAPositionHandle = GLES20.glGetUniformLocation(vProgram, "yAPosition");
        mVxBPositionHandle = GLES20.glGetUniformLocation(vProgram, "xBPosition");
        mVyBPositionHandle = GLES20.glGetUniformLocation(vProgram, "yBPosition");
    }

    @Override
    public void drawBitmap() {
    	setTimeAB();
        GLES20.glUniform1f(mBxAPositionHandle, (float) (0.5 + Math.cos(TimaA)/2.0));
        GLES20.glUniform1f(mByAPositionHandle, (float) (0.5 + Math.sin(TimaA)/2.0));
        GLES20.glUniform1f(mBxBPositionHandle, (float) (0.5 + Math.cos(TimeB)/2.0));
        GLES20.glUniform1f(mByBPositionHandle, (float) (0.5 + Math.sin(TimeB)/2.0));
    }

    @Override
    public void drawVideo() {
    	setTimeAB();
        GLES20.glUniform1f(mVxAPositionHandle, (float) (0.5 + Math.cos(TimaA)/2.0));
        GLES20.glUniform1f(mVyAPositionHandle, (float) (0.5 + Math.sin(TimaA)/2.0));
        GLES20.glUniform1f(mVxBPositionHandle, (float) (0.5 + Math.cos(TimeB)/2.0));
        GLES20.glUniform1f(mVyBPositionHandle, (float) (0.5 + Math.sin(TimeB)/2.0));
    }

    @Override
    public String getBitmapSingleFragment() {
        return getShaderRaw(R.raw.bitmap_fragment_light_shader);
    }

    @Override
    public String getVideoFragment() {
        return getShaderRaw(R.raw.video_fragment_color_shader);
    }

    private void setTimeAB(){
        if((System.currentTimeMillis() - StartTime > 1000 && !mActivity.checkPause()) || (mActivity.isSaving() && (mTimer-StartTime > 1000000000))) {
            if(mActivity.isSaving())
            	StartTime = StartTime + 1000000000;
            else StartTime = System.currentTimeMillis();
            float temp = TimaA;
            TimaA = TimeB;
            TimeB = temp;
        }
    }



}
