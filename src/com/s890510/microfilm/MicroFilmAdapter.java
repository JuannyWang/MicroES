package com.s890510.microfilm;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MicroFilmAdapter extends BaseAdapter {
	private final String TAG = "MicroFilmAdapter";
	private ArrayList<MediaInfo> mMediaInfo;
	private Context mContext;

	public MicroFilmAdapter(Context context) {
		mContext = context;
		mMediaInfo = ((MediaPool)mContext).getMediaInfo();
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		((MediaPool)mContext).setCountId();
		mMediaInfo = ((MediaPool)mContext).getMediaInfo();
	}

	@Override
	public int getCount() {
		return mMediaInfo.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View mView;
        ImageView mImageView;
        if(convertView == null) {
            mView  = LayoutInflater.from(mContext).inflate(R.layout.asus_micromovie_edititem, null);
        }
        else {
            mView = convertView;
        }

        mView.setPadding(1, 1, 1, 1);

        mImageView = (ImageView) mView.findViewById(R.id.micromovie_item);
        
        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int edge = metrics.widthPixels / 5;

        mImageView.setImageBitmap(Bitmap.createScaledBitmap(mMediaInfo.get(position).getThumbNail(), edge, edge, false));
        
        ImageView mDeleteView = (ImageView) mView.findViewById(R.id.micromovie_item_delete);
        mDeleteView.setOnClickListener(getOnClickListener(position));
        mDeleteView.setTag(new Integer(position));
        
        return mView;
	}
	
	private OnClickListener getOnClickListener(final int position) {
        OnClickListener listener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Integer position = (Integer)v.getTag();
				Log.e(TAG, "position:" + position);
				
				mMediaInfo = ((MediaPool)mContext).getMediaInfo();
				mMediaInfo.get(position).Destory();
				mMediaInfo.remove(position);
				((MediaPool)mContext).removeInfo(position);
				notifyDataSetChanged();
			}
		};
		
		return listener;
	}
}
