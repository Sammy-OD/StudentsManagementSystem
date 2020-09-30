package stdmansys.constants;

public enum Path {

    LOGIN_PAGE("loginpage/loginpage.fxml"), HOME_PAGE("homepage/homepage.fxml"), TEACHER_REG_FORM("registrationform/teacher/registrationform.fxml"), STUDENT_REG_FORM("registrationform/student/registrationform.fxml"),
    APP_XML("doc/app.xml");

    private final String path;

    Path(String path){
        this.path = path;
    }


    public String getPath() {
        return path;
    }

}