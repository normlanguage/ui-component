package dev.normlanguage.ui.component;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public final class BorderBeam extends StackPane implements AutoCloseable {
    private final Rectangle beam = new Rectangle();
    private final Timeline motion = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(beam.strokeDashOffsetProperty(), 0)),
            new KeyFrame(Duration.seconds(4), new KeyValue(beam.strokeDashOffsetProperty(), -235)));
    private final javafx.beans.property.BooleanProperty animated = new javafx.beans.property.SimpleBooleanProperty(this, "animated", true);
    private final ConfigurationConnection configuration = new ConfigurationConnection(this, this::updateMotion);
    private final javafx.beans.InvalidationListener visibility = observable -> updateMotion();
    private final ChangeListener<Scene> sceneChanged = (observable, previous, current) -> {
        if (previous != null) previous.windowProperty().removeListener(visibility);
        if (current != null) current.windowProperty().addListener(visibility);
        updateMotion();
    };
    private javafx.stage.Window window;
    private boolean closed;
    public BorderBeam(Node content) {
        beam.setFill(Color.TRANSPARENT);
        beam.setStroke(Color.TRANSPARENT);
        beam.setStyle("-fx-stroke: -fx-accent;");
        beam.getStyleClass().add("norm-border-beam-path");
        beam.setStrokeWidth(2);
        beam.setMouseTransparent(true);
        beam.getStrokeDashArray().setAll(35.0, 200.0);
        beam.widthProperty().bind(Bindings.max(0, widthProperty().subtract(4)));
        beam.heightProperty().bind(Bindings.max(0, heightProperty().subtract(4)));
        motion.setCycleCount(Animation.INDEFINITE);
        getChildren().addAll(content, beam);
        sceneProperty().addListener(sceneChanged);
        visibleProperty().addListener(visibility);
        animated.addListener(visibility);
        configuration.connect();
    }
    public javafx.beans.property.BooleanProperty animatedProperty() { return animated; }
    public void setAnimated(boolean value) { animated.set(value); }
    public boolean isAnimating() { return motion.getStatus() == Animation.Status.RUNNING; }
    private void updateMotion() {
        if (closed) { motion.stop(); return; }
        var next = getScene() == null ? null : getScene().getWindow();
        if (window != next) {
            if (window != null) window.showingProperty().removeListener(visibility);
            window = next;
            if (window != null) window.showingProperty().addListener(visibility);
        }
        if (animated.get() && ConfigurationConnection.resolve(this).motionEnabled()
                && isVisible() && window != null && window.isShowing()) motion.play();
        else motion.stop();
    }
    @Override public void close() {
        if (closed) return;
        closed = true; motion.stop();
        if (window != null) window.showingProperty().removeListener(visibility);
        if (getScene() != null) getScene().windowProperty().removeListener(visibility);
        sceneProperty().removeListener(sceneChanged);
        configuration.close();
        visibleProperty().removeListener(visibility);
        animated.removeListener(visibility);
        beam.widthProperty().unbind();
        beam.heightProperty().unbind();
        window = null;
    }
}
