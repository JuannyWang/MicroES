package com.s890510.microfilm.script;

import java.util.ArrayList;

import com.asus.gallery.micromovie.MicroMovieActivity;
import com.asus.gallery.micromovie.MusicManager;
import com.asus.gallery.micromovie.ProcessGL;
import com.asus.gallery.micromovie.Slogan;
import com.asus.gallery.micromovie.StringLoader;
import com.asus.gallery.micromovie.ThemeAdapter;
import com.s890510.microfilm.filter.FilterChooser;
import com.s890510.microfilm.script.effects.EffectLib;
import com.s890510.microfilm.shader.Shader;

public class Life extends BasicScript {
    private float mRed = 255;
    private float mGreen = 255;
    private float mBlue = 255;

    private float[] mLeft = {255, 153, 0, 255};
    private float[] mRight = {51, 153, 255, 255};
    private float mFAlpha = 0.35f;

    public Life(boolean isFromEncode, MicroMovieActivity activity, ProcessGL processGL) {
        this(activity, processGL);
        mIsFromEncode = isFromEncode;
    }

    public Life(MicroMovieActivity activity, ProcessGL processGL) {
        super(activity, processGL);

        for(int i=0; i<mLeft.length; i++) {
            mLeft[i] = (mLeft[i]*(1-mFAlpha) + 255*mFAlpha)/255;
        }

        for(int i=0; i<mRight.length; i++) {
            mRight[i] = (mRight[i]*(1-mFAlpha) + 255*mFAlpha)/255;
        }

        ArrayList<String> mString_1 = new ArrayList<String>();
        mString_1.add("You & Me");

        mEffects.add(EffectLib.Translate(processGL, new int[]{3600, 1400, 1000}, 0, Shader.Mirror_Tilted_Mask,
                new boolean[]{true, false, false}, new int[]{2, 0, 0}, 0, 0, true,
                new float[]{-0.9f, 0, 0}, new float[]{0.9f, 0, 0},
                new float[]{-1.0f, 1.0f, 0.9709f}, new float[]{-1.0f, 0.9709f, 0.95f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                0.8f, 0.85f, new int[]{0, 0, 0}, new int[]{1, 2, 2}));

        mEffects.add(EffectLib.String(new int[]{1600, 3400, 1000}, 5000, new boolean[]{true, true, true}, Shader.String, null,
                new boolean[]{false, false, false}, new int[]{0, 0, 0},
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, 0.75f, 1.0f,
                new int[]{StringLoader.STRING_WHITE_NOBK_GEO, StringLoader.STRING_WHITE_NOBK_GEO, StringLoader.STRING_WHITE_NOBK_GEO}, new int[]{3, 3, 3}, 30, 1));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{1500, 6800}, 1000, Shader.Cover_Center_H,
                new boolean[]{true, false}, new int[]{0, 0}, 0, 0,
                new float[]{1.1f, 1.0825f}, new float[]{1.0825f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f},
                1.0f, 1.0f, new int[]{0, 0}, new int[]{7, 2}));

        mEffects.add(EffectLib.String(new int[]{7300}, 2600, new boolean[]{true}, Shader.String, null,
                new boolean[]{false}, new int[]{0},
                new float[]{1.0f}, new float[]{1.0f}, new float[]{1.0f}, new float[]{1.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_WHITE_NOBK_GEO}, new int[]{3}, 30, 1));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{2500, 4000, 1300}, 2200, Shader.Cover_Half_Right,
                new boolean[]{true, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{1.1f, 1.0679f, 1.0128f}, new float[]{1.0679f, 1.0128f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                0.5f, 1.0f, new int[]{0, 0, 0}, new int[]{4, 4, 4}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{2500, 4000, 1300}, 2100, Shader.Cover_Half_Left,
                new boolean[]{true, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{1.1f, 1.0679f, 1.0128f}, new float[]{1.0679f, 1.0128f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                0.5f, 1.0f, new int[]{0, 0, 0}, new int[]{3, 3, 3}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{1300, 1100, 1300}, 1100, Shader.Scale_Fade,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{1.1f, 1.0648f, 1.0351f}, new float[]{1.0648f, 1.0351f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                0.5f, 1.0f, new int[]{0, 0, 0}, new int[]{4, 4, 4}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{1300, 1100, 1000}, 1400, Shader.Scale_Fade,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{1.1f, 1.0617f, 1.0294f}, new float[]{1.0617f, 1.0294f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                0.5f, 1.0f, new int[]{0, 0, 0}, new int[]{3, 3, 3}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{100, 1300, 1100, 2000}, 0, Shader.Scale_Fade,
                new boolean[]{false, false, false, false}, new int[]{0, 0, 0, 0}, 0, 0,
                new float[]{1.1f, 1.1f, 1.07046f, 1.0454f}, new float[]{1.1f, 1.0704f, 1.0454f, 1.0f}, new float[]{0.0f, 0.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f, 0.0f},
                0.5f, 1.0f, new int[]{0, 0, 0, 0}, new int[]{4, 4, 4, 4}));

        mEffects.add(EffectLib.String(new int[]{500, 4100, 4000}, 5200, new boolean[]{false, false, false}, Shader.String, mString_1,
                new boolean[]{false, false, false}, new int[]{0, 0, 0},
                new float[]{0.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{0.0f, 0.0f, 1.0f}, new float[]{0.0f, 1.0f, 0.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_WHITE_NOBK, StringLoader.STRING_WHITE_NOBK, StringLoader.STRING_WHITE_NOBK}, new int[]{5, 5, 5}, 180, 0));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{1000, 4700}, 1200, Shader.Cover_Half_Left_Q,
                new boolean[]{true, false}, new int[]{0, 0}, 0, 0,
                new float[]{1.1f, 1.0824f}, new float[]{1.0824f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f},
                1f/3f, 1.0f, new int[]{0, 0}, new int[]{14, 14}));
        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{1000, 4700}, 1200, Shader.Cover_Half_Left_Q,
                new boolean[]{true, false}, new int[]{0, 0}, 0, 0,
                new float[]{1.1f, 1.0824f}, new float[]{1.0824f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f},
                1f/3f, 1.0f, new int[]{0, 0}, new int[]{15, 15}));
        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{1000, 4400}, 2300, Shader.Cover_Half_Left_Q,
                new boolean[]{true, false}, new int[]{0, 0}, 0, 0,
                new float[]{1.1f, 1.0824f}, new float[]{1.0824f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f},
                1f/3f, 1.0f, new int[]{0, 0}, new int[]{16, 16}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{1000, 3800, 500}, 1200, Shader.Cover_Half_Left_Q,
                new boolean[]{true, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{1.1f, 1.0821f, 1.0089f}, new float[]{1.0821f, 1.0089f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1f/3f, 1.0f, new int[]{0, 0, 0}, new int[]{8, 8, 8}));
        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{1000, 2600, 500}, 1200, Shader.Cover_Half_Left_Q,
                new boolean[]{true, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{1.0785f, 1.0607f, 1.0089f}, new float[]{1.0607f, 1.0089f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1f/3f, 1.0f, new int[]{0, 0, 0}, new int[]{9, 9, 9}));
        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{1000, 1400, 500}, 2600, Shader.Cover_Half_Left_Q,
                new boolean[]{true, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{1.0625f, 1.04464f, 1.0089f}, new float[]{1.04464f, 1.0089f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1f/3f, 1.0f, new int[]{0, 0, 0}, new int[]{10, 10, 10}));

        mEffects.add(EffectLib.Slogan(new int[]{1000, 2000, 1700}, 4600, Shader.Slogan_TypeC,
                new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                new int[]{Slogan.SLOGAN_TEXT, Slogan.SLOGAN_LINE, Slogan.SLOGAN_ALL}, new boolean[]{false, false, false}));

        init();
    }

    @Override
    public int getMusicId() {
        return MusicManager.MUISC_ASUS_LIFE;
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
        return 2;
    }

    @Override
    public int GetScriptId() {
        return ThemeAdapter.TYPE_LIFE;
    }

    @Override
    public int GetSloganType() {
        return 1;
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
