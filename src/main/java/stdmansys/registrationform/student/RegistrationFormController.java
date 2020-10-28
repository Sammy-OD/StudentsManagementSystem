package stdmansys.registrationform.student;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.controlsfx.control.CheckComboBox;
import org.w3c.dom.Document;
import stdmansys.Loader;
import stdmansys.constants.Path;
import stdmansys.constants.XMLConstants;
import stdmansys.camera.Camera;
import stdmansys.constants.SessionConstants;
import stdmansys.property.SessionProperty;
import stdmansys.utils.XMLUtil;
import stdmansys.validator.Validator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RegistrationFormController implements Initializable {

    @FXML
    private ImageView backIcon;
    @FXML
    private ComboBox departmentComboBox, classComboBox;
    @FXML
    private CheckBox maleChkBox, femaleChkBox;
    @FXML
    private Button takePicBtn, submitBtn;
    @FXML
    private TextField firstNameTxtFld, lastNameTxtFld, midNameTxtFld, religionTxtFld, nationTxtFld, addressTxtFld, previousSchlTxtFld, fatherNameTxtFld, motherNameTxtFld,
                        fOccupationTxtFld, mOccupationTxtFld, fPhnTxtFld, mPhnTxtFld, fEmailTxtFld, mEmailTxtFld;
    @FXML
    private DatePicker datePicker;
    @FXML
    private CheckComboBox<String> subjectChkComboBox;
    @FXML
    private AnchorPane formNode;
    private RegistrationForm form;
    private Validator<Control> validator;
    private List<String> check;
    @FXML
    private ScrollPane studRoot;

    @FXML
    private void handleOnMouseClicked(MouseEvent evt) {
        if(evt.getSource() == backIcon){
            Stage stage = (Stage) backIcon.getScene().getWindow();
            Parent root = Loader.load(Path.HOME_PAGE.getPath());
            stage.getScene().setRoot(root);
            stage.show();
        }
    }

    @FXML
    private void handleOnAction(ActionEvent evt) {
        if(evt.getSource() == classComboBox){
            String selectedItem = (String) classComboBox.getValue();
            if(selectedItem.contentEquals("SS 1") || selectedItem.contentEquals("SS 2") || selectedItem.contentEquals("SS 3")){
                // Initializes department combo box.
                String[] department = {"Science", "Social Science", "Arts"};
                departmentComboBox.setItems(FXCollections.observableArrayList(department));

                validator.registerEmptyValidation(departmentComboBox, "Selection Required");
                validator.registerEmptyValidation(subjectChkComboBox, "Selection Required");
            }else{
                // Removes all items from department combo box.
                departmentComboBox.getItems().clear();
                // Removes all items from subject combo box.
                if(subjectChkComboBox.getItems() != null){
                    subjectChkComboBox.getItems().clear();
                }
                validator.removeValidation(departmentComboBox);
                validator.removeValidation(subjectChkComboBox);
            }
        }

        if(evt.getSource() == departmentComboBox){
            if(subjectChkComboBox.getItems() != null){
                subjectChkComboBox.getItems().clear();
            }
            ObservableList<String> list = FXCollections.observableArrayList();
            list.addAll("Mathematics", "English");
            Document doc = XMLUtil.loadXML(Path.APP_XML.getPath());
            if(departmentComboBox.getValue() != null){
                if(departmentComboBox.getValue().toString().equals("Science")){
                    if(doc != null){
                        String[] s = doc.getElementsByTagName(XMLConstants.SCIENCE_SUBJECTS.getTag().getTagName()).item(XMLConstants.SCIENCE_SUBJECTS.getTag().getIndex()).getTextContent().split(",");
                        for(int i = 0; i < s.length; i++){
                            list.add(s[i]);
                        }
                    }
                }else if(departmentComboBox.getValue().toString().equals("Social Science")){
                    if(doc != null){
                        String[] s = doc.getElementsByTagName(XMLConstants.SOCIAL_SUBJECTS.getTag().getTagName()).item(XMLConstants.SOCIAL_SUBJECTS.getTag().getIndex()).getTextContent().split(",");
                        for(int i = 0; i < s.length; i++){
                            list.add(s[i]);
                        }
                    }
                }else if(departmentComboBox.getValue().toString().equals("Arts")){
                    if(doc != null){
                        String[] s = doc.getElementsByTagName(XMLConstants.ARTS_SUBJECTS.getTag().getTagName()).item(XMLConstants.ARTS_SUBJECTS.getTag().getIndex()).getTextContent().split(",");
                        for(int i = 0; i < s.length; i++){
                            list.add(s[i]);
                        }
                    }
                }
                subjectChkComboBox.getItems().addAll(list);
                subjectChkComboBox.getCheckModel().checkIndices(0, 1);
            }
        }

        if(evt.getSource() == maleChkBox){
            if(maleChkBox.isSelected()){
                femaleChkBox.setDisable(true);
            }else{
                femaleChkBox.setDisable(false);
            }

        }

        if(evt.getSource() == femaleChkBox){
            if(femaleChkBox.isSelected()){
                maleChkBox.setDisable(true);
            }else{
                maleChkBox.setDisable(false);
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
                if(getFormInputs()){
                    form.setStudentId(form.generateStudentId(classComboBox));
                    if(form.submitForm()){
                        Camera.setImageName(null);
                        if(classComboBox.getValue().toString().equals("Primary 1")){
                            SessionProperty.updateProperty(SessionConstants.NO_OF_P1STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P1STUDENTS.getKey())) + 1));
                        }else if(classComboBox.getValue().toString().equals("Primary 2")){
                            SessionProperty.updateProperty(SessionConstants.NO_OF_P2STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P2STUDENTS.getKey())) + 1));
                        }else if(classComboBox.getValue().toString().equals("Primary 3")){
                            SessionProperty.updateProperty(SessionConstants.NO_OF_P3STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P3STUDENTS.getKey())) + 1));
                        }else if(classComboBox.getValue().toString().equals("Primary 4")){
                            SessionProperty.updateProperty(SessionConstants.NO_OF_P4STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P4STUDENTS.getKey())) + 1));
                        }else if(classComboBox.getValue().toString().equals("Primary 5")){
                            SessionProperty.updateProperty(SessionConstants.NO_OF_P5STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P5STUDENTS.getKey())) + 1));
                        }else if(classComboBox.getValue().toString().equals("Primary 6")){
                            SessionProperty.updateProperty(SessionConstants.NO_OF_P6STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P6STUDENTS.getKey())) + 1));
                        }else if(classComboBox.getValue().toString().equals("JS 1")){
                            SessionProperty.updateProperty(SessionConstants.NO_OF_J1STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J1STUDENTS.getKey())) + 1));
                        }else if(classComboBox.getValue().toString().equals("JS 2")){
                            SessionProperty.updateProperty(SessionConstants.NO_OF_J2STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J2STUDENTS.getKey())) + 1));
                        }else if(classComboBox.getValue().toString().equals("JS 3")){
                            SessionProperty.updateProperty(SessionConstants.NO_OF_J3STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J3STUDENTS.getKey())) + 1));
                        }else if(classComboBox.getValue().toString().equals("SS 1")){
                            SessionProperty.updateProperty(SessionConstants.NO_OF_S1STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S1STUDENTS.getKey())) + 1));
                        }else if(classComboBox.getValue().toString().equals("SS 2")){
                            SessionProperty.updateProperty(SessionConstants.NO_OF_S2STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S2STUDENTS.getKey())) + 1));
                        }else if(classComboBox.getValue().toString().equals("SS 3")){
                            SessionProperty.updateProperty(SessionConstants.NO_OF_S3STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S3STUDENTS.getKey())) + 1));
                        }
                        Stage stage = (Stage) submitBtn.getScene().getWindow();
                        Parent root = Loader.load(Path.STUDENT_REG_FORM.getPath());
                        stage.getScene().setRoot(root);
                        stage.show();
                        Alert alert = new Alert(Alert.AlertType.NONE, "Registration Successful", ButtonType.OK);
                        alert.showAndWait();
                    }else{
                        form = new RegistrationForm();
                    }
                }else{
                    Alert alert = new Alert(Alert.AlertType.NONE, check.get(0), ButtonType.OK);
                    alert.showAndWait();
                }

            }
        }
    }

    private boolean getFormInputs() {
        check = new ArrayList<>();
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
        // Gets religion.
        if(!religionTxtFld.getText().isEmpty()) form.setReligion(religionTxtFld.getText());
        // Gets previous school.
        if(!previousSchlTxtFld.getText().isEmpty()) form.setPSchool(previousSchlTxtFld.getText());
        // Gets Parents' name.
        if(!fatherNameTxtFld.getText().isEmpty()) form.setFatherName(fatherNameTxtFld.getText());
        if(!motherNameTxtFld.getText().isEmpty()) form.setMotherName(motherNameTxtFld.getText());
        // Gets email.
        if(!fEmailTxtFld.getText().isEmpty()) form.setFEmail(fEmailTxtFld.getText());
        if(!mEmailTxtFld.getText().isEmpty()) form.setMEmail(mEmailTxtFld.getText());
        // Gets phone number.
        if(!fPhnTxtFld.getText().isEmpty()) form.setFPhone(fPhnTxtFld.getText());
        if(!mPhnTxtFld.getText().isEmpty()) form.setMPhone(mPhnTxtFld.getText());
        // Gets Occupation
        if(!fOccupationTxtFld.getText().isEmpty()) form.setFOccupation(fOccupationTxtFld.getText());
        if(!mOccupationTxtFld.getText().isEmpty()) form.setMOccupation(mOccupationTxtFld.getText());
        // Gets date of birth.
        try{
            form.setDOB(datePicker.getValue().toString());
        }catch (Exception ignored){}
        // Gets gender.
        if(maleChkBox.isSelected()){
            form.setGender(maleChkBox.getText());
        }else if(femaleChkBox.isSelected()){
            form.setGender(femaleChkBox.getText());
        }else{
            check.add("Select Gender");
        }
        // Gets class.
        if(classComboBox.getValue() != null) form.set$class(classComboBox.getValue().toString());
        // Gets department.
        if(departmentComboBox.getValue() != null) form.setDepartment(departmentComboBox.getValue().toString());
        // Gets subject.
        if(subjectChkComboBox.getCheckModel().getCheckedItems().size() < 3){
            if(classComboBox.getValue() != null){
                if(classComboBox.getValue().toString().equals("SS 1") || classComboBox.getValue().toString().equals("SS 2") || classComboBox.getValue().toString().equals("SS 3")){
                    check.add("Not enough subjects");
                }
            }
        }else{
            String subjects = null;
            List<String> s = subjectChkComboBox.getCheckModel().getCheckedItems();
            for(int i = 0; i < s.size(); i++){
                if(i == 0){
                    subjects = s.get(i);
                }else{
                    subjects = subjects + ", " + s.get(i);
                }
            }
            form.setSubjects(subjects);
        }
        // Gets image name.
        if(Camera.getImageName() != null) form.setImageName(Camera.getImageName());

        if(check.isEmpty()){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        File file = new File("image/logo.jpg");
        Image img = new Image(file.toURI().toString());
        BackgroundImage bg = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(50,50,true,true,true, false));
        studRoot.setBackground(new Background(bg));
        // Initializes class combo box.
        String[] $class = {"Primary 1", "Primary 2", "Primary 3", "Primary 4", "Primary 5", "Primary 6",
                                "JS 1", "JS 2", "JS 3", "SS 1", "SS 2", "SS 3"};
        classComboBox.setItems(FXCollections.observableArrayList($class));

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
        validator = new Validator<>(formNode, 0,45);

        validator.registerEmptyValidation(firstNameTxtFld, "Field Required");
        validator.registerEmptyValidation(lastNameTxtFld, "Field Required");
        validator.registerEmptyValidation(midNameTxtFld, "Field Required");
        validator.registerEmptyValidation(addressTxtFld, "Field Required");
        validator.registerEmptyValidation(previousSchlTxtFld, "Field Required");
        validator.registerEmptyValidation(datePicker, "Field Required");
        validator.registerEmptyValidation(classComboBox, "Selection Required");
        validator.registerRegexValidation(datePicker, "Invalid Date Format", "[0-9]{2}-[0-9]{2}-[0-9]{4}");
        validator.registerRegexValidation(fPhnTxtFld, "Invalid Phone Number", "^0([789][0]|[8][1])[0-9]{8}");
        validator.registerRegexValidation(mPhnTxtFld, "Invalid Phone Number", "^0([789][0]|[8][1])[0-9]{8}");
        validator.registerRegexValidation(fEmailTxtFld, "Invalid Email Address", "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}");
        validator.registerRegexValidation(mEmailTxtFld, "Invalid Email Address", "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}");
    }

}