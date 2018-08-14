#version 400

uniform vec2 scale;
uniform vec2 translation;

in vec2 position;

out vec2 coords;

void main() {
	gl_Position = vec4(scale * position + translation, 0.0, 1.0);
    coords = position;
}
