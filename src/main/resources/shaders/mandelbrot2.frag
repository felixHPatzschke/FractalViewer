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
uniform int option_enum;


out vec4 frag_color;

vec4 get_c(float H/*angle*/, float abs)
{
    if(H<0.0)
        H+=2*PI;
    abs/=2;
    //abs=1-abs;  // inverts the brightness within the valid area, makes for a visible bondary
    float S = sqrt(abs);
    float V = (1.0-(abs))*(1.0-(abs));

    // HSV to RGB conversion
    int h = int(3.0*H/PI);
    float f = ((3.0*H/PI)-h);
    float p = V*((1.0-S));
    float q = V*((1.0-S*f));
    float t = V*((1.0-S*(1.0-f)));

    if (h==1)
        return vec4(q, V, p, 1.0);
    else if (h==2)
        return vec4(p, V, t, 1.0);
    else if (h==3)
        return vec4(p, q, V, 1.0);
    else if (h==4)
        return vec4(t, p, V, 1.0);
    else if (h==5)
        return vec4(V, p, q, 1.0);
    else
        return vec4(V, t, p, 1.0);
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

void main()
{
    dvec2 c = dvec2(
        double(aspect)*(double(scale)*double(frag_pos.x)+double(translx)),
        double(scale)*double(frag_pos.y)+double(transly)
    );

    dvec2 z = dvec2(0.0, 0.0);
    for(uint i=0; i<max_iter; ++i)
    {
        dvec2 zn = cpow(z, option_enum-2) + c;
        double x = zn.x;//(z.x * z.x - z.y * z.y) + c.x;
        double y = zn.y;//(2*z.x*z.y) + c.y;
        //if(zn.x*zn.x + zn.y*zn.y > 4.0)
        if(x*x+y*y>4.0)
        {
            frag_color = vec4(0.0, 0.0, 0.0, 1.0);
            return;
        }
        z.x = x;
        z.y = y;
    }
    if(z.x==0.0 && z.y==0.0)
        frag_color = vec4(1.0, 1.0, 1.0, 1.0);
    else
    {
        double a = sqrt(z.x*z.x+z.y*z.y);
        float angle = atan2(float(z.y), float(z.x));
	    frag_color = get_c(angle, float(a));
    }
}
