#ifdef GL_ES
  precision mediump float;
#endif

uniform sampler2D Texture;

uniform mat3 matrix;
uniform vec2 resolution;
uniform float mAlpha;
uniform float mCount;
uniform float mSetBound;
uniform float mGap[2];
uniform float mBound[2];

varying vec4 vCenter;
varying vec2 vTextureCoord;
varying float vTXClipDist;
varying float vTYClipDist;

void main() {
    vec4 color = texture2D(Texture, vTextureCoord);
    if(vCenter.x + mGap[0] < vTXClipDist || vCenter.x - mGap[0] > vTXClipDist ||
        vCenter.y + mGap[1] < vTYClipDist || vCenter.y - mGap[1] > vTYClipDist) {

        //In here we have two type => bound scale, none scale
        if(mSetBound == 0.0) {
            gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
        } else {
            if(vCenter.x + mBound[0] < vTXClipDist || vCenter.x - mBound[0] > vTXClipDist ||
                vCenter.y + mBound[1] < vTYClipDist || vCenter.y - mBound[1] > vTYClipDist) {
                gl_FragColor = vec4(1.0, 1.0, 1.0, 0.0);
            } else {
                gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);
            }
        }
    } else {
        gl_FragColor = color;
        gl_FragColor.w = mAlpha;
    }
}
