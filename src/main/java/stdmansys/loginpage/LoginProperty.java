package stdmansys.loginpage;

import stdmansys.utils.DatabaseUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class LoginProperty {

    private static boolean isLoginCredentialsCorrect = false;

    public LoginProperty(){}

    public static boolean verifyLoginCredentials(String username, String password) {
        String query = "SELECT Username, Password FROM info";
        try(Connection connection = DatabaseUtil.getDBConnection("user");
        Statement statement = connection.createStatement()){
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()){
                if(username.contentEquals(rs.getString("Username")) &&
                        password.contentEquals(rs.getString("Password"))){
                    isLoginCredentialsCorrect = true;
                    break;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return isLoginCredentialsCorrect;
    }

    public static void setIsLoginCredentialsCorrect(boolean isLoginCredentialsCorrect) {
        LoginProperty.isLoginCredentialsCorrect = isLoginCredentialsCorrect;
    }

}