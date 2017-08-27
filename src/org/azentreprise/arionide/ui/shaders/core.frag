#version 400

const vec3 specularColor = vec3(1.0, 1.0, 1.0);
const float shininess = 16.0;
const float attenuationFactor = 0.0001;
const float sunEmissionConcentration = 32.0;

flat in vec3 seed;
in vec3 fragVertex;

out vec4 outColor;

uniform mat4 model;
uniform vec4 color;
uniform vec3 camera;
uniform vec3 lightColor;
uniform vec3 lightPosition;
uniform float ambientFactor;

float rand(float id) {
    return fract(sin(dot(seed.xz * id, vec2(12.9898989898, 78.233333))) * 43758.54535353);
}

float sunBrightness() {
    return pow(max(dot(normalize(fragVertex), vec3(0.0, 1.0, 0.0)), 0.0), sunEmissionConcentration);
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
        vec3 normal = normalize(fragVertex);
        vec3 lightDirection = normalize(lightPosition - fragVertex);
        vec3 cameraDirection = normalize(camera - fragVertex);
        vec3 reflectionDirection = reflect(-lightDirection, normal);
        
        float diffuseFactor = max(0.0, dot(normal, lightDirection));
        float specularFactor = 0.0;
        float attenuation = 1.0 / (1.0 + attenuationFactor * pow(length(lightPosition - fragVertex), 2));
        
        if(diffuseFactor > 0.0) {
            specularFactor = pow(max(0.0, dot(cameraDirection, reflectionDirection)), shininess);
        }
        
        vec3 ambient = ambientFactor * color.xyz;
        vec3 diffuse = diffuseFactor * color.xyz * attenuation;
        vec3 specular = specularFactor * specularColor.xyz * attenuation;
        
        outColor = vec4((ambient + diffuse + specular) * lightColor, color.w);
    }
}
