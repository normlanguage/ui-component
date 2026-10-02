package dev.normlanguage.ui.component;

import javafx.beans.InvalidationListener;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;

public final class Affix extends StackPane implements AutoCloseable {
    private final ScrollPane scroll;
    private final Node content;
    private final InvalidationListener scrolled = observable -> position();
    private boolean closed;
    public Affix(ScrollPane scroll, Node content) {
        this.scroll = scroll; this.content = content;
        getChildren().add(content);
        sceneProperty().addListener((observable, previous, current) -> {
            if (previous != null) scroll.vvalueProperty().removeListener(scrolled);
            if (!closed && current != null) scroll.vvalueProperty().addListener(scrolled);
            position();
        });
        layoutBoundsProperty().addListener(scrolled);
    }
    private void position() {
        if (closed) return;
        if (getScene() == null || scroll.getScene() != getScene()) { content.setTranslateY(0); return; }
        var viewport = scroll.lookup(".viewport");
        if (viewport == null) return;
        var top = viewport.localToScene(viewport.getBoundsInLocal()).getMinY();
        var own = localToScene(getLayoutBounds()).getMinY();
        content.setTranslateY(Math.max(0, top - own));
    }
    @Override public void close() {
        if (closed) return;
        closed = true;
        scroll.vvalueProperty().removeListener(scrolled);
        content.setTranslateY(0);
    }
}
