package com.s890510.microfilm.mask;

import com.s890510.microfilm.ElementInfo;
import com.s890510.microfilm.MicroFilmActivity;
import com.s890510.microfilm.draw.GLDraw;

public class ShowMask {
    private final String TAG = "ShowMask";
    private MicroFilmActivity mActivity;

    private SquareBorderMask mSquareBorderMask;
    private CircleBorderMask mCircleBorderMask;
    private TopBottomBarMask mTopBottomBarMask;
    private FilterMask mFilterMask;
    private GLDraw mGLDraw;

    public ShowMask(MicroFilmActivity activity, GLDraw gldraw) {
        mActivity = activity;
        mGLDraw = gldraw;
    }

    public void initMask() {
        mSquareBorderMask = new SquareBorderMask(mActivity, mGLDraw);
        mCircleBorderMask = new CircleBorderMask(mActivity, mGLDraw);
        mTopBottomBarMask = new TopBottomBarMask(mActivity, mGLDraw);
        mFilterMask = new FilterMask(mActivity, mGLDraw);
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
