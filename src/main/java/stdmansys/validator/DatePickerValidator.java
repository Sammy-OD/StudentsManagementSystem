package stdmansys.validator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DatePickerValidator {

    private final Pane pane;
    private final Map<DatePicker, Label> map;
    private final double x, y;

    public DatePickerValidator(Pane pane, double x, double y) {
        this.x = x;
        this.y = y;
        this.pane = pane;
        map = new HashMap<>();
    }

    public void registerEmptyValidation(DatePicker datePicker, String message) {
        if(!map.containsKey(datePicker)){
            add(datePicker);
            datePicker.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean focusLost, Boolean focusGained) {
                    if(focusLost && datePicker.getValue() == null){
                        map.get(datePicker).setText(message);
                        pane.getChildren().add(map.get(datePicker));
                    }else if(focusGained && map.get(datePicker).isVisible()){
                        pane.getChildren().remove(map.get(datePicker));
                    }
                }
            });
        }
    }

    public void registerRegexValidation(DatePicker datePicker, String message, String regex) {
        if(!map.containsKey(datePicker)){
            add(datePicker);
            datePicker.getEditor().textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(newValue);
                    boolean didFind = matcher.find();
                    if(didFind && !pane.getChildren().contains(map.get(datePicker))){
                        map.get(datePicker).setText(message);
                        pane.getChildren().add(map.get(datePicker));
                    }else if(!didFind && pane.getChildren().contains(map.get(datePicker))){
                        pane.getChildren().remove(map.get(datePicker));
                    }
                }
            });
        }
    }

    private void add(DatePicker datePicker) {
        Label label = new Label();
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
        Point2D points = datePicker.localToScene(x, y);
        label.setLayoutX(points.getX());
        label.setLayoutY(points.getY());
        datePicker.layoutXProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                Point2D points = datePicker.localToScene(x, y);
                label.setLayoutX(points.getX());
            }
        });
        datePicker.layoutYProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                Point2D points = datePicker.localToScene(x, y);
                label.setLayoutY(points.getY());
            }
        });
        map.put(datePicker, label);
    }

}