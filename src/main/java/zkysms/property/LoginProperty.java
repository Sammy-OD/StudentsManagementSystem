package zkysms.property;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import zkysms.StudentsManagementSystemApp;
import zkysms.constants.Constants;
import zkysms.constants.Path;
import zkysms.utils.CryptoUtil;
import zkysms.utils.DatabaseUtil;
import zkysms.utils.LogUtil;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginProperty {

    private static boolean isLoginParametersCorrect = false;
    private static Logger LOGGER = LogUtil.getLOGGER(LoginProperty.class.getName());

    public LoginProperty(){}

    public static boolean verifyTeacherLogin(String username, String password) {
        DatabaseUtil.decryptDB("teacher", StudentsManagementSystemApp.getKeyStorePassword(), StudentsManagementSystemApp.getSecretKeyEntryPassword());
        String query = "SELECT TeacherId, Password FROM info";
        Connection connection = null;
        Statement statement = null;
        ResultSet rs = null;
        try{
            connection = DatabaseUtil.getDBConnection("teacher");
            statement = connection.createStatement();
            rs = statement.executeQuery(query);
            while(rs.next()){
                if(username.contentEquals(rs.getString("TeacherId")) &&
                        password.contentEquals(rs.getString("Password"))){
                    isLoginParametersCorrect = true;
                    break;
                }
            }
        }catch (SQLException e){
            LOGGER.error(e.getMessage(), e);
        }finally {
            try{
                if(rs != null) rs.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            DatabaseUtil.encryptDB("teacher", StudentsManagementSystemApp.getKeyStorePassword(), StudentsManagementSystemApp.getSecretKeyEntryPassword());
        }
        return isLoginParametersCorrect;
    }

    public static boolean verifyAdminLogin(String password) {
        try{
            Cipher cipher = Cipher.getInstance("AES");
            if(CryptoUtil.decrypt(FileUtils.readFileToByteArray(new File("doc/admin")), cipher, Path.KEYSTORE.getPath(), StudentsManagementSystemApp.getKeyStorePassword(), StudentsManagementSystemApp.getSecretKeyEntryPassword(), Constants.SECRET_KEY_ALIAS.getValue()).contentEquals(password)){
                isLoginParametersCorrect = true;
            }
        }catch(NoSuchAlgorithmException | NoSuchPaddingException | IOException e){
            LOGGER.error(e.getMessage(), e);
        }
        return isLoginParametersCorrect;
    }

    public static void setIsLoginParametersCorrect(boolean isLoginParametersCorrect) {
        LoginProperty.isLoginParametersCorrect = isLoginParametersCorrect;
    }

}