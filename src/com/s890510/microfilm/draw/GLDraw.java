package com.s890510.microfilm.draw;

public abstract class GLDraw {
	abstract public void setView(int width, int height);
	abstract public void setEye();
	abstract public void prepare();
	abstract public void draw();
}
