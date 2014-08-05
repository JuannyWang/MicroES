package com.s890510.microfilm.shader;

import com.s890510.microfilm.ElementInfo;
import com.s890510.microfilm.MicroMovieActivity;
import com.s890510.microfilm.ProcessGL;

public class SingleShader {
    private static final String TAG = "SingleShader";
    private MicroMovieActivity mActivity;

    private DefaultShader mDefaultShader;
    private CoverShader mCoverShader;
    private LatticeShader mLatticeShader;
    private MirrorShader mMirrorShader;
    private RotateShader mRotateShader;
    private LineShader mLineShader;
    private PhotoShader mPhotoShader;
    private ProcessGL mProcessGL;

    public SingleShader(MicroMovieActivity activity, ProcessGL processGL) {
        mActivity = activity;
        mProcessGL = processGL;
    }

    public void initSingleShader() {
        mDefaultShader = new DefaultShader(mActivity, mProcessGL);
        mCoverShader = new CoverShader(mActivity, mProcessGL);
        mLatticeShader = new LatticeShader(mActivity, mProcessGL);
        mMirrorShader = new MirrorShader(mActivity, mProcessGL);
        mRotateShader = new RotateShader(mActivity, mProcessGL);
        mLineShader = new LineShader(mActivity, mProcessGL);
        mPhotoShader = new PhotoShader(mActivity, mProcessGL);
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
