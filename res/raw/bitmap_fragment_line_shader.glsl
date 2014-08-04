#version 100

precision mediump float;

uniform sampler2D Texture;

uniform mat3 matrix;
uniform vec2 resolution;
uniform float mTheme;
uniform float mTrans;
uniform float mAlpha;
uniform float mReverse;
uniform float mIsString;
uniform vec4 mLeft;
uniform vec4 mRight;
uniform float mFAlpha;

uniform float mNPos;
uniform int mStart;
uniform int mEnd;

//10 area
uniform float mXPos[10];
uniform float mArea[10];

varying vec2 vTextureCoord;
varying float vXClipDist;
varying float vGXClipDist;

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

    if(mTrans == 1.0) {
        float ratio = resolution.x/resolution.y;
        float alpha = 0.0;

        if(mReverse == 1.0) {
            if(vXClipDist < mNPos) {
                for(int i=0; i<10; i++) {
                    if(i < mStart) {
                        continue;
                    }
                    if(mXPos[i] <= -ratio || i > mEnd) {
                        break;
                    }
                    if(vXClipDist <= mXPos[i] && vXClipDist >= mXPos[i] - mArea[i]) {
                        alpha = 1.0;
                        break;
                    }
                }
            } else {
                alpha = 1.0;
            }
        } else {
            if(vXClipDist > mNPos) {
                for(int i=0; i<10; i++) {
                    if(i < mStart) {
                        continue;
                    }
                    if(mXPos[i] >= ratio || i > mEnd) {
                        break;
                    }
                    if(vXClipDist >= mXPos[i] && vXClipDist <= mXPos[i] + mArea[i]) {
                        alpha = 1.0;
                        break;
                    }
                }
            } else {
                alpha = 1.0;
            }
        }

        if(mReverse == 1.0) {
            if(alpha == 1.0) {
                alpha = 0.0;
            } else {
                alpha = 1.0;
            }
        }

        if(alpha == 0.0)
            discard;
    }

    if(mIsString == 0.0) {
        gl_FragColor = Theme();
    } else {
        gl_FragColor = texture2D(Texture, vTextureCoord);
    }

    if(mTrans != 1.0) {
        gl_FragColor.w = mAlpha;
    }
}