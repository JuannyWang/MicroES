package com.s890510.microfilm.script;

import java.util.ArrayList;

import com.s890510.microfilm.MicroMovieActivity;
import com.s890510.microfilm.MusicManager;
import com.s890510.microfilm.ProcessGL;
import com.s890510.microfilm.ThemeAdapter;
import com.s890510.microfilm.draw.Slogan;
import com.s890510.microfilm.draw.StringLoader;
import com.s890510.microfilm.filter.FilterChooser;
import com.s890510.microfilm.mask.Mask;
import com.s890510.microfilm.script.effects.EffectLib;
import com.s890510.microfilm.shader.Shader;
import com.s890510.microfilm.util.Easing;

public class Kids extends BasicScript {
    private float   mRed    = 255;
    private float   mGreen  = 255;
    private float   mBlue   = 255;

    private float[] mLeft   = { 255, 255, 153, 255 };
    private float[] mRight  = { 51, 102, 204, 255 };
    private float   mFAlpha = 0.7f;

    public Kids(boolean isFromEncode, MicroMovieActivity activity, ProcessGL processGL) {
        this(activity, processGL);
        mIsFromEncode = isFromEncode;
    }

    public Kids(MicroMovieActivity activity, ProcessGL processGL) {
        super(activity, processGL);

        for(int i = 0; i < mLeft.length; i++) {
            mLeft[i] = (mLeft[i] * (1 - mFAlpha) + 255 * mFAlpha) / 255;
        }

        for(int i = 0; i < mRight.length; i++) {
            mRight[i] = (mRight[i] * (1 - mFAlpha) + 255 * mFAlpha) / 255;
        }

        ArrayList<String> mString_1 = new ArrayList<String>();
        mString_1.add(":D");

        ArrayList<String> mString_2 = new ArrayList<String>();
        mString_2.add(":D / Happy Day!");

        float mRatio = processGL.ScreenRatio;

        mEffects.add(EffectLib.String(new int[] { 700, 700, 700 }, 2000, new boolean[] { false, false, false }, Shader.String, mString_1,
                new boolean[] { false, false, false }, new int[] { Easing.easeOutBack, 0, Easing.easeInBack }, new float[] { 0.0f, 1.0f, 1.0f },
                new float[] { 1.0f, 1.0f, 0.0f }, new float[] { 1.0f, 1.0f, 1.0f }, new float[] { 1.0f, 1.0f, 1.0f }, 1.0f, 1.0f, new int[] {
                        StringLoader.STRING_BLUE, StringLoader.STRING_BLUE, StringLoader.STRING_BLUE }, new int[] { 3, 3, 3 }, 96, 0));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[] { 600, 2300 }, 2400, Shader.Circle_Mask, new boolean[] { false, false }, new int[] {
                0, 0 }, 0, 0, new float[] { 1.0f, 0.97932f }, new float[] { 0.97932f, 0.9f }, new float[] { 0.0f, 1.0f }, new float[] { 1.0f, 1.0f },
                1.0f, 1.0f, new int[] { Mask.TRANS_IN_SMALL, Mask.SHOWN }, new int[] { 7, 2 }));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[] { 500, 1000 }, 1000, Shader.Circle_Mask_Cover, new boolean[] { true, false },
                new int[] { 0, 0 }, 0, 0, new float[] { 1.1f, 1.067f }, new float[] { 1.067f, 1.0f }, new float[] { 1.0f, 1.0f }, new float[] { 1.0f,
                        1.0f }, 0.8f, 0.8f, new int[] { Mask.SHOWN, Mask.SHOWN }, new int[] { 2, 2 }));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[] { 500, 1000 }, 800, Shader.Circle_Mask_Cover, new boolean[] { true, false },
                new int[] { 0, 0 }, 0, 0, new float[] { 1.1f, 1.067f }, new float[] { 1.067f, 1.0f }, new float[] { 1.0f, 1.0f }, new float[] { 1.0f,
                        1.0f }, 0.8f, 0.8f, new int[] { Mask.SHOWN, Mask.SHOWN }, new int[] { 2, 2 }));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[] { 700, 1000, 500 }, 1700, Shader.Circle_Mask_Cover,
                new boolean[] { true, false, false }, new int[] { 0, 0, 0 }, 0, 0, new float[] { 1.1f, 1.0737f, 1.0106f }, new float[] { 1.0737f,
                        1.0106f, 1.0f }, new float[] { 1.0f, 1.0f, 1.0f }, new float[] { 1.0f, 1.0f, 0.0f }, 1.0f, 1.0f, new int[] { Mask.TRANS_OUT,
                        Mask.GONE, Mask.GONE }, new int[] { 2, 2, 2 }));

        mEffects.add(EffectLib
                .Rotate_Translate(new int[] { 700, 1200, 1000 }, 700, Shader.Rotate, new boolean[] { false, false, false }, new int[] {
                        Easing.easeInExpo, 0, Easing.easeInOutExpo }, 0, 0, new float[] { 0.0f, 0.0f, 0.0f }, new float[] { 0.0f, 0.0f,
                        mRatio * -1.5f }, new float[] { 0.0f, 0.0f, 0.0f }, new float[] { 0.0f, 0.0f, 1.5f }, new float[] { 2.0f, 1.0f, 1.0f },
                        new float[] { 1.0f, 1.0f, 1.2f }, new float[] { 0.0f, 1.0f, 1.0f }, new float[] { 1.0f, 1.0f, 1.0f }, new float[] { 180.0f,
                                0.0f, 0.0f }, new float[] { 360.0f, 0.0f, 0.0f }, 1.0f, 1.0f, new int[] { 0, 0, 0 }, new int[] { 0, -1, -1 },
                        new int[] { 3, 1, 4 }));

        mEffects.add(EffectLib.String_Translate(new int[] { 100, 1100, 1000 }, 1200, new boolean[] { false, false, false }, Shader.String, null,
                new boolean[] { false, false, false }, new int[] { 0, 0, Easing.easeInOutExpo }, 0, 0, new float[] { mRatio * 0.65f, mRatio * 0.65f,
                        mRatio * 0.65f }, new float[] { mRatio * 0.65f, mRatio * 0.65f, mRatio * -0.85f }, new float[] { 0.0f, 0.0f, 0.0f },
                new float[] { 0.0f, 0.0f, 1.5f }, new float[] { 1.0f, 1.0f, 1.0f }, new float[] { 1.0f, 1.0f, 1.0f },
                new float[] { 0.0f, 1.0f, 1.0f }, new float[] { 1.0f, 1.0f, 1.0f }, 1.0f, 1.0f, new int[] { StringLoader.STRING_KIDS_CIRCLE_DATE,
                        StringLoader.STRING_KIDS_CIRCLE_DATE, StringLoader.STRING_KIDS_CIRCLE_DATE }, new int[] { 1, 1, 1 }, 33, 1));

        // --#8
        mEffects.add(EffectLib.Rotate_Translate(new int[] { 1000, 1000, 1000 }, 0, Shader.Rotate, new boolean[] { false, false, false }, new int[] {
                Easing.easeInOutExpo, 0, Easing.easeInOutExpo }, 0, 0, new float[] { mRatio * 1.5f, 0.0f, 0.0f }, new float[] { 0.0f, 0.0f,
                mRatio * 1.5f }, new float[] { -1.5f, 0.0f, 0.0f }, new float[] { 0.0f, 0.0f, 1.5f }, new float[] { 1.2f, 1.0f, 1.0f }, new float[] {
                1.0f, 1.0f, 1.2f }, new float[] { 1.0f, 1.0f, 1.0f }, new float[] { 1.0f, 1.0f, 1.0f }, new float[] { 0.0f, 0.0f, 0.0f },
                new float[] { 0.0f, 0.0f, 0.0f }, 1.0f, 1.0f, new int[] { 0, 0, 0 }, new int[] { -1, -1, -1 }, new int[] { 5, 1, 4 }));

        mEffects.add(EffectLib.String_Translate(new int[] { 1000, 1000, 1000 }, 2000, new boolean[] { false, false, false }, Shader.String, null,
                new boolean[] { false, false, false }, new int[] { Easing.easeInOutExpo, 0, Easing.easeInOutExpo }, 0, 0, new float[] {
                        mRatio * 2.15f, mRatio * 0.65f, mRatio * 0.65f }, new float[] { mRatio * 0.65f, mRatio * 0.65f, mRatio * 2.15f },
                new float[] { -1.5f, 0.0f, 0.0f }, new float[] { 0.0f, 0.0f, 1.5f }, new float[] { 1.0f, 1.0f, 1.0f },
                new float[] { 1.0f, 1.0f, 1.0f }, new float[] { 0.0f, 1.0f, 1.0f }, new float[] { 1.0f, 1.0f, 1.0f }, 1.0f, 1.0f, new int[] {
                        StringLoader.STRING_KIDS_CIRCLE_DATE, StringLoader.STRING_KIDS_CIRCLE_DATE, StringLoader.STRING_KIDS_CIRCLE_DATE },
                new int[] { 1, 1, 1 }, 33, 1));

        // --#9
        mEffects.add(EffectLib.Rotate_Translate(new int[] { 1000, 1000, 1000 }, 0, Shader.Rotate, new boolean[] { false, false, false }, new int[] {
                Easing.easeInOutExpo, 0, Easing.easeInOutExpo }, 0, 0, new float[] { mRatio * -1.5f, 0.0f, 0.0f }, new float[] { 0.0f, 0.0f,
                mRatio * -1.5f }, new float[] { -1.5f, 0.0f, 0.0f }, new float[] { 0.0f, 0.0f, 1.5f }, new float[] { 1.2f, 1.0f, 1.0f }, new float[] {
                1.0f, 1.0f, 1.2f }, new float[] { 1.0f, 1.0f, 1.0f }, new float[] { 1.0f, 1.0f, 1.0f }, new float[] { 0.0f, 0.0f, 0.0f },
                new float[] { 0.0f, 0.0f, 0.0f }, 1.0f, 1.0f, new int[] { 0, 0, 0 }, new int[] { -1, -1, -1 }, new int[] { 5, 1, 4 }));

        mEffects.add(EffectLib.String_Translate(new int[] { 1000, 1000, 1000 }, 2000, new boolean[] { false, false, false }, Shader.String, null,
                new boolean[] { false, false, false }, new int[] { Easing.easeInOutExpo, 0, Easing.easeInOutExpo }, 0, 0, new float[] {
                        mRatio * -0.85f, mRatio * 0.65f, mRatio * 0.65f }, new float[] { mRatio * 0.65f, mRatio * 0.65f, mRatio * -0.85f },
                new float[] { -1.5f, 0.0f, 0.0f }, new float[] { 0.0f, 0.0f, 1.5f }, new float[] { 1.0f, 1.0f, 1.0f },
                new float[] { 1.0f, 1.0f, 1.0f }, new float[] { 0.0f, 1.0f, 1.0f }, new float[] { 1.0f, 1.0f, 1.0f }, 1.0f, 1.0f, new int[] {
                        StringLoader.STRING_KIDS_CIRCLE_DATE, StringLoader.STRING_KIDS_CIRCLE_DATE, StringLoader.STRING_KIDS_CIRCLE_DATE },
                new int[] { 1, 1, 1 }, 33, 1));

        // --#10
        mEffects.add(EffectLib.Rotate_Translate(new int[] { 1000, 700, 600 }, 0, Shader.Rotate, new boolean[] { false, false, false }, new int[] {
                Easing.easeInOutExpo, 0, Easing.easeInExpo }, 0, 0, new float[] { mRatio * 1.5f, 0.0f, 0.0f }, new float[] { 0.0f, 0.0f, 0.0f },
                new float[] { -1.5f, 0.0f, 0.0f }, new float[] { 0.0f, 0.0f, 0.0f }, new float[] { 1.2f, 1.0f, 1.0f },
                new float[] { 1.0f, 1.0f, 0.0f }, new float[] { 1.0f, 1.0f, 1.0f }, new float[] { 1.0f, 1.0f, 0.0f },
                new float[] { 0.0f, 0.0f, 0.0f }, new float[] { 0.0f, 0.0f, 360.0f }, 1.0f, 1.0f, new int[] { 0, 0, 0 }, new int[] { -1, -1, 1 },
                new int[] { 5, 1, 2 }));

        mEffects.add(EffectLib.String_Translate(new int[] { 1000, 700, 100 }, 2300, new boolean[] { false, false, false }, Shader.String, null,
                new boolean[] { false, false, false }, new int[] { Easing.easeInOutExpo, 0, Easing.easeInOutExpo }, 0, 0, new float[] {
                        mRatio * 2.15f, mRatio * 0.65f, mRatio * 0.65f }, new float[] { mRatio * 0.65f, mRatio * 0.65f, mRatio * 0.65f },
                new float[] { -1.5f, 0.0f, 0.0f }, new float[] { 0.0f, 0.0f, 0.0f }, new float[] { 1.0f, 1.0f, 1.0f },
                new float[] { 1.0f, 1.0f, 1.0f }, new float[] { 0.0f, 1.0f, 1.0f }, new float[] { 1.0f, 1.0f, 0.0f }, 1.0f, 1.0f, new int[] {
                        StringLoader.STRING_KIDS_CIRCLE_DATE, StringLoader.STRING_KIDS_CIRCLE_DATE, StringLoader.STRING_KIDS_CIRCLE_DATE },
                new int[] { 1, 1, 1 }, 33, 1));

        // --#11, 13/
        mEffects.add(EffectLib.Scale_Fade(processGL, new int[] { 200, 2900, 600, 400 }, 400, Shader.Lattice_Blue_Bar_Mask, new boolean[] { false,
                false, true, false }, new int[] { 0, 0, 0, 0 }, 0, 0, new float[] { 0.0f, 1.0f, 1.166f, 0.0f }, new float[] { 1.0f, 1.166f, 1.2f,
                0.0f }, new float[] { 0.0f, 1.0f, 1.0f, 0.0f }, new float[] { 1.0f, 1.0f, 1.0f, 0.0f }, 1.0f, 1.0f, new int[] { Mask.TRANS_OUT_FULL,
                0, 0, 0 }, new int[] { 2, 2, 2, 2 }));

        // --#12 27+6+2 27+6
        mEffects.add(EffectLib.String(new int[] { 3500 }, 3300, new boolean[] { true, false }, Shader.String, null, new boolean[] { false, false,
                false }, new int[] { 0, 0, 0 }, new float[] { 1.0f, 1.0f, 1.0f }, new float[] { 1.0f, 1.0f, 0.0f }, new float[] { 1.0f, 1.0f, 1.0f },
                new float[] { 1.0f, 1.0f, 1.0f }, 1.0f, 1.0f, new int[] { StringLoader.STRING_KIDS_ICON_A, StringLoader.STRING_KIDS_ICON_A },
                new int[] { 9, 9, 9 }, 40, 1));

        // --20s
        // --#13/, 14, 16/
        mEffects.add(EffectLib.Scale_Fade(processGL, new int[] { 400, 600, 2400, 600, 400 }, 0, Shader.Lattice_Blue_Bar, new boolean[] { false, true,
                false, true, false }, new int[] { 0, 0, 0, 0, 0 }, 0, 0, new float[] { 1.0f, 1.0f, 1.033f, 1.166f, 0.0f }, new float[] { 1.0f,
                1.033f, 1.166f, 1.2f, 0.0f }, new float[] { 0.0f, 0.0f, 1.0f, 1.0f, 0.0f }, new float[] { 0.0f, 1.0f, 1.0f, 1.0f, 0.0f }, 1.0f, 1.0f,
                new int[] { 0, Shader.LATTICE_BLUE_BAR_GONE, 0, 0, 0 }, new int[] { 5, 5, 2, 5, 5 }));

        // --#15
        mEffects.add(EffectLib.String(new int[] { 4200 }, 4000, new boolean[] { true, false }, Shader.String, null, new boolean[] { false, false,
                false }, new int[] { 0, 0, 0 }, new float[] { 1.0f, 1.0f, 1.0f }, new float[] { 1.0f, 1.0f, 0.0f }, new float[] { 1.0f, 1.0f, 1.0f },
                new float[] { 1.0f, 1.0f, 1.0f }, 1.0f, 1.0f, new int[] { StringLoader.STRING_KIDS_ICON_B, StringLoader.STRING_KIDS_ICON_B },
                new int[] { 9, 9, 9 }, 40, 1));

        // --#16/, 17, 19/
        mEffects.add(EffectLib.Scale_Fade(processGL, new int[] { 400, 600, 1900, 600, 400 }, 0, Shader.Lattice_Blue_Bar, new boolean[] { false, true,
                false, true, false }, new int[] { 0, 0, 0, 0, 0 }, 0, 0, new float[] { 1.0f, 1.0f, 1.038f, 1.161f, 0.0f }, new float[] { 1.0f,
                1.038f, 1.161f, 1.2f, 0.0f }, new float[] { 0.0f, 0.0f, 1.0f, 1.0f, 0.0f }, new float[] { 0.0f, 1.0f, 1.0f, 1.0f, 0.0f }, 1.0f, 1.0f,
                new int[] { 0, Shader.LATTICE_BLUE_BAR_GONE, 0, 0, 0 }, new int[] { 5, 5, 2, 5, 5 }));

        mEffects.add(EffectLib.String(new int[] { 3700 }, 3500, new boolean[] { true, false }, Shader.String, null, new boolean[] { false, false,
                false }, new int[] { 0, 0, 0 }, new float[] { 1.0f, 1.0f, 1.0f }, new float[] { 1.0f, 1.0f, 0.0f }, new float[] { 1.0f, 1.0f, 1.0f },
                new float[] { 1.0f, 1.0f, 1.0f }, 1.0f, 1.0f, new int[] { StringLoader.STRING_KIDS_ICON_A, StringLoader.STRING_KIDS_ICON_A },
                new int[] { 9, 9, 9 }, 40, 1));

        // --#19/, 20, 22/
        mEffects.add(EffectLib.Scale_Fade(processGL, new int[] { 400, 600, 1500, 600, 400 }, 0, Shader.Lattice_Blue_Bar, new boolean[] { false, true,
                false, true, false }, new int[] { 0, 0, 0, 0, 0 }, 0, 0, new float[] { 1.0f, 1.0f, 1.044f, 1.155f, 0.0f }, new float[] { 1.0f,
                1.044f, 1.155f, 1.2f, 0.0f }, new float[] { 0.0f, 0.0f, 1.0f, 1.0f, 0.0f }, new float[] { 0.0f, 1.0f, 1.0f, 1.0f, 0.0f }, 1.0f, 1.0f,
                new int[] { 0, Shader.LATTICE_BLUE_BAR_GONE, 0, 0, 0 }, new int[] { 5, 5, 2, 5, 5 }));

        mEffects.add(EffectLib.String(new int[] { 3300 }, 3300, new boolean[] { true, false }, Shader.String, null, new boolean[] { false, false,
                false }, new int[] { 0, 0, 0 }, new float[] { 1.0f, 1.0f, 1.0f }, new float[] { 1.0f, 1.0f, 0.0f }, new float[] { 1.0f, 1.0f, 1.0f },
                new float[] { 1.0f, 1.0f, 1.0f }, 1.0f, 1.0f, new int[] { StringLoader.STRING_KIDS_ICON_B, StringLoader.STRING_KIDS_ICON_B },
                new int[] { 9, 9, 9 }, 40, 1));

        // --30.6s
        // --#23, #24
        mEffects.add(EffectLib.String(new int[] { 600, 900, 2200 }, 3300, new boolean[] { false, false, false }, Shader.Lattice_Blue_Bar_String,
                mString_2, new boolean[] { true, false, false }, new int[] { 0, 0, 0 }, new float[] { 1.4f, 1.24f, 1.0f }, new float[] { 1.24f, 1.0f,
                        1.0f }, new float[] { 1.0f, 1.0f, 1.0f }, new float[] { 1.0f, 1.0f, 1.0f }, 1.0f, 1.0f, new int[] {
                        StringLoader.STRING_KIDS_END, StringLoader.STRING_KIDS_END, StringLoader.STRING_KIDS_END }, new int[] { 2, 2, 2 }, 80, 0));

        mEffects.add(EffectLib.String(new int[] { 500 }, 500, new boolean[] { false }, Shader.CMask, mString_2, new boolean[] { true },
                new int[] { 0 }, new float[] { 0.0f }, new float[] { 0.0f }, new float[] { 0.0f }, new float[] { 0.0f }, 1.0f, 1.0f,
                new int[] { Mask.TRANS_IN_BIG }, new int[] { 2 }, 0, 0));

        mEffects.add(EffectLib.Slogan(new int[] { 500, 1000, 2300 }, 3700, Shader.Slogan_TypeB, new float[] { 0.0f, 1.0f, 1.0f }, new float[] { 1.0f,
                1.0f, 1.0f }, new int[] { Slogan.SLOGAN_TEXT, Slogan.SLOGAN_DATE, Slogan.SLOGAN_ALL }, new boolean[] { false, false, false }));

        init();
    }

    @Override
    public int getMusicId() {
        return MusicManager.MUISC_ASUS_KIDS;
    }

    @Override
    public int getFilterId() {
        return FilterChooser.SEPIA;
    }

    @Override
    public float ColorRed() {
        if(shouldReturnDefaultColor())
            return super.ColorRed();
        else
            return (float) mRed / 255;
    }

    @Override
    public float ColorGreen() {
        if(shouldReturnDefaultColor())
            return super.ColorGreen();
        else
            return (float) mGreen / 255;
    }

    @Override
    public int getFilterNumber() {
        return 3;
    }

    @Override
    public float ColorBlue() {
        if(shouldReturnDefaultColor())
            return super.ColorBlue();
        else
            return (float) mBlue / 255;
    }

    @Override
    public int GetScriptId() {
        return ThemeAdapter.TYPE_KIDS;
    }

    @Override
    public int GetSloganType() {
        return 3;
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
