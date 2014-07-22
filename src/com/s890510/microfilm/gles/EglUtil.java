package com.s890510.microfilm.gles;

import android.opengl.EGL14;

public class EglUtil {
	/**
     * Checks for EGL errors.
     */
    public static void checkEglError(String msg) {
        int error;
        if ((error = EGL14.eglGetError()) != EGL14.EGL_SUCCESS) {
            throw new RuntimeException(msg + ": EGL error: 0x" + Integer.toHexString(error));
        }
    }
}
