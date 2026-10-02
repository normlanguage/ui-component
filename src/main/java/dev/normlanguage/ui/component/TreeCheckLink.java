package dev.normlanguage.ui.component;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;

import java.util.ArrayDeque;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

public final class TreeCheckLink<T> implements AutoCloseable {
    private final Tree<T> tree;
    private final ReadOnlyObjectWrapper<List<T>> current = new ReadOnlyObjectWrapper<>(List.of());
    private final SelectionLink<List<T>> delegate;
    private final Map<CheckBoxTreeItem<T>,ChangeListener<Boolean>> listeners = new IdentityHashMap<>();
    private TreeItem<T> root;
    private boolean suspended;
    private boolean closed;

    public TreeCheckLink(Tree<T> tree) {
        Util.requireFxThread();
        this.tree = Objects.requireNonNull(tree);
        delegate = new SelectionLink<>(tree, current.getReadOnlyProperty(), this::apply);
        rewire();
    }

    public void update(List<T> values, Consumer<List<T>> changed) {
        Util.requireFxThread();
        if (closed) throw new IllegalStateException("Check link is closed");
        rewire();
        delegate.update(List.copyOf(values), next -> {
            if (!suspended) changed.accept(next);
        });
    }

    public void unbind() { delegate.unbind(); }

    private void apply(List<T> values) {
        suspended = true;
        try {
            for (var item : listeners.keySet()) item.setSelected(values.contains(item.getValue()));
        } finally { suspended = false; }
        publish();
    }

    private void rewire() {
        if (root == tree.getRoot()) return;
        suspended = true;
        try {
            for (var entry : listeners.entrySet()) entry.getKey().selectedProperty().removeListener(entry.getValue());
            listeners.clear();
            root = tree.getRoot();
            if (root != null) {
                var queue = new ArrayDeque<TreeItem<T>>();
                queue.add(root);
                while (!queue.isEmpty()) {
                    var item = queue.removeFirst();
                    if (item instanceof CheckBoxTreeItem<T> check) {
                        ChangeListener<Boolean> listener = (observable, previous, next) -> publish();
                        check.selectedProperty().addListener(listener);
                        listeners.put(check, listener);
                    }
                    queue.addAll(item.getChildren());
                }
            }
            current.set(List.copyOf(tree.getCheckedValues()));
        } finally { suspended = false; }
    }

    private void publish() {
        if (suspended || closed) return;
        var next = List.copyOf(tree.getCheckedValues());
        if (!Objects.equals(current.get(), next)) current.set(next);
    }

    @Override public void close() {
        Util.requireFxThread();
        if (closed) return;
        closed = true;
        for (var entry : listeners.entrySet()) entry.getKey().selectedProperty().removeListener(entry.getValue());
        listeners.clear();
        root = null;
        delegate.close();
    }
}
