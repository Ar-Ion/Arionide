#version 400

in vec3 position;

out vec4 fragPosition;

uniform dmat4 model;
uniform dmat4 view;
uniform dmat4 projection;

void main() {
	fragPosition = vec4(position, 1.0);
    gl_Position = vec4(projection * view * model * dvec4(position, 1.0));
}
