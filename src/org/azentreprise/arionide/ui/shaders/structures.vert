#version 400

precision highp double;
precision highp float;

in vec3 position;

out vec4 fragVertex;
out vec3 fragNormal;

uniform dmat4 model;
uniform dmat4 view;
uniform dmat4 projection;

void main() {
    fragVertex = vec4(model * dvec4(position, 1.0));
    fragNormal = inverse(transpose(mat3(model))) * position;
    gl_Position = vec4(projection * view * model * dvec4(position, 1.0));
}
