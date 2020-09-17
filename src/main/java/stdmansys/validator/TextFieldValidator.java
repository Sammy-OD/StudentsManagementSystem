package stdmansys.validator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFieldValidator {

    private final Pane pane;
    private final Map<TextField, Label> map;
    private final double x, y;

    public TextFieldValidator(Pane pane, double x, double y) {
        this.x = x;
        this.y = y;
        this.pane = pane;
        map = new HashMap<>();
    }

    public void registerEmptyValidation(TextField textField, String message) {
        if(!map.containsKey(textField)){
            add(textField);
            textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean focusLost, Boolean focusGained) {
                    if(focusLost && textField.getText().isEmpty()){
                        map.get(textField).setText(message);
                        pane.getChildren().add(map.get(textField));
                    }else if(focusGained && map.get(textField).isVisible()){
                        pane.getChildren().remove(map.get(textField));
                    }
                }
            });
        }
    }

    public void registerRegexValidation(TextField textField, String message, String regex) {
        if(!map.containsKey(textField)){
            add(textField);
            textField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(newValue);
                    boolean didFind = matcher.find();
                    if(didFind && !pane.getChildren().contains(map.get(textField))){
                        map.get(textField).setText(message);
                        pane.getChildren().add(map.get(textField));
                    }else if(!didFind && pane.getChildren().contains(map.get(textField))){
                        pane.getChildren().remove(map.get(textField));
                    }
                }
            });
        }
    }

    private void add(TextField textField) {
        Label label = new Label();
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
        Point2D points = textField.localToScene(x, y);
        label.setLayoutX(points.getX());
        label.setLayoutY(points.getY());
        textField.layoutXProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                Point2D points = textField.localToScene(x, y);
                label.setLayoutX(points.getX());
            }
        });
        textField.layoutYProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                Point2D points = textField.localToScene(x, y);
                label.setLayoutY(points.getY());
            }
        });
        map.put(textField, label);
    }

}