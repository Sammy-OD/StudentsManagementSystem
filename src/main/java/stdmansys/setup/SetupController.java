package stdmansys.setup;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import stdmansys.Loader;
import stdmansys.constants.Path;
import stdmansys.StudentsManagementSystemApp;
import stdmansys.utils.PasswordUtil;
import stdmansys.utils.XMLUtil;
import stdmansys.validator.Validator;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;

public class SetupController implements Initializable {

    @FXML
    private Button backBtn, nextBtn, finishBtn, browseBtn;
    @FXML
    private TextField sessionTxtFld, adminNameTxtFld, schlNameTxtFld, logoTxtFld;
    @FXML
    private PasswordField passwordTxtFld, cPasswordTxtFld;
    @FXML
    private ComboBox termComboBox;
    @FXML
    private AnchorPane root;
    @FXML
    private VBox adminBox, schoolBox, periodBox;
    private Validator<Control> sessionValidator, adminValidator, schlValidator;

    @FXML
    private void handleOnAction(ActionEvent evt) {
        if(evt.getSource() == browseBtn){
            Stage stage  = new Stage();
            FileChooser chooser = new FileChooser();
            chooser.setInitialDirectory(new File(System.getProperty("user.home")));
            chooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpg"), new FileChooser.ExtensionFilter("PNG", "*.png"));
            File file = chooser.showOpenDialog(stage);
            if(file != null){
                stage.close();
                logoTxtFld.setText(file.getPath());
            }
        }
        if(evt.getSource() == nextBtn){
            if(adminBox.isVisible()){
                adminValidator = new Validator<>(root, 45, 75);
                adminValidator.registerEmptyValidation(adminNameTxtFld, "Field Required");
                if(adminValidator.validate()){
                    if(passwordTxtFld.getText().isEmpty() && passwordTxtFld.getText().isEmpty()){
                        adminBox.setVisible(false);
                        adminBox.setManaged(false);
                        schoolBox.setVisible(true);
                        schoolBox.setManaged(true);
                        backBtn.setDisable(false);
                        schoolBox.requestFocus();
                    }else{
                        if(passwordTxtFld.getText().contentEquals(cPasswordTxtFld.getText())){
                            adminBox.setVisible(false);
                            adminBox.setManaged(false);
                            schoolBox.setVisible(true);
                            schoolBox.setManaged(true);
                            backBtn.setDisable(false);
                            schoolBox.requestFocus();
                        }else{
                            Alert alert = new Alert(Alert.AlertType.NONE, "Password does not match", ButtonType.OK);
                            alert.showAndWait();
                        }
                    }
                }
            }else if(schoolBox.isVisible()){
                schlValidator = new Validator<>(root, 45, 75);
                schlValidator.registerEmptyValidation(schlNameTxtFld, "Field Required");
                if(schlValidator.validate()){
                    schoolBox.setVisible(false);
                    schoolBox.setManaged(false);
                    periodBox.setVisible(true);
                    periodBox.setManaged(true);
                    nextBtn.setDisable(true);
                    finishBtn.setDisable(false);
                    backBtn.setDisable(false);
                    periodBox.requestFocus();
                }
            }
        }
        if(evt.getSource() == backBtn){
            if(schoolBox.isVisible()){
                schoolBox.setVisible(false);
                schoolBox.setManaged(false);
                if(schlValidator != null){
                    schlValidator.removeLabel(schlNameTxtFld);
                }
                adminBox.setVisible(true);
                adminBox.setManaged(true);
                backBtn.setDisable(true);
                adminBox.requestFocus();
            }else if(periodBox.isVisible()){
                periodBox.setVisible(false);
                periodBox.setManaged(false);
                if(sessionValidator != null){
                    sessionValidator.removeLabel(sessionTxtFld);
                }
                schoolBox.setVisible(true);
                schoolBox.setManaged(true);
                nextBtn.setDisable(false);
                finishBtn.setDisable(true);
                schoolBox.requestFocus();
            }
        }
        if(evt.getSource() == finishBtn){
            sessionValidator = new Validator<>(root, 45, 75);
            sessionValidator.registerEmptyValidation(sessionTxtFld, "Field Required");
            sessionValidator.registerEmptyValidation(termComboBox, "Selection Required");
            sessionValidator.registerRegexValidation(sessionTxtFld, "Invalid Input", "2[0-9]{3}/2[0-9]{3}");
            if(sessionValidator.validate()){
                Document doc = XMLUtil.loadXML(Path.APP_XML.getPath());
                if(doc != null){
                    // Creates file admin to store admin password.
                    if(!passwordTxtFld.getText().isEmpty()){
                        try{
                            Cipher cipher = Cipher.getInstance("AES");
                            byte[] password = PasswordUtil.encryptPassword(passwordTxtFld.getText(), cipher);
                            File file = new File("doc/admin");
                            file.createNewFile();
                            if(password != null){
                                FileUtils.writeByteArrayToFile(file, password);
                            }
                        }catch(NoSuchPaddingException e) {
                            e.printStackTrace();
                        }catch(NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }catch(IOException e) {
                            e.printStackTrace();
                        }
                    }else{
                        try{
                            Cipher cipher = Cipher.getInstance("AES");
                            byte[] password = PasswordUtil.encryptPassword("admin", cipher);
                            File file = new File("doc/admin");
                            file.createNewFile();
                            if(password != null){
                                FileUtils.writeByteArrayToFile(file, password);
                            }
                        }catch(NoSuchPaddingException e) {
                            e.printStackTrace();
                        }catch(NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        }catch(IOException e) {
                            e.printStackTrace();
                        }
                    }

                    // Copies logo to image folder of the project.
                    File src = new File(logoTxtFld.getText());
                    File dest = new File("image/logo." + FilenameUtils.getExtension(src.getName()));
                    try{
                        FileUtils.copyFile(src, dest);
                    }catch(IOException e) {
                        System.out.println(e.getMessage());
                    }

                    // Writes to app.xml
                    NodeList app = doc.getDocumentElement().getChildNodes();
                    NodeList school = app.item(1).getChildNodes();
                    school.item(1).setTextContent(schlNameTxtFld.getText());
                    if(src.exists()){
                        school.item(3).setTextContent(dest.getPath());
                    }
                    NodeList state = school.item(5).getChildNodes();
                    state.item(1).setTextContent(sessionTxtFld.getText());
                    state.item(3).setTextContent(termComboBox.getValue().toString());
                    NodeList admin = app.item(3).getChildNodes();
                    admin.item(1).setTextContent(adminNameTxtFld.getText());
                    XMLUtil.updateXML(Path.APP_XML.getPath(), doc);

                    ((Stage)(finishBtn.getScene().getWindow())).close();

                    Stage stage = new Stage();
                    Parent root = Loader.load("loginpage/loginpage.fxml");
                    if(root != null){
                        stage.setScene(new Scene(root, StudentsManagementSystemApp.getWidth(), StudentsManagementSystemApp.getHeight()));
                    }
                    stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent windowEvent) {
                            System.exit(2);
                        }
                    });
                    stage.show();
                }
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        schoolBox.setVisible(false);
        schoolBox.setManaged(false);
        periodBox.setVisible(false);
        periodBox.setManaged(false);
        String[] term = {"1", "2", "3"};
        termComboBox.setItems(FXCollections.observableArrayList(term));
    }

}