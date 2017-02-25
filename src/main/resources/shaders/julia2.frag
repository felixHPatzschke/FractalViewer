#version 400

#define PI 3.1415926535897932384626433832795
#define SEED_REAL   /**0.0/**/     /**-0.7269/**/     /**-0.8/**/    /**/-0.381966/**/
#define SEED_IMAG   /**1.0/**/     /**0.1889/**/      /**0.156/**/   /**/0.618034/**/

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
    if(H<0.0)
        H+=2*PI;
    abs/=2;
    abs=1-abs;  // inverts the brightness within the valid area, makes for a visible bondary
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

uint julia(dvec2 c)
{
	uint i;
    dvec2 z = dvec2(seed_real, seed_imag);
    //double x = c.x;
    //double y = c.y;
    dvec2 cn = c;
    for(i=0; i<max_iter; ++i)
    {
        //c.x = x;
        //c.y = y;
        c = cn;
        if(cn.x*cn.x+cn.y*cn.y>4.0) break;
        cn.x = c.x*c.x - c.y*c.y + z.x;
        cn.y = 2*c.x*c.y + z.y;
        //x = (c.x * c.x - c.y * c.y) + z.x;
        //y = (2*c.x*c.y) + z.y;
    }
    return i;
}

vec4 get_outer_c(uint i)
{
    float r = float(i+1)/float(max_iter);
    float g = float(i+1)/float(max_iter);
    float b = float(i+1)/float(max_iter);
    //float g = (i>max_iter/2)?(float((2*i+1)-max_iter)/float(max_iter)):(0.0);
    //float b = (i>3*max_iter/4)?(float((4*i+1)-(3*max_iter))/float(max_iter)):(0.0);
    // TODO: do stuff
    return vec4(r, g, b, 1.0);
}

vec4 noise(vec4 original, vec3 co)
{
    return 0.8*original + 0.2*vec4(
        fract(sin(dot(co.xy, vec2(12.9898, 78.233))) * 43758.5453),
        fract(sin(dot(co.xz, vec2(12.9898, 78.233))) * 43758.5453),
        fract(sin(dot(co.yz, vec2(12.9898, 78.233))) * 43758.5453),
        1.0
    );
}

void main()
{
    dvec2 c = dvec2(
        double(aspect)*(double(scale)*double(frag_pos.x)+double(translx)),
        double(scale)*double(frag_pos.y)+double(transly)
    );

    uint outeriter = julia(c);
    if(outeriter < max_iter)
    {
        frag_color = get_outer_c(outeriter);
        return;
    }

    dvec2 z = dvec2(seed_real, seed_imag);

    double x = c.x;
    double y = c.y;
    for(uint i=0; i<max_iter; ++i)
    {
        c.x = x;
        c.y = y;
        if(x*x+y*y>4.0)
        {
            frag_color = vec4(0.0, 0.0, 0.0, 1.0);
            return;
        }
        x = (c.x * c.x - c.y * c.y) + z.x;
        y = (2*c.x*c.y) + z.y;
    }
    if(c.x==0.0 && c.y==0.0)
        frag_color = vec4(1.0, 1.0, 1.0, 1.0);
    else
    {
        double a = sqrt(c.x*c.x+c.y*c.y);
        float angle = atan2(float(c.y), float(c.x));
	    frag_color = get_c(angle, float(a));
    }

    vec3 co = vec3(c, outeriter);
    frag_color = noise(frag_color, co);

}
