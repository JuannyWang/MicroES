package com.s890510.microfilm.mask;

import android.opengl.GLES20;
import android.util.Log;

import com.asus.gallery.micromovie.MicroMovieActivity;
import com.asus.gallery.micromovie.RawReader;

public abstract class Mask {
    private static final String TAG = "Mask";

    public static final int Square      = 11;
    public static final int Circle      = 12;
    public static final int Bar         = 13;
    public static final int Filter      = 14;

    //Mask Type
    public static final String SquareBorder     = "SquareBorder";

    //Mask Anim
    public static final int NONE            = 0;
    public static final int SHOWN           = 1;
    public static final int TRANS_IN        = 3;
    public static final int TRANS_IN_SMALL  = 4;
    public static final int TRANS_IN_BIG    = 5;
    public static final int TRANS_OUT       = 6;
    public static final int TRANS_OUT_BACK  = 7;
    public static final int TRANS_OUT_FULL  = 8;
    public static final int GONE            = 9;

    protected MicroMovieActivity mActivity;

    abstract public void Reset();

    public Mask(MicroMovieActivity activity){
        mActivity = activity;
    }

    public void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    protected String getShaderRaw(final int ResourcesId) {
        return RawReader.readTextFileFromRawResource(mActivity.getApplicationContext(), ResourcesId);
    }
}
