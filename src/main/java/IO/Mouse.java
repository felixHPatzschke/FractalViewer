package IO;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Created by Felix on 20.02.2017.
 */
public class Mouse {

    private class MouseState
    {
        protected boolean l, r, m;
        protected double x, y;

        public MouseState()
        {
            l = false;
            r = false;
            m = false;
            x = 0;
            y = 0;
        }
    }


    MouseState current, last;


    public Mouse()
    {
        current = new MouseState();
        last = new MouseState();
    }


    public void button(int button, int action)
    {
        boolean newVal = (action==GLFW_PRESS)?(true):(false);
        switch(button)
        {
            case GLFW_MOUSE_BUTTON_LEFT:
                current.l = newVal;
                break;
            case GLFW_MOUSE_BUTTON_RIGHT:
                current.r = newVal;
                break;
            case GLFW_MOUSE_BUTTON_MIDDLE:
                current.m = newVal;
                break;
            default:
                break;
        }
    }

    public void position(double xpos, double ypos)
    {
        current.x = xpos;
        current.y = ypos;
    }

}
