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
    private App owner;
    private Node previousFocus;
    private boolean initialized;

    public Modal(Node anchor, String title, Node body) {
        this.anchor = java.util.Objects.requireNonNull(anchor);
        detached = observable -> { if (anchor.getScene() == null) close(); };
        content = new ConfigProvider(body);
        theme = new ThemeConnection(anchor, content);
        motion = new Motion(content);
        content.getStyleClass().add("norm-card");
        stage.setTitle(title);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(new Scene(content));
        stage.setOnHidden(event -> disconnect());
        content.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ESCAPE) { close(); event.consume(); }
        });
    }
    public boolean isShowing() { return stage.isShowing(); }
    public Window getWindow() { return stage; }
    public ConfigProvider getContentRoot() { return content; }
    public void show() {
        Util.requireFxThread();
        if (stage.isShowing()) return;
        owner = Util.app(anchor);
        if (anchor.getScene() == null || anchor.getScene().getWindow() == null) throw new IllegalStateException("Modal requires a window");
        var window = anchor.getScene().getWindow();
        if (!initialized) { stage.initOwner(window); initialized = true; }
        else if (stage.getOwner() != window) throw new IllegalStateException("Create a new modal after moving to another window");
        previousFocus = anchor.getScene().getFocusOwner();
        owner.own(this);
        theme.connect();
        anchor.sceneProperty().addListener(detached);
        try { stage.show(); motion.enter(0, 18); }
        catch (RuntimeException failure) { disconnect(); throw failure; }
    }
    private void disconnect() {
        motion.finish();
        theme.close();
        anchor.sceneProperty().removeListener(detached);
        if (owner != null) { owner.release(this); owner = null; }
        if (previousFocus != null && previousFocus.getScene() != null) previousFocus.requestFocus();
        previousFocus = null;
    }
    @Override public void close() { Util.requireFxThread(); stage.hide(); disconnect(); }
}
