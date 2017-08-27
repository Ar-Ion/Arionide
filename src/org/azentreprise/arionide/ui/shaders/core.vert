#version 400

in vec3 position;

flat out vec3 seed;
out vec3 fragVertex;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    fragVertex = position;
    seed = position;
    gl_Position = projection * view * model * vec4(position, 1.0);
}
