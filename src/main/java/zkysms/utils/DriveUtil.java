package zkysms.utils;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import org.slf4j.Logger;
import zkysms.constants.Constants;
import zkysms.constants.Path;
import java.io.*;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DriveUtil {

    private static final String APPLICATION_NAME = "Students Management System";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "drive";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = Path.CREDENTIALS_JSON.getPath();
    private static Drive driveService;
    private static boolean hasAccess = false;
    private static Logger LOGGER = LogUtil.getLOGGER(DriveUtil.class.getName());

    public static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) {

        try {
            InputStream in = new FileInputStream(CREDENTIALS_FILE_PATH);
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        }catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }

    }

    public static Drive getDriveService() {
        if(driveService == null) {
            try {
                NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
                driveService = new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                        .setApplicationName(APPLICATION_NAME)
                        .build();
                return driveService;
            }catch(GeneralSecurityException | IOException e) {
                LOGGER.error(e.getMessage(), e);
                return null;
            }
        }else {
            return driveService;
        }
    }

    public static void createFolder(String folderName, Drive driveService) {
        File fileMetadata = new File();
        fileMetadata.setName(folderName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        try {
            driveService.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
        } catch(IOException e){
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void createFolder(String folderName, String parentFolder, Drive driveService) {
        String folderId = getFolder(parentFolder, driveService, true).getId();
        File fileMetadata = new File();
        fileMetadata.setName(folderName);
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        fileMetadata.setParents(Collections.singletonList(folderId));
        try {
            driveService.files().create(fileMetadata)
                    .setFields("id, parents")
                    .execute();
        }catch(IOException e){
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static boolean createFile(String fileName, String parentFolder, java.io.File fileToCreate, Drive driveService) {
        String folderId = getFolder(parentFolder, driveService, true).getId();
        File fileMetadata = new File();
        fileMetadata.setName(fileName);
        fileMetadata.setParents(Collections.singletonList(folderId));
        FileContent fileContent = new FileContent("", fileToCreate);
        try {
            driveService.files().create(fileMetadata, fileContent)
                    .setFields("id, parents")
                    .execute();
            return true;
        }catch(IOException e){
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    public static File getFolder(String folderName, Drive driveService, boolean log) {
        File returnFile = null;
        String pageToken = null;
        do {
            try {
                FileList fileList = driveService.files().list()
                        .setQ("mimeType='application/vnd.google-apps.folder'")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken).execute();
                for(File file : fileList.getFiles()) {
                    if(file.getName().contentEquals(folderName)) {
                        returnFile = file;
                    }
                }
            }catch(IOException e){
                if(log){
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }while(pageToken != null);
        return returnFile;
    }

    public static File getFile(String fileName, String parentFolder, Drive driveService, boolean log) {
        File returnFile = null;
        String pageToken = null;
        do {
            try {
                FileList fileList = driveService.files().list()
                        .setQ("'" + getFolder(parentFolder, driveService, log).getId() + "' in parents and mimeType!='application/vnd.google-apps.folder'")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken).execute();
                for(File file : fileList.getFiles()) {
                    if(file.getName().contentEquals(fileName)) {
                        returnFile = file;
                    }
                }
            }catch(IOException e){
                if(log){
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }while(pageToken != null);
        return returnFile;
    }

    public static boolean downloadFile(String fileName, String parentFolder, String downloadPath, Drive driveService) {
        File file = getFile(fileName, parentFolder, driveService, true);
        String id = file.getId();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(downloadPath + "/" + file.getName());
            inputStream = driveService.files().get(id).executeMedia().getContent();
            int len;
            byte[] b = new byte[1024];
            while((len = inputStream.read(b)) != -1) {
                outputStream.write(b, 0, len);
            }
            return true;
        } catch(IOException e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        } finally {
            try {
                if(outputStream != null) outputStream.close();
                if(inputStream != null) inputStream.close();
            } catch(IOException e){
                e.printStackTrace();
            }
        }
    }

    public static boolean deleteFile(String fileName, String parentFolder, Drive driveService, boolean log) {
        String fileId = getFile(fileName, parentFolder, driveService, log).getId();
        try {
            driveService.files().delete(fileId).execute();
            return true;
        }catch(IOException e){
            if(log){
                LOGGER.error(e.getMessage(), e);
            }
            return false;
        }
    }

    public static List<File> getFilesInFolder(String folderName, Drive driveService) {
        String folderId = getFolder(folderName, driveService, true).getId();
        List<File> result = new ArrayList<>();
        String pageToken = null;
        do {
            try {
                FileList fileList = driveService.files().list()
                        .setQ("'" + folderId + "' in parents and mimeType!='application/vnd.google-apps.folder'")
                        .setSpaces("drive")
                        .setFields("nextPageToken, files(id, name)")
                        .setPageToken(pageToken).execute();
                for(File file : fileList.getFiles()) {
                    result.add(file);
                }
            }catch(IOException e){
                LOGGER.error(e.getMessage(), e);
            }
        }while(pageToken != null);
        return result;
    }

    public static void manageConcurrency(Drive driveService){
        long start = System.currentTimeMillis();
        long time = start;
        do{
            boolean delete = false;
            try {
                delete = deleteFile("concurrency", Constants.DRIVE_ROOT_FOLDER.getValue(), driveService, false);
            }catch(Exception ignored){}
           if(delete){
               hasAccess= true;
               break;
           }
           time = System.currentTimeMillis();
        }while(time <= start + 18000);
    }

    public static boolean hasAccess() {
        return hasAccess;
    }

    public static void setAccess(boolean hasAccess) {
        DriveUtil.hasAccess = hasAccess;
    }

}