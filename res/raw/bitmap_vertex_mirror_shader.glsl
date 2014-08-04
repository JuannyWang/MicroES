uniform mat4 uMVPMatrix;

attribute vec4 aPosition;
attribute vec4 aTextureCoord;

varying vec2 vTextureCoord;
varying float vGXClipDist;
varying float vGYClipDist;
varying float vGAClipDist;
varying float vGBClipDist;
varying float vGCClipDist;

void main() {
    gl_Position = uMVPMatrix * aPosition;
    vTextureCoord = aTextureCoord.xy;

    vec4 aXClipDist = vec4(1.0, 0.0, 0.0, 0.0);
    vec4 aYClipDist = vec4(0.0, 1.0, 0.0, 0.0);
    vec4 aAClipDist = vec4(1.0, -1.0, 0.0, 0.0);
    vec4 aBClipDist = vec4(1.0, 1.0, 0.0, 0.0);
    vec4 aCClipDist = vec4(1.0, -0.2, 0.0, 0.0);

    vGXClipDist = dot(gl_Position.xyz, aXClipDist.xyz) + aXClipDist.w;
    vGYClipDist = dot(gl_Position.xyz, aYClipDist.xyz) + aYClipDist.w;
    vGAClipDist = dot(gl_Position.xyz, aAClipDist.xyz) + aAClipDist.w;
    vGBClipDist = dot(gl_Position.xyz, aBClipDist.xyz) + aBClipDist.w;
    vGCClipDist = dot(gl_Position.xyz, aCClipDist.xyz) + aCClipDist.w;
}