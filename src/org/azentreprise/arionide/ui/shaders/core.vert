#version 400

in vec3 position;

flat out vec3 seed;
out vec4 fragVertex;
out vec3 fragNormal;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main() {
    seed = position;
    fragVertex = model * vec4(position, 1.0);
    fragNormal = position;
    gl_Position = projection * view * model * vec4(position, 1.0);
}
