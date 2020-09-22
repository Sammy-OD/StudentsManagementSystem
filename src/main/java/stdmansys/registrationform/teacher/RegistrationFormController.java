package stdmansys.registrationform.teacher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.w3c.dom.Document;
import stdmansys.Loader;
import stdmansys.SessionProperty;
import stdmansys.camera.Camera;
import stdmansys.utils.XMLUtil;
import stdmansys.validator.Validator;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.UnaryOperator;

public class RegistrationFormController implements Initializable {

    @FXML
    private ImageView backIcon;
    @FXML
    private TextField firstNameTxtFld, lastNameTxtFld, midNameTxtFld, phnNumTxtFld, religionTxtFld,
                        nationTxtFld, addressTxtFld, emailTxtFld;
    @FXML
    private Button submitBtn, takePicBtn;
    @FXML
    private DatePicker datePicker;
    @FXML
    private CheckBox mrChkBox, mrsChkBox, missChkBox;
    @FXML
    private AnchorPane formNode;
    private RegistrationForm form;
    private Validator<Control> validator;

    @FXML
    private void handleOnMouseClicked(MouseEvent evt) {
        if(evt.getSource() == backIcon){
            Stage stage = (Stage) backIcon.getScene().getWindow();
            Parent root = Loader.load("startpage/startpage.fxml");
            stage.getScene().setRoot(root);
            stage.show();
        }
    }

    @FXML
    private void handleOnAction(ActionEvent evt) {
        if(evt.getSource() == mrChkBox){
            if(mrChkBox.isSelected()){
                mrsChkBox.setDisable(true);
                missChkBox.setDisable(true);
            }else{
                mrsChkBox.setDisable(false);
                missChkBox.setDisable(false);
            }
        }

        if(evt.getSource() == mrsChkBox){
            if(mrsChkBox.isSelected()){
                mrChkBox.setDisable(true);
                missChkBox.setDisable(true);
            }else{
                mrChkBox.setDisable(false);
                missChkBox.setDisable(false);
            }
        }

        if(evt.getSource() == missChkBox){
            if(missChkBox.isSelected()){
                mrChkBox.setDisable(true);
                mrsChkBox.setDisable(true);
            }else{
                mrChkBox.setDisable(false);
                mrsChkBox.setDisable(false);
            }
        }

        if(evt.getSource() == takePicBtn){
            RegistrationForm.setRootNode(takePicBtn.getScene().getRoot());
            Camera.initWebcam();
            Camera.launch();
            Camera.setCameraCaller(this.getClass().getCanonicalName());
        }

        if(evt.getSource() == submitBtn){
            if(validator.validate()){
                getFormInputs();
                if(form.submitForm()){
                    SessionProperty.setCurrentNumberOfTeachersRegisteredThisSession(SessionProperty.getCurrentNumberOfTeachersRegisteredThisSession() + 1);
                    Document doc = XMLUtil.loadXML("state");
                    if (doc != null) {
                        doc.getElementsByTagName("teacher").item(0).setTextContent(Integer.toString(SessionProperty.getCurrentNumberOfTeachersRegisteredThisSession()));
                        XMLUtil.updateXML("state", doc);
                    }
                    Stage stage = (Stage) submitBtn.getScene().getWindow();
                    Parent root = Loader.load("registrationform/teacher/registrationform.fxml");
                    stage.getScene().setRoot(root);
                    stage.show();
                    Alert alert = new Alert(Alert.AlertType.NONE, "Submit Successful", ButtonType.OK);
                    alert.showAndWait();
                }else{
                    form = new RegistrationForm();
                }
            }
        }
    }

    private void getFormInputs(){
        // Gets first name.
        if(!firstNameTxtFld.getText().isEmpty()) form.setFirstName(firstNameTxtFld.getText());
        // Gets last name.
        if(!lastNameTxtFld.getText().isEmpty()) form.setLastName(lastNameTxtFld.getText());
        // Gets middle name.
        if(!midNameTxtFld.getText().isEmpty()) form.setMiddleName(midNameTxtFld.getText());
        // Gets address.
        if(!addressTxtFld.getText().isEmpty()) form.setAddress(addressTxtFld.getText());
        // Gets nationality.
        if(!nationTxtFld.getText().isEmpty()) form.setNationality(nationTxtFld.getText());
        // Gets email.
        if(!emailTxtFld.getText().isEmpty()) form.setEmail(emailTxtFld.getText());
        // Gets phone number.
        if(!phnNumTxtFld.getText().isEmpty()) form.setPhoneNumber(phnNumTxtFld.getText());
        // Gets religion.
        if(!religionTxtFld.getText().isEmpty()) form.setReligion(religionTxtFld.getText());
        // Gets date of birth.
        try{
            form.setDOB(datePicker.getValue().toString());
        }catch (Exception ignored){}
        // Gets title.
        if(mrChkBox.isSelected()){
            form.setTitle(mrChkBox.getText());
        }else if(mrsChkBox.isSelected()){
            form.setTitle(mrsChkBox.getText());
        }else if(missChkBox.isSelected()){
            form.setTitle(missChkBox.getText());
        }
        // Gets image name.
        if(Camera.getImageName() != null) form.setImageName(Camera.getImageName());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        datePicker.setConverter(new StringConverter<LocalDate>() {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            @Override
            public String toString(LocalDate localDate) {
                if(localDate != null){
                    return dateFormatter.format(localDate);
                }else{
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String s) {
                if(s != null && !s.isEmpty()){
                    return LocalDate.parse(s, dateFormatter);
                }else{
                    return null;
                }
            }
        });
        form = new RegistrationForm();
        validator = new Validator<>(formNode, -70, 35);
        validator.registerEmptyValidation(firstNameTxtFld, "Field Required");
        validator.registerEmptyValidation(lastNameTxtFld, "Field Required");
        validator.registerEmptyValidation(midNameTxtFld, "Field Required");
        validator.registerEmptyValidation(datePicker, "Field Required");
        validator.registerEmptyValidation(phnNumTxtFld, "Field Required");
        validator.registerEmptyValidation(emailTxtFld, "Field Required");
        validator.registerEmptyValidation(addressTxtFld, "Field Required");
        validator.registerRegexValidation(datePicker, "Invalid Date Format", "[0-9]{2}-[0-9]{2}-[0-9]{4}");
        validator.registerRegexValidation(phnNumTxtFld, "Invalid Phone Number", "^0([789][0]|[8][1])[0-9]{8}");
        validator.registerRegexValidation(emailTxtFld, "Invalid Email Address", "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}");
    }

}