package com.s890510.microfilm.shader;

import java.util.ArrayList;

import com.s890510.microfilm.ElementInfo;
import com.s890510.microfilm.MicroMovieActivity;
import com.s890510.microfilm.ProcessGL;

public class MultiShader {
    private static final String    TAG          = "MultiShader";
    private MicroMovieActivity     mActivity;
    private ArrayList<Integer>     mTextureId   = new ArrayList<Integer>();
    private ArrayList<ElementInfo> mElementInfo = new ArrayList<ElementInfo>();

    private LatticeShader          mLatticeShader;
    private FadeShader             mFadeShader;
    private ShuttersShader         mShuttersShader;
    private ProcessGL              mProcessGL;

    public MultiShader(MicroMovieActivity activity, ProcessGL processGL) {
        mActivity = activity;
        mProcessGL = processGL;
    }

    public void initMultipleShader() {
        mLatticeShader = new LatticeShader(mActivity, mProcessGL);
        mFadeShader = new FadeShader(mActivity, mProcessGL);
        mShuttersShader = new ShuttersShader(mActivity, mProcessGL);
    }

    public void SetContainer(int TId, ElementInfo Info) {
        if(mElementInfo.size() < 2) {
            mTextureId.add(TId);
            mElementInfo.add(Info);
        }
    }

    public void DrawRandar(int ShaderMode, float[] mViewMatrix, float[] mProjectionMatrix) {
        if(ShaderMode == Shader.FadeShader) {
            mFadeShader.DrawRandar(mViewMatrix, mProjectionMatrix, mTextureId, mElementInfo);
        } else if(ShaderMode == Shader.ShuttersShader) {
            mShuttersShader.DrawRandar(mViewMatrix, mProjectionMatrix, mTextureId, mElementInfo);
        }
    }

    public void clear() {
        reset();
        mTextureId.clear();
        mElementInfo.clear();
    }

    public void reset() {
        mLatticeShader.Reset();
        mFadeShader.Reset();
        mShuttersShader.Reset();
    }

    public int getSize() {
        return mElementInfo.size();
    }
}
