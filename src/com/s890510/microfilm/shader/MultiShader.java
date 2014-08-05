package com.s890510.microfilm.shader;

import java.util.ArrayList;

import com.s890510.microfilm.ElementInfo;
import com.s890510.microfilm.MicroFilmActivity;
import com.s890510.microfilm.draw.GLDraw;

public class MultiShader {
    private static final String TAG = "MultiShader";
    private MicroFilmActivity mActivity;
    private ArrayList<Integer> mTextureId = new ArrayList<Integer>();
    private ArrayList<ElementInfo> mElementInfo = new ArrayList<ElementInfo>();

    private LatticeShader mLatticeShader;
    private FadeShader mFadeShader;
    private ShuttersShader mShuttersShader;
    private GLDraw mGLDraw;

    public MultiShader(MicroFilmActivity activity, GLDraw gldraw) {
        mActivity = activity;
        mGLDraw = gldraw;
    }

    public void initMultipleShader() {
        mLatticeShader = new LatticeShader(mActivity, mGLDraw);
        mFadeShader = new FadeShader(mActivity, mGLDraw);
        mShuttersShader = new ShuttersShader(mActivity, mGLDraw);
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
