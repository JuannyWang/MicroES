package com.s890510.microfilm.script.effects;

import java.util.ArrayList;

import com.s890510.microfilm.ProcessGL;
import com.s890510.microfilm.mask.Mask;

public class ComboFactory {
    public static final int DEFAULT             = 0;
    public static final int BLINKING            = 1;
    public static final int MIRROR              = 2;
    public static final int SCALE               = 3;
    public static final int TRANS_IN_SCALE      = 4;
    public static final int SHOW_LEFT_HALF      = 5;
    public static final int SHOW_RIGHT_HALF     = 6;
    public static final int SEQUENTIAL_EFFECT_1 = 10;
    public static final int SEQUENTIAL_EFFECT_2 = 11;
    public static final int SEQUENTIAL_EFFECT_3 = 12;
    public static final int SEQUENTIAL_EFFECT_4 = 13;
    public static final int VIDEO_DEFAULT       = 20;
    public static final int VIDEO_SLIDE         = 21;
    public static final int MULTI               = 30;
    public static final int SCALE_FADE_MASK     = 31;
    public static final int SCALE_MASK          = 32;
    public static final int SCALE_FADE_MASK_OUT = 33;
    public static final int COVER               = 34;
    public static final int COVER_LEFT          = 35;
    public static final int COVER_RIGHT         = 36;
    public static final int COVER_TOP           = 37;
    public static final int COVER_BOTTOM        = 38;
    public static final int COVER_STOP          = 39;
    public static final int STRING              = 40;
    public static final int SCALE_FADE_1        = 41;
    public static final int SCALE_FADE_2        = 42;
    public static final int SCALE_FADE_3        = 43;
    public static final int SCALE_FADE_4        = 44;
    public static final int LATTICE_TILT        = 45;
    public static final int LATTICE_VERTICAL    = 46;
    public static final int LATTICE_HORIZONTAL  = 47;
    public static final int STRING_LONG         = 48;

    /*
     * public static Effect getComboEffect(int key, String shader, String str) {
     * ComboEffect mEffect = (ComboEffect)getComboEffect(key, shader);
     * mEffect.setString(str); return mEffect; }
     */
    public static Effect getComboEffect(ProcessGL processGL, int key, String shader) {
        ComboEffect mEffect = (ComboEffect) getComboEffect(processGL, key);
        mEffect.setShader(shader);
        return mEffect;
    }

    public static Effect getComboEffect(ProcessGL processGL, int key) {
        ArrayList<Effect> mElements = new ArrayList<Effect>();
        ComboEffect mEffect;
        Effect E1;
        switch (key) {
            case BLINKING:
                mElements.add(new EffectShowInCenter(150));
                mElements.add(new EffectNotShow(50));
                mElements.add(new EffectShowInCenter(150));
                mElements.add(new EffectNotShow(50));
                mElements.add(new EffectShowInCenter(150));
                mElements.add(new EffectNotShow(50));
                mElements.add(new EffectShowInCenter(150));
                mElements.add(new EffectNotShow(50));
                mElements.add(new EffectShowInCenter(150));
                mElements.add(new EffectNotShow(50));
                mElements.add(new EffectShowInCenter(150));
                mElements.add(new EffectNotShow(50));
                mElements.add(new EffectShowInCenter(150));
                mElements.add(new EffectNotShow(50));
                mElements.add(new EffectShowInCenter(150));
                mElements.add(new EffectNotShow(50));
                mElements.add(new EffectShowInCenter(150));
                mElements.add(new EffectNotShow(50));
                mElements.add(new EffectShowInCenter(150));
                mElements.add(new EffectNotShow(50));
                mEffect = new ComboEffect(mElements);
                break;
            case MIRROR:
                mElements.add(new EffectShowInCenter(500));
                mElements.add(new EffectMirror(500));
                mElements.add(new EffectShowInCenter(500));
                mElements.add(new EffectMirror(500));
                mElements.add(new EffectShowInCenter(500));
                mElements.add(new EffectMirror(500));
                mElements.add(new EffectShowInCenter(500));
                mElements.add(new EffectMirror(500));
                mEffect = new ComboEffect(mElements);
                break;
            case SCALE:
                mElements.add(new EffectShowInCenter(350));
                mElements.add(new EffectShowInCenter(350, 1.2f));
                mElements.add(new EffectShowInCenter(350));
                mElements.add(new EffectShowInCenter(350, 1.2f));
                mElements.add(new EffectShowInCenter(350));
                mElements.add(new EffectShowInCenter(350, 1.2f));
                mElements.add(new EffectShowInCenter(350));
                mEffect = new ComboEffect(mElements);
                break;
            case TRANS_IN_SCALE:
                mElements.add(new EffectTransInFromLeftBottom(1000));
                mElements.add(new EffectScale(3000));
                mEffect = new ComboEffect(mElements);

                break;
            case SHOW_LEFT_HALF:
                mElements.add(new EffectBySetting(1000, 1000, 1, 1, -0.5f, 0.5f, -0.5f, 0));
                mElements.add(new EffectShowInLeftHalf(processGL));
                mEffect = new ComboEffect(mElements);
                mEffect.setTextureRatio(0.5f, 1.0f);
                mEffect.setSleep(1000);
                break;
            case SHOW_RIGHT_HALF:
                mElements.add(new EffectBySetting(1000, 1000, 1, 1, 0.5f, -0.5f, 0.5f, 0));
                mElements.add(new EffectShowInRightHalf(processGL));
                mEffect = new ComboEffect(mElements);
                mEffect.setTextureRatio(0.5f, 1.0f);
                break;
            case SEQUENTIAL_EFFECT_1:
                mElements.add(new EffectBySetting(3000, 3000, 0.8f, 0.5f, -1f, 1f, -1f, 1f));
                mElements.add(new EffectShowInCenter(4000, 0.5f, -1f, 1f));
                mEffect = new ComboEffect(mElements);
                mEffect.setSleep(1500);
                break;
            case SEQUENTIAL_EFFECT_2:
                mElements.add(new EffectBySetting(1500, 1500, 0.8f, 0.5f, 1f, 1f, 1f, 1f));
                mElements.add(new EffectShowInCenter(4000, 0.5f, 1f, 1f));
                mEffect = new ComboEffect(mElements);
                mEffect.setSleep(1500);
                break;
            case SEQUENTIAL_EFFECT_3:
                mElements.add(new EffectShowInCenter(4000, 0.5f, 1f, -1f));
                mEffect = new ComboEffect(mElements);
                mEffect.setSleep(1500);
                break;
            case SEQUENTIAL_EFFECT_4:
                mElements.add(new EffectTransInFromLeftBottom(1000));
                mElements.add(new EffectScale(1500));
                mEffect = new ComboEffect(mElements);
                break;
            case MULTI:
                mElements.add(new EffectShowInCenter(4000));
                mEffect = new ComboEffect(mElements);
                mEffect.setSleep(3000);
                // mEffect.setMultiple(true);
                break;
            case STRING:
                mElements.add(new EffectShowInCenter(2000));
                mEffect = new ComboEffect(mElements);
                mEffect.setSleep(1700);
                break;
            case STRING_LONG:
                mElements.add(new EffectShowInCenter(2200));
                mEffect = new ComboEffect(mElements);
                mEffect.setSleep(1700);
                break;
            case SCALE_MASK:
                mElements.add(new EffectScale(200, 0.5f, 1.0f, 0.0f, 1.0f));
                mElements.add(new EffectShowInCenter(1000));
                mElements.add(new EffectScale(200, 1.0f, 1.0f, 1.0f, 0.0f));
                mEffect = new ComboEffect(mElements);
                mEffect.setSleep(1200);
                break;
            case SCALE_FADE_MASK:
                mElements.add(new EffectScale(200, 1.2f, 1.0f, 0.0f, 1.0f));
                mElements.add(new EffectShowInCenter(1000));
                mElements.add(new EffectScale(200, 1.0f, 1.0f, 1.0f, 0.0f));
                mEffect = new ComboEffect(mElements);
                mEffect.setSleep(1200);
                break;
            case SCALE_FADE_MASK_OUT:
                mElements.add(new EffectScale(200, 1.2f, 1.0f, 0.0f, 1.0f));
                mElements.add(new EffectShowInCenter(1000));
                mElements.add(new EffectShowInCenter(500, Mask.TRANS_OUT));
                mElements.add(new EffectShowInCenter(1000, Mask.GONE));
                mEffect = new ComboEffect(mElements);
                mEffect.setSleep(2200);
                break;
            case COVER:
                E1 = new EffectShowInCenter(500);
                E1.setTransition(true);
                mElements.add(E1);
                mElements.add(new EffectShowInCenter(1000));
                mEffect = new ComboEffect(mElements);
                mEffect.setSleep(1000);
                break;
            case COVER_STOP:
                E1 = new EffectShowInCenter(500);
                E1.setTransition(true);
                mElements.add(E1);
                mElements.add(new EffectShowInCenter(500));
                mElements.add(new EffectScale(500, 1.0f, 1.0f, 1.0f, 0.0f));
                mEffect = new ComboEffect(mElements);
                break;
            case COVER_LEFT:
                E1 = new EffectShowInLeftHalf(processGL, 500);
                E1.setTransition(true);
                mElements.add(E1);
                mElements.add(new EffectShowInLeftHalf(processGL, 1500));
                mEffect = new ComboEffect(mElements);
                mEffect.setTextureRatio(0.5f, 1.0f);
                mEffect.setSleep(0);
                break;
            case COVER_RIGHT:
                E1 = new EffectShowInRightHalf(processGL, 500);
                E1.setTransition(true);
                mElements.add(E1);
                mElements.add(new EffectShowInRightHalf(processGL, 1500));
                mEffect = new ComboEffect(mElements);
                mEffect.setTextureRatio(0.5f, 1.0f);
                mEffect.setSleep(1500);
                break;
            case SCALE_FADE_1:
                mElements.add(new EffectScale(200, 0.5f, 0.51f, 0.0f, 1.0f));
                mElements.add(new EffectScale(1000, 0.510f, 0.55f));
                mElements.add(new EffectScale(200, 0.55f, 0.56f, 1.0f, 0.0f));
                mEffect = new ComboEffect(mElements);
                mEffect.setSleep(1200);
                break;
            case SCALE_FADE_2:
                mElements.add(new EffectScale(200, 0.56f, 0.57f, 0.0f, 1.0f));
                mElements.add(new EffectScale(1000, 0.57f, 0.61f));
                mElements.add(new EffectScale(200, 0.61f, 0.62f, 1.0f, 0.0f));
                mEffect = new ComboEffect(mElements);
                mEffect.setSleep(1200);
                break;
            case SCALE_FADE_3:
                mElements.add(new EffectScale(200, 0.62f, 0.63f, 0.0f, 1.0f));
                mElements.add(new EffectScale(1000, 0.63f, 0.67f));
                mElements.add(new EffectScale(200, 0.67f, 0.68f, 1.0f, 0.0f));
                mEffect = new ComboEffect(mElements);
                mEffect.setSleep(1200);
                break;
            case SCALE_FADE_4:
                mElements.add(new EffectScale(200, 0.68f, 0.69f, 0.0f, 1.0f));
                mElements.add(new EffectScale(1000, 0.69f, 0.73f));
                mElements.add(new EffectScale(2200, 0.74f, 1.0f, 1.0f, 1.0f));
                mEffect = new ComboEffect(mElements);
                mEffect.setSleep(1200);
                break;
            case LATTICE_TILT:
                E1 = new EffectShowInRightHalf(processGL, 500);
                E1.setTransition(true);
                mElements.add(E1);
                mElements.add(new EffectShowInCenter(1200));
                mEffect = new ComboEffect(mElements);
                mEffect.setSleep(1200);
                break;
            case VIDEO_DEFAULT:
                return new VideoEffect(5000, 1f, 1f, 0, 0, 0, 0);
            case VIDEO_SLIDE:
                return new VideoEffect(5000, 1f, 1.4f, 0, 0, 0, 0.25f);
            default:
                mElements.add(new EffectShowInCenter(5000));
                mEffect = new ComboEffect(mElements);
                break;
        }

        return mEffect;
    }

}
