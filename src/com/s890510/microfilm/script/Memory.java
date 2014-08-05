package com.s890510.microfilm.script;

import java.util.ArrayList;

import android.util.Log;

import com.s890510.microfilm.MicroFilmActivity;
import com.s890510.microfilm.MusicManager;
import com.s890510.microfilm.ThemeAdapter;
import com.s890510.microfilm.draw.GLDraw;
import com.s890510.microfilm.draw.Slogan;
import com.s890510.microfilm.draw.StringLoader;
import com.s890510.microfilm.filter.FilterChooser;
import com.s890510.microfilm.script.effects.EffectLib;
import com.s890510.microfilm.shader.Shader;

public class Memory extends BasicScript {
    private float mRed = 0;
    private float mGreen = 0;
    private float mBlue = 0;

    private float[] mLeft = {255f, 153, 0, 255};
    private float[] mRight = {153, 153, 153, 255};
    private float mFAlpha = 0.4f;

    public Memory(boolean isFromEncode, MicroFilmActivity activity, GLDraw gldraw) {
        this(activity, gldraw);
        mIsFromEncode = isFromEncode;
    }

    public Memory(MicroFilmActivity activity, GLDraw gldraw) {
        super(activity, gldraw);

        for(int i=0; i<mLeft.length; i++) {
            mLeft[i] = (mLeft[i]*(1-mFAlpha) + 255*mFAlpha)/255;
        }

        for(int i=0; i<mRight.length; i++) {
            mRight[i] = (mRight[i]*(1-mFAlpha) + 255*mFAlpha)/255;
        }

        float mScreenRatio = gldraw.ScreenRatio;
        float mMove_third = (1.0f * 1f) / 6.0f;

        Log.e("Memory", "mScreenRatio:" + mScreenRatio + ", mMove_third:" + mMove_third);

        ArrayList<String> mString_1 = new ArrayList<String>();
        mString_1.add("M  E  M  O  R  I  E  S");

        ArrayList<String> mString_2 = new ArrayList<String>();
        mString_2.add("MADE US WHO WE ARE");

        mEffects.add(EffectLib.Scale_Fade(gldraw, new int[]{3000, 2400, 1500}, 0, Shader.Default,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 1, 20,
                new float[]{1.1f, 1.0565f, 1.0217f}, new float[]{1.0565f, 1.0217f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 2, 2}));

        mEffects.add(EffectLib.Scale_Fade(gldraw, new int[]{5400, 1500, 5000, 1600}, 0, Shader.Default,
                new boolean[]{false, false, false, false}, new int[]{0, 0, 0, 0}, 1, 24,
                new float[]{1.1f, 1.1f, 1.0814f, 1.0197f}, new float[]{1.1f, 1.0814f, 1.0197f, 1.0f}, new float[]{0.0f, 0.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new int[]{0, 0, 0, 0}, new int[]{2, 2, 2, 2}));

        mEffects.add(EffectLib.String(new int[]{5600, 1800}, 10600, new boolean[]{true, false}, Shader.String, mString_1,
                new boolean[]{false, false, false}, new int[]{0, 0, 0},
                new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 0.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_WHITE_NOBK_ANIM, StringLoader.STRING_WHITE_NOBK_ANIM}, new int[]{4, 4}, 60, 0));

        mEffects.add(EffectLib.Scale_Fade(gldraw, new int[]{1000, 4500}, 900, Shader.Scale_Fade,
                new boolean[]{false, false}, new int[]{0, 0}, 1, 28,
                new float[]{1.1f, 1.0846f}, new float[]{1.0846f, 1.0307f}, new float[]{0.0f, 1.0f}, new float[]{1.0f, 1.0f},
                0.5f, 1.0f, new int[]{0, 0}, new int[]{3, 3}));

        mEffects.add(EffectLib.Scale_Fade(gldraw, new int[]{1000, 2000}, 2000, Shader.Scale_Fade,
                new boolean[]{false, false}, new int[]{0, 0}, 1, 30,
                new float[]{1.1f, 1.0846f}, new float[]{1.0846f, 1.0307f}, new float[]{0.0f, 1.0f}, new float[]{1.0f, 1.0f},
                0.5f, 1.0f, new int[]{0, 0}, new int[]{4, 4}));

        mEffects.add(EffectLib.Scale_Fade(gldraw, new int[]{1000, 1900, 1000}, 3300, Shader.Scale_Fade_Bar_TRANS_IN,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 1, 32,
                new float[]{1.1f, 1.0846f, 1.0538f}, new float[]{1.0846f, 1.0538f, 1.04f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 2, 2}));

        mEffects.add(EffectLib.Translate(gldraw, new int[]{2300, 4500, 1400, 2700}, 0, Shader.Scale_Fade_Bar_SHOWN,
                new boolean[]{false, false, false, false}, new int[]{0, 0, 0, 0}, 1, 34, true,
                new float[]{0.0f, -(mScreenRatio*0.2093f), -(mScreenRatio*0.06511f), -(mScreenRatio*0.1256f)},
                new float[]{mScreenRatio*0.15f, mScreenRatio*0.15f, mScreenRatio*0.15f+mScreenRatio*0.2093f, mScreenRatio*0.15f+mScreenRatio*0.2744f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f},
                new float[]{1.0f, 1.0f, 1.0f, 1.0f}, new float[]{0.0f, 0.0f, 1.0f, 1.0f},
                new float[]{0.0f, 1.0f, 1.0f, 0.0f}, 1.0f, 1.0f, new int[]{0, 0, 0, 0}, new int[]{1, 1, 1, 1}));

        mEffects.add(EffectLib.Translate(gldraw, new int[]{2300, 4500, 1400, 4100}, 0, Shader.Scale_Fade_Bar_SHOWN,
                new boolean[]{false, false, false, false}, new int[]{0, 0, 0, 0}, 1, 36, true,
                new float[]{0.0f, -(mScreenRatio*0.2093f), -(mScreenRatio*0.06511f), -(mScreenRatio*0.1906f)},
                new float[]{-(mScreenRatio*1.85f), -(mScreenRatio*1.85f), -(mScreenRatio*1.85f-mScreenRatio*0.2093f), -(mScreenRatio*1.85f-mScreenRatio*0.2744f)}, new float[]{1.0f, 1.0f, 1.0f, 1.0f},
                new float[]{1.0f, 1.0f, 1.0f, 1.0f}, new float[]{0.0f, 0.0f, 1.0f, 1.0f},
                new float[]{0.0f, 1.0f, 1.0f, 1.0f}, 1.0f, 1.0f, new int[]{0, 0, 0, 0}, new int[]{1, 1, 1, 1}));

        mEffects.add(EffectLib.String(new int[]{1500, 500, 2000}, 6200, new boolean[]{false, false, false}, Shader.String, mString_2,
                new boolean[]{false, false, false}, new int[]{0, 0, 0},
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_WHITE_NOBK, StringLoader.STRING_WHITE_NOBK, StringLoader.STRING_WHITE_NOBK}, new int[]{2, 2, 2}, 55, 0));

        mEffects.add(EffectLib.Translate(gldraw, new int[]{2300, 4300, 1500}, 1600, Shader.Scale_Fade_Bar_SHOWN,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 1, 20, true,
                new float[]{-mScreenRatio*0.1069f, -mScreenRatio*0.2f, -mScreenRatio*0.06976f},
                new float[]{mScreenRatio*0.3301f+mScreenRatio/2, mScreenRatio*0.4370f+mScreenRatio/2, mScreenRatio*0.637f+mScreenRatio/2}, new float[]{1.0f, 1.0f, 1.0f},
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f},
                new float[]{1.0f, 1.0f, 0.0f}, 0.5f, 1.0f, new int[]{0, 0, 0}, new int[]{1, 1, 1}));
        mEffects.add(EffectLib.Translate(gldraw, new int[]{2300, 2700, 1500}, 2200, Shader.Scale_Fade_Bar_SHOWN,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 1, 23, true,
                new float[]{-mScreenRatio*0.1069f, -mScreenRatio*0.12558f, -mScreenRatio*0.06976f},
                new float[]{mScreenRatio*0.4045f-mScreenRatio/2, mScreenRatio*0.5114f-mScreenRatio/2, mScreenRatio*0.6369f-mScreenRatio/2}, new float[]{1.0f, 1.0f, 1.0f},
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f},
                new float[]{1.0f, 1.0f, 0.0f}, 0.5f, 1.0f, new int[]{0, 0, 0}, new int[]{1, 1, 1}));
        mEffects.add(EffectLib.Translate(gldraw, new int[]{2300, 500, 1500}, 2800, Shader.Scale_Fade_Bar_SHOWN,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 1, 26, true,
                new float[]{-mScreenRatio*0.1069f, -mScreenRatio*0.0232f, -mScreenRatio*0.06976f},
                new float[]{-(mScreenRatio+mScreenRatio/2-mScreenRatio*0.5081f), -(mScreenRatio+mScreenRatio/2-mScreenRatio*0.615f), -(mScreenRatio+mScreenRatio/2-mScreenRatio*0.6382f)}, new float[]{1.0f, 1.0f, 1.0f},
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f},
                new float[]{1.0f, 1.0f, 0.0f}, 0.5f, 1.0f, new int[]{0, 0, 0}, new int[]{1, 1, 1}));
        mEffects.add(EffectLib.Scale_Fade(gldraw, new int[]{1500, 1700, 1500}, 3200, Shader.Scale_Fade_Bar_SHOWN,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 1, 29,
                new float[]{1.1f, 1.068f, 1.0319f}, new float[]{1.068f, 1.0319f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 2, 2}));

        mEffects.add(EffectLib.Scale_Fade(gldraw, new int[]{1500, 1100, 1500}, 2600, Shader.Scale_Fade_Bar_SHOWN,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 1, 32,
                new float[]{1.1f, 1.0634f, 1.0319f}, new float[]{1.0634f, 1.0319f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 2, 2}));

        mEffects.add(EffectLib.Scale_Fade(gldraw, new int[]{1500, 1200, 2000}, 2700, Shader.Scale_Fade_Bar_SHOWN,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 1, 35,
                new float[]{1.1f, 1.068f, 1.0425f}, new float[]{1.068f, 1.0425f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 2, 2}));

        mEffects.add(EffectLib.Scale_Fade(gldraw, new int[]{2000, 700, 2300}, 3000, Shader.Scale_Fade_Bar_SHOWN,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 1, 38,
                new float[]{1.1f, 1.066f, 1.055f}, new float[]{1.066f, 1.055f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 2, 2}));

        mEffects.add(EffectLib.String(new int[]{1000, 500}, 1000, new boolean[]{true, false}, Shader.String, null,
                new boolean[]{true, false}, new int[]{0, 0},
                new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{0.6f, 0.6f}, new float[]{0.6f, 0.6f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_WHITE_NOBK_LINE, StringLoader.STRING_WHITE_NOBK_LINE}, new int[]{6, 6}, 60, 0));

        mEffects.add(EffectLib.Slogan(new int[]{1000, 500, 2700}, 4100, Shader.Slogan_TypeA,
                new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                new int[]{Slogan.SLOGAN_TEXT, Slogan.SLOGAN_DATE, Slogan.SLOGAN_ALL}, new boolean[]{false, false, false}));

        init();
    }

    @Override
    public int getMusicId() {
        return MusicManager.MUISC_ASUS_MEMORY;
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
        return ThemeAdapter.TYPE_MEMORY;
    }

    @Override
    public int GetSloganType() {
        return 4;
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
