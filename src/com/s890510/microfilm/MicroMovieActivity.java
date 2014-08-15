package com.s890510.microfilm;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.s890510.microfilm.script.Carnival;
import com.s890510.microfilm.script.City;
import com.s890510.microfilm.script.Country;
import com.s890510.microfilm.script.Kids;
import com.s890510.microfilm.script.Life;
import com.s890510.microfilm.script.Lover;
import com.s890510.microfilm.script.Memory;
import com.s890510.microfilm.script.Script;
import com.s890510.microfilm.script.Sports;
import com.s890510.microfilm.util.AsusThemeUtility;
import com.s890510.microfilm.util.Util;

public class MicroMovieActivity extends Activity {

    private final String TAG = "MicroMovieActivity";
    private final int MIN_STORAGE_NEED = 200 * 1024 *1024; // 200 MB
    private ArrayList<MediaInfo> mFileList = new ArrayList<MediaInfo>();
    private ArrayList<ElementInfo> mFileOrder = null;
    private ProgressDialog mProgressDialog;
    private boolean isPause = false;
    private String Owner_Name;
    private UpdateListener mUpdateListener = new UpdateListener();
    private boolean mWindowhasFocus = false;
    //private double[] mLatitude, mLongitude;
    private boolean mIsAuto = false;

    private MicroMovieSurfaceView mMicroView = null;

    private ProgressDialog mSaveProgressDialog;
    private ControlPanel mControlPanel;
    private Script mScript;
    private int mScriptSelect = -1;

    private MenuItem mSaveMovie;
    private MenuItem mRefreshMovie;
    private MenuItem mMovieEdit;
    private ImageButton mControlButtom;
    private boolean isPlaying = false;
    private boolean setLayoutListener = false;
    private ThemeLayout mThemeLayout;
    private ThemeAdapter mThemeAdapter;
    private ActionBar mActionBar;

    private static boolean mIsSaving = false;
    private boolean mIsThisActivitySaving = false;
    private boolean isInitial = false;
    private boolean mIsLoadBitmapDone = false;
    private boolean mIsSetView = false;
    private boolean mIsDDS = false;
    private boolean mFirstLoad = true;
    public long mStartDate = 0;
    public long mEndDate = 0;
    public int mItemCount = 0;
    private boolean mIsPauseState = false;
    private Object mAttachView = new Object();
    private boolean mWindowState = false;
    private int mViewWidth;
    private int mViewHeight;

    private static Thread mSaveThread;

    private int mInitBitmapCount = 0;
    private int mDoneBitmapCount = 0;

    private EncodeAndMuxTest mEncodeAndMuxTest;
    public LoadTexture mLoadTexture;
    public MicroMovieOrder mMicroMovieOrder;

    public static final int mVisioWidth = 1280;
    public static final int mVisioHeight = 720;

    interface ISaveCallback{
        void onSaveDone(String saveUri, int frameTime);
        void onException();
        void onDDSSave();
        void onInterrupted();
    }

    SaveCallback mSaveCallback;
    AlertDialog mStopSaveDialog = null;

    public boolean checkPause() {
        return isPause;
    }

    public boolean checkPlay() {
        return isPlaying;
    }

    public boolean checkInitial() {
        return isInitial;
    }

    public void setInitial(boolean set) {
        isInitial = set;
    }

    public void DoneLoadBitmap() {
        mDoneBitmapCount++;
        synchronized (mAttachView) {
            if(!mIsSetView) SetupMicroView();
        }
        if(mInitBitmapCount == mDoneBitmapCount) {
            //Here we need to quickly check again about bitmap
            for(int i=0; i<mFileList.size(); i++) {
                if(!mFileList.get(i).mIsInitial || mFileList.get(i).getImage() == null) {
                    mFileList.remove(i);
                    i--;
                } else {
                    mFileList.get(i).CountId = i;
                }
            }
            mMicroView.setMedia(mFileList);
            mMicroView.InitData();
            mIsLoadBitmapDone = true;
            if(mMicroView.mReadyInit)
                mMicroView.SendMSG(MicroMovieSurfaceView.MSG_STOPPROGRASS);
        }
    }

    public int getInitBitmapCount() {
        return mInitBitmapCount;
    }

    public class SaveCallback implements ISaveCallback{

        @Override
        public void onSaveDone(final String saveUri, final int frameTime) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mIsSaving = false;
                    mIsThisActivitySaving = false;
                    if(mSaveProgressDialog!=null && mSaveProgressDialog.isShowing()){
                        mSaveProgressDialog.dismiss();
                        mSaveProgressDialog = null;
                    }
                    //resumeFromSave();
                    //Toast.makeText(MicroMovieActivity.this, "MicroMovie is saved to "+EncodeAndMuxTest.OUTPUT_DIR, Toast.LENGTH_SHORT).show();
                    /*
                    Intent intent = new Intent();
                    intent.setClass(MicroMovieActivity.this, CompleteAndShareActivity.class);
                    intent.putExtra(CompleteAndShareActivity.EXTRA_TYPE, CompleteAndShareActivity.TYPE_MICROMOVIE);
                    intent.putExtra(CompleteAndShareActivity.EXTRA_DIRECTORY, "Gallery/MicroFilm");
                    intent.putExtra(CompleteAndShareActivity.EXTRA_IMAGE_URI, saveUri);

                    intent.putExtra(CompleteAndShareActivity.EXTRA_DIRECTOR, Owner_Name);
                    intent.putExtra(CompleteAndShareActivity.EXTRA_FRAME_TIME, frameTime);

                    SimpleDateFormat formatter = new SimpleDateFormat("MM.dd.yyyy");
                    String now = formatter.format(new Date());
                    intent.putExtra(CompleteAndShareActivity.EXTRA_DATE, now);

                    int themeNameRId = mThemeAdapter.getThemeNameRId(mScriptSelect);
                    if(themeNameRId != -1){
                        String themeName = getBaseContext().getString(themeNameRId);
                        intent.putExtra(CompleteAndShareActivity.EXTRA_THEME, themeName);
                    }

                    startActivity(intent);
                    */
                    finish();

                }
            });
        }

        @Override
        public void onException() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showEncodeExceptionDialog();
                }
            });
        }

        @Override
        public void onDDSSave() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mControlPanel.ControlPause(true);
                    showRestartSaveDialog();
                }
            });
        }

        @Override
        public void onInterrupted() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    resumeFromSave();

                }
            });
        }
    }

    public String getOwnerUser() {
        return Owner_Name;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // keep screen on

        mThemeAdapter = new ThemeAdapter(this);
        mThemeAdapter.setUpdateLintener(mUpdateListener);

        mActionBar = getActionBar();
        mActionBar.setDisplayShowTitleEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(false);
        mActionBar.setTitle(R.string.micromovie_title);

        final String[] projection = new String[] { ContactsContract.Profile.DISPLAY_NAME };
        final Uri dataUri = Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI, ContactsContract.Contacts.Data.CONTENT_DIRECTORY);
        final ContentResolver contentResolver = getContentResolver();
        Cursor c = null;
        try{
            c = contentResolver.query(dataUri, projection, null, null, null);
            if(c != null && c.moveToFirst()) {
                Owner_Name = c.getString(c.getColumnIndex(ContactsContract.Profile.DISPLAY_NAME));
            }
        }finally{
            if(c != null)
                c.close();
        }

        setContentView(R.layout.asus_micromovie_main);

        mMicroMovieOrder = new MicroMovieOrder(this);
        mMicroView = new MicroMovieSurfaceView(this);
        mMicroView.setUpdateLintener(mUpdateListener);

        mControlPanel = new ControlPanel(this, mMicroView, (LinearLayout) findViewById(R.id.micromovie_control_panel));
        mMicroView.setControlPanel(mControlPanel);

        mControlButtom = (ImageButton) findViewById(R.id.micromovie_control_btn);
        mControlButtom.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isPlaying) {
                    DoPlay();
                } else {
                    if(!isPause) {
                        mControlPanel.ControlPause(true);
                    } else {
                        mControlPanel.ControlResume(true);
                    }
                }
            }
        });

        mThemeLayout = (ThemeLayout) findViewById(R.id.micromovie_themelayout);
        mThemeLayout.SetTheme(mThemeAdapter);

        if(savedInstanceState != null) {
            mScriptSelect = savedInstanceState.getInt("SelectTheme");
            if(mScriptSelect > -1) {
                mThemeAdapter.setThemePosition(mScriptSelect);
                mThemeAdapter.showPlay(mThemeLayout.GetThemeView(mScriptSelect, mThemeAdapter));
                mIsDDS = true;
            }
            mIsThisActivitySaving = savedInstanceState.getBoolean("IsSaving");

            for(int i=0; i<ThemeAdapter.TYPE_Count; i++) {
                mMicroMovieOrder.setOrderList(i,
                        savedInstanceState.getIntArray("OrderList_" + i),
                        savedInstanceState.getFloatArray("CenterX_" + i),
                        savedInstanceState.getFloatArray("CenterY_" + i));
            }
        } else {
            mMicroMovieOrder.Reset();
        }

        mSaveCallback = new SaveCallback();
        mLoadTexture = new LoadTexture();
        
        //temp
        mFileList = ((MediaPool)getApplicationContext()).getMediaInfo();
        Log.e(TAG, "mFileList.size:" + mFileList.size());
        mMicroView.setMedia(mFileList);
        mMicroView.InitData();
        mIsLoadBitmapDone = true;
    }

    private void checkSaveWhenLaunch(){
        if(mIsSaving && !mIsThisActivitySaving){ // someone is saving and it's not this activity
            if(mStopSaveDialog == null)
                showStopAnotherTranscodingDialog();
        }
    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void SetupMicroView() {
        LinearLayout mPreview = (LinearLayout) findViewById(R.id.asus_micromovie_preview);
        if(!setLayoutListener) {
            setLayoutListener = true;
            mPreview.addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right,
                        int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if((v.getWidth() != 0 && v.getHeight() != 0) && (mViewWidth != v.getWidth() || mViewHeight != v.getHeight())) {
                        synchronized (mAttachView) {
                            SetupMicroView();
                        }
                    }
                }
            });
        }
        int mWidth = mPreview.getWidth();
        int mHeight = mPreview.getHeight();

        if((mWidth != 0 && mHeight != 0) && (mViewWidth != mWidth || mViewHeight != mHeight)) {
            mViewWidth = mWidth;
            mViewHeight = mHeight;

            if(mWidth/16 <= mHeight/9) {
                mHeight = (mWidth/16) * 9;
                mWidth = (mWidth/16) * 16;
            } else {
                mWidth = (mHeight/9) * 16;
                mHeight = (mHeight/9) * 9;
            }

            RelativeLayout mPlayer = (RelativeLayout) findViewById(R.id.micromovie_player);
            RelativeLayout.LayoutParams layout_param= new RelativeLayout.LayoutParams(mWidth, mHeight);
            layout_param.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            layout_param.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);

            if(mPlayer.getChildCount() == 0) {
                mPlayer.removeAllViews();
                mPlayer.addView(mMicroView, layout_param);
            } else {
                mPlayer.getChildAt(0).setLayoutParams(layout_param);
            }

            mIsSetView = true;
        }

        if(!mWindowState && isPause && mWindowhasFocus) {
            mControlPanel.drawScreen();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.e(TAG, "onWindowFocusChanged, " + hasFocus);
        mWindowhasFocus = hasFocus;

        synchronized (mAttachView) {
            SetupMicroView();
        }

        mWindowState = hasFocus;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.asus_micromovie_menu, menu);
        mSaveMovie = (MenuItem) menu.findItem(R.id.MicroMovie_save_button);
        mRefreshMovie = (MenuItem) menu.findItem(R.id.MicroMovie_refresh_button);
        mMovieEdit = (MenuItem) menu.findItem(R.id.MicroMovie_album_button);

        if(mScript == null){
            mSaveMovie.setEnabled(false);
            mSaveMovie.getIcon().setAlpha(130);

            mRefreshMovie.setEnabled(false);
            mRefreshMovie.getIcon().setAlpha(130);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.MicroMovie_save_button:
                if(!mIsSaving){ // avoid multiple entrance
                    if(Util.getAvailableSpace() < MIN_STORAGE_NEED){
                        showStorageNotEnoughDialog();
                    }else{
                        SaveMicroMovie();
                    }
                }
                return true;
            case R.id.MicroMovie_refresh_button:
                setMovieOrder(true);
                DoPlay();
                return true;
            case R.id.MicroMovie_album_button:
            	Intent intent = new Intent();
		        intent.setClass(getApplicationContext(), MicroFilmActivity.class);
		        intent.putExtra("ToEdit", true);
		        startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkSaveWhenLaunch();

        mIsPauseState = false;
        mActionBar.show();
        Log.e(TAG, "select:" + mScriptSelect);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mIsPauseState = true;
        Log.e(TAG, "onPause");

        if(isPlaying) {
            mControlPanel.ControlPause(true);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(checkPlay()) {
            outState.putInt("SelectTheme", mScriptSelect);
        } else {
            outState.putInt("SelectTheme", -1);
        }

        outState.putBoolean("IsSaving", mIsThisActivitySaving);

        for(int i=0; i<ThemeAdapter.TYPE_Count; i++) {
            outState.putIntArray("OrderList_" + i, mMicroMovieOrder.getOrderList(i));
            outState.putFloatArray("CenterX_" + i, mMicroMovieOrder.getCenterX(i));
            outState.putFloatArray("CenterY_" + i, mMicroMovieOrder.getCenterY(i));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");

        if(isSaving()){
            if(mSaveProgressDialog!=null && mSaveProgressDialog.isShowing()){
                mSaveProgressDialog.dismiss();
                mSaveProgressDialog = null;
            }

            if(mIsThisActivitySaving){
                if(mSaveThread != null){
                    mSaveThread.interrupt();
                }
                mIsSaving = false;
                mIsThisActivitySaving = false;
            }
        }

        /*
        for(int i=0; i<mFileList.size(); i++) {
            mFileList.get(i).onDestory();
            if(mFileList.get(i).mBitmap != null) {
                mFileList.get(i).mBitmap.recycle();
                mFileList.get(i).mBitmap = null;
            }
            if(mFileList.get(i).mGeoInfo != null) {
                mFileList.get(i).mGeoInfo.cancel();
            }
        }
        */

        if(mProgressDialog != null)
            mProgressDialog.dismiss();
        mControlPanel.destroy();
        mMicroView.onDestroy();

        isInitial = false;
        isPause = false;
        isPlaying = false;
        mStartDate = 0;
        mEndDate = 0;
        mItemCount = 0;
    }

    private void SaveMicroMovie() {
        mEncodeAndMuxTest = new EncodeAndMuxTest(this, mFileList, mFileOrder, mScript, mScriptSelect);

        // show progress dialog
        mIsSaving = true;
        mIsThisActivitySaving = true;
        mSaveProgressDialog = new ProgressDialog(this);
        mSaveProgressDialog.setMessage(getResources().getString(R.string.transcoding));
        mSaveProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mSaveProgressDialog.setMax(mEncodeAndMuxTest.TOTAL_FRAMES + 100);
        mSaveProgressDialog.setProgressNumberFormat(null);
        //mSaveProgressDialog.setProgressPercentFormat(null);
        mSaveProgressDialog.setCanceledOnTouchOutside(false);
        mSaveProgressDialog.setOnKeyListener(new OnKeyListener(){
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode,
                    KeyEvent event) {
                if(keyCode == KeyEvent.KEYCODE_BACK){
                    if(event.getAction() == KeyEvent.ACTION_UP)
                        showCancelSaveDialog();
                    return true;
                }
                return false;
            }
        });

        if(isPlaying)
            mControlPanel.ControlPause(true);

        mSaveProgressDialog.show();


        // start encoding
        mSaveThread = new Thread(new Runnable(){
            @Override
            public void run() {
                mEncodeAndMuxTest.testEncodeVideoToMp4(mSaveCallback, mSaveProgressDialog);
            }
        });
        mSaveThread.start();
    }


    private void DoPlay() {
        if(mScript == null) {
            return;
        }

        isPause = false;

        if(isPlaying) {
            mMicroView.CanclePlay();
            mControlPanel.finishPlay();
        }

        mControlPanel.setVisibility(View.VISIBLE);
        mControlPanel.setSeekBarEnable(true);

        isPlaying = true;

        mControlPanel.startPlay(mScript);

        if(mStopSaveDialog != null && mStopSaveDialog.isShowing())
            mControlPanel.ControlPause(true);
    }

    public void switchTheme() {
        switch(mThemeAdapter.getThemePosition()){

            case ThemeAdapter.TYPE_KIDS:
                mScript = new Kids(this, mMicroView.getProcessGL());
                break;
            case ThemeAdapter.TYPE_MEMORY:
                mScript = new Memory(this, mMicroView.getProcessGL());
                break;
            case ThemeAdapter.TYPE_VINTAGE:
                mScript = new Country(this, mMicroView.getProcessGL());
                break;
            case ThemeAdapter.TYPE_ROMANCE:
                mScript = new Lover(this, mMicroView.getProcessGL());
                break;
            case ThemeAdapter.TYPE_CARNIVAL:
                mScript = new Carnival(this, mMicroView.getProcessGL());
                break;
            case ThemeAdapter.TYPE_LIFE:
                mScript = new Life(this, mMicroView.getProcessGL());
                break;
            case ThemeAdapter.TYPE_SPORTS:
                mScript = new Sports(this, mMicroView.getProcessGL());
                break;
            case ThemeAdapter.TYPE_CITY:
                mScript = new City(this, mMicroView.getProcessGL());
                break;
            default:
                Toast.makeText(MicroMovieActivity.this, "This theme is not ready", Toast.LENGTH_SHORT).show();
                return;
        }

        if(mScript == null) {
            return;
        }

        mScriptSelect = mThemeAdapter.getThemePosition();

        setMovieOrder(false);

        mSaveMovie.setEnabled(true);
        mSaveMovie.getIcon().setAlpha(255);

        mRefreshMovie.setEnabled(true);
        mRefreshMovie.getIcon().setAlpha(255);

        DoPlay();
    }

    private void setMovieOrder(boolean IsShuffle) {
        if(mMicroMovieOrder.IsOrder(mScript.GetScriptId()) && !IsShuffle) {
            mFileOrder = mMicroMovieOrder.getOrderInfo(mScript.GetScriptId());
        } else {
            ProcessGL processGL = mMicroView.getProcessGL();
            mFileOrder = mMicroMovieOrder.gettimeandorder(processGL, mFileList, mScript, IsShuffle);
            mFileOrder = mScript.setElementInfoTime(mFileOrder);

            /*/--------------------------------------------
            for(int i=0; i<mFileOrder.size(); i++) {
                Log.e(TAG, "i:" + i + ", Type:" + mFileOrder.get(i).Type + ", Info:" + mFileOrder.get(i).InfoId +
                        ", scaleW:" + mFileOrder.get(i).scaleW + ", scaleH:" + mFileOrder.get(i).scaleH +
                        ", x:" + mFileOrder.get(i).x + ", y:" + mFileOrder.get(i).y);
            }
            //--------------------------------------------*/

            //Calc. Bitmap TriangleVertices
            for(int i=0; i<mFileOrder.size(); i++) {
                if(mFileOrder.get(i).Type == MediaInfo.MEDIA_TYPE_IMAGE) {
                    mFileOrder.get(i).CalcTriangleVertices(processGL);
                }
            }

            mMicroMovieOrder.setOrderInfo(mScript.GetScriptId(), mFileOrder);
        }

        if(mMicroView != null)
            mMicroView.setFileOrder(mFileOrder);

        /*/--------------------------------------------
        for(int i=0; i<mFileOrder.size(); i++) {
            Log.e(TAG, "i:" + i + ", Type:" + mFileOrder.get(i).Type +
                    ", scaleW:" + mFileOrder.get(i).scaleW + ", scaleH:" + mFileOrder.get(i).scaleH +
                    ", x:" + mFileOrder.get(i).x + ", y:" + mFileOrder.get(i).y +
                    ",faceXx:" + mFileOrder.get(i).faceX + ",faceYy:" + mFileOrder.get(i).faceY);
        }
        //--------------------------------------------*/
    }
    
    /*
    private void runData() {
        int BitmapCounter = 0;
        int VideoCounter = 0;
        ArrayList<Integer> mVideoList = new ArrayList<Integer>();
        for(int i=0; i<mFiles.size(); i++) {
        	MediaInfo info = new MediaInfo(this);
            info.Path = mFiles.get(i);
            info.PathString = mVirtualPath.get(i);
            info.CountId = i;
            info.mDate = mDateInfo.get(i);

            if(mStartDate == 0 || info.mDate < mStartDate) {
                mStartDate = info.mDate;
            }
            if(info.mDate > mEndDate) {
                mEndDate = info.mDate;
            }

            if(mLatitude.get(i) != 99999 && mLongitude.get(i) != 99999) {
                info.mGeoInfo = new GeoInfo(getApplicationContext(), this, new double[]{mLatitude.get(i), mLongitude.get(i)});
                info.mGeoInfo.LoadAddress();
            }

            if(mTypeFiles.get(i) == MediaInfo.MEDIA_TYPE_IMAGE) {
                info.Type = MicroMovieSurfaceView.INFO_TYPE_BITMAP;
                info.TextureId = BitmapCounter;
                info.CountId = i;
                info.Rotate = mRotateInfo.get(i);
                info.LoadBitmap();

                mInitBitmapCount++;
                BitmapCounter++;
                mFileList.add(info);
            } else if(mTypeFiles.get(i) == MediaInfo.MEDIA_TYPE_VIDEO) {
                info.Type = MicroMovieSurfaceView.INFO_TYPE_VIDEO;
                info.TextureId = VideoCounter;

                VideoCounter++;
                mFileList.add(info);
                mVideoList.add(i);
            }
        }

        //make the bitmap number to 10 (only if we have video)
        if(VideoCounter > 0) {
            //first, calc how many bitmap need from per video
            int num = (20 - BitmapCounter) / VideoCounter;
            for(int i=0; i<VideoCounter; i++) {
                for(int j=0; j<num; j++) {
                    FileInfo info = new FileInfo(this);
                    info.Path = mFileList.get(mVideoList.get(i)).Path;
                    info.Type = MicroMovieSurfaceView.INFO_TYPE_BITMAP;
                    info.TextureId = BitmapCounter;
                    info.IsFake = true;
                    info.CountId = mFileList.size();
                    int[] VId = new int[2];
                    VId[0] = BitmapCounter;
                    VId[1] = mFileList.size();

                    info.VOId = mFileList.get(mVideoList.get(i)).TextureId;
                    info.mDate = mFileList.get(mVideoList.get(i)).mDate;
                    mFileList.get(mVideoList.get(i)).VId.add(VId);

                    BitmapCounter++;
                    mFileList.add(info);
                }
            }
        }

        //--------------------------------------------
        int bitmapCount = 0;
        int videoCount = 0;
        for(int i=0; i<mFileList.size(); i++) {
            FileInfo info = mFileList.get(i);
            if(info.IsFake)
                Log.e(TAG, "Path:" + info.Path + ", Type:" + info.Type + ", Id:" + info.TextureId + ", Fake:" + info.IsFake + ", CountId:" + info.CountId);
            else
                Log.e(TAG, "Path:" + info.Path + ", Type:" + info.Type + ", Id:" + info.TextureId + ", Fake:" + info.IsFake + ", VId:" + info.VId.size() + ", CountId:" + info.CountId);

            if(info.Type == MicroMovieSurfaceView.INFO_TYPE_BITMAP && !info.IsFake) bitmapCount++;
            if(info.Type == MicroMovieSurfaceView.INFO_TYPE_VIDEO && !info.IsFake) videoCount++;
        }
        Log.e(TAG, "bitmapCount:" + bitmapCount + ", videoCount:" + videoCount);
        //--------------------------------------------/
    }
    */

    public void showPrograssDialog() {
        mProgressDialog = ProgressDialog.show(this, null, getResources().getString(R.string.loading));
    }

    public void dismissPrograssDialog() {
        mProgressDialog.dismiss();
    }

    private class UpdateListener implements MicroMovieListener {
        private boolean InProgress = false;
        @Override
        public void doUpdate(int Item) {
            switch(Item) {
                case MicroMovieListener.START_INIT_PROGRASSDIALOG:
                    if(!InProgress) {
                        showPrograssDialog();
                        InProgress = true;
                    }
                    break;
                case MicroMovieListener.STOP_INIT_PROGRASSDIALOG:
                    if(InProgress && isInitial && mIsLoadBitmapDone) {
                        dismissPrograssDialog();
                        InProgress = false;
                        if(!mIsPauseState) {
                            if(mFirstLoad) {
                                mFirstLoad = false;
                                if(!mIsDDS) {
                                    mThemeAdapter.showPlay();
                                    switchTheme();
                                } else if(mIsDDS) {
                                    switchTheme();
                                    mIsDDS = false;

                                    if(mIsThisActivitySaving){
                                        mIsSaving = false;
                                        mIsThisActivitySaving = false;
                                        mControlPanel.setDDSSave(mSaveCallback);
                                    }
                                }
                            }
                        }
                    }
                    break;
                case MicroMovieListener.FINISH_CHANGESURFACE:
                    Log.v(TAG, "FINISH_CHANGESURFACE");
                    break;

                case MicroMovieListener.FINISH_PLAY:
                    mControlPanel.finishPlay();
                    mControlPanel.setSeekBarEnable(false);
                    mControlButtom.setEnabled(true);
                    isPlaying = false;
                    break;
                case MicroMovieListener.UPDATE_THEME:
                    Log.e(TAG, "UPDATE_THEME");
                    switchTheme();
                    break;
            }
        }
    }



    public boolean isSaving(){
        return mIsSaving;
    }

    public void setPause(boolean pause) {
        isPause = pause;
    }

    private void showCancelSaveDialog(){
            AlertDialog.Builder builder = new AlertDialog.Builder(this, AsusThemeUtility.THEME_DIALOG_ALERT);
            builder.setTitle(R.string.micromovie_cancel_save_title);
            builder.setMessage(getString(R.string.micromovie_not_complete) + "\n"
            + getString(R.string.leave_dialog_content));
            builder.setCancelable(false);
            builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                    if(mSaveThread != null){
                        mSaveThread.interrupt();
                        mSaveThread = null;
                    }

                    resumeFromSave();
                 }
            });

            builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                   if(mEncodeAndMuxTest != null){
                    mEncodeAndMuxTest.resumeEncode();
                   }
                }
           });

            if(mEncodeAndMuxTest != null){
                mEncodeAndMuxTest.pauseEncode();
            }

            AlertDialog dialog = builder.create();
            dialog.show();
    }

    private void showEncodeExceptionDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AsusThemeUtility.THEME_DIALOG_ALERT);
        builder.setMessage(getString(R.string.transcoding_fail));
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                resumeFromSave();
             }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showStorageNotEnoughDialog(){
        if(isPlaying)
            mControlPanel.ControlPause(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this, AsusThemeUtility.THEME_DIALOG_ALERT);
        builder.setTitle(R.string.not_compelete);
        builder.setMessage(getString(R.string.no_storage_save_microfilm) + "\n"
                + getString(R.string.delete_redundant_content));
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                // do nothing
             }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void resumeFromSave(){
        mIsSaving = false;
        mIsThisActivitySaving = false;
        if(mSaveProgressDialog!=null && mSaveProgressDialog.isShowing()){
            mSaveProgressDialog.dismiss();
            mSaveProgressDialog = null;
        }
    }

    private void showRestartSaveDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AsusThemeUtility.THEME_DIALOG_ALERT);
        builder.setTitle(R.string.not_compelete);
        builder.setMessage(R.string.cancel_and_restart);
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                if(Util.getAvailableSpace() < MIN_STORAGE_NEED){
                    showStorageNotEnoughDialog();
                    //mIsSaving = false;
                }else{
                    SaveMicroMovie();
                }
             }
        });

        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //mIsSaving = false;
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showStopAnotherTranscodingDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, AsusThemeUtility.THEME_DIALOG_ALERT);
        builder.setTitle(R.string.micromovie_cancel_save_title);
        builder.setMessage(R.string.stop_another_transcoding);
        builder.setCancelable(false);
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
             @Override
             public void onClick(DialogInterface dialog, int which) {
                if(mSaveThread != null){
                    mSaveThread.interrupt();
                    mSaveThread = null;
                }
                resumeFromSave();

                mStopSaveDialog = null;
             }
        });

        builder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        mStopSaveDialog = builder.create();
        mStopSaveDialog.show();
    }

    public static void stopSaveThreadForOutside(){
        if(mSaveThread != null){
            mSaveThread.interrupt();
            mSaveThread = null;
        }
    }

    public static boolean isSavingForOutside(){
        return mIsSaving;
    }

    public int getItemCount() {
        return mItemCount;
    }

    public long getStartDate() {
        return mStartDate;
    }

    public long getEndDate() {
        return mEndDate;
    }

    public int getDuration() {
        return mMicroView.getDuration();
    }
}
