uniform mat4 uMVPMatrix;

attribute vec4 aPosition;
attribute vec4 aTextureCoord_1;
attribute vec4 aTextureCoord_2;

varying vec2 vTextureCoord_1;
varying vec2 vTextureCoord_2;

void main() {
    gl_Position = uMVPMatrix * aPosition;
    vTextureCoord_1 = aTextureCoord_1.xy;
    vTextureCoord_2 = aTextureCoord_2.xy;
}