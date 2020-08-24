package stdmansys.loginpage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import stdmansys.Loader;
import stdmansys.UserProperty;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginPageController implements Initializable {

    @FXML
    private Button loginBtn;
    @FXML
    private TextField usernameTxtFld, passwordTxtFld;
    @FXML
    private Hyperlink signUpHyperlink;
    private static String username;

    @FXML
    private void handleOnAction(ActionEvent evt) {
        if(evt.getSource() == loginBtn){
            if(LoginProperty.verifyLoginCredentials(usernameTxtFld.getText(), passwordTxtFld.getText())){
                LoginProperty.setIsLoginCredentialsCorrect(false);
                username = usernameTxtFld.getText();
                UserProperty.setProperty(username);
                Stage stage = (Stage) loginBtn.getScene().getWindow();
                Parent root = Loader.load("startpage/startpage.fxml");
                stage.getScene().setRoot(root);
                stage.show();
            }else{

            }
        }

        if(evt.getSource() == signUpHyperlink){
            Stage stage = (Stage) signUpHyperlink.getScene().getWindow();
            Parent root = Loader.load("signupform/signupform.fxml");
            stage.getScene().setRoot(root);
            stage.show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}