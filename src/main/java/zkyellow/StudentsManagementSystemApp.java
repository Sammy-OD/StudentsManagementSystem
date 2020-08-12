package zkyellow;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

public class StudentsManagementSystemApp extends Application {

    private final double WIDTH = Screen.getPrimary().getBounds().getWidth() - 80;
    private final double HEIGHT = Screen.getPrimary().getBounds().getHeight() - 80;

    @Override
    public void start(Stage stage){
        try {
            Parent root = FXMLLoader.load(getClass().getResource("startpage.fxml"));
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
