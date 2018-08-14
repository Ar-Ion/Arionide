#version 400

uniform vec3 rgb;
uniform float alpha;

uniform vec2 lightCenter;
uniform float lightRadius;
uniform float lightStrength;

in vec2 coords;

out vec4 color;

void main() {
    color.xyz = rgb;
    
    if(lightRadius > -1.0) { // Radial gradient
        color.w = max(alpha, float(lightStrength * (1.0 - min(1.0, distance(coords, lightCenter) / lightRadius))));
    } else {
        color.w = alpha;
    }
}
