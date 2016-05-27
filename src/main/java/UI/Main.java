package UI;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;


public class Main extends Application{

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                UIController.exit();
            }
        });

        primaryStage.setWidth(Settings.jfx_width);
        primaryStage.setHeight(Settings.jfx_height);
        primaryStage.setX(Settings.jfx_x);
        primaryStage.setY(Settings.jfx_y);

        primaryStage.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Settings.jfx_width = newValue.intValue();
            }
        });
        primaryStage.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Settings.jfx_height = newValue.intValue();
            }
        });
        primaryStage.xProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Settings.jfx_x = newValue.intValue();
            }
        });
        primaryStage.yProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                Settings.jfx_y = newValue.intValue();
            }
        });

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/JavaFX/jfx_stage.fxml"));
        try
        {
            Parent p = loader.load();
            UIController controller = loader.getController();
            primaryStage.setTitle(Settings.title);
            primaryStage.setScene(new Scene(p));
            controller.setStage(primaryStage);
            primaryStage.show();
        }catch (IOException ioex)
        {
            ioex.printStackTrace(System.err);
            UIController.exit();
        }catch (Exception ex)
        {
            ex.printStackTrace();
            UIController.exit();
        }
    }

    public static void setLibNatives()
    {
        System.setProperty("org.lwjgl.librarypath", Settings.natives_path);
    }

    public static void main(String[] args) {
        Settings.init();
        setLibNatives();
        launch();
    }
}
