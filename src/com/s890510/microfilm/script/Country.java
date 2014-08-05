package com.s890510.microfilm.script;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.s890510.microfilm.MicroFilmActivity;
import com.s890510.microfilm.MusicManager;
import com.s890510.microfilm.ThemeAdapter;
import com.s890510.microfilm.draw.GLDraw;
import com.s890510.microfilm.draw.Slogan;
import com.s890510.microfilm.draw.StringLoader;
import com.s890510.microfilm.filter.FilterChooser;
import com.s890510.microfilm.script.effects.EffectLib;
import com.s890510.microfilm.shader.Shader;

public class Country extends BasicScript {
    private float mRed = 255;
    private float mGreen = 222;
    private float mBlue = 145;

    private float[] mLeft = {255, 153, 0, 255};
    private float[] mRight = {255, 255, 0, 255};
    private float mFAlpha = 0.65f;

    public Country(boolean isFromEncode, MicroFilmActivity activity, GLDraw gldraw) {
        this(activity, gldraw);
        mIsFromEncode = isFromEncode;
    }

    public Country(MicroFilmActivity activity, GLDraw gldraw) {
        super(activity, gldraw);

        for(int i=0; i<mLeft.length; i++) {
            mLeft[i] = (mLeft[i]*(1-mFAlpha) + 255*mFAlpha)/255;
        }

        for(int i=0; i<mRight.length; i++) {
            mRight[i] = (mRight[i]*(1-mFAlpha) + 255*mFAlpha)/255;
        }

        float mScreenRatio = gldraw.ScreenRatio;

        Date mStartDate=new Date(mActivity.getStartDate());
        Date mEndDate=new Date(mActivity.getEndDate());

        SimpleDateFormat mYear = new SimpleDateFormat("yyyy", Locale.ENGLISH);

        ArrayList<String> mString_1 = new ArrayList<String>();
        mString_1.add(mYear.format(mStartDate));
        mString_1.add("-");
        if(!mYear.format(mStartDate).equals(mYear.format(mEndDate)))
            mString_1.add(mYear.format(mEndDate));
        else
            mString_1.add("");


        mEffects.add(EffectLib.Filter(new int[]{1000, 7100}, 500, Shader.Filter,
                new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{3.0f, 3.0f}, new float[]{3.0f, 0.0f},
                1.0f, 0.839f, 0.521f, new int[]{1, 1}));

        mEffects.add(EffectLib.Bound(gldraw, new int[]{500, 5500, 1200, 2400}, 0, Shader.Default, 0, 0,
                new float[]{mScreenRatio*0.479f, mScreenRatio*0.479f, mScreenRatio*0.479f, mScreenRatio*0.479f},
                new float[]{mScreenRatio*0.479f, mScreenRatio*0.479f, mScreenRatio*0.479f, mScreenRatio*0.479f},
                new float[]{-0.45f, -0.45f, -0.66306f, -0.7107f}, new float[]{-0.3f, -0.66306f, -0.7107f, -0.80368f}, 1, 1,
                new float[]{1.0f, 1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, new float[]{0.0f, 0.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f, 0.0f},
                0.52f, 0.97f, new boolean[]{false, false, false, false}, new int[]{Shader.BOUNDING, Shader.BOUNDING, Shader.BOUNDING, Shader.BOUNDING}, new int[]{1, 1, 1, 1}));

        mEffects.add(EffectLib.Bound(gldraw, new int[]{500, 5800, 2900, 2400}, 0, Shader.Default, 0, 0,
                new float[]{-mScreenRatio*0.52f, -mScreenRatio*0.52f, -mScreenRatio*0.52f, -mScreenRatio*0.52f},
                new float[]{-mScreenRatio*0.52f, -mScreenRatio*0.52f, -mScreenRatio*0.52f, -mScreenRatio*0.52f},
                new float[]{0.03f, 0.03f, -0.1946f, -0.30702f}, new float[]{0.03f, -0.1946f, -0.30702f, -0.40f}, 1, 1,
                new float[]{1.0f, 1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, new float[]{0.0f, 0.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f, 0.0f},
                0.48f, 0.78f, new boolean[]{false, false, false, false}, new int[]{Shader.BOUNDING, Shader.BOUNDING, Shader.BOUNDING, Shader.BOUNDING}, new int[]{1, 1, 1, 1}));

        mEffects.add(EffectLib.Bound(gldraw, new int[]{1500, 4300, 3400, 2400}, 0, Shader.Default, 0, 0,
                new float[]{mScreenRatio*0.38f, mScreenRatio*0.38f, mScreenRatio*0.38f, mScreenRatio*0.38f},
                new float[]{mScreenRatio*0.38f, mScreenRatio*0.38f, mScreenRatio*0.38f, mScreenRatio*0.38f},
                new float[]{1.058f, 1.058f, 0.89168f, 0.75997f}, new float[]{1.058f, 0.89168f, 0.75997f, 0.667003f}, 1, 1,
                new float[]{1.0f, 1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, new float[]{0.0f, 0.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f, 0.0f},
                0.42f, 0.58f, new boolean[]{false, false, false, false}, new int[]{Shader.BOUNDING, Shader.BOUNDING, Shader.BOUNDING, Shader.BOUNDING}, new int[]{1, 1, 1, 1}));

        mEffects.add(EffectLib.String(new int[]{2300, 3500, 1000}, 7700, new boolean[]{true, false, true}, Shader.String, mString_1,
                new boolean[]{false, false, false}, new int[]{0, 0, 0},
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_YEAR_COUNTRY_FADEIN, StringLoader.STRING_YEAR_COUNTRY, StringLoader.STRING_YEAR_COUNTRY_FADEOUT}, new int[]{1, 1, 1}, 75, 0));

        mEffects.add(EffectLib.Bound(gldraw, new int[]{2400, 1500, 2300}, 4000, Shader.Default, 0, 0,
                new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f},
                new float[]{0.15f, 0.03388f, -0.0387f}, new float[]{0.03388f, -0.0387f, -0.15f}, 0, 0,
                new float[]{1.3f, 1.3f, 1.3f}, new float[]{1.3f, 1.3f, 1.3f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                0.8f, 1.0f, new boolean[]{false, false, false}, new int[]{0, 0, 0}, new int[]{1, 1, 1}));

        mEffects.add(EffectLib.Bound(gldraw, new int[]{1600, 3700, 1500}, 1600, Shader.Default, 0, 0,
                new float[]{mScreenRatio*0.4f, mScreenRatio*0.4301f, mScreenRatio*0.50f},
                new float[]{mScreenRatio*0.4301f, mScreenRatio*0.50f, mScreenRatio*0.5283f},
                new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f}, 0, 0,
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new boolean[]{false, false, false}, new int[]{0, 0, 0}, new int[]{1, 1, 1}));

        mEffects.add(EffectLib.Bound(gldraw, new int[]{1000, 4800, 1500}, 3700, Shader.Default, 0, 0,
                new float[]{-mScreenRatio*0.919f, -mScreenRatio*0.9001f, -mScreenRatio*0.8095f},
                new float[]{-mScreenRatio*0.9001f, -mScreenRatio*0.8095f, -mScreenRatio*0.7821f},
                new float[]{-0.40f, -0.40f, -0.40f}, new float[]{-0.40f, -0.40f, -0.40f}, 0, 0,
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                0.35f, 0.60f, new boolean[]{false, false, false}, new int[]{0, 0, 0}, new int[]{1, 1, 1}));

        mEffects.add(EffectLib.Bound(gldraw, new int[]{1500, 3200, 2000}, 1300, Shader.Default, 0, 0,
                new float[]{mScreenRatio*0.4975f, mScreenRatio*0.5258f, mScreenRatio*0.5861f},
                new float[]{mScreenRatio*0.5258f, mScreenRatio*0.5861f, mScreenRatio*0.62383f},
                new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f}, 0, 0,
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new boolean[]{false, false, false}, new int[]{0, 0, 0}, new int[]{1, 1, 1}));

        mEffects.add(EffectLib.Bound(gldraw, new int[]{1500, 3000, 4800, 1500}, 3600, Shader.Cover_Percent_B, 0, 0,
                new float[]{-mScreenRatio*0.826f, -mScreenRatio*0.7977f, -mScreenRatio*0.7411f, -mScreenRatio*0.6505f},
                new float[]{-mScreenRatio*0.7977f, -mScreenRatio*0.7411f, -mScreenRatio*0.6505f, -mScreenRatio*0.6222f},
                new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f, 0.0f}, 0.40f, 1.0f,
                new float[]{1.0f, 1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f, 0.0f},
                0.35f, 1.0f, new boolean[]{false, true, false, false}, new int[]{0, 0, Shader.BOUNDING, Shader.BOUNDING}, new int[]{2, 2, 1, 1}));

        mEffects.add(EffectLib.Bound(gldraw, new int[]{1500, 2100, 1500}, 3600, Shader.Default, 0, 0,
                new float[]{mScreenRatio*0.01f, mScreenRatio*0.0383f, mScreenRatio*0.0779f},
                new float[]{mScreenRatio*0.0383f, mScreenRatio*0.0779f, mScreenRatio*0.1062f},
                new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f}, 0, 0,
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                0.42f, 1.0f, new boolean[]{false, false, false}, new int[]{0, 0, 0}, new int[]{1, 1, 1}));

        mEffects.add(EffectLib.Bound(gldraw, new int[]{2100, 2000, 2000}, 2100, Shader.Default, 0, 0,
                new float[]{mScreenRatio*0.6556f, mScreenRatio*0.6952f, mScreenRatio*0.7329f},
                new float[]{mScreenRatio*0.6952f, mScreenRatio*0.7329f, mScreenRatio*0.77068f},
                new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f}, 0, 0,
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new boolean[]{false, false, false}, new int[]{0, 0, 0}, new int[]{2, 1, 1}));

        mEffects.add(EffectLib.Bound(gldraw, new int[]{1500, 500, 2000}, 2500, Shader.Default, 0, 0,
                new float[]{-mScreenRatio*0.7535f, -mScreenRatio*0.7252f, -mScreenRatio*0.71577f},
                new float[]{-mScreenRatio*0.7252f, -mScreenRatio*0.71577f, -mScreenRatio*0.6780f},
                new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f}, 0, 0,
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                0.45f, 1.0f, new boolean[]{false, false, false}, new int[]{0, 0, 0}, new int[]{1, 1, 1}));

        mEffects.add(EffectLib.Slogan(new int[]{1500, 1000, 1000, 400}, 3800, Shader.Slogan_TypeA,
                new float[]{0.0f, 1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f},
                new int[]{Slogan.SLOGAN_LINE, Slogan.SLOGAN_TEXT, Slogan.SLOGAN_DATE, Slogan.SLOGAN_ALL}, new boolean[]{false, false, false, false}));

        init();
    }

    @Override
    public int getMusicId() {
        return MusicManager.MUISC_ASUS_VINTAGE;
    }

    @Override
    public int getFilterId() {
        return FilterChooser.COLOR;
    }

    @Override
    public float ColorRed() {
        if(shouldReturnDefaultColor())
            return super.ColorRed();
        else
            return (float)mRed/255;
    }

    @Override
    public float ColorGreen() {
        if(shouldReturnDefaultColor())
            return super.ColorGreen();
        else
            return (float)mGreen/255;
    }

    @Override
    public float ColorBlue() {
        if(shouldReturnDefaultColor())
            return super.ColorBlue();
        else
            return (float)mBlue/255;
    }

    @Override
    public float GetRed() {
        if(shouldReturnDefaultColor())
            return super.ColorRed();
        else
            return mRed;
    }

    @Override
    public float GetGreen() {
        if(shouldReturnDefaultColor())
            return super.ColorGreen();
        else
            return mGreen;
    }

    @Override
    public float GetBlue() {
        if(shouldReturnDefaultColor())
            return super.ColorBlue();
        else
            return mBlue;
    }

    @Override
    public int getFilterNumber() {
        return 3;
    }

    @Override
    public int GetScriptId() {
        return ThemeAdapter.TYPE_VINTAGE;
    }

    @Override
    public int GetSloganType() {
        return 5;
    }

    @Override
    public float[] getFilterLeft() {
        return mLeft;
    }

    @Override
    public float[] getFilterRight() {
        return mRight;
    }
}
