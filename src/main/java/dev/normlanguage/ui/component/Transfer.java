package dev.normlanguage.ui.component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

public class Transfer<T> extends HBox {
    private final ObservableList<T> available = FXCollections.observableArrayList();
    private final ObservableList<T> selected = FXCollections.observableArrayList();
    private final ListView<T> left = new ListView<>(available);
    private final ListView<T> right = new ListView<>(selected);
    public Transfer(List<T> items) {
        available.setAll(items);
        left.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        right.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        var add = new javafx.scene.control.Button("→");
        var remove = new javafx.scene.control.Button("←");
        add.setOnAction(event -> moveSelected(left, available, selected));
        remove.setOnAction(event -> moveSelected(right, selected, available));
        getChildren().addAll(left, new VBox(add, remove), right);
        getStyleClass().add("norm-transfer");
    }
    public ObservableList<T> getAvailableItems() { return FXCollections.unmodifiableObservableList(available); }
    public ObservableList<T> getSelectedItems() { return FXCollections.unmodifiableObservableList(selected); }
    public void setItems(List<T> items) {
        selected.removeIf(item -> !items.contains(item));
        available.setAll(items.stream().filter(item -> !selected.contains(item)).toList());
    }
    public ListView<T> getAvailableView() { return left; }
    public ListView<T> getSelectedView() { return right; }
    public void select(T item) {
        if (!available.remove(item)) throw new IllegalArgumentException("Item is not available");
        selected.add(item);
    }
    public void remove(T item) {
        if (!selected.remove(item)) throw new IllegalArgumentException("Item is not selected");
        available.add(item);
    }
    private void moveSelected(ListView<T> view, ObservableList<T> source, ObservableList<T> destination) {
        var moving = List.copyOf(view.getSelectionModel().getSelectedItems());
        source.removeAll(moving);
        destination.addAll(moving);
    }
}
