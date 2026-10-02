package dev.normlanguage.ui.component;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import java.util.Objects;
import java.util.function.Consumer;

public final class SelectionLink<T> implements AutoCloseable {
    private final Node node;
    private final ObservableValue<T> selected;
    private final Consumer<T> select;
    private final Runnable release;
    private final ChangeListener<T> listener;
    private Consumer<T> changed;
    private boolean synchronizing;
    private boolean closed;

    public SelectionLink(Node node, ObservableValue<T> selected, Consumer<T> select) {
        this(node, selected, select, () -> {});
    }

    public SelectionLink(Node node, ObservableValue<T> selected, Consumer<T> select, Runnable release) {
        Util.requireFxThread();
        this.node = Objects.requireNonNull(node);
        this.selected = Objects.requireNonNull(selected);
        this.select = Objects.requireNonNull(select);
        this.release = Objects.requireNonNull(release);
        listener = (observable, previous, next) -> {
            if (!closed && !synchronizing && changed != null) changed.accept(next);
        };
        selected.addListener(listener);
    }

    public Node node() { return node; }

    public void update(T value, Consumer<T> changed) {
        Util.requireFxThread();
        if (closed) throw new IllegalStateException("Selection link is closed");
        this.changed = Objects.requireNonNull(changed);
        if (Objects.equals(selected.getValue(), value)) return;
        synchronizing = true;
        try { select.accept(value); }
        finally { synchronizing = false; }
    }

    public void unbind() {
        Util.requireFxThread();
        if (closed) throw new IllegalStateException("Selection link is closed");
        changed = null;
    }

    @Override public void close() {
        Util.requireFxThread();
        if (closed) return;
        closed = true;
        changed = null;
        selected.removeListener(listener);
        release.run();
    }
}
