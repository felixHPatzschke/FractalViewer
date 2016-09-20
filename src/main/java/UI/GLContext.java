package UI;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;

import java.nio.IntBuffer;
import GL.Camera;
import GL.Shader;
import GL.RenderedTexture;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
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
    private String shaderName = null;
    //private Shader quadShader = null;
    //private RenderedTexture renderedTex = null;
    private Camera camera = null;
    private int vao, vbo, ibo;
    private GLFWErrorCallback errorCallback = null;
    private GLFWKeyCallback keyCallback = null;
    private GLFWWindowPosCallback windowPosCallback = null;
    private GLFWWindowSizeCallback windowSizeCallback = null;
    private GLFWWindowCloseCallback windowCloseCallback = null;
    private boolean something_changed = true;



    public GLContext()
    {
        shaderName = "mandelbrot.frag";
    }

    public GLContext(String shaderName)
    {
        this.shaderName = shaderName;
    }


    public void makeCurrent()
    {
        glfwMakeContextCurrent(window);
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

        // Create the window
        window = glfwCreateWindow(Settings.glfw_window_width, Settings.glfw_window_height, Settings.title, NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");
        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        GL.createCapabilities();

        try{
            shader = new Shader(shaderName);
            //quadShader = new Shader("texquad.frag");
        } catch (Exception ex)
        {
            ex.printStackTrace(System.err);
        }
        //renderedTex = new RenderedTexture(Settings.glfw_window_width/2, Settings.glfw_window_height/2);
        camera = new Camera();


        // <editor-fold desc="set up callbacks">.
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                something_changed = true;
                float multiplier = 1.0f;
                if((mods & GLFW_MOD_SHIFT) != 0)
                {
                    multiplier = 0.1f;
                }
                if((mods & GLFW_MOD_CONTROL) != 0)
                {
                    if(key == GLFW_KEY_UP && action == GLFW_PRESS)
                        camera.zoom(-multiplier);
                    if(key == GLFW_KEY_DOWN && action == GLFW_PRESS)
                        camera.zoom(multiplier);
                    if(key == GLFW_KEY_B && action == GLFW_RELEASE)
                        Settings.benchmark = true;
                }
                if((mods & GLFW_MOD_ALT) != 0)
                {
                    if(key == GLFW_KEY_LEFT && action == GLFW_PRESS)
                        camera.shift_seed(multiplier*-0.01f, 0.0f);
                    if(key == GLFW_KEY_RIGHT && action == GLFW_PRESS)
                        camera.shift_seed(multiplier*0.01f, 0.0f);
                    if(key == GLFW_KEY_UP && action == GLFW_PRESS)
                        camera.shift_seed(0.0f, multiplier*0.01f);
                    if(key == GLFW_KEY_DOWN && action == GLFW_PRESS)
                        camera.shift_seed(0.0f, multiplier*-0.01f);
                }
                if((mods | GLFW_MOD_SHIFT) == GLFW_MOD_SHIFT)
                {
                    if(key == GLFW_KEY_LEFT && action == GLFW_PRESS)
                        camera.translate(-multiplier, 0);
                    if(key == GLFW_KEY_RIGHT && action == GLFW_PRESS)
                        camera.translate(multiplier, 0);
                    if(key == GLFW_KEY_UP && action == GLFW_PRESS)
                        camera.translate(0, multiplier);
                    if(key == GLFW_KEY_DOWN && action == GLFW_PRESS)
                        camera.translate(0, -multiplier);
                }
                if(key == GLFW_KEY_SPACE && action == GLFW_RELEASE)
                {
                    if(mods == GLFW_MOD_ALT)
                        camera.resetSeed();
                    else
                        camera.reset();
                }
                if(key == GLFW_KEY_W && action == GLFW_PRESS)
                    if((mods & GLFW_MOD_ALT) != 0)
                        camera.shift_option(1);
                    else
                        camera.increment_iterations((int)(1/multiplier));
                if(key == GLFW_KEY_Q && action == GLFW_PRESS)
                    if((mods & GLFW_MOD_ALT) != 0)
                        camera.shift_option(-1);
                    else
                        camera.increment_iterations((int)(-1/multiplier));
            }
        });
        glfwSetWindowSizeCallback(window, windowSizeCallback = new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int x, int y) {
                something_changed = true;
                Settings.glfw_window_width = x;
                Settings.glfw_window_height = y;
                glViewport(0, 0, Settings.glfw_window_width, Settings.glfw_window_height);
                camera.setAspectRatio(Settings.glfw_window_width, Settings.glfw_window_height);
            }
        });
        glfwSetWindowPosCallback(window, windowPosCallback = new GLFWWindowPosCallback() {
            @Override
            public void invoke(long window, int xpos, int ypos) {
                //something_changed = true;
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

        glfwSetWindowPos(window, Settings.glfw_window_x, Settings.glfw_window_y);
        // </editor-fold>

        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);

        // Create a Vertex Buffer Objectand Vertex Array Object
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
        glEnable(GL_TEXTURE_2D);
        //glEnable(GL_BLEND);
        //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        double frametime = 0.0;

        while(glfwWindowShouldClose(window)==GLFW_FALSE)
        {
            if(something_changed)
            {
                if(Settings.benchmark)
                {
                    frametime = glfwGetTime();
                }
                //renderedTex.bind();
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
                //renderedTex.unbind(Settings.glfw_window_width, Settings.glfw_window_height);

                /*
                quadShader.bind();
                //glUniform1i(glGetUniformLocation(quadShader.getProgramID(), "tex"), renderedTex.textureID);
                glActiveTexture(GL_TEXTURE0);
                glBindTexture(GL_TEXTURE_2D, renderedTex.textureID);

                glEnableVertexAttribArray(0);
                glBindBuffer(GL_ARRAY_BUFFER, vbo);
                glVertexAttribPointer(0, 2, GL_INT, false, 0, 0);
                glDrawArrays(GL_QUADS, 0, 8);
                glDisableVertexAttribArray(0);

                quadShader.unbind();
                */

                glfwSwapBuffers(window);
                something_changed = false;

                if(Settings.benchmark)
                {
                    frametime = glfwGetTime()-frametime;
                    System.out.println(
                            "Drawing " + shader.getPath() + ":\n" +
                            "Framebuffer: " + Settings.glfw_window_width + "x" + Settings.glfw_window_height + "\n" +
                            //"Area: [" +  + "]x[" + Settings.glfw_window_height + "]\n" +
                            camera.getIterations() + " Iterations\n" +
                            "Frametime: " + 1000.0*frametime + "ms / Framerate: " + 1.0/frametime + "FPS"
                    );
                    Settings.benchmark = false;
                }
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
