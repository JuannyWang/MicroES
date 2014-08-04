#ifdef GL_ES
  precision mediump float;
#endif

uniform sampler2D Texture_1;
uniform sampler2D Texture_2;

uniform float numTexture;

uniform float mAlpha;

uniform vec2 resolution;
uniform float vDoLattic;
varying vec2 vTextureCoord_1;
varying vec2 vTextureCoord_2;

void main ()
{
    vec4 vColor_1 = texture2D(Texture_1, vTextureCoord_1);
    vec4 vColor_2 = texture2D(Texture_2, vTextureCoord_2);

    if(numTexture == 1.0) {
        gl_FragColor = vColor_1;
    } else {
        gl_FragColor = vColor_1*(1.0-mAlpha) + vColor_2*mAlpha;
    }
}