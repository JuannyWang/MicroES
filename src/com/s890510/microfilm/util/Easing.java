package com.s890510.microfilm.util;

import java.io.File;

import android.os.Environment;
import android.os.StatFs;

public class Easing {
    //time, beginning, change, duration
    public static int easeInExpo        = 1;
    public static int easeOutExpo       = 2;
    public static int easeInOutExpo     = 3;
    public static int easeInOutElastic  = 4;
    public static int easeOutCubic      = 5;
    public static int easeInOutQuint    = 6;
    public static int easeInBack        = 7;
    public static int easeOutBack       = 8;
    public static int easeInOutBack     = 9;
    public static int easeInOutCubic    = 10;

    public static float Easing(int type, float t, float b, float c, float d) {
        if(type == 1) {
            return easeInExpo(t, b, c, d);
        } else if(type == 2) {
            return easeOutExpo(t, b, c, d);
        } else if(type == 3) {
            return easeInOutExpo(t, b, c, d);
        } else if(type == 4) {
            return easeInOutElastic(t, b, c, d);
        } else if(type == 5) {
            return easeOutCubic(t, b, c, d);
        } else if(type == 6) {
            return easeInOutQuint(t, b, c, d);
        } else if(type == 7) {
            return easeInBack(t, b, c, d);
        } else if(type == 8) {
            return easeOutBack(t, b, c, d);
        } else if(type == 9) {
            return easeInOutBack(t, b, c, d);
        } else if(type == 10) {
            return easeInOutCubic(t, b, c, d);
        } else {
            return 0.0f;
        }
    }

    public static float easeInExpo(float t, float b, float c, float d) {
        return (t==0) ? b : c * (float)Math.pow(2, 10 * (t/d - 1)) + b;
    }

    public static float easeOutExpo(float t, float b, float c, float d) {
        return (t==d) ? b+c : c * (-(float)Math.pow(2, -10 * t/d) + 1) + b;
    }

    public static float easeInOutExpo(float t, float b, float c, float d) {
        if (t==0) return b;
        if (t==d) return b+c;
        if ((t/=d/2) < 1) return c/2 * (float)Math.pow(2, 10 * (t - 1)) + b;
        return c/2 * (-(float)Math.pow(2, -10 * --t) + 2) + b;
    }

    public static float easeInOutElastic(float t, float b, float c, float d) {
        if (t==0) return b;  if ((t/=d/2)==2) return b+c;
        float p=d*(.3f*1.5f);
        float a=c;
        float s=p/4;
        if (t < 1) return -.5f*(a*(float)Math.pow(2,10*(t-=1)) * (float)Math.sin( (t*d-s)*(2*(float)Math.PI)/p )) + b;
        return a*(float)Math.pow(2,-10*(t-=1)) * (float)Math.sin( (t*d-s)*(2*(float)Math.PI)/p )*.5f + c + b;
    }

    public static float easeOutCubic (float t, float b, float c, float d) {
        return c*((t=t/d-1)*t*t + 1) + b;
    }

    public static float easeInOutQuint (float t, float b, float c, float d) {
        if ((t/=d/2) < 1) return c/2*t*t*t*t*t + b;
        return c/2*((t-=2)*t*t*t*t + 2) + b;
    }

    public static float  easeInBack(float t, float b, float c, float d) {
        float s = 1.70158f;
        return c*(t/=d)*t*((s+1)*t - s) + b;
    }

    public static float easeOutBack(float t, float b, float c, float d) {
        float s = 1.70158f;
        return c*((t=t/d-1)*t*((s+1)*t + s) + 1) + b;
    }

    public static float easeInOutBack(float t, float b, float c, float d) {
        float s = 1.70158f;
        if ((t/=d/2) < 1) return c/2*(t*t*(((s*=(1.525f))+1)*t - s)) + b;
        return c/2*((t-=2)*t*(((s*=(1.525f))+1)*t + s) + 2) + b;
    }

    public static float easeInOutCubic(float t, float b, float c, float d) {
        if ((t/=d/2) < 1) return c/2*t*t*t + b;
        return c/2*((t-=2)*t*t + 2) + b;
    }
    
    public static long getAvailableSpace(){
    	//File path = Environment.getExternalStorageDirectory();
    	File path = Environment.getDataDirectory();
    	StatFs stat = new StatFs(path.getPath());
    	return stat.getAvailableBytes();
    }
}
