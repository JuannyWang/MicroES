package com.s890510.microfilm.filter;

import android.opengl.GLES20;

import com.s890510.microfilm.MicroMovieActivity;

public class SepiaFilter extends DefaultFilter {
    
    public SepiaFilter(MicroMovieActivity activity) {
		super(activity);
	}

	private float[] weights = { 805.0f / 2048.0f, 715.0f / 2048.0f, 557.0f / 2048.0f,
            1575.0f / 2048.0f, 1405.0f / 2048.0f, 1097.0f / 2048.0f,
            387.0f / 2048.0f, 344.0f / 2048.0f, 268.0f / 2048.0f };	
    
    private int mBEffectMatrixHandle;
    private int mVEffectMatrixHandle;
    

	@Override
	public String getBitmapSingleFragment() {
		return
				"precision mediump float;\n" +
				"uniform sampler2D Texture;\n" +
				"uniform mat3 matrix;\n" +
				"uniform float mAlpha;\n" +
				"varying vec2 vTextureCoord;\n" +

				"void main() {\n" +
				"	 vec4 color = texture2D(Texture, vTextureCoord);\n" +
	            "    vec3 new_color = min(matrix * color.rgb, 1.0);\n" +
	            "    gl_FragColor = vec4(new_color.rgb, color.a);\n" +
				"    gl_FragColor.a = mAlpha;\n" +
				"}\n";
	}

	@Override
	public String getVideoFragment() {
		return
				"#extension GL_OES_EGL_image_external : require\n" +
	            "precision mediump float;\n" +
	            "uniform samplerExternalOES exTexture;\n" +
	            "uniform mat3 matrix;\n" +
				"uniform float mAlpha;\n" +
	            "varying vec2 vTextureCoord;\n" +
	            "void main() {\n" +
	            "  vec4 color = texture2D(exTexture, vTextureCoord);\n" +
	            "  vec3 new_color = min(matrix * color.rgb, 1.0);\n" +
	            "  gl_FragColor = vec4(new_color.rgb, color.a);\n" +
				"  gl_FragColor.a = mAlpha;\n" +
	            "}\n";
	}

	@Override
	public void setSingleBitmapParams(int bProgram) {
		mBEffectMatrixHandle = GLES20.glGetUniformLocation(bProgram, "matrix");
	}

	@Override
	public void setVideoParams(int vProgram) {
		mVEffectMatrixHandle = GLES20.glGetUniformLocation(vProgram, "matrix");	
	}

	@Override
	public void drawBitmap() {
		GLES20.glUniformMatrix3fv(mBEffectMatrixHandle, 1, false, weights, 0);
	}

	@Override
	public void drawVideo() {
		GLES20.glUniformMatrix3fv(mVEffectMatrixHandle, 1, false, weights, 0);
	}
}
