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
    private Window window;
    private final javafx.beans.InvalidationListener detached;
    private final javafx.beans.InvalidationListener windowHidden = observable -> {
        if (window != null && !window.isShowing()) close();
    };
    private App app;
    private Node previousFocus;

    public Drawer(Node anchor, Node content, Side side) {
        this.anchor = Objects.requireNonNull(anchor);
        detached = observable -> { if (anchor.getScene() == null) close(); };
        panel = new ConfigProvider(Objects.requireNonNull(content));
        theme = new ThemeConnection(anchor, panel);
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
        overlay.setOnMouseClicked(event -> { if (event.getTarget() == overlay) close(); });
        overlay.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) { close(); event.consume(); }
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
    public boolean isShowing() { return app != null; }
    public ConfigProvider getContentRoot() { return panel; }
    public void show() {
        Util.requireFxThread();
        if (app != null) return;
        var owner = Util.app(anchor);
        if (anchor.getScene() == null || anchor.getScene().getWindow() == null
                || !anchor.getScene().getWindow().isShowing())
            throw new IllegalStateException("Drawer requires a visible window");
        previousFocus = anchor.getScene().getFocusOwner();
        window = anchor.getScene().getWindow();
        app = owner;
        try {
            owner.own(this);
            theme.connect();
            anchor.sceneProperty().addListener(detached);
            window.showingProperty().addListener(windowHidden);
            owner.getChildren().add(overlay);
            var nodes = new ArrayList<Node>();
            collectFocusable(panel, nodes);
            if (nodes.isEmpty()) overlay.requestFocus(); else nodes.getFirst().requestFocus();
        } catch (RuntimeException failure) { close(); throw failure; }
    }
    @Override public void close() {
        Util.requireFxThread();
        if (app == null) return;
        app.getChildren().remove(overlay);
        app.release(this);
        app = null;
        theme.close();
        anchor.sceneProperty().removeListener(detached);
        if (window != null) window.showingProperty().removeListener(windowHidden);
        window = null;
        if (previousFocus != null && previousFocus.getScene() != null) previousFocus.requestFocus();
        previousFocus = null;
    }
    private void collectFocusable(Node node, List<Node> result) {
        if (!node.isVisible() || node.isDisabled()) return;
        if (node.isFocusTraversable()) result.add(node);
        if (node instanceof Parent parent) for (var child : parent.getChildrenUnmodifiable()) collectFocusable(child, result);
    }
}
