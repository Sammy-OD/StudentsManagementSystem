package stdmansys;

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
        String query = "SELECT TeacherId, FirstName, LastName, UserType FROM info WHERE Username = ?";
        try(Connection connection = DatabaseUtil.getDBConnection("user");
            PreparedStatement preparedStatement = connection.prepareStatement(query)){
            preparedStatement.setString(1, username);
            ResultSet rs = preparedStatement.executeQuery();
            teacherId = rs.getString("TeacherId");
            firstName = rs.getString("FirstName");
            lastName = rs.getString("LastName");
            if(rs.getString("UserType").contentEquals("Admin")){
                isAdmin = true;
            }
        }catch (SQLException e){
            e.printStackTrace();
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