#version 400

in vec3 position;
in vec3 color;

out vec4 fragColor;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
	fragColor = vec4(color, 1.0);
    gl_Position = vec4(projection * view * model * vec4(position, 1.0));
}
