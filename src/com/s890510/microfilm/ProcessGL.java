package com.s890510.microfilm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.SurfaceTexture;
import android.graphics.SurfaceTexture.OnFrameAvailableListener;
import android.media.MediaPlayer;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;
import android.view.Surface;

import com.s890510.microfilm.draw.GLUtil;
import com.s890510.microfilm.draw.Slogan;
import com.s890510.microfilm.draw.StringLoader;
import com.s890510.microfilm.filter.Filter;
import com.s890510.microfilm.filter.FilterChooser;
import com.s890510.microfilm.mask.Mask;
import com.s890510.microfilm.mask.ShowMask;
import com.s890510.microfilm.script.Script;
import com.s890510.microfilm.script.Script1;
import com.s890510.microfilm.script.Timer;
import com.s890510.microfilm.shader.BackgroundShader;
import com.s890510.microfilm.shader.Shader;
import com.s890510.microfilm.shader.SingleShader;

public class ProcessGL {
    private static final String  TAG                                 = "ProcessGL";

    // Video Encoder
    public static final int      FLOAT_SIZE_BYTES                    = 4;                         // float
                                                                                                   // =
                                                                                                   // 4bytes
    private static final int     TRIANGLE_VERTICES_DATA_STRIDE_BYTES = 5 * FLOAT_SIZE_BYTES;
    private static final int     TRIANGLE_VERTICES_DATA_POS_OFFSET   = 0;
    private static final int     TRIANGLE_VERTICES_DATA_UV_OFFSET    = 3;

    private float[]              mMVPMatrix                          = new float[16];             // the
                                                                                                   // texture
    private float[]              mModelMatrix                        = new float[16];
    private float[]              mViewMatrix                         = new float[16];
    private float[]              mProjectionMatrix                   = new float[16];

    private ArrayList<MediaInfo> mMediaList                          = new ArrayList<MediaInfo>();
    private ArrayList<Integer>   BitmapTexture                       = new ArrayList<Integer>();
    private ArrayList<int[]>     BitmapTextureConvert                = new ArrayList<int[]>();
    private ArrayList<int[]>     WaitBitmapTexture                   = new ArrayList<int[]>();

    private BackgroundShader     mBackground;
    private SingleShader         mSingleShader;
    private ShowMask             mShowMask;

    private int[]                mBitmapTextureID                    = new int[5];
    private int[]                mBitmapTextureCount                 = new int[5];
    private int                  VideoShowCounter                    = -1;
    private int                  mSpecialTextureID;
    public int                   mSpecialHash                        = 0;

    private int                  mTextureUpdateCount                 = 0;
    private Context              mContext;
    public StringLoader          mStringLoader;

    public int                   ScreenHeight;
    public int                   ScreenWidth;
    public float                 ScreenScale                         = 1.0f;
    public float                 ScreenRatio;
    private boolean              mBitmapinit                         = false;
    private boolean              mBitmapUpdate                       = false;
    private boolean              mSloganinit                         = false;

    public Script                mScript;
    private MicroMovieActivity   mActivity;
    private Timer                mTimer;
    private ElementInfo[]        mProcessData                        = new ElementInfo[5];
    private Filter               mFilter;

    private boolean              mShouldResetOpenGL                  = false;
    private int                  mTextureNum                         = 1;
    private int[]                mStartTime                          = { 0, 0, 0, 0, 0 };
    private boolean              mNeedAgain                          = false;
    private int                  mPreSleep                           = 0;

    private Slogan               mSlogan;

    private boolean              mIsEncode                           = false;
    private int[]                mRemainTime                         = { 0, 0, 0, 0, 0 };

    public ProcessGL(Context context, MicroMovieActivity activity, boolean isEncode) {
        mContext = context;
        mActivity = activity;
        mScript = new Script1(mActivity, this);
        mTimer = new Timer(mScript.getTotalDuration(), mActivity, this);
        mFilter = FilterChooser.getFilter(mActivity, mScript.getFilterId());
        mSingleShader = new SingleShader(mActivity, this);
        mShowMask = new ShowMask(mActivity, this);
        mStringLoader = new StringLoader(mActivity, this);
        mIsEncode = isEncode;
    }

    public void setMediaInfo(ArrayList<MediaInfo> MList) {
        mMediaList = MList;
    }

    public String getFirstLocation() {
        if(mMediaList.get(0).mGeoInfo != null) {
            if(mMediaList.get(0).mGeoInfo.getLocation() != null) {
                return mMediaList.get(0).mGeoInfo.getLocation().get(0);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public float[] getLeftFilter() {
        return mScript.getFilterLeft();
    }

    public float[] getRightFilter() {
        return mScript.getFilterRight();
    }

    public int getScriptFilter() {
        return mScript.getFilterNumber();
    }

    public void setScript(Script script) {
        mScript = script;

        mShouldResetOpenGL = true;
    }

    public void setSpecialHash(int hash) {
        mSpecialHash = hash;
    }

    public void clearProcessData() {
        synchronized(mProcessData) {
            for(int i = 0; i < mProcessData.length; i++) {
                mProcessData[i] = null;
                mBitmapTextureCount[i] = 0;
            }
            BitmapTexture.clear();
            WaitBitmapTexture.clear();
        }
    }

    public Script getScript() {
        return mScript;
    }

    public void setScreenScale(float scale, int width, int height) {
        ScreenScale = scale;
        ScreenHeight = height;
        ScreenWidth = width;
        mSlogan.setScreen(width, height);
        mSingleShader.init();
        mShowMask.CalcVertices();
        mStringLoader.StringVertex();
        mBackground.CalcVertices();
        Log.e(TAG, "ScreenScale:" + ScreenScale + ", ScreenHeight:" + ScreenHeight + ", ScreenWidth:" + ScreenWidth);
    }

    public void playprepare() {
        // we need to find which Texture is not in use
        for(int i = 0; i < WaitBitmapTexture.size(); i++) {
            int[] Id = WaitBitmapTexture.get(i);
            if(mMediaList.get(Id[1]).getType() == MediaInfo.MEDIA_TYPE_IMAGE) {
                Log.e(TAG, "[0]:" + Id[0] + ", [1]:" + Id[1] + ", type:" + mMediaList.get(Id[1]).getType());
                GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + mBitmapTextureID[Id[0]]);
                mActivity.mLoadTexture.BindTexture(GLES20.GL_TEXTURE_2D, mBitmapTextureID[Id[0]]);

                if(Id[2] != 0) {
                    Bitmap bmp = null;
                    try {
                        Paint mPaint = new Paint();
                        ColorMatrix cMatrix = new ColorMatrix();
                        bmp = Bitmap.createBitmap(mMediaList.get(Id[1]).getImage().getWidth(), mMediaList.get(Id[1]).getImage().getHeight(),
                                Bitmap.Config.ARGB_8888);
                        Canvas mCanvas = new Canvas(bmp);

                        if(Id[2] == 1) {// Saturation
                            cMatrix.setSaturation((float) (Id[3] / 100.0));
                        }
                        mPaint.setColorFilter(new ColorMatrixColorFilter(cMatrix));
                        mCanvas.drawBitmap(mMediaList.get(Id[1]).getImage(), 0, 0, mPaint);
                        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);

                        bmp.recycle();
                    } catch(Exception e) {
                        e.printStackTrace();

                        if(bmp != null) {
                            bmp.recycle();
                            bmp = null;
                        }

                        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mMediaList.get(Id[1]).getImage(), 0);
                    }
                } else {
                    GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mMediaList.get(Id[1]).getImage(), 0);
                }
            }
        }
        WaitBitmapTexture.clear();
        Log.e(TAG, "BitmapTexture:" + BitmapTexture.size());
    }

    private void setBitmapUpdateInfo(ElementInfo eInfo) {
        int[] Id = new int[4];

        // find which one is empty
        for(int i = 0; i < mBitmapTextureCount.length; i++) {
            if(mBitmapTextureCount[i] == 0) {
                eInfo.TextureId = i;
                Id[0] = i;
                Id[1] = eInfo.InfoId;
                Id[2] = eInfo.effect.getConvertType();
                Id[3] = eInfo.effect.getConvertSize();
                mBitmapTextureCount[i]++;
                break;
            }
        }
        WaitBitmapTexture.add(Id);

        int[] convertData = { Id[2], Id[3] };
        if(BitmapTexture.size() - 1 >= Id[0]) {
            BitmapTexture.set(Id[0], Id[1]);
            BitmapTextureConvert.set(Id[0], convertData);
        } else {
            BitmapTexture.add(Id[1]);
            BitmapTextureConvert.add(convertData);
        }

        mBitmapUpdate = true;
    }

    public void changeBitmap(ElementInfo eInfo, boolean resetTimer) {
        if(resetTimer && !mActivity.checkPause())
            eInfo.timer.resetTimer();

        synchronized(mProcessData) {
            // mProcessData[0] => will be remove
            if(mProcessData[0] != null && mProcessData[0].InfoId > -1) {
                mBitmapTextureCount[mProcessData[0].TextureId]--;
            }

            if(eInfo.InfoId > -1) {
                // check BitmapTexture contain the eInfo.InfoId
                if(!BitmapTexture.contains(eInfo.InfoId)) {
                    // if not we prepare to add it
                    setBitmapUpdateInfo(eInfo);
                } else {
                    // if yes we reuse it
                    int index = BitmapTexture.indexOf(eInfo.InfoId);

                    if(BitmapTextureConvert.get(index)[0] == eInfo.effect.getConvertType()
                            && BitmapTextureConvert.get(index)[1] == eInfo.effect.getConvertSize()) {
                        mBitmapTextureCount[index]++;
                        eInfo.TextureId = index;
                    } else {
                        setBitmapUpdateInfo(eInfo);
                    }
                }
            } else {
                if(!mBitmapUpdate && !mBitmapinit)
                    mBitmapinit = true;
            }

            for(int i = 0; i < mProcessData.length; i++) {
                if(i + 1 < mProcessData.length)
                    mProcessData[i] = mProcessData[i + 1];
                else
                    mProcessData[i] = eInfo;
            }
        }
    }

    private float[] CalcXY(int width, int height) {
        float[] XY = new float[2]; // 0 => x, 1 => y

        if(width == height) { // width == height so we just need to change x
            if(height > ScreenHeight) {
                XY[1] = 1 / ((float) ScreenHeight / height);
                XY[0] = 1.0f;
            } else {
                XY[1] = 1 / ScreenScale;
                XY[0] = 1.0f;
            }
        } else {
            if(height > ScreenHeight) {
                if(width > ScreenWidth) {
                    XY[0] = 1.0f;
                    XY[1] = 1 / ((float) ScreenHeight / height);
                } else {
                    XY[0] = 1.0f;
                    XY[1] = 1 / ((float) ((float) width / ScreenWidth) * ((float) ScreenHeight / height));
                }
            } else if(width > ScreenWidth) {
                XY[0] = 1 / ((float) ((float) height / ScreenHeight) * ((float) ScreenWidth / width));
                XY[1] = 1.0f;
            } else {
                XY[0] = (float) width / ScreenWidth;
                XY[1] = (float) height / ScreenHeight;
                if(XY[0] > XY[1]) {
                    XY[0] = 1 / XY[1] * XY[0];
                    XY[1] = 1.0f;
                } else {
                    XY[1] = 1 / XY[0] * XY[1];
                    XY[0] = 1.0f;
                }
            }
        }

        return XY;
    }

    public float[] TVD(int width, int height) {
        float[] XY = CalcXY(width, height);

        float[] mTriangleVerticesData = new float[] {
                // X, Y, Z, U, V
                -XY[0], -XY[1], 1.0f, 0.0f, 0.0f, // A - bottom left
                XY[0], -XY[1], 0.0f, 1.0f, 0.0f, // B - bottom right
                -XY[0], XY[1], 1.0f, 0.0f, 1.0f, // C - top left
                XY[0], XY[1], 0.0f, 1.0f, 1.0f, // D - top right
        };

        return mTriangleVerticesData;
    }

    public void checkGlError(String op) {
        int error;
        while((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            // throw new RuntimeException(op + ": glError " + error);
        }
    }

    private void BitmapProgram() {
        mSingleShader.initSingleShader();
        mShowMask.initMask();
    }

    public void prepareOpenGL() {
        mBackground = new BackgroundShader(mActivity, this);
        mSlogan = new Slogan(mActivity, this);

        BitmapProgram();

        checkGlError("glProgram");

        SetTextureID();
    }

    public void SetTextureID() {
        // For Bitmap
        for(int i = 0; i < mBitmapTextureID.length; i++)
            mBitmapTextureID[i] = mActivity.mLoadTexture.GenTexture("Bitmap", i + 1);

        for(int i = 0; i < mBitmapTextureCount.length; i++)
            mBitmapTextureCount[i] = 0;

        // For SpecialTexture
        mSpecialTextureID = mActivity.mLoadTexture.GenTexture("Special", 6);

        for(int i = 0; i < mBitmapTextureID.length; i++)
            Log.e(TAG, "mBitmapTextureID[" + i + "]:" + mBitmapTextureID[i]);
    }

    public void changeSlogan() {
        mBitmapinit = false;
        mBitmapUpdate = false;
        mSloganinit = true;
    }

    private void updateTexture(int type) {
        if(type == MediaInfo.MEDIA_TYPE_IMAGE) {
            mBitmapinit = true;
            mBitmapUpdate = false;
            mSloganinit = false;
        }
    }

    public void doDrawNothing() {
        GLES20.glClearColor(0f, 0f, 0f, 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        checkGlError("glClear");
    }

    public void doDraw(long elapseTime) {
        if(WaitBitmapTexture.size() > 0) {
            playprepare();
        }

        GLES20.glClearColor(mScript.ColorRed(), mScript.ColorGreen(), mScript.ColorBlue(), 1.0f);
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_STENCIL_BUFFER_BIT);

        checkGlError("glClear");

        synchronized(mProcessData) {
            try {
                if(mBitmapinit && mProcessData[mProcessData.length - 1].effect.showBackground()) {
                    mBackground.DrawRandar(mProcessData[mProcessData.length - 1], mViewMatrix, mProjectionMatrix);
                }
            } catch(Exception e) {
                // Do nothing
            }
        }

        if(mBitmapUpdate) {
            updateTexture(MediaInfo.MEDIA_TYPE_IMAGE);
        }

        synchronized(mProcessData) {
            if(mBitmapinit && !mSloganinit) {
                if(mModelMatrix != null) {
                    Matrix.setIdentityM(mModelMatrix, 0);
                    drawSingleBitmap(elapseTime, false);
                }
            }
        }

        if(mShouldResetOpenGL) {
            mShouldResetOpenGL = false;
            mFilter = FilterChooser.getFilter(mActivity, mScript.getFilterId());
        }

        GLES20.glFinish();
    }

    public void setSleepZero(int duration) { // for encode
        for(int i = 0; i < mRemainTime.length - 1; i++) {
            mRemainTime[i] = mRemainTime[i + 1];
        }
        mRemainTime[mRemainTime.length - 1] = duration;

        for(int j = 0; j < mStartTime.length - 1; j++) {
            mStartTime[j] = mStartTime[j + 1];
        }
        mStartTime[mStartTime.length - 1] = -1; // set -1 to specify this is a
                                                // special case
    }

    private void drawSingleBitmap(long elapseTime, boolean isSecondTime) { // bitmap
        ElementInfo mInfo = null;

        for(int i = 0; i < mBitmapTextureID.length; i++) {
            if(mModelMatrix != null)
                Matrix.setIdentityM(mModelMatrix, 0);
            if(elapseTime >= 0) { // for making output file
                if(mProcessData[i] == null)
                    continue;
                mInfo = mProcessData[i];

                if(i >= mBitmapTextureID.length - mTextureNum) {

                } else {
                    continue;
                }
            } else { // for preview
                if(mProcessData[i] == null)
                    continue;
                if(!mProcessData[i].timer.isAlive())
                    continue;
                mInfo = mProcessData[i];
            }

            if(elapseTime >= 0) { // for making output file
                // mIsEncode = true;
                if(i == (mBitmapTextureID.length - 1)) {

                    if(elapseTime == 0 && !isSecondTime) {
                        int sleep = mInfo.effect.getSleep();
                        int duration = mInfo.effect.getDuration();

                        // Set remain time array. Store duration to check how
                        // many texture should be drawn
                        for(int j = 0; j < mRemainTime.length - 1; j++) {
                            if(mStartTime[j + 1] == -1)
                                mRemainTime[j] = mRemainTime[j + 1];
                            else
                                mRemainTime[j] = Math.max(0, mRemainTime[j + 1] - mPreSleep);
                        }
                        mRemainTime[mRemainTime.length - 1] = duration;

                        // Store start time of each texture according to
                        // previous sleep time
                        for(int j = 0; j < mStartTime.length - 1; j++) {
                            if(mStartTime[j + 1] == -1)
                                mStartTime[j] = 0;
                            else
                                mStartTime[j] = mStartTime[j + 1] + mPreSleep;
                        }
                        mStartTime[mStartTime.length - 1] = 0;

                        // set texture number
                        for(int j = 0; j < mRemainTime.length; j++) {
                            if(mRemainTime[j] > 0) {
                                mTextureNum = mRemainTime.length - j;
                                break;
                            }
                        }

                        if(mTextureNum > 1)
                            mNeedAgain = true;

                        mPreSleep = sleep;
                    }

                    if(!mNeedAgain) {

                        // if sleep > duration, texture4 should draw duration
                        // time instead of duration time
                        if(elapseTime > Math.min(mRemainTime[i], mPreSleep))
                            continue;

                        mInfo.timer.setElapseForEncode(elapseTime);
                        mModelMatrix = mInfo.effect.getMVPMatrixByElapse(elapseTime);
                    } else {
                        // Log.d(TAG,"drawSingleBitmap break 1");
                        break;
                    }

                } else if(i <= (mBitmapTextureID.length - 2)) {
                    if(elapseTime == 0 && !isSecondTime) {
                        // Log.d(TAG,"drawSingleBitmap break 2");
                        continue;
                    } else {
                        if(elapseTime > Math.min(mPreSleep, mRemainTime[i]) || mRemainTime[i] == 0) {
                            // Log.d(TAG,"drawSingleBitmap break 3");
                            continue;
                        } else {
                            mInfo.timer.setElapseForEncode(mStartTime[i] + elapseTime);
                            mModelMatrix = mInfo.effect.getMVPMatrixByElapse(mStartTime[i] + elapseTime);
                        }
                    }
                }
            } else { // for preview
                // mIsEncode = false;
                mModelMatrix = mInfo.effect.getMVPMatrixByElapse(mInfo.timer.getElapse());
            }

            if(mInfo.TextureId == -1 && mInfo.InfoId > -1) {
                continue;
            }

            if(mModelMatrix == null) {
                continue;
            }

            if(mInfo.effect.getShader().equals(Shader.Default)) {
                mSingleShader.DrawRandar(Shader.DefaultShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix,
                        mProjectionMatrix, Shader.NONE);
            } else if(mInfo.effect.getShader().equals(Shader.Default_White)) {
                mShowMask.DrawRandar(Mask.Filter, mModelMatrix, mViewMatrix, mProjectionMatrix, mInfo, mSpecialTextureID, Mask.NONE);
                mSingleShader.DrawRandar(Shader.DefaultShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix,
                        mProjectionMatrix, Shader.NONE);
            } else if(mInfo.effect.getShader().equals(Shader.String)) {
                if(mStringLoader.BindTexture(mSpecialTextureID, mInfo)) {
                    mSingleShader.DrawRandar(Shader.DefaultShader, mSpecialTextureID, mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                            Shader.STRING);
                }
            } else if(mInfo.effect.getShader().equals(Shader.String_Line)) {
                mStringLoader.BindTexture(mSpecialTextureID, mInfo);
                mSingleShader.DrawRandar(Shader.LineShader, mSpecialTextureID, mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix, Shader.STRING);
            } else if(mInfo.effect.getShader().equals(Shader.Scale_Mask)) {
                mSingleShader.DrawRandar(Shader.DefaultShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix,
                        mProjectionMatrix, Shader.NONE);
                mShowMask.DrawRandar(Mask.Square, mModelMatrix, mViewMatrix, mProjectionMatrix, mInfo, mSpecialTextureID, Mask.NONE);
            } else if(mInfo.effect.getShader().equals(Shader.Cover_Left)) {
                mSingleShader.DrawRandar(Shader.CoverShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.LEFT);
            } else if(mInfo.effect.getShader().equals(Shader.Cover_Right)) {
                mSingleShader.DrawRandar(Shader.CoverShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.RIGHT);
            } else if(mInfo.effect.getShader().equals(Shader.Cover_Top)) {
                mSingleShader.DrawRandar(Shader.CoverShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.TOP);
            } else if(mInfo.effect.getShader().equals(Shader.Cover_Bottom)) {
                mSingleShader.DrawRandar(Shader.CoverShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.BOTTOM);
            } else if(mInfo.effect.getShader().equals(Shader.Cover_Half_Left)) {
                mSingleShader.DrawRandar(Shader.CoverShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.HALF_LEFT);
            } else if(mInfo.effect.getShader().equals(Shader.Cover_Half_Left_Q)) {
                mSingleShader.DrawRandar(Shader.CoverShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.HALF_LEFT_Q);
            } else if(mInfo.effect.getShader().equals(Shader.Cover_Half_Right)) {
                mSingleShader.DrawRandar(Shader.CoverShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.HALF_RIGHT);
            } else if(mInfo.effect.getShader().equals(Shader.Cover_Half_Top)) {
                mSingleShader.DrawRandar(Shader.CoverShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.HALF_TOP);
            } else if(mInfo.effect.getShader().equals(Shader.Cover_Half_Bottom)) {
                mSingleShader.DrawRandar(Shader.CoverShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.HALF_BOTTOM);
            } else if(mInfo.effect.getShader().equals(Shader.Cover_Gfrag_Left)) {
                mSingleShader.DrawRandar(Shader.CoverShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.GFRAG_LEFT);
            } else if(mInfo.effect.getShader().equals(Shader.Cover_Empty_Left)) {
                mSingleShader.DrawRandar(Shader.CoverShader, mSpecialTextureID, mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.EMPTY_LEFT);
            } else if(mInfo.effect.getShader().equals(Shader.Cover_String_Left)) {
                mStringLoader.BindTexture(mSpecialTextureID, mInfo);
                mSingleShader.DrawRandar(Shader.CoverShader, mSpecialTextureID, mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.STRING_LEFT);
            } else if(mInfo.effect.getShader().equals(Shader.Cover_Center_H)) {
                mSingleShader.DrawRandar(Shader.CoverShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.CENTER_H);
            } else if(mInfo.effect.getShader().equals(Shader.Cover_Percent_L)) {
                mSingleShader.DrawRandar(Shader.CoverShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.PERCENT_L);
            } else if(mInfo.effect.getShader().equals(Shader.Cover_Percent_L_Half)) {
                mSingleShader.DrawRandar(Shader.CoverShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.PERCENT_L_HALF);
            } else if(mInfo.effect.getShader().equals(Shader.Cover_Percent_R)) {
                mSingleShader.DrawRandar(Shader.CoverShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.PERCENT_R);
            } else if(mInfo.effect.getShader().equals(Shader.Cover_Percent_B)) {
                mSingleShader.DrawRandar(Shader.CoverShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.PERCENT_B);
            } else if(mInfo.effect.getShader().equals(Shader.Scale_Fade)) {
                mSingleShader.DrawRandar(Shader.DefaultShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix,
                        mProjectionMatrix, Shader.NONE);
            } else if(mInfo.effect.getShader().equals(Shader.Scale_Fade_Bar_TRANS_IN)) {
                mSingleShader.DrawRandar(Shader.DefaultShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix,
                        mProjectionMatrix, Shader.NONE);
                mShowMask.DrawRandar(Mask.Bar, mModelMatrix, mViewMatrix, mProjectionMatrix, mInfo, mSpecialTextureID, Mask.TRANS_IN);
            } else if(mInfo.effect.getShader().equals(Shader.Scale_Fade_Bar_SHOWN)) {
                mSingleShader.DrawRandar(Shader.DefaultShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix,
                        mProjectionMatrix, Shader.NONE);
                mShowMask.DrawRandar(Mask.Bar, mModelMatrix, mViewMatrix, mProjectionMatrix, mInfo, mSpecialTextureID, Mask.SHOWN);
            } else if(mInfo.effect.getShader().equals(Shader.Lattice_Vertical)) {
                mSingleShader.DrawRandar(Shader.LatticeShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix,
                        mProjectionMatrix, Shader.LATTICE_VERTICAL);
            } else if(mInfo.effect.getShader().equals(Shader.Lattice_Horizontal)) {
                mSingleShader.DrawRandar(Shader.LatticeShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix,
                        mProjectionMatrix, Shader.LATTICE_HORIZONTAL);
            } else if(mInfo.effect.getShader().equals(Shader.Lattice_Tilted_Left)) {
                mSingleShader.DrawRandar(Shader.LatticeShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix,
                        mProjectionMatrix, Shader.LATTICE_TILTED_LEFT);
            } else if(mInfo.effect.getShader().equals(Shader.Lattice_Tilted_Left_T)) {
                mSingleShader.DrawRandar(Shader.LatticeShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix,
                        mProjectionMatrix, Shader.LATTICE_TILTED_LEFT_T);
            } else if(mInfo.effect.getShader().equals(Shader.Lattice_Tilted_Right)) {
                mSingleShader.DrawRandar(Shader.LatticeShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix,
                        mProjectionMatrix, Shader.LATTICE_TILTED_RIGHT);
            } else if(mInfo.effect.getShader().equals(Shader.Lattice_Tilted_Right_R)) {
                mSingleShader.DrawRandar(Shader.LatticeShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix,
                        mProjectionMatrix, Shader.LATTICE_TILTED_RIGHT_R);
            } else if(mInfo.effect.getShader().equals(Shader.Lattice_Cross_2)) {
                mSingleShader.DrawRandar(Shader.LatticeShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix,
                        mProjectionMatrix, Shader.LATTICE_CROSS_2);
            } else if(mInfo.effect.getShader().equals(Shader.Lattice_Cross_4)) {
                mSingleShader.DrawRandar(Shader.LatticeShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix,
                        mProjectionMatrix, Shader.LATTICE_CROSS_4);
            } else if(mInfo.effect.getShader().equals(Shader.Lattice_Blue_Bar)) {
                mSingleShader.DrawRandar(Shader.LatticeShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix,
                        mProjectionMatrix, Shader.LATTICE_BLUE_BAR);
            } else if(mInfo.effect.getShader().equals(Shader.Lattice_Blue_Bar_String)) {
                if(mStringLoader.BindTexture(mSpecialTextureID, mInfo)) {
                    mSingleShader.DrawRandar(Shader.LatticeShader, mSpecialTextureID, mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                            Shader.LATTICE_BLUE_BAR_GONE_STRING);
                }
            } else if(mInfo.effect.getShader().equals(Shader.Lattice_Blue_Bar_Mask)) {
                mSingleShader.DrawRandar(Shader.LatticeShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix,
                        mProjectionMatrix, Shader.LATTICE_BLUE_BAR);
                mShowMask.DrawRandar(Mask.Circle, mModelMatrix, mViewMatrix, mProjectionMatrix, mInfo, mSpecialTextureID, Mask.NONE);
            } else if(mInfo.effect.getShader().equals(Shader.Mirror_Vertical)) {
                mSingleShader.DrawRandar(Shader.MirrorShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.MIRROR_VERTICAL);
            } else if(mInfo.effect.getShader().equals(Shader.Mirror_Vertical_TB)) {
                mSingleShader.DrawRandar(Shader.MirrorShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.MIRROR_VERTICAL_TB);
            } else if(mInfo.effect.getShader().equals(Shader.Mirror_Tilted_Left)) {
                mSingleShader.DrawRandar(Shader.MirrorShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.MIRROR_TILTED_LEFT);
            } else if(mInfo.effect.getShader().equals(Shader.Mirror_Tilted_Mask)) {
                mSingleShader.DrawRandar(Shader.MirrorShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.MIRROR_TILTED_MASK);
                mShowMask.DrawRandar(Mask.Square, mModelMatrix, mViewMatrix, mProjectionMatrix, mInfo, mSpecialTextureID, Mask.NONE);
            } else if(mInfo.effect.getShader().equals(Shader.Mirror_Tilted)) {
                if(mInfo.effect.getTransition(mInfo.timer.getElapse())) {
                    mSingleShader.DrawRandar(Shader.MirrorShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix,
                            mProjectionMatrix, Shader.MIRROR_TILTED);
                } else {
                    mSingleShader.DrawRandar(Shader.DefaultShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix,
                            mProjectionMatrix, Shader.NONE);
                }
            } else if(mInfo.effect.getShader().equals(Shader.Circle_Mask)) {
                mSingleShader.DrawRandar(Shader.DefaultShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix,
                        mProjectionMatrix, Shader.NONE);
                mShowMask.DrawRandar(Mask.Circle, mModelMatrix, mViewMatrix, mProjectionMatrix, mInfo, mSpecialTextureID, Mask.NONE);
            } else if(mInfo.effect.getShader().equals(Shader.Circle_Mask_Cover)) {
                mSingleShader.DrawRandar(Shader.CoverShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.HALF_RIGHT_Q);
                mShowMask.DrawRandar(Mask.Circle, mModelMatrix, mViewMatrix, mProjectionMatrix, mInfo, mSpecialTextureID, Mask.NONE);
            } else if(mInfo.effect.getShader().equals(Shader.Rotate)) {
                mSingleShader.DrawRandar(Shader.RotateShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.NONE);
            } else if(mInfo.effect.getShader().equals(Shader.Rotate_Mask)) {
                mSingleShader.DrawRandar(Shader.RotateShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.NONE);
                mShowMask.DrawRandar(Mask.Circle, mModelMatrix, mViewMatrix, mProjectionMatrix, mInfo, mSpecialTextureID, Mask.NONE);
            } else if(mInfo.effect.getShader().equals(Shader.CMask)) {
                mShowMask.DrawRandar(Mask.Circle, mModelMatrix, mViewMatrix, mProjectionMatrix, mInfo, mSpecialTextureID, Mask.NONE);
            } else if(mInfo.effect.getShader().equals(Shader.Line)) {
                mSingleShader.DrawRandar(Shader.LineShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.NONE);
            } else if(mInfo.effect.getShader().equals(Shader.Filter)) {
                mShowMask.DrawRandar(Mask.Filter, mModelMatrix, mViewMatrix, mProjectionMatrix, mInfo, mSpecialTextureID, Mask.NONE);
            } else if(mInfo.effect.getShader().equals(Shader.Photo)) {
                mSingleShader.DrawRandar(Shader.PhotoShader, mBitmapTextureID[mInfo.TextureId], mInfo, mModelMatrix, mViewMatrix, mProjectionMatrix,
                        Shader.NONE);
            } else if(mInfo.effect.getShader().equals(Shader.Slogan_TypeA)) {
                mSlogan.DrawRandar(mViewMatrix, mProjectionMatrix, mSpecialTextureID, mScript, mInfo);
            } else if(mInfo.effect.getShader().equals(Shader.Slogan_TypeB)) {
                mSlogan.DrawRandar(mViewMatrix, mProjectionMatrix, mSpecialTextureID, mScript, mInfo);
            } else if(mInfo.effect.getShader().equals(Shader.Slogan_TypeC)) {
                mSlogan.DrawRandar(mViewMatrix, mProjectionMatrix, mSpecialTextureID, mScript, mInfo);
            } else if(mInfo.effect.getShader().equals(Shader.Slogan_TypeD)) {
                mSlogan.DrawRandar(mViewMatrix, mProjectionMatrix, mSpecialTextureID, mScript, mInfo);
            } else if(mInfo.effect.getShader().equals(Shader.Slogan_TypeE)) {
                mSlogan.DrawRandar(mViewMatrix, mProjectionMatrix, mSpecialTextureID, mScript, mInfo);
            } else if(mInfo.effect.getShader().equals(Shader.Slogan_TypeF)) {
                mSlogan.DrawRandar(mViewMatrix, mProjectionMatrix, mSpecialTextureID, mScript, mInfo);
            }

            mInfo = null;
            GLES20.glFlush();
        }

        checkGlError("drawSingleBitmap");

        if(mNeedAgain) {
            mNeedAgain = false;
            drawSingleBitmap(elapseTime, true);
        }
    }

    public boolean isEncode() {
        return mIsEncode;
    }

    public long getElapseTime() {
        return mTimer.getElapse();
    }

    public void setTimerElapse(long elpase) {
        mTimer.setElapse(elpase);
    }

    public void setTimerElapse(long elapse, ArrayList<ElementInfo> FOrder) {
        mTimer.setElapse(elapse);
        ArrayList<ElementInfo> data = mScript.getElementInfoByElapseTime(elapse, FOrder);

        Log.e(TAG, "data.size():" + data.size());
        for(int i = 0; i < data.size(); i++) {
            changeBitmap(data.get(i), true);
        }
    }

    public void reset() {
        mBitmapinit = false;
        mBitmapUpdate = false;
        mSloganinit = false;
    }

    public void setTimerForFilter(long timer) {
        mFilter.setTimer(timer);
    }

    public void setEye() {
        // Position the eye in front of the origin.
        final float eyeX = 0.0f;
        final float eyeY = 0.0f;
        final float eyeZ = -1.0f;

        // We are looking toward the distance
        final float lookX = 0.0f;
        final float lookY = 0.0f;
        final float lookZ = 0.0f;

        // Set our up vector. This is where our head would be pointing were we
        // holding the camera.
        final float upX = 0.0f;
        final float upY = -1.0f;
        final float upZ = 0.0f;

        // Set the view matrix. This matrix can be said to represent the camera
        // position.
        // NOTE: In OpenGL 1, a ModelView matrix is used, which is a combination
        // of a model and
        // view matrix. In OpenGL 2, we can keep track of these matrices
        // separately if we choose.
        Matrix.setLookAtM(mViewMatrix, 0, eyeX, eyeY, eyeZ, lookX, lookY, lookZ, upX, upY, upZ);
    }

    public void setView(int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        // Create a new perspective projection matrix. The height will stay the
        // same
        // while the width will vary as per aspect ratio.
        // final float ratio = (float) height/width;
        final float ratio = (float) width / height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;

        ScreenRatio = (float) Math.ceil(((float) width / height) * 10.0f) / 10;

        Log.e(TAG, "setView, width:" + width + ", height:" + height + ", ScreenRatio:" + ScreenRatio);

        Matrix.orthoM(mProjectionMatrix, 0, left, right, bottom, top, near, far);
    }
}
