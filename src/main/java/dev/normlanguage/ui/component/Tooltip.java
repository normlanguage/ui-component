package dev.normlanguage.ui.component;

import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.util.Duration;

import java.util.Objects;

public final class Tooltip implements AutoCloseable {
    private final Node anchor;
    private final javafx.scene.control.Tooltip popup = new javafx.scene.control.Tooltip();
    private final ConfigProvider contentRoot;
    private final ThemeConnection theme;
    private final PauseTransition focusDelay = new PauseTransition(Duration.millis(500));
    private final ChangeListener<Boolean> focusChanged;
    private final ChangeListener<javafx.scene.Scene> sceneChanged;
    private App owner;
    private boolean closed;

    public Tooltip(Node anchor, String text) {
        this.anchor = Objects.requireNonNull(anchor);
        contentRoot = new ConfigProvider(new Label(Objects.requireNonNull(text)));
        contentRoot.getStyleClass().add("norm-popover");
        theme = new ThemeConnection(anchor, contentRoot);
        popup.setGraphic(contentRoot);
        popup.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        popup.setStyle("-fx-padding: 0; -fx-background-color: transparent;");
        popup.setShowDelay(focusDelay.getDuration());
        popup.setOnShowing(event -> {
            theme.connect();
        });
        popup.setOnHidden(event -> theme.close());
        focusDelay.setOnFinished(event -> {
            if (anchor.isFocused()) show();
        });
        focusChanged = (observable, previous, focused) -> {
            if (focused) focusDelay.playFromStart();
            else {
                focusDelay.stop();
                if (!anchor.isHover()) popup.hide();
            }
        };
        sceneChanged = (observable, previous, current) -> {
            if (current == null) {
                close();
            } else attachOwner();
        };
        javafx.scene.control.Tooltip.install(anchor, popup);
        anchor.focusedProperty().addListener(focusChanged);
        anchor.sceneProperty().addListener(sceneChanged);
        attachOwner();
    }
    public void setDelay(Duration duration) {
        Util.requireFxThread();
        if (duration == null || duration.lessThan(Duration.ZERO)) throw new IllegalArgumentException("Delay must be nonnegative");
        focusDelay.setDuration(duration);
        popup.setShowDelay(duration);
    }
    public ConfigProvider getContentRoot() { return contentRoot; }
    public boolean isShowing() { return popup.isShowing(); }
    public boolean isClosed() { return closed; }
    public void show() {
        Util.requireFxThread();
        if (closed || popup.isShowing() || anchor.getScene() == null || anchor.getScene().getWindow() == null
                || !anchor.getScene().getWindow().isShowing()) return;
        attachOwner();
        var bounds = anchor.localToScreen(anchor.getBoundsInLocal());
        if (bounds != null) popup.show(anchor, bounds.getMinX(), bounds.getMaxY() + 4);
    }
    public void hide() { Util.requireFxThread(); popup.hide(); }
    private void attachOwner() {
        if (closed || owner != null) return;
        for (Node current = anchor; current != null; current = current.getParent()) {
            if (current instanceof App app && !app.isClosed()) {
                owner = app;
                app.own(this);
                return;
            }
        }
    }
    @Override public void close() {
        Util.requireFxThread();
        if (closed) return;
        closed = true;
        focusDelay.stop();
        popup.hide();
        theme.close();
        javafx.scene.control.Tooltip.uninstall(anchor, popup);
        anchor.focusedProperty().removeListener(focusChanged);
        anchor.sceneProperty().removeListener(sceneChanged);
        contentRoot.close();
        popup.setGraphic(null);
        if (owner != null) { owner.release(this); owner = null; }
    }
}
