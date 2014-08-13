package com.s890510.microfilm;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class MicroFilmAdapter extends BaseAdapter {
    private ArrayList<MediaInfo> mMediaInfo;
    private Context              mContext;

    public MicroFilmAdapter(Context context) {
        mContext = context;
        mMediaInfo = ((MediaPool) mContext).getMediaInfo();
    }

<<<<<<< HEAD
    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        mMediaInfo = ((MediaPool) mContext).getMediaInfo();
    }

    @Override
    public int getCount() {
        return mMediaInfo.size();
    }
=======
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mMediaInfo.size();
	}
>>>>>>> parent of 5342a45... Remove Fileinfo and adjust several thing

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
            mView = LayoutInflater.from(mContext).inflate(R.layout.asus_micromovie_edititem, null);
        } else {
            mView = convertView;
        }

        mView.setPadding(1, 1, 1, 1);
        mImageView = (ImageView) mView.findViewById(R.id.micromovie_item);

        DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
        int edge = metrics.widthPixels / 4;
        
        mImageView.setImageBitmap(Bitmap.createScaledBitmap(mMediaInfo.get(position).getThumbNail(), edge, edge, false));

        return mView;
    }
}
