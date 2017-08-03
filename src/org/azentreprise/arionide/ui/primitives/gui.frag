#version 400

uniform vec3 rgb;
uniform float alpha;
uniform sampler2D sampler;

out vec4 color;
flat in int state;
smooth in vec2 uv;

void main() {
	if(state != 0) {
		color.w = alpha;
    	color.xyz = rgb;
    
		if(state == 2) {
    		color.w *= texture(sampler, uv).w;
   		}
   	}
}