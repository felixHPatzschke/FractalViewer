#version 400

#define ETA (1.0e-6)
#define ETA_TWO (1.0e-1)

#define PI 3.1415926535897932384626433832795
#define COLOR_POSITION 1
#define COLOR_VALUE 2
#define COLOR_MAPPING COLOR_POSITION

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

vec4 get_c(float H/*angle*/, float abs)
{
    abs/=sqrt(2.0);
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

dvec2 f(dvec2 n)
{
    return dvec2(n.x*n.x*n.x - 3.0*n.x*n.y*n.y - 1.0, -n.y*n.y*n.y + 3.0*n.x*n.x*n.y);
    //return dvec2(n.x*n.x - n.y*n.y - 1.0, 2*n.x*n.y);
    //return dvec2(double(sin(float(n.x))), double(sinh(float(n.y))));
}

dvec2 df(dvec2 n)
{
    return 3.0 * vec2(n.x*n.x - n.y*n.y, 2.0 * n.x * n.y);
    //return 2.0 * n;
    //return dvec2(double(cos(float(n.x))), double(cosh(float(n.y))));
}

dvec2 cdiv(dvec2 a, dvec2 b)
{
    double d = dot(b, b);
    if(d == 0.0) return a;
    else return dvec2( (a.x*b.x + a.y*b.y) / d, (a.y*b.x - a.x*b.y) / d );
}

void main()
{
    dvec2 z = dvec2(
            double(aspect)*(double(scale)*double(frag_pos.x)+double(translx)),
            double(scale)*double(frag_pos.y)+double(transly)
    );
    uint i;

    dmat2 mfactor;
    double sfactor;
    //mfactor = dmat2(1.0, 1.0, -1.0, 1.0);     // 45° rotation Matrix, scalar factor sqrt2
    //mfactor = dmat2(0.0, 1.0, -1.0, 0.0);     // 90° rotation Matrix
    //mfactor = dmat2(1.0, 0.0, 0.0, 1.0);      // Identity Matrix
    mfactor = dmat2(double(cos(seed_real*PI)), double(sin(seed_real*PI)),
                    -1.0*double(sin(seed_real*PI)), double(cos(seed_real*PI)));
    //sfactor = 0.7071067811865475;
    //sfactor = 1.0;
    sfactor = seed_imag;

    for(i=1; i<max_iter; ++i)
    {
        dvec2 zn = z - sfactor*(mfactor*cdiv(f(z), df(z)));
        //if(distance(zn, z)<ETA) break;
        z = zn;
    }
    if(COLOR_MAPPING == COLOR_VALUE)
        z=f(z);
    if(z.x==0.0 && z.y==0.0)
        frag_color = vec4(1.0, 1.0, 1.0, 1.0);
    else
    {
        double a = sqrt(z.x*z.x+z.y*z.y);
        float angle = atan2(float(z.y), float(z.x));
	    frag_color = get_c(angle, float(a));
    }
}
