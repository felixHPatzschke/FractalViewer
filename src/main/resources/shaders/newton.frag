#version 400

#define ETA (1.0e-6)
#define ETA_TWO (1.0e-1)

#define ROOT_NONE 0u
#define ROOT_ONE 1u
#define ROOT_TWO 2u
#define ROOT_THREE 3u

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

out vec4 frag_color;

vec4 get_c(uint i, uint ri)
{
    if (i==max_iter)
        return vec4(1.0, 1.0, 1.0, 1.0);
    float r, g, b;
    if ( ri == ROOT_ONE )
    {
        r = float(i+1)/float(max_iter);
        g = (i>max_iter/2)?(float((2*i+1)-max_iter)/float(max_iter)):(0.0);
        b = (i>3*max_iter/4)?(float((4*i+1)-(3*max_iter))/float(max_iter)):(0.0);
    }else if ( ri == ROOT_TWO )
    {
        g = float(i+1)/float(max_iter);
        b = (i>max_iter/2)?(float((2*i+1)-max_iter)/float(max_iter)):(0.0);
        r = (i>3*max_iter/4)?(float((4*i+1)-(3*max_iter))/float(max_iter)):(0.0);
    }else if ( ri == ROOT_THREE )
    {
        b = float(i+1)/float(max_iter);
        r = (i>max_iter/2)?(float((2*i+1)-max_iter)/float(max_iter)):(0.0);
        g = (i>3*max_iter/4)?(float((4*i+1)-(3*max_iter))/float(max_iter)):(0.0);
    }else
    {
        r = float(i+1)/float(max_iter);
        g = float(i+1)/float(max_iter);
        b = float(i+1)/float(max_iter);
    }
    return vec4(r, g, b, 1.0);
}

uint root_index(dvec2 z)
{
    uint res = ROOT_NONE;
    if(distance(z,dvec2(1.0, 0.0))<ETA_TWO) res = ROOT_ONE;
    else if(distance(z,dvec2(-0.5, -0.866))<ETA_TWO) res = ROOT_TWO;
    else if(distance(z,dvec2(-0.5, 0.866))<ETA_TWO) res = ROOT_THREE;
    return res;
}

dvec2 f(dvec2 n)
{
    return dvec2(n.x*n.x*n.x - 3.0*n.x*n.y*n.y - 1.0, -n.y*n.y*n.y + 3.0*n.x*n.x*n.y);
    //return dvec2(n.x*n.x - n.y*n.y - 1.0, 2*n.x*n.y);
}

dvec2 df(dvec2 n)
{
    return 3.0 * vec2(n.x*n.x - n.y*n.y, 2.0 * n.x * n.y);
    //return 2.0 * n;
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
    /**
    In order to imitate complex multiplication, the vector is multiplied
    by a rotation matrix for the complex angle of the factor and real number
    as the absolute value of the complex factor.
    Here, seed_imag makes the absolute value and seed_real the argument.
    */

    for(i=1; i<max_iter; ++i)
    {
        dvec2 zn = z - sfactor*(mfactor*cdiv(f(z), df(z)));
        if(distance(zn, z)<ETA) break;
        z = zn;
    }
    frag_color = get_c(i, root_index(z));
}
