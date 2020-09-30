package stdmansys.property;

import org.apache.commons.io.FileUtils;
import stdmansys.utils.DatabaseUtil;
import stdmansys.utils.PasswordUtil;
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

    public LoginProperty(){}

    public static boolean verifyTeacherLogin(String username, String password) {
        DatabaseUtil.decryptDB("teacher");
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
            e.printStackTrace();
        }finally {
            try{
                if(rs != null) rs.close();
                if(statement != null) statement.close();
                if(connection != null) connection.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
            DatabaseUtil.encryptDB("teacher");
        }
        return isLoginParametersCorrect;
    }

    public static boolean verifyAdminLogin(String password) {
        try{
            Cipher cipher = Cipher.getInstance("AES");
            if(PasswordUtil.decryptPassword(FileUtils.readFileToByteArray(new File("doc/admin")), cipher).contentEquals(password)){
                isLoginParametersCorrect = true;
            }
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
        }catch(NoSuchPaddingException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }
        return isLoginParametersCorrect;
    }

    public static void setIsLoginParametersCorrect(boolean isLoginParametersCorrect) {
        LoginProperty.isLoginParametersCorrect = isLoginParametersCorrect;
    }

}