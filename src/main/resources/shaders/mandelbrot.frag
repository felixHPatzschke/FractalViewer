#version 400

in vec2 frag_pos;
//in int max_i;

uniform float translx;
uniform float transly;
uniform float scale;
uniform int max_iter;
uniform float aspect;
uniform sampler1D tex;
uniform float seed_real;
uniform float seed_imag;

out vec4 frag_color;

vec4 get_c(uint i)
{
    if (i==max_iter)
        return vec4(0.0, 0.0, 0.0, 1.0);
    //float r = float(i+1)/float(max_iter);
    //float g = float(i+1)/float(max_iter);
    //float b = float(i+1)/float(max_iter);
    float b = float(i+1)/float(max_iter);
    float r = (i>max_iter/2)?(float((2*i+1)-max_iter)/float(max_iter)):(0.0);
    float g = (i>3*max_iter/4)?(float((4*i+1)-(3*max_iter))/float(max_iter)):(0.0);
    // TODO: do stuff
    return vec4(r, g, b, 1.0);
}

uint mandelbrot(dvec2 c)
{
	uint i;
    dvec2 z = dvec2(0.0, 0.0);
    for(i=0; i<max_iter; ++i)
    {
        double x = (z.x * z.x - z.y * z.y) + c.x;
        double y = (2*z.x*z.y) + c.y;
        if(x*x+y*y>4.0) break;
        z.x = x;
        z.y = y;
    }
    return i;
}

void main()
{
    dvec2 c = dvec2(
        double(aspect)*(double(scale)*double(frag_pos.x)+double(translx)),
        double(scale)*double(frag_pos.y)+double(transly)
    );
    uint i = mandelbrot(c);
	frag_color = get_c(i);
	//frag_color = texture1D(tex, (i==max_iter ? 0.0 : float(i)/100.0))
}
