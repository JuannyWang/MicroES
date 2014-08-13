package com.s890510.microfilm;

import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.ExifInterface;
import android.media.FaceDetector;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;

import com.s890510.microfilm.util.Future;
import com.s890510.microfilm.util.FutureListener;
import com.s890510.microfilm.util.ThreadPool.Job;
import com.s890510.microfilm.util.ThreadPool.JobContext;

public class MediaInfo {
	private final String TAG = "MediaInfo";
	private static final boolean FACE_DEBUG = false;

	public static final int MEDIA_TYPE_IMAGE = 1;
	public static final int MEDIA_TYPE_VIDEO = 2;
	public static final int MEDIA_TYPE_UNKNOW = 3;

	private Activity mActivity;
	private LoadControl mLoadControl;
	private Uri mUri;
	private int mType;
	private String mPath;
	private int mRotate = 0;
	private long mDate;
	private float[] mLatLong = new float[2];
	private boolean mHaveLatLong = false;
	private boolean mIsUTC = false;
	private Bitmap mThumbNail;
	private Bitmap mBitmap;
	private int mThimbNailEdge = 240;
	
	private Future<Bitmap> mBitmapLookupJob;
	private final Handler mHandler;
	private final int MAX_FACES = 10;
	
	public boolean mIsInitial = false;
	public GeoInfo mGeoInfo = null;
	public int TextureId;
    public int CountId;
	public float x;
    public float y;
	
	public int mFaceCount = 0;
    public float[] mFBorder = {-1, -1, -1, -1}; //0->left, 1->right, 2->top, 3->bottom
    public ArrayList<long[]> mFaceRect = new ArrayList<long[]>();
	
	public MediaInfo(Activity activity) {
		mActivity = activity;
		mHandler = new Handler(Looper.getMainLooper());
	}
	
	public void setup(Uri uri, LoadControl lControl) {
		mUri = uri;
		mPath = getRealPath(uri);
		mLoadControl = lControl;
		
		String[] mSplit = getMimeType(uri).split("/");
		if(mSplit[0].equals("image")) mType =  MEDIA_TYPE_IMAGE;
		else if(mSplit[0].equals("video")) mType = MEDIA_TYPE_VIDEO;
		else mType = MEDIA_TYPE_UNKNOW;

		try {
			ExifInterface mExif = new ExifInterface(mPath);
			int result = mExif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED); 
			mRotate = 0; 
            switch(result) { 
	            case ExifInterface.ORIENTATION_ROTATE_90: 
	            	mRotate = 90; 
	                break; 
	            case ExifInterface.ORIENTATION_ROTATE_180: 
	            	mRotate = 180; 
	                break; 
	            case ExifInterface.ORIENTATION_ROTATE_270: 
	            	mRotate = 270; 
	                break; 
	            default: 
	                break; 
            }
            
            
            mDate=getTime(uri, mExif.getAttribute(ExifInterface.TAG_DATETIME));
            
            if(mExif.getLatLong(mLatLong))
            	mHaveLatLong = true;
            
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		LoadBitmap();
	}
	
	public boolean IsUTC() {
		return mIsUTC;
	}
	
	public boolean HaveLatLong() {
		return mHaveLatLong;
	}
	
	public long getDate() {
		return mDate;
	}
	
	public Uri getUri() {
		return mUri;
	}
	
	public String getPath() {
		return mPath;
	}
	
	public int getType() {
		return mType;
	}
	
	public int getRotate() {
		return mRotate;
	}
	
	public double getLatitude() {
		if(mHaveLatLong)
			return mLatLong[0];
		else
			return 99999;
	}
	
	public double getLongitude() {
		if(mHaveLatLong)
			return mLatLong[1];
		else
			return 99999;
	}
	
	public Bitmap getThumbNail() {
		return mThumbNail;
	}
	
	public Bitmap getImage() {
		return mBitmap;
	}
	
	public void setImage(Bitmap bitmap) {
		mBitmap = bitmap;
	}

	private String getMimeType(Uri uri) {
        Cursor cursor = mActivity.getApplicationContext().getContentResolver().query(uri,
        		new String[] { MediaStore.MediaColumns.MIME_TYPE }, null, null, null);

        if (cursor != null && cursor.moveToNext()) {
            return cursor.getString(0);
        }

        return null;
    }
    
    private String getRealPath(Uri uri) {
        Cursor cursor = mActivity.getApplicationContext().getContentResolver().query(uri,
        		new String[] { MediaStore.Images.Media.DATA }, null, null, null);
        
        if (cursor != null && cursor.moveToNext()) {
            return cursor.getString(0);
        }
        
        return null;
    }

    private long getTime(Uri uri) {
    	Cursor cursor = mActivity.getApplicationContext().getContentResolver().query(uri,
        		new String[] { MediaStore.Images.Media.DATE_TAKEN }, null, null, null);
        
        if (cursor != null && cursor.moveToNext()) {
            return (long)cursor.getLong(0);
        }
        
        return -1;
    }
    
    private long getTime(Uri uri, String datetime) {
    	if(datetime == null) return getTime(uri);

    	SimpleDateFormat sFormatter = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
    	sFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));

    	ParsePosition pos = new ParsePosition(0);
        try {
            Date mDate = sFormatter.parse(datetime, pos);
            if(mDate == null) {
            	return getTime(uri);
            } else {
            	mIsUTC = true;
            	return mDate.getTime();
            }
        } catch (Exception e) {
        	return getTime(uri);
        }
    }
    
    public void LoadBitmap() {
    	final MediaInfo mInfo = this;
        mBitmapLookupJob = ((MediaPool)mActivity.getApplicationContext()).getBitmapThreadPool().submit(
            new LoadBitmapJob(),
            new FutureListener<Bitmap>() {
                @Override
                public void onFutureDone(final Future<Bitmap> future) {
                    mBitmapLookupJob = null;
                    if (!future.isCancelled()) {
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mBitmap = future.get();
                                if(mBitmap != null) {
                                	Log.e(TAG, "Bitmap is not null");
                                    mIsInitial = true;
                                    mThumbNail = ThumbnailUtils.extractThumbnail(mBitmap, mThimbNailEdge, mThimbNailEdge);
                                    Log.e(TAG, "loadTexture, width:" + mBitmap.getWidth() + ", height:" + mBitmap.getHeight());
                                }
                                mLoadControl.DoneLoadBitmap(mInfo);
                            }
                        });
                    }
                }
            });
    }

    public void onDestory() {
        if(mBitmapLookupJob != null && !mBitmapLookupJob.isDone()) {
            mBitmapLookupJob.cancel();
        }
    }

    private class LoadBitmapJob implements Job<Bitmap> {
        private int width = MicroMovieActivity.mVisioWidth;
        private int height = MicroMovieActivity.mVisioHeight;

        protected LoadBitmapJob() {

        }

        private void LoadFaceInfo(Bitmap bitmap) {
            try {
                Class.forName("android.media.FaceDetector");
                boolean IsLarge = (bitmap.getHeight() > 640 || bitmap.getWidth() > 640) ? true : false;
                float scale = 1;
                Bitmap mTmpImage = null;

                if(IsLarge) {
                    scale = (bitmap.getHeight() > bitmap.getWidth()) ? (float)640/bitmap.getHeight() : (float)640/bitmap.getWidth();

                    Matrix matrix = new Matrix();
                    matrix.postScale(scale, scale);

                    mTmpImage = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                } else {
                    mTmpImage = Bitmap.createBitmap(bitmap);
                }


                BitmapFactory.Options bitmap_options = new BitmapFactory.Options();
                bitmap_options.inPreferredConfig = Bitmap.Config.RGB_565;
                mTmpImage = mTmpImage.copy(Bitmap.Config.RGB_565, false);

                FaceDetector mFaceDetect = new FaceDetector(mTmpImage.getWidth(), mTmpImage.getHeight(), MAX_FACES);
                FaceDetector.Face[] faces = new FaceDetector.Face[MAX_FACES];
                mFaceCount = mFaceDetect.findFaces(mTmpImage, faces);

                scale = 1/scale;

                for (int i=0; i<mFaceCount; i++) {
                    PointF mPoint = new PointF();
                    FaceDetector.Face face = faces[i];
                    face.getMidPoint(mPoint);

                    mPoint.x = mPoint.x * scale;
                    mPoint.y = mPoint.y * scale;
                    float EDist = face.eyesDistance() * scale;

                    long top = (long) (mPoint.y - EDist);
                    long left = (long) (mPoint.x - EDist);
                    long bottom = (long) (mPoint.y + EDist);
                    long right = (long) (mPoint.x + EDist);
                    if(top < 0) top = 0;
                    if(left < 0) left = 0;
                    if(bottom > bitmap.getHeight()) bottom = bitmap.getHeight();
                    if(right > bitmap.getWidth()) right = bitmap.getWidth();

                    if(left < mFBorder[0] || mFBorder[0] == -1) mFBorder[0] = left;
                    if(right > mFBorder[1] || mFBorder[1] == -1) mFBorder[1] = right;
                    if(top < mFBorder[2] || mFBorder[2] == -1) mFBorder[2] = top;
                    if(bottom > mFBorder[3] || mFBorder[3] == -1) mFBorder[3] = bottom;

                    if(FACE_DEBUG) {
                        Paint myPaint = new Paint();
                        myPaint.setColor(Color.GREEN);
                        myPaint.setStyle(Paint.Style.STROKE);
                        myPaint.setStrokeWidth(3);

                        Canvas canvasTemp = new Canvas(bitmap);
                        canvasTemp.drawRect(mPoint.x - EDist, mPoint.y - EDist, mPoint.x + EDist, mPoint.y + EDist, myPaint);
                    }

                    mFaceRect.add(new long[]{(long) (mPoint.x - EDist), (long) (mPoint.x + EDist), (long) (mPoint.y - EDist), (long) (mPoint.y + EDist)});
                }

                mTmpImage.recycle();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public Bitmap run(JobContext jc) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            //We first get the bitmap width and height
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(mPath, options);

            options.inSampleSize = calculateInSampleSize(options);
            options.inJustDecodeBounds=false;
            options.inDither=false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inPurgeable = true;
            options.inPreferQualityOverSpeed = true;
            options.inInputShareable = true;

            try {
                if(FACE_DEBUG)
                    bitmap = BitmapFactory.decodeFile(mPath, options).copy(Bitmap.Config.ARGB_8888, true);
                else
                    bitmap = BitmapFactory.decodeFile(mPath, options);

                if(mRotate > 0) { //check this bitmap need rotate?
                    Matrix mtx = new Matrix();
                    mtx.postRotate(mRotate);

                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), mtx, true);
                }

                //And now we scale it second times
                int bwidth = bitmap.getWidth();
                int bheight = bitmap.getHeight();

                if (Math.max(bheight, bwidth) > Math.max(height, width)) {
                    float scale = (float)Math.max(height, width)/(float)Math.max(bheight, bwidth);

                    Matrix matrix = new Matrix();
                    matrix.postScale(scale, scale);

                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bwidth, bheight, matrix, true);
                }

                if(bitmap != null) {
                    LoadFaceInfo(bitmap);
                }

                return bitmap;
            } catch(Exception e) {
                e.printStackTrace();

                if(bitmap != null) {
                    bitmap.recycle();
                    bitmap = null;
                }

                return null;
            }
        }

        private int calculateInSampleSize(BitmapFactory.Options options) {
            final int bwidth = options.outWidth;
            final int bheight = options.outHeight;
            int inSampleSize = 1;

            //We do first time scale
            if (Math.max(bheight, bwidth) > Math.max(height, width)) {
                int maxEdge = Math.max(width, height);
                inSampleSize = (int)((float)Math.max(bwidth, bheight)/(float)maxEdge);
            }

            return inSampleSize;
        }
    }
}
