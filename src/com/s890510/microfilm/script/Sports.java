package com.s890510.microfilm.script;

import java.util.ArrayList;

import com.asus.gallery.micromovie.MicroMovieActivity;
import com.asus.gallery.micromovie.MusicManager;
import com.asus.gallery.micromovie.ProcessGL;
import com.asus.gallery.micromovie.Slogan;
import com.asus.gallery.micromovie.StringLoader;
import com.asus.gallery.micromovie.ThemeAdapter;
import com.s890510.microfilm.mask.Mask;
import com.s890510.microfilm.script.effects.EffectLib;
import com.s890510.microfilm.shader.Shader;

public class Sports extends BasicScript {
    private float[] mLeft = {13, 145, 255, 255};
    private float[] mRight = {255, 156, 0, 255};
    private float mFAlpha = 0.5f;

    public Sports(boolean isFromEncode, MicroMovieActivity activity, ProcessGL processGL) {
        this(activity, processGL);
        mIsFromEncode = isFromEncode;
    }

    public Sports(MicroMovieActivity activity, ProcessGL processGL) {
        super(activity, processGL);

        for(int i=0; i<mLeft.length; i++) {
            mLeft[i] = (mLeft[i]*(1-mFAlpha) + 255*mFAlpha)/255;
        }

        for(int i=0; i<mRight.length; i++) {
            mRight[i] = (mRight[i]*(1-mFAlpha) + 255*mFAlpha)/255;
        }

        //Get Account info
        String Owner = mActivity.getOwnerUser();

        if(Owner == null) {
            Owner = "Your";
        }

        ArrayList<String> mString_1 = new ArrayList<String>();
        mString_1.add("My");
        mString_1.add("Life Style");

        ArrayList<String> mString_2 = new ArrayList<String>();
        mString_2.add("Make Life");
        mString_2.add("Different");

        ArrayList<String> mString_3 = new ArrayList<String>();
        mString_3.add("Stay Alive");

        mEffects.add(EffectLib.Translate(processGL, new int[]{700, 300, 1100}, 0, Shader.Default,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0, false,
                new float[]{0.5f, 0.16f, 0.83f},
                new float[]{0.5f, 0.5f, 0.34f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 0.0f, 1.0f},
                new float[]{0.0f, 1.0f, 1.0f}, 0.5f, 1.0f, new int[]{0, 0, 0}, new int[]{6, 6, 6}));

        mEffects.add(EffectLib.String(new int[]{400, 300, 1500}, 2000, new boolean[]{true, true, false}, Shader.String, mString_1,
                new boolean[]{false, false, false}, new int[]{0, 0, 0},
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_SHADER, StringLoader.STRING_SHADER, StringLoader.STRING_NOBK}, new int[]{6, 6, 6}, 92, 0));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{500, 1200, 500}, 1700, Shader.Scale_Mask,
                new boolean[]{false, false, false}, new int[]{2, 0, 0}, 0, 0,
                new float[]{0.45f, 0.0f, 1.0f}, new float[]{1.0f, 0.0f, 1.0f}, new float[]{0.0f, 0.0f, 1.0f}, new float[]{1.0f, 0.0f, 0.0f},
                0.8f, 0.8f, new int[]{0, 0, 0}, new int[]{2, 1, 2}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{500, 600, 500}, 1100, Shader.Scale_Mask,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{1.1f, 0.0f, 1.0f}, new float[]{1.0f, 0.0f, 1.0f}, new float[]{0.0f, 0.0f, 1.0f}, new float[]{1.0f, 0.0f, 0.0f},
                0.8f, 0.8f, new int[]{0, 0, 0}, new int[]{2, 1, 2}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{500, 600, 500}, 1100, Shader.Scale_Mask,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{1.1f, 0.0f, 1.0f}, new float[]{1.0f, 0.0f, 1.0f}, new float[]{0.0f, 0.0f, 1.0f}, new float[]{1.0f, 0.0f, 0.0f},
                0.8f, 0.8f, new int[]{0, 0, 0}, new int[]{2, 1, 2}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{500, 800, 300, 1000}, 2300, Shader.Scale_Mask,
                new boolean[]{false, false, false, false}, new int[]{0, 0, 0, 0}, 0, 0,
                new float[]{1.1f, 0.0f, 0.0f, 0.0f}, new float[]{1.0f, 0.0f, 0.0f, 0.0f}, new float[]{0.0f, 0.0f, 0.0f, 0.0f}, new float[]{1.0f, 0.0f, 0.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, Mask.TRANS_OUT, Mask.GONE}, new int[]{2, 1, 1, 1}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{300, 900}, 0, Shader.Cover_Top,
                new boolean[]{true, false}, new int[]{0, 0}, 0, 0,
                new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f},
                0.5f, 1.0f, new int[]{0, 0}, new int[]{3 ,3}));
        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{300, 900}, 900, Shader.Cover_Bottom,
                new boolean[]{true, false}, new int[]{0, 0}, 0, 0,
                new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f},
                0.5f, 1.0f, new int[]{0, 0}, new int[]{4 ,4}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{300, 800}, 0, Shader.Cover_Half_Right,
                new boolean[]{true, false}, new int[]{0, 0}, 0, 0,
                new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f},
                0.5f, 1.0f, new int[]{0, 0}, new int[]{3 ,3}));
        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{300, 800}, 800, Shader.Cover_Half_Left,
                new boolean[]{true, false}, new int[]{0, 0}, 0, 0,
                new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f},
                0.5f, 1.0f, new int[]{0, 0}, new int[]{4 ,4}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{300, 900}, 0, Shader.Cover_Top,
                new boolean[]{true, false}, new int[]{0, 0}, 0, 0,
                new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f},
                0.5f, 1.0f, new int[]{0, 0}, new int[]{3 ,3}));
        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{300, 900}, 900, Shader.Cover_Bottom,
                new boolean[]{true, false}, new int[]{0, 0}, 0, 0,
                new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f},
                0.5f, 1.0f, new int[]{0, 0}, new int[]{4 ,4}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{900, 500, 700, 500}, 0, Shader.Scale_Fade,
                new boolean[]{false, false, false, false}, new int[]{0, 0, 0, 0}, 0, 0,
                new float[]{0.0f, 0.55f, 0.57f, 0.6f}, new float[]{0.0f, 0.57f, 0.6f, 0.62f}, new float[]{0.0f, 0.0f, 1.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0, 0}, new int[]{2, 2, 2, 2}));

        mEffects.add(EffectLib.Translate(processGL, new int[]{500, 400, 500}, 2100, Shader.Mirror_Vertical_TB,
                new boolean[]{true, false, false}, new int[]{2, 0, 1}, 0, 0, true,
                new float[]{-1.0f, 0, 0},
                new float[]{1.0f, 0, 0}, new float[]{-1.0f, -1.0f, -1.0f}, new float[]{-1.0f, -1.0f, -1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                new float[]{1.0f, 1.0f, 1.0f}, 1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{1, 3, 4}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{500, 700, 500}, 1200, Shader.Scale_Fade,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{0.6f, 0.62f, 0.65f}, new float[]{0.62f, 0.65f, 0.67f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 2, 2}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{500, 700, 500}, 1200, Shader.Scale_Fade,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{0.65f, 0.67f, 0.70f}, new float[]{0.67f, 0.70f, 0.72f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 2, 2}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{500, 700, 500}, 1700, Shader.Scale_Fade,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0,
                new float[]{0.70f, 0.72f, 0.75f}, new float[]{0.72f, 0.75f, 0.9f}, new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{2, 2, 2}));

        mEffects.add(EffectLib.Translate(processGL, new int[]{1100, 300, 1500}, 0, Shader.Default,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0, false,
                new float[]{0.5f, 0.16f, 0.83f}, new float[]{0.5f, 0.5f, 0.34f},
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{0.0f, 0.0f, 1.0f}, new float[]{0.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{6, 6, 6}));

        mEffects.add(EffectLib.String(new int[]{500, 600, 1900}, 2000, new boolean[]{true, true, false}, Shader.String, mString_2,
                new boolean[]{false, false, false}, new int[]{0, 0, 0},
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.1f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_FADE_LIGHT, StringLoader.STRING_FADE_LIGHT, StringLoader.STRING_NOBK_SCALE}, new int[]{10, 10, 6}, 100, 0));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{1000, 1000}, 1500, Shader.Lattice_Tilted_Right_R,
                new boolean[]{true, false}, new int[]{0, 0}, 0, 0,
                new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f}, new float[]{1.0f, 1.0f},
                1.0f, 1.0f, new int[]{0, 0}, new int[]{1, 1}));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{500, 500, 500}, 1500, Shader.Lattice_Cross_4,
                new boolean[]{true, false, true}, new int[]{0, 0, 0}, 0, 0,
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new int[]{0, 0, Shader.LATTICE_CROSS_2}, new int[]{1, 1, 1}));

        mEffects.add(EffectLib.Cover(new int[]{500, 600, 500}, 0, Shader.Cover_Top,
                new boolean[]{true, false, true}, new int[]{0, 0, 0}, 0, 0, -processGL.ScreenRatio, -1,
                new float[]{0.5f, 0.5f, 0.5f}, new float[]{0.5f, 0.5f, 0.5f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new int[]{0, 0, Mask.GONE}, new int[]{1, 1, 1}));
        mEffects.add(EffectLib.Cover(new int[]{500, 600, 500}, 0, Shader.Cover_Left,
                new boolean[]{true, false, true}, new int[]{0, 0, 0}, 0, 0, processGL.ScreenRatio, -1,
                new float[]{0.5f, 0.5f, 0.5f}, new float[]{0.5f, 0.5f, 0.5f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new int[]{0, 0, Mask.GONE}, new int[]{1, 1, 1}));
        mEffects.add(EffectLib.Cover(new int[]{500, 600, 500}, 0, Shader.Cover_Right,
                new boolean[]{true, false, true}, new int[]{0, 0, 0}, 0, 0, -processGL.ScreenRatio, 1,
                new float[]{0.5f, 0.5f, 0.5f}, new float[]{0.5f, 0.5f, 0.5f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new int[]{0, 0, Mask.GONE}, new int[]{1, 1, 1}));
        mEffects.add(EffectLib.Cover(new int[]{500, 600, 500}, 1600, Shader.Cover_Bottom,
                new boolean[]{true, false, true}, new int[]{0, 0, 0}, 0, 0, processGL.ScreenRatio, 1,
                new float[]{0.5f, 0.5f, 0.5f}, new float[]{0.5f, 0.5f, 0.5f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new int[]{0, 0, Mask.GONE}, new int[]{1, 1, 1}));

        mEffects.add(EffectLib.Translate(processGL, new int[]{500, 500, 500}, 1500, Shader.Mirror_Tilted_Left,
                new boolean[]{false, false, false}, new int[]{0, 0, 0}, 0, 0, true,
                new float[]{-0.1f, 0, 0}, new float[]{-0.1f, 0, 0},
                new float[]{1.0f, 1.0f, 1.01f}, new float[]{1.0f, 1.01f, 1.0225f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 0.0f},
                1.0f, 1.0f, new int[]{0, 0, 0}, new int[]{5, 2, 2}));

        mEffects.add(EffectLib.Translate(processGL, new int[]{3400}, 0, Shader.Default,
                new boolean[]{false}, new int[]{0}, 0, 0, false, new float[]{1.0f},
                new float[]{0.5f}, new float[]{1.0f}, new float[]{1.0f}, new float[]{1.0f},
                new float[]{1.0f}, 1.0f, 1.0f, new int[]{0}, new int[]{6}));

        mEffects.add(EffectLib.String(new int[]{3400}, 2400, new boolean[]{false}, Shader.String, mString_3, new boolean[]{false}, new int[]{0},
                new float[]{1.0f}, new float[]{1.1f}, new float[]{1.0f}, new float[]{1.0f}, 1.0f, 1.0f,
                new int[]{StringLoader.STRING_NOBK}, new int[]{2}, 95, 0));

        mEffects.add(EffectLib.Scale_Fade(processGL, new int[]{900, 400, 600}, 1600, Shader.Lattice_Tilted_Right_R,
                new boolean[]{true, false, true}, new int[]{0, 0, 0}, 0, 0,
                new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                1.0f, 1.0f, new int[]{0, 0, Shader.LATTICE_HORIZONTAL}, new int[]{1, 1, 1}));

        mEffects.add(EffectLib.Slogan(new int[]{500, 1000, 3400}, 4800, Shader.Slogan_TypeB,
                new float[]{0.0f, 1.0f, 1.0f}, new float[]{1.0f, 1.0f, 1.0f},
                new int[]{Slogan.SLOGAN_TEXT, Slogan.SLOGAN_DATE, Slogan.SLOGAN_ALL}, new boolean[]{false, false, false}));

        init();
    }

    @Override
    public int getMusicId() {
        return MusicManager.MUISC_ASUS_YOUNG;
    }

    @Override
    public int getFilterNumber() {
        return 1;
    }

    @Override
    public int GetScriptId() {
        return ThemeAdapter.TYPE_SPORTS;
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
