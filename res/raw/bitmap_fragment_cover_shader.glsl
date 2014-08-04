#version 100

precision mediump float;

uniform sampler2D Texture;

uniform float mSize;
uniform float mTrans;
uniform float mDirect;
uniform float mAlpha;
uniform float mReverse;
uniform float mTheme;
uniform float mIsEmpty;
uniform float mSetBound;
uniform float mBound[2];
uniform vec4 mLeft;
uniform vec4 mRight;
uniform float mFAlpha;

varying vec4 vCenter;
uniform vec2 resolution;
varying vec2 vTextureCoord;
varying float vXClipDist;
varying float vYClipDist;

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

void main () {
    float mIsDiscard = 0.0;

    if(mSetBound == 1.0) {
        if(vCenter.x + mBound[0] < vTXClipDist || vCenter.x - mBound[0] > vTXClipDist ||
           vCenter.y + mBound[1] < vTYClipDist || vCenter.y - mBound[1] > vTYClipDist) {
            mIsDiscard = 1.0;
        }
    } else if(mSetBound == 4.0) {
        if(mBound[0] < vTXClipDist) {
            mIsDiscard = 1.0;
        }
    }
    if(mTrans == 1.0 && mIsDiscard == 0.0) {
        float alpha;
        if(vXClipDist < mSize && mDirect == 0.0) {
            alpha = 1.0;
        } else if(vXClipDist > mSize && mDirect == 1.0) {
            alpha = 1.0;
        } else if(vYClipDist > mSize && mDirect == 3.0) {
            alpha = 1.0;
        } else if(vYClipDist < mSize && mDirect == 2.0) {
            alpha = 1.0;
        } else if(vGXClipDist < mSize && mDirect == 4.0) {
            alpha = 1.0;
        } else if((vYClipDist >= mSize || vYClipDist <= -mSize) && mDirect == 5.0) {
            alpha = 1.0;
        } else {
            alpha = 0.0;
        }

        if(mReverse == 1.0) {
            if(alpha == 1.0) {
                mIsDiscard = 1.0;
            }
        } else if(alpha == 0.0) {
            mIsDiscard = 1.0;
        }
    }

    if(mIsDiscard == 1.0) {
        gl_FragColor = vec4(1.0, 1.0, 1.0, 0.0);
    } else if(mIsEmpty == 0.0) {
        gl_FragColor = Theme();
    } else {
        gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
    }
}