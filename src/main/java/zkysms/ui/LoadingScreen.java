package zkysms.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

public class LoadingScreen {

    private String message;
    private Label label;
    private HBox root;
    private Pane pane;

    public LoadingScreen(String message, Pane pane) {
        this.message = message;
        this.pane = pane;

        root = new HBox();
        root.setAlignment(Pos.CENTER);
        root.setSpacing(15);
        root.setPadding(new Insets(15, 15, 15, 15));

        label = new Label(message);
        label.setFont(new Font("Arial", 18));
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setStyle("-fx-accent: #228B22;");

        root.getChildren().addAll(label, progressIndicator);
        AnchorPane.setLeftAnchor(root, 0.0);
        AnchorPane.setRightAnchor(root, 0.0);
        AnchorPane.setTopAnchor(root, 0.0);
        AnchorPane.setBottomAnchor(root, 150.0);
    }

    public void show() {
        pane.getChildren().add(root);
    }

    public void close() {
        pane.getChildren().remove(root);
    }

    public void setMessage(String message) {
        label.setText(message);
    }

}
