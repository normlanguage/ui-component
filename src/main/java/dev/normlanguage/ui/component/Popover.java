package dev.normlanguage.ui.component;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.ContextMenu;

public class Popover implements AutoCloseable {
    private final Node anchor;
    private final ConfigProvider contentRoot;
    private final ContextMenu popup = new ContextMenu();
    private final ThemeConnection theme;
    private final javafx.beans.InvalidationListener detached;
    private App owner;
    private Side side = Side.BOTTOM;
    private Runnable onHidden;

    public Popover(Node anchor, Node content) {
        this.anchor = java.util.Objects.requireNonNull(anchor);
        detached = observable -> { if (anchor.getScene() == null) close(); };
        contentRoot = new ConfigProvider(content);
        theme = new ThemeConnection(anchor, contentRoot);
        contentRoot.getStyleClass().add("norm-popover");
        popup.getItems().add(new CustomMenuItem(contentRoot, false));
        popup.setOnHidden(event -> {
            disconnect();
            if (onHidden != null) onHidden.run();
        });
    }
    public ConfigProvider getContentRoot() { return contentRoot; }
    public boolean isShowing() { return popup.isShowing(); }
    public void setSide(Side side) { this.side = java.util.Objects.requireNonNull(side); }
    public void setOnHidden(Runnable action) { onHidden = action; }
    public void show() {
        Util.requireFxThread();
        if (popup.isShowing()) return;
        owner = Util.app(anchor);
        owner.own(this);
        theme.connect();
        anchor.sceneProperty().addListener(detached);
        try { popup.show(anchor, side, 0, 4); }
        catch (RuntimeException failure) { disconnect(); throw failure; }
    }
    private void disconnect() {
        theme.close();
        anchor.sceneProperty().removeListener(detached);
        if (owner != null) { owner.release(this); owner = null; }
    }
    @Override public void close() {
        Util.requireFxThread();
        popup.hide();
        disconnect();
    }
}
