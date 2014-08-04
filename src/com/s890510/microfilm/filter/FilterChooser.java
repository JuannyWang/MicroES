package com.s890510.microfilm.filter;

import com.s890510.microfilm.MicroFilmActivity;

public class FilterChooser {
    public static final int DEFAULT = 0;
    public static final int SEPIA = 1;
    public static final int OLDFILM = 2;
    public static final int GRAY = 3;
    public static final int LOMO = 4;
    public static final int COLOR = 5;
    public static final int LATTICE = 6;

    public static Filter getFilter(MicroFilmActivity activity, int id){
        switch(id){
            case SEPIA:
                return new SepiaFilter(activity);

            case OLDFILM:
                return new OldFilmFilter(activity);

            case GRAY:
                return new GrayFilter(activity);

            case LOMO:
                return new LomoFilter(activity);

            case COLOR:
                return new ColorFilter(activity);

            default:
                return new DefaultFilter(activity);
        }
    }
}
