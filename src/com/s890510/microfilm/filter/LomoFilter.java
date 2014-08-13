package com.s890510.microfilm.filter;

import java.util.Date;
import java.util.Random;

import android.opengl.GLES20;

import com.s890510.microfilm.MicroMovieActivity;

public class LomoFilter extends DefaultFilter {
    private int     mBSeedHandle;
    private int     mBStepsizeXHandle;
    private int     mBStepsizeYHandle;
    private int     mBStepsizeHandle;
    private int     mBScaleHandle;
    private int     mBInvMaxDistHandle;

    private int     mVSeedHandle;
    private int     mVStepsizeXHandle;
    private int     mVStepsizeYHandle;
    private int     mVStepsizeHandle;
    private int     mVScaleHandle;
    private int     mVInvMaxDistHandle;

    private Random  mRandom;
    private int     mWidth  = 1280;
    private int     mHeight = 720;
    private float   mMaxDist;
    private float[] mScale;

    public LomoFilter(MicroMovieActivity activity) {
        super(activity);

        mScale = new float[2];
        if(mWidth > mHeight) {
            mScale[0] = 1f;
            mScale[1] = ((float) mHeight) / mWidth;
        } else {
            mScale[0] = ((float) mWidth) / mHeight;
            mScale[1] = 1f;
        }
        mMaxDist = ((float) Math.sqrt(mScale[0] * mScale[0] + mScale[1] * mScale[1])) * 0.5f;
        mRandom = new Random(new Date().getTime());
    }

    @Override
    public String getBitmapSingleFragment() {
        return "precision mediump float;\n"
                + "uniform sampler2D Texture;\n"
                + "uniform vec2 seed;\n"
                + "uniform float stepsizeX;\n"
                + "uniform float stepsizeY;\n"
                + "uniform float stepsize;\n"
                + "uniform vec2 scale;\n"
                + "uniform float inv_max_dist;\n"
                + "uniform float mAlpha;\n"
                + "varying vec2 vTextureCoord;\n"
                + "float rand(vec2 loc) {\n"
                + "  float theta1 = dot(loc, vec2(0.9898, 0.233));\n"
                + "  float theta2 = dot(loc, vec2(12.0, 78.0));\n"
                + "  float value = cos(theta1) * sin(theta2) + sin(theta1) * cos(theta2);\n"
                +
                // keep value of part1 in range: (2^-14 to 2^14).
                "  float temp = mod(197.0 * value, 1.0) + value;\n"
                + "  float part1 = mod(220.0 * temp, 1.0) + temp;\n"
                + "  float part2 = value * 0.5453;\n"
                + "  float part3 = cos(theta1 + theta2) * 0.43758;\n"
                + "  return fract(part1 + part2 + part3);\n"
                + "}\n"
                + "void main() {\n"
                +
                // sharpen
                // "  vec3 nbr_color = vec3(0.0, 0.0, 0.0);\n" +
                "  vec2 coord;\n"
                + "  vec4 color = texture2D(Texture, vTextureCoord);\n"
                +
                /*
                 * "  coord.x = vTextureCoord.x - 0.5 * stepsizeX;\n" +
                 * "  coord.y = vTextureCoord.y - stepsizeY;\n" +
                 * "  vec4 calculate = Calculate(coord);\n" +
                 * "  nbr_color += calculate.rgb - color.rgb;\n" +
                 * "  coord.x = vTextureCoord.x - stepsizeX;\n" +
                 * "  coord.y = vTextureCoord.y + 0.5 * stepsizeY;\n" +
                 * "  calculate = Calculate(coord);\n" +
                 * "  nbr_color += calculate.rgb - color.rgb;\n" +
                 * "  coord.x = vTextureCoord.x + stepsizeX;\n" +
                 * "  coord.y = vTextureCoord.y - 0.5 * stepsizeY;\n" +
                 * "  calculate = Calculate(coord);\n" +
                 * "  nbr_color += calculate.rgb - color.rgb;\n" +
                 * "  coord.x = vTextureCoord.x + stepsizeX;\n" +
                 * "  coord.y = vTextureCoord.y + 0.5 * stepsizeY;\n" +
                 * "  calculate = Calculate(coord);\n" +
                 * "  nbr_color += calculate.rgb - color.rgb;\n" +
                 * "  vec3 s_color = vec3(color.rgb + 0.3 * nbr_color);\n" +
                 */
                "  vec3 s_color = color.xyz;\n"
                +
                // cross process
                "  vec3 c_color = vec3(0.0, 0.0, 0.0);\n" + "  float value;\n" + "  if (s_color.r < 0.5) {\n" + "    value = s_color.r;\n"
                + "  } else {\n" + "    value = 1.0 - s_color.r;\n" + "  }\n" + "  float red = 4.0 * value * value * value;\n"
                + "  if (s_color.r < 0.5) {\n" + "    c_color.r = red;\n" + "  } else {\n" + "    c_color.r = 1.0 - red;\n" + "  }\n"
                + "  if (s_color.g < 0.5) {\n" + "    value = s_color.g;\n" + "  } else {\n" + "    value = 1.0 - s_color.g;\n" + "  }\n"
                + "  float green = 2.0 * value * value;\n" + "  if (s_color.g < 0.5) {\n" + "    c_color.g = green;\n" + "  } else {\n"
                + "    c_color.g = 1.0 - green;\n" + "  }\n"
                + "  c_color.b = s_color.b * 0.5 + 0.25;\n"
                +
                // blackwhite
                /*
                 * "  float dither = rand(vTextureCoord + seed);\n" +
                 * "  vec3 xform = clamp((c_color.rgb - 0.15) * 1.53846, 0.0, 1.0);\n"
                 * +
                 * "  vec3 temp = clamp((color.rgb + stepsize - 0.15) * 1.53846, 0.0, 1.0);\n"
                 * +
                 * "  vec3 bw_color = clamp(xform + (temp - xform) * (dither - 0.5), 0.0, 1.0);\n"
                 * +
                 */
                // vignette
                "  coord = vTextureCoord - vec2(0.5, 0.5);\n" + "  float dist = length(coord * scale);\n"
                + "  float lumen = 0.85 / (1.0 + exp((dist * inv_max_dist - 0.73) * 20.0)) + 0.15;\n" +
                // "  gl_FragColor = vec4(bw_color * lumen, color.a);\n" +
                "  gl_FragColor = vec4(c_color * lumen, color.a);\n" + "  gl_FragColor.a = mAlpha;\n" + "}\n";
    }

    @Override
    public String getVideoFragment() {
        return "#extension GL_OES_EGL_image_external : require\n"
                + "precision mediump float;\n"
                + "uniform samplerExternalOES exTexture;\n"
                + "uniform vec2 seed;\n"
                + "uniform float stepsizeX;\n"
                + "uniform float stepsizeY;\n"
                + "uniform float stepsize;\n"
                + "uniform vec2 scale;\n"
                + "uniform float inv_max_dist;\n"
                + "uniform float mAlpha;\n"
                + "varying vec2 vTextureCoord;\n"
                + "float rand(vec2 loc) {\n"
                + "  float theta1 = dot(loc, vec2(0.9898, 0.233));\n"
                + "  float theta2 = dot(loc, vec2(12.0, 78.0));\n"
                + "  float value = cos(theta1) * sin(theta2) + sin(theta1) * cos(theta2);\n"
                +
                // keep value of part1 in range: (2^-14 to 2^14).
                "  float temp = mod(197.0 * value, 1.0) + value;\n"
                + "  float part1 = mod(220.0 * temp, 1.0) + temp;\n"
                + "  float part2 = value * 0.5453;\n"
                + "  float part3 = cos(theta1 + theta2) * 0.43758;\n"
                + "  return fract(part1 + part2 + part3);\n"
                + "}\n"
                + "void main() {\n"
                +
                // sharpen
                // "  vec3 nbr_color = vec3(0.0, 0.0, 0.0);\n" +
                "  vec2 coord;\n"
                + "  vec4 color = texture2D(exTexture, vTextureCoord);\n"
                +
                /*
                 * "  coord.x = vTextureCoord.x - 0.5 * stepsizeX;\n" +
                 * "  coord.y = vTextureCoord.y - stepsizeY;\n" +
                 * "  nbr_color += texture2D(exTexture, coord).rgb - color.rgb;\n"
                 * + "  coord.x = vTextureCoord.x - stepsizeX;\n" +
                 * "  coord.y = vTextureCoord.y + 0.5 * stepsizeY;\n" +
                 * "  nbr_color += texture2D(exTexture, coord).rgb - color.rgb;\n"
                 * + "  coord.x = vTextureCoord.x + stepsizeX;\n" +
                 * "  coord.y = vTextureCoord.y - 0.5 * stepsizeY;\n" +
                 * "  nbr_color += texture2D(exTexture, coord).rgb - color.rgb;\n"
                 * + "  coord.x = vTextureCoord.x + stepsizeX;\n" +
                 * "  coord.y = vTextureCoord.y + 0.5 * stepsizeY;\n" +
                 * "  nbr_color += texture2D(exTexture, coord).rgb - color.rgb;\n"
                 * + "  vec3 s_color = vec3(color.rgb + 0.3 * nbr_color);\n" +
                 */
                "  vec3 s_color = color.xyz;\n"
                +
                // cross process
                "  vec3 c_color = vec3(0.0, 0.0, 0.0);\n" + "  float value;\n" + "  if (s_color.r < 0.5) {\n" + "    value = s_color.r;\n"
                + "  } else {\n" + "    value = 1.0 - s_color.r;\n" + "  }\n" + "  float red = 4.0 * value * value * value;\n"
                + "  if (s_color.r < 0.5) {\n" + "    c_color.r = red;\n" + "  } else {\n" + "    c_color.r = 1.0 - red;\n" + "  }\n"
                + "  if (s_color.g < 0.5) {\n" + "    value = s_color.g;\n" + "  } else {\n" + "    value = 1.0 - s_color.g;\n" + "  }\n"
                + "  float green = 2.0 * value * value;\n" + "  if (s_color.g < 0.5) {\n" + "    c_color.g = green;\n" + "  } else {\n"
                + "    c_color.g = 1.0 - green;\n" + "  }\n"
                + "  c_color.b = s_color.b * 0.5 + 0.25;\n"
                +
                // blackwhite
                /*
                 * "  float dither = rand(vTextureCoord + seed);\n" +
                 * "  vec3 xform = clamp((c_color.rgb - 0.15) * 1.53846, 0.0, 1.0);\n"
                 * +
                 * "  vec3 temp = clamp((color.rgb + stepsize - 0.15) * 1.53846, 0.0, 1.0);\n"
                 * +
                 * "  vec3 bw_color = clamp(xform + (temp - xform) * (dither - 0.5), 0.0, 1.0);\n"
                 * +
                 */
                // vignette
                "  coord = vTextureCoord - vec2(0.5, 0.5);\n" + "  float dist = length(coord * scale);\n"
                + "  float lumen = 0.85 / (1.0 + exp((dist * inv_max_dist - 0.73) * 20.0)) + 0.15;\n" +
                // "  gl_FragColor = vec4(bw_color * lumen, color.a);\n" +
                "  gl_FragColor = vec4(c_color * lumen, color.a);\n" + "  gl_FragColor.a = mAlpha;\n" + "}\n";
    }

    @Override
    public void setSingleBitmapParams(int bProgram) {
        mBSeedHandle = GLES20.glGetUniformLocation(bProgram, "seed");
        mBStepsizeXHandle = GLES20.glGetUniformLocation(bProgram, "stepsizeX");
        mBStepsizeYHandle = GLES20.glGetUniformLocation(bProgram, "stepsizeY");
        mBStepsizeHandle = GLES20.glGetUniformLocation(bProgram, "stepsize");
        mBScaleHandle = GLES20.glGetUniformLocation(bProgram, "scale");
        mBInvMaxDistHandle = GLES20.glGetUniformLocation(bProgram, "inv_max_dist");
    }

    @Override
    public void setVideoParams(int vProgram) {
        mVSeedHandle = GLES20.glGetUniformLocation(vProgram, "seed");
        mVStepsizeXHandle = GLES20.glGetUniformLocation(vProgram, "stepsizeX");
        mVStepsizeYHandle = GLES20.glGetUniformLocation(vProgram, "stepsizeY");
        mVStepsizeHandle = GLES20.glGetUniformLocation(vProgram, "stepsize");
        mVScaleHandle = GLES20.glGetUniformLocation(vProgram, "scale");
        mVInvMaxDistHandle = GLES20.glGetUniformLocation(vProgram, "inv_max_dist");
    }

    @Override
    public void drawBitmap() {
        GLES20.glUniform2f(mBScaleHandle, mScale[0], mScale[1]);
        GLES20.glUniform1f(mBInvMaxDistHandle, 1.0f / mMaxDist);
        GLES20.glUniform1f(mBStepsizeHandle, 1.0f / 255.0f);
        GLES20.glUniform1f(mBStepsizeXHandle, 1.0f / mWidth);
        GLES20.glUniform1f(mBStepsizeYHandle, 1.0f / mHeight);

        GLES20.glUniform2f(mBSeedHandle, mRandom.nextFloat(), mRandom.nextFloat());
    }

    @Override
    public void drawVideo() {
        GLES20.glUniform2f(mVScaleHandle, mScale[0], mScale[1]);
        GLES20.glUniform1f(mVInvMaxDistHandle, 1.0f / mMaxDist);
        GLES20.glUniform1f(mVStepsizeHandle, 1.0f / 255.0f);
        GLES20.glUniform1f(mVStepsizeXHandle, 1.0f / mWidth);
        GLES20.glUniform1f(mVStepsizeYHandle, 1.0f / mHeight);

        GLES20.glUniform2f(mVSeedHandle, mRandom.nextFloat(), mRandom.nextFloat());
    }
}
