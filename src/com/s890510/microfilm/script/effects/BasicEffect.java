package com.s890510.microfilm.script.effects;

import java.util.ArrayList;

import com.s890510.microfilm.shader.Shader;

public class BasicEffect extends Effect {
    private static final String TAG = "BasicEffect";
    protected int mDuration = 5000;
    protected int mDrawType = 1;
    protected int mSleep = 5000;
    protected int mMask = 0;
    protected ArrayList<String> mString;
    protected String mShader = Shader.Default;
    protected int mTextCount = 0;
    protected float mTextureScaleX = 1.0f;
    protected float mTextureScaleY = 1.0f;
    protected boolean mTransition = false;
    protected boolean mBackGround = false;
    protected boolean mShowBackGround = false;
    protected int mFixBound = 0;
    protected float[] mColor = {1.0f, 1.0f, 1.0f, 1.0f};
    protected float mTextSize = 110;
    protected float[] mPos = {1.0f, 1.0f};
    protected int mConvertType = 0;
    protected int mConvertSize = 0;
    protected boolean mIsNoItem = false;
    protected boolean mIsInCount = true;
    protected int mStringType = 0;

    public BasicEffect() {

    }

    @Override
    public int getDuration() {
        return mDuration;
    }

    @Override
    public int getDuration(long elapse) {
        return mDuration;
    }

    @Override
    public int getEffectType() {
        return EFFECT_DEFAULT;
    }

    @Override
    public Effect getEffect(long elapse) {
        return this;
    }

    @Override
    public long getElapseTime(long elapse) {
        return elapse;
    }

    @Override
    public float[] getMVPMatrixByElapse(long elapse) {
        return null;
    }

    @Override
    public float[] getMultipleMVPMatrixByElapse(long elapse, int num) {
        return null;
    }

    @Override
    public float getProgressByElapse(long elapse){
        float progress = ((float)elapse/(float)mDuration);
        if(progress > 1 ) progress = 1;
        else if(progress < 0 ) progress = 0;
        return progress;
    }

    @Override
    public void setDuration(int duration) {
        mDuration = duration;
    }

    @Override
    public int getSleep() {
        return mSleep;
    }

    @Override
    public void setSleep(int sleep) {
        mSleep = sleep;
    }

    @Override
    public float getTextureWidthScaleRatio() {
        return mTextureScaleX;
    }

    @Override
    public float getTextureHightScaleRatio() {
        return mTextureScaleY;
    }

    @Override
    public void setTextureRatio(float w, float h) {
        mTextureScaleX = w;
        mTextureScaleY = h;
    }

    @Override
    public float getAlpha(long elapse) {
        return 1.0f;
    }

    @Override
    public boolean showBackground() {
        return mShowBackGround;
    }

    @Override
    public void setshowBackground(boolean show) {
        mShowBackGround = show;
    }

    @Override
    public String getShader() {
        return mShader;
    }

    @Override
    public void setShader(String shader) {
        mShader = shader;
    }

    @Override
    public ArrayList<String> getString() {
        return mString;
    }

    @Override
    public void setString(ArrayList<String> str) {
        mString = str;
    }

    @Override
    public int getMaskType(long elapse) {
        return mMask;
    }

    @Override
    public boolean getTransition(long elapse) {
        return mTransition;
    }

    @Override
    public void setTransition(boolean set) {
        mTransition = set;
    }

    @Override
    public void setCount(int count) {
        mTextCount = count;
    }

    @Override
    public int getCount(long elapse) {
        return mTextCount;
    }

    @Override
    public void setBGColor(float[] color) {
        mColor = color;
    }

    @Override
    public float[] getBGColor(long elapse) {
        return mColor;
    }

    @Override
    public void setTextSize(float size) {
        mTextSize = size;
    }

    @Override
    public float getTextSize(long elapse) {
        return mTextSize;
    }

    @Override
    public void setFixBound(int Fix) {
        mFixBound = Fix;
    }

    @Override
    public int getFixBound(long elapse) {
        return mFixBound;
    }

    @Override
    public void setRunPos(float mStart, float mEnd) {
        mPos[0] = mStart;
        mPos[1] = mEnd;
    }

    @Override
    public float[] getRunPos(long elapse) {
        return mPos;
    }

    @Override
    public void setConvertInfo(int mType, int mSize) {
        mConvertType = mType;
        mConvertSize = mSize;
    }

    @Override
    public int getConvertType() {
        return mConvertType;
    }

    @Override
    public int getConvertSize() {
        return mConvertSize;
    }

	@Override
	public void setIsNoItem(boolean set) {
		mIsNoItem = set;
	}

	@Override
	public boolean getIsNoItem() {
		return mIsNoItem;
	}

	@Override
	public void setStringType(int type) {
		mStringType = type;
	}

	@Override
	public int getStringType() {
		return mStringType;
	}

	@Override
	public void setIsInCount(boolean set) {
		mIsInCount = set;
	}

	@Override
	public boolean getIsInCount() {
		return mIsInCount;
	}

	@Override
	public float getScaleSize(long elapse) {
		return 1.0f;
	}
}