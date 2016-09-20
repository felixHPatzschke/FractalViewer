#version 400

#define PI 3.1415926535897932384626433832795

in vec2 frag_pos;

uniform float translx;
uniform float transly;
uniform float scale;
uniform int max_iter;
uniform float aspect;
uniform sampler1D tex;
uniform float seed_real;
uniform float seed_imag;

layout (location = 0) out vec3 complex_color;
layout (location = 1) out vec3 iter_color;


vec3 get_i_color(uint i)
{
    if (i==max_iter)
        return vec3(0.0, 0.0, 0.0);
    //float r = float(i+1)/float(max_iter);
    //float g = float(i+1)/float(max_iter);
    //float b = float(i+1)/float(max_iter);
    float b = float(i+1)/float(max_iter);
    float r = (i>max_iter/2)?(float((2*i+1)-max_iter)/float(max_iter)):(0.0);
    float g = (i>3*max_iter/4)?(float((4*i+1)-(3*max_iter))/float(max_iter)):(0.0);
    // TODO: do stuff
    return vec3(r, g, b);
}

vec3 get_c_color(float H/*angle*/, float abs)
{
    if(H<0.0)
        H+=2*PI;
    float S = sqrt(abs/2);
    float V = (1.0-(abs/2))*(1.0-(abs/2));

    // HSV to RGB conversion
    int h = int(3.0*H/PI);
    float f = ((3.0*H/PI)-h);
    float p = V*((1.0-S));
    float q = V*((1.0-S*f));
    float t = V*((1.0-S*(1.0-f)));

    if (h==1)
        return vec3(q, V, p);
    else if (h==2)
        return vec3(p, V, t);
    else if (h==3)
        return vec3(p, q, V);
    else if (h==4)
        return vec3(t, p, V);
    else if (h==5)
        return vec3(V, p, q);
    else
        return vec3(V, t, p);
}

float atan2(float y, float x)
{
    if (x==0)
    {
        if(y==0) return 0.0;
        else if(y>0) return PI/2.0;
        else return -PI/2.0;
    }
    else if(x>0) return atan(y/x);
    else
    {
        if(y>=0) return (atan(y/x)+PI);
        else return (atan(y/x)-PI);
    }
}

void main()
{
    dvec2 c = dvec2(
        double(aspect)*(double(scale)*double(frag_pos.x)+double(translx)),
        double(scale)*double(frag_pos.y)+double(transly)
    );

    dvec2 z = dvec2(0.0, 0.0);
    for(uint i=0; i<max_iter; ++i)
    {
        double x = (z.x * z.x - z.y * z.y) + c.x;
        double y = (2*z.x*z.y) + c.y;
        if(x*x+y*y>4.0)
        {
            //frag_color = vec4(0.0, 0.0, 0.0, 1.0);
    	    iter_color = get_i_color(i);
            return;
        }
        z.x = x;
        z.y = y;
    }
    if(z.x==0.0 && z.y==0.0)
        complex_color = vec3(1.0, 1.0, 1.0);
    else
    {
        double a = sqrt(z.x*z.x+z.y*z.y);
        float angle = atan2(float(z.y), float(z.x));
	    complex_color = get_c_color(angle, float(a));
    }
}
