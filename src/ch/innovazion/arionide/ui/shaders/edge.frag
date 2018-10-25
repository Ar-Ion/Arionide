#version 400

uniform vec3 rgb;
uniform float alpha;

uniform vec2 lightCenter;
uniform float lightRadius;
uniform float lightStrength;

uniform sampler2D edgeTexture;

in vec2 coords;
in vec2 textureCoords;

out vec4 color;

void main() {
    color = vec4(rgb, texture(edgeTexture, textureCoords).w);

    if(lightRadius > 0.0) { // Radial gradient
        color.w *= max(alpha, float(lightStrength * (1.0 - min(1.0, distance(coords, lightCenter) / lightRadius))));
    } else {
        color.w *= alpha;
    }
}
