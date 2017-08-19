#version 400

uniform vec3 rgb;
uniform float alpha;

out vec4 color;

void main() {
    color.w = alpha;
    color.xyz = rgb;
}
