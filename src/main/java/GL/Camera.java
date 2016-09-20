package GL;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created by felix on 26.05.2016.
 */
public class Camera {

    private double scale, tx, ty, aspect;
    private double sx, sy;
    private int mi;
    private int option_enum;


    public Camera()
    {
        reset();
        resetSeed();
        mi = 24;
        aspect = 1.0f;
        option_enum = 4;
        this.zero_shift();
    }

    public void zero_shift()
    {
        this.shift_option(0);
        this.shift_seed(0,0);
        this.increment_iterations(0);
        this.translate(0,0);
        this.zoom(0);
    }

    public final void reset()
    {
        scale = 1.5f;
        tx = -0.0f;
        ty = 0.0f;
    }

    public final void resetSeed()
    {
        sx = 0.0f;
        sy = 1.0f;
    }


    public void apply(Shader s)
    {
        glUniform1f(s.getScaleLocation(), (float)scale);
        glUniform1f(s.getTranslXLocation(), (float)tx);
        glUniform1f(s.getTranslYLocation(), (float)ty);
        glUniform1i(s.getMaxIterLocation(), (int)mi);
        glUniform1f(s.getAspectLocation(), (float)aspect);
        glUniform1f(s.getSeedRealLocation(), (float)sx);
        glUniform1f(s.getSeedImagLocation(), (float)sy);
        glUniform1i(s.getOptionEnumLocation(), (int)option_enum);
    }

    public void translate(float x, float y)
    {
        tx += x*scale*0.1;
        ty += y*scale*0.1;
        System.out.println("Translation: " + tx + " | " + ty);
    }

    public void setAspectRatio(int width, int height)
    {
        aspect = (float)width/(float)(height);
    }

    public void zoom(float i)
    {
        scale *= 1.0+(0.1*i);
        System.out.println("Scale: " + scale);
    }

    public void increment_iterations(int i)
    {
        if(mi+i>0) mi += i;
        System.out.println("Iterations: " + (mi-1));
    }

    public void shift_option(int i)
    {
        if(option_enum+i>=0) option_enum+=i;
        System.out.println("Option: " + option_enum);
        System.out.println("\tMandelbrot Set: z := z^" + (option_enum-2) + " + c");
        System.out.println("\tNewton Fractal:"
                + "\n\t\t" + (((option_enum & 1) == 0)?("Mapping Position"):("Mapping Value"))
                + "\n\t\t" + (((option_enum & 2) == 0)?("Colour by Complex Value"):("Colour by Iteration"))
                + (((option_enum & 4) == 0)?("\n\t\tInverting Colour"):(""))
        );
    }

    public int getIterations()
    {
        return mi;
    }

    public void setIterations(int i)
    {
        if(i>0)
            mi = i;
    }

    public void shift_seed(float x, float y)
    {
        sx += x;
        sy += y;
        System.out.println("Seed: " + sx + " | " + sy);
    }

}
