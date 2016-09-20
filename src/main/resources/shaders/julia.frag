#version 400

#define SEED_REAL   /**0.0/**/     /**-0.7269/**/     /**/-0.8/**/    /**-0.381966/**/
#define SEED_IMAG   /**1.0/**/     /**0.1889/**/      /**/0.156/**/   /**0.618034/**/

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

vec4 get_c(uint i)
{
    if (i==max_iter)
        return vec4(0.0, 0.0, 0.0, 1.0);
    float r = float(i+1)/float(max_iter);
    float g = (i>max_iter/2)?(float((2*i+1)-max_iter)/float(max_iter)):(0.0);
    float b = (i>3*max_iter/4)?(float((4*i+1)-(3*max_iter))/float(max_iter)):(0.0);
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
        cn = cpow(c, option_enum-2) + z;
        //x = (c.x * c.x - c.y * c.y) + z.x;
        //y = (2*c.x*c.y) + z.y;
    }
    return i;
}

void main()
{
    dvec2 c = dvec2(
        double(aspect)*(double(scale)*double(frag_pos.x)+double(translx)),
        double(scale)*double(frag_pos.y)+double(transly)
    );
    uint i = julia(c);
	frag_color = get_c(i);
}
