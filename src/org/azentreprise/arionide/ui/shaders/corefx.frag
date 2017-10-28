#version 400

const float exposure = 0.005;
const float decay = 0.995;
const float density = 0.8;
const float weight = 6.0;

const int samples = 100;

uniform sampler2D texture;
uniform vec2 lightPosition;

in vec2 textureCoords;
out vec4 outColor;

void main() {
    outColor = vec4(0);
    
    // Get current texture coordinates.
    vec2 textCoo = textureCoords.xy;
    
    vec2 light = lightPosition;
    
    if(abs(light.y) < 1.0) {
    // Calculate the vector that is a one step on vector from lightsource to
    // the pixel of image.
    vec2 deltaTextCoord = textCoo - light;
    deltaTextCoord *= 1.0 /  float(samples) * density;
    
    // Set up illumination decay factor.
    float illuminationDecay = 1.0;
    
    // Evaluate the summation of shadows from occlusion texture
    for(int i = 0; i < samples ; i++)
    {
        // Step sample location along ray.
        textCoo -= deltaTextCoord;
        
        /*if(textCoo < 0.0 || textCoo > 1.0) {
            continue;
        }*/
        
        // Retrieve sample at new location.
        vec4 colorSample  = texture(texture, vec2( clamp(textCoo,0,1.0) ));
        
        // Apply sample attenuation scale/decay factors.
        colorSample  *= illuminationDecay * weight;
        
        // Accumulate combined color.
        outColor += colorSample;
        
        // Update exponential decay factor.
        illuminationDecay *= decay;
    }
    
    // Output final color with a further scale control factor.
    outColor *= exposure;
    
    // Get the avarage of color from calculated light scattering and normal scene.
    }
    outColor += texture(texture, textureCoords);
    outColor *= 0.5;
}
