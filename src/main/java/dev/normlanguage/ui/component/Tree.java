package dev.normlanguage.ui.component;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class Tree<T> extends TreeView<T> {
    private final Map<TreeItem<T>,AsyncRequest<List<T>>> pending = new IdentityHashMap<>();
    private final Map<TreeItem<T>,List<TreeItem<T>>> generated = new IdentityHashMap<>();
    private final Set<TreeItem<T>> loaded = Collections.newSetFromMap(new IdentityHashMap<>());
    private final ObjectProperty<Throwable> loadError = new SimpleObjectProperty<>(this, "loadError");
    private Function<T,CompletableFuture<List<T>>> lazyChildren;

    public Tree(T rootValue) {
        getStyleClass().add("norm-tree");
        setRoot(createItem(rootValue));
        sceneProperty().addListener((o,a,b) -> {
            if (b == null) {
                for (var request : pending.values()) request.cancel();
                pending.clear();
            } else if (getRoot().isExpanded()) load(getRoot());
        });
        getRoot().setExpanded(true);
    }

    public void setLazyChildren(Function<T,CompletableFuture<List<T>>> provider) {
        for (var request : pending.values()) request.cancel();
        pending.clear();
        for (var entry : generated.entrySet()) entry.getKey().getChildren().removeAll(entry.getValue());
        generated.clear();
        lazyChildren = provider;
        loaded.clear();
        if (getScene() != null && getRoot().isExpanded()) load(getRoot());
    }
    public ObjectProperty<Throwable> loadErrorProperty() { return loadError; }
    public Throwable getLoadError() { return loadError.get(); }
    public void setCheckable(boolean value) {
        setCellFactory(value ? CheckBoxTreeCell.forTreeView() : null);
    }
    public List<T> getCheckedValues() {
        var result = new ArrayList<T>();
        var queue = new ArrayDeque<TreeItem<T>>();
        queue.add(getRoot());
        while (!queue.isEmpty()) {
            var item = queue.removeFirst();
            if (item instanceof CheckBoxTreeItem<T> check && check.isSelected()) result.add(item.getValue());
            queue.addAll(item.getChildren());
        }
        return result;
    }
    private TreeItem<T> createItem(T value) {
        var item = new CheckBoxTreeItem<>(value);
        item.expandedProperty().addListener((o,a,b) -> {
            if (b) load(item);
            else {
                var request = pending.remove(item);
                if (request != null) request.cancel();
            }
        });
        return item;
    }
    private void load(TreeItem<T> item) {
        if (lazyChildren == null || getScene() == null || loaded.contains(item) || pending.containsKey(item)) return;
        var provider = lazyChildren;
        var request = new AsyncRequest<>(() -> provider.apply(item.getValue()));
        pending.put(item, request);
        request.result().whenComplete((children, error) -> Platform.runLater(() -> {
            if (pending.get(item) != request) return;
            pending.remove(item);
            if (error != null) { loadError.set(error); return; }
            if (getScene() == null || !item.isExpanded()) return;
            var added = new ArrayList<TreeItem<T>>();
            for (var child : children) {
                var childItem = createItem(child);
                item.getChildren().add(childItem);
                added.add(childItem);
            }
            generated.put(item, added);
            loadError.set(null);
            loaded.add(item);
        }));
    }
}
