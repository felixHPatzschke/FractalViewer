package UI;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.IntBuffer;
import GL.Camera;
import GL.Shader;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.*;

/**
 * Created by felix on 26.05.2016.
 */
public class GLContext extends Thread
{
    private long /** GLFWwindow* */ window;
    private Shader shader = null;
    private Camera camera = null;
    private int width, height;
    private int vao, vbo, ibo;
    private GLFWErrorCallback errorCallback = null;
    private GLFWKeyCallback keyCallback = null;
    private GLFWWindowPosCallback windowPosCallback = null;
    private GLFWWindowSizeCallback windowSizeCallback = null;
    private GLFWWindowCloseCallback windowCloseCallback = null;
    private boolean something_changed = true;



    public GLContext()
    {

    }


    protected void init()
    {
        glfwSetErrorCallback(errorCallback =GLFWErrorCallback.createPrint(System.err));

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( glfwInit()==GLFW_FALSE )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure our window
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        width = Settings.glfw_window_width;
        height = Settings.glfw_window_height;

        // Create the window
        window = glfwCreateWindow(width, height, Settings.title, NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        GL.createCapabilities();

        try{
            shader = new Shader("mandelbrot");
        } catch (Exception ex)
        {
            ex.printStackTrace(System.err);
        }
        camera = new Camera();



        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                something_changed = true;
                if(key == GLFW_KEY_LEFT && action == GLFW_RELEASE)
                    camera.translate(-1, 0);
                if(key == GLFW_KEY_RIGHT && action == GLFW_RELEASE)
                    camera.translate(1, 0);
                if(mods == GLFW_MOD_SHIFT)
                {
                    if(key == GLFW_KEY_UP && action == GLFW_RELEASE)
                        camera.zoom(-1);
                    if(key == GLFW_KEY_DOWN && action == GLFW_RELEASE)
                        camera.zoom(1);
                }else if(mods == GLFW_MOD_CONTROL)
                {
                    if(key == GLFW_KEY_UP && action == GLFW_RELEASE)
                    {
                        camera.increment_iterations(1);
                        System.out.println(camera.getIterations());
                    }
                    if(key == GLFW_KEY_DOWN && action == GLFW_RELEASE)
                    {
                        camera.increment_iterations(-1);
                        System.out.println(camera.getIterations());
                    }
                }else
                {
                    if(key == GLFW_KEY_UP && action == GLFW_RELEASE)
                        camera.translate(0, 1);
                    if(key == GLFW_KEY_DOWN && action == GLFW_RELEASE)
                        camera.translate(0, -1);
                }
                if(key == GLFW_KEY_SPACE && action == GLFW_RELEASE)
                    camera.reset();
            }
        });
        glfwSetWindowSizeCallback(window, windowSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int x, int y) {
                something_changed = true;
                width = x;
                height = y;
                Settings.glfw_window_width = x;
                Settings.glfw_window_height = y;
                glViewport(0, 0, width, height);
                camera.setAspectRatio(width, height);
            }
        });
        glfwSetWindowPosCallback(window, windowPosCallback = new GLFWWindowPosCallback() {
            @Override
            public void invoke(long window, int xpos, int ypos) {
                something_changed = true;
                Settings.glfw_window_x = xpos;
                Settings.glfw_window_y = ypos;
            }
        });
        glfwSetWindowCloseCallback(window, windowCloseCallback = new GLFWWindowCloseCallback() {
            @Override
            public void invoke(long window) {
                UIController.exit();
            }
        });

        // Get the resolution of the primary monitor
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        // Center our window
        glfwSetWindowPos(window, Settings.glfw_window_x, Settings.glfw_window_y);

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        IntBuffer vertices = BufferUtils.createIntBuffer(8);
        vertices.put(new int[] {-1, -1, 1, -1, 1, 1, -1, 1});
        vertices.flip();

        vao = glGenVertexArrays();
        glBindVertexArray(vao);
        vbo = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vbo);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
        glVertexAttribPointer(0, 2, GL_INT, false, 0, 0);
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

    }

    protected void loop()
    {

        glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        glColor3f(1.0f, 0.0f, 0.0f);
        //glEnable(GL_BLEND);
        //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);


        while(glfwWindowShouldClose(window)==GLFW_FALSE)
        {
            if(something_changed)
            {
                glClear(GL_COLOR_BUFFER_BIT); // clear the framebuffer
                shader.bind();
                camera.apply(shader);

                /*
                glBegin(GL_QUADS);
                glVertex2f(-1, -1);
                glVertex2f(1, -1);
                glVertex2f(1, 1);
                glVertex2f(-1, 1);
                glEnd();
                /*
                /**/
                glEnableVertexAttribArray(0);
                glBindBuffer(GL_ARRAY_BUFFER, vbo);
                glVertexAttribPointer(0, 2, GL_INT, false, 0, 0);
                glDrawArrays(GL_QUADS, 0, 8);
                glDisableVertexAttribArray(0);
                /**/
                shader.unbind();

                glfwSwapBuffers(window);
                something_changed = false;
            }
            glfwPollEvents();
        }

    }

    public void run()
    {
        try {
            init();
            loop();

            // Free the window callbacks and destroy the window
            glfwReleaseCallbacks(window);
            glfwDestroyWindow(window);
        } finally {
            cleanup();
        }
    }

    public void exit()
    {
        glfwSetWindowShouldClose(window, GLFW_TRUE);
    }

    public void cleanup()
    {
        // Terminate GLFW and free the error callback
        if(shader!=null)
        {
            shader.unbind();
            shader.destroy();
        }

        if (keyCallback != null)
            keyCallback.release();
        if (errorCallback != null)
            errorCallback.release();
        if (windowPosCallback != null)
            windowPosCallback.release();
//        if (scrollCallback != null)
//            scrollCallback.release();
//        if (windowFocusCallback != null)
//            windowFocusCallback.release();
        if (windowSizeCallback != null)
            windowSizeCallback.release();
        glfwDestroyWindow(window);
        glfwTerminate();
    }

    public static void checkError()
    {
        int error=glGetError();
        if(error!=GL_NO_ERROR){
            Exception ex = new Exception(String.valueOf(error));
            ex.printStackTrace(System.err);
        }
    }

}
