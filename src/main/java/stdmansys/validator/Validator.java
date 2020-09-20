package stdmansys.validator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Point2D;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator<T> {

    private final Pane pane;
    private final double x, y;
    private final Map<T, Label> map1;
    private final Map<T, String> map2;
    private final Map<T, String> map3;

    public Validator(Pane pane, double x, double y){
        this.pane = pane;
        this.x = x;
        this.y = y;
        map1 = new HashMap<>();
        map2 = new HashMap<>();
        map3 = new HashMap<>();
    }

    public void registerEmptyValidation(T control, String message) {
        add(control);
        map2.put(control, message);
    }

    public void registerRegexValidation(T control, String message, String regex) {
        String merge = message + ",   " + regex;  // Merges message and regex separating with a comma and three white spaces.
        add(control);
        map3.put(control, merge);
    }

    public boolean validate() {
        List<Label> labels = new ArrayList<>();
        Set<T> controls = map1.keySet();
        Iterator<T> iterator = controls.iterator();
        while(iterator.hasNext()){
            Object object = iterator.next();
            if(object instanceof TextField){
                if(map2.containsKey(object) && ((TextField) object).getText().isEmpty()){
                    map1.get(object).setText(map2.get(object));
                    pane.getChildren().add(map1.get(object));
                    labels.add(map1.get(object));
                }else if(map3.containsKey(object)){
                    Pattern pattern = Pattern.compile(map3.get(object).split(",   ")[1]);
                    Matcher matcher = pattern.matcher(((TextField) object).getText());
                    if(!matcher.matches()){
                        map1.get(object).setText(map3.get(object).split(",   ")[0]);
                        pane.getChildren().add(map1.get(object));
                        labels.add(map1.get(object));
                    }
                }
            }else if(object instanceof DatePicker)
                if (map2.containsKey(object) && ((DatePicker) object).getValue() == null) {
                    map1.get(object).setText(map2.get(object));
                    pane.getChildren().add(map1.get(object));
                    labels.add(map1.get(object));
                }else if (map3.containsKey(object)) {
                    if(((DatePicker) object).getValue() != null){
                        Pattern pattern = Pattern.compile(map3.get(object).split(",   ")[1]);
                        Matcher matcher = pattern.matcher(((DatePicker) object).getValue().toString());
                        if (!matcher.matches()) {
                            map1.get(object).setText(map3.get(object).split(",   ")[0]);
                            pane.getChildren().add(map1.get(object));
                            labels.add(map1.get(object));
                        }
                    }
                }
        }
        if(labels.isEmpty()){
            return true;
        }
        return false;
    }

    private void add(T control) {
        Label label = new Label();
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
        Point2D points = ((Control) control).localToScene(x, y);
        label.setLayoutX(points.getX());
        label.setLayoutY(points.getY());
        ((Control) control).focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean focusLost, Boolean focusGained) {
                if(focusGained && pane.getChildren().contains(map1.get(control))) {
                    pane.getChildren().remove(map1.get(control));
                }
            }
        });
        ((Control) control).layoutXProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                Point2D points = ((Control) control).localToScene(x, y);
                label.setLayoutX(points.getX());
            }
        });
        ((Control) control).layoutYProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                Point2D points = ((Control) control).localToScene(x, y);
                label.setLayoutY(points.getY());
            }
        });
        map1.put(control, label);
    }

}