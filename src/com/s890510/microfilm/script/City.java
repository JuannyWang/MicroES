package com.s890510.microfilm.script;

import java.util.ArrayList;

import com.s890510.microfilm.MicroFilmActivity;
import com.s890510.microfilm.MusicManager;
import com.s890510.microfilm.ThemeAdapter;
import com.s890510.microfilm.draw.GLDraw;
import com.s890510.microfilm.draw.Slogan;
import com.s890510.microfilm.draw.StringLoader;
import com.s890510.microfilm.script.effects.EffectLib;
import com.s890510.microfilm.shader.Shader;

public class City extends BasicScript {
    private float mRed = 0;
    private float mGreen = 0;
    private float mBlue = 0;

    private float[] mLeft = {51, 154, 252, 255};
    private float[] mRight = {51, 154, 252, 255};
    private float mFAlpha = 0.85f;

    public City(boolean isFromEncode, MicroFilmActivity activity, GLDraw gldraw) {
        this(activity, gldraw);
        mIsFromEncode = isFromEncode;
    }

    public City(MicroFilmActivity activity, GLDraw gldraw) {
        super(activity, gldraw);

        for(int i=0; i<mLeft.length; i++) {
            mLeft[i] = (mLeft[i]*(1-mFAlpha) + 255*mFAlpha)/255;
        }

        for(int i=0; i<mRight.length; i++) {
            mRight[i] = (mRight[i]*(1-mFAlpha) + 255*mFAlpha)/255;
        }

        float mScreenRatio = gldraw.ScreenRatio;

        ArrayList<String> mString_1 = new ArrayList<String>();
        mString_1.add("live your life");

        ArrayList<String> mString_2 = new ArrayList<String>();
        mString_2.add(mActivity.getItemCount() + " pictures");

        mEffects.add(EffectLib.String(new int[]{500, 600, 700, 500, 500}, 2300, new boolean[]{false, false, true, false, false}, Shader.String, null,
                new boolean[]{false, false, false, false, false}, new int[]{0, 0, 0, 0, 0},
                new float[]{1.3f, 1.0f, 1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 0.0f},
                new float[]{0.0f, 1.0f, 1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f, 1.0f, 0.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_ANIM_CITY_BLUE, StringLoader.STRING_ANIM_CITY_BLUE, StringLoader.STRING_ANIM_CITY_TRANS, StringLoader.STRING_ANIM_CITY_BLACK, StringLoader.STRING_ANIM_CITY_BLACK},
                new int[]{2, 2, 2, 2, 7}, 80, 0));

        //--#1
        mEffects.add(EffectLib.Bound(gldraw, new int[]{500, 3700, 400}, 0, Shader.Default, 0, 0,
                new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, -mScreenRatio*2},
                new float[]{3.0f, 0.1f, -0.1f}, new float[]{0.1f, -0.1f, -0.1f}, -0.8f, 0.8f,
                new float[]{2.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new boolean[]{false, false, false}, new int[]{Shader.LIMIT_Y_TOP, Shader.LIMIT_Y, Shader.LIMIT_Y}, new int[]{2, 2, 2}));

        mEffects.add(EffectLib.String(new int[]{500, 3700}, 4200, new boolean[]{true, false}, Shader.String, null,
                new boolean[]{false, false}, new int[]{0, 0},
                new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{0.0f, 1.0f}, new float[]{1.0f, 1.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_DATE_CITY_TRANS, StringLoader.STRING_DATE_CITY}, new int[]{1, 1}, 30, 1));

        //--#2
        mEffects.add(EffectLib.Bound(gldraw, new int[]{400, 2300, 400}, 0, Shader.Default, 0, 0,
                new float[]{mScreenRatio*2, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, -mScreenRatio*2},
                new float[]{0.1f, 0.1f, -0.1f}, new float[]{0.1f, -0.1f, -0.1f}, -0.8f, 0.8f,
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new boolean[]{false, false, false}, new int[]{Shader.LIMIT_Y, Shader.LIMIT_Y, Shader.LIMIT_Y}, new int[]{2, 2, 2}));

        mEffects.add(EffectLib.String(new int[]{400, 2300}, 2700, new boolean[]{true, false}, Shader.String, null,
                new boolean[]{false, false}, new int[]{0, 0},
                new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{0.0f, 1.0f}, new float[]{1.0f, 1.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_DATE_CITY_TRANS, StringLoader.STRING_DATE_CITY}, new int[]{1, 1}, 30, 1));

        //--#3
        mEffects.add(EffectLib.Bound(gldraw, new int[]{400, 2200, 400}, 0, Shader.Default, 0, 0,
                new float[]{mScreenRatio*2, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, -mScreenRatio*2},
                new float[]{0.1f, 0.1f, -0.1f}, new float[]{0.1f, -0.1f, -0.1f}, -0.8f, 0.8f,
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new boolean[]{false, false, false}, new int[]{Shader.LIMIT_Y, Shader.LIMIT_Y, Shader.LIMIT_Y}, new int[]{2, 2, 2}));

        mEffects.add(EffectLib.String(new int[]{400, 2200}, 2600, new boolean[]{true, false}, Shader.String, null,
                new boolean[]{false, false}, new int[]{0, 0},
                new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{0.0f, 1.0f}, new float[]{1.0f, 1.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_DATE_CITY_TRANS, StringLoader.STRING_DATE_CITY}, new int[]{1, 1}, 30, 1));

        //--#6
        mEffects.add(EffectLib.String(new int[]{2400, 400}, 0, new boolean[]{false, false}, Shader.String, mString_1,
                new boolean[]{false, false}, new int[]{0, 0},
                new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.025f}, new float[]{0.0f, 1.0f}, new float[]{0.0f, 1.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_NONE, StringLoader.STRING_WHITE_NOBK}, new int[]{2, 2}, 70, 0));

        //--#4
        mEffects.add(EffectLib.Bound(gldraw, new int[]{400, 1800, 400}, 0, Shader.Default, 0, 0,
                new float[]{mScreenRatio*2, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f},
                new float[]{0.2f, 0.2f, 0.0f}, new float[]{0.2f, 0.0f, -2.0f}, -0.8f, 0.8f,
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new boolean[]{false, false, false}, new int[]{Shader.LIMIT_Y, Shader.LIMIT_Y, Shader.LIMIT_Y_TB}, new int[]{5, 5, 6}));

        mEffects.add(EffectLib.String(new int[]{400, 1800, 400}, 2600, new boolean[]{true, false, false}, Shader.String, null,
                new boolean[]{false, false, false}, new int[]{0, 0, 0},
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_DATE_CITY_TRANS, StringLoader.STRING_DATE_CITY, StringLoader.STRING_NONE}, new int[]{6, 6, 8}, 30, 1));

        //--#5
        /*
        mEffects.add(EffectLib.Bound(new int[]{500, 1500, 500}, 0, Shader.Default,
                new float[]{mScreenRatio*2, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f},
                new float[]{0.2f, 0.2f, 0.0f}, new float[]{0.2f, 0.0f, -2.0f}, -0.8f, 0.8f,
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new boolean[]{false, false, false}, new int[]{Shader.LIMIT_Y, Shader.LIMIT_Y, Shader.LIMIT_Y_TB}, new int[]{5, 5, 6}));

        mEffects.add(EffectLib.String(new int[]{500, 1500, 500}, 2500, new boolean[]{true, false, false}, Shader.String, null,
                new boolean[]{false, false, false}, new int[]{0, 0, 0},
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_DATE_CITY_TRANS, StringLoader.STRING_DATE_CITY, StringLoader.STRING_NONE}, new int[]{6, 6, 8}, 30, 1));
        */

        //--#6
        mEffects.add(EffectLib.String(new int[]{1900}, 1500, new boolean[]{false}, Shader.String, mString_1,
                new boolean[]{false}, new int[]{0}, new float[]{1.0208f}, new float[]{1.1f}, new float[]{1.0f}, new float[]{1.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_WHITE_NOBK}, new int[]{8}, 70, 0));

        //--#9
        mEffects.add(EffectLib.Bound(gldraw, new int[]{400, 1600, 400}, 2000, Shader.Default, 0, 0,
                new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f},
                new float[]{2.2f, 0.1f, -0.1f}, new float[]{0.1f, -0.1f, -2.2f}, -1.0f, 1.0f,
                new float[]{1.2f, 1.2f, 1.2f}, new float[]{1.2f, 1.2f, 1.2f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new boolean[]{false, false, false}, new int[]{0, 0, 0}, new int[]{6, 1, 1}));

        //--#10
        mEffects.add(EffectLib.Bound(gldraw, new int[]{400, 1600, 400}, 2000, Shader.Default, 0, 0,
                new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f},
                new float[]{2.2f, 0.1f, -0.1f}, new float[]{0.1f, -0.1f, -2.2f}, -1.0f, 1.0f,
                new float[]{1.2f, 1.2f, 1.2f}, new float[]{1.2f, 1.2f, 1.2f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new boolean[]{false, false, false}, new int[]{0, 0, 0}, new int[]{1, 1, 1}));

        //--#11
        mEffects.add(EffectLib.Bound(gldraw, new int[]{400, 1500}, 1600, Shader.Default, 0, 0,
                new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f},
                new float[]{2.2f, 0.1f}, new float[]{0.1f, -0.1f}, -1.0f, 1.0f,
                new float[]{1.2f, 1.2f}, new float[]{1.2f, 1.2f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f},
                1.0f, 1.0f, new boolean[]{false, false}, new int[]{0, 0}, new int[]{1, 1}));

        //--#12
        mEffects.add(EffectLib.Bound(gldraw, new int[]{300, 1100}, 1400, Shader.Default, 0, 0,
                new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f},
                new float[]{2.0f, 0.0f}, new float[]{0.0f, 0.0f}, -1.0f, 1.0f,
                new float[]{1.0f, 1.0107f}, new float[]{1.0107f, 1.05f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f},
                1.0f, 1.0f, new boolean[]{false, false}, new int[]{0, 0}, new int[]{7, 1}));

        //--#13
        mEffects.add(EffectLib.Bound(gldraw, new int[]{100, 300}, 400, Shader.Default, 0, 0,
                new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f},
                new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f}, -1.0f, 1.0f,
                new float[]{1.0f, 1.005f}, new float[]{1.005f, 1.02f}, new float[]{0.0f, 1.0f}, new float[]{1.0f, 1.0f},
                1.0f, 1.0f, new boolean[]{false, false}, new int[]{0, 0}, new int[]{7, 1}));

        //--#14
        mEffects.add(EffectLib.Bound(gldraw, new int[]{100, 300}, 400, Shader.Default, 0, 0,
                new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f},
                new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f}, -1.0f, 1.0f,
                new float[]{1.0f, 1.005f}, new float[]{1.005f, 1.02f}, new float[]{0.0f, 1.0f}, new float[]{1.0f, 1.0f},
                1.0f, 1.0f, new boolean[]{false, false}, new int[]{0, 0}, new int[]{7, 1}));

        //--#15
        mEffects.add(EffectLib.Bound(gldraw, new int[]{100, 300}, 400, Shader.Default, 0, 0,
                new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f},
                new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f}, -1.0f, 1.0f,
                new float[]{1.0f, 1.005f}, new float[]{1.005f, 1.02f}, new float[]{0.0f, 1.0f}, new float[]{1.0f, 1.0f},
                1.0f, 1.0f, new boolean[]{false, false}, new int[]{0, 0}, new int[]{7, 1}));

        //#16
        mEffects.add(EffectLib.Bound(gldraw, new int[]{100, 1400}, 400, Shader.Default, 0, 0,
                new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f},
                new float[]{0.0f, 0.0f}, new float[]{0.0f, 0.0f}, -1.0f, 1.0f,
                new float[]{1.0f, 1.008333f}, new float[]{1.005f, 1.06f}, new float[]{0.0f, 1.0f}, new float[]{1.0f, 1.0f},
                1.0f, 1.0f, new boolean[]{false, false}, new int[]{0, 0}, new int[]{7, 1}));

        //---24.5s

        mEffects.add(EffectLib.Bound(gldraw, new int[]{700, 400, 1200}, 1900, Shader.Cover_Percent_L, 0, 0,
                new float[]{mScreenRatio*2, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f},
                new float[]{0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f}, 0.33f, 1.0f,
                new float[]{1.0f, 1.0f, 1.0208f}, new float[]{1.0f, 1.0208f, 1.05f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new boolean[]{false, true, false}, new int[]{0, 0, 0}, new int[]{2, 2, 1}));

        mEffects.add(EffectLib.String(new int[]{300, 1400, 300}, 2000, new boolean[]{false, false, false}, Shader.Cover_String_Left, mString_2,
                new boolean[]{true, false, false}, new int[]{0, 0, 0},
                new float[]{1.0f, 1.0f, 1.0411f}, new float[]{1.0f, 1.0411f, 1.05f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_ANIM_CITY_BLACK, StringLoader.STRING_ANIM_CITY_BLACK, StringLoader.STRING_ANIM_CITY_BLACK}, new int[]{6, 6, 6}, 80, 0));

        //--#21, #22
        mEffects.add(EffectLib.Translate(gldraw, new int[]{400, 300, 300}, 700, Shader.Mirror_Tilted,
                new boolean[]{true, false, false}, new int[]{2, 0, 0}, 0, 0, true,
                new float[]{-0.5f, 0, 0}, new float[]{0.5f, 0, 0},
                new float[]{-1.0f, 1.0f, 1.015385f}, new float[]{-1.0f, 1.015385f, 1.03077f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                0.65f, 0.65f, new int[]{0, 0, 0}, new int[]{1, 2, 2}));

        //--#23
        mEffects.add(EffectLib.Scale_Fade(gldraw, new int[]{300, 400, 300}, 700, Shader.Default_White,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{0.67f, 0.676f, 0.684f}, new float[]{0.676f, 0.684f, 0.69f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 2, 2}));

        //--#24
        mEffects.add(EffectLib.Scale_Fade(gldraw, new int[]{300, 400, 300}, 700, Shader.Default_White,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{0.69f, 0.696f, 0.704f}, new float[]{0.696f, 0.704f, 0.71f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 2, 2}));

        //--#25
        mEffects.add(EffectLib.Scale_Fade(gldraw, new int[]{300, 600, 300}, 900, Shader.Default_White,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{0.71f, 0.715f, 0.725f}, new float[]{0.715f, 0.725f, 0.73f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 2, 2}));

        //--#26
        mEffects.add(EffectLib.Scale_Fade(gldraw, new int[]{300, 300, 300}, 600, Shader.Default_White,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{0.73f, 0.7366f, 0.743f}, new float[]{0.7366f, 0.743f, 0.75f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 2, 2}));

        //--#27
        /*
        mEffects.add(EffectLib.Scale_Fade(new int[]{300, 700, 300}, 1000, Shader.Default_White,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{0.75f, 0.7546f, 0.7654f}, new float[]{0.7546f, 0.7654f, 0.77f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 2, 2}));

        //--#28
        mEffects.add(EffectLib.Scale_Fade(new int[]{300, 700, 500}, 1000, Shader.Default_White,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{0.77f, 0.7746f, 0.7854f}, new float[]{0.7746f, 0.7854f, 0.79f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 2, 2}));
        */
        mEffects.add(EffectLib.Scale_Fade(gldraw, new int[]{300, 700, 1000}, 1000, Shader.Default_White,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{0.75f, 0.7546f, 0.7654f}, new float[]{0.7546f, 0.7654f, 0.77f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 2, 2}));

        mEffects.add(EffectLib.Slogan(new int[]{1000, 1000, 2900}, 4800, Shader.Slogan_TypeB,
                new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                new int[]{Slogan.SLOGAN_TEXT, Slogan.SLOGAN_DATE, Slogan.SLOGAN_ALL}, new boolean[]{false, false, false}));

        init();
    }

    @Override
    public int getMusicId() {
        return MusicManager.MUISC_ASUS_CITY_STREET;
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
    public int getFilterNumber() {
        return 3;
    }

    @Override
    public int GetScriptId() {
        return ThemeAdapter.TYPE_CITY;
    }

    @Override
    public int GetSloganType() {
        return 6;
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
