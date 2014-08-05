package com.s890510.microfilm;


import android.content.Context;
import android.os.Handler;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.s890510.microfilm.MicroFilmActivity.SaveCallback;
import com.s890510.microfilm.script.Script;
import com.s890510.microfilm.script.Timer;

public class ControlPanel extends LinearLayout {
    private static final String TAG = "ControlPanel";

    private SeekBar mSeekbar;
    private ImageButton mButton;

    private Context mContext;
    private MicroFilmActivity mActivity;
    private MicroFilmSurfaceView mMovieSurfaceView;
    private TextView mElpaseTime, mElpaseTime_total;

    private final int SEEKBAR_UPDATE_INTERVAL = 100;
    private int mTimerPause;
    private Timer mTimer;
    private boolean IsPause = false;
    private boolean mPButton = false;

    private Handler mHandler = new Handler();
    
    private boolean mIsDDSSave = false;
    private SaveCallback mSaveCallback = null;

    Runnable mUpdate =new Runnable(){
        @Override
        public void run() {
            if(!mActivity.checkPause()) {
                mSeekbar.setProgress((int)mTimer.SeekBarElapse());
                mHandler.postDelayed(mUpdate, SEEKBAR_UPDATE_INTERVAL);
                
                if(mIsDDSSave){
                	mIsDDSSave = false;
                	mSaveCallback.onDDSSave();
                }
            }
        }
    };

    public ControlPanel(Context context) {
        super(context);
    }

    public ControlPanel(MicroFilmActivity activity, MicroFilmSurfaceView videoView, LinearLayout control_panel) {
        super(activity.getApplicationContext());
        mContext = activity.getApplicationContext();
        mActivity = activity;
        mMovieSurfaceView = videoView;

        mSeekbar = (SeekBar) control_panel.findViewById(R.id.micromovie_seekbar);
        mSeekbar.setEnabled(false);
        mSeekbar.setMax(mMovieSurfaceView.getDuration());
        setOnSeekbarChangeListener();

        mButton = (ImageButton) control_panel.findViewById(R.id.micromovie_control_btn);
        mButton.setEnabled(false);
        mButton.setImageAlpha(150);

        mElpaseTime = (TextView) control_panel.findViewById(R.id.micromovie_eclipsetime);
        mElpaseTime.setText("00:00");

        mElpaseTime_total = (TextView) control_panel.findViewById(R.id.micromovie_eclipsetime_total);
        mElpaseTime_total.setText("00:00");

        mTimer = mMovieSurfaceView.getTimer();
    }

    public void setSeekBarEnable(boolean enable) {
        mSeekbar.setEnabled(enable);
    }

    private void setOnSeekbarChangeListener() {
        mSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int mProcessTime = mSeekbar.getProgress();
                if((int)mProcessTime/1000 < 10)
                    mElpaseTime.setText("00:0" + (int)mProcessTime/1000);
                else
                    mElpaseTime.setText("00:" + (int)mProcessTime/1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(!IsPause) {
                    ControlPause(false);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mTimerPause = ((int)seekBar.getProgress()/1000)*1000;
                mTimer.setElapse(mTimerPause);
                mMovieSurfaceView.resetItemElapse((long)mTimerPause);
                mMovieSurfaceView.setProgress(mTimerPause);

                if(IsPause && !mPButton) {
                    ControlResume(false);
                } else {
                    mMovieSurfaceView.setMusic(mTimerPause);
                }
            }
        });
    }

    //If the changeIcon is true means it's come from activity(pause button)
    public void ControlPause(boolean changeIcon) {
        IsPause = true;
        mTimerPause = (int)mTimer.SeekBarElapse(); //We get the time from timer

        mActivity.setPause(true);
        mMovieSurfaceView.MusicPause();

        mHandler.removeCallbacks(mUpdate);

        if(changeIcon) {
            mButton.setImageResource(R.drawable.asus_micromovie_play_icon);
            mPButton = true;
        }
    }

    public void ControlResume(boolean changeIcon) {
        IsPause = false;
        if(changeIcon) {
            mTimer.setElapse(mTimerPause);
            mSeekbar.setProgress(mTimerPause);
            mButton.setImageResource(R.drawable.asus_micromovie_press_icon);
            mPButton = false;
        }

        mMovieSurfaceView.resetItemElapse((long)mTimerPause);

        mActivity.setPause(false);

        mMovieSurfaceView.ResumePreview();
        mMovieSurfaceView.setMusic(mTimerPause);
        mHandler.postDelayed(mUpdate, SEEKBAR_UPDATE_INTERVAL);
    }

    public void startPlay(Script script){
        mButton.setEnabled(true);
        mButton.setImageAlpha(225);
        mButton.setImageResource(R.drawable.asus_micromovie_press_icon);
        mMovieSurfaceView.StartPreview(script);

        mElpaseTime_total.setText("00:" + (int)Math.ceil(mMovieSurfaceView.getDuration()/1000));

        mHandler.postDelayed(mUpdate, SEEKBAR_UPDATE_INTERVAL);
    }

    public void drawScreen() {
        mTimerPause = mSeekbar.getProgress();
        mTimer.setElapse(mTimerPause);
        mMovieSurfaceView.resetItemElapse((long)mTimerPause);
        mMovieSurfaceView.setProgress(mTimerPause);

        if(IsPause && !mPButton) {
            ControlResume(false);
        } else {
            mMovieSurfaceView.setMusic(mTimerPause);
        }
    }

    public void destroy(){
        mHandler.removeCallbacks(mUpdate);
    }

    public void setSeekbarMax(){
        mSeekbar.setMax(mMovieSurfaceView.getDuration());
    }

    public void finishPlay(){
        mHandler.removeCallbacks(mUpdate);
        mSeekbar.setProgress(0);
        mElpaseTime.setText("00:00");
        mButton.setImageResource(R.drawable.asus_micromovie_replay_icon);
    }
    
    public void setDDSSave(SaveCallback callback){
    	mIsDDSSave = true;
    	if(mSaveCallback == null)
    		mSaveCallback = callback;
    }
}
