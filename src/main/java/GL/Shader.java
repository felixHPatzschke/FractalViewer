package GL;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.ARBShaderObjects.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import UI.*;

/**
 * Created by felix on 26.05.2016.
 */
public class Shader {

    private int vertex_shader, fragment_shader;
    private int shader_program;
    private int tx, ty, scale, max_i, aspect;

    public Shader(String name)
    {
        vertex_shader = glCreateShader(GL_VERTEX_SHADER);
        Charset charset = Charset.forName("UTF-8");
        //Charset charset = Charset.defaultCharset();
        glShaderSource(vertex_shader, readFile(Settings.shader_path + "\\main.vert", charset));
        glCompileShader(vertex_shader);

        fragment_shader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragment_shader, readFile(Settings.shader_path + "\\" + name + ".frag", charset));
        glCompileShader(fragment_shader);

        int vert_status = glGetShaderi(vertex_shader, GL_COMPILE_STATUS);
        if(vert_status!=GL_TRUE)
            throw new RuntimeException("In vertex shader:\n" + glGetShaderInfoLog(vertex_shader));
        int frag_status = glGetShaderi(fragment_shader, GL_COMPILE_STATUS);
        if(frag_status!=GL_TRUE)
            throw new RuntimeException("In fragment shader:\n" + glGetShaderInfoLog(fragment_shader));

        shader_program = glCreateProgram();
        glAttachShader(shader_program, vertex_shader);
        glAttachShader(shader_program, fragment_shader);
        glBindAttribLocation(shader_program, 0, "vert_pos");
        glLinkProgram(shader_program);
        bind();
        tx = glGetUniformLocation(shader_program, "translx");
        ty = glGetUniformLocation(shader_program, "transly");
        scale = glGetUniformLocation(shader_program, "scale");
        max_i = glGetUniformLocation(shader_program, "max_iter");
        aspect = glGetUniformLocation(shader_program, "aspect");
        unbind();
    }

    private static String readFile(String path, Charset encoding)
    {
        try
        {
            byte[] encoded = Files.readAllBytes(Paths.get(path));
            return new String(encoded, encoding);
        }catch(IOException ioex)
        {
            System.err.println("IOException: ");
            ioex.printStackTrace(System.err);
            // TODO: Let the user choose a shader location/file
            return "";
        }
    }

    public void bind()
    {
        glUseProgram(shader_program);
    }

    public void unbind()
    {
        glUseProgram(0);
    }

    public void destroy()
    {
        glDeleteShader(vertex_shader);
        glDeleteShader(fragment_shader);
        glDeleteProgram(shader_program);
    }

    public int getTranslXLocation()
    {
        return tx;
    }

    public int getTranslYLocation()
    {
        return ty;
    }

    public int getScaleLocation()
    {
        return scale;
    }

    public int getMaxIterLocation()
    {
        return max_i;
    }

    public int getAspectLocation()
    {
        return aspect;
    }

}
