#version 400

const float sunEmissionConcentration = 32.0;
const float starsPerFragment = 0.01;
const float sparkleFactor = 0.6;

uniform dvec3 lightPosition;
uniform ivec2 windowDimensions;

in vec4 fragPosition;

out vec4 outColor;

vec2 getSeed() {
	return round(windowDimensions * fragPosition.xz * sign(fragPosition.y) * sparkleFactor);
}

float rand(float id) {
    return fract(sin(dot(getSeed() * id, vec2(12.9898989898, 78.233333))) * 43758.54535353);
}

float sunBrightness() {
    return pow(max(0.0, float(dot(normalize(fragPosition.xyz), normalize(vec3(lightPosition))))), sunEmissionConcentration);
}

void main() {
	float sun = sunBrightness();

	outColor = vec4(sun, sun, sun, 1.0);

	if(rand(1) <= starsPerFragment) {
		float factor = rand(2);
		float red = (1 - factor) * rand(3) * factor;
		float blue = (1 - factor) * rand(4) * factor;
		float brightness = 0.1 + pow(rand(5), 32.0);

		outColor += vec4(vec3(red + factor, factor, blue + factor) * brightness, 1.0) * pow(1.0 - sun, 4.0);
	}
}
