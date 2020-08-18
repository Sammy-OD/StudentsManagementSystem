package stdmansys.registrationform;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import stdmansys.Loader;

import java.net.URL;
import java.util.ResourceBundle;

public class RegistrationFormController implements Initializable {

    @FXML
    private ImageView backIcon;

    @FXML
    private void handleBackIconOnMouseClicked(){
        Stage stage = (Stage) backIcon.getScene().getWindow();
        Parent root = Loader.load("startpage/startpage.fxml");
        stage.getScene().setRoot(root);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}
