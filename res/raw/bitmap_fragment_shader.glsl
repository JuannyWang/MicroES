#version 100

precision mediump float;

uniform sampler2D Texture;

uniform mat3 matrix;
uniform vec2 resolution;
uniform float mAlpha;
uniform float mTheme;
uniform float mString_BK;
uniform float mString_BKR;
uniform float mString_BKG;
uniform float mString_BKB;
uniform float mSetBound;
uniform float mBound[2];
uniform vec4 mLeft;
uniform vec4 mRight;

varying vec4 vCenter;
varying vec2 vTextureCoord;
varying float vGXClipDist;
varying float vGYClipDist;

varying float vTXClipDist;
varying float vTYClipDist;

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

void main() {
    float mIsDiscard = 0.0;
    if(mString_BK == 0.0) {
        if(mSetBound == 1.0) {
            if(vCenter.x + mBound[0] < vTXClipDist || vCenter.x - mBound[0] > vTXClipDist ||
               vCenter.y + mBound[1] < vTYClipDist || vCenter.y - mBound[1] > vTYClipDist) {
                mIsDiscard = 1.0;
            }
        } else if(mSetBound == 2.0) {
            if(mBound[0] > vTXClipDist || mBound[1] < vTXClipDist) {
                mIsDiscard = 1.0;
            }
        } else if(mSetBound == 3.0) {
            if(mBound[0] > vTYClipDist || mBound[1] < vTYClipDist) {
                mIsDiscard = 1.0;
            }
        } else if(mSetBound == 4.0) {
            if(mBound[0] > vTYClipDist || vCenter.y + mBound[1] < vTYClipDist) {
                mIsDiscard = 1.0;
            }
        } else if(mSetBound == 5.0) {
            if(vCenter.y + mBound[0] > vTYClipDist || vCenter.y + mBound[1] < vTYClipDist) {
                mIsDiscard = 2.0;
            }
        }

        if(mIsDiscard == 0.0) {
            gl_FragColor = Theme();
        } else if(mIsDiscard == 2.0) {
            gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);
        } else {
            gl_FragColor = vec4(1.0, 1.0, 1.0, 0.0);
        }
    } else {
        vec4 srcColor = texture2D(Texture, vTextureCoord);
        if(mString_BK == 1.0) {
            if(srcColor.r == 0.0 && srcColor.g == 0.0 && srcColor.b == 0.0) {
                srcColor = vec4(1.0, 1.0, 1.0, 0.0);
            } else if(floor(srcColor.r*10.0) != floor(mString_BKR*10.0) || floor(srcColor.g*10.0) != floor(mString_BKG*10.0) || floor(srcColor.b*10.0) != floor(mString_BKB*10.0)) {
                srcColor = vec4(1.0, 1.0, 1.0, (srcColor.r+srcColor.g+srcColor.b)/3.0);
            }
        } else if(mString_BK == 2.0) {
            if(srcColor.r == 0.0 && srcColor.g == 0.0 && srcColor.b == 0.0) {
                srcColor = vec4(1.0, 1.0, 1.0, 0.0);
            } else {
                srcColor = vec4(1.0, 1.0, 1.0, (srcColor.r+srcColor.g+srcColor.b)/3.0*mAlpha);
            }
        } else if(mString_BK == 3.0) {
            if(srcColor.r == 1.0 && srcColor.g == 1.0 && srcColor.b == 1.0) {
                srcColor = vec4(0.0, 0.0, 0.0, 0.0);
            } else {
                srcColor = vec4(0.0, 0.0, 0.0, (1.0-(srcColor.r+srcColor.g+srcColor.b)/3.0)*mAlpha);
            }
        }
        gl_FragColor = srcColor;
    }
}
