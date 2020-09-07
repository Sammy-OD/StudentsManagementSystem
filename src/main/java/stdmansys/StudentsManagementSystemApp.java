package stdmansys;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class StudentsManagementSystemApp extends Application {

    private final double WIDTH = Screen.getPrimary().getBounds().getWidth() * 0.8;
    private final double HEIGHT = Screen.getPrimary().getBounds().getHeight() * 0.8;

    @Override
    public void start(Stage stage) {
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
        stage.show();
    }

    public static void main(String[] args) {
        System.setProperty("java.library.path", "./lib");
        SessionProperty.setSessionProperties();
        launch(args);
    }

}