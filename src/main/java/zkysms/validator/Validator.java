package zkysms.validator;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import org.apache.commons.validator.GenericValidator;
import org.controlsfx.control.CheckComboBox;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validator {

    private final Pane pane;
    private final double x, y;
    private final Map<Control, Label> map1;
    private final Map<Control, String> map2;
    private final Map<Control, List<String>> map3;
    private final Map<Control, String> map4;
    private final Map<Control, Control> map5;
    private final Map<Control, String> map6;
    private final Map<Control, List<Control>> map7;


    public Validator(Pane pane, double x, double y){
        this.pane = pane;
        this.x = x;
        this.y = y;
        map1 = new HashMap<>();
        map2 = new HashMap<>();
        map3 = new HashMap<>();
        map4 = new HashMap<>();
        map5 = new HashMap<>();
        map6 = new HashMap<>();
        map7 = new HashMap<>();
    }

    public void registerEmptyValidation(Control control, String message) {
        if(!map1.containsKey(control)){
            add(control);
        }
        map2.put(control, message);
    }

    public void registerRegexValidation(Control control, String message, String regex) {
        if(control instanceof ComboBox || control instanceof CheckComboBox){
            System.out.println("Cannot Register Validation");
            // Create Exception.
        }else{
            List<String> s = new ArrayList<>();
            s.add(message);
            s.add(regex);
            if(!map1.containsKey(control)){
                add(control);
            }
            map3.put(control, s);
        }
    }

    public void registerEqualValidation(Control control1, Control control2, String message) {
        if(control2 instanceof TextField){
            if(!map1.containsKey(control2)){
                add(control2);
            }
            map4.put(control2, message);
            map5.put(control2, control1);
        }
    }

    public void registerChoiceValidation(String message, Control... controls) {
        List<CheckBox> checkBoxList = new ArrayList<>();
        for(int i = 0; i < controls.length; i++){
            if(controls[i] instanceof CheckBox){
                checkBoxList.add((CheckBox) controls[i]);
            }
            if(i == 0) continue;
            controls[i].focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean focusLost, Boolean focusGained) {
                    if(focusGained && map1.get(controls[0]).isVisible()) {
                        removeLabel(controls[0]);
                    }
                }
            });
        }
        if(checkBoxList.size() == controls.length){
            add(controls[0]);
            map6.put(controls[0], message);
            List<Control> list = new ArrayList<>(Arrays.asList(controls));
            map7.put(controls[0], list);
        }
    }

    public boolean validate() {
        List<Label> labels = new ArrayList<>();
        Set<Control> controls = map1.keySet();
        Iterator<Control> iterator = controls.iterator();
        while(iterator.hasNext()) {
            Control control = iterator.next();
            if(control instanceof TextField) {
                if (map2.containsKey(control) && ((TextField) control).getText().isEmpty()) {
                    if(!map1.get(control).isVisible()) {
                        map1.get(control).setText(map2.get(control));
                        map1.get(control).setVisible(true);
                    }
                    labels.add(map1.get(control));
                }else if(map3.containsKey(control)) {
                    if (!((TextField) control).getText().isEmpty()) {
                        Pattern pattern = Pattern.compile(map3.get(control).get(1));
                        Matcher matcher = pattern.matcher(((TextField) control).getText());
                        if (!matcher.matches()) {
                            if (!map1.get(control).isVisible()) {
                                map1.get(control).setText(map3.get(control).get(0));
                                map1.get(control).setVisible(true);
                            }
                            labels.add(map1.get(control));
                        }
                    }
                }else if(map4.containsKey(control)) {
                    if(!((TextField) map5.get(control)).getText().isEmpty()){
                        if(!((TextField) control).getText().equals(((TextField) map5.get(control)).getText())){
                            if (!map1.get(control).isVisible()) {
                                map1.get(control).setText(map4.get(control));
                                map1.get(control).setVisible(true);
                            }
                            labels.add(map1.get(control));
                        }
                    }
                }
            }else if(control instanceof DatePicker){
                if(map2.containsKey(control) && ((DatePicker) control).getEditor().getText().isEmpty()) {
                    if(!map1.get(control).isVisible()) {
                        map1.get(control).setText(map2.get(control));
                        map1.get(control).setVisible(true);
                    }
                    labels.add(map1.get(control));
                }else if(map3.containsKey(control)) {
                    if(!((DatePicker) control).getEditor().getText().isEmpty()) {
                        Pattern pattern = Pattern.compile(map3.get(control).get(1));
                        Matcher matcher = pattern.matcher(((DatePicker) control).getEditor().getText());
                        if(!matcher.matches()) {
                            if(!map1.get(control).isVisible()) {
                                map1.get(control).setText(map3.get(control).get(0));
                                map1.get(control).setVisible(true);
                            }
                            labels.add(map1.get(control));
                        }else if(matcher.matches() && !GenericValidator.isDate(((DatePicker) control).getEditor().getText(), "dd-MM-yyyy", true)) {
                            if(!map1.get(control).isVisible()) {
                                map1.get(control).setText("Invalid Date");
                                map1.get(control).setVisible(true);
                            }
                            labels.add(map1.get(control));
                        }
                    }
                }
            }else if(control instanceof ComboBox){
                if (map2.containsKey(control) && ((ComboBox) control).getValue() == null) {
                    if (!map1.get(control).isVisible()) {
                        map1.get(control).setText(map2.get(control));
                        map1.get(control).setVisible(true);
                    }
                    labels.add(map1.get(control));
                }
            }else if(control instanceof CheckComboBox){
                if (map2.containsKey(control) && ((CheckComboBox) control).getCheckModel().getCheckedItems().isEmpty()) {
                    if (!map1.get(control).isVisible()) {
                        map1.get(control).setText(map2.get(control));
                        map1.get(control).setVisible(true);
                    }
                    labels.add(map1.get(control));
                }
            }else if(control instanceof CheckBox){
                if(map6.containsKey(control)){
                    if(map7.containsKey(control)){
                        List<Control> list = map7.get(control);
                        List<Boolean> check = new ArrayList<>();
                        for(int i = 0; i < list.size(); i++){
                            if(((CheckBox) list.get(i)).isSelected()){
                                check.add(true);
                            }else{
                                check.add(false);
                            }
                        }
                        if(!check.contains(true)){
                            if (!map1.get(control).isVisible()) {
                                map1.get(control).setText(map6.get(control));
                                map1.get(control).setVisible(true);
                            }
                            labels.add(map1.get(control));
                        }
                    }
                }
            }
        }
        if(labels.isEmpty()){
            return true;
        }
        return false;
    }

    private void add(Control control) {
        Label label = new Label();
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: red;");
        Point2D points;
        if(control instanceof CheckBox){
            points = control.getParent().localToParent(x, y);
        }else{
            points = control.localToParent(x, y);
        }
        label.setLayoutX(points.getX());
        label.setLayoutY(points.getY());
        pane.getChildren().add(label);
        label.setVisible(false);
        if(control instanceof CheckComboBox){
            ((CheckComboBox) control).getCheckModel().getCheckedItems().addListener(new ListChangeListener() {
                @Override
                public void onChanged(Change change) {
                    if(map1.get(control).isVisible()){
                        removeLabel(control);
                    }
                }
            });
        }else{
            control.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observableValue, Boolean focusLost, Boolean focusGained) {
                    if(focusGained && map1.get(control).isVisible()) {
                        removeLabel(control);
                    }
                }
            });
        }
        if(control instanceof CheckBox){
            control.getParent().layoutXProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    Point2D points = control.getParent().localToParent(x, y);
                    label.setLayoutX(points.getX());
                }
            });
            control.getParent().layoutYProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    Point2D points = control.getParent().localToParent(x, y);
                    label.setLayoutY(points.getY());
                }
            });
        }else{
            control.layoutXProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    Point2D points = control.localToParent(x, y);
                    label.setLayoutX(points.getX());
                }
            });
            control.layoutYProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                    Point2D points = control.localToParent(x, y);
                    label.setLayoutY(points.getY());
                }
            });
        }
        map1.put(control, label);
    }

    public void removeLabel(Control control) {
        if(map1.containsKey(control)){
            if(map1.get(control).isVisible()){
                map1.get(control).setVisible(false);
            }
        }
    }

    public void removeAllLabel(Control... control) {
        for(int i = 0; i < control.length; i++){
            if(map1.containsKey(control[i])){
                if(map1.get(control[i]).isVisible()){
                    map1.get(control[i]).setVisible(false);
                }
            }
        }
    }

    public void removeValidation(Control control) {
        if(map1.containsKey(control)){
            pane.getChildren().remove(map1.get(control));
            map1.remove(control);
        }
    }

    public void removeAllValidation(Control... control) {
        for(int i = 0; i < control.length; i++){
            if(map1.containsKey(control[i])){
                pane.getChildren().remove(map1.get(control[i]));
                map1.remove(control[i]);
            }
        }
    }

}