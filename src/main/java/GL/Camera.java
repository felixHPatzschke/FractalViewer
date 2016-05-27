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
    private int mi;


    public Camera()
    {
        reset();
        mi = 24;
        aspect = 1.0f;
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
    }

    public void translate(int x, int y)
    {
        tx += x*scale*0.1;
        ty += y*scale*0.1;
    }

    public void setAspectRatio(int width, int height)
    {
        aspect = (float)width/(float)(height);
    }

    public void zoom(int i)
    {
        scale *= 1.0+(0.1*i);
    }

    public void increment_iterations(int i)
    {
        if(mi+i>0) mi += i;
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

}
