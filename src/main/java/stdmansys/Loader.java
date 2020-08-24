package stdmansys;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.io.IOException;

public class Loader {

    /**
     * Loads FXML file.
     * @param path a relative path to the FXML file.
     * @return a Parent node.
     */
    public static Parent load(String path) {
        try{
            FXMLLoader loader = new FXMLLoader();
            return loader.load(Loader.class.getResource(path));
        }catch (IOException e){
            e.printStackTrace();
            return null;
        }
    }

}