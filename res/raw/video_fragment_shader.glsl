#extension GL_OES_EGL_image_external : require

precision mediump float;

uniform samplerExternalOES exTexture;

uniform mat3 matrix;
uniform vec2 resolution;

varying vec2 vTextureCoord;

void main() {

    vec4 color = texture2D(exTexture, vTextureCoord);
    gl_FragColor = color;
}
