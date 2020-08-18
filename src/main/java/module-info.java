module stdmansys {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;

    opens stdmansys to javafx.fxml;
    exports stdmansys;
    opens stdmansys.startpage to javafx.fxml;
    exports stdmansys.startpage;
    opens stdmansys.registrationform to javafx.fxml;
    exports stdmansys.registrationform;
}