package GL;

import org.lwjgl.BufferUtils;
import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Created by felix on 26.05.2016.
 */
public class Camera {

    private float scale, tx, ty, aspect;
    private float sx, sy;
    private int mi;


    public Camera()
    {
        reset();
        mi = 24;
        aspect = 1.0f;
        sx = 0.0f;
        sy = 1.0f;
    }

    public final void reset()
    {
        scale = 1.5f;
        tx = -0.0f;
        ty = 0.0f;
    }


    public void apply(Shader s)
    {
        glUniform1f(s.getScaleLocation(), scale);
        glUniform1f(s.getTranslXLocation(), tx);
        glUniform1f(s.getTranslYLocation(), ty);
        glUniform1i(s.getMaxIterLocation(), mi);
        glUniform1f(s.getAspectLocation(), aspect);
        glUniform1f(s.getSeedRealLocation(), sx);
        glUniform1f(s.getSeedImagLocation(), sy);
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
        System.out.println("Iterations: " + mi);
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
