package zkysms.form.teacher;

import com.google.api.services.drive.Drive;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import zkysms.ui.LoadingScreen;
import zkysms.StudentsManagementSystemApp;
import zkysms.constants.Constants;
import zkysms.constants.Path;
import zkysms.constants.SessionConstants;
import zkysms.property.SessionProperty;
import zkysms.utils.DatabaseUtil;
import zkysms.utils.DriveUtil;
import zkysms.utils.LogUtil;
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
    private String firstName, lastName, middleName, title, role, nationality, dob, address, email, phoneNumber, imageName;

    public RegistrationForm(){}

    public void submitForm(){
        if(service != null){
            DriveUtil.manageConcurrency(service);
            if(DriveUtil.hasAccess()){
                DriveUtil.setAccess(false);
                File file = new File(Constants.LOCAL_DATABASE_FOLDER.getValue() + "/teacher.enc");
                // Stores database file in retained folder.
                try {
                    FileUtils.copyFile(file, new File(Constants.LOCAL_DATABASE_RETAINED_FOLDER.getValue() + "/teacher.enc"));
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
                        DatabaseUtil.decryptDB("teacher", StudentsManagementSystemApp.getKeyStorePassword(), StudentsManagementSystemApp.getSecretKeyEntryPassword());
                        Connection connection = null;
                        PreparedStatement preparedStatement = null;
                        String query = "INSERT INTO info(TeacherId, FirstName, LastName, MiddleName, Title, Role, Nationality, " +
                                "DOB, Address, Email, PhoneNumber, ImageName) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                        try{
                            connection = DatabaseUtil.getDBConnection("teacher");
                            preparedStatement = connection.prepareStatement(query);
                            preparedStatement.setString(1, generateTeacherId());
                            preparedStatement.setString(2, firstName);
                            preparedStatement.setString(3, lastName);
                            preparedStatement.setString(4, middleName);
                            preparedStatement.setString(5, title);
                            preparedStatement.setString(6, role);
                            preparedStatement.setString(7, nationality);
                            preparedStatement.setString(8, dob);
                            preparedStatement.setString(9, address);
                            preparedStatement.setString(10, email);
                            preparedStatement.setString(11, phoneNumber);
                            if(imageName != null){
                                preparedStatement.setString(12, imageName);
                                File image = new File(Constants.LOCAL_IMAGE_FOLDER.getValue() + "/" + imageName + ".png");
                                try{
                                    DriveUtil.createFile(image.getName(), Constants.DRIVE_IMAGE_FOLDER.getValue(), image, service);
                                }catch(Exception ignored){}
                            }else{
                                preparedStatement.setString(12, "NOIMAGE");
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
                            DatabaseUtil.encryptDB("teacher", StudentsManagementSystemApp.getKeyStorePassword(), StudentsManagementSystemApp.getSecretKeyEntryPassword());

                            boolean create = false;
                            try{
                                create = DriveUtil.createFile(file.getName(), Constants.DRIVE_DATABASE_FOLDER.getValue(), new File(Constants.LOCAL_DATABASE_FOLDER.getValue() + "/teacher.enc"), service);
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
                                    File toReplace = new File(Constants.LOCAL_DATABASE_FOLDER.getValue() + "/teacher.enc");
                                    File replacing = new File(Constants.LOCAL_DATABASE_RETAINED_FOLDER.getValue() + "/teacher.enc");
                                    if(replacing.exists()) toReplace.delete();
                                    try {
                                        FileUtils.copyFile(replacing, new File(Constants.LOCAL_DATABASE_FOLDER.getValue() + "/teacher.enc"));
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

    private String generateTeacherId() {
        String teacherId;
        if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_TEACHERS.getKey())) < 9){
            teacherId = "TCH" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4)
                            + "00" + (Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_TEACHERS.getKey())) + 1);
        }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_TEACHERS.getKey())) >= 9
                    && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_TEACHERS.getKey())) < 99){
            teacherId = "TCH" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4)
                            + "0"+ (Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_TEACHERS.getKey())) + 1);
        }else if(Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_TEACHERS.getKey())) >= 99
                && Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_TEACHERS.getKey())) < 999){
            teacherId = "TCH" + SessionProperty.getProperty(SessionConstants.CURRENT_SESSION.getKey()).substring(0, 4)
                            + (Integer.parseInt(SessionProperty.getProperty(SessionConstants.NO_OF_TEACHERS.getKey())) + 1);
        }else{
            teacherId = null;
        }
        return teacherId;
    }

    public static Node getScrollPane() {
        return scrollPane;
    }

    public LoadingScreen getLoadingScreen() {
        return loadingScreen;
    }

    public static Drive getDriveService() {
        return service;
    }

    public Logger getLOGGER() {
        return LOGGER;
    }

    public boolean hasSubmitted() {
        return hasSubmit;
    }

    public void setLoadingScreen(LoadingScreen loadingScreen) {
        this.loadingScreen = loadingScreen;
    }

    public static void setDriveService(Drive service) {
       RegistrationForm.service = service;
    }

    public static void setScrollPane(Node scrollPane) {
        RegistrationForm.scrollPane = scrollPane;
    }

    public void setFirstName(String firstName){
        this.firstName = firstName;
    }

    public void setLastName(String lastName){
        this.lastName = lastName;
    }

    public void setMiddleName(String middleName){
        this.middleName = middleName;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setRole(String religion){
        this.role = role;
    }

    public void setNationality(String nationality){
        this.nationality = nationality;
    }

    public void setAddress(String address){
        this.address = address;
    }

    public void setPhoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }

    public void setDOB(String dob){
        this.dob = dob;
    }

    public void setImageName(String imageName){
        this.imageName = imageName;
    }

}