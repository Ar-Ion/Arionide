#version 400

const vec3 specularColor = vec3(1.0, 1.0, 1.0);
const float shininess = 3.0;
const float attenuation = 0.01;

flat in vec3 seed;
in vec3 fragVertex;

out vec4 outColor;

uniform mat4 model;
uniform vec3 color;
uniform vec3 camera;
uniform vec3 lightColor;
uniform vec3 lightPosition;
uniform float ambient;

float rand(float seed) {
    return fract(sin(dot(fragVertex.xy * seed, vec2(12.9898989898, 78.233333))) * 43758.54535353);
}

void main() {
    if(color.x > 1.0) {
        float brightness = rand(1);
        float red = (1 - brightness) * rand(2) * brightness;
        outColor = vec4(red + brightness, brightness, brightness, 1.0);
    } else {
        mat3 matrix = transpose(inverse(mat3(model)));
        vec3 normal = normalize(matrix * fragVertex);
        vec3 surfaceLightVector = normalize(lightPosition - fragVertex);
        vec3 surfaceCameraVector = normalize(camera - fragVertex);
        
        float diffuse = max(0.0, dot(normal, surfaceLightVector));
        float specular = 0.0;
        
        if(diffuse > 0.0) {
            specular = max(0.0, pow(-dot(surfaceCameraVector, reflect(surfaceLightVector, normal)), shininess));
        }
        
        float att = 1.0 / (1.0 + attenuation * pow(length(lightPosition - fragVertex), 2));
        
        outColor = vec4(((ambient + att * diffuse) * color + att * specular * specularColor) * lightColor, 1.0);
    }
}
