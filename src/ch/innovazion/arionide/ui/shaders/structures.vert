#version 400

precision mediump float;

in vec3 position;

out vec4 ambientColor;
out vec3 fragNormal;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;
uniform vec4 color;
uniform float ambientFactor;

void main() {
    mat3 simplified = mat3(model);
    vec3 fragVertex = simplified * position;
    
    fragNormal = normalize(fragVertex - simplified * vec3(0.0));
    ambientColor = ambientFactor * color;
    
    gl_Position = projection * view * model * vec4(position, 1.0);
}
