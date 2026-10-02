package dev.normlanguage.ui.component;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import java.util.List;
import java.util.IdentityHashMap;
import java.util.Map;
public class Segmented<T> extends HBox {
    private final ObservableList<T> items = FXCollections.observableArrayList();
    private final ObjectProperty<T> value = new SimpleObjectProperty<>(this, "value");
    private final ToggleGroup group = new ToggleGroup();
    private final Map<javafx.scene.control.Toggle,T> values = new IdentityHashMap<>();
    public Segmented(List<T> choices) {
        getStyleClass().add("norm-segmented");
        items.addAll(choices);
        items.addListener((ListChangeListener<T>) change -> rebuild());
        value.addListener((o,a,b) -> {
            for (var toggle : group.getToggles()) if (toggle.getUserData() == b || toggle.getUserData() != null && toggle.getUserData().equals(b)) {
                group.selectToggle(toggle);
                return;
            }
            group.selectToggle(null);
        });
        group.selectedToggleProperty().addListener((o,a,b) -> value.set(values.get(b)));
        rebuild();
    }
    private void rebuild() {
        T selection = value.get();
        getChildren().clear();
        group.getToggles().clear();
        values.clear();
        for (T item : items) {
            var button = new ToggleButton(String.valueOf(item));
            values.put(button, item);
            button.setToggleGroup(group);
            getChildren().add(button);
            if (item == selection || item != null && item.equals(selection)) button.setSelected(true);
        }
    }
    public ObservableList<T> getItems() { return items; }
    public ObjectProperty<T> valueProperty() { return value; }
    public T getValue() { return value.get(); }
    public void setValue(T selected) { value.set(selected); }
}
