package zkysms.ui.setup;

import com.google.api.services.drive.Drive;
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
import zkysms.ui.Loader;
import zkysms.constants.Path;
import zkysms.StudentsManagementSystemApp;
import zkysms.constants.Constants;
import zkysms.mail.Mail;
import zkysms.utils.CryptoUtil;
import zkysms.utils.DatabaseUtil;
import zkysms.utils.DriveUtil;
import zkysms.utils.XMLUtil;
import zkysms.validator.Validator;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SetupController implements Initializable {

    @FXML
    private Button backBtn, nextBtn, finishBtn, browseBtn;
    @FXML
    private TextField sessionTxtFld, adminEmailTxtFld, schlNameTxtFld, logoTxtFld;
    @FXML
    private PasswordField passwordTxtFld, cPasswordTxtFld;
    @FXML
    private ComboBox<String> termComboBox;
    @FXML
    private AnchorPane root;
    @FXML
    private VBox adminBox, schoolBox, periodBox;
    private Validator sessionValidator, adminValidator, schlValidator;
    private static final Logger LOGGER = Logger.getLogger(SetupController.class.getName());

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
                if(adminValidator.validate()){
                    adminBox.setVisible(false);
                    adminBox.setManaged(false);
                    schoolBox.setVisible(true);
                    schoolBox.setManaged(true);
                    backBtn.setDisable(false);
                    schoolBox.requestFocus();
                }
            }else if(schoolBox.isVisible()){
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
                schlValidator.removeLabel(schlNameTxtFld);
                adminBox.setVisible(true);
                adminBox.setManaged(true);
                backBtn.setDisable(true);
                adminBox.requestFocus();
            }else if(periodBox.isVisible()){
                periodBox.setVisible(false);
                periodBox.setManaged(false);
                sessionValidator.removeAllLabel(sessionTxtFld, termComboBox);
                schoolBox.setVisible(true);
                schoolBox.setManaged(true);
                nextBtn.setDisable(false);
                finishBtn.setDisable(true);
                schoolBox.requestFocus();
            }
        }
        if(evt.getSource() == finishBtn){
            if(sessionValidator.validate()){
                ((Stage)(finishBtn.getScene().getWindow())).close();

                String keyStorePassword = CryptoUtil.generatePassword(passwordTxtFld.getText().length());
                String secretKeyEntryPassword = CryptoUtil.generatePassword(passwordTxtFld.getText().length());
                Mail mailSend = new Mail();
                String to = adminEmailTxtFld.getText();
                String text = "Welcome " + schlNameTxtFld.getText() + ",\n" +
                        keyStorePassword + " and " + secretKeyEntryPassword + " are your keystore password and secret key entry password respectively." + "\n" +
                        "Keep this safe as these unlock all of your files thereby giving access to anyone." + "\n" +
                        "Also, at every start up these passwords will be required." + "\n" +
                        "Thanks.";
                if(mailSend.send(Constants.NO_REPLY_EMAIL.getValue(), to, Constants.NO_REPLY_PASSWORD.getValue(), Constants.KEYSTORE_EMAIL_SUBJECT.getValue(), text)){
                    Document doc = XMLUtil.loadXML(Path.APP_XML.getPath());
                    Drive service = DriveUtil.getDriveService();
                    if(doc != null && service != null){
                        // Creates keystore and stores secret key.
                        CryptoUtil.createKeyStore(Path.KEYSTORE.getPath(), keyStorePassword);
                        try{
                            SecureRandom secureRandom = new SecureRandom();
                            byte[] salt = new byte[8];
                            secureRandom.nextBytes(salt);
                            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                            KeySpec spec = new PBEKeySpec(passwordTxtFld.getText().toCharArray(), salt, 10000, 128);
                            SecretKey tmp = factory.generateSecret(spec);
                            SecretKey secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");
                            CryptoUtil.storeSecretKey(Path.KEYSTORE.getPath(), keyStorePassword, Constants.SECRET_KEY_ALIAS.getValue(), secretKeyEntryPassword, secretKey);
                            File kFile = new File(Path.KEYSTORE.getPath());
                            try{
                                DriveUtil.createFile(kFile.getName(), "doc", kFile, service);
                            }catch(Exception ignored){}
                        }catch(NoSuchAlgorithmException | InvalidKeySpecException e){
                            e.printStackTrace();
                            LOGGER.log(Level.SEVERE, e.getMessage(), e);
                            LOGGER.getHandlers()[0].close();
                        }

                        // Creates file admin to store admin password.
                        try{
                            Cipher cipher = Cipher.getInstance("AES");
                            byte[] password = CryptoUtil.encrypt(passwordTxtFld.getText(), cipher, Path.KEYSTORE.getPath(), keyStorePassword, secretKeyEntryPassword, Constants.SECRET_KEY_ALIAS.getValue());
                            File file = new File(Path.ADMIN.getPath());
                            file.createNewFile();
                            if(password != null){
                                FileUtils.writeByteArrayToFile(file, password);
                            }
                            try{
                                DriveUtil.createFile(file.getName(), "doc", new File(Path.ADMIN.getPath()), service);
                            }catch(Exception e){}
                        }catch(NoSuchPaddingException | NoSuchAlgorithmException | IOException e){
                            e.printStackTrace();
                            LOGGER.log(Level.SEVERE, e.getMessage(), e);
                            LOGGER.getHandlers()[0].close();
                        }

                        // Copies logo to image folder of the project.
                        File src = new File(logoTxtFld.getText());
                        File dest = new File("image/logo." + FilenameUtils.getExtension(src.getName()));
                        try{
                            FileUtils.copyFile(src, dest);
                            try{
                                DriveUtil.createFile(dest.getName(), "image", new File("image/" + dest.getName()), service);
                            }catch(Exception ignored){}
                        }catch(IOException ignored){}

                        // Writes to app.xml
                        doc.getElementsByTagName(Constants.SCHOOL_NAME_TAG.getTag().getTagName()).item(Constants.SCHOOL_NAME_TAG.getTag().getIndex()).setTextContent(schlNameTxtFld.getText());
                        if(src.exists()){
                            doc.getElementsByTagName(Constants.SCHOOL_LOGO_TAG.getTag().getTagName()).item(Constants.SCHOOL_LOGO_TAG.getTag().getIndex()).setTextContent(dest.getPath());
                        }
                        doc.getElementsByTagName(Constants.SESSION_TAG.getTag().getTagName()).item(Constants.SESSION_TAG.getTag().getIndex()).setTextContent(sessionTxtFld.getText());
                        doc.getElementsByTagName(Constants.TERM_TAG.getTag().getTagName()).item(Constants.TERM_TAG.getTag().getIndex()).setTextContent(termComboBox.getValue().toString());
                        doc.getElementsByTagName(Constants.ADMIN_EMAIL_TAG.getTag().getTagName()).item(Constants.ADMIN_EMAIL_TAG.getTag().getIndex()).setTextContent(adminEmailTxtFld.getText());
                        XMLUtil.updateXML(Path.APP_XML.getPath(), doc);
                        File xFile = new File(Path.APP_XML.getPath());
                        try{
                            DriveUtil.createFile(xFile.getName(), "doc", xFile, service);
                        }catch(Exception ignored){}


                        // Creates databases.
                        DatabaseUtil.createNewDatabase("teacher", keyStorePassword, secretKeyEntryPassword);
                        String teacher = "CREATE TABLE info (\n" +
                                "    TeacherId   TEXT NOT NULL\n" +
                                "                     UNIQUE,\n" +
                                "    FirstName   TEXT NOT NULL,\n" +
                                "    LastName    TEXT NOT NULL,\n" +
                                "    MiddleName  TEXT NOT NULL,\n" +
                                "    Title       TEXT NOT NULL,\n" +
                                "    Role        TEXT,\n" +
                                "    Nationality TEXT,\n" +
                                "    DOB         TEXT NOT NULL,\n" +
                                "    Address     TEXT NOT NULL,\n" +
                                "    Email       TEXT NOT NULL\n" +
                                "                     UNIQUE,\n" +
                                "    PhoneNumber TEXT NOT NULL\n" +
                                "                     UNIQUE,\n" +
                                "    Password    TEXT NOT NULL\n" +
                                "                     DEFAULT teacher,\n" +
                                "    ImageName   TEXT NOT NULL\n" +
                                ");";
                        DatabaseUtil.createTable("teacher", teacher, keyStorePassword, secretKeyEntryPassword);
                        DatabaseUtil.createNewDatabase("student", keyStorePassword, secretKeyEntryPassword);
                        String student = "CREATE TABLE info (\n" +
                                "    StudentId   TEXT NOT NULL\n" +
                                "                     UNIQUE,\n" +
                                "    FirstName   TEXT NOT NULL,\n" +
                                "    LastName    TEXT NOT NULL,\n" +
                                "    MiddleName  TEXT NOT NULL,\n" +
                                "    DOB         TEXT NOT NULL,\n" +
                                "    Gender      TEXT NOT NULL,\n" +
                                "    Class       TEXT NOT NULL,\n" +
                                "    Department  TEXT,\n" +
                                "    Subjects    TEXT NOT NULL,\n" +
                                "    Religion    TEXT,\n" +
                                "    Nationality TEXT,\n" +
                                "    Address     TEXT NOT NULL,\n" +
                                "    PSchool     TEXT NOT NULL,\n" +
                                "    FatherName  TEXT,\n" +
                                "    MotherName  TEXT,\n" +
                                "    FEmail      TEXT,\n" +
                                "    MEmail      TEXT,\n" +
                                "    FPhone      TEXT,\n" +
                                "    MPhone      TEXT,\n" +
                                "    FOccupation TEXT,\n" +
                                "    MOccupation TEXT,\n" +
                                "    ImageName   TEXT NOT NULL\n" +
                                ");";
                        DatabaseUtil.createTable("student", student, keyStorePassword, secretKeyEntryPassword);

                        try{
                            DriveUtil.createFile("teacher.enc", "database", new File("database/teacher.enc"), service);
                            DriveUtil.createFile("student.enc", "database", new File("database/student.enc"), service);
                        }catch(Exception ignored){}

                        StudentsManagementSystemApp.setKeyStorePassword(keyStorePassword);
                        StudentsManagementSystemApp.setSecretKeyEntryPassword(secretKeyEntryPassword);

                        Parent root = Loader.load(Path.LOGIN_PAGE.getPath());
                        if(root != null){
                            StudentsManagementSystemApp.getPrimaryStage().setScene(new Scene(root, StudentsManagementSystemApp.getWidth(), StudentsManagementSystemApp.getHeight()));
                        }
                        StudentsManagementSystemApp.getPrimaryStage().setOnCloseRequest(new EventHandler<WindowEvent>() {
                            @Override
                            public void handle(WindowEvent windowEvent) {
                                System.exit(2);
                            }
                        });
                        StudentsManagementSystemApp.getPrimaryStage().show();
                    }
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
        adminValidator = new Validator(root, 45, 75);
        adminValidator.registerEmptyValidation(adminEmailTxtFld, "Field Required");
        adminValidator.registerRegexValidation(adminEmailTxtFld, "Invalid Email Address", "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}");
        adminValidator.registerEmptyValidation(passwordTxtFld, "Field Required");
        adminValidator.registerEmptyValidation(cPasswordTxtFld, "Field Required");
        adminValidator.registerEqualValidation(passwordTxtFld, cPasswordTxtFld, "Password does not match");
        schlValidator = new Validator(root, 45, 75);
        schlValidator.registerEmptyValidation(schlNameTxtFld, "Field Required");
        sessionValidator = new Validator(root, 45, 75);
        sessionValidator.registerEmptyValidation(sessionTxtFld, "Field Required");
        sessionValidator.registerEmptyValidation(termComboBox, "Selection Required");
        sessionValidator.registerRegexValidation(sessionTxtFld, "Invalid Input", "2[0-9]{3}/2[0-9]{3}");
    }

}