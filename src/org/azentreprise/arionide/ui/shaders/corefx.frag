#version 400

const float decay = 1.0;
const float density = 0.84;
const float weight = 5.65;

const int samples = 100;

uniform sampler2D texture;
uniform vec2 lightPosition;
uniform float exposure;

in vec2 textureCoords;
out vec4 outColor;

void main()
{
    if(exposure > 0.0) {
        vec2 delta = (textureCoords - lightPosition) / float(samples) * density;
        vec2 coords = textureCoords;
        vec4 color = vec4(0.0);
        float illumination = 1.0;

        for(int i = 0; i < samples ; i++) {
            coords -= delta;
            color += texture(texture, coords) * illumination * weight;
            illumination *= decay;
        }
    
        color *= exposure;
    
        outColor += color;
        outColor += texture(texture, textureCoords);
    } else {
        outColor = texture(texture, textureCoords);
    }
}
