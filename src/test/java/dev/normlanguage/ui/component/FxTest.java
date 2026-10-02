package dev.normlanguage.ui.component;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

abstract class FxTest {
    @BeforeAll
    static void toolkit() throws Exception {
        var ready = new CountDownLatch(1);
        try { Platform.startup(() -> { Platform.setImplicitExit(false); ready.countDown(); }); }
        catch (IllegalStateException running) { ready.countDown(); }
        if (!ready.await(20, TimeUnit.SECONDS)) throw new AssertionError("JavaFX startup timed out");
    }

    static void fx(Runnable action) throws Exception {
        var task = new FutureTask<Void>(action, null);
        Platform.runLater(task);
        task.get(20, TimeUnit.SECONDS);
    }
}
