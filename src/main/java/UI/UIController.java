package UI;

import javafx.fxml.FXML;
import javafx.stage.Stage;

/**
 * Created by Felix Patzschke on 27.05.2016.
 */
public class UIController {

    private static GLContext glContext;
    private Stage stage;

    public void setStage(Stage stage)
    {
        this.stage = stage;
    }

    @FXML
    public void initialize(){
        glContext = new GLContext("newton2.frag");
        glContext.start();
    }

    public static void exit()
    {
        glContext.exit();
        Settings.exportConfig();
        System.exit(0);
    }

}
