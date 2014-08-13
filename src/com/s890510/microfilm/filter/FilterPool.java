package com.s890510.microfilm.filter;

public class FilterPool {
    public static String BackgroundVertexShader() {
        return "uniform mat4 uMVPMatrix;       \n" + "attribute vec4 aPosition;      \n" + "attribute vec4 aColor;			\n"
                + "varying vec4 vColor;			\n" + "void main() {                  \n" + "    vColor = aColor;           \n"
                + "    gl_Position = uMVPMatrix  \n" + "                * aPosition;  \n" + "}                              \n";
    }

    public static String BackgroundFragmentShader() {
        return "precision mediump float;       \n" + "varying vec4 vColor;           \n" + "void main() {                  \n"
                + "    gl_FragColor = vColor;     \n" + "}                              \n";
    }
}
