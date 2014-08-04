package com.s890510.microfilm;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Random;

import com.s890510.microfilm.script.Timer;
import com.s890510.microfilm.script.effects.Effect;

public class ElementInfo {
    private static final String TAG = "ElementInfo";
    public int Type;
    public int TextureId;
    public int time;

    public int Videopart;   //Which part of video in this movie, 0 ,1, 2.....
    public Effect effect;
    public Timer timer;
    public int InfoId = -1;
    public boolean isVideo = false;
    public long mDate;
    public ArrayList<String> mLocation;
    public ArrayList<long[]> mFaceRect = new ArrayList<long[]>();

    public FloatBuffer mVertices = null;
    public FloatBuffer mSTextureCoords = null; //square
    public FloatBuffer mCTextureCoords = null; //circle

    public float scaleW = 1.0f;
    public float scaleH = 1.0f;
    public float x = 1.0f;
    public float y = 1.0f;
    public float width = 0.0f;
    public float height = 0.0f;
    public float centerX = 0.5f;
    public float centerY = 0.5f;

    public int mFaceCount = 0;
    //0->left, 1->right, 2->top, 3->bottom
    public float[] mFBorder = new float[4];
    public ArrayList<int[]> mFaceRegion;
    public boolean mIsRestore = false;

    public void CalcTriangleVertices(ProcessGL processGL) {
        float mVisioRatio = (float)processGL.ScreenHeight/processGL.ScreenWidth;
        float mBitmapRatio = (float)height/width;

        float textW = 1.0f;
        float textH = 1.0f;
        float RatioScale = 1.0f;

        if(mVisioRatio == mBitmapRatio) {
            textW = scaleW;
            textH = scaleH;
        } else {
            if(mBitmapRatio > mVisioRatio) { //can't cut width, need to cut height
                RatioScale = (float)((mVisioRatio*width)/height);
                textW = 1.0f * scaleW;
                textH = RatioScale * scaleH;
            } else {
                RatioScale = (float)((height/mVisioRatio)/width);
                textW = RatioScale * scaleW;
                textH = 1.0f * scaleH;
            }
        }

        //Trans
        if(mFaceCount > 0 && !mIsRestore) {
            if((((mFBorder[1] - mFBorder[0])/width > textW) || ((mFBorder[3] - mFBorder[2])/height > textH)) && mFaceCount > 1) {
                //For here we need to get one face to be center
                //At first we remove too small face
                ArrayList<Long> mRect = new ArrayList<Long>();
                long Max_Rect = 0;

                //Calc all face rect
                for(int i=0; i<mFaceRect.size(); i++) {
                    long r = (mFaceRect.get(i)[1] - mFaceRect.get(i)[0])*(mFaceRect.get(i)[3] - mFaceRect.get(i)[2]);
                    if(r > Max_Rect)
                        Max_Rect = r;
                    mRect.add(r);
                }

                //if the face is smaller then 1/4 Max_Rect we remove it
                for(int i=0; i<mFaceRect.size(); i++) {
                    if(mRect.get(i) < Max_Rect/4) {
                        mRect.remove(i);
                        mFaceRect.remove(i);
                        i--;
                    }
                }

                //now we random a face to show
                Random mRandom = new Random(System.currentTimeMillis());
                long[] f = mFaceRect.get(mRandom.nextInt(mFaceRect.size()));

                centerX = ((f[0] + f[1])/2)/width;
                centerY = ((f[2] + f[3])/2)/height;
            } else {
                centerX = ((mFBorder[0] + mFBorder[1])/2)/width;
                centerY = ((mFBorder[2] + mFBorder[3])/2)/height;
            }

            if(centerX - textW/2 < 0) {
                centerX = centerX + Math.abs(centerX - textW/2);
            }
            if(centerX + textW/2 > 1) {
                centerX = centerX - Math.abs(centerX + textW/2 - 1);
            }
            if(centerY - textH/2 < 0) {
                centerY = centerY + Math.abs(centerY - textH/2);
            }
            if(centerY + textH/2 > 1) {
                centerY = centerY - Math.abs(centerY + textH/2 - 1);
            }
        }

        x = scaleW * processGL.ScreenRatio;
        y = scaleH;

        float[] mVerticesData = new float[]{
                -x, -y, 0.0f,
                 x, -y, 0.0f,
                -x,  y, 0.0f,
                 x,  y, 0.0f
        };

        float[] mTextCoordsData = new float[]{
                (centerX - textW/2), (centerY - textH/2),
                (centerX + textW/2), (centerY - textH/2),
                (centerX - textW/2), (centerY + textH/2),
                (centerX + textW/2), (centerY + textH/2)
        };

        //Calc. circle texture
        //circle texture need to make face detect more correct
        int vCount = 3 * 72;
        float angdegSpan = 360.0f/72;

        float[] textures = new float[vCount*2];

        int count = 0;
        int stCount = 0;

        for(float angdeg=0; Math.ceil(angdeg)<360; angdeg+=angdegSpan) {
            double angrad=Math.toRadians(angdeg);
            double angradNext=Math.toRadians(angdeg+angdegSpan);

            textures[stCount++] = centerX;
            textures[stCount++] = centerY;

            textures[stCount++] = (float)(centerX-(textW/2)/processGL.ScreenRatio*Math.sin(angrad));
            textures[stCount++] = (float)(centerY+(textH/2)*Math.cos(angrad));

            textures[stCount++] = (float)(centerX-(textW/2)/processGL.ScreenRatio*Math.sin(angradNext));
            textures[stCount++] = (float)(centerY+(textH/2)*Math.cos(angradNext));
        }

        if(mVertices != null)
            mVertices.clear();

        if(mSTextureCoords != null)
            mSTextureCoords.clear();

        mVertices = ByteBuffer.allocateDirect(
                mVerticesData.length * ProcessGL.FLOAT_SIZE_BYTES)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mVertices.put(mVerticesData).position(0);

        mSTextureCoords = ByteBuffer.allocateDirect(
                mTextCoordsData.length * ProcessGL.FLOAT_SIZE_BYTES)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mSTextureCoords.put(mTextCoordsData).position(0);

        mCTextureCoords = ByteBuffer.allocateDirect(
                textures.length * ProcessGL.FLOAT_SIZE_BYTES)
                    .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mCTextureCoords.put(textures).position(0);
    }
}
