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

void main()
{
    dvec2 c = dvec2(
        double(aspect)*(double(scale)*double(frag_pos.x)+double(translx)),
        double(scale)*double(frag_pos.y)+double(transly)
    );

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
}
