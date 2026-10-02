package dev.normlanguage.ui.component;

import javafx.beans.InvalidationListener;
import javafx.scene.Node;

import java.util.ArrayList;

final class ThemeConnection implements AutoCloseable {
    private final Node anchor;
    private final ConfigProvider destination;
    private final ArrayList<ConfigProvider> providers = new ArrayList<>();
    private final InvalidationListener changed = observable -> refresh();
    private final ConfigurationConnection configuration;

    ThemeConnection(Node anchor, ConfigProvider destination) {
        this.anchor = anchor;
        this.destination = destination;
        configuration = new ConfigurationConnection(anchor,
                () -> destination.setConfig(ConfigurationConnection.resolve(anchor)));
    }
    void connect() {
        if (!providers.isEmpty()) return;
        for (Node current = anchor; current != null; current = current.getParent()) {
            if (current instanceof ConfigProvider provider) {
                providers.add(provider);
                provider.themeCssProperty().addListener(changed);
            }
        }
        refresh();
        configuration.connect();
    }
    private void refresh() {
        var css = new StringBuilder();
        for (var provider : providers.reversed()) css.append(provider.getThemeCss()).append(';');
        destination.setThemeCss(css.toString());
    }
    @Override public void close() {
        for (var provider : providers) provider.themeCssProperty().removeListener(changed);
        providers.clear();
        configuration.close();
    }
}
