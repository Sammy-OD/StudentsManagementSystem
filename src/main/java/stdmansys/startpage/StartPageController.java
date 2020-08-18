package stdmansys.startpage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import stdmansys.Loader;

import java.net.URL;
import java.util.ResourceBundle;

public class StartPageController implements Initializable {

    @FXML
    private Button regStdBtn, beginSessionBtn, beginTermBtn, rsltUpBtn, viewGrdShtBtn, viewStdRepBtn, searchBtn;
    @FXML
    private Label user;
    @FXML
    private ContextMenu contxtMenuUser;

    @FXML
    private void handleUserLabelOnMouseClick(){
        contxtMenuUser.show(user, Side.LEFT, 0, user.getHeight());
    }

    @FXML
    private void handleButtonOnAction(ActionEvent evt) {
        if(evt.getSource() == regStdBtn){
            Stage stage = (Stage) regStdBtn.getScene().getWindow();
            Parent root = Loader.load("registrationform/registrationform.fxml");
            stage.getScene().setRoot(root);
            stage.show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}
