package dev.normlanguage.ui.component;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Button;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class TreeSelect<T> extends HBox {
    private final TreeView<T> tree;
    private final Button trigger = new Button();
    private final Popover popup;
    private final ReadOnlyObjectWrapper<T> value = new ReadOnlyObjectWrapper<>(this, "value");
    public TreeSelect(TreeItem<T> root) {
        tree = new TreeView<>(Objects.requireNonNull(root));
        tree.setShowRoot(false);
        popup = new Popover(trigger, tree);
        trigger.setOnAction(event -> {
            if (popup.isShowing()) popup.close();
            else popup.show();
        });
        tree.getSelectionModel().selectedItemProperty().addListener((observable, old, selected) -> {
            if (selected != null) {
                value.set(selected.getValue());
                trigger.setText(String.valueOf(selected.getValue()));
                popup.close();
            }
        });
        sceneProperty().addListener((observable, old, scene) -> { if (scene == null) popup.close(); });
        getChildren().add(trigger);
        getStyleClass().add("norm-tree-select");
    }
    public TreeItem<T> getRootItem() { return tree.getRoot(); }
    public TreeView<T> getTreeView() { return tree; }
    public boolean isShowing() { return popup.isShowing(); }
    public ReadOnlyObjectProperty<T> valueProperty() { return value.getReadOnlyProperty(); }
    public T getSelectedValue() { return value.get(); }
    public TreeItem<T> getSelectedItem() { return tree.getSelectionModel().getSelectedItem(); }
    public void select(TreeItem<T> item) {
        var parent = item;
        while (parent != null && parent != tree.getRoot()) parent = parent.getParent();
        if (parent == null) throw new IllegalArgumentException("Item is outside tree");
        tree.getSelectionModel().select(item);
    }
}
