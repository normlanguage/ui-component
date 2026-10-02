package dev.normlanguage.ui.component;

import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Drawer implements AutoCloseable {
    private final Node anchor;
    private final ConfigProvider panel;
    private final StackPane overlay = new StackPane();
    private final ThemeConnection theme;
    private final Motion motion;
    private final Side side;
    private Window window;
    private final javafx.beans.InvalidationListener detached;
    private final javafx.beans.InvalidationListener windowHidden = observable -> {
        if (window != null && !window.isShowing()) hide();
    };
    private OverlayHost host;
    private Node previousFocus;
    private boolean closed;
    private Runnable onHidden;

    public Drawer(Node anchor, Node content, Side side) { this(anchor, content, side, ContentOwnership.OWNED); }
    public Drawer(Node anchor, Node content, Side side, ContentOwnership ownership) {
        this.anchor = Objects.requireNonNull(anchor);
        this.side = Objects.requireNonNull(side);
        detached = observable -> { if (anchor.getScene() == null) close(); };
        panel = new ConfigProvider(Objects.requireNonNull(content), ownership);
        theme = new ThemeConnection(anchor, panel);
        motion = new Motion(panel);
        panel.getStyleClass().add("norm-card");
        panel.setMaxWidth(360);
        StackPane.setAlignment(panel, switch (side) {
            case LEFT -> Pos.CENTER_LEFT;
            case RIGHT -> Pos.CENTER_RIGHT;
            case TOP -> Pos.TOP_CENTER;
            case BOTTOM -> Pos.BOTTOM_CENTER;
        });
        overlay.getChildren().add(panel);
        overlay.setStyle("-fx-background-color: -norm-scrim;");
        overlay.setOnMouseClicked(event -> { if (event.getTarget() == overlay) hide(); });
        overlay.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) { hide(); event.consume(); }
            else if (event.getCode() == KeyCode.TAB) {
                var nodes = new ArrayList<Node>();
                collectFocusable(panel, nodes);
                if (nodes.isEmpty()) overlay.requestFocus();
                else {
                    int current = nodes.indexOf(overlay.getScene().getFocusOwner());
                    int next = event.isShiftDown() ? (current <= 0 ? nodes.size() - 1 : current - 1)
                            : (current + 1) % nodes.size();
                    nodes.get(next).requestFocus();
                }
                event.consume();
            }
        });
        overlay.setFocusTraversable(true);
    }
    public boolean isShowing() { return host != null; }
    public void setOnHidden(Runnable action) { onHidden = action; }
    public ConfigProvider getContentRoot() { return panel; }
    public void show() {
        Util.requireFxThread();
        if (closed) throw new IllegalStateException("Drawer is closed");
        if (host != null) return;
        var owner = OverlayHost.nearest(anchor);
        if (anchor.getScene() == null || anchor.getScene().getWindow() == null
                || !anchor.getScene().getWindow().isShowing())
            throw new IllegalStateException("Drawer requires a visible window");
        previousFocus = anchor.getScene().getFocusOwner();
        window = anchor.getScene().getWindow();
        host = owner;
        try {
            theme.connect();
            anchor.sceneProperty().addListener(detached);
            window.showingProperty().addListener(windowHidden);
            owner.overlayLayer().getChildren().add(overlay);
            motion.enter(side == Side.LEFT ? -360 : side == Side.RIGHT ? 360 : 0,
                    side == Side.TOP ? -240 : side == Side.BOTTOM ? 240 : 0);
            var nodes = new ArrayList<Node>();
            collectFocusable(panel, nodes);
            if (nodes.isEmpty()) overlay.requestFocus(); else nodes.getFirst().requestFocus();
        } catch (RuntimeException failure) { hide(); throw failure; }
    }
    public void hide() {
        Util.requireFxThread();
        if (host == null) return;
        motion.finish();
        host.overlayLayer().getChildren().remove(overlay);
        host = null;
        theme.close();
        anchor.sceneProperty().removeListener(detached);
        if (window != null) window.showingProperty().removeListener(windowHidden);
        window = null;
        if (previousFocus != null && previousFocus.getScene() != null) previousFocus.requestFocus();
        previousFocus = null;
        if (onHidden != null) onHidden.run();
    }
    @Override public void close() {
        Util.requireFxThread();
        if (closed) return;
        closed = true;
        hide();
        motion.close();
        panel.close();
        overlay.getChildren().clear();
    }
    private void collectFocusable(Node node, List<Node> result) {
        if (!node.isVisible() || node.isDisabled()) return;
        if (node.isFocusTraversable()) result.add(node);
        if (node instanceof Parent parent) for (var child : parent.getChildrenUnmodifiable()) collectFocusable(child, result);
    }
}
