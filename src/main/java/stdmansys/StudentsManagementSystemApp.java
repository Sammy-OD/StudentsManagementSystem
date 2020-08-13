package stdmansys;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class StudentsManagementSystemApp extends Application {

    private final double WIDTH = Screen.getPrimary().getBounds().getWidth() * 0.8;
    private final double HEIGHT = Screen.getPrimary().getBounds().getHeight() * 0.8;

    @Override
    public void start(Stage stage){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("homepage/startpage.fxml"));
            stage.setScene(new Scene(root, WIDTH, HEIGHT));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        launch(args);
    }

}
