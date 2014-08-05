package com.s890510.microfilm;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;
import android.opengl.GLES20;
import android.util.Log;

public class LoadTexture {
    private String TAG = "LoadTexture";
    private MicroFilmActivity mActivity;
    private int[] textureHandle = null;
    private int width;
    private int height;

    public LoadTexture(MicroFilmActivity activity) {
        mActivity = activity;
        width = mActivity.mVisioWidth;
        height = mActivity.mVisioHeight;
    }

    public boolean loadTexture(final Context context, final FileInfo Info, final boolean IsFake, int duration) {
        if(!Info.Path.isEmpty()) {

            Bitmap bitmap = null;

            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(Info.Path);
            Log.e(TAG, "INFO_TYPE_VITMAP Duration:" + duration);
            bitmap = mediaMetadataRetriever.getFrameAtTime(duration*1000); //unit in microsecond

            mediaMetadataRetriever.release();

            if(bitmap != null) {
                if(bitmap.getWidth() > width || bitmap.getHeight() > height)
                    bitmap = BitmapScale(bitmap, width, height);

                Log.e(TAG, "loadTexture, width:" + bitmap.getWidth() + ", height:" + bitmap.getHeight());
                Info.mBitmap = bitmap;
                return true;
            }
            else {
                Log.e(TAG, "bitmap is null");
            }
        }
        return false;
    }

    public void BindTexture(int target, int texture) {
        // Bind to the texture in OpenGL
        GLES20.glBindTexture(target, texture);

        // Set filtering
        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameterf(target, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

        GLES20.glTexParameteri(target, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(target, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
    }

    public int GenTexture(String Method, int Id) {
        if(textureHandle == null) {
            textureHandle = new int[8]; //0: unuse, 1:movie, 2-6:bitmap, 7:slogan
            for(int i=0; i<textureHandle.length; i++)
                textureHandle[i] = i;
        }

        if(Method == "Video" || Method == "Bitmap" || Method == "Special")
            return textureHandle[Id];

        return 0;
    }

    private Bitmap BitmapScale(Bitmap mBitmap, int width, int height) {
        int bwidth = mBitmap.getWidth();
        int bheight = mBitmap.getHeight();
        float scaleSrc = (float)bheight/bwidth;
        float scaleScreen = (float)height/width;
        float scale;

        if(scaleSrc > scaleScreen) scale = (float)height/bheight;
        else scale = (float)width/bwidth;

        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);

        mBitmap = Bitmap.createBitmap(mBitmap, 0, 0,bwidth, bheight, matrix, true);

        return mBitmap;
    }
}
