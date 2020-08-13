module stdmansys {
    requires javafx.controls;
    requires javafx.fxml;

    opens stdmansys to javafx.fxml;
    exports stdmansys;
    opens stdmansys.startpage to javafx.fxml;
    exports stdmansys.startpage;
}