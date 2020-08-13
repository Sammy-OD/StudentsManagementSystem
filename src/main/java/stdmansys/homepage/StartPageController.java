package stdmansys.homepage;

import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;

public class StartPageController {

    @FXML
    private Label user;

    @FXML
    private ContextMenu contxtMenuUser;

    @FXML
    private void handleUserLabelOnClick(){
        contxtMenuUser.show(user, Side.LEFT, 0, user.getHeight());
    }

}
