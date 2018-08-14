#version 400

uniform vec2 translation;
uniform vec2 scale;

in vec2 position;
in vec2 uv;

out vec2 coords;
out vec2 textureCoords;

void main() {
    vec2 theCoords = translation + position * scale;
    
    gl_Position = vec4(theCoords, 0.0, 1.0);
    textureCoords = uv;
    coords = theCoords;
}
