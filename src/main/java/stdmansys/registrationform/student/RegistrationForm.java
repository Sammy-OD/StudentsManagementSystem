package stdmansys.registrationform.student;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import org.w3c.dom.Document;
import stdmansys.constants.Path;
import stdmansys.constants.XMLConstants;
import stdmansys.constants.SessionConstants;
import stdmansys.property.SessionProperty;
import stdmansys.utils.DatabaseUtil;
import stdmansys.utils.XMLUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationForm {

    private static Node rootNode;
    private String studentId, firstName, lastName, middleName, dob, gender, $class, department, subjects, religion, nationality, address, pSchool, fatherName, motherName, fOccupation, mOccupation, fPhone, mPhone, fEmail, mEmail, imageName;

    public RegistrationForm(){}

    public boolean submitForm(){
        DatabaseUtil.decryptDB("student");
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
                            subjects = "Mathematics, English, " + doc.getElementsByTagName(XMLConstants.PRIMARY_SUBJECTS.getTag().getTagName()).item(XMLConstants.PRIMARY_SUBJECTS.getTag().getIndex()).getTextContent();
                        }
                    }else if($class.equals("JS 1") || $class.equals("JS 2") || $class.equals("JS 3")){
                        if(doc != null){
                            subjects = "Mathematics, English, " + doc.getElementsByTagName(XMLConstants.JUNIOR_SUBJECTS.getTag().getTagName()).item(XMLConstants.JUNIOR_SUBJECTS.getTag().getIndex()).getTextContent();
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
            }else{
                preparedStatement.setString(22, "NOIMAGE");
            }
            preparedStatement.executeUpdate();
            return true;
        }catch (SQLException e){
            System.out.println(e.getMessage());
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
            DatabaseUtil.encryptDB("student");
        }
    }

    public String generateStudentId(ComboBox $class){
        String studentId;
        if($class.getValue().toString().equals("Primary 1")){
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
        }else if($class.getValue().toString().equals("Primary 2")){
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
        }else if($class.getValue().toString().equals("Primary 3")){
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
        }else if($class.getValue().toString().equals("Primary 4")){
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
        }else if($class.getValue().toString().equals("Primary 5")){
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
        }else if($class.getValue().toString().equals("Primary 6")){
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
        }else if($class.getValue().toString().equals("JS 1")){
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
        }else if($class.getValue().toString().equals("JS 2")){
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
        }else if($class.getValue().toString().equals("JS 3")){
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
        }else if($class.getValue().toString().equals("SS 1")){
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
        }else if($class.getValue().toString().equals("SS 2")){
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
        }else if($class.getValue().toString().equals("SS 3")){
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

    public static Node getRootNode(){
        return rootNode;
    }

    public static void setRootNode(Node rootNode){
        RegistrationForm.rootNode = rootNode;
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