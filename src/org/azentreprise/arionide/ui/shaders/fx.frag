#version 400

/* FXAA */
const vec3 lumaVector = vec3(0.299, 0.587, 0.114);
const float quality[] = float[](1.0, 1.0, 1.0, 1.0, 1.0, 1.5, 2.0, 2.0, 2.0, 2.0, 4.0, 8.0, 16.0, 24.0, 32.0);

uniform vec2 pixelSize;

/* Motion blur */
const vec2 blurTransform = vec2(0.5, 0.5);
const int blurSamples = 64;
const vec3 minColor = vec3(0.0001);

/* God rays */
const float decay = 1.0;
const float density = 0.84;
const float weight = 5.65;
const int godRaysSamples = 64;

uniform float exposure;
uniform vec2 lightPosition;

/* Common */
uniform sampler2D colorTexture;
uniform sampler2D depthTexture;
uniform dmat4 currentToPreviousViewportMatrix;

/* Shader data */
in vec2 textureCoords;
out vec4 fragColor;

float getLuma(vec3 color) {
	return sqrt(dot(color, lumaVector));
}

float getLuma(vec4 color) {
	return sqrt(dot(color.rgb, lumaVector));
}

void lightAdaptation() {
	float lightDistanceFromCenter = length(lightPosition - vec2(0.5, 0.5)) / 2.0;

	float adaptationFactor = min(1.5, lightDistanceFromCenter);

	fragColor *= max(adaptationFactor, getLuma(fragColor.rgb));
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
		return color.rgb;
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
	value *= (-2.0 * value + 3.0) * value;

	float finalSubPixelOffset = value * value * 0.75;

	finalOffset = max(finalOffset, finalSubPixelOffset);

	if(isHorizontal) {
		coords.y += finalOffset * stepLength;
	} else {
		coords.x += finalOffset * stepLength;
	}

	return texture(colorTexture, coords);
}

dvec4 normalizeHVC(dvec4 hvc) {
	return vec4(hvc.xyz / hvc.w, hvc.w);
}

vec4 motionBlur() {
	double z = texture(depthTexture, textureCoords).r;

	dvec4 viewportPosition = normalizeHVC(dvec4(textureCoords.x * 2.0 - 1.0, (1.0 - textureCoords.y) * 2.0 - 1.0, z * 2.0 - 1.0, 1.0));
	dvec4 previousViewportPosition = normalizeHVC(currentToPreviousViewportMatrix * viewportPosition);

	dvec2 velocity = (previousViewportPosition.xy * vec2(1.0, -1.0) - viewportPosition.xy * vec2(1.0, -1.0)) * blurTransform / 2.0;

    vec4 result = vec4(0.0);

    float contributions = 0.0;

    for (int i = 0; i < blurSamples; i++) {
    	vec4 color = fxaa(textureCoords + vec2(velocity) * (float(i) / float(blurSamples - 1) - 0.5));

    	if(length(color.rgb) < length(minColor)) {
    		color.rgb = minColor;
    	}

    	float weight = pow(length(color.rgb), 0.3);

    	result += color * weight;
		contributions += weight;
    }

    return result / contributions;
}

vec4 godRays() {
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

	return color * exposure * factor;
}

void main() {
	fragColor = motionBlur();

	lightAdaptation();

	if(exposure > 0.0) {
		fragColor += godRays();
	}
}
