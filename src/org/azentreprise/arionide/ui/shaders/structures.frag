#version 400

const vec3 lightColor = vec3(1.0, 1.0, 1.0);
const float shininess = 4.0;
const float diffuseAmplifier = 2.0;
const float specularAmplifier = 2.0;

uniform vec4 color;
uniform dvec3 camera;
uniform vec3 specularColor;
uniform dvec3 lightPosition;
uniform float ambientFactor;

in vec4 fragVertex;
in vec3 fragNormal;

out vec4 outColor;

void main() {
	dvec3 normal = normalize(dvec3(fragNormal));
	dvec3 lightDirection = normalize(lightPosition);
	dvec3 cameraObject = camera - dvec3(fragVertex);
	dvec3 reflectionDirection = reflect(-lightDirection, normal);

	float diffuseFactor = float(dot(normal, lightDirection));
	float specularFactor = 0.0;

	if(diffuseFactor > 0.0) {
		specularFactor = diffuseFactor * pow(max(0.0, float(dot(reflectionDirection, normalize(cameraObject)))), shininess);
	}

	vec3 ambient = ambientFactor * color.xyz;
	vec3 diffuse = max(0.0, diffuseAmplifier * diffuseFactor) * color.xyz;
	vec3 specular = specularAmplifier * specularFactor * specularColor;

	outColor = (vec4((ambient + diffuse + specular) * lightColor, color.w) + color) / 2.0 * (1.0 - length(vec3(cameraObject)) / 100.0); // like fog
}
