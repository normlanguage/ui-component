package dev.normlanguage.ui.component;

import javafx.scene.Node;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public final class App extends ConfigProvider implements AutoCloseable {
    private final Set<AutoCloseable> resources = new LinkedHashSet<>();
    private final Message messages = new Message(this);
    private final Notification notifications = new Notification(this);

    public App() { super(); }
    public App(Node content) { super(content); }
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
        try { super.close(); }
        catch (RuntimeException error) { failure = error; }
        try { Util.closeTree(getContent()); }
        catch (RuntimeException error) {
            if (failure == null) failure = error; else failure.addSuppressed(error);
        }
        setContent(null);
        var owned = new ArrayList<>(resources);
        resources.clear();
        for (var resource : owned.reversed()) {
            try { resource.close(); }
            catch (Exception error) {
                if (failure == null) failure = new IllegalStateException("Component cleanup failed", error);
                else failure.addSuppressed(error);
            }
        }
        if (failure != null) throw failure;
    }
}
