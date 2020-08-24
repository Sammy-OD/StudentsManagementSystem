package stdmansys.registrationform;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import stdmansys.Loader;

import java.net.URL;
import java.util.ResourceBundle;

public class RegistrationFormController implements Initializable {

    @FXML
    private ImageView backIcon;
    @FXML
    private ComboBox departmentComboBox, classComboBox;
    @FXML
    private Label departmentLabel, subjectLabel;
    @FXML
    private CheckComboBox subjectChkComboBox;

    @FXML
    private void handleOnMouseClicked(MouseEvent evt) {
        if(evt.getSource() == backIcon){
            Stage stage = (Stage) backIcon.getScene().getWindow();
            Parent root = Loader.load("startpage/startpage.fxml");
            stage.getScene().setRoot(root);
            stage.show();
        }
    }

    @FXML
    private void handleOnAction(ActionEvent evt) {
        if(evt.getSource() == classComboBox){
            String selectedItem = (String) classComboBox.getValue();
            if(selectedItem.contentEquals("SS 1") || selectedItem.contentEquals("SS 2") || selectedItem.contentEquals("SS 3")){
                // Initializes department combo box.
                String[] department = {"Science", "Social Science", "Arts"};
                departmentComboBox.setItems(FXCollections.observableArrayList(department));
            }
            if(!selectedItem.contentEquals("SS 1") && !selectedItem.contentEquals("SS 2") && !selectedItem.contentEquals("SS 3")){
                // Removes all items from department combo box.
                departmentComboBox.getItems().clear();
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initializes class combo box.
        String[] $class = {"Primary 1", "Primary 2", "Primary 3", "Primary 4", "Primary 5", "Primary 6",
                                "JS 1", "JS 2", "JS 3", "SS 1", "SS 2", "SS 3"};
        classComboBox.setItems(FXCollections.observableArrayList($class));
    }

}
