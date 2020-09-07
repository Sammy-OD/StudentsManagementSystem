package stdmansys.loginpage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.controlsfx.control.ToggleSwitch;
import stdmansys.Loader;
import stdmansys.UserProperty;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginPageController implements Initializable {

    @FXML
    private Button loginBtn;
    @FXML
    private TextField idTxtFld, passwordTxtFld;
    @FXML
    private ToggleSwitch adminToggleSwitch;

    @FXML
    private void handleOnMouseClicked(MouseEvent evt) {
        if(evt.getSource() == adminToggleSwitch){
            if(adminToggleSwitch.isSelected()) {
                idTxtFld.setDisable(true);
                passwordTxtFld.requestFocus();
            }else{
                idTxtFld.setDisable(false);
                idTxtFld.requestFocus();
            }
        }
    }

    @FXML
    private void handleOnAction(ActionEvent evt) {
        if(evt.getSource() == loginBtn){
            if(adminToggleSwitch.isSelected()){
                if(LoginProperty.verifyAdminLogin(passwordTxtFld.getText())){
                    LoginProperty.setIsLoginParametersCorrect(false);
                    UserProperty.setIsAdmin(true);
                    Stage stage = (Stage) loginBtn.getScene().getWindow();
                    Parent root = Loader.load("startpage/startpage.fxml");
                    stage.getScene().setRoot(root);
                    stage.show();
                }
            }else{
                if(LoginProperty.verifyTeacherLogin(idTxtFld.getText(), passwordTxtFld.getText())){
                    LoginProperty.setIsLoginParametersCorrect(false);
                    String teacherId = idTxtFld.getText();
                    UserProperty.setProperty(teacherId);
                    Stage stage = (Stage) loginBtn.getScene().getWindow();
                    Parent root = Loader.load("startpage/startpage.fxml");
                    stage.getScene().setRoot(root);
                    stage.show();
                }
            }
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}