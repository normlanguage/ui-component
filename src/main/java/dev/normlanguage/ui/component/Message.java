package dev.normlanguage.ui.component;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.util.Duration;

public final class Message {
    private final App app;
    private final VBox queue = new VBox(8);
    Message(App app) {
        this.app = app;
        queue.setAlignment(Pos.TOP_CENTER);
        queue.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        queue.setPickOnBounds(false);
        StackPane.setAlignment(queue, Pos.TOP_CENTER);
    }
    public int getVisibleCount() { return queue.getChildren().size(); }
    public Handle show(String text, Duration duration) { return show(text, duration, () -> {}); }
    public Handle show(String text, Duration duration, Runnable dismissed) {
        Util.requireFxThread();
        var label = new Label(text);
        label.getStyleClass().add("norm-message");
        StackPane.setAlignment(label, Pos.TOP_CENTER);
        var timer = new PauseTransition(duration);
        var motion = new Motion(label);
        class Entry implements Handle {
            private boolean closed;
            @Override public void close() {
                Util.requireFxThread();
                if (closed) return;
                closed = true;
                motion.close();
                timer.stop(); queue.getChildren().remove(label); app.release(this);
                if (queue.getChildren().isEmpty()) app.getChildren().remove(queue);
                dismissed.run();
            }
        }
        var entry = new Entry();
        app.own(entry);
        if (queue.getParent() == null) app.getChildren().add(queue);
        queue.getChildren().add(label);
        motion.enter(0, -12);
        timer.setOnFinished(event -> entry.close());
        timer.play();
        return entry;
    }
    public Handle success(String text) { return show(text, Duration.seconds(3)); }
}
