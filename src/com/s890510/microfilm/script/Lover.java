package com.s890510.microfilm.script;

import java.util.ArrayList;

import com.s890510.microfilm.MicroMovieActivity;
import com.s890510.microfilm.MusicManager;
import com.s890510.microfilm.ProcessGL;
import com.s890510.microfilm.ThemeAdapter;
import com.s890510.microfilm.draw.Slogan;
import com.s890510.microfilm.draw.StringLoader;
import com.s890510.microfilm.filter.FilterChooser;
import com.s890510.microfilm.script.effects.EffectLib;
import com.s890510.microfilm.shader.Shader;

public class Lover extends BasicScript {
    private float mRed = 240;
    private float mGreen = 240;
    private float mBlue = 240;

    public Lover(boolean isFromEncode, MicroMovieActivity activity, ProcessGL processGL) {
        this(activity, processGL);
        mIsFromEncode = isFromEncode;
    }

    public Lover(MicroMovieActivity activity, ProcessGL processGL) {
        super(activity, processGL);

        float mScreenRatio = processGL.ScreenRatio;

        ArrayList<String> mString_1 = new ArrayList<String>();
        mString_1.add("These days");

        mEffects.add(EffectLib.Bound(processGL, new int[]{2500, 2500}, 0, Shader.Photo, 0, 0,
                new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f},
                new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f}, 1, 1,
                new float[]{1.1f, 1.0f}, new float[]{1.0f, 1.05f}, new float[]{0.0f, 1.0f}, new float[]{1.0f, 1.0f},
                0.65f, 0.65f, new boolean[]{false, false}, new int[]{Shader.BOUNDING, Shader.NONE}, new int[]{1, 1}));

        mEffects.add(EffectLib.Filter(new int[]{2500, 1500}, 1000, Shader.Filter,
                new float[]{0.633f, 0.633f}, new float[]{0.633f, 0.65172f}, new float[]{0.0f, 0.5f}, new float[]{0.5f, 0.0f},
                0.976f, 0.407f, 0.96f, new int[]{1, 1}));

        mEffects.add(EffectLib.String(new int[]{2000, 700}, 4000, new boolean[]{true, false}, Shader.String, mString_1,
                new boolean[]{false, false}, new int[]{0, 0},
                new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 0.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_WHITE_NOBK_LOVER, StringLoader.STRING_WHITE_NOBK_LOVER}, new int[]{2, 2}, 65, 0));

        mEffects.add(EffectLib.Bound(processGL, new int[]{800, 1000}, 1800, Shader.Photo, 0, 0,
                new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f},
                new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f}, 1, 1,
                new float[]{1.05f, 1.066f}, new float[]{1.066f, 1.086f}, new float[]{0.0f, 1.0f}, new float[]{1.0f, 1.0f},
                0.65f, 0.65f, new boolean[]{false, false}, new int[]{Shader.NONE, Shader.NONE}, new int[]{1, 1}));

        mEffects.add(EffectLib.Bound(processGL, new int[]{800, 400}, 1200, Shader.Photo, 0, 0,
                new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f},
                new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f}, 1, 1,
                new float[]{1.086f, 1.102f}, new float[]{1.102f, 1.122f}, new float[]{0.0f, 1.0f}, new float[]{1.0f, 1.0f},
                0.65f, 0.65f, new boolean[]{false, false}, new int[]{Shader.NONE, Shader.NONE}, new int[]{1, 1}));

        mEffects.add(EffectLib.Bound(processGL, new int[]{800, 2000}, 2800, Shader.Photo, 0, 0,
                new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f},
                new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f}, 1, 1,
                new float[]{1.122f, 1.138f}, new float[]{1.138f, 1.166f}, new float[]{0.0f, 1.0f}, new float[]{1.0f, 1.0f},
                0.65f, 0.65f, new boolean[]{false, false}, new int[]{Shader.NONE, Shader.NONE}, new int[]{1, 1}));

        mEffects.add(EffectLib.Bound(processGL, new int[]{500, 2900, 500}, 500, Shader.Photo, 0, 0,
                new float[]{-mScreenRatio*0.402f, -mScreenRatio*0.402f, -mScreenRatio*0.402f},
                new float[]{-mScreenRatio*0.402f, -mScreenRatio*0.402f, -mScreenRatio*0.402f},
                new float[]{0.1f, 0.07435f, -0.074351f}, new float[]{0.07435f, -0.074351f, -0.1f}, 1, 1,
                new float[]{1.2f, 1.2f, 1.2f}, new float[]{1.2f, 1.2f, 1.2f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                0.54f, 1.0f, new boolean[]{false, false, false}, new int[]{Shader.NONE, Shader.NONE, Shader.NONE}, new int[]{1, 1, 1}));

        mEffects.add(EffectLib.String(new int[]{500, 2400, 500}, 3400, new boolean[]{true, false, false}, Shader.String, null,
                new boolean[]{false, false, false}, new int[]{0, 0, 0},
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f}, 0.64f, 0.65f,
                new int[]{StringLoader.STRING_DATE_LOVER, StringLoader.STRING_DATE_LOVER, StringLoader.STRING_DATE_LOVER}, new int[]{2, 2, 2}, 45, 1));

        mEffects.add(EffectLib.Bound(processGL, new int[]{500, 2600, 500}, 500, Shader.Photo, 0, 0,
                new float[]{mScreenRatio*0.52f, mScreenRatio*0.52f, mScreenRatio*0.52f},
                new float[]{mScreenRatio*0.52f, mScreenRatio*0.52f, mScreenRatio*0.52f},
                new float[]{-0.1f, -0.07435f, 0.074351f}, new float[]{-0.07435f, 0.074351f, 0.1f}, 1, 1,
                new float[]{1.3f, 1.3f, 1.3f}, new float[]{1.3f, 1.3f, 1.3f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                0.48f, 1.0f, new boolean[]{false, false, false}, new int[]{Shader.NONE, Shader.NONE, Shader.NONE}, new int[]{1, 1, 1}));

        mEffects.add(EffectLib.String(new int[]{500, 2100, 500}, 3100, new boolean[]{true, false, false}, Shader.String, null,
                new boolean[]{false, false, false}, new int[]{0, 0, 0},
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f}, 0.23f, 0.38f,
                new int[]{StringLoader.STRING_DATE_LOVER, StringLoader.STRING_DATE_LOVER, StringLoader.STRING_DATE_LOVER}, new int[]{2, 2, 2}, 45, 1));

        mEffects.add(EffectLib.Bound(processGL, new int[]{500, 2200, 500}, 500, Shader.Photo, 0, 0,
                new float[]{-mScreenRatio*0.46f, -mScreenRatio*0.450625f, -mScreenRatio*0.409375f},
                new float[]{-mScreenRatio*0.450625f, -mScreenRatio*0.409375f, -mScreenRatio*0.40f},
                new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f}, 1, 1,
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                0.48f, 0.9f, new boolean[]{false, false, false}, new int[]{Shader.NONE, Shader.NONE, Shader.NONE}, new int[]{1, 1, 1}));

        mEffects.add(EffectLib.String(new int[]{500, 1700, 500}, 2700, new boolean[]{true, false, false}, Shader.String, null,
                new boolean[]{false, false, false}, new int[]{0, 0, 0},
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f}, 0.56f, 0.92f,
                new int[]{StringLoader.STRING_DATE_LOVER, StringLoader.STRING_DATE_LOVER, StringLoader.STRING_DATE_LOVER}, new int[]{2, 2, 2}, 45, 1));

        mEffects.add(EffectLib.Bound(processGL, new int[]{800, 2200, 500}, 1200, Shader.Default, 0, 0,
                new float[]{mScreenRatio*0.5f, mScreenRatio*0.5f, mScreenRatio*0.5f},
                new float[]{mScreenRatio*0.5f, mScreenRatio*0.5f, mScreenRatio*0.5f},
                new float[]{0.1f, 0.05429f, -0.07145f}, new float[]{0.05429f, -0.07145f, -0.1f}, 1, 1,
                new float[]{1.1f, 1.1f, 1.1f}, new float[]{1.1f, 1.1f, 1.1f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                0.4546f, 1.0f, new boolean[]{false, false, false}, new int[]{Shader.NONE, Shader.NONE, Shader.NONE}, new int[]{1, 1, 1}));

        mEffects.add(EffectLib.Bound(processGL, new int[]{800, 1000, 500}, 2300, Shader.Default, 0, 0,
                new float[]{-mScreenRatio*0.5f, -mScreenRatio*0.5f, -mScreenRatio*0.5f},
                new float[]{-mScreenRatio*0.5f, -mScreenRatio*0.5f, -mScreenRatio*0.5f},
                new float[]{-0.06571f, -0.02f, 0.03714f}, new float[]{-0.02f, 0.03714f, 0.06571f}, 1, 1,
                new float[]{1.1f, 1.1f, 1.1f}, new float[]{1.1f, 1.1f, 1.1f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                0.4546f, 1.0f, new boolean[]{false, false, false}, new int[]{Shader.NONE, Shader.NONE, Shader.NONE}, new int[]{1, 1, 1}));

        mEffects.add(EffectLib.Bound(processGL, new int[]{500, 4700, 700}, 2200, Shader.Photo, 0, 0,
                new float[]{-mScreenRatio*0.32f, -mScreenRatio*0.32f, -mScreenRatio*0.32f},
                new float[]{-mScreenRatio*0.32f, -mScreenRatio*0.32f, -mScreenRatio*0.32f},
                new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f}, 1, 1,
                new float[]{1.0f, 1.0108f, 1.0705f}, new float[]{1.0108f, 1.0705f, 1.08f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                0.58f, 0.7f, new boolean[]{false, false, false}, new int[]{Shader.NONE, Shader.NONE, Shader.NONE}, new int[]{1, 1, 1}));

        mEffects.add(EffectLib.Bound(processGL, new int[]{500, 2500, 700}, 3700, Shader.Photo, 0, 0,
                new float[]{mScreenRatio*0.5f, mScreenRatio*0.5f, mScreenRatio*0.5f},
                new float[]{mScreenRatio*0.5f, mScreenRatio*0.5f, mScreenRatio*0.5f},
                new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f}, 1, 1,
                new float[]{1.0f, 1.0108f, 1.0648f}, new float[]{1.0108f, 1.0648f, 1.08f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                0.3f, 0.82f, new boolean[]{false, false, false}, new int[]{Shader.NONE, Shader.NONE, Shader.NONE}, new int[]{1, 1, 1}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{1000, 1400, 1000}, 1400, Shader.Default,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{1.1f, 1.0706f, 1.0294f}, new float[]{1.0706f, 1.0294f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 2, 2}));

        mEffects.add(EffectLib.String(new int[]{1000, 500}, 1000, new boolean[]{true, false}, Shader.String, null,
                new boolean[]{true, false}, new int[]{0, 0},
                new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{0.6f, 0.6f}, new float[]{0.6f, 0.6f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_WHITE_NOBK_LINE, StringLoader.STRING_WHITE_NOBK_LINE}, new int[]{2, 2}, 60, 0));

        mEffects.add(EffectLib.Slogan(new int[]{1000, 1000, 1500}, 3400, Shader.Slogan_TypeA,
                new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                new int[]{Slogan.SLOGAN_TEXT, Slogan.SLOGAN_DATE, Slogan.SLOGAN_ALL}, new boolean[]{false, false, false}));

        init();
    }

    @Override
    public int getMusicId() {
        return MusicManager.MUISC_ASUS_ROMENTIC;
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
    public int GetScriptId() {
        return ThemeAdapter.TYPE_ROMANCE;
    }

    @Override
    public int GetSloganType() {
        return 4;
    }
}
