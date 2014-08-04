uniform mat4 uMVPMatrix;
uniform mat4 uMVMMatrix;

attribute vec4 aPosition;
attribute vec4 aTextureCoord;

varying vec2 vTextureCoord;
varying vec4 vCenter;
varying float vGXClipDist;
varying float vGYClipDist;

varying float vTXClipDist;
varying float vTYClipDist;

void main() {
    gl_Position = uMVPMatrix * aPosition;

    vec4 tmp_position = uMVMMatrix * aPosition;

    //To get center position in global coords
    vCenter = uMVMMatrix * vec4(0.0, 0.0, 0.0, 1.0);

    vTextureCoord = aTextureCoord.xy;

    vec4 aXClipDist = vec4(1.0, 0.0, 0.0, 0.0);
    vec4 aYClipDist = vec4(0.0, 1.0, 0.0, 0.0);

    vGXClipDist = dot(gl_Position.xyz, aXClipDist.xyz) + aXClipDist.w;
    vGYClipDist = dot(gl_Position.xyz, aYClipDist.xyz) + aYClipDist.w;

    vTXClipDist = dot(tmp_position.xyz, aXClipDist.xyz) + aXClipDist.w;
    vTYClipDist = dot(tmp_position.xyz, aYClipDist.xyz) + aYClipDist.w;
}