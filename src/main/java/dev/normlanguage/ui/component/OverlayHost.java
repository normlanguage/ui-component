package dev.normlanguage.ui.component;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;

import java.util.Objects;

public interface OverlayHost {
    StackPane overlayLayer();

    static Layer wrap(Node content) { return new Layer(content); }

    static OverlayHost nearest(Node anchor) {
        for (Node current = Objects.requireNonNull(anchor); current != null; current = current.getParent())
            if (current instanceof OverlayHost host) return host;
        throw new IllegalStateException("Drawer requires an OverlayHost ancestor");
    }

    final class Layer extends StackPane implements OverlayHost {
        private final StackPane overlays = new StackPane();

        private Layer(Node content) {
            Objects.requireNonNull(content);
            overlays.setPickOnBounds(false);
            getChildren().addAll(content, overlays);
        }

        @Override public StackPane overlayLayer() { return overlays; }
    }
}
