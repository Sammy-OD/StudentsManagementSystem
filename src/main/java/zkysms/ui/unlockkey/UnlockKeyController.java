package zkysms.ui.unlockkey;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import zkysms.ui.Loader;
import zkysms.StudentsManagementSystemApp;
import zkysms.constants.Constants;
import zkysms.constants.Path;
import zkysms.utils.CryptoUtil;

import java.net.URL;
import java.util.ResourceBundle;

public class UnlockKeyController implements Initializable {

    @FXML
    private TextField kpasswordTxtFld, epasswordTxtFld;
    @FXML
    private Button unlockBtn;
    @FXML
    private HBox errorLblBox;

    @FXML
    private void handleOnKeyTyped(KeyEvent evt) {
        if(evt.getSource() == kpasswordTxtFld || evt.getSource() == epasswordTxtFld){
            if(!kpasswordTxtFld.getText().isEmpty() && !epasswordTxtFld.getText().isEmpty()){
                unlockBtn.setDisable(false);
            }else{
                if(!unlockBtn.isDisable()){
                    unlockBtn.setDisable(true);
                }
            }
        }
    }

    @FXML
    private void handleOnAction(ActionEvent evt) {
        if(evt.getSource() == unlockBtn){
            if(CryptoUtil.validateKeyStore(Path.KEYSTORE.getPath(), kpasswordTxtFld.getText()) &&  CryptoUtil.getSecretKey(Path.KEYSTORE.getPath(), kpasswordTxtFld.getText(), epasswordTxtFld.getText(), Constants.SECRET_KEY_ALIAS.getValue()) != null){
                StudentsManagementSystemApp.setKeyStorePassword(kpasswordTxtFld.getText());
                StudentsManagementSystemApp.setSecretKeyEntryPassword(epasswordTxtFld.getText());

                Stage stage = (Stage) unlockBtn.getScene().getWindow();
                Parent root = Loader.load(Path.LOGIN_PAGE.getPath());
                stage.getScene().setRoot(root);
                stage.show();
            }else{
                errorLblBox.setVisible(true);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        errorLblBox.setVisible(false);
        kpasswordTxtFld.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean focusLost, Boolean focusGained) {
                if(focusGained && errorLblBox.isVisible()){
                    errorLblBox.setVisible(false);
                }
            }
        });
        epasswordTxtFld.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean focusLost, Boolean focusGained) {
                if(focusGained && errorLblBox.isVisible()){
                    errorLblBox.setVisible(false);
                }
            }
        });
    }

}