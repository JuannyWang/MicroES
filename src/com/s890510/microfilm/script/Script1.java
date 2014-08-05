
package com.s890510.microfilm.script;

import com.s890510.microfilm.MicroFilmActivity;
import com.s890510.microfilm.MusicManager;
import com.s890510.microfilm.draw.GLDraw;
import com.s890510.microfilm.filter.FilterChooser;

public class Script1 extends BasicScript
{
    public Script1(boolean isFromEncode, MicroFilmActivity activity, GLDraw mGLDraw) {
        this(activity, mGLDraw);
        mIsFromEncode = isFromEncode;
    }

    public Script1(MicroFilmActivity activity, GLDraw mGLDraw) {
        super(activity, mGLDraw);

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

