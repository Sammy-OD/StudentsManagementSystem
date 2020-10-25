package zkysms.ui.registrationform.student;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import org.controlsfx.control.CheckComboBox;
import org.w3c.dom.Document;
import zkysms.StudentsManagementSystemApp;
import zkysms.ui.Loader;
import zkysms.ui.LoadingScreen;
import zkysms.constants.Path;
import zkysms.constants.Constants;
import zkysms.camera.Camera;
import zkysms.constants.SessionConstants;
import zkysms.property.SessionProperty;
import zkysms.form.student.RegistrationForm;
import zkysms.utils.DriveUtil;
import zkysms.utils.XMLUtil;
import zkysms.validator.Validator;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class RegistrationFormController implements Initializable {

    @FXML
    private ImageView backIcon;
    @FXML
    private ComboBox<String> departmentComboBox, classComboBox;
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
    private AnchorPane formNode, root;
    @FXML
    private ScrollPane scrollPane;
    private RegistrationForm form;
    private Validator validator;

    @FXML
    private void handleOnMouseClicked(MouseEvent evt) {
        if(evt.getSource() == backIcon){
            String imageName = Camera.getImageName();
            if(imageName != null){
                File image = new File(Constants.LOCAL_IMAGE_FOLDER.getValue() + "/" + imageName + ".png");
                image.delete(); // Deletes unused captured image.
            }
            RegistrationForm.setScrollPane(null);
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
                if(departmentComboBox.getValue().equals("Science")){
                    if(doc != null){
                        String[] s = doc.getElementsByTagName(Constants.SCIENCE_SUBJECTS_TAG.getTag().getTagName()).item(Constants.SCIENCE_SUBJECTS_TAG.getTag().getIndex()).getTextContent().split(",");
                        for(int i = 0; i < s.length; i++){
                            list.add(s[i].trim());
                        }
                    }
                }else if(departmentComboBox.getValue().equals("Social Science")){
                    if(doc != null){
                        String[] s = doc.getElementsByTagName(Constants.SOCIAL_SUBJECTS_TAG.getTag().getTagName()).item(Constants.SOCIAL_SUBJECTS_TAG.getTag().getIndex()).getTextContent().split(",");
                        for(int i = 0; i < s.length; i++){
                            list.add(s[i].trim());
                        }
                    }
                }else if(departmentComboBox.getValue().equals("Arts")){
                    if(doc != null){
                        String[] s = doc.getElementsByTagName(Constants.ARTS_SUBJECTS_TAG.getTag().getTagName()).item(Constants.ARTS_SUBJECTS_TAG.getTag().getIndex()).getTextContent().split(",");
                        for(int i = 0; i < s.length; i++){
                            list.add(s[i].trim());
                        }
                    }
                }
                subjectChkComboBox.getItems().addAll(list);
                subjectChkComboBox.getCheckModel().checkIndices(0, 1);
            }
        }

        if(evt.getSource() == maleChkBox){
            femaleChkBox.setDisable(maleChkBox.isSelected());

        }

        if(evt.getSource() == femaleChkBox){
            maleChkBox.setDisable(femaleChkBox.isSelected());
        }

        if(evt.getSource() == takePicBtn){
            if(RegistrationForm.getScrollPane() == null){
                RegistrationForm.setScrollPane(scrollPane);
            }
            String imageName = Camera.getImageName();
            if(imageName != null){
                File image = new File(Constants.LOCAL_IMAGE_FOLDER.getValue() + "/" + imageName + ".png");
                image.delete(); // Deletes unused captured image.
            }
            Camera.initWebcam();
            Camera.launch();
            Camera.setCameraCaller(this.getClass().getCanonicalName());
        }

        if(evt.getSource() == submitBtn){
            if(RegistrationForm.getScrollPane() == null){
                RegistrationForm.setScrollPane(scrollPane);
            }
            RegistrationForm.getScrollPane().setDisable(true);
            form.setLoadingScreen(new LoadingScreen("Submitting", root));
            form.getLoadingScreen().show();
            boolean val = validator.validate();
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    if(val){
                        getFormInputs();
                        form.setStudentId(form.generateStudentId(classComboBox));
                        form.submitForm();
                        if(form.hasSubmitted()){
                            Camera.setImageName(null);
                            if(classComboBox.getValue().equals("Primary 1")){
                                SessionProperty.updateProperty(SessionConstants.NO_OF_P1STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P1STUDENTS.getKey())) + 1));
                            }else if(classComboBox.getValue().equals("Primary 2")){
                                SessionProperty.updateProperty(SessionConstants.NO_OF_P2STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P2STUDENTS.getKey())) + 1));
                            }else if(classComboBox.getValue().equals("Primary 3")){
                                SessionProperty.updateProperty(SessionConstants.NO_OF_P3STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P3STUDENTS.getKey())) + 1));
                            }else if(classComboBox.getValue().equals("Primary 4")){
                                SessionProperty.updateProperty(SessionConstants.NO_OF_P4STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P4STUDENTS.getKey())) + 1));
                            }else if(classComboBox.getValue().equals("Primary 5")){
                                SessionProperty.updateProperty(SessionConstants.NO_OF_P5STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P5STUDENTS.getKey())) + 1));
                            }else if(classComboBox.getValue().equals("Primary 6")){
                                SessionProperty.updateProperty(SessionConstants.NO_OF_P6STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P6STUDENTS.getKey())) + 1));
                            }else if(classComboBox.getValue().equals("JS 1")){
                                SessionProperty.updateProperty(SessionConstants.NO_OF_J1STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J1STUDENTS.getKey())) + 1));
                            }else if(classComboBox.getValue().equals("JS 2")){
                                SessionProperty.updateProperty(SessionConstants.NO_OF_J2STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J2STUDENTS.getKey())) + 1));
                            }else if(classComboBox.getValue().equals("JS 3")){
                                SessionProperty.updateProperty(SessionConstants.NO_OF_J3STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J3STUDENTS.getKey())) + 1));
                            }else if(classComboBox.getValue().equals("SS 1")){
                                SessionProperty.updateProperty(SessionConstants.NO_OF_S1STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S1STUDENTS.getKey())) + 1));
                            }else if(classComboBox.getValue().equals("SS 2")){
                                SessionProperty.updateProperty(SessionConstants.NO_OF_S2STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S2STUDENTS.getKey())) + 1));
                            }else if(classComboBox.getValue().equals("SS 3")){
                                SessionProperty.updateProperty(SessionConstants.NO_OF_S3STUDENTS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S3STUDENTS.getKey())) + 1));
                            }
                            File file = new File(Constants.LOCAL_DOC_FOLDER.getValue() + "/app.xml");

                            boolean deleteX = false;
                            try{
                                deleteX = DriveUtil.deleteFile(file.getName(), Constants.DRIVE_DOC_FOLDER.getValue(), RegistrationForm.getDriveService(), false);
                            }catch(Exception ignored){}
                            if(deleteX){
                                try{
                                    DriveUtil.createFile(file.getName(), Constants.DRIVE_DOC_FOLDER.getValue(), file, RegistrationForm.getDriveService());
                                }catch(Exception ignored){}
                            }else{
                                form.getLOGGER().info("Could not delete app.xml from drive\n" +
                                        "Updated number of registered" + classComboBox.getValue().toString() + "students");
                            }

                            try{
                                DriveUtil.createFile("concurrency", Constants.DRIVE_ROOT_FOLDER.getValue(), new File(Path.DRIVE_CONCURRENCY.getPath()), RegistrationForm.getDriveService());
                            }catch(Exception ignored){}

                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    form.getLoadingScreen().close();
                                    RegistrationForm.setScrollPane(null);
                                    Stage stage = (Stage) submitBtn.getScene().getWindow();
                                    Parent root = Loader.load(Path.STUDENT_REG_FORM.getPath());
                                    stage.getScene().setRoot(root);
                                    stage.show();
                                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                                    alert.initOwner(StudentsManagementSystemApp.getPrimaryStage());
                                    alert.setHeaderText("Registration Successful");
                                    alert.setContentText(null);
                                    alert.showAndWait();
                                }
                            });
                        }else{
                            form = new RegistrationForm();
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    form.setLoadingScreen(new LoadingScreen("Submitting", root));
                                }
                            });
                        }
                    }else{
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                form.getLoadingScreen().close();
                                RegistrationForm.getScrollPane().setDisable(false);
                                submitBtn.requestFocus();
                            }
                        });
                    }
                    return null;
                }
            };
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void getFormInputs() {
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
        }
        // Gets class.
        if(classComboBox.getValue() != null) form.set$class(classComboBox.getValue());
        // Gets department.
        if(departmentComboBox.getValue() != null) form.setDepartment(departmentComboBox.getValue());
        // Gets subject.
        if(subjectChkComboBox.getCheckModel().getCheckedItems() != null){
            StringBuilder subjects = null;
            List<String> s = subjectChkComboBox.getCheckModel().getCheckedItems();
            for(int i = 0; i < s.size(); i++){
                if(i == 0){
                    subjects = new StringBuilder(s.get(i));
                }else{
                    subjects.append(", ").append(s.get(i));
                }
            }
            if(subjects != null){
                form.setSubjects(subjects.toString());
            }
        }
        // Gets image name.
        if(Camera.getImageName() != null) form.setImageName(Camera.getImageName());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initializes class combo box.
        String[] $class = {"Primary 1", "Primary 2", "Primary 3", "Primary 4", "Primary 5", "Primary 6",
                                "JS 1", "JS 2", "JS 3", "SS 1", "SS 2", "SS 3"};
        classComboBox.setItems(FXCollections.observableArrayList($class));

        datePicker.setConverter(new StringConverter<LocalDate>() {
            final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
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
        RegistrationForm.setDriveService(DriveUtil.getDriveService());
        RegistrationForm.setScrollPane(scrollPane);
        validator = new Validator(formNode, 0,45);

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
        validator.registerChoiceValidation("Select Gender", maleChkBox, femaleChkBox);
    }

}