#version 400

in vec2 frag_pos;

out vec4 frag_color;

uniform sampler2D tex;

void main()
{
    frag_color = texture(tex, frag_pos);
}
