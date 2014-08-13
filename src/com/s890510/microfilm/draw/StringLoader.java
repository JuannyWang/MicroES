package com.s890510.microfilm.draw;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.opengl.GLES20;
import android.opengl.GLUtils;

import com.s890510.microfilm.ElementInfo;
import com.s890510.microfilm.MicroMovieActivity;
import com.s890510.microfilm.ProcessGL;
import com.s890510.microfilm.R;
import com.s890510.microfilm.util.Typefaces;
import com.s890510.microfilm.util.Easing;
import com.s890510.microfilm.util.WeatherQuery;

public class StringLoader {
    private static final String TAG                         = "StringLoader";
    private Context             mContext;

    private Bitmap              mBitmap;
    private ArrayList<String>   mString;
    private ArrayList<String>   mString_Past;
    private int                 LType                       = 0;
    public FloatBuffer          mStringVertices;
    public FloatBuffer          mStringTextureCoords;
    private ProcessGL           mProcessGL;
    private MicroMovieActivity  mActivity;

    public static int           STRING_NONE                 = 0;
    public static int           STRING_NOBK                 = 1;             // String
                                                                              // Alpha
                                                                              // =
                                                                              // 1
    public static int           STRING_BK                   = 2;             // String
                                                                              // Alpha
                                                                              // =
                                                                              // 0
    public static int           STRING_FADE                 = 3;             // FadeIN,
                                                                              // FadeOut
    public static int           STRING_TYPE                 = 4;             // Typewriter
    public static int           STRING_ANIM_TD              = 5;             // Top,
                                                                              // Bottom
                                                                              // translate
                                                                              // animation
    public static int           STRING_ANIM_LR              = 6;             // Left,
                                                                              // Right
                                                                              // translate
                                                                              // animation
    public static int           STRING_LATE_SCALE           = 7;             // Late
                                                                              // show
                                                                              // and
                                                                              // scale
    public static int           STRING_MASK                 = 8;             // Mask
                                                                              // on
                                                                              // string
                                                                              // when
                                                                              // start
    public static int           STRING_SHADER               = 9;             // Gradient
                                                                              // color
    public static int           STRING_WHITE                = 10;            //
    public static int           STRING_WHITE_NOBK           = 11;            //
    public static int           STRING_WHITE_NOBK_ANIM      = 12;            //
    public static int           STRING_BLUE                 = 13;            //
    public static int           STRING_WHITE_NOBK_GEO       = 14;            //
    public static int           STRING_DATE_CITY            = 15;            //
    public static int           STRING_DATE_CITY_TRANS      = 16;            //
    public static int           STRING_YEAR_COUNTRY         = 17;            //
    public static int           STRING_YEAR_COUNTRY_FADEIN  = 18;            //
    public static int           STRING_YEAR_COUNTRY_FADEOUT = 19;            //
    public static int           STRING_BK_SCALE             = 20;            // String
                                                                              // Alpha
                                                                              // =
                                                                              // 0,
                                                                              // scale
    public static int           STRING_NOBK_SCALE           = 21;            // String
                                                                              // Alpha
                                                                              // =
                                                                              // 1
    public static int           STRING_FADE_LIGHT           = 22;
    public static int           STRING_WHITE_NOBK_LINE      = 23;
    public static int           STRING_WHITE_NOBK_LOVER     = 24;
    public static int           STRING_DATE_LOVER           = 25;
    public static int           STRING_ANIM_CITY_BLUE       = 26;
    public static int           STRING_ANIM_CITY_TRANS      = 27;
    public static int           STRING_ANIM_CITY_BLACK      = 28;
    public static int           STRING_KIDS_END             = 29;
    public static int           STRING_KIDS_CIRCLE_DATE     = 30;
    public static int           STRING_KIDS_ICON_A          = 31;
    public static int           STRING_KIDS_ICON_B          = 32;

    public StringLoader(MicroMovieActivity activity, ProcessGL processGL) {
        mActivity = activity;
        mContext = mActivity.getApplicationContext();
        mProcessGL = processGL;
    }

    public boolean BindTexture(int mTextureId, ElementInfo mElementInfo) {
        long timer = mElementInfo.timer.getElapse();
        int mCount = mElementInfo.effect.getCount(timer);
        int mType = mElementInfo.effect.getMaskType(timer);

        if(mType == STRING_NONE)
            return false;

        ArrayList<String> string = null;
        if(mType == STRING_WHITE_NOBK_GEO) {
            if(mElementInfo.mLocation != null) {
                string = mElementInfo.mLocation;
            } else {
                return false;
            }
        } else if((mType == STRING_ANIM_CITY_BLUE || mType == STRING_ANIM_CITY_TRANS || mType == STRING_ANIM_CITY_BLACK)
                && mElementInfo.effect.getString() == null) {
            if(mProcessGL.getFirstLocation() != null) {
                string = new ArrayList<String>();
                string.add(mProcessGL.getFirstLocation());
            } else {
                string = new ArrayList<String>();
                string.add("My Journey");
            }
        } else if(mType == STRING_DATE_CITY || mType == STRING_DATE_CITY_TRANS) {
            if(mElementInfo.mDate != 0) {
                DateFormat formater = new SimpleDateFormat("MMM.yyyy", Locale.ENGLISH);
                string = new ArrayList<String>();
                string.add(formater.format(new Date(mElementInfo.mDate)));
            } else {
                return false;
            }
        } else if(mType == STRING_KIDS_CIRCLE_DATE || mType == STRING_KIDS_ICON_A || mType == STRING_KIDS_ICON_B) {
            if(mElementInfo.mDate != 0) {
                DateFormat formater = new SimpleDateFormat("MMMM dd", Locale.ENGLISH);
                string = new ArrayList<String>();
                string.add(formater.format(new Date(mElementInfo.mDate)));
            } else {
                return false;
            }
        } else if(mType == STRING_DATE_LOVER) {
            if(mElementInfo.mDate != 0) {
                DateFormat formater = new SimpleDateFormat("d.MMM.yyyy", Locale.ENGLISH);
                string = new ArrayList<String>();
                string.add(formater.format(new Date(mElementInfo.mDate)));
            } else {
                return false;
            }
        } else if(mType == STRING_WHITE_NOBK_LINE) {
            string = new ArrayList<String>();
            string.add("");
        } else {
            string = mElementInfo.effect.getString();
        }

        if(LType == STRING_DATE_CITY && mType == STRING_DATE_CITY_TRANS) {
            mString_Past = mString;
        } else if(mType != STRING_DATE_CITY_TRANS && mType != STRING_DATE_CITY) {
            mString_Past = null;
        }

        if(mBitmap == null || mCount != 0 || mProcessGL.mSpecialHash != mBitmap.hashCode() || !mString.equals(string) || LType != mType) {
            mString = string;
            generateString(mElementInfo.effect.getDuration(timer), mElementInfo.timer.getElapse(), mElementInfo.effect.getProgressByElapse(timer),
                    mType, mCount, mElementInfo.effect.getTextSize(timer), mElementInfo);
            mProcessGL.setSpecialHash(mBitmap.hashCode());
        }

        // Bind Texture
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + mTextureId);
        mActivity.mLoadTexture.BindTexture(GLES20.GL_TEXTURE_2D, mTextureId);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, mBitmap, 0);

        LType = mType;

        return true;
    }

    private void generateString(int duration, long elapse, float progress, int mType, int mCount, float mTextSize, ElementInfo mElementInfo) {
        float mFontSize = (float) mProcessGL.ScreenWidth / 1280.0f * mTextSize;

        if(mType == STRING_BK_SCALE) {
            mFontSize = (float) mProcessGL.ScreenWidth / 1280.0f * mTextSize * progress;
        }

        Paint mPaint = new Paint();
        mPaint.setTextSize(mFontSize);
        mPaint.setSubpixelText(true);
        mPaint.setAntiAlias(true);
        mPaint.setShader(null);

        if(mType == STRING_WHITE_NOBK_ANIM) {
            mPaint.setTypeface(Typefaces.get(mContext, "fonts/RobotoCondensed-Regular.ttf"));
        } else if(mType == STRING_WHITE_NOBK || mType == STRING_BLUE || mType == STRING_KIDS_CIRCLE_DATE) {
            mPaint.setTypeface(Typefaces.get(mContext, "fonts/Roboto-Thin.ttf"));
        } else if(mType == STRING_WHITE_NOBK_GEO || mType == STRING_YEAR_COUNTRY || mType == STRING_YEAR_COUNTRY_FADEIN
                || mType == STRING_YEAR_COUNTRY_FADEOUT) {
            mPaint.setTypeface(Typefaces.get(mContext, "fonts/Roboto-Regular.ttf"));
        } else if(mType == STRING_BK_SCALE || mType == STRING_NOBK_SCALE || mType == STRING_FADE_LIGHT || mType == STRING_WHITE_NOBK_LOVER
                || mType == STRING_DATE_LOVER || mType == STRING_ANIM_CITY_BLUE || mType == STRING_ANIM_CITY_TRANS || mType == STRING_ANIM_CITY_BLACK
                || mType == STRING_DATE_CITY || mType == STRING_DATE_CITY_TRANS || mType == STRING_KIDS_ICON_A || mType == STRING_KIDS_ICON_B
                || mType == STRING_ANIM_LR) {
            mPaint.setTypeface(Typefaces.get(mContext, "fonts/Roboto-Light.ttf"));
        } else {
            mPaint.setTypeface(Typefaces.get(mContext, "fonts/RobotoCondensed-Light.ttf"));
        }

        int mTextWidth = 0;
        int mTextHeight = 0;
        String mtemp = null;
        Rect rect = new Rect();
        if(mType != STRING_ANIM_TD && mType != STRING_ANIM_LR && mType != STRING_MASK && mType != STRING_WHITE_NOBK_ANIM
                && mType != STRING_WHITE_NOBK_GEO && mType != STRING_DATE_CITY && mType != STRING_DATE_LOVER && mType != STRING_DATE_CITY_TRANS
                && mType != STRING_KIDS_ICON_A && mType != STRING_KIDS_ICON_B) {
            mPaint.setTextAlign(Align.CENTER);
        } else {
            for(int i = 0; i < mString.size(); i++) {
                if(i == 0) {
                    mtemp = mString.get(i);
                } else {
                    mtemp += " " + mString.get(i);
                }
            }

            mPaint.getTextBounds(mtemp, 0, mtemp.length(), rect);
            mTextWidth = rect.width();
            mTextHeight = rect.height();

            mtemp = null;
        }

        int xPos = mProcessGL.ScreenWidth / 2;
        int yPos = (int) ((mProcessGL.ScreenHeight / 2) - ((mPaint.descent() + mPaint.ascent()) / 2) - ((mFontSize / mString.size()) * (mString
                .size() - 1)));

        if(mBitmap == null || (mProcessGL.ScreenWidth > 0 && mProcessGL.ScreenWidth != mBitmap.getWidth())
                || (mProcessGL.ScreenHeight > 0 && mProcessGL.ScreenHeight != mBitmap.getHeight()))
            mBitmap = Bitmap.createBitmap(mProcessGL.ScreenWidth, mProcessGL.ScreenHeight, Bitmap.Config.ARGB_8888);

        Canvas canvasTemp = new Canvas(mBitmap);
        if(mType == STRING_WHITE_NOBK || mType == STRING_WHITE_NOBK_GEO || mType == STRING_DATE_CITY || mType == STRING_YEAR_COUNTRY
                || mType == STRING_YEAR_COUNTRY_FADEIN || mType == STRING_YEAR_COUNTRY_FADEOUT || mType == STRING_WHITE_NOBK_LOVER
                || mType == STRING_ANIM_CITY_BLACK || mType == STRING_WHITE_NOBK_LINE || mType == STRING_DATE_CITY_TRANS
                || mType == STRING_KIDS_ICON_A || mType == STRING_KIDS_ICON_B) {
            canvasTemp.drawARGB(255, 0, 0, 0);
        } else if(mType == STRING_BLUE || mType == STRING_DATE_LOVER || mType == STRING_KIDS_CIRCLE_DATE) {
            canvasTemp.drawARGB(255, 255, 255, 255);
        } else if(mType == STRING_ANIM_CITY_BLUE) {
            canvasTemp.drawARGB(255, 0, 102, 204);
        } else if(mType == STRING_ANIM_CITY_TRANS) {
            canvasTemp.drawARGB(255, 0, (int) (102 * (1 - progress)), (int) (204 * (1 - progress)));
        } else if(mType == STRING_KIDS_END) {
            canvasTemp.drawARGB(255, 87, 184, 253);
        } else {
            canvasTemp
                    .drawARGB(255, (int) (mProcessGL.mScript.GetRed()), (int) (mProcessGL.mScript.GetGreen()), (int) (mProcessGL.mScript.GetBlue())); // Default
                                                                                                                                                      // White
        }

        for(int length = 0; length < mString.size(); length++) {
            if(mType == STRING_WHITE_NOBK_GEO && length > 0)
                break;
            if((mCount != 0 && mCount <= length) && mType != STRING_ANIM_TD && mType != STRING_ANIM_LR && mType != STRING_WHITE_NOBK_ANIM
                    && mType != STRING_WHITE_NOBK_GEO && mType != STRING_DATE_CITY && mType != STRING_YEAR_COUNTRY
                    && mType != STRING_YEAR_COUNTRY_FADEIN && mType != STRING_YEAR_COUNTRY_FADEOUT && mType != STRING_FADE
                    && mType != STRING_WHITE_NOBK_LINE && mType != STRING_WHITE_NOBK_LOVER && mType != STRING_DATE_LOVER
                    && mType != STRING_ANIM_CITY_BLACK && mType != STRING_ANIM_CITY_TRANS && mType != STRING_ANIM_CITY_BLUE
                    && mType != STRING_DATE_CITY_TRANS && mType != STRING_KIDS_CIRCLE_DATE && mType != STRING_KIDS_ICON_A
                    && mType != STRING_KIDS_ICON_B)
                break;
            if(mType == STRING_TYPE) {
                mPaint.setARGB(255, 0, 0, 0);
                int str_length = mString.get(length).length();
                int i = (int) Math.floor((float) (elapse / (float) (duration / (str_length * 3))));

                if(i > str_length) {
                    canvasTemp.drawText(mString.get(length), xPos, yPos + (mFontSize * length), mPaint);
                } else {
                    canvasTemp.drawText(mString.get(length), 0, i, xPos, yPos + (mFontSize * length), mPaint);
                }
            } else if(mType == STRING_ANIM_CITY_BLACK || mType == STRING_ANIM_CITY_TRANS || mType == STRING_ANIM_CITY_BLUE) {
                if(mType == STRING_ANIM_CITY_BLUE) {
                    mPaint.setARGB(255, 255, 255, 255);
                } else if(mType == STRING_ANIM_CITY_TRANS) {
                    int r = (int) (255 + progress * (0 - 255));
                    int g = (int) (255 + progress * (102 - 255));
                    int b = (int) (255 + progress * (204 - 255));
                    mPaint.setARGB(255, r, g, b);
                } else {
                    mPaint.setARGB(255, 0, 102, 204);
                }

                canvasTemp.drawText(mString.get(length), xPos, yPos + (mFontSize * length), mPaint);
            } else if(mType == STRING_FADE || mType == STRING_SHADER || mType == STRING_FADE_LIGHT) {
                if((progress >= 1.0f) || (mCount - 1 > length && mCount != 0) || mCount == 0) {
                    mPaint.setARGB(255, 0, 0, 0);
                } else {
                    if(mType == STRING_FADE || mType == STRING_FADE_LIGHT || (mType == STRING_SHADER && length > 0)) {
                        mPaint.setARGB((int) Math.floor(255 * progress), 0, 0, 0);
                    } else if(mType == STRING_SHADER) {
                        mPaint.getTextBounds(mString.get(length), 0, mString.get(length).length(), rect);
                        android.graphics.Shader mShader = new LinearGradient(0, 0, rect.width(), 0, new int[] { Color.BLACK, Color.WHITE },
                                new float[] { 1 - progress, progress }, android.graphics.Shader.TileMode.REPEAT);
                        mPaint.setShader(mShader);
                    }
                }
                canvasTemp.drawText(mString.get(length), xPos, yPos + (mFontSize * length), mPaint);
            } else if(mType == STRING_ANIM_TD) {
                if(length == 0) {
                    xPos = mProcessGL.ScreenWidth / 2 - mTextWidth / 2;
                    mtemp = mString.get(length) + " ";
                } else {
                    mPaint.getTextBounds(mtemp, 0, mtemp.length(), rect);
                    xPos = (mProcessGL.ScreenWidth / 2 - mTextWidth / 2) + (rect.width());
                    mtemp += mString.get(length) + " ";
                }

                yPos = (int) ((mProcessGL.ScreenHeight / 2) - ((mPaint.descent() + mPaint.ascent()) / 2));

                if(progress < 0.15) {
                    yPos = (int) ((yPos + ((mPaint.descent() + mPaint.ascent()))) * (progress * (10 / 1.5)));
                    if(length != 0)
                        yPos = mProcessGL.ScreenHeight - yPos - (int) (mPaint.descent() + mPaint.ascent());
                } else if(progress > 0.15 && progress < 0.85) {
                    yPos = (int) ((yPos + ((mPaint.descent() + mPaint.ascent()))) - (((mPaint.descent() + mPaint.ascent()) * 2) * ((progress - 0.15) * (10 / 7))));
                    if(length != 0)
                        yPos = mProcessGL.ScreenHeight - yPos - (int) (mPaint.descent() + mPaint.ascent());
                } else if(progress > 0.85) {
                    int i = (int) (yPos - (mPaint.descent() + mPaint.ascent()));
                    yPos = (int) (i + ((mProcessGL.ScreenHeight - i / 2) * ((progress - 0.85) * (10 / 1.5))));
                    if(length != 0)
                        yPos = mProcessGL.ScreenHeight - yPos - (int) (mPaint.descent() + mPaint.ascent());
                }

                mPaint.setARGB(255, 0, 0, 0);
                canvasTemp.drawText(mString.get(length), xPos + (mFontSize * length), yPos, mPaint);
            } else if(mType == STRING_ANIM_LR) {
                if(length == 0) {
                    mPaint.setTextSize((float) mProcessGL.ScreenWidth / 1280.0f * 70);
                } else {
                    mPaint.setTextSize((float) mProcessGL.ScreenWidth / 1280.0f * 62);
                }
                mPaint.getTextBounds(mString.get(length), 0, mString.get(length).length(), rect);
                mTextWidth = rect.width();
                xPos = mProcessGL.ScreenWidth / 2 - mTextWidth / 2;

                if(progress < 0.15) {
                    if(length == 0) {
                        xPos = (int) ((xPos - mProcessGL.ScreenWidth * 0.005f) * (progress * (10 / 1.5)));
                    } else {
                        xPos = (int) (mProcessGL.ScreenWidth - (mProcessGL.ScreenWidth - (xPos + mProcessGL.ScreenWidth * 0.005f))
                                * (progress * (10 / 1.5f)));
                    }
                } else if(progress > 0.15 && progress < 0.85) {
                    if(length == 0)
                        xPos = (int) ((xPos - mProcessGL.ScreenWidth * 0.005f) + (mProcessGL.ScreenWidth * 0.01f * ((progress - 0.15) * (10 / 7))));
                    else
                        xPos = (int) (xPos + mProcessGL.ScreenWidth * 0.005f - (mProcessGL.ScreenWidth * 0.01f) * ((progress - 0.15) * (10 / 7)));
                } else if(progress > 0.85) {
                    if(length == 0)
                        xPos = (int) ((xPos + mProcessGL.ScreenWidth * 0.005f) + ((mProcessGL.ScreenWidth - (xPos + mProcessGL.ScreenWidth * 0.005f)) * ((progress - 0.85) * (10 / 1.5))));
                    else
                        xPos = (int) (xPos - mProcessGL.ScreenWidth * 0.005f - (xPos - mProcessGL.ScreenWidth * 0.005f)
                                * ((progress - 0.85) * (10 / 1.5)));
                }

                mPaint.setARGB(255, 0, 0, 0);
                canvasTemp.drawText(mString.get(length), xPos, yPos + (mFontSize * length), mPaint);
            } else if(mType == STRING_MASK) {
                mPaint.setARGB(255, 0, 0, 0);
                if(progress > 0.6) {
                    canvasTemp.drawText(mString.get(length), xPos - mTextWidth / 2, yPos + (mFontSize * length), mPaint);
                }

                float StartX = xPos - mTextWidth * 2 / 3;
                float Mask_length = mTextWidth * 4 / 3;
                // 1, 3
                // Green_1
                Paint mPaint_1 = new Paint();
                mPaint_1.setColor(Color.rgb(148, 182, 103));
                mPaint_1.setStyle(Paint.Style.FILL);

                if(progress <= 0.15) {
                    canvasTemp.drawRect(StartX, yPos - mTextHeight, StartX + ((Mask_length * 1 / 2) * progress * 10 / 1.5f), yPos + mTextHeight / 2,
                            mPaint_1);
                } else if(progress > 0.15 && progress <= 0.3) {
                    canvasTemp.drawRect(StartX + ((Mask_length * 1 / 2) * (progress - 0.15f) * 10 / 1.5f), yPos - mTextHeight, StartX + Mask_length
                            * 1 / 2 + ((Mask_length * 1 / 2) * (progress - 0.15f) * 10 / 1.5f), yPos + mTextHeight / 2, mPaint_1);
                } else if(progress > 0.3 && progress <= 0.45)
                    canvasTemp.drawRect(StartX + Mask_length * 1 / 2 + ((Mask_length * 1 / 2) * (progress - 0.3f) * 10 / 1.5f), yPos - mTextHeight,
                            StartX + Mask_length, yPos + mTextHeight / 2, mPaint_1);

                // Green_2
                mPaint_1.setColor(Color.rgb(192, 217, 178));

                if(progress > 0.15 && progress <= 0.3) {
                    canvasTemp.drawRect(StartX, yPos - mTextHeight, StartX + ((Mask_length * 1 / 2) * (progress - 0.15f) * 10 / 1.5f), yPos
                            + mTextHeight / 2, mPaint_1);
                } else if(progress > 0.3 && progress <= 0.45) {
                    canvasTemp.drawRect(StartX + ((Mask_length * 1 / 2) * (progress - 0.3f) * 10 / 1.5f), yPos - mTextHeight, StartX + Mask_length
                            * 1 / 2 + ((Mask_length * 1 / 2) * (progress - 0.3f) * 10 / 1.5f), yPos + mTextHeight / 2, mPaint_1);
                } else if(progress > 0.45 && progress <= 0.6) {
                    canvasTemp.drawRect(StartX + Mask_length * 1 / 2 + ((Mask_length * 1 / 2) * (progress - 0.45f) * 10 / 1.5f), yPos - mTextHeight,
                            StartX + Mask_length, yPos + mTextHeight / 2, mPaint_1);
                }

                // White
                mPaint_1.setColor(Color.WHITE);

                if(progress > 0.3 && progress <= 0.6) {
                    canvasTemp.drawRect(StartX, yPos - mTextHeight, StartX + ((Mask_length) * (progress - 0.3f) * 10 / 3), yPos + mTextHeight / 2,
                            mPaint_1);
                } else if(progress > 0.6 && progress <= 0.75) {
                    canvasTemp.drawRect(StartX + ((Mask_length) * (progress - 0.6f) * 10 / 1.5f), yPos - mTextHeight, StartX + Mask_length, yPos
                            + mTextHeight / 2, mPaint_1);
                }

            } else if(mType == STRING_WHITE_NOBK_ANIM) {
                if(progress > 0.3 && progress <= 0.6) {
                    mPaint.setARGB((int) Math.floor(255 * ((progress - 0.3) * 10 / 3)), 255, 255, 255);
                    canvasTemp.drawText(mString.get(length), xPos - (mTextWidth) / 2, yPos + (mFontSize * length), mPaint);
                } else if(progress > 0.6) {
                    mPaint.setARGB(255, 255, 255, 255);
                    canvasTemp.drawText(mString.get(length), xPos - (mTextWidth) / 2, yPos + (mFontSize * length), mPaint);
                }

                Paint mPaint_1 = new Paint();
                mPaint_1.setColor(Color.WHITE);
                mPaint_1.setStyle(Paint.Style.FILL);

                int Wtotal = (int) (mProcessGL.ScreenWidth * 0.5f);
                int WStart = (mProcessGL.ScreenWidth - Wtotal) / 2;

                int HTPos = (int) (mProcessGL.ScreenHeight * 0.395f);
                int HBPos = mProcessGL.ScreenHeight - HTPos;
                float mElapse = progress * duration;

                mPaint_1.setAlpha((int) (255 * progress));
                float temp = Easing.Easing(2, mElapse, 0, Wtotal, duration);
                canvasTemp.drawRect(WStart, HTPos, WStart + temp, HTPos + mPaint.descent() / 4, mPaint_1);
                canvasTemp.drawRect(mProcessGL.ScreenWidth - WStart - temp, HBPos - mPaint.descent() / 4, WStart + Wtotal, HBPos, mPaint_1);
            } else if(mType == STRING_WHITE_NOBK_GEO) {
                xPos = (int) ((mProcessGL.ScreenWidth / 2 * (1 - mElementInfo.effect.getTextureWidthScaleRatio())) + mTextWidth * 3 / 5);
                mPaint.setARGB(255, 255, 255, 255);

                Paint mPaint_1 = new Paint();
                mPaint_1.setColor(Color.WHITE);
                mPaint_1.setStyle(Paint.Style.FILL);

                // First we need to check text length
                if(xPos + (mTextWidth) / 2 > mProcessGL.ScreenWidth / 3 && mString.size() > 1) {

                    mPaint.getTextBounds(mString.get(0), 0, mString.get(0).length(), rect);
                    int a = rect.width();

                    mPaint.getTextBounds(mString.get(1), 0, mString.get(1).length(), rect);
                    int b = rect.width();

                    if(a > b)
                        mTextWidth = a;
                    else
                        mTextWidth = b;

                    xPos = (int) ((mProcessGL.ScreenWidth / 2 * (1 - mElementInfo.effect.getTextureWidthScaleRatio())) + mTextWidth * 3 / 5);

                    canvasTemp.drawText(mString.get(0), xPos - (mTextWidth) / 2, yPos + (mFontSize * length) - mPaint.descent() * 1.5f, mPaint);

                    canvasTemp.drawRect(xPos - mTextWidth * 4 / 5, yPos - mPaint.descent() / 6, xPos + mTextWidth * 4 / 7, yPos, mPaint_1);

                    canvasTemp.drawText(mString.get(1), xPos - (mTextWidth) / 2, yPos + (mFontSize * (length + 1)) + mPaint.descent() / 2, mPaint);
                } else {
                    for(int i = 0; i < mString.size(); i++) {
                        if(i == 0)
                            mtemp = mString.get(i);
                        else
                            mtemp = mtemp + ", " + mString.get(i);
                    }
                    canvasTemp.drawText(mtemp, xPos - (mTextWidth) / 2, yPos + (mFontSize * length), mPaint);
                    canvasTemp.drawRect(xPos - mTextWidth * 4 / 5, yPos + mTextHeight / 2 - mPaint.descent() / 4, xPos + mTextWidth * 4 / 7, yPos
                            + mTextHeight / 2, mPaint_1);
                }
            } else if(mType == STRING_DATE_CITY || mType == STRING_DATE_CITY_TRANS) {
                mPaint.setARGB(255, 255, 255, 255);
                float tmp = mPaint.ascent() - mPaint.descent();
                Paint mMask = new Paint();
                mMask.setARGB(255, 0, 0, 0);

                if(mType == STRING_DATE_CITY_TRANS) {
                    // old one
                    if(mString_Past != null) {
                        canvasTemp.drawText(mString_Past.get(length), mProcessGL.ScreenWidth * 0.88f, mProcessGL.ScreenHeight * 0.9f - tmp
                                - (progress * mTextHeight * 1.5f), mPaint);
                        canvasTemp.drawRect(mProcessGL.ScreenWidth * 0.88f - mTextWidth, mProcessGL.ScreenHeight * 0.9f, mProcessGL.ScreenWidth
                                * 0.88f + mTextWidth * 2, mProcessGL.ScreenHeight * 0.9f - mTextHeight * 2, mMask);
                    }
                    // new one
                    canvasTemp.drawText(mString.get(length), mProcessGL.ScreenWidth * 0.88f, mProcessGL.ScreenHeight * 0.9f - tmp
                            + ((1 - progress) * mTextHeight * 1.5f), mPaint);
                    canvasTemp.drawRect(mProcessGL.ScreenWidth * 0.88f - mTextWidth, mProcessGL.ScreenHeight * 0.9f - tmp, mProcessGL.ScreenWidth
                            * 0.88f + mTextWidth * 2, mProcessGL.ScreenHeight * 0.9f - tmp + mTextHeight * 2, mMask);
                } else if(mType == STRING_DATE_CITY) {
                    canvasTemp.drawText(mString.get(length), mProcessGL.ScreenWidth * 0.88f, mProcessGL.ScreenHeight * 0.9f - tmp, mPaint);
                }

            } else if(mType == STRING_WHITE_NOBK_LINE) {
                Paint mLinePaint = new Paint();
                mLinePaint.setARGB(255, 50, 50, 50);
                mLinePaint.setStyle(Paint.Style.FILL);
                int totalLine = (int) (mProcessGL.ScreenWidth * 0.66f);
                float LineHeight = (float) (mProcessGL.ScreenHeight / 720.0);
                if(LineHeight < 0.5f)
                    LineHeight = 0.5f;

                canvasTemp.drawRect(mProcessGL.ScreenWidth * 0.17f, mProcessGL.ScreenHeight / 2 - LineHeight, mProcessGL.ScreenWidth * 0.17f
                        + totalLine * progress, mProcessGL.ScreenHeight / 2 + LineHeight, mLinePaint);
            } else if(mType == STRING_WHITE_NOBK_LOVER) {
                if(progress <= 0.4) {
                    mPaint.setARGB((int) Math.floor(255 * (progress * 10 / 4)), 255, 255, 255);
                    canvasTemp.drawText(mString.get(length), xPos - (mTextWidth) / 2, yPos + (mFontSize * length), mPaint);
                } else if(progress > 0.4) {
                    mPaint.setARGB(255, 255, 255, 255);
                    canvasTemp.drawText(mString.get(length), xPos - (mTextWidth) / 2, yPos + (mFontSize * length), mPaint);
                }

                mPaint.getTextBounds(mString.get(length), 0, mString.get(length).length(), rect);
                mTextWidth = rect.width();
                mTextHeight = rect.height();

                Paint mPaint_1 = new Paint();
                mPaint_1.setColor(Color.WHITE);
                mPaint_1.setStyle(Paint.Style.FILL);

                int Wtotal = (int) (mTextWidth * 1.4);
                int WStart = (int) (mProcessGL.ScreenWidth / 2 - Wtotal / 2);

                int Htotal = (int) (mTextHeight * 1.4);
                int HStart = (int) (mProcessGL.ScreenHeight / 2 - Htotal / 2);

                float LineWidth = mPaint.descent() / 4;

                int total = Wtotal + Htotal;

                if(progress <= 0.6) {
                    // Split into two part (top, right), (left, bottom)
                    if(total * (progress * 10 / 6) > Wtotal) { // top
                        canvasTemp.drawRect(WStart, HStart, WStart + Wtotal, HStart + LineWidth, mPaint_1);
                        canvasTemp.drawRect(WStart + Wtotal - LineWidth, HStart, WStart + Wtotal, HStart + (total * (progress * 10 / 6) - Wtotal),
                                mPaint_1);
                    } else {
                        canvasTemp.drawRect(WStart, HStart, WStart + total * (progress * 10 / 6), HStart + LineWidth, mPaint_1);
                    }

                    if(total * (progress * 10 / 6) > Htotal) { // top
                        canvasTemp.drawRect(WStart, HStart + Htotal - LineWidth, WStart + (total * (progress * 10 / 6) - Htotal), HStart + Htotal,
                                mPaint_1);
                        canvasTemp.drawRect(WStart, HStart, WStart + LineWidth, HStart + Htotal, mPaint_1);
                    } else {
                        canvasTemp.drawRect(WStart, HStart, WStart + LineWidth, HStart + total * (progress * 10 / 6), mPaint_1);
                    }
                } else {
                    canvasTemp.drawRect(WStart, HStart, WStart + Wtotal, HStart + LineWidth, mPaint_1);
                    canvasTemp.drawRect(WStart + Wtotal - LineWidth, HStart, WStart + Wtotal, HStart + Htotal, mPaint_1);

                    canvasTemp.drawRect(WStart, HStart + Htotal - LineWidth, WStart + Wtotal, HStart + Htotal, mPaint_1);
                    canvasTemp.drawRect(WStart, HStart, WStart + LineWidth, HStart + Htotal, mPaint_1);
                }
            } else if(mType == STRING_DATE_LOVER) {
                float mPosX = mProcessGL.ScreenWidth * mElementInfo.effect.getTextureWidthScaleRatio();
                float mPosY = mProcessGL.ScreenHeight * mElementInfo.effect.getTextureHightScaleRatio();

                mPaint.setARGB(255, 0, 0, 0);
                canvasTemp.drawText(mString.get(length), mPosX, mPosY, mPaint);
            } else if(mType == STRING_KIDS_ICON_A || mType == STRING_KIDS_ICON_B) {

                // Date
                float tmp = mProcessGL.ScreenHeight * 0.5f;
                float mTextPosY = mProcessGL.ScreenHeight * 0.75f - mPaint.descent();
                float mDPosX = mProcessGL.ScreenWidth * 0.265f - mTextWidth / 2;
                float mDPosY = 0;

                if(progress < 0.15f) {
                    mPaint.setARGB(255, 255, 255, 255);
                    mDPosY = (mTextPosY + tmp) - Easing.Easing(Easing.easeOutBack, progress * duration, 0, tmp, duration * 0.15f);
                } else if(progress >= 0.15f && progress < 0.75f) {
                    mPaint.setARGB(255, 255, 255, 255);
                    mDPosY = mTextPosY;
                } else if(progress >= 0.75f && progress < 0.85f) {
                    mPaint.setARGB((int) ((1 - (progress - 0.75f) * 10) * 255), 255, 255, 255);
                    mDPosY = (mTextPosY) + Easing.Easing(Easing.easeInBack, (progress - 0.75f) * duration, 0, tmp, duration * 0.1f);
                }

                canvasTemp.drawText(mString.get(length), mDPosX, mDPosY, mPaint);

                // Weather or note

                Paint mWeatherPaint = new Paint();
                WeatherQuery mWeather = new WeatherQuery();
                float mBitmapPosY = mProcessGL.ScreenHeight * 0.25f;
                float mBPosY = 0;

                if(progress < 0.15f) {
                    mBPosY = (mBitmapPosY + tmp) - Easing.Easing(Easing.easeOutBack, progress * duration, 0, tmp, duration * 0.15f);
                } else if(progress >= 0.15f && progress < 0.85f) {
                    mBPosY = mBitmapPosY;
                } else if(progress >= 0.85f) {
                    mBPosY = (mBitmapPosY) - Easing.Easing(Easing.easeInBack, (progress - 0.85f) * duration, 0, tmp * 1.5f, duration * 0.15f);
                }

                if(mWeather.queryWeather(mContext, String.valueOf(mElementInfo.mDate))) {
                    Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), mWeather.getWeatherResId(mWeather.getWeatherCode()));
                    float scale = (((float) mProcessGL.ScreenHeight / 720f) * 270f) / bmp.getHeight();

                    Matrix matrix = new Matrix();
                    matrix.postScale(scale, scale);

                    int bwidth = bmp.getWidth();
                    int bheight = bmp.getHeight();

                    bmp = Bitmap.createBitmap(bmp, 0, 0, bwidth, bheight, matrix, true);

                    canvasTemp.drawBitmap(bmp, mProcessGL.ScreenWidth * 0.16f, mBPosY, mWeatherPaint);
                } else {
                    Bitmap bmp;
                    if(mType == STRING_KIDS_ICON_A) {
                        bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.asus_micromovie_icon_kids_a);
                    } else {
                        bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.asus_micromovie_icon_kids_b);
                    }

                    float scale = (((float) mProcessGL.ScreenHeight / 720f) * 270f) / bmp.getHeight();

                    Matrix matrix = new Matrix();
                    matrix.postScale(scale, scale);

                    int bwidth = bmp.getWidth();
                    int bheight = bmp.getHeight();

                    bmp = Bitmap.createBitmap(bmp, 0, 0, bwidth, bheight, matrix, true);

                    canvasTemp.drawBitmap(bmp, mProcessGL.ScreenWidth * 0.165f, mBPosY, mWeatherPaint);
                }
            } else {
                if(mType == STRING_WHITE || mType == STRING_WHITE_NOBK || mType == STRING_KIDS_END) {
                    mPaint.setARGB(255, 255, 255, 255);
                } else if(mType == STRING_YEAR_COUNTRY_FADEIN || mType == STRING_YEAR_COUNTRY || mType == STRING_YEAR_COUNTRY_FADEOUT) {
                    int alpha = 255;
                    if(mType == STRING_YEAR_COUNTRY_FADEIN)
                        alpha = (int) (alpha * progress);
                    else if(mType == STRING_YEAR_COUNTRY_FADEOUT)
                        alpha = (int) (alpha * (1 - progress));

                    mPaint.setARGB(alpha, 255, 255, 255);

                    if(length == 0 || length == 2) {
                        mPaint.setFlags(Paint.UNDERLINE_TEXT_FLAG);
                    } else {
                        mPaint.setFlags(0);
                    }

                    if(length < 2) {
                        mPaint.getTextBounds(mString.get(0), 0, mString.get(0).length(), rect);
                        int a = rect.width();
                        if(length == 0)
                            xPos = (int) (mProcessGL.ScreenWidth * 0.48f - a * 2 / 3);
                        else
                            xPos = (int) (mProcessGL.ScreenWidth * 0.48f - a / 4);
                    } else {
                        mPaint.getTextBounds(mString.get(length), 0, mString.get(length).length(), rect);
                        int a = rect.width();
                        xPos = (int) (mProcessGL.ScreenWidth * 0.48f + a * 2 / 3);
                    }

                    yPos = (int) ((mProcessGL.ScreenHeight * 0.4) - ((mPaint.descent() + mPaint.ascent()) / 2) - ((mFontSize / mString.size()) * (mString
                            .size() - 1)));

                } else if(mType == STRING_BLUE) {
                    mPaint.setARGB(255, 129, 200, 249);
                } else {
                    mPaint.setARGB(255, 0, 0, 0);
                }
                canvasTemp.drawText(mString.get(length), xPos, yPos + (mFontSize * length), mPaint);
            }
        }
    }

    public void StringVertex() {
        float mRatio = mProcessGL.ScreenRatio;
        float[] mVerticesData = new float[] { -mRatio, -1.0f, 0.0f, mRatio, -1.0f, 0.0f, -mRatio, 1.0f, 0.0f, mRatio, 1.0f, 0.0f };

        float[] mTextCoordsData = new float[] { 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f };

        mStringVertices = ByteBuffer.allocateDirect(mVerticesData.length * ProcessGL.FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder()).asFloatBuffer();
        mStringVertices.put(mVerticesData).position(0);

        mStringTextureCoords = ByteBuffer.allocateDirect(mTextCoordsData.length * ProcessGL.FLOAT_SIZE_BYTES).order(ByteOrder.nativeOrder())
                .asFloatBuffer();
        mStringTextureCoords.put(mTextCoordsData).position(0);
    }
}
