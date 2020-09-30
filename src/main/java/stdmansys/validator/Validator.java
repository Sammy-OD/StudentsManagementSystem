package stdmansys.validator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import org.apache.commons.validator.GenericValidator;
import org.controlsfx.control.CheckComboBox;
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
        if(!map1.containsKey(control)){
            add(control);
        }
        map2.put(control, message);
    }

    public void registerRegexValidation(T control, String message, String regex) {
        if(control instanceof ComboBox || control instanceof CheckComboBox){
            System.out.println("Cannot Register Validation");
            // Create Exception.
        }else{
            String merge = message + ",   " + regex;  // Merges message and regex separating with a comma and three white spaces.
            if(!map1.containsKey(control)){
                add(control);
            }
            map3.put(control, merge);
        }
    }

    public boolean validate() {
        List<Label> labels = new ArrayList<>();
        Set<T> controls = map1.keySet();
        Iterator<T> iterator = controls.iterator();
        while(iterator.hasNext()) {
            Object object = iterator.next();
            if (object instanceof TextField) {
                if (map2.containsKey(object) && ((TextField) object).getText().isEmpty()) {
                    if(!pane.getChildren().contains(map1.get(object))) {
                        map1.get(object).setText(map2.get(object));
                        pane.getChildren().add(map1.get(object));
                    }
                    labels.add(map1.get(object));
                }else if(map3.containsKey(object)) {
                    if(!((TextField) object).getText().isEmpty()){
                        Pattern pattern = Pattern.compile(map3.get(object).split(",   ")[1]);
                        Matcher matcher = pattern.matcher(((TextField) object).getText());
                        if(!matcher.matches()) {
                            if(!pane.getChildren().contains(map1.get(object))) {
                                map1.get(object).setText(map3.get(object).split(",   ")[0]);
                                pane.getChildren().add(map1.get(object));
                            }
                            labels.add(map1.get(object));
                        }
                    }

                }
            }else if(object instanceof DatePicker){
                if(map2.containsKey(object) && ((DatePicker) object).getEditor().getText().isEmpty()) {
                    if(!pane.getChildren().contains(map1.get(object))) {
                        map1.get(object).setText(map2.get(object));
                        pane.getChildren().add(map1.get(object));
                    }
                    labels.add(map1.get(object));
                }else if(map3.containsKey(object)) {
                    if(!((DatePicker) object).getEditor().getText().isEmpty()) {
                        Pattern pattern = Pattern.compile(map3.get(object).split(",   ")[1]);
                        Matcher matcher = pattern.matcher(((DatePicker) object).getEditor().getText());
                        if(!matcher.matches()) {
                            if(!pane.getChildren().contains(map1.get(object))) {
                                map1.get(object).setText(map3.get(object).split(",   ")[0]);
                                pane.getChildren().add(map1.get(object));
                            }
                            labels.add(map1.get(object));
                        }else if(matcher.matches() && !GenericValidator.isDate(((DatePicker) object).getEditor().getText(), "dd-MM-yyyy", true)) {
                            if(!pane.getChildren().contains(map1.get(object))) {
                                map1.get(object).setText("Invalid Date");
                                pane.getChildren().add(map1.get(object));
                            }
                            labels.add(map1.get(object));
                        }
                    }
                }
            }else if(object instanceof ComboBox){
                if (map2.containsKey(object) && ((ComboBox) object).getValue() == null) {
                    if (!pane.getChildren().contains(map1.get(object))) {
                        map1.get(object).setText(map2.get(object));
                        pane.getChildren().add(map1.get(object));
                    }
                    labels.add(map1.get(object));
                }
            }else if(object instanceof CheckComboBox){
                if (map2.containsKey(object) && ((CheckComboBox) object).getCheckModel().getCheckedItems().isEmpty()) {
                    if (!pane.getChildren().contains(map1.get(object))) {
                        map1.get(object).setText(map2.get(object));
                        pane.getChildren().add(map1.get(object));
                    }
                    labels.add(map1.get(object));
                }
            }
            // Awaiting trial.....
            /**else if(object instanceof PasswordField){
                if(map2.containsKey(object) && ((PasswordField) object).getText().isEmpty()) {
                    if (!pane.getChildren().contains(map1.get(object))) {
                        map1.get(object).setText(map2.get(object));
                        pane.getChildren().add(map1.get(object));
                    }
                    labels.add(map1.get(object));
                }else if(map3.containsKey(object)) {
                    Pattern pattern = Pattern.compile(map3.get(object).split(",   ")[1]);
                    Matcher matcher = pattern.matcher(((PasswordField) object).getText());
                    if(!matcher.matches()) {
                        if(!pane.getChildren().contains(map1.get(object))) {
                            map1.get(object).setText(map3.get(object).split(",   ")[0]);
                            pane.getChildren().add(map1.get(object));
                        }
                        labels.add(map1.get(object));
                    }
                }
            }**/
        }
        if(labels.isEmpty()){
            return true;
        }
        return false;
    }

    private void add(T control) {
        Label label = new Label();
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
        Point2D points = ((Control) control).localToParent(x, y);
        label.setLayoutX(points.getX());
        label.setLayoutY(points.getY());
        if(control instanceof CheckComboBox){
            ((CheckComboBox) control).getCheckModel().getCheckedItems().addListener(new ListChangeListener() {
                @Override
                public void onChanged(Change change) {
                    if(pane.getChildren().contains(map1.get(control))){
                        removeLabel(control);
                    }
                }
            });
        }else{
            ((Control) control).focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean focusLost, Boolean focusGained) {
                    if(focusGained && pane.getChildren().contains(map1.get(control))) {
                        removeLabel(control);
                    }
                }
            });
        }
        ((Control) control).layoutXProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                Point2D points = ((Control) control).localToParent(x, y);
                label.setLayoutX(points.getX());
            }
        });
        ((Control) control).layoutYProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                Point2D points = ((Control) control).localToParent(x, y);
                label.setLayoutY(points.getY());
            }
        });
        map1.put(control, label);
    }

    public void removeLabel(T control) {
        if(map1.containsKey(control)){
            pane.getChildren().remove(map1.get(control));
        }
    }

    public void removeAllLabel(T... control) {
        for(int i = 0; i < control.length; i++){
            if(map1.containsKey(control[i])){
                pane.getChildren().remove(control[i]);
            }
        }
    }

    public void removeValidation(T control) {
        if(map1.containsKey(control)){
            pane.getChildren().remove(map1.get(control));
            map1.remove(control);
        }
    }

    public void removeAllValidation(T... control) {
        for(int i = 0; i < control.length; i++){
            if(map1.containsKey(control[i])){
                pane.getChildren().remove(control[i]);
                map1.remove(control[i]);
            }
        }
    }

}