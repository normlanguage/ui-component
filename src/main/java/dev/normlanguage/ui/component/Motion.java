package dev.normlanguage.ui.component;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WritableValue;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.util.Duration;
import java.util.Objects;

public final class Motion implements AutoCloseable {
    public static final Duration FAST = Duration.millis(120);
    public static final Duration STANDARD = Duration.millis(200);
    public static final Duration ENTER = Duration.millis(240);
    public static final Interpolator EASING = Interpolator.SPLINE(0.2, 0.8, 0.2, 1);
    private final Node node;
    private final javafx.animation.Timeline timeline = new javafx.animation.Timeline();
    private final ConfigurationConnection configuration;
    private final ChangeListener<Scene> sceneChanged;
    private KeyValue[] destination = new KeyValue[0];
    private boolean closed;

    public Motion(Node node) {
        this.node = Objects.requireNonNull(node);
        configuration = new ConfigurationConnection(node, () -> {
            if (!ConfigurationConnection.resolve(node).motionEnabled()) finish();
        });
        sceneChanged = (observable, old, scene) -> {
            if (scene == null) { finish(); configuration.close(); }
            else configuration.connect();
        };
        node.sceneProperty().addListener(sceneChanged);
        if (node.getScene() != null) configuration.connect();
    }
    public void enter(double x, double y) {
        finish();
        node.setOpacity(0);
        node.setTranslateX(x);
        node.setTranslateY(y);
        animate(ENTER, new KeyValue(node.opacityProperty(), 1, EASING),
                new KeyValue(node.translateXProperty(), 0, EASING),
                new KeyValue(node.translateYProperty(), 0, EASING));
    }
    public void animate(Duration duration, KeyValue... values) {
        Util.requireFxThread();
        timeline.stop();
        destination = values.clone();
        if (closed || node.getScene() == null || !ConfigurationConnection.resolve(node).motionEnabled()) {
            finish();
            return;
        }
        timeline.getKeyFrames().setAll(new KeyFrame(duration, destination));
        timeline.playFromStart();
    }
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void finish() {
        Util.requireFxThread();
        timeline.stop();
        for (var value : destination) ((WritableValue) value.getTarget()).setValue(value.getEndValue());
        destination = new KeyValue[0];
    }
    @Override public void close() {
        if (closed) return;
        finish();
        closed = true;
        configuration.close();
        node.sceneProperty().removeListener(sceneChanged);
    }
}
