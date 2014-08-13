
package com.s890510.microfilm.script;

import com.s890510.microfilm.MicroMovieActivity;
import com.s890510.microfilm.MusicManager;
import com.s890510.microfilm.ProcessGL;
import com.s890510.microfilm.filter.FilterChooser;
import com.s890510.microfilm.script.effects.ComboFactory;

public class Script1 extends BasicScript
{
    public Script1(boolean isFromEncode, MicroMovieActivity activity, ProcessGL processGL) {
        this(activity, processGL);
        mIsFromEncode = isFromEncode;
    }

    public Script1(MicroMovieActivity activity, ProcessGL processGL) {
        super(activity, processGL);

        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.SHOW_LEFT_HALF));
        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.SHOW_RIGHT_HALF));
        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.VIDEO_DEFAULT));
        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.BLINKING));
        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.MIRROR));
        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.VIDEO_SLIDE));
        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.SCALE));
        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.SEQUENTIAL_EFFECT_1));
        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.SEQUENTIAL_EFFECT_2));
        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.SEQUENTIAL_EFFECT_3));
        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.SEQUENTIAL_EFFECT_4));

        init();
    }

    @Override
    public int getMusicId() {
        return MusicManager.MUISC_ASUS_KIDS;
    }

    @Override
    public int getFilterId() {
        return FilterChooser.DEFAULT;
    }
}

