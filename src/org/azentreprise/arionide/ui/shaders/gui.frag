#version 400

uniform vec3 rgb;
uniform float alpha;
uniform sampler2D roundRectSampler;

uniform dvec2 lightCenter;
uniform double lightRadius;
uniform double lightStrength;

flat in int state;
in vec2 uv;
in vec2 coords;

out vec4 color;

void main() {
    if(state != 0) {
        color.xyz = rgb;
        
        if(lightRadius > -1.0) { // Radial gradient
            color.w = max(alpha, float(lightStrength * (1.0 - min(1.0, distance(coords, lightCenter) / lightRadius))));
        } else {
            color.w = alpha;
        }
        
        if(state == 2) {
            color.w *= texture(roundRectSampler, uv).w;
        }
    } else {
        color = vec4(0.0, 0.0, 0.0, 0.0);
    }
}
