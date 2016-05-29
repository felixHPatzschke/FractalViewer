package UI;

import org.json.JSONObject;

import java.io.*;

/**
 * Created by felix on 27.05.2016.
 */
public abstract class Settings {

    public static int glfw_window_width, glfw_window_height, glfw_window_x, glfw_window_y;
    public static int jfx_width, jfx_height, jfx_x, jfx_y;
    public static String shader_path, natives_path;
    public static final String title = "FractalViewer v0.2";


    private static void importConfig() throws FileNotFoundException, IOException
    {
        BufferedReader br = new BufferedReader(new FileReader("./settings.json"));

        StringBuilder sb = new StringBuilder();
        String line;
        while((line=br.readLine())!=null)
        {
            sb.append(line);
            sb.append(System.lineSeparator());
        }

        JSONObject o = new JSONObject(sb.toString());

        JSONObject glfw_window = o.getJSONObject("glfw_window");
        glfw_window_width = glfw_window.getInt("width");
        glfw_window_height = glfw_window.getInt("height");
        glfw_window_x = glfw_window.getInt("x");
        glfw_window_y = glfw_window.getInt("y");

        JSONObject jfx = o.getJSONObject("jfx_stage");
        jfx_width = jfx.getInt("width");
        jfx_height = jfx.getInt("height");
        jfx_x = jfx.getInt("x");
        jfx_y = jfx.getInt("y");

        shader_path = o.getString("shader_path");
        natives_path = o.getString("natives_path");
    }

    public static void exportConfig()
    {
        JSONObject res = new JSONObject();
        JSONObject glfw = new JSONObject();
        glfw.put("x", glfw_window_x);
        glfw.put("y", glfw_window_y);
        glfw.put("width", glfw_window_width);
        glfw.put("height", glfw_window_height);
        JSONObject jfx = new JSONObject();
        jfx.put("width", jfx_width);
        jfx.put("height", jfx_height);
        jfx.put("x", jfx_x);
        jfx.put("y", jfx_y);
        res.put("glfw_window", glfw);
        res.put("jfx_stage", jfx);
        res.put("shader_path", shader_path);
        res.put("natives_path", natives_path);

        try(FileWriter fw = new FileWriter("./settings.json"))
        {
            fw.write(res.toString(4));
            fw.flush();
            fw.close();
        }catch (IOException ex)
        {
            System.err.println("could not write settings to file:");
            ex.printStackTrace(System.err);
        }
    }

    private static void createDefault()
    {
        glfw_window_width = 600;
        glfw_window_height = 600;
        glfw_window_x = 400;
        glfw_window_y = 200;

        jfx_width = 250;
        jfx_height = 400;
        jfx_x = 20;
        jfx_y = 20;

        shader_path = "src/main/resources/shaders";
        natives_path = "native";

        exportConfig();
    }

    public static void init()
    {
        try
        {
            importConfig();
        }catch(IOException ioex)
        {
            createDefault();
        }
    }

}
