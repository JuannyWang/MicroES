#ifdef GL_ES
  precision mediump float;
#endif

uniform sampler2D Texture;

uniform mat3 matrix;
uniform vec2 resolution;
uniform float mAlpha;
uniform float mColorType;

varying vec2 vTextureCoord;

vec4 gray(vec4 color) {
    float y = dot(color, vec4(0.299, 0.587, 0.114, 0));
    return vec4(y, y, y, color.a);
}

vec4 sepia(vec4 color) {
    vec3 new_color = min(matrix * color.rgb, 1.0);
    return vec4(new_color.rgb, color.a);
}

void main() {
    vec4 color = texture2D(Texture, vTextureCoord);
    if(mColorType == 1.0) {
        gl_FragColor = gray(color);
    } else if(mColorType == 2.0) {
        gl_FragColor = sepia(color);
    } else {
        gl_FragColor = color;
    }

    gl_FragColor.w = mAlpha;
}
