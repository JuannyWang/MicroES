package com.s890510.microfilm;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.s890510.microfilm.util.Typefaces;

public class ThemeAdapter extends BaseAdapter {
    private String             TAG           = "ThemeAdapter";

    public static final int    TYPE_Count    = 9;
    public static final int    TYPE_KIDS     = 0;
    public static final int    TYPE_MEMORY   = 1;
    public static final int    TYPE_VINTAGE  = 2;
    public static final int    TYPE_ROMANCE  = 3;
    public static final int    TYPE_CARNIVAL = 4;
    public static final int    TYPE_CITY     = 5;
    public static final int    TYPE_LIFE     = 6;
    public static final int    TYPE_FASHION  = 7;
    public static final int    TYPE_SPORTS   = 8;

    private Context            mContext;
    private MicroMovieActivity mActivity;
    private int                ThemePosition = -1;
    private MicroMovieListener mUpdatelistener;
    private final Handler      mHandler;
    private View               mView         = null;
    public static final int    MSG_UPDATE    = 0;

    private int[]              mThemeImage   = { R.drawable.asus_micromovie_theme_01, R.drawable.asus_micromovie_theme_02,
            R.drawable.asus_micromovie_theme_03, R.drawable.asus_micromovie_theme_04, R.drawable.asus_micromovie_theme_05,
            R.drawable.asus_micromovie_theme_06, R.drawable.asus_micromovie_theme_07, R.drawable.asus_micromovie_theme_08,
            R.drawable.asus_micromovie_theme_09 };

    private int[]              mThemeName    = { R.string.kids, R.string.memory, R.string.country, R.string.lover, R.string.carnival, R.string.city,
            R.string.life, R.string.fashion, R.string.sports };

    public boolean[]           mThemeState   = { true, true, true, true, true, true, true, false, true };

    public int[]               mOrder        = { 0, 8, 1, 4, 2, 5, 3, 6, 7 };

    public ThemeAdapter(MicroMovieActivity activity) {
        mContext = activity.getApplicationContext();
        mActivity = activity;

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_UPDATE:
                        if(mUpdatelistener != null)
                            mUpdatelistener.doUpdate(MicroMovieListener.UPDATE_THEME);
                        break;
                }
            }
        };
    }

    public void ThemeInfo(int position, View convertView) {
        ImageView mImageView = (ImageView) convertView.findViewById(R.id.micromovie_theme_pic);
        mImageView.setImageResource(mThemeImage[mOrder[position]]);

        mImageView = (ImageView) convertView.findViewById(R.id.micromovie_theme_dot);
        mImageView.setImageResource(R.drawable.asus_micromovie_select_dot);
        mImageView.setVisibility(View.INVISIBLE);

        TextView mTextView = (TextView) convertView.findViewById(R.id.micromovie_theme_name);
        mTextView.setText(mThemeName[mOrder[position]]);
        mTextView.setTextSize(14);
        mTextView.setTypeface(Typefaces.get(mContext, "fonts/Roboto-Regular.ttf"));
    }

    public void setUpdateLintener(MicroMovieListener updateListener) {
        mUpdatelistener = updateListener;
    }

    @Override
    public int getCount() {
        return mThemeName.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View mView;
        ImageView mImageView;
        if(convertView == null) {
            mView = LayoutInflater.from(mContext).inflate(R.layout.asus_micromovie_theme, null);
        } else {
            mView = convertView;
            mImageView = (ImageView) mView.findViewById(R.id.micromovie_theme_pic);
            TextView mTextView = (TextView) convertView.findViewById(R.id.micromovie_theme_name);

            mImageView.setBackground(null);
            mTextView.setText(null);
            mView.setOnClickListener(null);
        }

        Log.e(TAG, "position:" + position);
        if(ThemePosition == -1) { // Set default to first one
            ThemePosition = mOrder[position];
            this.mView = mView;
        }
        mView.setOnClickListener(getOnClickListener(position));

        return mView;
    }

    public void showPlay() {
        showPlay(mView);
    }

    public void showPlay(View view) {
        ImageView mImageView;
        mImageView = (ImageView) view.findViewById(R.id.micromovie_theme_pic);
        mImageView.setBackgroundResource(R.drawable.asus_micromovie_select);

        mImageView = (ImageView) view.findViewById(R.id.micromovie_theme_dot);
        mImageView.setVisibility(View.VISIBLE);
        mView = view;
    }

    public void SendMSG(int msg) {
        Message m = new Message();
        m.what = msg;
        mHandler.sendMessage(m);
    }

    private OnClickListener getOnClickListener(final int position) {
        OnClickListener listener = new OnClickListener() {
            private int       mPosition = mOrder[position];
            private ImageView mImageView;

            @Override
            public void onClick(View v) {
                if(mActivity.checkInitial()) {
                    if(mView != null) {
                        mImageView = (ImageView) mView.findViewById(R.id.micromovie_theme_pic);
                        mImageView.setBackground(null);

                        mImageView = (ImageView) mView.findViewById(R.id.micromovie_theme_dot);
                        mImageView.setVisibility(View.INVISIBLE);
                    }

                    mImageView = (ImageView) v.findViewById(R.id.micromovie_theme_pic);
                    mImageView.setBackgroundResource(R.drawable.asus_micromovie_select);

                    mImageView = (ImageView) v.findViewById(R.id.micromovie_theme_dot);
                    mImageView.setVisibility(View.VISIBLE);
                    setThemePosition(mPosition);
                    SendMSG(MSG_UPDATE);
                    mView = v;
                    String ThemeType = null;
                    switch (mPosition) {
                        case ThemeAdapter.TYPE_KIDS:
                            ThemeType = "TYPE_KIDS";
                            break;
                        case ThemeAdapter.TYPE_MEMORY:
                            ThemeType = "TYPE_MEMORY";
                            break;
                        case ThemeAdapter.TYPE_VINTAGE:
                            ThemeType = "TYPE_VINTAGE";
                            break;
                        case ThemeAdapter.TYPE_ROMANCE:
                            ThemeType = "TYPE_ROMANCE";
                            break;
                        case ThemeAdapter.TYPE_CARNIVAL:
                            ThemeType = "TYPE_CARNIVAL";
                            break;
                        case ThemeAdapter.TYPE_LIFE:
                            ThemeType = "TYPE_LIFE";
                            break;
                        case ThemeAdapter.TYPE_SPORTS:
                            ThemeType = "TYPE_SPORTS";
                            break;
                        case ThemeAdapter.TYPE_CITY:
                            ThemeType = "TYPE_CITY";
                            break;
                    }
                }
            }
        };

        return listener;
    }

    public int getThemePosition() {
        return ThemePosition;
    }

    public void setThemePosition(int position) {
        ThemePosition = position;
    }

    public int getThemeNameRId(int position) {
        return mThemeName[position];
    }
}
