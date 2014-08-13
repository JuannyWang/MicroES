package com.s890510.microfilm.shader;

import android.opengl.GLES20;
import android.util.Log;

import com.s890510.microfilm.MicroMovieActivity;
import com.s890510.microfilm.draw.GLUtil;

public abstract class Shader {
    private static final String TAG = "Shader";

    protected MicroMovieActivity mActivity;

    public static final int NONE            = 0;

    //Single
    public static final int DefaultShader   = 1;
    //public static final int ColorShader     = 2;
    //public static final int LomoShader      = 3;
    //public static final int OldfilmShader   = 4;
    //public static final int LightShader     = 5;
    public static final int PhotoShader     = 6;
    public static final int StringShader    = 7;
    public static final int CoverShader     = 8;
    public static final int LatticeShader   = 9;
    public static final int MirrorShader    = 0;
    public static final int RotateShader    = 11;
    public static final int LineShader      = 12;

    //Mutiple
    public static final int FadeShader      = 31;
    public static final int ShuttersShader  = 32;

    //Color
    public static final int GRAY        = 51;
    public static final int SEPIA       = 52;

    //Cover
    public static final int LEFT        	= 101;
    public static final int RIGHT       	= 102;
    public static final int TOP         	= 103;
    public static final int BOTTOM      	= 104;
    public static final int HALF_LEFT   	= 105;
    public static final int HALF_RIGHT  	= 106;
    public static final int HALF_TOP    	= 107;
    public static final int HALF_BOTTOM 	= 108;
    public static final int GFRAG_LEFT  	= 109;
    public static final int EMPTY_LEFT  	= 110;
    public static final int CENTER_H    	= 111;
    public static final int PERCENT_L   	= 112;
    public static final int PERCENT_L_HALF 	= 113;
    public static final int PERCENT_R   	= 114;
    public static final int PERCENT_B   	= 115;
    public static final int HALF_LEFT_Q 	= 116;
    public static final int HALF_RIGHT_Q 	= 117;

    //Lattice
    public static final int LATTICE_VERTICAL        		= 201;
    public static final int LATTICE_HORIZONTAL      		= 202;
    public static final int LATTICE_TILTED_LEFT     		= 203;
    public static final int LATTICE_TILTED_LEFT_T   		= 204;
    public static final int LATTICE_TILTED_RIGHT    		= 205;
    public static final int LATTICE_TILTED_RIGHT_R  		= 206;
    public static final int LATTICE_CROSS_2         		= 207;
    public static final int LATTICE_CROSS_4         		= 208;
    public static final int LATTICE_BLUE_BAR        		= 209;
    public static final int LATTICE_BLUE_BAR_GONE   		= 210;
    public static final int LATTICE_BLUE_BAR_GONE_STRING   	= 211;

    //String
    public static final int STRING              = 301;
    public static final int STRING_LEFT         = 302;

    //Mirror
    public static final int MIRROR_VERTICAL     = 401;
    public static final int MIRROR_HORIZONTAL   = 402;
    public static final int MIRROR_TILTED_LEFT  = 403;
    public static final int MIRROR_TILTED_RIGHT = 404;
    public static final int MIRROR_VERTICAL_TB  = 405;
    public static final int MIRROR_TILTED_MASK  = 406;
    public static final int MIRROR_TILTED       = 407;

    //Line
    public static final int EMPTY               = 501;

    //bounding-scale
    public static final int BOUNDING            = 601;
    public static final int LIMIT_X             = 602;
    public static final int LIMIT_Y             = 603;
    public static final int LIMIT_Y_TOP         = 604;
    public static final int LIMIT_Y_TB          = 605;
    public static final int LIMIT_COVER_X       = 606;

    //Shader Type
    public static final String Default                  = "Default";
    public static final String Default_White            = "Default_White";
    public static final String Gray                     = "Gray";
    public static final String Sepia                    = "Sepia";
    public static final String Lomo                     = "Lomo";
    public static final String Oldfilm                  = "Oldfilm";
    public static final String Light                    = "Light";
    public static final String Lattice                  = "Lattice";
    public static final String Fade                     = "Fade";
    public static final String Photo                    = "Photo";
    public static final String String                   = "String";
    public static final String String_NoBK              = "String_NoBK";
    public static final String String_Line              = "String_Line";
    public static final String Shutters                 = "Shutters";
    public static final String Scale_Mask               = "Scale_Mask";
    public static final String Scale_Fade               = "Scale_Fade";
    public static final String Scale_Fade_Bar_TRANS_IN  = "Scale_Fade_Bar_TRANS_IN";
    public static final String Scale_Fade_Bar_SHOWN     = "Scale_Fade_Bar_SHOWN";
    public static final String Mask_Gone                = "Mask_Gone";
    public static final String Cover_Left               = "Cover_Left";
    public static final String Cover_Right              = "Cover_Right";
    public static final String Cover_Top                = "Cover_Top";
    public static final String Cover_Bottom             = "Cover_Bottom";
    public static final String Cover_Half_Left          = "Cover_Half_Left";
    public static final String Cover_Half_Left_Q        = "Cover_Half_Left_Q";
    public static final String Cover_Half_Right         = "Cover_Half_Right";
    public static final String Cover_Half_Top           = "Cover_Half_Top";
    public static final String Cover_Half_Bottom        = "Cover_Half_Bottom";
    public static final String Cover_Gfrag_Left         = "Cover_Gfrag_Left";
    public static final String Cover_Empty_Left         = "Cover_Empty_Left";
    public static final String Cover_Center_H           = "Cover_Center_H";
    public static final String Cover_Percent_L          = "Cover_Percent_L";
    public static final String Cover_Percent_L_Half     = "Cover_Percent_L_Half";
    public static final String Cover_Percent_R          = "Cover_Percent_R";
    public static final String Cover_Percent_B          = "Cover_Percent_B";
    public static final String Cover_String_Left        = "Cover_String_Left";
    public static final String Lattice_Vertical         = "Lattice_Vertical";
    public static final String Lattice_Horizontal       = "Lattice_Horizontal";
    public static final String Lattice_Tilted_Left      = "Lattice_Tilted_Left";
    public static final String Lattice_Tilted_Left_T    = "Lattice_Tilted_Left_T";
    public static final String Lattice_Tilted_Right     = "Lattice_Tilted_Right";
    public static final String Lattice_Tilted_Right_R   = "Lattice_Tilted_Right_R";
    public static final String Lattice_Cross_2          = "Lattice_Cross_2";
    public static final String Lattice_Cross_4          = "Lattice_Cross_4";
    public static final String Lattice_Blue_Bar         = "Lattice_Blue_Bar";
    public static final String Lattice_Blue_Bar_Mask    = "Lattice_Blue_Bar_Mask";
    public static final String Lattice_Blue_Bar_String  = "Lattice_Blue_Bar_String";
    public static final String Mirror_Vertical          = "Mirror_Vertical";
    public static final String Mirror_Vertical_TB       = "Mirror_Vertical_TB"; //have top, bottom cover
    public static final String Mirror_Horizontal        = "Mirror_Horizontal";
    public static final String Mirror_Tilted_Left       = "Mirror_Tilted_Left";
    public static final String Mirror_Tilted_Right      = "Mirror_Tilted_Right";
    public static final String Mirror_Tilted_Mask       = "Mirror_Tilted_Mask";
    public static final String Mirror_Tilted            = "Mirror_Tilted";
    public static final String Circle_Mask              = "Circle_Mask";
    public static final String Circle_Mask_Cover        = "Circle_Mask_Cover";
    public static final String Rotate                   = "Rotate";
    public static final String Rotate_Mask              = "Rotate_Mask";
    public static final String Line                     = "Line";
    public static final String Filter                   = "Filter";
    public static final String CMask                    = "CMask";
    public static final String Slogan_TypeA             = "Slogan_TypeA";
    public static final String Slogan_TypeB             = "Slogan_TypeB";
    public static final String Slogan_TypeC             = "Slogan_TypeC";
    public static final String Slogan_TypeD             = "Slogan_TypeD";
    public static final String Slogan_TypeE             = "Slogan_TypeE";
    public static final String Slogan_TypeF             = "Slogan_TypeF";

    public Shader(MicroMovieActivity activity){
        mActivity = activity;
    }

    public void checkGlError(String op) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, op + ": glError " + error);
            throw new RuntimeException(op + ": glError " + error);
        }
    }

    protected String getShaderRaw(final int ResourcesId) {
        return GLUtil.readTextFileFromRawResource(mActivity.getApplicationContext(), ResourcesId);
    }

    abstract public void Reset();
}
