package com.s890510.microfilm.script;

import com.asus.gallery.micromovie.MicroMovieActivity;
import com.asus.gallery.micromovie.MusicManager;
import com.asus.gallery.micromovie.ProcessGL;
import com.s890510.microfilm.filter.FilterChooser;
import com.s890510.microfilm.script.effects.ComboFactory;
import com.s890510.microfilm.shader.Shader;

public class Script5 extends BasicScript
{
    public Script5(boolean isFromEncode, MicroMovieActivity activity, ProcessGL processGL) {
        this(activity, processGL);
        mIsFromEncode = isFromEncode;
    }

    public Script5(MicroMovieActivity activity, ProcessGL processGL) {
        super(activity, processGL);

        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.MULTI, Shader.Fade));
        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.MULTI, Shader.Shutters));
        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.MULTI, Shader.Fade));
        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.MULTI, Shader.Lattice));
        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.MULTI, Shader.Fade));
        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.MULTI, Shader.Lattice));
        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.MULTI, Shader.Fade));
        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.MULTI, Shader.Lattice));
        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.MULTI, Shader.Fade));
        mEffects.add(ComboFactory.getComboEffect(processGL, ComboFactory.MULTI, Shader.Lattice));
        init();
    }

    @Override
    public int getMusicId() {
        return MusicManager.MUISC_ASUS_CITY_STREET;
    }

    @Override
    public int getFilterId() {
        return FilterChooser.DEFAULT;
    }
}
