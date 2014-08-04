#ifdef GL_ES
  precision mediump float;
#endif

uniform sampler2D Texture_1;
uniform sampler2D Texture_2;

uniform float numTexture;
uniform float mAlpha;
uniform float mYValue;
uniform float mSize;

uniform vec2 resolution;
uniform float vDoLattic;
varying vec2 vTextureCoord_1;
varying vec2 vTextureCoord_2;
varying float vClipDist;

void main ()
{
    vec4 vColor_1 = texture2D(Texture_1, vTextureCoord_1);
    vec4 vColor_2 = texture2D(Texture_2, vTextureCoord_2);

    if(numTexture == 1.0) {
        gl_FragColor = vColor_1;
    } else {
        if(vClipDist < mYValue) {
            gl_FragColor = vColor_1;
        } else {
            if(vClipDist > 0.5 && vClipDist < 1.0) {
                if(mYValue < 0.0 && vClipDist > mYValue + 1.25) {
                    gl_FragColor = vColor_2;
                } else {
                    gl_FragColor = vColor_1;
                }
            } else if(vClipDist > 0.0 && vClipDist < 0.5) {
                if(mYValue < -0.5 && vClipDist > mYValue + 1.0) {
                    gl_FragColor = vColor_2;
                } else {
                    gl_FragColor = vColor_1;
                }
            } else if(vClipDist > -0.5 && vClipDist < -0.0) {
                if(mYValue < -1.0 && vClipDist > mYValue + 0.75) {
                    gl_FragColor = vColor_2;
                } else {
                    gl_FragColor = vColor_1;
                }
            } else if(vClipDist > -1.0 && vClipDist < -0.5) {
                if(mYValue < -1.5 && vClipDist > mYValue + 0.5) {
                    gl_FragColor = vColor_2;
                } else {
                    gl_FragColor = vColor_1;
                }
            } else {
                gl_FragColor = vColor_2;
            }
        }
    }
}