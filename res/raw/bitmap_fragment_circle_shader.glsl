#ifdef GL_ES
  precision mediump float;
#endif

uniform sampler2D Texture;

varying vec2 vTextureCoord;

void main() {
    vec4 srcColor = texture2D(Texture, vTextureCoord);

    if(srcColor.r == 0.0 && srcColor.g == 0.0 && srcColor.b == 0.0) {
        discard;
    } else {
        srcColor = vec4(1.0, 1.0, 1.0, (srcColor.r+srcColor.g+srcColor.b)/3.0);
    }

    gl_FragColor = srcColor;
}
