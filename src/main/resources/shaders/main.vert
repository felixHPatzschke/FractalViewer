#version 400

in vec2 vert_pos;

out vec2 frag_pos;

void main()
{
    frag_pos = vert_pos;
    gl_Position = vec4(vert_pos, 0.0, 1.0);
}
