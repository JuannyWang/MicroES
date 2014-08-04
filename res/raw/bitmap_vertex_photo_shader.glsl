uniform mat4 uMVPMatrix;
uniform mat4 uMVMMatrix;

attribute vec4 aPosition;
attribute vec4 aTextureCoord;

varying vec2 vTextureCoord;
varying vec4 vCenter;
varying float vTXClipDist;
varying float vTYClipDist;

void main() {
    gl_Position = uMVPMatrix * aPosition;
    vTextureCoord = aTextureCoord.xy;

    vec4 tmp_position = uMVMMatrix * aPosition;

    vCenter = uMVMMatrix * vec4(0.0, 0.0, 0.0, 1.0);

    vec4 aXClipDist = vec4(1.0, 0.0, 0.0, 0.0);
    vec4 aYClipDist = vec4(0.0, 1.0, 0.0, 0.0);
    vTXClipDist = dot(tmp_position.xyz, aXClipDist.xyz) + aXClipDist.w;
    vTYClipDist = dot(tmp_position.xyz, aYClipDist.xyz) + aYClipDist.w;
}