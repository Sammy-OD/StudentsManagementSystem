package zkysms.form.student;

import com.google.api.services.drive.Drive;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import zkysms.ui.LoadingScreen;
import zkysms.StudentsManagementSystemApp;
import zkysms.constants.Path;
import zkysms.constants.Constants;
import zkysms.constants.SessionConstants;
import zkysms.property.SessionProperty;
import zkysms.utils.DatabaseUtil;
import zkysms.utils.DriveUtil;
import zkysms.utils.LogUtil;
import zkysms.utils.XMLUtil;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationForm {

    private static Node scrollPane;
    private static Drive service;
    private boolean hasSubmit = false;
    private LoadingScreen loadingScreen;
    private final Logger LOGGER = LogUtil.getLOGGER(RegistrationForm.class.getName());
    private String studentId, firstName, lastName, middleName, dob, gender, $class, department, subjects, religion, nationality, address, pSchool, fatherName, motherName, fOccupation, mOccupation, fPhone, mPhone, fEmail, mEmail, imageName;

    public RegistrationForm(){}

    public void submitForm(){
        if(service != null){
            DriveUtil.manageConcurrency(service);
            if(DriveUtil.hasAccess()){
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        loadingScreen.setMessage("Processing");
                    }
                });
                DriveUtil.setAccess(false);
                File file = new File(Constants.LOCAL_DATABASE_FOLDER.getValue() + "/student.enc");
                // Stores database file in retained folder.
                try {
                    FileUtils.copyFile(file, new File(Constants.LOCAL_DATABASE_RETAINED_FOLDER.getValue() + "/student.enc"), true);
                }catch(IOException e) {
                    LOGGER.error(e.getMessage(), e);
                    return;
                }
                boolean download = false;
                try{
                    download = DriveUtil.downloadFile(file.getName(), Constants.DRIVE_DATABASE_FOLDER.getValue(), Constants.LOCAL_DATABASE_FOLDER.getValue(), service);
                }catch(Exception ignored){}
                if(download){
                    boolean delete = false;
                    try{
                        delete = DriveUtil.deleteFile(file.getName(), Constants.DRIVE_DATABASE_FOLDER.getValue(), service, true);
                    }catch(Exception ignored){}
                    if(delete){
                        DatabaseUtil.decryptDB("student", StudentsManagementSystemApp.getKeyStorePassword(), StudentsManagementSystemApp.getSecretKeyEntryPassword());
                        Connection connection = null;
                        PreparedStatement preparedStatement = null;
                        String query = "INSERT INTO info (StudentId, FirstName, LastName, MiddleName, DOB, Gender, Class, Department, Subjects, Religion, Nationality, " +
                                " Address, PSchool, FatherName, MotherName, FEmail, MEmail, FPhone, MPhone, FOccupation, MOccupation, ImageName) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        try{
                            connection = DatabaseUtil.getDBConnection("student");
                            preparedStatement = connection.prepareStatement(query);
                            preparedStatement.setString(1, studentId);
                            preparedStatement.setString(2, firstName);
                            preparedStatement.setString(3, lastName);
                            preparedStatement.setString(4, middleName);
                            preparedStatement.setString(5, dob);
                            preparedStatement.setString(6, gender);
                            preparedStatement.setString(7, $class);
                            preparedStatement.setString(8, department);
                            if(subjects == null){
                                if($class != null){
                                    Document doc = XMLUtil.loadXML(Path.APP_XML.getPath());
                                    if($class.equals("Primary 1") || $class.equals("Primary 2") || $class.equals("Primary 3") || $class.equals("Primary 4")
                                            || $class.equals("Primary 5") || $class.equals("Primary 6")){
                                        if(doc != null){
                                            subjects = "Mathematics, English, " + doc.getElementsByTagName(Constants.PRIMARY_SUBJECTS_TAG.getTag().getTagName()).item(Constants.PRIMARY_SUBJECTS_TAG.getTag().getIndex()).getTextContent();
                                        }
                                    }else if($class.equals("JS 1") || $class.equals("JS 2") || $class.equals("JS 3")){
                                        if(doc != null){
                                            subjects = "Mathematics, English, " + doc.getElementsByTagName(Constants.JUNIOR_SUBJECTS_TAG.getTag().getTagName()).item(Constants.JUNIOR_SUBJECTS_TAG.getTag().getIndex()).getTextContent();
                                        }
                                    }
                                }
                            }
                            preparedStatement.setString(9, subjects);
                            preparedStatement.setString(10, religion);
                            preparedStatement.setString(11, nationality);
                            preparedStatement.setString(12, address);
                            preparedStatement.setString(13, pSchool);
                            preparedStatement.setString(14, fatherName);
                            preparedStatement.setString(15, motherName);
                            preparedStatement.setString(16, fEmail);
                            preparedStatement.setString(17, mEmail);
                            preparedStatement.setString(18, fPhone);
                            preparedStatement.setString(19, mPhone);
                            preparedStatement.setString(20, fOccupation);
                            preparedStatement.setString(21, mOccupation);
                            if(imageName != null){
                                preparedStatement.setString(22, imageName);
                                File image = new File(Constants.LOCAL_IMAGE_FOLDER.getValue() + "/" + imageName + ".png");
                                try{
                                    DriveUtil.createFile(image.getName(), Constants.DRIVE_IMAGE_FOLDER.getValue(), image, service);
                                }catch(Exception ignored){}
                            }else{
                                preparedStatement.setString(22, "NOIMAGE");
                            }
                            preparedStatement.executeUpdate();
                            hasSubmit = true;
                        }catch(SQLException e){
                            LOGGER.error(e.getMessage(), e);
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    loadingScreen.setMessage("Cancelling");
                                }
                            });
                        }finally {
                            try{
                                if(preparedStatement != null) preparedStatement.close();
                                if(connection != null) connection.close();
                            }catch(SQLException e){
                                e.printStackTrace();
                            }
                            DatabaseUtil.encryptDB("student", StudentsManagementSystemApp.getKeyStorePassword(), StudentsManagementSystemApp.getSecretKeyEntryPassword());

                            boolean create = false;
                            try{
                                create = DriveUtil.createFile(file.getName(), Constants.DRIVE_DATABASE_FOLDER.getValue(), new File(Constants.LOCAL_DATABASE_FOLDER.getValue() + "/student.enc"), service);
                            }catch(Exception ignored){}

                            if(hasSubmitted()){
                                if(!create){
                                    // If creating database file in drive fails it replaces the updated database with previous one.
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            loadingScreen.setMessage("Cancelling");
                                        }
                                    });
                                    hasSubmit = false;
                                    File toReplace = new File(Constants.LOCAL_DATABASE_FOLDER.getValue() + "/student.enc");
                                    File replacing = new File(Constants.LOCAL_DATABASE_RETAINED_FOLDER.getValue() + "/student.enc");
                                    if(replacing.exists()) toReplace.delete();
                                    try {
                                        FileUtils.copyFile(replacing, new File(Constants.LOCAL_DATABASE_FOLDER.getValue() + "/student.enc"), true);
                                        replacing.delete();
                                    }catch(IOException e){
                                        LOGGER.error(e.getMessage(), e);
                                    }
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run(){
                                            loadingScreen.close();
                                            scrollPane.setDisable(false);
                                            Alert alert = new Alert(Alert.AlertType.ERROR);
                                            alert.initOwner(StudentsManagementSystemApp.getPrimaryStage());
                                            alert.setHeaderText("Submission Failed");
                                            alert.setContentText("10003");
                                            alert.showAndWait();
                                        }
                                    });
                                    try{
                                        DriveUtil.createFile("concurrency", Constants.DRIVE_ROOT_FOLDER.getValue(), new File(Path.DRIVE_CONCURRENCY.getPath()), service);
                                    }catch(Exception ignored){}
                                }
                            }else{
                                try{
                                    DriveUtil.createFile("concurrency", Constants.DRIVE_ROOT_FOLDER.getValue(), new File(Path.DRIVE_CONCURRENCY.getPath()), service);
                                }catch(Exception ignored){}
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        loadingScreen.close();
                                        scrollPane.setDisable(false);
                                        Alert alert = new Alert(Alert.AlertType.ERROR);
                                        alert.initOwner(StudentsManagementSystemApp.getPrimaryStage());
                                        alert.setHeaderText("Submission Failed");
                                        alert.setContentText("10004");
                                        alert.showAndWait();
                                    }
                                });
                            }
                        }
                    }else{
                        try{
                            DriveUtil.createFile("concurrency", Constants.DRIVE_ROOT_FOLDER.getValue(), new File(Path.DRIVE_CONCURRENCY.getPath()), service);
                        }catch(Exception ignored){}
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                loadingScreen.close();
                                scrollPane.setDisable(false);
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.initOwner(StudentsManagementSystemApp.getPrimaryStage());
                                alert.setHeaderText("Submission Failed");
                                alert.setContentText("10002");
                                alert.showAndWait();
                            }
                        });
                    }
                }else{
                    try{
                        DriveUtil.createFile("concurrency", Constants.DRIVE_ROOT_FOLDER.getValue(), new File(Path.DRIVE_CONCURRENCY.getPath()), service);
                    }catch(Exception ignored){}
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            loadingScreen.close();
                            scrollPane.setDisable(false);
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.initOwner(StudentsManagementSystemApp.getPrimaryStage());
                            alert.setHeaderText("Submission Failed");
                            alert.setContentText("10001");
                            alert.showAndWait();
                        }
                    });
                }
            }else{
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        loadingScreen.close();
                        scrollPane.setDisable(false);
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.initOwner(StudentsManagementSystemApp.getPrimaryStage());
                        alert.setHeaderText("Submission Failed");
                        alert.setContentText("Could not establish connection to google drive");
                        alert.showAndWait();
                    }
                });
            }
        }
    }

    public String generateStudentId(ComboBox<String> $class){
        String studentId;
        if($class.getValue().equals("Primary 1")){
            if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P1STUDENTS.getKey())) < 9){
                studentId = "STD01" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "00"
                                + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P1STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P1STUDENTS.getKey())) >= 9
                        && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P1STUDENTS.getKey())) < 99){
                studentId = "STD01" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "0"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P1STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P1STUDENTS.getKey())) >= 99
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P1STUDENTS.getKey())) < 999){
                studentId = "STD01" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4)
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P1STUDENTS.getKey())) + 1));
            }else{
                studentId = null;
            }
        }else if($class.getValue().equals("Primary 2")){
            if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P2STUDENTS.getKey())) < 9){
                studentId = "STD02" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "00"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P2STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P2STUDENTS.getKey())) >= 9
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P2STUDENTS.getKey())) < 99){
                studentId = "STD02" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "0"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P2STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P2STUDENTS.getKey())) >= 99
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P2STUDENTS.getKey())) < 999){
                studentId = "STD02" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4)
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P2STUDENTS.getKey())) + 1));
            }else{
                studentId = null;
            }
        }else if($class.getValue().equals("Primary 3")){
            if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P3STUDENTS.getKey())) < 9){
                studentId = "STD03" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "00"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P3STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P3STUDENTS.getKey())) >= 9
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P3STUDENTS.getKey())) < 99){
                studentId = "STD03" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "0"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P3STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P3STUDENTS.getKey())) >= 99
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P3STUDENTS.getKey())) < 999){
                studentId = "STD03" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4)
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P3STUDENTS.getKey())) + 1));
            }else{
                studentId = null;
            }
        }else if($class.getValue().equals("Primary 4")){
            if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P4STUDENTS.getKey())) < 9){
                studentId = "STD04" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "00"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P4STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P4STUDENTS.getKey())) >= 9
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P4STUDENTS.getKey())) < 99){
                studentId = "STD04" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "0"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P4STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P4STUDENTS.getKey())) >= 99
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P4STUDENTS.getKey())) < 999){
                studentId = "STD04" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4)
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P4STUDENTS.getKey())) + 1));
            }else{
                studentId = null;
            }
        }else if($class.getValue().equals("Primary 5")){
            if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P5STUDENTS.getKey())) < 9){
                studentId = "STD05" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "00"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P5STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P5STUDENTS.getKey())) >= 9
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P5STUDENTS.getKey())) < 99){
                studentId = "STD05" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "0"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P5STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P5STUDENTS.getKey())) >= 99
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P5STUDENTS.getKey())) < 999){
                studentId = "STD05" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4)
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P5STUDENTS.getKey())) + 1));
            }else{
                studentId = null;
            }
        }else if($class.getValue().equals("Primary 6")){
            if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P6STUDENTS.getKey())) < 9){
                studentId = "STD06" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "00"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P6STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P6STUDENTS.getKey())) >= 9
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P6STUDENTS.getKey())) < 99){
                studentId = "STD06" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "0"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P6STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P6STUDENTS.getKey())) >= 99
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P6STUDENTS.getKey())) < 999){
                studentId = "STD06" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4)
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_P6STUDENTS.getKey())) + 1));
            }else{
                studentId = null;
            }
        }else if($class.getValue().equals("JS 1")){
            if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J1STUDENTS.getKey())) < 9){
                studentId = "STD07" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "00"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J1STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J1STUDENTS.getKey())) >= 9
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J1STUDENTS.getKey())) < 99){
                studentId = "STD07" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "0"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J1STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J1STUDENTS.getKey())) >= 99
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J1STUDENTS.getKey())) < 999){
                studentId = "STD07" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4)
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J1STUDENTS.getKey())) + 1));
            }else{
                studentId = null;
            }
        }else if($class.getValue().equals("JS 2")){
            if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J2STUDENTS.getKey())) < 9){
                studentId = "STD08" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "00"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J2STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J2STUDENTS.getKey())) >= 9
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J2STUDENTS.getKey())) < 99){
                studentId = "STD08" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "0"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J2STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J2STUDENTS.getKey())) >= 99
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J2STUDENTS.getKey())) < 999){
                studentId = "STD08" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4)
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J2STUDENTS.getKey())) + 1));
            }else{
                studentId = null;
            }
        }else if($class.getValue().equals("JS 3")){
            if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J3STUDENTS.getKey())) < 9){
                studentId = "STD09" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "00"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J3STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J3STUDENTS.getKey())) >= 9
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J3STUDENTS.getKey())) < 99){
                studentId = "STD09" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "0"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J3STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J3STUDENTS.getKey())) >= 99
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J3STUDENTS.getKey())) < 999){
                studentId = "STD09" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4)
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_J3STUDENTS.getKey())) + 1));
            }else{
                studentId = null;
            }
        }else if($class.getValue().equals("SS 1")){
            if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S1STUDENTS.getKey())) < 9){
                studentId = "STD10" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "00"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S1STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S1STUDENTS.getKey())) >= 9
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S1STUDENTS.getKey())) < 99){
                studentId = "STD10" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "0"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S1STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S1STUDENTS.getKey())) >= 99
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S1STUDENTS.getKey())) < 999){
                studentId = "STD10" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4)
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S1STUDENTS.getKey())) + 1));
            }else{
                studentId = null;
            }
        }else if($class.getValue().equals("SS 2")){
            if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S2STUDENTS.getKey())) < 9){
                studentId = "STD11" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "00"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S2STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S2STUDENTS.getKey())) >= 9
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S2STUDENTS.getKey())) < 99){
                studentId = "STD11" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "0"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S2STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S2STUDENTS.getKey())) >= 99
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S2STUDENTS.getKey())) < 999){
                studentId = "STD11" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4)
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S2STUDENTS.getKey())) + 1));
            }else{
                studentId = null;
            }
        }else if($class.getValue().equals("SS 3")){
            if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S3STUDENTS.getKey())) < 9){
                studentId = "STD12" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "00"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S3STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S3STUDENTS.getKey())) >= 9
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S3STUDENTS.getKey())) < 99){
                studentId = "STD12" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4) + "0"
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S3STUDENTS.getKey())) + 1));
            }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S3STUDENTS.getKey())) >= 99
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S3STUDENTS.getKey())) < 999){
                studentId = "STD12" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4)
                        + ((Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_S3STUDENTS.getKey())) + 1));
            }else{
                studentId = null;
            }
        }else{
            studentId = null;
        }
        return studentId;
    }

    public static Node getScrollPane(){
        return scrollPane;
    }

    public LoadingScreen getLoadingScreen() {
        return loadingScreen;
    }

    public static Drive getDriveService() {
        return service;
    }

    public boolean hasSubmitted() {
        return hasSubmit;
    }

    public Logger getLOGGER() {
        return LOGGER;
    }

    public static void setScrollPane(Node scrollPane){
        RegistrationForm.scrollPane = scrollPane;
    }

    public void setLoadingScreen(LoadingScreen loadingScreen) {
        this.loadingScreen = loadingScreen;
    }

    public static void setDriveService(Drive service) {
        RegistrationForm.service = service;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public void setDOB(String dob) {
        this.dob = dob;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void set$class(String $class) {
        this.$class = $class;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public void setSubjects(String subjects) {
        this.subjects = subjects;
    }

    public void setReligion(String religion) {
        this.religion = religion;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPSchool(String pSchool) {
        this.pSchool = pSchool;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public void setFOccupation(String fOccupation) {
        this.fOccupation = fOccupation;
    }

    public void setMOccupation(String mOccupation) {
        this.mOccupation = mOccupation;
    }

    public void setFPhone(String fPhone) {
        this.fPhone = fPhone;
    }

    public void setMPhone(String mPhone) {
        this.mPhone = mPhone;
    }

    public void setFEmail(String fEmail) {
        this.fEmail = fEmail;
    }

    public void setMEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

}