#version 400

uniform sampler2D textSampler;

out vec4 color;
in vec2 uv;

void main() {
    color = texture(textSampler, uv);
}
