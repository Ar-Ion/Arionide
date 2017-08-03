#version 400

uniform vec3 rgb;
uniform float alpha;
uniform sampler2d sampler;

out vec4 color;
flat in int state;
smooth in vec2 uv;

void main() {
	if(state != 0) {
		if(uv.x < -0.5) {
			color.w = alpha;
    		color.xyz = rgb;
		} else {
			color = texture(sampler, uv);
		}
    }
}