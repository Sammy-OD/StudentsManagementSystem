module zkyellow {
    requires javafx.controls;
    requires javafx.fxml;

    opens zkyellow to javafx.fxml;
    exports zkyellow;
}