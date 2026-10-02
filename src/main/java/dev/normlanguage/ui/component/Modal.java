package dev.normlanguage.ui.component;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Modal implements AutoCloseable {
    private final Node anchor;
    private final Stage stage = new Stage();
    private final ConfigProvider content;
    private final ThemeConnection theme;
    private final Motion motion;
    private final javafx.beans.InvalidationListener detached;
    private Node previousFocus;
    private boolean initialized;
    private boolean closed;

    public Modal(Node anchor, String title, Node body) { this(anchor, title, body, ContentOwnership.OWNED); }
    public Modal(Node anchor, String title, Node body, ContentOwnership ownership) {
        this.anchor = java.util.Objects.requireNonNull(anchor);
        detached = observable -> { if (anchor.getScene() == null) close(); };
        content = new ConfigProvider(body, ownership);
        theme = new ThemeConnection(anchor, content);
        motion = new Motion(content);
        content.getStyleClass().add("norm-card");
        stage.setTitle(title);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(content));
        stage.setOnHidden(event -> disconnect());
        content.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) { hide(); event.consume(); }
        });
    }
    public boolean isShowing() { return stage.isShowing(); }
    public Window getWindow() { return stage; }
    public ConfigProvider getContentRoot() { return content; }
    public void show() {
        Util.requireFxThread();
        if (closed) throw new IllegalStateException("Modal is closed");
        if (stage.isShowing()) return;
        if (anchor.getScene() == null || anchor.getScene().getWindow() == null) throw new IllegalStateException("Modal requires a window");
        var window = anchor.getScene().getWindow();
        if (!initialized) { stage.initOwner(window); initialized = true; }
        else if (stage.getOwner() != window) throw new IllegalStateException("Create a new modal after moving to another window");
        previousFocus = anchor.getScene().getFocusOwner();
        theme.connect();
        anchor.sceneProperty().addListener(detached);
        try { stage.show(); motion.enter(0, 18); }
        catch (RuntimeException failure) { disconnect(); throw failure; }
    }
    private void disconnect() {
        motion.finish();
        theme.close();
        anchor.sceneProperty().removeListener(detached);
        if (previousFocus != null && previousFocus.getScene() != null) previousFocus.requestFocus();
        previousFocus = null;
    }
    public void hide() { Util.requireFxThread(); stage.hide(); disconnect(); }
    @Override public void close() {
        Util.requireFxThread();
        if (closed) return;
        closed = true;
        hide();
        motion.close();
        content.close();
        stage.setOnHidden(null);
        stage.setScene(null);
    }
}
