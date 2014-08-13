package com.s890510.microfilm;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;

public class LoadTexture {
    private String             TAG           = "LoadTexture";
    private int[]              textureHandle = null;

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
