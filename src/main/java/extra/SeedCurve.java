package extra;

import static java.lang.Math.cos;
import static java.lang.StrictMath.sin;

/**
 * Created by Felix on 20.02.2017.
 */
public abstract class SeedCurve {

    private double[] x = {0.0, 0.0};
    private double[] v = {0.0, 0.0};
    private double[] a = {0.0, 0.0};
    public double t = 0.0;
    private double T0 = 0.0, V0 = 0.0;
    private boolean needsNormalize = false;


    public SeedCurve(double x0, double y0, double vx0, double vy0)
    {
        x[0] = x0;
        x[1] = y0;
        v[0] = vx0;
        v[1] = vy0;
        T0 = T();
        V0 = V(x,t);
        debug();
    }


    public void tick(double dt)
    {
        dt /= 100.0*rsq(v);

        if(needsNormalize)
            normalize();

        x[0] += v[0]*dt;//(10.0*Math.sqrt(T()));
        x[1] += v[1]*dt;//(10.0*Math.sqrt(T()));

        a = F(x, v, t);

        v[0] += a[0]*dt;
        v[1] += a[1]*dt;

        t += dt;
    }

    public void debug()
    {
        //System.out.println("\nV0 = " + V0);
        System.out.println("    V = " + V(x,t));
        //System.out.println("T0 = " + T0);
        System.out.println("    T = " + T());
        System.out.println("T + V = " + (T() + V(x,t)));
    }

    private final void normalize()
    {
        double k = Math.sqrt((T0+V0-V(x,t))/T());
        if(!Double.isNaN(k))
        {
            v[0] *= k;
            v[1] *= k;
            needsNormalize = false;
            debug();
        }
    }

    public final void enqueue_normalize()
    {
        needsNormalize = true;
    }

    public double[] F(double[] x, double[] v, double t)
    {
        double[] dx = new double[2];
        dx[0] = x[0];
        dx[1] = x[1];

        double[] a = new double[2];
        dx[0] += 0.000000001;
        a[0] = -(V(dx,t)-V(x,t))/0.000000001;
        dx[0] = x[0];
        dx[1] += 0.000000001;
        a[1] = -(V(dx,t)-V(x,t))/0.000000001;

        //a[0] += 0.5*Math.cos(0.001*t)*v[1]/Math.sqrt(T());
        //a[0] += -0.5*Math.cos(0.001*t)*v[0]/Math.sqrt(T());

        return a;
    }

    public abstract double V(double[] x, double t);

    public final double T()
    {
        return (v[0]*v[0]+v[1]*v[1]);
    }

    public static final double rsq(double[] x)
    {
        return (x[0]*x[0]+x[1]*x[1]);
    }

    private static final double distsq(double[] x, double a, double b)
    {
        return ((x[0]-a)*(x[0]-a) + (x[1]-b)*(x[1]-b));
    }

    public final double[] getX()
    {
        return x;
    }




    public static final double V1(double[] x)
    {
        double r = Math.sqrt(rsq(x));
        return 0.1*(0.5/(2.0-r) + 0.5/(2.0+r) + 0.1/r + (0.05*Math.exp(x[0])));
    }

    public static final double V2(double[] x, double t)
    {
        double res = cos(0.0001*t)*(sin(16*x[0])+sin(16*x[1])) + sin(0.00015*t)*(sin(24*x[0])*sin(24*x[1]));
        return 0.001*(res*res*res);
    }

    public static final double V3(double[] x)
    {
        double res = 0.0;
        for(int i = 0; i < Meta.Julia_InterstingSpots.length; ++i)
        {
            res -= Math.exp( -200.0*distsq(x, Meta.Julia_InterstingSpots[i][0], Meta.Julia_InterstingSpots[i][1]) );
            res -= Math.exp( -200.0*distsq(x, Meta.Julia_InterstingSpots[i][0], -Meta.Julia_InterstingSpots[i][1]) );
        }
        res *= 0.0001;
        return res;
    }

}
