#version 400

precision lowp float;

/*
const vec3 lightColor = vec3(1.0, 1.0, 1.0);
const float shininess = 16.0;
const vec3 lightDirection = vec3(0.0, -1.0, 0.0);
*/

uniform vec4 color;

/*
uniform vec3 specularColor;
uniform vec3 camera;
*/

in vec4 ambientColor;
in vec3 fragNormal;

out vec4 outColor;

void main() {
    float diffuseFactor = max(0.0, fragNormal.y);
	outColor = vec4(diffuseFactor * color.xyz + ambientColor.xyz, ambientColor.w);
    
    /*
    vec3 reflectionDirection = reflect(lightDirection, fragNormal);
    float specularFactor = pow(max(0.0, float(dot(reflectionDirection, normalize(-camera + fragVertex)))), shininess);
	vec3 specular = diffuseFactor * specularFactor * specularColor;
     
     So much time wasted to get specular highlights
     */
}
