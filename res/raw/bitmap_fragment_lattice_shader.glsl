#version 100

precision mediump float;

uniform sampler2D Texture;

uniform float mAlpha;
uniform float mSize;
uniform float mMotion;
uniform float mTheme;
uniform float mRadius;
uniform vec4 mLeft;
uniform vec4 mRight;
uniform float mFAlpha;

varying vec2 vTextureCoord;
varying vec2 vResolution;
varying float vAClipDist;
varying float vBClipDist;

vec2 resolution = vResolution;

#define Blend(base, blend, funcf)       vec3(funcf(base.r, blend.r), funcf(base.g, blend.g), funcf(base.b, blend.b))
#define BlendOverlayf(base, blend)      (base < 0.5 ? (2.0 * base * blend) : (1.0 - 2.0 * (1.0 - base) * (1.0 - blend)))
#define BlendMultiply(base, blend)      (base * blend)

vec4 Theme() {
    vec4 mResultColor;
    vec4 srcColor = texture2D(Texture, vTextureCoord);

    if(mTheme > 0.0) {
        if(mTheme == 1.0) { //¡÷
            float progress = gl_FragCoord.x/resolution.x;
            float mRed = mLeft.r+progress*(mRight.r-mLeft.r);
            float mGreen = mLeft.g+progress*(mRight.g-mLeft.g);
            float mBlue = mLeft.b+progress*(mRight.b-mLeft.b);

            vec4 Mask = vec4(mRed, mGreen, mBlue, 1.0);

            mResultColor = vec4(Blend(srcColor, Mask, BlendOverlayf), mAlpha);
        } else { //¡û 2, 3
            float step1 = resolution.y * 0.50;
            float step2 = resolution.y * -0.50;

            vec4 Mask = mix(mLeft, mRight, smoothstep(step1, step2, gl_FragCoord.y - 0.538*gl_FragCoord.x));

            mResultColor = (mTheme == 2.0) ? vec4(Blend(srcColor, Mask, BlendOverlayf), mAlpha) : vec4(Blend(srcColor, Mask, BlendMultiply), mAlpha);
        }
    } else {
        mResultColor = srcColor;
        mResultColor.w = mAlpha;
    }

    return mResultColor;
}

void getColor(in float mClipDist, in float mNoClipDist, out float alpha) {
    if(mClipDist >= -2.0 && mClipDist < -1.5 && mNoClipDist < -mSize+2.0) {
        alpha = 1.0;
    } else if(mClipDist >= -1.5 && mClipDist < -1.0 && mNoClipDist < -mSize+1.5) {
        alpha = 1.0;
    } else if(mClipDist >= -1.0 && mClipDist < -0.5 && mNoClipDist < -mSize+1.0) {
        alpha = 1.0;
    } else if(mClipDist >= -0.5 && mClipDist < 0.0 && mNoClipDist < -mSize+0.5) {
        alpha = 1.0;
    } else if(mClipDist >= 0.0 && mClipDist < 0.5 && mNoClipDist < -mSize) {
        alpha = 1.0;
    } else if(mClipDist >= 0.5 && mClipDist < 1.0 && mNoClipDist < -mSize-0.5) {
        alpha = 1.0;
    } else if(mClipDist >= 1.0 && mClipDist < 1.5 && mNoClipDist < -mSize-1.0) {
        alpha = 1.0;
    } else if(mClipDist >= 1.5 && mClipDist < 2.0 && mNoClipDist < -mSize-1.5) {
        alpha = 1.0;
    } else {
        alpha = 0.0;
    }
}

void main ()
{
    float alpha = 1.0;
    float DrawSrc = 1.0;

    if(mMotion == 1.0) {
        if(-vAClipDist < mSize) {
            float f = gl_FragCoord.x + gl_FragCoord.y;
            alpha = float(mod(f/mRadius, 2.0) > 1.0);
        } else {
            alpha = 0.0;
        }
    } else if(mMotion == 2.0) {
        if(-vAClipDist > mSize) {
            float f = gl_FragCoord.x + gl_FragCoord.y;
            alpha = float(mod(f/mRadius, 2.0) > 1.0);
        } else {
            alpha = 1.0;
        }
    } else if(mMotion == 3.0) {
        float f = gl_FragCoord.x + gl_FragCoord.y;
        alpha = float(mod(f/mRadius, 2.0) > 1.0);
    } else if(mMotion == 4.0) {
        float f = gl_FragCoord.x + gl_FragCoord.y;
        float mColorA = 0.0;
        float mColorB = 0.0;

        if(-vAClipDist <= mSize || -vAClipDist >= -mSize) {
            float temp = mod(f/mRadius, 2.0);

            if(-vAClipDist <= mSize) {
                mColorA = float(temp < 1.0);
            }
            if(-vAClipDist >= -mSize) {
                mColorB = float(temp > 1.0);
            }
        }

        alpha = max(mColorA, mColorB);
    } else if(mMotion == 5.0) {
        float f = gl_FragCoord.x + gl_FragCoord.y;
        float mColorA = 0.0;
        float mColorB = 0.0;

        if(-vAClipDist >= mSize || -vAClipDist <= -mSize) {
            float temp = mod(f/mRadius, 2.0);

            if(-vAClipDist >= mSize) {
                mColorA = float(temp <= 1.0);
            }
            if(-vAClipDist <= -mSize) {
                mColorB = float(temp >= 1.0);
            }
        }

        alpha = max(mColorA, mColorB);
    } else if(mMotion == 6.0) {
        float f = gl_FragCoord.y;
        alpha = float(mod(f/mRadius, 2.0) < mSize);
    } else if(mMotion == 7.0 || mMotion == 8.0) {
        if(mMotion == 7.0 || mMotion == 8.0) {
            getColor(vBClipDist, vAClipDist, alpha);
        }

        if(mMotion == 8.0) {
            if(alpha == 1.0)
                alpha = 0.0;
            else
                alpha = 1.0;
        }

        if(alpha == 0.0) {
            DrawSrc = 0.0;
        }
    }

    if(DrawSrc == 1.0) {
        if(alpha == 0.0) {
            gl_FragColor = vec4(1.0, 1.0, 1.0, 0.0);
        } else {
            gl_FragColor = Theme();
        }
    } else {
        gl_FragColor = vec4(0.34, 0.72, 0.99, 1.0);
    }
}