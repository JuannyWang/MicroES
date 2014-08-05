package com.s890510.microfilm.filter;

import com.s890510.microfilm.MicroMovieActivity;
import com.s890510.microfilm.R;

public class DefaultFilter extends Filter {

	public DefaultFilter(MicroMovieActivity activity) {
		super(activity);
	}

	@Override
	public void setSingleBitmapParams(int bProgram) {
	}

	@Override
	public void setMultipleBitmapParams(int bProgram) {
	}

	@Override
	public void setVideoParams(int vProgram) {
	}

	@Override
	public void drawBitmap() {
	}

	@Override
	public void drawVideo() {
	}

	@Override
	public String getBitmapSingleFragment() {
		return getShaderRaw(R.raw.bitmap_fragment_shader);
	}

	@Override
	public String getVideoFragment() {
		return getShaderRaw(R.raw.video_fragment_shader);
	}

	@Override
	public String getBitmapSingleVertex() {
		return getShaderRaw(R.raw.bitmap_vertex_shader);
	}

	@Override
	public String getVideoVertex() {
		return getShaderRaw(R.raw.video_vertex_shader);
	}

	@Override
	public String getBitmapMultiFragment() {
		return getShaderRaw(R.raw.bitmap_fragment_lattice_shader);
	}

	@Override
	public String getBitmapMultiVertex() {
		return getShaderRaw(R.raw.bitmap_vertex_lattice_shader);
	}
}
