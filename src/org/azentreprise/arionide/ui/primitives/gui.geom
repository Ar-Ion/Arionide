#version 400

const ivec2[4] transform = ivec2[4](ivec2(0, -1), ivec2(-1, 0), ivec2(0, 1), ivec2(1, 0));

const float radius = 16.0;

uniform dvec2 pixelSize;

layout(lines_adjacency) in; // 4 vertices
layout(triangle_strip, max_vertices = 25) out;

flat out int state; // 0: no render / 1: color rendering / 2: uv rendering
smooth out vec2 uv;

void triangulate(in int id, inout vec4 thirdVertex) {
    vec4 vertex = gl_in[id].gl_Position;
    
    vec4 firstTransform = vec4(transform[id] * pixelSize, 0.0, 0.0);
    vec4 secondTransform = vec4(transform[(id + 3) % 4] * pixelSize, 0.0, 0.0);
    
    vec4 firstVertex = vertex + firstTransform * radius;
        
    if(id != 0) {
        gl_Position = firstVertex + secondTransform;
        EmitVertex(); // transition vertex
        
        state = 0;
        gl_Position = thirdVertex;
        EmitVertex();
    }
    
    thirdVertex = vertex + secondTransform * radius;
    
    state = 1;
    uv = vec2(0.0, 0.0);
    gl_Position = firstVertex;
    EmitVertex();
            
    state = 2;
    uv = vec2(0.0, 1.0);
    gl_Position = vertex;
    EmitVertex();

    uv = vec2(1.0, 1.0);
    gl_Position = thirdVertex;
    EmitVertex();
    
    state = 0;
    gl_Position = thirdVertex + firstTransform;
    EmitVertex(); // transition vertex
    state = 1;
}

void main() {
    vec4 buffer = vec4(0.0, 0.0, 0.0, 0.0);
    
    state = 1;
    
    triangulate(0, buffer);
    triangulate(1, buffer);
    triangulate(2, buffer);    
    triangulate(3, buffer);
    
    vec4 veryFirstVertex = gl_in[0].gl_Position + vec4(transform[0] * pixelSize * radius, 0.0, 0.0);
    
    gl_Position = veryFirstVertex;
    EmitVertex();
    
    gl_Position = veryFirstVertex + vec4(transform[3] * pixelSize, 0.0, 0.0);
    EmitVertex();
}