#version 400

precision highp float;

uniform vec2 translation;
uniform vec2 scale;

in vec2 position;
in vec2 uv;

out vec2 textureCoords;

void main() {
    gl_Position = vec4(translation + position * scale, 0.0, 1.0);
    textureCoords = uv;
}
