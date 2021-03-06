package com.s890510.microfilm.mask;

import com.s890510.microfilm.ElementInfo;
import com.s890510.microfilm.MicroMovieActivity;
import com.s890510.microfilm.ProcessGL;

public class ShowMask {
    private final String TAG = "ShowMask";
    private MicroMovieActivity mActivity;

    private SquareBorderMask mSquareBorderMask;
    private CircleBorderMask mCircleBorderMask;
    private TopBottomBarMask mTopBottomBarMask;
    private FilterMask mFilterMask;
    private ProcessGL mProcessGL;

    public ShowMask(MicroMovieActivity activity, ProcessGL processGL) {
        mActivity = activity;
        mProcessGL = processGL;
    }

    public void initMask() {
        mSquareBorderMask = new SquareBorderMask(mActivity, mProcessGL);
        mCircleBorderMask = new CircleBorderMask(mActivity, mProcessGL);
        mTopBottomBarMask = new TopBottomBarMask(mActivity, mProcessGL);
        mFilterMask = new FilterMask(mActivity, mProcessGL);
    }

    public void CalcVertices() {
        mSquareBorderMask.CalcVertices();
        mCircleBorderMask.CalcVertices();
        mTopBottomBarMask.CalcVertices();
        mFilterMask.CalcVertices();
    }

    public void DrawRandar(int ShaderMode, float[] mModelMatrix, float[] mViewMatrix, float[] mProjectionMatrix,
            ElementInfo mElementInfo, int mTextureId, int mType) {
        if(ShaderMode == Mask.Square) {
            mSquareBorderMask.DrawRandar(mViewMatrix, mProjectionMatrix, mElementInfo);
        } else if(ShaderMode == Mask.Circle) {
            mCircleBorderMask.DrawRandar(mViewMatrix, mProjectionMatrix, mElementInfo, mTextureId);
        } else if(ShaderMode == Mask.Bar) {
            mTopBottomBarMask.DrawRandar(mViewMatrix, mProjectionMatrix, mElementInfo, mType);
        } else if(ShaderMode == Mask.Filter) {
            mFilterMask.DrawRandar(mModelMatrix, mViewMatrix, mProjectionMatrix, mElementInfo);
        }
    }

    public void reset(int ShaderMode) {

    }
}
