package zkysms;

import com.google.api.services.drive.Drive;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import zkysms.constants.Constants;
import zkysms.constants.Path;
import zkysms.ui.Loader;
import zkysms.utils.DriveUtil;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentsManagementSystemApp extends Application {

    private static final double WIDTH = Screen.getPrimary().getBounds().getWidth() * 0.8;
    private static final double HEIGHT = Screen.getPrimary().getBounds().getHeight() * 0.8;
    private static String keyStorePassword, secretKeyEntryPassword;
    private Stage stageSplash;
    private static Stage primaryStage, setupStage;
    private ProgressBar progressBar;

    @Override
    public void start(Stage primaryStage) {
        StudentsManagementSystemApp.primaryStage = primaryStage;
        primaryStage.getIcons().add(new Image(new File("image/icon.jpg").toURI().toString()));
        List<File> fileListDrive = new ArrayList<>();
        File jsonCredentials = new File(Path.CREDENTIALS_JSON.getPath());
        if(jsonCredentials.exists()) fileListDrive.add(jsonCredentials);
        File storedCredentials  = new File(Path.STOREDCREDENTIALS.getPath());
        if(storedCredentials.exists()) fileListDrive.add(storedCredentials);

        List<File> fileListDoc = new ArrayList<>();
        File admin = new File(Path.ADMIN.getPath());
        if(admin.exists()) fileListDoc.add(admin);
        File keystore = new File(Path.KEYSTORE.getPath());
        if(keystore.exists()) fileListDoc.add(keystore);

        if(fileListDrive.size() < 2){
            if(fileListDrive.size() != 0 && fileListDrive.get(0).getName().equals("credentials.json")){
                Drive service = DriveUtil.getDriveService();
                if(service != null){
                    boolean done = false;
                    try{
                        DriveUtil.createFolder("Students Management System", service);
                        DriveUtil.createFolder("database", "Students Management System", service);
                        DriveUtil.createFolder("doc", "Students Management System", service);
                        DriveUtil.createFolder("image", "Students Management System", service);
                        DriveUtil.createFile("concurrency", "Students Management System", new File(Path.DRIVE_CONCURRENCY.getPath()), service);
                        done = true;
                    }catch(Exception ignored){}
                    if(done){
                        showSetup();
                    }
                }
            }else{
                Alert alert = new Alert(Alert.AlertType.ERROR);
                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new File("image/icon.jpg").toURI().toString()));
                alert.setHeaderText("File Not Found");
                alert.setContentText("credentials.json cannot be found in directory" + "\n" + new File("drive").getAbsolutePath());
                alert.setOnCloseRequest(new EventHandler<DialogEvent>() {
                    @Override
                    public void handle(DialogEvent dialogEvent) {
                        System.exit(4);
                    }
                });
                alert.showAndWait();
            }
        }else if(fileListDoc.size() != 2){
            showSetup();
            Dialog<RadioButton> dialog = new Dialog<>();
            dialog.setHeaderText(null);
            dialog.initOwner(setupStage);
            ButtonType okay = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(okay, ButtonType.CLOSE);
            HBox selectionBox = new HBox(15);
            selectionBox.setPadding(new Insets(30, 30, 28, 28));
            selectionBox.setAlignment(Pos.CENTER);
            Font fontBtn = new Font("Arial", 13);
            RadioButton importBtn = new RadioButton("Import existing data");
            importBtn.setFont(fontBtn);
            RadioButton noBtn = new RadioButton("Continue setup");
            noBtn.setFont(fontBtn);
            selectionBox.getChildren().addAll(importBtn, noBtn);
            Node okayBtn = dialog.getDialogPane().lookupButton(okay);
            okayBtn.setDisable(true);
            EventHandler<ActionEvent> radioBtnEvent = new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent evt) {
                    if(evt.getSource() == importBtn){
                        if(importBtn.isSelected()){
                            if(okayBtn.isDisable()){
                                okayBtn.setDisable(false);
                            }
                            if(noBtn.isSelected()){
                                noBtn.setSelected(false);
                            }
                        }else{
                            okayBtn.setDisable(true);
                        }
                    }
                    if(evt.getSource() == noBtn){
                        if(noBtn.isSelected()){
                            if(okayBtn.isDisable()){
                                okayBtn.setDisable(false);
                            }
                            if(importBtn.isSelected()){
                                importBtn.setSelected(false);
                            }
                        }else{
                            okayBtn.setDisable(true);
                        }
                    }
                }
            };
            importBtn.setOnAction(radioBtnEvent);
            noBtn.setOnAction(radioBtnEvent);
            dialog.getDialogPane().setContent(selectionBox);
            dialog.setResultConverter(dialogBtn -> {
                if(dialogBtn == okay){
                    if(importBtn.isSelected()){
                        return importBtn;
                    }else if(noBtn.isSelected()){
                        return noBtn;
                    }
                }
                return null;
            });
            Optional<RadioButton> result = dialog.showAndWait();
            if(result.isPresent()){
                if(result.get() == importBtn){
                    setupStage.close();
                    showSplash();
                    importData(primaryStage);
                }
            }else{
                System.exit(6);
            }
        }else{
            launchApp();
        }
    }

    private void launchApp() {
        showSplash();
        updateFiles(primaryStage);
    }

    private void showSplash() {
        stageSplash = new Stage();
        stageSplash.getIcons().add(new Image(new File("image/icon.jpg").toURI().toString()));
        stageSplash.initStyle(StageStyle.UNDECORATED);
        VBox root = new VBox();
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(600);
        progressBar.setStyle("-fx-accent: #228B22;");
        ImageView imageView = new ImageView(new File("image/splash.jpg").toURI().toString());
        imageView.setFitHeight(300);
        imageView.setFitWidth(600);
        imageView.setPreserveRatio(false);
        imageView.setSmooth(true);
        imageView.setCache(true);
        root.getChildren().addAll(imageView, progressBar);
        stageSplash.setScene(new Scene(root));
        stageSplash.show();
    }

    private void showSetup() {
        Parent root = Loader.load("setup/setup.fxml");
        setupStage = new Stage();
        setupStage.setTitle("Setup");
        setupStage.getIcons().add(new Image(new File("image/icon.jpg").toURI().toString()));
        if(root != null){
            setupStage.setScene(new Scene(root));
        }
        setupStage.setResizable(false);
        setupStage.show();
    }

    private void updateFiles(Stage stage) {
        Drive service = DriveUtil.getDriveService();
        if(service != null) {
            Task task = new Task<Void>(){
                @Override
                protected Void call() throws Exception {
                    DriveUtil.manageConcurrency(service);
                    if(DriveUtil.hasAccess()){
                        DriveUtil.setAccess(false);
                        int max = 20;
                        double progress = 0;
                        // Updates admin and app.xml files.
                        File docDir = new File(Constants.LOCAL_DOC_FOLDER.getValue());
                        File[] docFiles = docDir.listFiles();
                        if(docFiles != null){
                            for(File file : docFiles){
                                if(file.getName().equals("keystore.pfx")) continue;
                                try{
                                    DriveUtil.downloadFile(file.getName(), Constants.DRIVE_DOC_FOLDER.getValue(), Constants.LOCAL_DOC_FOLDER.getValue(), service);
                                }catch(Exception ignored){}
                                progress += 1;
                                if(progress >= max){
                                    max += 5;
                                }
                                updateProgress(progress, max);
                            }
                        }
                        // Updates databases.
                        File dbDir = new File(Constants.LOCAL_DATABASE_FOLDER.getValue());
                        File[] dbFiles = dbDir.listFiles();
                        if(dbFiles != null){
                            for(File file : dbFiles){
                                if(file.getName().equals("retained")) continue;
                                try{
                                    DriveUtil.downloadFile(file.getName(), Constants.DRIVE_DATABASE_FOLDER.getValue(), Constants.LOCAL_DATABASE_FOLDER.getValue(), service);
                                }catch(Exception ignored){}
                                progress += 1;
                                if(progress >= max){
                                    max += 5;
                                }
                                updateProgress(progress, max);
                            }
                        }
                        // Updates image folder.
                        File imgDir = new File(Constants.LOCAL_IMAGE_FOLDER.getValue());
                        File[] imageFiles = imgDir.listFiles();
                        List<String> imageFileNames = new ArrayList<>();
                        if(imageFiles != null){
                            for(File file : imageFiles){
                                imageFileNames.add(file.getName());
                            }
                        }
                        List<com.google.api.services.drive.model.File> driveImageFiles = null;
                        try{
                            driveImageFiles = DriveUtil.getFilesInFolder(Constants.DRIVE_IMAGE_FOLDER.getValue(), service);
                        }catch(Exception ignored){}
                        progress = max - driveImageFiles.size();
                        updateProgress(progress, max);
                        for(com.google.api.services.drive.model.File driveFile : driveImageFiles){
                            if(!imageFileNames.contains(driveFile.getName())){
                                try{
                                    DriveUtil.downloadFile(driveFile.getName(), Constants.DRIVE_IMAGE_FOLDER.getValue(), Constants.LOCAL_IMAGE_FOLDER.getValue(), service);
                                }catch(Exception ignored){}
                            }
                            progress += 1;
                            updateProgress(progress, max);
                        }

                        try{
                            DriveUtil.createFile("concurrency", Constants.DRIVE_ROOT_FOLDER.getValue(), new File(Path.DRIVE_CONCURRENCY.getPath()), service);
                        }catch(Exception ignored){}

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                stageSplash.close();
                                Parent root = Loader.load(Path.UNLOCK_SECRETKEY_FXML.getPath());
                                if(root != null){
                                    stage.setScene(new Scene(root, WIDTH, HEIGHT));
                                }
                                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                                    @Override
                                    public void handle(WindowEvent windowEvent) {
                                        System.exit(2);
                                    }
                                });
                                stage.show();
                            }
                        });
                    }else{
                        showConnectionFailed();
                    }
                    return null;
                }
            };
            progressBar.progressProperty().bind(task.progressProperty());
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void importData(Stage stage) {
        Drive service = DriveUtil.getDriveService();
        if(service != null){
            Task task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    DriveUtil.manageConcurrency(service);
                    if(DriveUtil.hasAccess()){
                        DriveUtil.setAccess(false);
                        int max = 20;
                        double progress = 0;

                        // Downloads databases.
                        List<com.google.api.services.drive.model.File> dbFiles = null;
                        try{
                            dbFiles = DriveUtil.getFilesInFolder(Constants.DRIVE_DATABASE_FOLDER.getValue(), service);
                        }catch(Exception ignored){}
                        if(dbFiles != null){
                            for(com.google.api.services.drive.model.File dbFile : dbFiles){
                                try{
                                    DriveUtil.downloadFile(dbFile.getName(), Constants.DRIVE_DATABASE_FOLDER.getValue(), Constants.LOCAL_DATABASE_FOLDER.getValue(), service);
                                }catch(Exception ignored){}
                                progress += 1;
                                if(progress >= max){
                                    max += 5;
                                }
                                updateProgress(progress, max);
                            }
                        }

                        // Downloads doc folder contents.
                        List<com.google.api.services.drive.model.File> docFiles = null;
                        try{
                            docFiles = DriveUtil.getFilesInFolder(Constants.DRIVE_DOC_FOLDER.getValue(), service);
                        }catch(Exception ignored){}
                        if(docFiles != null){
                            for(com.google.api.services.drive.model.File docFile : docFiles){
                                try{
                                    DriveUtil.downloadFile(docFile.getName(), Constants.DRIVE_DOC_FOLDER.getValue(), Constants.LOCAL_DOC_FOLDER.getValue(), service);
                                }catch(Exception ignored){}
                                progress += 1;
                                if(progress >= max){
                                    max += 5;
                                }
                                updateProgress(progress, max);
                            }
                        }

                        // Download images.
                        List<com.google.api.services.drive.model.File> imageFiles = null;
                        try{
                            imageFiles = DriveUtil.getFilesInFolder(Constants.DRIVE_DATABASE_FOLDER.getValue(), service);
                        }catch(Exception ignored){}
                        if(imageFiles != null){
                            progress = max - imageFiles.size();
                            updateProgress(progress, max);
                            for(com.google.api.services.drive.model.File imageFile : imageFiles){
                                try{
                                    DriveUtil.downloadFile(imageFile.getName(), Constants.DRIVE_IMAGE_FOLDER.getValue(), Constants.LOCAL_IMAGE_FOLDER.getValue(), service);
                                }catch(Exception ignored){}
                                progress += 1;
                                updateProgress(progress, max);
                            }
                        }

                        try{
                            DriveUtil.createFile("concurrency", Constants.DRIVE_ROOT_FOLDER.getValue(), new File(Path.DRIVE_CONCURRENCY.getPath()), service);
                        }catch(Exception ignored){}

                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                stageSplash.close();
                                Parent root = Loader.load(Path.UNLOCK_SECRETKEY_FXML.getPath());
                                if(root != null){
                                    stage.setScene(new Scene(root, WIDTH, HEIGHT));
                                }
                                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                                    @Override
                                    public void handle(WindowEvent windowEvent) {
                                        System.exit(2);
                                    }
                                });
                                stage.show();
                            }
                        });
                    }else{
                        showConnectionFailed();
                    }
                    return null;
                }
            };
            progressBar.progressProperty().bind(task.progressProperty());
            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    private void showConnectionFailed(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stageSplash.close();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(new Image(new File("image/icon.jpg").toURI().toString()));
                alert.setHeaderText("Connection Failed");
                alert.setContentText("Could not establish connection to google drive");
                alert.getDialogPane().getButtonTypes().remove(0);
                ButtonType close = new ButtonType("Close");
                ButtonType tryAgain = new ButtonType("Try Again");
                alert.getDialogPane().getButtonTypes().addAll(close, tryAgain);
                Optional<ButtonType> result = alert.showAndWait();
                if(result.isPresent()){
                    if(result.get() == tryAgain){
                        launchApp();
                    }else if(result.get() == close){
                        System.exit(5);
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        System.setProperty("java.library.path", "./lib");
        System.setProperty("java.util.logging.config.file", "logging.properties");
        launch(args);
    }

    public static double getWidth() {
        return WIDTH;
    }

    public static double getHeight() {
        return HEIGHT;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setKeyStorePassword(String keyStorePassword) {
        StudentsManagementSystemApp.keyStorePassword = keyStorePassword;
    }

    public static String getKeyStorePassword() {
        return keyStorePassword;
    }

    public static void setSecretKeyEntryPassword(String secretKeyEntryPassword) {
        StudentsManagementSystemApp.secretKeyEntryPassword = secretKeyEntryPassword;
    }

    public static String getSecretKeyEntryPassword() {
        return secretKeyEntryPassword;
    }

}