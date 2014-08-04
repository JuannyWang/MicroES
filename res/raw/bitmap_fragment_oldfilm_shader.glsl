#ifdef GL_ES
  precision mediump float;
#endif

uniform float SepiaValue;
uniform float InnerVignetting;
uniform float OuterVignetting;

uniform sampler2D Texture;

uniform float RandomValue;
uniform float mAlpha;

uniform vec2 resolution;

varying vec2 vTextureCoord;

vec3 Overlay (vec3 src, vec3 dst) {
    return vec3((dst.x <= 0.5) ? (2.0 * src.x * dst.x) : (1.0 - 2.0 * (1.0 - dst.x) * (1.0 - src.x)),
                (dst.y <= 0.5) ? (2.0 * src.y * dst.y) : (1.0 - 2.0 * (1.0 - dst.y) * (1.0 - src.y)),
                (dst.z <= 0.5) ? (2.0 * src.z * dst.z) : (1.0 - 2.0 * (1.0 - dst.z) * (1.0 - src.z)));
}

void main ()
{
    // Sepia RGB value
    vec3 sepia = vec3(112.0 / 255.0, 66.0 / 255.0, 20.0 / 255.0);

    // Step 1: Convert to grayscale
    vec4 srcColor = texture2D(Texture, vTextureCoord);

    vec3 colour = srcColor.xyz;
    float gray = (colour.x + colour.y + colour.z) / 3.0;
    vec3 grayscale = vec3(gray);

    // Step 2: Appy sepia overlay
    vec3 finalColour = Overlay(sepia, grayscale);

    // Step 3: Lerp final sepia colour
    finalColour = grayscale + SepiaValue * (finalColour - grayscale);

    float rand = abs(sin(RandomValue * vTextureCoord.x) * sin(RandomValue * vTextureCoord.y) * 10000.0);
    rand = rand / 64.0;
    float halfValue = rand - float(floor(rand));
    finalColour *= max( 1.0, (halfValue + 0.5));

    // Step 6: Apply vignetting
    // Max distance from centre to corner is ~0.7. Scale that to 1.0.
    float d = distance(vec2(0.5, 0.5), gl_FragCoord.xy / resolution) * 1.414213;
    float vignetting = clamp((OuterVignetting - d) / (OuterVignetting - InnerVignetting), 0.0, 1.0);
    finalColour.xyz *= vignetting;


    // Apply colour
    gl_FragColor.xyz = finalColour;
    gl_FragColor.w = 1.0;
    gl_FragColor.a = mAlpha;
}