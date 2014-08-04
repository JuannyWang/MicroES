#ifdef GL_ES
  precision mediump float;
#endif

uniform sampler2D Texture;

uniform mat3 matrix;
uniform vec2 resolution;
uniform float mType;

varying vec2 vTextureCoord;

void main() {
    vec4 color = texture2D(Texture, vTextureCoord);
    gl_FragColor = color;
    if((color.r != 1.0 || color.g != 1.0 || color.b != 1.0) && mType == 1.0) {
        gl_FragColor.a = 0.0;
    }
}
