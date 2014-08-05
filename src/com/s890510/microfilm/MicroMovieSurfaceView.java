package com.s890510.microfilm;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;

import com.s890510.microfilm.script.Script;
import com.s890510.microfilm.script.Timer;

class MicroMovieSurfaceView extends GLSurfaceView
    implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private static String TAG = "MicroMovieSurfaceView";
    private ProcessGL mProcessGL;
    private boolean updateSurface = false;
    private PlayBackMusic mPlayBackMusic = null;
    private ArrayList<FileInfo> mFilesList;
    private ArrayList<ElementInfo> mFileOrder = new ArrayList<ElementInfo>();

    private MicroMovieActivity mActivity;
    private MicroMovieListener mUpdatelistener;
    private final Handler mHandler;
    private PlayControl mPlayControl = null;
    private int mWidth = 0, mHeight = 0;
    private boolean mIsDone = false;
    private Timer mTimer;
    private Script mScript;
    public boolean mReadyInit = false;
    public boolean mIsPlayFin = false;

    public static final int MSG_STARTPROGRASS = 1;
    public static final int MSG_STOPPROGRASS = 2;
    public static final int MSG_FINCHANGESURFACE = 3;
    public static final int MSG_PLAYFIN = 4;

    public static final int INFO_TYPE_BITMAP = 0;
    public static final int INFO_TYPE_VIDEO = 1;

    public MicroMovieSurfaceView(MicroMovieActivity activity) {
        this(activity, null);
    }

    public void IsPlayFin(boolean IsFin) {
        mIsPlayFin = IsFin;
    }

    private Context mContext;
    private ControlPanel mControlPanel;
    public MicroMovieSurfaceView(MicroMovieActivity activity, AttributeSet attrs) {
        super(activity.getApplicationContext(), attrs);
        mContext = activity.getApplicationContext();
        mActivity = activity;
        setEGLContextClientVersion(2);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_STARTPROGRASS:
                        if(mUpdatelistener!=null)
                            mUpdatelistener.doUpdate(MicroMovieListener.START_INIT_PROGRASSDIALOG);
                        break;
                    case MSG_STOPPROGRASS:
                        if(mUpdatelistener!=null)
                            mUpdatelistener.doUpdate(MicroMovieListener.STOP_INIT_PROGRASSDIALOG);
                        break;
                    case MSG_FINCHANGESURFACE:
                        if(mUpdatelistener!=null)
                            mUpdatelistener.doUpdate(MicroMovieListener.FINISH_CHANGESURFACE);
                        break;
                    case MSG_PLAYFIN:
                        Log.e(TAG, "MSG_PLAYFIN");
                        if(!mIsDone) {
                            mProcessGL.reset();
                            mProcessGL.clearProcessData();
                            ClearOrderTexture();
                        }
                        if(mUpdatelistener!=null)
                            mUpdatelistener.doUpdate(MicroMovieListener.FINISH_PLAY);
                        break;
                }
            }
        };

        mProcessGL = new ProcessGL(mContext, mActivity, false);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        mPlayControl = new PlayControl(this);

        // set music playback
        mPlayBackMusic = new PlayBackMusic(mContext, 0);
        mPlayBackMusic.prepareMusic();

        mTimer = new Timer(getDuration() * 1000, mActivity, mProcessGL);
    }

    public boolean checkPause() {
        return mActivity.checkPause();
    }

    public Timer getTimer() {
        return mTimer;
    }

    public void CanclePlay() {
        synchronized (mPlayControl) {
            if(mPlayControl != null && mPlayControl.isAlive()) {
                mPlayControl.terminate();
                mPlayControl.interrupt();
                mPlayControl.join();
            }
        }
        mPlayBackMusic.destroy();

        mProcessGL.clearProcessData();

        if(!mIsDone)
            mProcessGL.reset();
    }

    public void MusicPause() {
        if(mPlayControl != null) {
            synchronized (mPlayControl) {
                if(mPlayBackMusic != null) {
                    if(mActivity.checkPause() && mPlayBackMusic.isPlaying()) {
                        mPlayControl.interrupt();
                        mPlayBackMusic.pause();
                    }
                } else {
                    if(mActivity.checkPause() && !mPlayBackMusic.isPlaying())
                        mPlayControl.interrupt();
                }
            }
        }
    }

    public void setFileOrder(ArrayList<ElementInfo> FOrder) {
        mFileOrder = FOrder;
    }

    public void StartPreview(Script script) {
        mIsPlayFin = false;
        if(mPlayBackMusic.CheckPlayer()) {
            mPlayBackMusic.destroy();
        }

        if(mPlayControl == null) {
            mPlayControl.CreateControl(mFileOrder);
        } else {
            if(mPlayControl.isAlive()) {
                mPlayControl.terminate();
                mPlayControl.interrupt();
                mPlayControl.join();
            }
            mPlayControl.CreateControl(mFileOrder);
        }

        synchronized (mPlayControl) {
            mScript = script;

            try {
                mPlayBackMusic.setAudioTrackFile(mContext, script.getMusicId());
                mPlayBackMusic.prepareMusic();
                mControlPanel.setSeekbarMax();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mProcessGL.setScript(script);
            mProcessGL.setTimerElapse(0);
            mPlayBackMusic.start();

            mPlayControl.start();
        }
        this.requestRender();
    }

    public void ResumePreview() {
        mPlayControl.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public Script getScript(){
        return mProcessGL.getScript();
    }

    public void changeVideo(int VideoId) {
        mProcessGL.changeVideo(VideoId);
    }

    public void changeSlogan() {
        mProcessGL.changeSlogan();
    }

    public void changeBitmap(ElementInfo eInfo, boolean resetTimer) {
        mProcessGL.changeBitmap(eInfo, resetTimer);
    }

    public void SeekVideo(int textureId, int videopart, int time) {
        mProcessGL.SeekVideo(textureId, videopart, time);
    }

    public void onDestroy() {
        if(mPlayControl!=null && mPlayControl.isAlive()) {
            mPlayControl.terminate();
            mPlayControl.interrupt();
            mPlayControl.onDestory();
            mPlayControl = null;
        }
        mPlayBackMusic.destroy();

        mIsDone = true;
        mProcessGL.onDestroy();
        GLES20.glFinish();
    }

    public void resumeMediaPlayer() {
        mProcessGL.resumeMediaPlayer();
    }

    public void pauseMediaPlayer() {
        mProcessGL.pauseMediaPlayer();
    }

    public void stopMediaPlayer() {
        mProcessGL.stopMediaPlayer();
    }

    public void SendMSG(int msg) {
        Message m = new Message();
        m.what = msg;
        mHandler.sendMessage(m);
    }

    public void InitData() {
        //The same means all files are in init.
        if(mActivity.getInitBitmapCount() == mFilesList.size()) {
            mActivity.setInitial(true);
            return;
        }

        for(int i=0; i<mFilesList.size(); i++) {
            if(!mFilesList.get(i).IsFake) continue;

            if(mFilesList.get(i).Type == INFO_TYPE_BITMAP) {
                mFilesList.get(i).mIsInitial = mProcessGL.setBitmap(mFilesList.get(i));
            } else if(mFilesList.get(i).Type == INFO_TYPE_VIDEO) {
                mFilesList.get(i).mIsInitial = mProcessGL.setMediaPlayer(mFilesList.get(i));
            }
        }

        mActivity.setInitial(true);
    }

    public void setFiles(ArrayList<FileInfo> FList) {
        mFilesList = FList;
        mProcessGL.setFileInfo(FList);
    }

    public void setUpdateLintener(MicroMovieListener updateListener) {
        mUpdatelistener = updateListener;
    }

    public int getDuration() {
        return mPlayBackMusic.getduration();
    }

    public int getProgress(){
        return (int) mTimer.getElapse();
    }

    private void ClearOrderTexture() {
        for(int i=0; i<mFileOrder.size(); i++) {
            if(mFileOrder.get(i).Type == INFO_TYPE_BITMAP)
                mFileOrder.get(i).TextureId = -1;
        }
    }

    public void resetItemElapse(long elapse) {
        if(mScript != null)
            mScript.resetItemElapse(elapse, mFileOrder);
    }

    public void setProgress(int progress_msec) {
        int progress = progress_msec;

        mProcessGL.clearProcessData();
        ClearOrderTexture();

        if(mProcessGL.getVideoStatus()) {
            mProcessGL.stopMediaPlayer();
        }

        synchronized (mPlayControl) {
            mPlayControl.setElapse(progress);
            mPlayControl.setSleep((int)mProcessGL.getScript().getSleepByElapse(progress));
        }
        mProcessGL.setTimerElapse(progress, mFileOrder);

        this.requestRender();
    }

    public void setMusic(int progress_msec) {
        int progress = progress_msec;

        if(progress > mPlayBackMusic.getduration()) {
            mPlayBackMusic.pause();
        } else {
            mPlayBackMusic.seekTo(progress);
            if(!mPlayBackMusic.isPlaying() && !mActivity.checkPause()) {
                mPlayBackMusic.start();
                this.requestRender();
            }
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if(mActivity.isSaving()){
            mProcessGL.doDrawNothing();
        }else{
            synchronized(this) {
                if (updateSurface) {
                    mProcessGL.updateSurfaceTexture();
                    updateSurface = false;
                }
            }
            mProcessGL.doDraw(-1);
        }

        if(!mActivity.checkPause() && mActivity.checkPlay() && !mIsPlayFin) {
            this.requestRender();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mProcessGL.setView(width, height);
        mProcessGL.setEye();
        if(mWidth != width || mHeight != height) {
            SendMSG(MSG_STARTPROGRASS);
            mProcessGL.setScreenScale((float)height/width, width, height);
            mWidth = width;
            mHeight = height;
            SendMSG(MSG_STOPPROGRASS);
        }
        SendMSG(MSG_STOPPROGRASS);
        SendMSG(MSG_FINCHANGESURFACE);
        mReadyInit = true;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        SendMSG(MSG_STARTPROGRASS);
        mProcessGL.prepareOpenGL(this);
        synchronized(this) {
            updateSurface = false;
        }

        mWidth = 0;
        mHeight = 0;

        GLES20.glDepthMask(false);
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        updateSurface = true;
    }

    public void setTimerElapse(long l, ArrayList<ElementInfo> FOrder){
        mProcessGL.setTimerElapse(l, FOrder);
    }

    public void setTimerElapse(long l){
        mProcessGL.setTimerElapse(l);
    }

    public void setControlPanel(ControlPanel mCPanel) {
        mControlPanel = mCPanel;
    }
    
    public ProcessGL getProcessGL(){
    	return mProcessGL;
    }
}
