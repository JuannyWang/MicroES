package com.s890510.microfilm.shader;

import com.s890510.microfilm.ElementInfo;
import com.s890510.microfilm.MicroFilmActivity;
import com.s890510.microfilm.draw.GLDraw;

public class SingleShader {
    private static final String TAG = "SingleShader";
    private MicroFilmActivity mActivity;

    private DefaultShader mDefaultShader;
    private CoverShader mCoverShader;
    private LatticeShader mLatticeShader;
    private MirrorShader mMirrorShader;
    private RotateShader mRotateShader;
    private LineShader mLineShader;
    private PhotoShader mPhotoShader;
    private GLDraw mGLDraw;

    public SingleShader(MicroFilmActivity activity, GLDraw gldraw) {
        mActivity = activity;
        mGLDraw = gldraw;
    }

    public void initSingleShader() {
        mDefaultShader = new DefaultShader(mActivity, mGLDraw);
        mCoverShader = new CoverShader(mActivity, mGLDraw);
        mLatticeShader = new LatticeShader(mActivity, mGLDraw);
        mMirrorShader = new MirrorShader(mActivity, mGLDraw);
        mRotateShader = new RotateShader(mActivity, mGLDraw);
        mLineShader = new LineShader(mActivity, mGLDraw);
        mPhotoShader = new PhotoShader(mActivity, mGLDraw);
    }

    public void DrawRandar(int ShaderMode, int mTextureId, ElementInfo mElementInfo,
            float[] mModelMatrix, float[] mViewMatrix, float[] mProjectionMatrix, int mType) {
        if(ShaderMode == Shader.DefaultShader) {
            mDefaultShader.DrawRandar(mModelMatrix, mViewMatrix, mProjectionMatrix, mTextureId, mElementInfo, mType);
        } else if(ShaderMode == Shader.CoverShader) {
            mCoverShader.DrawRandar(mModelMatrix, mViewMatrix, mProjectionMatrix, mTextureId, mElementInfo, mType);
        } else if(ShaderMode == Shader.LatticeShader) {
            mLatticeShader.DrawRandar(mModelMatrix, mViewMatrix, mProjectionMatrix, mTextureId, mElementInfo, mType);
        } else if(ShaderMode == Shader.MirrorShader) {
            mMirrorShader.DrawRandar(mModelMatrix, mViewMatrix, mProjectionMatrix, mTextureId, mElementInfo, mType);
        } else if(ShaderMode == Shader.RotateShader) {
            mRotateShader.DrawRandar(mModelMatrix, mViewMatrix, mProjectionMatrix, mTextureId, mElementInfo);
        } else if(ShaderMode == Shader.LineShader) {
            mLineShader.DrawRandar(mModelMatrix, mViewMatrix, mProjectionMatrix, mTextureId, mElementInfo, mType);
        } else if(ShaderMode == Shader.PhotoShader) {
            mPhotoShader.DrawRandar(mModelMatrix, mViewMatrix, mProjectionMatrix, mTextureId, mElementInfo, mType);
        }

    }

    public void reset(int ShaderMode) {
        if(ShaderMode == Shader.DefaultShader) {
            //do nothing
        }
    }

    public void init() {
        mLineShader.init();
        mCoverShader.CalcVertices();
    }
}
