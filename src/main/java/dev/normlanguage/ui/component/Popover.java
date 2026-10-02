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
    private final Motion motion;
    private final javafx.beans.InvalidationListener detached;
    private Side side = Side.BOTTOM;
    private Runnable onHidden;
    private boolean closed;

    public Popover(Node anchor, Node content) { this(anchor, content, ContentOwnership.OWNED); }
    public Popover(Node anchor, Node content, ContentOwnership ownership) {
        this.anchor = java.util.Objects.requireNonNull(anchor);
        detached = observable -> { if (anchor.getScene() == null) close(); };
        contentRoot = new ConfigProvider(content, ownership);
        theme = new ThemeConnection(anchor, contentRoot);
        motion = new Motion(contentRoot);
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
        if (closed) throw new IllegalStateException("Popover is closed");
        if (popup.isShowing()) return;
        if (anchor.getScene() == null || anchor.getScene().getWindow() == null)
            throw new IllegalStateException("Popover requires a window");
        theme.connect();
        anchor.sceneProperty().addListener(detached);
        try { popup.show(anchor, side, 0, 4); motion.enter(0, side == Side.TOP ? 8 : -8); }
        catch (RuntimeException failure) { disconnect(); throw failure; }
    }
    private void disconnect() {
        motion.finish();
        theme.close();
        anchor.sceneProperty().removeListener(detached);
    }
    public void hide() {
        Util.requireFxThread();
        popup.hide();
        disconnect();
    }
    @Override public void close() {
        Util.requireFxThread();
        if (closed) return;
        closed = true;
        hide();
        motion.close();
        contentRoot.close();
        popup.setOnHidden(null);
        popup.getItems().clear();
    }
}
