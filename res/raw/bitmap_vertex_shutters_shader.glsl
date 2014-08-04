uniform mat4 uMVPMatrix;

attribute vec4 aPosition;
attribute vec4 aTextureCoord_1;
attribute vec4 aTextureCoord_2;

attribute float aZValue;

varying vec2 vTextureCoord_1;
varying vec2 vTextureCoord_2;
varying float vClipDist;

void main() {
    gl_Position = uMVPMatrix * aPosition;
    vTextureCoord_1 = aTextureCoord_1.xy;
    vTextureCoord_2 = aTextureCoord_2.xy;

    vec4 aClipDist = vec4(0.0, -1.0, 0.0, 0.0);
    vClipDist = dot(aPosition.xyz, aClipDist.xyz) + aClipDist.w;
}