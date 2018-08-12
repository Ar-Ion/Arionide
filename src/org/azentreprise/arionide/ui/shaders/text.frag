#version 400

precision highp float;

uniform sampler2D bitmap;
uniform vec3 rgb;
uniform float alpha;

in vec2 textureCoords;
out vec4 color;

void main() {
    color = vec4(rgb, alpha) * texture(bitmap, textureCoords);
}
