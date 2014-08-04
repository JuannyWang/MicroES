#version 100

precision mediump float;

uniform sampler2D Texture;

uniform float mSize;
uniform float mTrans;
uniform float mDirect;
uniform float mCover;
uniform float mAlpha;
uniform float mTheme;
uniform vec4 mLeft;
uniform vec4 mRight;
uniform float mFAlpha;

uniform vec2 resolution;

varying vec2 vTextureCoord;
varying float vGXClipDist;
varying float vGYClipDist;
varying float vGAClipDist;
varying float vGBClipDist;
varying float vGCClipDist;

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

void main ()
{
    if(vGXClipDist >= 0.0 && mDirect == 0.0) {
        discard;
    } else if(vGXClipDist < 0.0 && mDirect == 1.0) {
        discard;
    } else if(vGAClipDist >= 0.0 && mDirect == 2.0) {
        discard;
    } else if(vGAClipDist < 0.0 && mDirect == 3.0) {
        discard;
    } else if(vGCClipDist < 0.0 && mDirect == 4.0) {
        discard;
    } else if(vGCClipDist >= 0.0 && mDirect == 5.0) {
        discard;
    }

    if(mTrans == 1.0) {
        if(vGXClipDist > mSize && mCover == 0.0) {
            discard;
        } else if(vGXClipDist < mSize && mCover == 1.0) {
            discard;
        } else if(vGYClipDist > mSize && mCover == 2.0) {
            discard;
        } else if(vGYClipDist < mSize && mCover == 3.0) {
            discard;
        }
    }

    if(mAlpha == 0.0)
        discard;

    gl_FragColor = Theme();
}