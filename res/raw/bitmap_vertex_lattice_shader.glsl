uniform mat4 uMVPMatrix;

attribute vec4 aPosition;
attribute vec4 aTextureCoord;
attribute vec2 resolution;

varying vec2 vTextureCoord;
varying vec2 vResolution;
varying float vAClipDist;
varying float vBClipDist;

void main() {
    gl_Position = uMVPMatrix * aPosition;
    vTextureCoord = aTextureCoord.xy;
    float sizeratio = resolution.y/resolution.x;

    vec4 aAClipDist = vec4(1.0, -sizeratio, 0.0, 0.0);
    vec4 aBClipDist = vec4(1.0, sizeratio, 0.0, 0.0);

    vAClipDist = dot(gl_Position.xyz, aAClipDist.xyz) + aAClipDist.w;
    vBClipDist = dot(gl_Position.xyz, aBClipDist.xyz) + aBClipDist.w;

    vResolution = resolution;
}