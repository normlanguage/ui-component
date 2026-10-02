package dev.normlanguage.ui.component;

import javafx.beans.InvalidationListener;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Parent;

import java.util.ArrayList;
import java.util.Objects;

final class ConfigurationConnection implements AutoCloseable {
    private final Node anchor;
    private final Runnable changed;
    private final ArrayList<Node> lineage = new ArrayList<>();
    private final ChangeListener<Parent> parentChanged = (observable, old, next) -> rewire();
    private final InvalidationListener configurationChanged;
    private boolean connected;

    ConfigurationConnection(Node anchor, Runnable changed) {
        this.anchor = Objects.requireNonNull(anchor);
        this.changed = Objects.requireNonNull(changed);
        configurationChanged = observable -> changed.run();
    }
    static ComponentConfig resolve(Node anchor) {
        for (Node current = anchor; current != null; current = current.getParent()) {
            if (current instanceof ConfigProvider provider && provider.getConfig() != null) return provider.getConfig();
        }
        return ComponentConfig.defaults();
    }
    void connect() {
        if (connected) return;
        connected = true;
        rewire();
    }
    private void rewire() {
        for (var node : lineage) {
            node.parentProperty().removeListener(parentChanged);
            if (node instanceof ConfigProvider provider) provider.configProperty().removeListener(configurationChanged);
        }
        lineage.clear();
        if (!connected) return;
        for (Node current = anchor; current != null; current = current.getParent()) {
            lineage.add(current);
            current.parentProperty().addListener(parentChanged);
            if (current instanceof ConfigProvider provider) provider.configProperty().addListener(configurationChanged);
        }
        changed.run();
    }
    @Override public void close() {
        if (!connected) return;
        connected = false;
        rewire();
    }
}
