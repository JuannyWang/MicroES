uniform mat4 uMVPMatrix;

attribute vec4 aPosition;
attribute vec4 aTextureCoord;

varying vec2 vTextureCoord;
varying float vXClipDist;
varying float vGXClipDist;

void main() {
    gl_Position = uMVPMatrix * aPosition;
    vTextureCoord = aTextureCoord.xy;

    vec4 aXClipDist = vec4(1.0, 0.0, 0.0, 0.0);

    vXClipDist = dot(aPosition.xyz, aXClipDist.xyz) + aXClipDist.w;
    vGXClipDist = dot(gl_Position.xyz, aXClipDist.xyz) + aXClipDist.w;
}