package com.s890510.microfilm.draw;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.DateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.s890510.microfilm.ElementInfo;
import com.s890510.microfilm.MicroMovieActivity;
import com.s890510.microfilm.ProcessGL;
import com.s890510.microfilm.R;
import com.s890510.microfilm.script.Script;
import com.s890510.microfilm.script.effects.Effect;
import com.s890510.microfilm.shader.Shader;
import com.s890510.microfilm.util.Typefaces;

public class Slogan {
    private static final String TAG                               = "SloganPage";
    private static final int    TRIANGLE_VERTICES_DATA_POS_OFFSET = 0;
    private static final int    TRIANGLE_VERTICES_DATA_UV_OFFSET  = 3;

    private int                 mProgram;
    private int                 mPositionHandle;
    private int                 mTextureHandle;
    private int                 mMVPMatrixHandle;
    private int                 mSamplerHandle;
    private int                 mAlphaHandle;
    private FloatBuffer         mTriangleVertices;
    private float[]             mMVPMatrix                        = new float[16];
    private float[]             mModelMatrix                      = new float[16];
    private int                 mWidth;
    private int                 mHeight;
    private Bitmap              mBitmap;
    private Bitmap              mLogoWBitmap;
    private Bitmap              mLogoBBitmap;
    private Context             mContext;
    private Script              mScript;
    private MicroMovieActivity  mActivity;
    private ProcessGL           mProcessGL;

    public static int           SLOGAN_LINE                       = 1;
    public static int           SLOGAN_TEXT                       = 2;
    public static int           SLOGAN_DATE                       = 3;
    public static int           SLOGAN_ALL                        = 4;

    public Slogan(MicroMovieActivity activity, ProcessGL processGL) {
        mActivity = activity;
        mContext = mActivity.getApplicationContext();
        mProcessGL = processGL;
        CreateProgram();
        mLogoBBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.asus_micromovie_logo_b);
        mLogoWBitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.asus_micromovie_logo_w);
    }

    public void DrawRandar(float[] mViewMatrix, float[] mProjectionMatrix, int mTextureID, Script script, ElementInfo mElementInfo) {

        long mElapseTime;
        long timer = mElementInfo.timer.getElapse();
        Effect mEffect = mElementInfo.effect.getEffect(timer);

        if(mEffect == null)
            return;
        else
            mElapseTime = mElementInfo.effect.getElapseTime(timer);

        boolean transition = mEffect.getTransition(mElapseTime);
        float progress = mEffect.getProgressByElapse(mElapseTime);
        int mType = mEffect.getMaskType(mElapseTime);

        mScript = script;

        generateSlogan(progress, transition, mType, mElementInfo.effect.getShader());

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + mTextureID);
        mActivity.mLoadTexture.BindTexture(GLES20.GL_TEXTURE_2D, mTextureID);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);

        GLES20.glUseProgram(mProgram);

        GLES20.glUniform1i(mSamplerHandle, mTextureID);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + mTextureID);

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_POS_OFFSET);
        GLES20.glVertexAttribPointer(mPositionHandle, 3, GLES20.GL_FLOAT, false, 20, mTriangleVertices);
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        mTriangleVertices.position(TRIANGLE_VERTICES_DATA_UV_OFFSET);
        GLES20.glVertexAttribPointer(mTextureHandle, 3, GLES20.GL_FLOAT, false, 20, mTriangleVertices);
        GLES20.glEnableVertexAttribArray(mTextureHandle);

        Matrix.setIdentityM(mModelMatrix, 0);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        GLES20.glUniform1f(mAlphaHandle, mEffect.getAlpha(mElapseTime));

        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glBlendFuncSeparate(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA, GLES20.GL_ONE, GLES20.GL_ONE);

        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

        GLES20.glDisable(GLES20.GL_BLEND);

        checkGlError("SloganPage");
    }

    private void CreateProgram() {
        final int vertexShaderHandle = GLUtil.compileShader(GLES20.GL_VERTEX_SHADER, VertexShader());
        final int fragmentShaderHandle = GLUtil.compileShader(GLES20.GL_FRAGMENT_SHADER, FragmentShader());

        checkGlError("Sloganshader");
        // Create the new program
        mProgram = GLUtil.createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle);
        if(mProgram == 0) {
            Log.e(TAG, "mProgram is 0");
            return;
        }

        // Set program handles. These will later be used to pass in values to
        // the program.
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
        mTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        mSamplerHandle = GLES20.glGetUniformLocation(mProgram, "Texture");
        mAlphaHandle = GLES20.glGetUniformLocation(mProgram, "mAlpha");
        checkGlError("SloganCreateProgram");
    }

    private void CalcBKVertices() {
        float mRatio = mProcessGL.ScreenRatio;
        final float[] mTriangleVerticesData = {
                // X, Y, Z, U, V
                -mRatio, -1.0f, 0.0f, 0.0f, 0.0f, mRatio, -1.0f, 0.0f, 1.0f, 0.0f, -mRatio, 1.0f, 0.0f, 0.0f, 1.0f, mRatio, 1.0f, 0.0f, 1.0f, 1.0f };

        // Initialize the buffers.
        mTriangleVertices = ByteBuffer.allocateDirect(mTriangleVerticesData.length * ProcessGL.FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder())
                .asFloatBuffer();

        mTriangleVertices.put(mTriangleVerticesData).position(0);
    }

    private String VertexShader() {
        return "uniform mat4 uMVPMatrix;                       \n" + "attribute vec4 aPosition;                      \n"
                + "attribute vec4 aTextureCoord;                  \n" + "varying vec2 vTextureCoord;                    \n"
                + "void main() {                                  \n" + "    vTextureCoord = aTextureCoord.xy;          \n"
                + "    gl_Position = uMVPMatrix * aPosition;      \n" + "}                                              \n";
    }

    private String FragmentShader() {
        return "precision mediump float;                                    \n" + "uniform sampler2D Texture;                                  \n"
                + "uniform float mAlpha;                                       \n" + "varying vec2 vTextureCoord;                                 \n"
                + "void main() {                                               \n" + "    gl_FragColor = texture2D(Texture, vTextureCoord);       \n"
                + "    gl_FragColor.w = mAlpha;                                \n" + "}                                                           \n";
    }

    public void checkGlError(String op) {
        int error;
        while((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    private void DrawLine(Canvas mCanvas, int mType, boolean mTrans, float progress) {
        Paint mLinePaint = new Paint();

        int mAlpha = 255;
        if(mTrans) {
            mAlpha = (int) (mAlpha * progress);
        }

        if(mScript.GetSloganType() == 0) {
            mLinePaint.setARGB(mAlpha, 50, 50, 50);
        } else if(mScript.GetSloganType() == 1 || mScript.GetSloganType() == 3 || mScript.GetSloganType() == 4) {
            mLinePaint.setARGB(mAlpha, 209, 209, 209);
        } else if(mScript.GetSloganType() == 5) {
            mLinePaint.setARGB(mAlpha, 97, 61, 25);
        } else if(mScript.GetSloganType() == 6) {
            mLinePaint.setARGB(255, 209, 209, 209);
        }

        mLinePaint.setStyle(Paint.Style.FILL);
        int totalLine = (int) (mProcessGL.ScreenWidth * 0.66f);
        float LineHeight = (float) (mProcessGL.ScreenHeight / 720.0);
        if(LineHeight < 0.5f)
            LineHeight = 0.5f;

        if(mTrans) {
            if(mScript.GetSloganType() == 3) {
                mCanvas.drawRect(mProcessGL.ScreenWidth / 2 - totalLine / 2 * progress, mProcessGL.ScreenHeight / 2 - LineHeight,
                        mProcessGL.ScreenWidth / 2 + totalLine / 2 * progress, mProcessGL.ScreenHeight / 2 + LineHeight, mLinePaint);
            } else if(mScript.GetSloganType() == 2) {
                if(progress < 0.75) {
                    mCanvas.drawRect(mProcessGL.ScreenWidth * 0.17f, mProcessGL.ScreenHeight / 2 - LineHeight, mProcessGL.ScreenWidth * 0.17f
                            + totalLine * progress * 10 / 7.5f, mProcessGL.ScreenHeight / 2 + LineHeight, mLinePaint);
                } else {
                    mCanvas.drawRect(mProcessGL.ScreenWidth * 0.17f, mProcessGL.ScreenHeight / 2 - LineHeight, mProcessGL.ScreenWidth * 0.17f
                            + totalLine, mProcessGL.ScreenHeight / 2 + LineHeight, mLinePaint);
                }
            } else {
                mCanvas.drawRect(mProcessGL.ScreenWidth * 0.17f, mProcessGL.ScreenHeight / 2 - LineHeight, mProcessGL.ScreenWidth * 0.17f + totalLine
                        * progress, mProcessGL.ScreenHeight / 2 + LineHeight, mLinePaint);
            }
        } else {
            mCanvas.drawRect(mProcessGL.ScreenWidth * 0.17f, mProcessGL.ScreenHeight / 2 - LineHeight, mProcessGL.ScreenWidth * 0.17f + totalLine,
                    mProcessGL.ScreenHeight / 2 + LineHeight, mLinePaint);
        }
    }

    private void DrawText(Canvas mCanvas, int mType, boolean mTrans, float progress) {
        int xPos = mProcessGL.ScreenWidth / 2;
        Rect rect = new Rect();
        Paint mCTStringPaint = new Paint();
        Paint mMask = new Paint();
        Paint mLogoPaint = new Paint();

        float mTopFontSize = mProcessGL.ScreenWidth / 1280.0f * 50.0f;
        String mCTString = "Your Life Made Awesome";

        mCTStringPaint.setTextSize(mTopFontSize);
        mCTStringPaint.setTypeface(Typefaces.get(mContext, "fonts/Roboto-Light.ttf"));
        mCTStringPaint.setTextAlign(Align.CENTER);
        mCTStringPaint.setSubpixelText(true);
        mCTStringPaint.setAntiAlias(true);
        mCTStringPaint.setShader(null);
        mCTStringPaint.getTextBounds(mCTString, 0, mCTString.length(), rect);

        int mAlpha = 255;
        if(mTrans) {
            mAlpha = (int) (mAlpha * progress);
        }

        float gap = mProcessGL.ScreenWidth / 1280.0f * 20f;
        float LineHeight = (float) (mProcessGL.ScreenHeight / 720.0) * 2;

        if(mScript.GetSloganType() == 0 || mScript.GetSloganType() == 4) {
            mCTStringPaint.setARGB(mAlpha, 204, 204, 204);
            mLogoPaint.setAlpha(mAlpha);
            mMask.setARGB(255, 0, 0, 0);

            float scale = (((float) mProcessGL.ScreenHeight / 720f) * 24f) / mLogoWBitmap.getHeight();
            android.graphics.Matrix matrix = new android.graphics.Matrix();
            matrix.postScale(scale, scale);

            int bwidth = mLogoWBitmap.getWidth();
            int bheight = mLogoWBitmap.getHeight();

            Bitmap bmp = Bitmap.createBitmap(mLogoWBitmap, 0, 0, bwidth, bheight, matrix, true);
            mCanvas.drawBitmap(bmp, mProcessGL.ScreenWidth - bmp.getWidth() - gap, mProcessGL.ScreenHeight - bmp.getHeight() - gap, mLogoPaint);
        } else if(mScript.GetSloganType() == 1 || mScript.GetSloganType() == 3) {
            mCTStringPaint.setARGB(mAlpha, 51, 51, 51);
            mLogoPaint.setAlpha(mAlpha);
            mMask.setARGB(255, 255, 255, 255);

            float scale = (((float) mProcessGL.ScreenHeight / 720f) * 24f) / mLogoBBitmap.getHeight();
            android.graphics.Matrix matrix = new android.graphics.Matrix();
            matrix.postScale(scale, scale);

            int bwidth = mLogoWBitmap.getWidth();
            int bheight = mLogoWBitmap.getHeight();

            Bitmap bmp = Bitmap.createBitmap(mLogoBBitmap, 0, 0, bwidth, bheight, matrix, true);
            mCanvas.drawBitmap(bmp, mProcessGL.ScreenWidth - bmp.getWidth() - gap, mProcessGL.ScreenHeight - bmp.getHeight() - gap, mLogoPaint);
        } else if(mScript.GetSloganType() == 2) {
            mCTStringPaint.setARGB(mAlpha, 0, 0, 0);
            mLogoPaint.setAlpha(mAlpha);
            mMask.setARGB(255, 254, 228, 40);

            float scale = (((float) mProcessGL.ScreenHeight / 720f) * 24f) / mLogoBBitmap.getHeight();
            android.graphics.Matrix matrix = new android.graphics.Matrix();
            matrix.postScale(scale, scale);

            int bwidth = mLogoWBitmap.getWidth();
            int bheight = mLogoWBitmap.getHeight();

            Bitmap bmp = Bitmap.createBitmap(mLogoBBitmap, 0, 0, bwidth, bheight, matrix, true);
            mCanvas.drawBitmap(bmp, mProcessGL.ScreenWidth - bmp.getWidth() - gap, mProcessGL.ScreenHeight - bmp.getHeight() - gap, mLogoPaint);
        } else if(mScript.GetSloganType() == 5) {
            mCTStringPaint.setARGB(mAlpha, 97, 61, 25);
            mLogoPaint.setAlpha(mAlpha);
            mMask.setARGB(255, 0, 0, 0);

            float scale = (((float) mProcessGL.ScreenHeight / 720f) * 24f) / mLogoBBitmap.getHeight();
            android.graphics.Matrix matrix = new android.graphics.Matrix();
            matrix.postScale(scale, scale);

            int bwidth = mLogoWBitmap.getWidth();
            int bheight = mLogoWBitmap.getHeight();

            Bitmap bmp = Bitmap.createBitmap(mLogoBBitmap, 0, 0, bwidth, bheight, matrix, true);
            mCanvas.drawBitmap(bmp, mProcessGL.ScreenWidth - bmp.getWidth() - gap, mProcessGL.ScreenHeight - bmp.getHeight() - gap, mLogoPaint);
        } else if(mScript.GetSloganType() == 6) {
            mCTStringPaint.setARGB(255, 204, 204, 204);
            mLogoPaint.setAlpha(255);
            mMask.setARGB(255, 0, 0, 0);

            float scale = (((float) mProcessGL.ScreenHeight / 720f) * 24f) / mLogoWBitmap.getHeight();
            android.graphics.Matrix matrix = new android.graphics.Matrix();
            matrix.postScale(scale, scale);

            int bwidth = mLogoWBitmap.getWidth();
            int bheight = mLogoWBitmap.getHeight();

            Bitmap bmp = Bitmap.createBitmap(mLogoWBitmap, 0, 0, bwidth, bheight, matrix, true);
            mCanvas.drawBitmap(bmp, mProcessGL.ScreenWidth - bmp.getWidth() - gap, mProcessGL.ScreenHeight - bmp.getHeight() - gap, mLogoPaint);
        }

        // String "Your Life Made Awesome"
        if((mScript.GetSloganType() == 2 || mScript.GetSloganType() == 3) && mTrans) {
            mCanvas.drawText(mCTString, xPos, (int) ((mProcessGL.ScreenHeight / 2) + rect.height() * 1.5f - (rect.height() * 2 * progress)),
                    mCTStringPaint);

            mMask.setStyle(Paint.Style.FILL);
            mCanvas.drawRect(xPos - rect.width() / 2, mProcessGL.ScreenHeight / 2 + LineHeight, xPos + rect.width() / 2, mProcessGL.ScreenHeight / 2
                    + rect.height() * 2 + LineHeight, mMask);

            if(mScript.GetSloganType() == 2) {
                // Mask
                float StartX = mProcessGL.ScreenWidth * 0.17f;
                float Mask_length = mProcessGL.ScreenWidth * 0.66f;
                int mTextHeight = rect.height();
                int yPos = (int) ((mProcessGL.ScreenHeight / 2) - rect.height() / 2);

                // 1, 3
                // Green_1
                Paint mPaint_1 = new Paint();
                mPaint_1.setColor(Color.rgb(148, 182, 103));
                mPaint_1.setStyle(Paint.Style.FILL);

                if(progress <= 0.15) {
                    mCanvas.drawRect(StartX, yPos - mTextHeight, StartX + ((Mask_length * 1 / 2) * progress * 10 / 1.5f), yPos + mTextHeight * 1.5f,
                            mPaint_1);
                } else if(progress > 0.15 && progress <= 0.3) {
                    mCanvas.drawRect(StartX + ((Mask_length * 1 / 2) * (progress - 0.15f) * 10 / 1.5f), yPos - mTextHeight, StartX + Mask_length * 1
                            / 2 + ((Mask_length * 1 / 2) * (progress - 0.15f) * 10 / 1.5f), yPos + mTextHeight * 1.5f, mPaint_1);
                } else if(progress > 0.3 && progress <= 0.45)
                    mCanvas.drawRect(StartX + Mask_length * 1 / 2 + ((Mask_length * 1 / 2) * (progress - 0.3f) * 10 / 1.5f), yPos - mTextHeight,
                            StartX + Mask_length, yPos + mTextHeight * 1.5f, mPaint_1);

                // Green_2
                mPaint_1.setColor(Color.rgb(192, 217, 178));

                if(progress > 0.15 && progress <= 0.3) {
                    mCanvas.drawRect(StartX, yPos - mTextHeight, StartX + ((Mask_length * 1 / 2) * (progress - 0.15f) * 10 / 1.5f), yPos
                            + mTextHeight * 1.5f, mPaint_1);
                } else if(progress > 0.3 && progress <= 0.45) {
                    mCanvas.drawRect(StartX + ((Mask_length * 1 / 2) * (progress - 0.3f) * 10 / 1.5f), yPos - mTextHeight, StartX + Mask_length * 1
                            / 2 + ((Mask_length * 1 / 2) * (progress - 0.3f) * 10 / 1.5f), yPos + mTextHeight * 1.5f, mPaint_1);
                } else if(progress > 0.45 && progress <= 0.6) {
                    mCanvas.drawRect(StartX + Mask_length * 1 / 2 + ((Mask_length * 1 / 2) * (progress - 0.45f) * 10 / 1.5f), yPos - mTextHeight,
                            StartX + Mask_length, yPos + mTextHeight * 1.5f, mPaint_1);
                }

                // White
                mPaint_1.setColor(Color.WHITE);

                if(progress > 0.3 && progress <= 0.6) {
                    mCanvas.drawRect(StartX, yPos - mTextHeight, StartX + ((Mask_length) * (progress - 0.3f) * 10 / 3), yPos + mTextHeight * 1.5f,
                            mPaint_1);
                } else if(progress > 0.6 && progress <= 0.75) {
                    mCanvas.drawRect(StartX + ((Mask_length) * (progress - 0.6f) * 10 / 1.5f), yPos - mTextHeight, StartX + Mask_length, yPos
                            + mTextHeight * 1.5f, mPaint_1);
                }
            }
        } else {
            mCanvas.drawText(mCTString, xPos, (int) ((mProcessGL.ScreenHeight / 2) - rect.height() / 2), mCTStringPaint);
        }
    }

    private void DrawDate(Canvas mCanvas, int mType, boolean mTrans, float progress) {
        int xPos = mProcessGL.ScreenWidth / 2;
        Rect rect = new Rect();
        Paint mCBStringPaint = new Paint();

        int mAlpha = 255;
        if(mTrans) {
            mAlpha = (int) (mAlpha * progress);
        }

        if(mScript.GetSloganType() == 0 || mScript.GetSloganType() == 4) {
            mCBStringPaint.setARGB(mAlpha, 122, 122, 122);
        } else if(mScript.GetSloganType() == 1 || mScript.GetSloganType() == 3) {
            mCBStringPaint.setARGB(mAlpha, 92, 92, 92);
        } else if(mScript.GetSloganType() == 2) {
            mCBStringPaint.setARGB(mAlpha, 0, 0, 0);
        } else if(mScript.GetSloganType() == 5) {
            mCBStringPaint.setARGB(mAlpha, 97, 61, 25);
        } else if(mScript.GetSloganType() == 6) {
            mCBStringPaint.setARGB(mAlpha, 204, 204, 204);
        }

        float mBottomFontSize = mProcessGL.ScreenWidth / 1280.0f * 40.0f;
        mCBStringPaint.setTextSize(mBottomFontSize);
        mCBStringPaint.setTypeface(Typefaces.get(mContext, "fonts/Roboto-Light.ttf"));
        mCBStringPaint.setTextAlign(Align.CENTER);
        mCBStringPaint.setSubpixelText(true);
        mCBStringPaint.setAntiAlias(true);
        mCBStringPaint.setShader(null);

        DateFormat formater = android.text.format.DateFormat.getDateFormat(mContext);
        String date = formater.format(new Date(System.currentTimeMillis()));
        mCBStringPaint.getTextBounds(date, 0, date.length(), rect);
        mCanvas.drawText(date, xPos, (int) ((mProcessGL.ScreenHeight / 2) + rect.height() * 1.5), mCBStringPaint);
    }

    private void generateSlogan(float progress, boolean transition, int mType, String sType) {

        if(mBitmap == null) {
            mBitmap = Bitmap.createBitmap(mProcessGL.ScreenWidth, mProcessGL.ScreenHeight, Bitmap.Config.ARGB_8888);
        }

        Canvas mCanvas = new Canvas(mBitmap);

        if(mScript.GetSloganType() == 0) {
            mCanvas.drawARGB(255, 0, 0, 0);
        } else if(mScript.GetSloganType() == 3) {
            mCanvas.drawARGB(255, 255, 255, 255);
        } else if(mScript.GetSloganType() == 2) {
            mCanvas.drawARGB(255, 254, 228, 40);
        } else if(mScript.GetSloganType() == 4) {
            mCanvas.drawARGB(255, 56, 31, 53);
        } else if(mScript.GetSloganType() == 5) {
            mCanvas.drawARGB(255, 255, 219, 88);
        } else if(mScript.GetSloganType() == 1) {
            mCanvas.drawARGB(255, 230, 230, 230);
        } else if(mScript.GetSloganType() == 6) {
            mCanvas.drawARGB(255, 0, 102, 204);
        }

        if(sType.equals(Shader.Slogan_TypeA)) { // Line, Text, Date
            if(mType == SLOGAN_LINE) {
                DrawLine(mCanvas, mType, true, progress);
            } else if(mType == SLOGAN_TEXT) {
                DrawText(mCanvas, mType, true, progress);
                DrawLine(mCanvas, mType, false, progress);
            } else if(mType == SLOGAN_DATE) {
                DrawDate(mCanvas, mType, true, progress);
                DrawText(mCanvas, mType, false, progress);
                DrawLine(mCanvas, mType, false, progress);
            } else {
                DrawDate(mCanvas, mType, false, progress);
                DrawText(mCanvas, mType, false, progress);
                DrawLine(mCanvas, mType, false, progress);
            }
        } else if(sType == Shader.Slogan_TypeB) { // Text, Line, Date
            if(mType == SLOGAN_TEXT) {
                DrawLine(mCanvas, mType, true, progress);
                DrawText(mCanvas, mType, true, progress);
            } else if(mType == SLOGAN_DATE) {
                DrawDate(mCanvas, mType, true, progress);
                DrawText(mCanvas, mType, false, progress);
                DrawLine(mCanvas, mType, false, progress);
            } else {
                DrawDate(mCanvas, mType, false, progress);
                DrawText(mCanvas, mType, false, progress);
                DrawLine(mCanvas, mType, false, progress);
            }
        } else if(sType == Shader.Slogan_TypeC) { // ALL
            if(mType == SLOGAN_TEXT) {
                DrawText(mCanvas, mType, false, progress);
                DrawDate(mCanvas, mType, false, progress);
            } else if(mType == SLOGAN_LINE) {
                DrawLine(mCanvas, mType, true, progress);
                DrawText(mCanvas, mType, false, progress);
                DrawDate(mCanvas, mType, false, progress);
            } else {
                DrawDate(mCanvas, mType, false, progress);
                DrawText(mCanvas, mType, false, progress);
                DrawLine(mCanvas, mType, false, progress);
            }
        }
    }

    public void setScreen(int width, int height) {
        mWidth = width;
        mHeight = height;
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);

        CalcBKVertices();
    }
}
