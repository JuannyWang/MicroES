package com.s890510.microfilm;

import java.util.ArrayList;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class ThemeLayout extends LinearLayout {
    private String TAG = "ThemeLayout";
    private boolean mFirst = false;
    View mView;
    ArrayList<View> mThemeList;

    public ThemeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mThemeList = new ArrayList<View>();
    }

    public void SetTheme(ThemeAdapter adapter) {
        int AdapterCount = adapter.getCount();
        Log.e(TAG, "AdapterCount:" + AdapterCount);

        for (int i = 0; i < AdapterCount; i++) {
            if(!adapter.mThemeState[adapter.mOrder[i]]) continue;

            View mView = this.getChildAt(i);
            if(mView != null) {
                mView = adapter.getView(i, mView, null);
            } else {
                mView = adapter.getView(i, null, null);
                if(!mFirst) {
                    mView.setPadding(0, 0, 0, 0);
                    mFirst = true;
                } else {
                    mView.setPadding(0, 8, 0, 0);
                }
                mView.setClickable(true);
                mThemeList.add(mView);
                this.setOrientation(VERTICAL);
                this.addView(mView, new LinearLayout.LayoutParams(
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                        android.view.ViewGroup.LayoutParams.WRAP_CONTENT));
            }

            adapter.ThemeInfo(i, mView);
        }
    }

    public View GetThemeView(int position, ThemeAdapter adapter) {
        //get the key
        int key = 0;
        for(int i=0; i<adapter.getCount(); i++) {
            if(!adapter.mThemeState[adapter.mOrder[i]]) continue;
            if(adapter.mOrder[i] == position) break;
            key++;
        }
        return this.getChildAt(key);
    }
}
