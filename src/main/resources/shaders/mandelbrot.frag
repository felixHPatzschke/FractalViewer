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
uniform int option_enum;

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

dvec2 cmul(dvec2 a, dvec2 b) { return dvec2( (a.x*b.x - a.y*b.y), (a.y*b.x + a.x*b.y) ); }

dvec2 cpow(dvec2 a, int x)
{
    if(x<=0)
            return dvec2(1.0, 0.0);
    if(x==1)
        return a;
    if(x==2)
        return dvec2((a.x*a.x - a.y*a.y), (2*a.x*a.y));
    dvec2 res = a;
    while(x-->1)
        res = cmul(res, a);
    /*while(x>=2)
        if(x%2==0)
        {
            res = dvec2((res.x*res.x - res.y*res.y), (2*res.x*res.y));
            x /= 2;
            //x>>1;
        }
        else
        {
            res = cmul(a, res);
            --x;
        }
    */
    return res;

}

uint mandelbrot(dvec2 c)
{
	uint i;
    dvec2 z = dvec2(0.0, 0.0);
    for(i=0; i<max_iter; ++i)
    {
        dvec2 zn = cpow(z, option_enum-2) + c;
        //double x = (z.x * z.x - z.y * z.y) + c.x;
        //double y = (2*z.x*z.y) + c.y;
        if(zn.x*zn.x+zn.y*zn.y>4.0) break;
        z = zn;
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
