#version 400

/* FXAA */
const vec3 lumaVector = vec3(0.299, 0.587, 0.114);

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


void lightAdaptation() {
	float lightDistanceFromCenter = length(lightPosition - vec2(0.5, 0.5));

	float adaptationFactor = min(1.5, lightDistanceFromCenter);

	fragColor *= max(adaptationFactor, getLuma(fragColor.rgb));
}


vec4 fxaa(vec2 coords) {
	vec3 rgbM = texture(colorTexture, coords).rgb;

	vec3 rgbNW = textureOffset(colorTexture, coords, ivec2(-1, 1)).rgb;
	vec3 rgbNE = textureOffset(colorTexture, coords, ivec2(1, 1)).rgb;
	vec3 rgbSW = textureOffset(colorTexture, coords, ivec2(-1, -1)).rgb;
	vec3 rgbSE = textureOffset(colorTexture, coords, ivec2(1, -1)).rgb;

	vec4 color = vec4(0.0);

	const vec3 toLuma = vec3(0.299, 0.587, 0.114);

	float lumaNW = dot(rgbNW, toLuma);
	float lumaNE = dot(rgbNE, toLuma);
	float lumaSW = dot(rgbSW, toLuma);
	float lumaSE = dot(rgbSE, toLuma);
	float lumaM = dot(rgbM, toLuma);

	float lumaMin = min(lumaM, min(min(lumaNW, lumaNE), min(lumaSW, lumaSE)));
	float lumaMax = max(lumaM, max(max(lumaNW, lumaNE), max(lumaSW, lumaSE)));

	if (lumaMax - lumaMin < lumaMax * 0.1)
	{
		return vec4(rgbM, 1.0);
	}

	vec2 samplingDirection;
	samplingDirection.x = -((lumaNW + lumaNE) - (lumaSW + lumaSE));
	samplingDirection.y =  ((lumaNW + lumaSW) - (lumaNE + lumaSE));

	float samplingDirectionReduce = max((lumaNW + lumaNE + lumaSW + lumaSE) * 0.25 * 8.0, 128.0);

	float minSamplingDirectionFactor = 1.0 / (min(abs(samplingDirection.x), abs(samplingDirection.y)) + samplingDirectionReduce);

	samplingDirection = clamp(samplingDirection * minSamplingDirectionFactor, vec2(-8.0, -8.0), vec2(8.0, 8.0)) * 0.2;

	vec3 rgbSampleNeg = texture(colorTexture, coords + samplingDirection * (1.0/3.0 - 0.5)).rgb;
	vec3 rgbSamplePos = texture(colorTexture, coords + samplingDirection * (2.0/3.0 - 0.5)).rgb;

	vec3 rgbTwoTab = (rgbSamplePos + rgbSampleNeg) * 0.5;

	vec3 rgbSampleNegOuter = texture(colorTexture, coords + samplingDirection * (0.0/3.0 - 0.5)).rgb;
	vec3 rgbSamplePosOuter = texture(colorTexture, coords + samplingDirection * (3.0/3.0 - 0.5)).rgb;

	vec3 rgbFourTab = (rgbSamplePosOuter + rgbSampleNegOuter) * 0.25 + rgbTwoTab * 0.5;

	float lumaFourTab = dot(rgbFourTab, toLuma);

	if (lumaFourTab < lumaMin || lumaFourTab > lumaMax)
	{
		color = vec4(rgbTwoTab, 1.0);
	}
	else
	{
		color = vec4(rgbFourTab, 1.0);
	}

	return color;
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

    for (int i = 1; i < blurSamples; i++) {
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
