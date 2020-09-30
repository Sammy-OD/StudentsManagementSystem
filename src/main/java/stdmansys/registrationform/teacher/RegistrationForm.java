package stdmansys.registrationform.teacher;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import stdmansys.constants.SessionConstants;
import stdmansys.property.SessionProperty;
import stdmansys.utils.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationForm {

    private static Node rootNode;
    private String firstName, lastName, middleName, title, religion, nationality, dob, address, email, phoneNumber, imageName;

    public RegistrationForm(){}

    public boolean submitForm(){
        DatabaseUtil.decryptDB("teacher");
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String query = "INSERT INTO info(TeacherId, FirstName, LastName, MiddleName, Title, Religion, Nationality, " +
                            "DOB, Address, Email, PhoneNumber, ImageName) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try{
            connection = DatabaseUtil.getDBConnection("teacher");
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, generateTeacherId());
            preparedStatement.setString(2, firstName);
            preparedStatement.setString(3, lastName);
            preparedStatement.setString(4, middleName);
            preparedStatement.setString(5, title);
            preparedStatement.setString(6, religion);
            preparedStatement.setString(7, nationality);
            preparedStatement.setString(8, dob);
            preparedStatement.setString(9, address);
            preparedStatement.setString(10, email);
            preparedStatement.setString(11, phoneNumber);
            if(imageName != null){
                preparedStatement.setString(12, imageName);
            }else{
                preparedStatement.setString(12, "NOIMAGE");
            }
            preparedStatement.executeUpdate();
            return true;
        }catch (SQLException e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(Integer.toString(e.getErrorCode()));
            alert.showAndWait();
            return false;
        }finally {
            try{
                if(preparedStatement != null) preparedStatement.close();
                if(connection != null) connection.close();
            }catch(SQLException e){
                e.printStackTrace();
            }
            DatabaseUtil.encryptDB("teacher");
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

    public static Node getRootNode(){
        return rootNode;
    }

    public static void setRootNode(Node rootNode){
        RegistrationForm.rootNode = rootNode;
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

    public void setReligion(String religion){
        this.religion = religion;
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