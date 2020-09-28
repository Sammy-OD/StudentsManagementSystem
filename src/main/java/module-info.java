module stdmansys {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;
    requires org.controlsfx.controls;
    requires sqlite.jdbc;
    requires java.sql;
    requires webcam.capture;
    requires bridj;
    requires slf4j.api;
    requires slf4j.nop;
    requires org.apache.commons.io;
    requires commons.validator;

    opens stdmansys to javafx.fxml;
    exports stdmansys;
    opens stdmansys.homepage to javafx.fxml;
    opens stdmansys.registrationform.student to javafx.fxml;
    opens stdmansys.registrationform.teacher to javafx.fxml;
    opens stdmansys.loginpage to javafx.fxml;
    opens stdmansys.setup to javafx.fxml;
}