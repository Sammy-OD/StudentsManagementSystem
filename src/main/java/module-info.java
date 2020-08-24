module stdmansys {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires sqlite.jdbc;
    requires java.sql;

    opens stdmansys to javafx.fxml;
    exports stdmansys;
    opens stdmansys.startpage to javafx.fxml;
    opens stdmansys.registrationform.student to javafx.fxml;
    opens stdmansys.registrationform.teacher to javafx.fxml;
    opens stdmansys.loginpage to javafx.fxml;
    opens stdmansys.signupform to javafx.fxml;
}