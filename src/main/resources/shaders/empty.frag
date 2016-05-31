#version 400

in vec2 frag_pos;

uniform float translx;
uniform float transly;
uniform float scale;
uniform int max_iter;
uniform float aspect;
uniform sampler1D tex;
uniform float seed_real;
uniform float seed_imag;

out vec4 frag_color;

void main()
{
    frag_color = vec4(1.0, 1.0, 1.0, 1.0);
}
