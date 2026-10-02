package dev.normlanguage.ui.component;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.util.Duration;
import java.util.Objects;

public final class NoticeHost extends Region implements AutoCloseable {
    private final boolean notification;
    private String text = "";
    private double duration = 3000;
    private Node content;
    private boolean open;
    private boolean closed;
    private boolean synchronizing;
    private Handle handle;
    private Runnable dismissed = () -> {};
    private final ChangeListener<Scene> sceneChanged = (observable, before, after) -> {
        hide();
        reconcile();
    };
    private NoticeHost(boolean notification) {
        this.notification = notification;
        setManaged(false);
        setVisible(false);
        sceneProperty().addListener(sceneChanged);
    }
    public static NoticeHost message() { return new NoticeHost(false); }
    public static NoticeHost notification() { return new NoticeHost(true); }
    public void content(Node value) {
        if (content == value) return;
        hide();
        content = value;
        reconcile();
    }
    public void update(String text, double duration, boolean open, Runnable dismissed) {
        Util.requireFxThread();
        if (closed) throw new IllegalStateException("Notice is closed");
        if (!Double.isFinite(duration) || duration <= 0) throw new IllegalArgumentException("Duration must be positive");
        if (!Objects.equals(this.text, text) || this.duration != duration) hide();
        this.text = Objects.requireNonNull(text);
        this.duration = duration;
        this.open = open;
        this.dismissed = Objects.requireNonNull(dismissed);
        reconcile();
    }
    private void reconcile() {
        if (closed || !open || getScene() == null) { hide(); return; }
        if (handle != null || notification && content == null) return;
        var app = Util.app(this);
        Runnable hidden = () -> {
            handle = null;
            if (!closed && !synchronizing) {
                open = false;
                dismissed.run();
            }
        };
        handle = notification ? app.getNotifications().show(text, content, hidden)
                : app.getMessages().show(text, Duration.millis(duration), hidden);
    }
    public void dismiss() {
        Util.requireFxThread();
        if (handle != null) handle.close();
    }
    private void hide() {
        if (handle == null) return;
        synchronizing = true;
        try { handle.close(); } finally { handle = null; synchronizing = false; }
    }
    @Override public void close() {
        Util.requireFxThread();
        if (closed) return;
        closed = true;
        sceneProperty().removeListener(sceneChanged);
        hide();
        content = null;
        dismissed = () -> {};
    }
}
