#version 400

#define MOTION_BLUR
#define LIGHT_ADAPTATION
#define GOD_RAYS
#define BLOOM
#define LENS_FLARE
#define SUN


/* FXAA */
const vec3 lumaVector = vec3(0.299, 0.587, 0.114);
const float quality[] = float[](1.0, 1.0, 1.0, 1.0, 1.0, 1.5, 2.0, 2.0, 2.0, 2.0, 4.0, 8.0, 16.0, 24.0, 32.0);

uniform vec2 pixelSize;

/* Motion blur */
const int blurSamples = 64;
const vec3 minColor = vec3(0.0001);

uniform float renderTime;

/* God rays */
const float decay = 1.01;
const float density = 0.74;
const float weight = 5.65;
const int godRaysSamples = 64;

uniform float exposure;
uniform vec2 lightPosition;

/* Lens flare */
/*
*** Flare 0 ***

const vec2 flareAxis = vec2(-1.0, 1.0);
const float flareDilatationFactor = 6.0;
*/

const vec2 flareAxis = vec2(-1.0, 1.8);
const float flareDilatationFactor = 6;

/* Sun */
const float concentration = 2.5;
const float sunSize = 0.15;
const float strength = 5.5;

/* Bloom */
const mat3 kernel = mat3(0.0625, 0.125, 0.0625, 0.125, 0.25, 0.125, 0.0625, 0.125, 0.0625);
const vec3 unity = vec3(1.0, 1.0, 1.0);

/* Common */
uniform sampler2D colorTexture;
uniform sampler2D depthTexture;
uniform sampler2D flareTexture;
uniform mat4 currentToPreviousViewportMatrix;

/* Shader data */
in vec2 textureCoords;
out vec4 fragColor;

float getLuma(vec3 color) {
	return sqrt(dot(color, lumaVector));
}

float getLuma(vec4 color) {
	return getLuma(color.rgb);
}

void lightAdaptation() {
	float lightDistanceFromCenter = length(lightPosition - vec2(0.5, 0.5));

	float adaptationFactor = min(1.2, lightDistanceFromCenter);

	fragColor *= max(min(1.0, adaptationFactor), getLuma(fragColor.rgb));
}

void bloom() {
    vec4 center = texture(colorTexture, textureCoords);
    vec4 up = textureOffset(colorTexture, textureCoords, ivec2(0, 1));
    vec4 down = textureOffset(colorTexture, textureCoords, ivec2(0, -1));
    vec4 left = textureOffset(colorTexture, textureCoords, ivec2(-1, 0));
    vec4 right = textureOffset(colorTexture, textureCoords, ivec2(1, 0));
    vec4 nw = textureOffset(colorTexture, textureCoords, ivec2(-1, 1));
    vec4 ne = textureOffset(colorTexture, textureCoords, ivec2(1, 1));
    vec4 sw = textureOffset(colorTexture, textureCoords, ivec2(-1, -1));
    vec4 se = textureOffset(colorTexture, textureCoords, ivec2(1, -1));

    mat3 rMatrix = mat3(nw.r, up.r, ne.r, left.r, center.r, right.r, sw.r, down.r, se.r);
    mat3 gMatrix = mat3(nw.g, up.g, ne.g, left.g, center.g, right.g, sw.g, down.g, se.g);
    mat3 bMatrix = mat3(nw.b, up.b, ne.b, left.b, center.b, right.b, sw.b, down.b, se.b);

    float rConvolved = dot(rMatrix * kernel * unity, unity);
    float gConvolved = dot(gMatrix * kernel * unity, unity);
    float bConvolved = dot(bMatrix * kernel * unity, unity);

    fragColor.rgb += 0.5 * (vec3(1.0, 1.0, 1.0) - fragColor.rgb) * vec3(rConvolved, gConvolved, bConvolved);
}

vec4 fxaa(vec2 coords) {
	vec4 color = texture(colorTexture, coords);

	float lumaCenter = getLuma(color);
	float lumaUp = getLuma(textureOffset(colorTexture, coords, ivec2(0, 1)));
	float lumaDown = getLuma(textureOffset(colorTexture, coords, ivec2(0, -1)));
	float lumaLeft = getLuma(textureOffset(colorTexture, coords, ivec2(-1, 0)));
	float lumaRight = getLuma(textureOffset(colorTexture, coords, ivec2(1, 0)));

	float lumaMin = min(lumaCenter, min(min(lumaUp, lumaDown), min(lumaLeft, lumaRight)));
	float lumaMax = max(lumaCenter, max(max(lumaUp, lumaDown), max(lumaLeft, lumaRight)));

	float lumaRange = lumaMax - lumaMin;

	if (lumaRange < max(0.0625, lumaMax * 0.125)) {
		return color;
	}

	float lumaNW = getLuma(textureOffset(colorTexture, coords, ivec2(-1, 1)));
	float lumaNE = getLuma(textureOffset(colorTexture, coords, ivec2(1, 1)));
	float lumaSW = getLuma(textureOffset(colorTexture, coords, ivec2(-1, -1)));
	float lumaSE = getLuma(textureOffset(colorTexture, coords, ivec2(1, -1)));

	float lumaVertical = lumaUp + lumaDown;
	float lumaHorizontal = lumaLeft + lumaRight;

	float lumaNorth = lumaNW + lumaNE;
	float lumaSouth = lumaSW + lumaSE;
	float lumaWest = lumaNW + lumaSW;
	float lumaEast = lumaNE + lumaSE;

	float horizontalEdge = abs(lumaWest - 2.0 * lumaLeft) + 2.0 * abs(lumaVertical - 2.0 * lumaCenter) + abs(lumaEast - 2.0 * lumaRight);
	float verticalEdge = abs(lumaNorth - 2.0 * lumaUp) + 2.0 * abs(lumaHorizontal - 2.0 * lumaCenter) + abs(lumaSouth - 2.0 * lumaDown);

	bool isHorizontal = horizontalEdge >= verticalEdge;

	float firstLuma = isHorizontal ? lumaDown : lumaLeft;
	float secondLuma = isHorizontal ? lumaUp : lumaRight;

	float firstGradient = firstLuma - lumaCenter;
	float secondGradient = secondLuma - lumaCenter;

	bool isFirstGradientSteeper = abs(firstGradient) >= abs(secondGradient);

	float scaledGradient = 0.25 * max(abs(firstGradient), abs(secondGradient));

	float stepLength = isHorizontal ? pixelSize.y : pixelSize.x;

	float lumaAVG = 0.0;

	if(isFirstGradientSteeper) {
		stepLength = -stepLength;
		lumaAVG = 0.5 * (firstLuma + lumaCenter);
	} else {
		lumaAVG = 0.5 * (secondLuma + lumaCenter);
	}

	vec2 uv = coords;

	if(isHorizontal) {
		uv.y += stepLength * 0.5;
	} else {
		uv.x += stepLength * 0.5;
	}

	vec2 offset = isHorizontal ? vec2(pixelSize.x, 0.0) : vec2(0.0, pixelSize.y);

	vec2 firstUV = uv - offset;
	vec2 secondUV = uv + offset;

	float firstLumaEnd = getLuma(texture(colorTexture, firstUV)) - lumaAVG;
	float secondLumaEnd = getLuma(texture(colorTexture, secondUV)) - lumaAVG;

	bool firstReached = abs(firstLumaEnd) >= scaledGradient;
	bool secondReached = abs(secondLumaEnd) >= scaledGradient;

	bool bothReached = firstReached && secondReached;

	if(!firstReached) {
		firstUV -= offset;
	}

	if(!secondReached) {
		secondUV += offset;
	}

	if(!bothReached) {
		for(int i = 0; i < quality.length(); i++) {
			if(!firstReached) {
				firstLumaEnd = getLuma(texture(colorTexture, firstUV)) - lumaAVG;
			}

			if(!secondReached) {
				secondLumaEnd = getLuma(texture(colorTexture, secondUV)) - lumaAVG;
			}

			firstReached = abs(firstLumaEnd) >= scaledGradient;
			secondReached = abs(secondLumaEnd) >= scaledGradient;
			bothReached = firstReached && secondReached;

			if(!firstReached) {
				firstUV -= offset * quality[i];
			}

			if(!secondReached) {
				secondUV += offset * quality[i];
			}

			if(bothReached) {
				break;
			}
		}
	}

	float firstDistance = isHorizontal ? (coords.x - firstUV.x) : (coords.y - firstUV.y);
	float secondDistance = isHorizontal ? (secondUV.x - coords.x) : (secondUV.y - coords.y);

	bool isFirstDirection = firstDistance < secondDistance;
	float finalDistance = min(firstDistance, secondDistance);

	float thickness = firstDistance + secondDistance;

	float pixelOffset = -finalDistance / thickness + 0.5;

	bool isLumaCenterSmaller = lumaCenter < lumaAVG;

	bool correctVariation = ((isFirstDirection ? firstLumaEnd : secondLumaEnd) < 0.0) != isLumaCenterSmaller;

	float finalOffset = correctVariation ? pixelOffset : 0.0;

	float average = (1.0 / 12.0) * (2.0 * (lumaVertical + lumaHorizontal) + lumaWest + lumaEast);

	float value = clamp(abs(average - lumaCenter) / lumaRange, 0.0, 1.0);
	float var = (-2.0 * value + 3.0) * value * value;

	float finalSubPixelOffset = var * var * 0.75;

	finalOffset = max(finalOffset, finalSubPixelOffset);

	if(isHorizontal) {
		coords.y += finalOffset * stepLength;
	} else {
		coords.x += finalOffset * stepLength;
	}

	return texture(colorTexture, coords);
}

vec4 normalizeHVC(vec4 hvc) {
	return vec4(hvc.xyz / hvc.w, 1.0);
}

vec4 motionBlur() {
	float z = texture(depthTexture, textureCoords).r;

	vec4 viewportPosition = vec4(textureCoords.x * 2 - 1, textureCoords.y * 2 - 1, sqrt(z), 1.0);
	vec4 previousViewportPosition = normalizeHVC(currentToPreviousViewportMatrix * viewportPosition);

	vec2 velocity = (previousViewportPosition.xy - viewportPosition.xy) / renderTime * 20.0f;
    
    if(length(velocity) < 0.05f) {
        velocity = vec2(0.0f, 0.0f);
    }

    vec4 result = vec4(0.0);

    float contributions = 0.0;

    for (int i = 0; i < blurSamples; i++) {
    	vec4 color = fxaa(textureCoords + velocity * (float(i) / float(blurSamples - 1) - 0.5));

    	if(length(color.rgb) < length(minColor)) {
    		color.rgb = minColor;
    	}

    	float weight = pow(length(color.rgb), 0.3);

    	result += color * weight;
		contributions += weight;
    }
    
    return result / contributions;
}

void lens_flare() {
    float ratio = pixelSize.y / pixelSize.x;
    vec2 deltaLight = lightPosition - vec2(0.5);
    
    vec2 axis = normalize(vec2(ratio, 1) * flareAxis);
    
    float cosAngle = dot(normalize(deltaLight), axis);
    float sinAngle = sqrt(1 - cosAngle * cosAngle);
    float dilatation = flareDilatationFactor * length(deltaLight);
    
    if(deltaLight.y * axis.x < deltaLight.x * axis.y) {
        sinAngle *= -1; // Sign correction to compensate the dot product
    }
    
    mat2 transformationMatrix = mat2(cosAngle, -sinAngle, sinAngle, cosAngle);
    
    vec2 transformed = mat2(cosAngle, -sinAngle, sinAngle, cosAngle) * ((textureCoords - vec2(0.5)) * vec2(ratio, 1) / dilatation);
    transformed += vec2(0.5);
    float flareIntensity = min(1.0, pow(length(textureCoords - lightPosition), 2.0));

    if(dilatation > 2) {
        flareIntensity *= exp(2 - dilatation);
    }
    
    vec4 flare = texture(flareTexture, transformed);
    fragColor += flare * flare.w * flareIntensity;
}

void godRays() {
	float factor = 1.0;
	float lightDistanceFromCenter = length(lightPosition - vec2(0.5, 0.5));

	if(lightDistanceFromCenter > 0.5) {
		factor = max(0.0, 1.5 - lightDistanceFromCenter);
	}

	vec2 delta = (textureCoords - lightPosition) / float(godRaysSamples) * density;
	vec2 coords = textureCoords;
	vec4 color = vec4(0.0);
	float illumination = 1.0;

	for(int i = 0; i < godRaysSamples ; i++) {
		coords -= delta;
		color += texture(colorTexture, coords) * illumination * weight;
		illumination *= decay;
	}

	fragColor += color * exposure * factor;
}

void sun() {
    vec4 color = texture(colorTexture, textureCoords);

    float ratio = pixelSize.y / pixelSize.x;
    
    vec2 transformed = textureCoords - vec2(0.5, 0.5);
    transformed.x *= ratio;
    transformed += vec2(0.5, 0.5);
    
    float lightDistanceFromCenter = length(lightPosition - transformed);
    float brightness = pow(lightDistanceFromCenter / sunSize, -concentration);
    
    float factor = 1.0 - pow(strength, -brightness);

    fragColor += (1 - fragColor) * factor * max(1 - color.a, 1 - pow(getLuma(color), 3.5));
}

void main() {
    #ifdef MOTION_BLUR
        fragColor = motionBlur();
    #else
        fragColor = fxaa(textureCoords);
    #endif
    
    #ifdef LENS_FLARE
        lens_flare();
    #endif
    
    #ifdef SUN
        sun();
        
        #ifdef LIGHT_ADAPTATION
            lightAdaptation();
        #endif
    #endif

    #ifdef GOD_RAYS
		godRays();
    #endif
    
    #ifdef BLOOM
        bloom();
    #endif
}
