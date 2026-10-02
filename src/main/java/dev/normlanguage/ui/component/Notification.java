package dev.normlanguage.ui.component;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;

public final class Notification {
    private final App app;
    private final VBox queue = new VBox(8);
    Notification(App app) {
        this.app = app;
        queue.setMaxSize(320, javafx.scene.layout.Region.USE_PREF_SIZE);
        queue.setPickOnBounds(false);
        StackPane.setAlignment(queue, Pos.TOP_RIGHT);
    }
    public int getVisibleCount() { return queue.getChildren().size(); }
    Node getContainer() { return queue; }
    public Handle show(String title, Node content) { return show(title, content, () -> {}); }
    public Handle show(String title, Node content, Runnable dismissed) {
        Util.requireFxThread();
        var dismiss = new Button("×");
        var box = new VBox(8, new Label(title), content, dismiss);
        box.setMaxSize(320, javafx.scene.layout.Region.USE_PREF_SIZE);
        box.getStyleClass().add("norm-card");
        var motion = new Motion(box);
        class Entry implements Handle {
            private boolean closed;
            @Override public void close() {
                Util.requireFxThread();
                if (closed) return;
                closed = true;
                motion.close();
                queue.getChildren().remove(box);
                app.release(this);
                if (queue.getChildren().isEmpty()) app.getChildren().remove(queue);
                dismissed.run();
            }
        }
        var entry = new Entry();
        app.own(entry);
        dismiss.setOnAction(event -> entry.close());
        if (queue.getParent() == null) app.getChildren().add(queue);
        queue.getChildren().add(box);
        motion.enter(28, 0);
        return entry;
    }
}
