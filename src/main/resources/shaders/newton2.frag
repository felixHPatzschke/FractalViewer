#version 400

#define ETA (1.0e-6)
#define ETA_TWO (1.0e-1)

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
//    if((option_enum & 1)==1)
    //    abs/=sqrt(3.0);
//    else if((option_enum & 2)==2)
        abs /= 2*sqrt(2.0);
        //abs /= PI;


    abs = -1/(abs+1)+1;

    if(H<0.0)
        H+=2*PI;

    if(abs>1)
        //return vec4(0.0, 0.0, 0.0, 1.0);
//        if((option_enum & 4)==4)
            abs -= int(abs);
//        else
            //abs = 1/(abs);
//    if((option_enum & 8)==8)
    //abs=1-abs;

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

dvec2 cdiv(dvec2 a, dvec2 b)
{
    double d = dot(b, b);
    if(d == 0.0) return a;
    else return dvec2( (a.x*b.x + a.y*b.y) / d, (a.y*b.x - a.x*b.y) / d );
}

dvec2 cmul(dvec2 a, dvec2 b) { return dvec2( (a.x*b.x - a.y*b.y), (a.y*b.x + a.x*b.y) ); }

dvec2 csqr(dvec2 a) { return dvec2(a.x*a.x-a.y*a.y, 2*a.x*a.y); }

double rexp(double x, uint n)
{
    double xi = 1;
    double d = 1;
    double res = 0;
    for(uint i=1; i<=n; ++i)
    {
        res += xi/d;
        xi *= x;
        d *= i;
    }
    return res;
}

dvec2 cexp(dvec2 x)
{
    return rexp(x.x, 6)*dvec2(double(cos(float(x.y))), double(sin(float(x.y))));
}

dvec2 csin(dvec2 x) { return dvec2(double(sin(float(x.x))), double(sinh(float(x.y)))); }

dvec2 ccos(dvec2 x) { return dvec2(double(cos(float(x.x))), double(cosh(float(x.y)))); }

dvec2 f(dvec2 n)
{
    // z²-1
    //return dvec2(n.x*n.x - n.y*n.y - 1.0, 2*n.x*n.y);

    // (z-1)*z²
    //return cmul(csqr(n), n-dvec2(1.0, 0.0))+dvec2(0.0, 1.0);

    // z³-1
    return dvec2(n.x*n.x*n.x - 3.0*n.x*n.y*n.y - 1.0, -n.y*n.y*n.y + 3.0*n.x*n.x*n.y);

    // z³-1
    //return cmul(n,cmul(n,n))+dvec2(-1.0, 0.0);

    // z³-z+i
    //return cmul(n,cmul(n,n)) - n + dvec2(0.0, 1.0);

    // z^4-1
    //return csqr(csqr(n))+dvec2(-1.0, 0.0);

    // (z²-2)² - 1  =  z^4 - 4z² + 3
    //return csqr(csqr(n) - dvec2(1.0, 0.0)) - dvec2(1.0, 0.0);

    // z^4-z^2+i
    //return csqr(csqr(n))-csqr(n)+dvec2(0.0, 1.0);

    // e^z-1
    //return cexp(n)-dvec2(1.0, 0.0);

    // sin(z)
    //return csin(n);
    //return dvec2(double(cos(float(n.x))), double(cos(float(n.y))));

    // RE( 2 - 2 cos(2x) - 4x sin(x) + x² - x^4 )
    //return dvec2(2.0 - (2*cos(2*float(n.x))) - (4*n.x*sin(float(n.x))) + n.x*n.x - n.x*n.x*n.x*n.x, n.y*n.y);
}

dvec2 df(dvec2 n)
{
    // 2 z
    //return 2.0 * n;

    // z²+2*z*(z-1)
    //return csqr(n)+(2*cmul(n,n-dvec2(1.0, 0.0)));

    // 3 z²
    return 3.0 * vec2(n.x*n.x - n.y*n.y, 2.0 * n.x * n.y);

    // 3 z²
    //return 3*csqr(n);

    // 3z²-1
    //return 3*csqr(n)+dvec2(-1.0, 0.0);

    // 4 z³
    //return 4*cmul(n,cmul(n,n));

    // 4z³ - 8z
    //return 4*cmul(n, csqr(csqr(n) - dvec2(1.0, 0.0)));

    // 4z³-2z
    //return 4*cmul(n,csqr(n))-2*n;

    // e^z
    //return cexp(n);

    // cos(z)
    //return ccos(n);
    //return -1*dvec2(double(sin(float(n.x))), double(sin(float(n.y))));

    // RE( -4x³ + 2x - 4 sin(x) - 4x cos(x) + 8 sin(x) cos(x) )
    //return dvec2(((-4*n.x*n.x*n.x) + (2*n.x) - (4*sin(float(n.x))) - (4*n.x*cos(float(n.x))) + (8*sin(float(n.x)*cos(float(n.x))))), 2*n.y);
}

void main()
{
    dvec2 z = dvec2(
            double(aspect)*(double(scale)*double(frag_pos.x)+double(translx)),
            double(scale)*double(frag_pos.y)+double(transly)
    );

    dmat2 mfactor;
    double sfactor;
    mfactor = dmat2(double(cos(seed_real*PI)), double(sin(seed_real*PI)),
                    -1.0*double(sin(seed_real*PI)), double(cos(seed_real*PI)));
    sfactor = seed_imag;

    uint i;
    for(i=1; i<max_iter; ++i)
    {
        dvec2 zn = z - sfactor*(mfactor*cdiv(f(z), df(z)));
        if(distance(zn, z)<ETA) break;
        z = zn;
    }

    if((option_enum & 1) == 1)
        z = f(z);

    double a = sqrt(z.x*z.x+z.y*z.y);
    float angle = ((option_enum & 2) == 0)
                ? (atan2(float(z.y), float(z.x)))
                : (atan2(
                    -sin(0.04*PI*float(i)),
                    -cos(0.04*PI*float(i)))
                );
    frag_color = get_c(angle, float(a));
    if((option_enum&4)==0) frag_color = vec4(1.0, 1.0, 1.0, 2.0)-frag_color;
}
