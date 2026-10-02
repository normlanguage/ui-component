package dev.normlanguage.ui.component.gallery;

import javafx.application.Platform;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public final class FxHarness {
    private FxHarness() {}
    public static void run(Runnable action) throws Exception {
        var started = new CountDownLatch(1);
        try { Platform.startup(() -> { Platform.setImplicitExit(false); started.countDown(); }); }
        catch (IllegalStateException running) { started.countDown(); }
        if (!started.await(20, TimeUnit.SECONDS)) throw new IllegalStateException("JavaFX startup timeout");
        var task = new FutureTask<Void>(action, null);
        Platform.runLater(task);
        task.get(30, TimeUnit.SECONDS);
    }
    public static void stop() { Platform.exit(); }
}
