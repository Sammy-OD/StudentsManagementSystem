module stdmansys {
    requires javafx.controls;
    requires javafx.fxml;

    opens stdmansys to javafx.fxml;
    exports stdmansys;
    opens stdmansys.homepage to javafx.fxml;
    exports stdmansys.homepage;
}