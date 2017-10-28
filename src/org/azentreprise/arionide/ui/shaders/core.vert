#version 400

in vec3 position;

flat out vec3 seed;
out vec4 fragVertex;
out vec3 fragNormal;

uniform dmat4 model;
uniform dmat4 view;
uniform dmat4 projection;

void main() {
    seed = position;
    fragVertex = vec4(model * dvec4(position, 1.0));
    fragNormal = position;
    gl_Position = vec4(projection * view * model * dvec4(position, 1.0));
}
