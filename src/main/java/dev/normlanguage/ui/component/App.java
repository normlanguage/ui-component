package dev.normlanguage.ui.component;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class App extends ConfigProvider implements OverlayHost {
    private final Set<AutoCloseable> resources = new LinkedHashSet<>();
    private final StackPane overlays = new StackPane();
    private final Message messages = new Message(this);
    private final Notification notifications = new Notification(this);

    public App() { this(null, ContentOwnership.OWNED); }
    public App(Node content) { this(content, ContentOwnership.OWNED); }
    public App(Node content, ContentOwnership ownership) {
        super(content, ownership);
        overlays.setPickOnBounds(false);
        getChildren().add(overlays);
    }
    @Override public StackPane overlayLayer() { return overlays; }
    public Message getMessages() { return messages; }
    public Notification getNotifications() { return notifications; }
    public void own(AutoCloseable resource) {
        Util.requireFxThread();
        if (isClosed()) throw new IllegalStateException("Component root is closed");
        resources.add(Objects.requireNonNull(resource));
    }
    public void release(AutoCloseable resource) { Util.requireFxThread(); resources.remove(resource); }
    public void onClose(Runnable action) { own(action::run); }
    @Override public void close() {
        Util.requireFxThread();
        if (isClosed()) return;
        RuntimeException failure = null;
        var owned = new ArrayList<>(resources);
        resources.clear();
        for (var resource : owned.reversed()) {
            try { resource.close(); }
            catch (Exception error) {
                if (failure == null) failure = new IllegalStateException("Component cleanup failed", error);
                else failure.addSuppressed(error);
            }
        }
        try { super.close(); }
        catch (RuntimeException error) {
            if (failure == null) failure = error; else failure.addSuppressed(error);
        }
        if (failure != null) throw failure;
    }
}
