package zkysms.ui.registrationform.teacher;

import javafx.application.Platform;
import javafx.concurrent.Task;
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
import zkysms.StudentsManagementSystemApp;
import zkysms.ui.Loader;
import zkysms.ui.LoadingScreen;
import zkysms.constants.Constants;
import zkysms.constants.Path;
import zkysms.constants.SessionConstants;
import zkysms.property.SessionProperty;
import zkysms.camera.Camera;
import zkysms.form.teacher.RegistrationForm;
import zkysms.utils.DriveUtil;
import zkysms.validator.Validator;
import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class RegistrationFormController implements Initializable {

    @FXML
    private ImageView backIcon;
    @FXML
    private TextField firstNameTxtFld, lastNameTxtFld, midNameTxtFld, phnNumTxtFld, roleTxtFld,
                        nationTxtFld, addressTxtFld, emailTxtFld;
    @FXML
    private Button submitBtn, takePicBtn;
    @FXML
    private DatePicker datePicker;
    @FXML
    private CheckBox mrChkBox, mrsChkBox, missChkBox;
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
                        form.submitForm();
                        if(form.hasSubmitted()){
                            Camera.setImageName(null);
                            SessionProperty.updateProperty(SessionConstants.NO_OF_TEACHERS.getKey(), Integer.toString(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_TEACHERS.getKey())) + 1));
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
                                        "Updated number of registered teachers");
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
                                   Parent root = Loader.load(Path.TEACHER_REG_FORM.getPath());
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
        // Gets Role.
        if(!roleTxtFld.getText().isEmpty()) form.setRole(roleTxtFld.getText());
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
        File file = new File("image/logo.jpg");
        Image img = new Image(file.toURI().toString());
        BackgroundImage bg = new BackgroundImage(img, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(50,50,true,true,true, false));
        scrollPane.setBackground(new Background(bg));
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
        validator = new Validator(formNode, 0, 45);

        validator.registerEmptyValidation(firstNameTxtFld, "Field Required");
        validator.registerEmptyValidation(lastNameTxtFld, "Field Required");
        validator.registerEmptyValidation(midNameTxtFld, "Field Required");
        validator.registerEmptyValidation(datePicker, "Field Required");
        validator.registerEmptyValidation(phnNumTxtFld, "Field Required");
        validator.registerEmptyValidation(emailTxtFld, "Field Required");
        validator.registerEmptyValidation(addressTxtFld, "Field Required");
        validator.registerChoiceValidation("Select Title", mrChkBox, mrsChkBox, missChkBox);
        validator.registerRegexValidation(datePicker, "Invalid Date Format", "[0-9]{2}-[0-9]{2}-[0-9]{4}");
        validator.registerRegexValidation(phnNumTxtFld, "Invalid Phone Number", "^0([789][0]|[8][1])[0-9]{8}");
        validator.registerRegexValidation(emailTxtFld, "Invalid Email Address", "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}");
    }

}