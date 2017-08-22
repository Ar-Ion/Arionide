#version 400

in vec3 position;
in vec3 color;

out vec3 fragColor;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    fragColor = color;
    gl_Position = projection * view * model * vec4(position, 1.0);
}
