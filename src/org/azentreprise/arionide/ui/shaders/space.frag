#version 400

const float starsPerFragment = 1;
const float sparkleFactor = 0.6;

uniform vec3 lightPosition;
uniform ivec2 windowDimensions;

in vec4 fragColor;

out vec4 outColor;

void main() {
    outColor = fragColor;
}
