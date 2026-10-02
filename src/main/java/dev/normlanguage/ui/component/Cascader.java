package dev.normlanguage.ui.component;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ListChangeListener;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class Cascader<T> extends HBox {
    public record Item<T>(T value, String label, List<Item<T>> children, boolean lazy) {
        public Item { Objects.requireNonNull(label); children = List.copyOf(children); }
        public Item(T value, String label, List<Item<T>> children) { this(value, label, children, false); }
    }
    private final ObservableList<Item<T>> roots = FXCollections.observableArrayList();
    private final ReadOnlyObjectWrapper<List<T>> value = new ReadOnlyObjectWrapper<>(this, "value", List.of());
    private final Map<Item<T>, List<Item<T>>> loaded = new IdentityHashMap<>();
    private Function<Item<T>, CompletableFuture<List<Item<T>>>> childrenProvider;
    private AsyncRequest<List<Item<T>>> request;
    private long revision;
    public Cascader(List<Item<T>> items) {
        roots.setAll(items);
        setSpacing(8);
        getStyleClass().add("norm-cascader");
        roots.addListener((ListChangeListener<Item<T>>) change -> {
            revision++;
            if (request != null) request.cancel();
            loaded.clear();
            getChildren().clear();
            value.set(List.of());
            showLevel(roots, List.of());
        });
        sceneProperty().addListener((observable, old, scene) -> {
            if (old != null && scene == null) {
                revision++;
                if (request != null) request.cancel();
                getChildren().removeIf(node -> node instanceof ProgressIndicator);
            } else if (old == null && scene != null) resumePendingSelection();
        });
        showLevel(roots, List.of());
    }
    public ObservableList<Item<T>> getItems() { return roots; }
    public ReadOnlyObjectProperty<List<T>> valueProperty() { return value.getReadOnlyProperty(); }
    public List<T> getValue() { return value.get(); }
    public void setChildrenProvider(Function<Item<T>, CompletableFuture<List<Item<T>>>> provider) {
        revision++;
        if (request != null) request.cancel();
        childrenProvider = Objects.requireNonNull(provider);
        getChildren().removeIf(node -> node instanceof ProgressIndicator);
        resumePendingSelection();
    }
    private void resumePendingSelection() {
        if (getChildren().isEmpty()) return;
        var last = getChildren().getLast();
        if (last instanceof ComboBox<?> raw && raw.getValue() instanceof Item<?> selected && selected.lazy()) {
            @SuppressWarnings("unchecked") ComboBox<Item<T>> box = (ComboBox<Item<T>>) raw;
            var item = box.getValue();
            if (!loaded.containsKey(item)) { box.setValue(null); box.setValue(item); }
        }
    }
    public void selectPath(List<T> path) {
        List<Item<T>> current = roots;
        for (T key : path) {
            Item<T> match = current.stream().filter(item -> Objects.equals(item.value(), key)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown path value: " + key));
            current = loaded.getOrDefault(match, match.children());
        }
        revision++;
        if (request != null) request.cancel();
        current = roots;
        getChildren().clear();
        value.set(List.of());
        showLevel(current, List.of());
        for (T key : path) {
            Item<T> match = current.stream().filter(item -> Objects.equals(item.value(), key)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown path value: " + key));
            @SuppressWarnings("unchecked") ComboBox<Item<T>> box = (ComboBox<Item<T>>) getChildren().getLast();
            box.setValue(match);
            current = loaded.getOrDefault(match, match.children());
        }
    }
    private void showLevel(List<Item<T>> items, List<T> prefix) {
        if (items.isEmpty()) return;
        var box = new ComboBox<Item<T>>(FXCollections.observableArrayList(items));
        box.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(Item<T> item) { return item == null ? "" : item.label(); }
            @Override public Item<T> fromString(String text) { throw new UnsupportedOperationException(); }
        });
        box.valueProperty().addListener((observable, old, chosen) -> {
            if (chosen == null) return;
            long current = ++revision;
            if (request != null) request.cancel();
            int index = getChildren().indexOf(box);
            getChildren().remove(index + 1, getChildren().size());
            var next = new ArrayList<>(prefix);
            next.add(chosen.value());
            value.set(List.copyOf(next));
            List<Item<T>> known = loaded.getOrDefault(chosen, chosen.children());
            if (!known.isEmpty() || !chosen.lazy() || childrenProvider == null) {
                showLevel(known, next);
                return;
            }
            var loading = new ProgressIndicator();
            loading.setPrefSize(24, 24);
            getChildren().add(loading);
            var provider = childrenProvider;
            request = new AsyncRequest<>(() -> provider.apply(chosen));
            request.result().whenComplete((children, failure) -> javafx.application.Platform.runLater(() -> {
                    if (current != revision || !getChildren().contains(box)) return;
                    getChildren().remove(loading);
                    if (failure == null) {
                        var result = List.copyOf(children);
                        loaded.put(chosen, result);
                        showLevel(result, next);
                    }
                }));
        });
        getChildren().add(box);
    }
}
