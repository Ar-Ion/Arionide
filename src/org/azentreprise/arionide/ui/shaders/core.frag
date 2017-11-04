#version 400

precision lowp float;

const vec3 lightColor = vec3(1.0, 1.0, 1.0);
const float shininess = 8.0;
const float attenuationFactor = 0.0001;
const float sunEmissionConcentration = 32.0;

flat in vec3 seed;
in vec4 fragVertex;
in vec3 fragNormal;

out vec4 outColor;

uniform vec4 color;
uniform dvec3 camera;
uniform vec3 specularColor;
uniform dvec3 lightPosition;
uniform float ambientFactor;

float rand(float id) {
    return fract(sin(dot(seed.xz * id, vec2(12.9898989898, 78.233333))) * 43758.54535353);
}

float sunBrightness() {
    return pow(max(0.0, float(dot(normalize(fragNormal), normalize(vec3(lightPosition - camera))))), sunEmissionConcentration);
}

void main() {
    if(color.x > 1.0) {
        float brightness = min(1.0, rand(1) + sunBrightness());
        float red = (1 - brightness) * rand(2) * brightness;
        outColor = vec4(red + brightness, brightness, brightness, 1.0);
    } else if(color.y > 1.0) {
        float brightness = sunBrightness();
        outColor = vec4(brightness, brightness, brightness, 1.0);
    } else {
        dvec3 normal = normalize(dvec3(fragNormal));
        dvec3 lightDirection = normalize(lightPosition);
        dvec3 cameraDirection = normalize(camera - dvec3(fragVertex));
        dvec3 reflectionDirection = reflect(-lightDirection, normal);
        
        float diffuseFactor = max(0.0, float(dot(normal, lightDirection)));
        float specularFactor = 0.0;
        
        if(diffuseFactor > 0.0) {
            specularFactor = pow(max(0.0, float(dot(reflectionDirection, cameraDirection))), shininess);
        }
        
        vec3 ambient = ambientFactor * color.xyz;
        vec3 diffuse = diffuseFactor * color.xyz;
        vec3 specular = specularFactor * specularColor.xyz;
        
        outColor = vec4((ambient + diffuse + specular) * lightColor, color.w);
    }
}
