package dev.normlanguage.ui.component;

import javafx.scene.control.TreeItem;
import java.util.ArrayDeque;
import java.util.Objects;
import java.util.function.Consumer;

public final class TreeSelectionLink<T> implements AutoCloseable {
    private final Tree<T> control;
    private final SelectionLink<TreeItem<T>> delegate;

    public TreeSelectionLink(Tree<T> control) {
        this.control = Objects.requireNonNull(control);
        var selection = control.getSelectionModel();
        delegate = new SelectionLink<>(control, selection.selectedItemProperty(), item -> {
            if (item == null) selection.clearSelection(); else selection.select(item);
        });
    }

    public void update(T value, Consumer<T> changed) {
        TreeItem<T> match = null;
        if (value != null) {
            var queue = new ArrayDeque<TreeItem<T>>();
            queue.add(control.getRoot());
            while (!queue.isEmpty()) {
                var item = queue.removeFirst();
                if (Objects.equals(item.getValue(), value)) { match = item; break; }
                queue.addAll(item.getChildren());
            }
        }
        delegate.update(match, item -> changed.accept(item == null ? null : item.getValue()));
    }

    public void unbind() { delegate.unbind(); }

    @Override public void close() { delegate.close(); }
}
