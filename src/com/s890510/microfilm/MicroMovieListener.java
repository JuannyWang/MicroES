package com.s890510.microfilm;

public interface MicroMovieListener {
    public static final int START_INIT_PROGRASSDIALOG = 0;
    public static final int STOP_INIT_PROGRASSDIALOG  = 1;
    public static final int FINISH_CHANGESURFACE      = 2;
    public static final int FINISH_PLAY               = 3;
    public static final int UPDATE_THEME              = 4;

    public void doUpdate(int Item);
}
