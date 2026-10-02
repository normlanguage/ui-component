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
    static void capture(javafx.scene.Node node, java.nio.file.Path file) {
        var image = node.snapshot(null, null);
        var pixels = image.getPixelReader();
        var output = new java.awt.image.BufferedImage((int) image.getWidth(), (int) image.getHeight(), java.awt.image.BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < output.getHeight(); y++)
            for (int x = 0; x < output.getWidth(); x++) output.setRGB(x, y, pixels.getArgb(x, y));
        try {
            java.nio.file.Files.createDirectories(file.getParent());
            javax.imageio.ImageIO.write(output, "png", file.toFile());
        } catch (java.io.IOException error) { throw new java.io.UncheckedIOException(error); }
    }
}
