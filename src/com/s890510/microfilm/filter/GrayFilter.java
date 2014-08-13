package com.s890510.microfilm.filter;

import com.s890510.microfilm.MicroMovieActivity;

public class GrayFilter extends DefaultFilter {

    public GrayFilter(MicroMovieActivity activity) {
        super(activity);
    }

    @Override
    public String getBitmapSingleFragment() {
        return "precision mediump float;										\n" + "uniform sampler2D Texture;										\n" + "uniform mat3 matrix;											\n"
                + "uniform float mAlpha;											\n" + "varying vec2 vTextureCoord;									\n" +

                "void main() {													\n" + "	 vec4 color = texture2D(Texture_1, vTextureCoord);			\n"
                + "    float y = dot(color, vec4(0.299, 0.587, 0.114, 0));		\n" + "    gl_FragColor = vec4(y, y, y, color.a);						\n"
                + "    gl_FragColor.a = mAlpha;									\n" + "}																\n";
    }

    @Override
    public String getVideoFragment() {
        return "#extension GL_OES_EGL_image_external : require					\n" + "precision mediump float;										\n"
                + "uniform samplerExternalOES exTexture;							\n" + "uniform float mAlpha;											\n" + "varying vec2 vTextureCoord;									\n"
                + "void main() {													\n" + "  vec4 color = texture2D(exTexture, vTextureCoord);			\n"
                + "  float y = dot(color, vec4(0.299, 0.587, 0.114, 0));			\n" + "  gl_FragColor = vec4(y, y, y, color.a);						\n"
                + "  gl_FragColor.a = mAlpha;										\n" + "}																\n";
    }
}
