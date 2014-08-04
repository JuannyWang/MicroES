#extension GL_OES_EGL_image_external : require

precision mediump float;

uniform samplerExternalOES exTexture;


uniform float xAPosition;
uniform float yAPosition;
uniform float xBPosition;
uniform float yBPosition;
uniform float mAlpha;
uniform vec2 resolution;

varying vec2 vTextureCoord;

void main ()
{
    vec4 srcColor;
    srcColor = texture2D(exTexture, vTextureCoord);

    vec3 finalColour = srcColor.xyz;

    vec2 lightAPosition;
    vec2 lightBPosition;

    lightAPosition.x = xAPosition;
    lightAPosition.y = yAPosition;

    lightBPosition.x = xBPosition;
    lightBPosition.y = yBPosition;

    float lightABrightness = 0.8;
    float lightBBrightness = 0.8;

    float lightAValue = (1.0 - distance(gl_FragCoord.xy / resolution, lightAPosition)) * lightABrightness;
    float lightBValue = (1.0 - distance(gl_FragCoord.xy / resolution, lightBPosition)) * lightBBrightness;


    float colorA = clamp(lightAValue, 0.0, 1.0);
    float colorB = clamp(lightBValue, 0.0, 1.0);

    finalColour.rg += colorA;
    finalColour.gb += colorB;

    gl_FragColor.xyz = finalColour;
    gl_FragColor.w = mAlpha;
}
