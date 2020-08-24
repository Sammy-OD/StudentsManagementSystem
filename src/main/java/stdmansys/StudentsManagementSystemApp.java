package stdmansys;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class StudentsManagementSystemApp extends Application {

    private final double WIDTH = Screen.getPrimary().getBounds().getWidth() * 0.8;
    private final double HEIGHT = Screen.getPrimary().getBounds().getHeight() * 0.8;

    @Override
    public void start(Stage stage) {
        Parent root = Loader.load("loginpage/loginpage.fxml");
        stage.setScene(new Scene(root, WIDTH, HEIGHT));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}