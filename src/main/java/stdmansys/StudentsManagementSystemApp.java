package stdmansys;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.File;

public class StudentsManagementSystemApp extends Application {

    private static final double WIDTH = Screen.getPrimary().getBounds().getWidth() * 0.8;
    private static final double HEIGHT = Screen.getPrimary().getBounds().getHeight() * 0.8;

    @Override
    public void start(Stage stage) {
        File file = new File("doc/admin");
        if(file.exists()){
            Parent root = Loader.load("loginpage/loginpage.fxml");
            if(root != null){
                stage.setScene(new Scene(root, WIDTH, HEIGHT));
            }
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent windowEvent) {
                    System.exit(2);
                }
            });
        }else{
            Parent root = Loader.load("setup/setup.fxml");
            if(root != null){
                stage.setScene(new Scene(root));
            }
            stage.setResizable(false);
        }
        stage.show();
    }

    public static void main(String[] args) {
        System.setProperty("java.library.path", "./lib");
        launch(args);
    }

    public static double getWidth() {
        return WIDTH;
    }

    public static double getHeight() {
        return HEIGHT;
    }

}