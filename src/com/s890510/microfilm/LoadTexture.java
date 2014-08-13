package com.s890510.microfilm;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;

public class LoadTexture {
<<<<<<< HEAD
    private String             TAG           = "LoadTexture";
    private int[]              textureHandle = null;
=======
    private String TAG = "LoadTexture";
    private MicroMovieActivity mActivity;
    private int[] textureHandle = null;
    private int width;
    private int height;

    public LoadTexture(MicroMovieActivity activity) {
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
>>>>>>> parent of 5342a45... Remove Fileinfo and adjust several thing

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
            textureHandle = new int[8]; // 0: unuse, 1-5:bitmap, 6:slogan
            for(int i = 0; i < textureHandle.length; i++)
                textureHandle[i] = i;
        }

        if(Method == "Video" || Method == "Bitmap" || Method == "Special")
            return textureHandle[Id];

        return 0;
    }
}
