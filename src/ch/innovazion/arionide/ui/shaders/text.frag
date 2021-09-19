#version 400

uniform sampler2D bitmap;
uniform vec3 rgb;
uniform float alpha;

uniform vec2 lightCenter;
uniform float lightRadius;
uniform float lightStrength;

in vec2 coords;
in vec2 textureCoords;

out vec4 color;

void main() {
    vec4 rgba = texture(bitmap, textureCoords);
    color = vec4(rgb, min(length(rgba.rgb), rgba.w));
    
    if(lightRadius > 0.0) { // Radial gradient
        color.w *= max(alpha, float(lightStrength * (1.0 - min(1.0, distance(coords, lightCenter) / lightRadius))));
    } else {
        color.w *= alpha;
    }
}
