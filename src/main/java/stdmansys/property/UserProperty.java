package stdmansys.property;

import stdmansys.utils.DatabaseUtil;
import java.sql.*;

/**
 * Stores current user properties.
 */
public class UserProperty {

    private static String teacherId, firstName, lastName;
    private static boolean isAdmin = false;

    public UserProperty() {}

    /**
     * Sets current user properties.
     * @param username current user username.
     */
    public static void setProperty(String username) {
        DatabaseUtil.decryptDB("teacher");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        String query = "SELECT TeacherId, FirstName, LastName FROM info WHERE TeacherId = ?";
        try{
            connection = DatabaseUtil.getDBConnection("teacher");
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            rs = preparedStatement.executeQuery();
            teacherId = rs.getString("TeacherId");
            firstName = rs.getString("FirstName");
            lastName = rs.getString("LastName");
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try{
                if(rs != null) rs.close();
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
            DatabaseUtil.encryptDB("teacher");
        }
    }

    public static String getFirstName() {
        return firstName;
    }

    public static String getLastName() {
        return lastName;
    }

    public static String getTeacherId() {
        return teacherId;
    }

    public static boolean getIsAdmin() {
        return isAdmin;
    }

    public static void setIsAdmin(boolean isAdmin) {
        UserProperty.isAdmin = isAdmin;
    }

}