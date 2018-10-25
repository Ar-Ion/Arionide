#version 400

uniform vec2 scale;
uniform vec2 translation;

uniform vec2 radius;

in vec2 position;
in vec2 unedgingFactor;

out vec2 coords;

void main() {
	gl_Position = vec4(scale * (position + unedgingFactor * radius) + translation, 0.0, 1.0);
    coords = position;
}
