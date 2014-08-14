package com.s890510.microfilm.script;

import java.util.ArrayList;

public class ScriptUtil {
	int[] AnimationTime; //Animation duration time
	int SleepTime; //Next effect wait time
	boolean[] Count; //???
	String mShader; //Use which shader type
	ArrayList<String> str; //String
	boolean[] Trans; //Doing Animation?
	int[] mUtil; //Run Easing type
    float[] StartScale; //Start scale value
	float[] EndScale; //End scale value
	float[] StartAlpha; //Start alpha value
	float[] EndAlpha; //End a;pha value
	float WRatio; //Width ratio
	float HRatio; //Height ratio
	int[] mMask; //Using mask type
	int[] mType; //Using effect type 
	int size; //String size
	int sType; //String animation type
	int ConvertType; 
	int ConvertSize;
	float x;
	float y;
	boolean IsInCount;
	
	//Here we merge some position variable together
	float[] StartPos; //Include StartPos, ShowPos it's means the start animation position(no specified x or y) 
	float[] EndPos; //It's means the end animation position(no specified x or y) 
	float[] StartPosX; //Start of X position
	float[] EndPosX; //End of X position
	float[] StartPosY; //Start of Y position
	float[] EndPosY; //End of Y position
	float[] StartRotate; //Start of rotate angle
	float[] EndRotate; //End of rotate angle
	int[] mRType; //Rotation type (x or y or z)
	int[] Bound; 
	float mRed; //Filter red value
	float mGreen; //Filter green value
	float mBlue; //Filter blue value
}
