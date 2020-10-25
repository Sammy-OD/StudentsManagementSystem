package zkysms.ui.homepage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import zkysms.ui.Loader;
import zkysms.constants.Path;
import zkysms.property.UserProperty;
import zkysms.constants.Constants;
import zkysms.utils.XMLUtil;
import javafx.scene.image.ImageView;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {

    @FXML
    private Button regStdBtn, beginSessionBtn, beginTermBtn, rsltUpBtn, viewGrdShtBtn, viewStdRepBtn, searchBtn;
    @FXML
    private Label userLabel;
    @FXML
    private ContextMenu contxtMenuUser;
    @FXML
    private MenuItem logoutMenuItem, regNewTeacherMenuItem;
    @FXML
    private HBox userLabelHBox;
    @FXML
    private Text schoolName;
    @FXML
    private ImageView schlogo;

    @FXML
    private void handleOnMouseClick(MouseEvent evt) {
        if(evt.getSource() == userLabel){
            contxtMenuUser.show(userLabel, Side.LEFT, 0, userLabel.getHeight());
        }
    }

    @FXML
    private void handleOnAction(ActionEvent evt) {
        if(evt.getSource() == regStdBtn){
            Stage stage = (Stage) regStdBtn.getScene().getWindow();
            Parent root = Loader.load(Path.STUDENT_REG_FORM.getPath());
            stage.getScene().setRoot(root);
            stage.show();
        }

        if(evt.getSource() == logoutMenuItem){
            UserProperty.setIsAdmin(false);
            Stage stage = (Stage) userLabelHBox.getScene().getWindow();
            Parent root = Loader.load(Path.LOGIN_PAGE.getPath());
            stage.getScene().setRoot(root);
            stage.show();
        }
        if(evt.getSource() == regNewTeacherMenuItem){
            Stage stage = (Stage) userLabelHBox.getScene().getWindow();
            Parent root = Loader.load(Path.TEACHER_REG_FORM.getPath());
            stage.getScene().setRoot(root);
            stage.show();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Document doc = XMLUtil.loadXML(Path.APP_XML.getPath());
        File logo = new File(doc.getElementsByTagName(Constants.SCHOOL_LOGO_TAG.getTag().getTagName()).item(Constants.SCHOOL_LOGO_TAG.getTag().getIndex()).getTextContent());
        File defaultLogo = new File("image/logo@.png");
        Image image;
        if(logo.exists()){
            image = new Image(logo.toURI().toString());
        }else{
            image = new Image(defaultLogo.toURI().toString());
        }
        schlogo.setImage(image);
        schoolName.setText(doc.getElementsByTagName(Constants.SCHOOL_NAME_TAG.getTag().getTagName()).item(Constants.SCHOOL_NAME_TAG.getTag().getIndex()).getTextContent());
        // Sets userLabel text.
        if(!UserProperty.getIsAdmin()){
            userLabel.setText(UserProperty.getFirstName().substring(0,1)
                    + UserProperty.getLastName().substring(0,1));
        }else{
            userLabel.setText("Ad");
        }
        // Disables Admin features for non Admin.
        if(!UserProperty.getIsAdmin()){
            regNewTeacherMenuItem.setVisible(false);
        }
    }

}