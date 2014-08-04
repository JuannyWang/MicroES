package com.s890510.microfilm.script;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.asus.gallery.micromovie.MicroMovieActivity;
import com.asus.gallery.micromovie.MusicManager;
import com.asus.gallery.micromovie.ProcessGL;
import com.asus.gallery.micromovie.Slogan;
import com.asus.gallery.micromovie.StringLoader;
import com.asus.gallery.micromovie.ThemeAdapter;
import com.s890510.microfilm.filter.FilterChooser;
import com.s890510.microfilm.script.effects.EffectLib;
import com.s890510.microfilm.shader.Shader;

public class Carnival extends BasicScript {
    private float mRed = 254;
    private float mGreen = 228;
    private float mBlue = 40;

    private float[] mLeft = {120, 211, 122, 255};
    private float[] mRight = {232, 234, 80, 255};
    private float mFAlpha = 0.37f;

    public Carnival(boolean isFromEncode, MicroMovieActivity activity, ProcessGL processGL) {
        this(activity, processGL);
        mIsFromEncode = isFromEncode;
    }

    public Carnival(MicroMovieActivity activity, ProcessGL processGL) {
        super(activity, processGL);

        for(int i=0; i<mLeft.length; i++) {
            mLeft[i] = (mLeft[i]*(1-mFAlpha) + 255*mFAlpha)/255;
        }

        for(int i=0; i<mRight.length; i++) {
            mRight[i] = (mRight[i]*(1-mFAlpha) + 255*mFAlpha)/255;
        }

        float mRatio = processGL.ScreenRatio;

        Date mDate=new Date();

        SimpleDateFormat mSDFMonth = new SimpleDateFormat("MMMM", Locale.ENGLISH);
        SimpleDateFormat mSDFYear = new SimpleDateFormat("yyyy");

        ArrayList<String> mString_1 = new ArrayList<String>();
        mString_1.add(mSDFMonth.format(mDate));
        mString_1.add(mSDFYear.format(mDate));

        ArrayList<String> mString_2 = new ArrayList<String>();
        mString_2.add("JOYFUL");
        /*
         *  aaaaaaaaaaaaaaaaa bbbbbbbbbbbbbbbbb cccccccccccccccccc
         * |--------|--------|--------|--------|--------|--------|
         * -R                         0                          R
         *
         */
        mEffects.add(EffectLib.String(new int[]{2200}, 2400, new boolean[]{true}, Shader.String, mString_1, new boolean[]{false}, new int[]{0},
                new float[]{1.0f}, new float[]{1.0f}, new float[]{1.0f}, new float[]{1.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_ANIM_LR}, new int[]{1}, 85, 0));

        mEffects.add(EffectLib.Translate(processGL, new int[]{800, 1100, 300}, 1900, Shader.Default,
                new boolean[]{false, false, false}, new int[]{2, 0, 0}, 0, 0, true,
                new float[]{0.0f, -mRatio*0.1f, -mRatio*2.1f},
                new float[]{0.0f, 0.0f, mRatio*0.1f}, new float[]{0, 1.1f, 1.1f}, new float[]{1.1f, 1.1f, 1.1f}, new float[]{0.0f, 1.0f, 1.0f},
                new float[]{1.0f, 1.0f, 1.0f}, 1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 1, 1}));
        mEffects.add(EffectLib.Translate(processGL, new int[]{300, 2000, 300}, 2300, Shader.Default,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0, true,
                new float[]{-mRatio*2.1f, -mRatio*0.1f, -mRatio*2.1f},
                new float[]{-mRatio*2.1f, 0f, mRatio*0.1f}, new float[]{1.1f, 1.1f, 1.1f}, new float[]{1.1f, 1.1f, 1.1f}, new float[]{1.0f, 1.0f, 1.0f},
                new float[]{1.0f, 1.0f, 1.0f}, 1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{1, 1, 1}));
        mEffects.add(EffectLib.Translate(processGL, new int[]{300, 1600, 300}, 1900, Shader.Default,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0, true,
                new float[]{-mRatio*2.1f, -mRatio*0.1f, -mRatio*2.1f},
                new float[]{-mRatio*2.1f, 0f, mRatio*0.1f}, new float[]{1.1f, 1.1f, 1.1f}, new float[]{1.1f, 1.1f, 1.1f}, new float[]{1.0f, 1.0f, 1.0f},
                new float[]{1.0f, 1.0f, 1.0f}, 1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{1, 1, 1}));
        mEffects.add(EffectLib.Translate(processGL, new int[]{300, 1600, 300}, 1900, Shader.Default,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0, true,
                new float[]{-mRatio*2.1f, -mRatio*0.1f, -mRatio*2.1f},
                new float[]{-mRatio*2.1f, 0f, mRatio*0.1f}, new float[]{1.1f, 1.1f, 1.1f}, new float[]{1.1f, 1.1f, 1.1f}, new float[]{1.0f, 1.0f, 1.0f},
                new float[]{1.0f, 1.0f, 1.0f}, 1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{1, 1, 1}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{800, 900, 500}, 1700, Shader.Line,
                new boolean[]{true, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{1.1f, 1.068f, 1.02f}, new float[]{1.068f, 1.02f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 2, 2}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{500, 1200, 500}, 1700, Shader.Scale_Fade,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{1.1f, 1.0772f, 1.0227f}, new float[]{1.0772f, 1.0227f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 2, 2}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{500, 1900}, 1700, Shader.Scale_Fade,
                new boolean[]{false, false}, new int[]{0, 0}, 0, 0,
                new float[]{1.1f, 1.0791f}, new float[]{1.0791f, 1.0f}, new float[]{0.0f, 1.0f}, new float[]{1.0f, 1.0f},
                1.0f, 1.0f, new int[]{0, 0}, new int[]{2, 2}));

        mEffects.add(EffectLib.Translate(processGL, new int[]{500, 2900}, 100, Shader.Default,
                new boolean[]{false, false}, new int[]{2, 0}, 0, 0, true,
                new float[]{mRatio*2, 0}, new float[]{-(mRatio*8/3), mRatio*2/3},
                new float[]{1.1f, 1.1f}, new float[]{1.1f, 1.0f}, new float[]{1.0f, 1.0f},
                new float[]{1.0f, 1.0f}, 1f/3f, 1.0f, new int[]{0, 0}, new int[]{11, 11}));
        mEffects.add(EffectLib.Translate(processGL, new int[]{500, 2800}, 100, Shader.Default,
                new boolean[]{false, false}, new int[]{2, 0}, 0, 0, true,
                new float[]{mRatio*2, 0},
                new float[]{-mRatio*2, 0}, new float[]{1.1f, 1.1f}, new float[]{1.1f, 1.0f}, new float[]{1.0f, 1.0f},
                new float[]{1.0f, 1.0f}, 1f/3f, 1.0f, new int[]{0, 0}, new int[]{12, 12}));
        mEffects.add(EffectLib.Translate(processGL, new int[]{500, 2700}, 2700, Shader.Default,
                new boolean[]{false, false}, new int[]{2, 0}, 0, 0, true,
                new float[]{2*mRatio, 0},
                new float[]{-(mRatio*4/3), -mRatio*2/3}, new float[]{1.1f, 1.1f}, new float[]{1.1f, 1.0f}, new float[]{1.0f, 1.0f},
                new float[]{1.0f, 1.0f}, 1f/3f, 1.0f, new int[]{0, 0}, new int[]{13, 13}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{500, 700}, 500, Shader.Cover_Empty_Left,
                new boolean[]{true, false}, new int[]{0, 0}, 0, 0,
                new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f},
                0.5f, 1.0f, new int[]{0, 0, 0}, new int[]{1, 1}));

        mEffects.add(EffectLib.Translate(processGL, new int[]{700, 300, 2500}, 0, Shader.Default,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0, false,
                new float[]{0.5f, 0.16f, 0.83f}, new float[]{0.5f, 0.5f, 0.34f},
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 0.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{6, 6, 6}));

        mEffects.add(EffectLib.String(new int[]{800}, 700, new boolean[]{true}, Shader.String_Line, mString_2, new boolean[]{true}, new int[]{0},
                new float[]{1.0f}, new float[]{1.0f}, new float[]{0.0f}, new float[]{1.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_BK_SCALE}, new int[]{3}, 290, 0));

        mEffects.add(EffectLib.String(new int[]{2800}, 2100, new boolean[]{false}, Shader.String, mString_2, new boolean[]{false}, new int[]{0},
                new float[]{1.0f}, new float[]{1.05f}, new float[]{1.0f}, new float[]{1.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_NOBK_SCALE}, new int[]{6}, 290, 0));

        mEffects.add(EffectLib.Translate(processGL, new int[]{700, 900, 300}, 1600, Shader.Line,
                new boolean[]{true, false, false}, new int[]{0, 0, 0}, 0, 0, true,
                new float[]{0, 0, -mRatio*2.1f}, new float[]{0, 0, 0},
                new float[]{1.0f, 1.0f, 1.1f}, new float[]{1.0f, 1.1f, 1.1f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{1, 2, 1}));
        mEffects.add(EffectLib.Translate(processGL, new int[]{300, 1400, 300}, 1700, Shader.Default,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0, true,
                new float[]{-mRatio*2.1f, 0, -mRatio*2.1f}, new float[]{-mRatio*2.1f, 0f, 0f},
                new float[]{1.0f, 1.0f, 1.1f}, new float[]{1.0f, 1.1f, 1.1f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{1, 2, 1}));
        mEffects.add(EffectLib.Translate(processGL, new int[]{300, 1400, 300}, 1700, Shader.Default,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0, true,
                new float[]{-mRatio*2.1f, 0, -mRatio*2.1f}, new float[]{-mRatio*2.1f, 0f, 0f},
                new float[]{1.0f, 1.0f, 1.1f}, new float[]{1.0f, 1.1f, 1.1f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{1, 2, 1}));
        mEffects.add(EffectLib.Translate(processGL, new int[]{300, 1700, 300}, 2000, Shader.Default,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0, true,
                new float[]{-mRatio*2.1f, 0, -mRatio*2.1f}, new float[]{-mRatio*2.1f, 0f, 0f},
                new float[]{1.0f, 1.0f, 1.1f}, new float[]{1.0f, 1.1f, 1.1f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{1, 2, 1}));
        mEffects.add(EffectLib.Translate(processGL, new int[]{300, 1700, 300}, 2300, Shader.Default,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0, true,
                new float[]{-mRatio*2.1f, 0, 0}, new float[]{-mRatio*2.1f, 0f, 0f},
                new float[]{1.0f, 1.0f, 1.1f}, new float[]{1.0f, 1.1f, 2.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{1, 2, 2}));

        /*
        mEffects.add(EffectLib.String(new int[]{1000, 1500}, 2000, new boolean[]{true, false}, Shader.String, mString_3, new boolean[]{false, false}, new int[]{0, 0},
                new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_MASK, StringLoader.STRING_FADE}, new int[]{2, 1}, 75, 0));
        */

        mEffects.add(EffectLib.Slogan(new int[]{1000, 1500, 1300}, 3700, Shader.Slogan_TypeB,
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                new int[]{Slogan.SLOGAN_TEXT, Slogan.SLOGAN_DATE, Slogan.SLOGAN_ALL}, new boolean[]{true, false, false}));

        init();
    }

    @Override
    public int getMusicId() {
        return MusicManager.MUISC_ASUS_BEACH;
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
        return 1;
    }

    @Override
    public int GetScriptId() {
        return ThemeAdapter.TYPE_CARNIVAL;
    }

    @Override
    public int GetSloganType() {
        return 2;
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
