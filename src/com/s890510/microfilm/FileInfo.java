package com.s890510.microfilm;

import java.util.ArrayList;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.media.FaceDetector;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.s890510.microfilm.util.Future;
import com.s890510.microfilm.util.FutureListener;
import com.s890510.microfilm.util.ThreadPool.Job;
import com.s890510.microfilm.util.ThreadPool.JobContext;

public class FileInfo {
    private static final String TAG = "FileInfo";
    private static final boolean FACE_DEBUG = false;
    public String Path;
    public String PathString;
    public int Type;
    public int TextureId;
    public int CountId;
    public boolean IsFake = false;
    public int Rotate;

    public float x;
    public float y;

    public int mFaceCount = 0;
    //0->left, 1->right, 2->top, 3->bottom
    public float[] mFBorder = {-1, -1, -1, -1};
    public ArrayList<long[]> mFaceRect = new ArrayList<long[]>();

    //Use for movie
    public int Count = 0;
    public ArrayList<int[]> VId = new ArrayList<int[]>();
    public int VOId = 0;    //Video original Id

    //For Save Bitmap
    public Bitmap mBitmap = null;
    public int mBWidth = 0;
    public int mBHeight = 0;

    public boolean mIsInitial = false;

    public GeoInfo mGeoInfo = null;
    public long mDate;

    private MicroFilmActivity mActivity;
    private Future<Bitmap> mBitmapLookupJob;
    private final Handler mHandler;
    private final int MAX_FACES = 10;

    public FileInfo(MicroFilmActivity activity) {
        mHandler = new Handler(Looper.getMainLooper());
        mActivity = activity;
    }

    public void LoadBitmap() {
        mBitmapLookupJob = mActivity.getBitmapThreadPool().submit(
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
                                    mIsInitial = true;
                                    mBWidth = mBitmap.getWidth();
                                    mBHeight = mBitmap.getHeight();
                                    Log.e("FileInfo", "loadTexture, width:" + mBitmap.getWidth() + ", height:" + mBitmap.getHeight());
                                }
                                mActivity.DoneLoadBitmap();
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
        private int width = mActivity.mVisioWidth;
        private int height = mActivity.mVisioHeight;

        protected LoadBitmapJob() {

        }

        /*
        private void getFaceInfo(Cursor mFaceCursor, Bitmap bitmap) {
            float[] tmp = new float[2];
            int index = mFaceCursor.getColumnIndex(FaceImageMap.COLUMN_TOP);
            long top = mFaceCursor.getLong(index);
            index = mFaceCursor.getColumnIndex(FaceImageMap.COLUMN_LEFT);
            long left = mFaceCursor.getLong(index);
            index = mFaceCursor.getColumnIndex(FaceImageMap.COLUMN_BOTTOM);
            long bottom = mFaceCursor.getLong(index);
            index = mFaceCursor.getColumnIndex(FaceImageMap.COLUMN_RIGHT);
            long right = mFaceCursor.getLong(index);

            //For rotate we should trans face position
            if(Rotate != 0) {
                int width, height;
                if(Rotate == 90 || Rotate == 270) {
                    width = (bitmap.getHeight() > bitmap.getWidth()) ?
                            FaceUtils.TYPE_THUMBNAIL_MAX_LENGTH : (int)(bitmap.getHeight()*((float)FaceUtils.TYPE_THUMBNAIL_MAX_LENGTH/(float)bitmap.getWidth()));
                    height = (bitmap.getWidth() > bitmap.getHeight()) ?
                            FaceUtils.TYPE_THUMBNAIL_MAX_LENGTH : (int)(bitmap.getWidth()*((float)FaceUtils.TYPE_THUMBNAIL_MAX_LENGTH/(float)bitmap.getHeight()));
                } else {
                    height = (bitmap.getHeight() > bitmap.getWidth()) ?
                            FaceUtils.TYPE_THUMBNAIL_MAX_LENGTH : (int)(bitmap.getHeight()*((float)FaceUtils.TYPE_THUMBNAIL_MAX_LENGTH/(float)bitmap.getWidth()));
                    width = (bitmap.getWidth() > bitmap.getHeight()) ?
                            FaceUtils.TYPE_THUMBNAIL_MAX_LENGTH : (int)(bitmap.getWidth()*((float)FaceUtils.TYPE_THUMBNAIL_MAX_LENGTH/(float)bitmap.getHeight()));
                }

                if(Rotate == 90 || Rotate == -270) {
                    long mRect = top;
                    top = left;
                    left = (height - bottom);
                    bottom = right;
                    right = height - mRect;
                } else if(Rotate == 180 || Rotate == -180) {
                    long mRect = top;
                    top = (height - bottom);
                    bottom = (height - mRect);

                    mRect = left;
                    left = (width - right);
                    right = (width - mRect);
                } else if(Rotate == 270 || Rotate == -90) {
                    long mRect = top;
                    top = (width - right);
                    right = bottom;
                    bottom = (width - left);
                    left = mRect;
                }
            }

            float maxLength = Math.max(bitmap.getWidth(), bitmap.getHeight());
            if(maxLength > FaceUtils.TYPE_THUMBNAIL_MAX_LENGTH) {
                float fscale = (float) maxLength/FaceUtils.TYPE_THUMBNAIL_MAX_LENGTH;
                top = (long) (fscale*top);
                left = (long) (fscale*left);
                bottom = (long) (fscale*bottom);
                right = (long) (fscale*right);
            }

            if(left < mFBorder[0] || mFBorder[0] == -1) mFBorder[0] = left;
            if(right > mFBorder[1] || mFBorder[1] == -1) mFBorder[1] = right;
            if(top < mFBorder[2] || mFBorder[2] == -1) mFBorder[2] = top;
            if(bottom > mFBorder[3] || mFBorder[3] == -1) mFBorder[3] = bottom;

            //Draw face block
            if(FACE_DEBUG) {
                Paint myPaint = new Paint();
                myPaint.setColor(Color.GREEN);
                myPaint.setStyle(Paint.Style.STROKE);
                myPaint.setStrokeWidth(3);

                Canvas canvasTemp = new Canvas(bitmap);
                canvasTemp.drawRect(left, top, right, bottom, myPaint);
            }

            mFaceRect.add(new long[]{left, right, top, bottom});
        }
        */

        private void LoadFaceInfo(Bitmap bitmap) {
        	/*
            if(!mActivity.getEPhotoApp().getEPhotoStampManager().isNeedDetectedByPath(com.asus.gallery.data.Path.fromString(PathString)) && FaceUtils.IsSoFileExist()) {
                Cursor mFaceCursor = null;
                try{
                    mFaceCursor = mActivity.getEPhotoApp().getEPhotoStampManager().getFaceInfoByImagePath(com.asus.gallery.data.Path.fromString(PathString));
                    if(mFaceCursor != null && mFaceCursor.getCount() > 0) {
                        mFaceCount = mFaceCursor.getCount();

                        for(int i=0; i<mFaceCursor.getCount(); i++) {
                            if(i == 0) {
                                if(mFaceCursor.moveToFirst()) {
                                    getFaceInfo(mFaceCursor, bitmap);
                                } else {
                                    break;
                                }
                            } else {
                                if(mFaceCursor.moveToNext()) {
                                    getFaceInfo(mFaceCursor, bitmap);
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                }finally{
                    if(mFaceCursor != null)
                        mFaceCursor.close();
                }
            } else { //We use google face detect api
            */
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
            //}
        }

        @Override
        public Bitmap run(JobContext jc) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            //We first get the bitmap width and height
            options.inJustDecodeBounds = true;
            Bitmap bitmap = BitmapFactory.decodeFile(Path, options);

            options.inSampleSize = calculateInSampleSize(options);
            options.inJustDecodeBounds=false;
            options.inDither=false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            options.inPurgeable = true;
            options.inPreferQualityOverSpeed = true;
            options.inInputShareable = true;

            try {
                if(FACE_DEBUG)
                    bitmap = BitmapFactory.decodeFile(Path, options).copy(Bitmap.Config.ARGB_8888, true);
                else
                    bitmap = BitmapFactory.decodeFile(Path, options);

                if(Rotate > 0) { //check this bitmap need rotate?
                    Matrix mtx = new Matrix();
                    mtx.postRotate(Rotate);

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
