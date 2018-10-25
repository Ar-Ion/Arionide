#version 400

const mat2x2 rotation = mat2x2(vec2(0, -1), vec2(1, 0));

uniform vec2 scale;
uniform vec2 translation;

uniform vec2 radius;

in vec2 position;
in vec2 edgeFactor;

out vec2 coords;
out vec2 textureCoords;

mat2x2 mat_pow(mat2x2 matrix, int power) {
    mat2x2 result = mat2x2(1.0); // Identity
    
    for(int i = 0; i < power; i++) {
        result *= matrix;
    }
    
    return result;
}

void main() {
    vec2 realFactor = mat_pow(rotation, gl_InstanceID) * edgeFactor;
    gl_Position = vec4(scale * (position + realFactor * radius) + translation, 0.0, 1.0);
    coords = position;
    textureCoords = edgeFactor + vec2(0.0, 1.0);
}
