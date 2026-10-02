package dev.normlanguage.ui.component;

import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import org.junit.jupiter.api.Test;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import static org.junit.jupiter.api.Assertions.*;

class ImageResourceTest extends FxTest {
    @Test void bundledImageBecomesVisibleAfterBackgroundLoading() throws Exception {
        var ready = new CompletableFuture<Void>();
        var control = new AtomicReference<Image>();
        var scene = new AtomicReference<Scene>();
        fx(() -> {
            var image = new Image("dev/normlanguage/ui/component/gallery/images/alpine-lake.png");
            control.set(image);
            scene.set(new Scene(new StackPane(image), 1024, 768));
            var pixels = image.getImageView().getImage();
            assertNotNull(pixels);
            Runnable check = () -> {
                if (pixels.isError()) ready.completeExceptionally(pixels.getException());
                else if (pixels.getProgress() == 1) ready.complete(null);
            };
            pixels.progressProperty().addListener((observable, previous, next) -> check.run());
            pixels.errorProperty().addListener((observable, previous, next) -> check.run());
            check.run();
        });
        ready.get(10, TimeUnit.SECONDS);
        fx(() -> {
            var image = control.get();
            scene.get().getRoot().applyCss();
            scene.get().getRoot().resize(640, 360);
            scene.get().getRoot().layout();
            assertTrue(image.getImageView().getImage().getWidth() > 0);
            assertTrue(image.getImageView().getBoundsInParent().getWidth() > 0);
            assertTrue(image.getImageView().getBoundsInParent().getHeight() > 0);
            assertTrue(image.getWidth() <= 640);
            assertTrue(image.getHeight() <= 360);
            assertTrue(image.getImageView().getBoundsInParent().getWidth() <= image.getWidth());
            assertTrue(image.getImageView().getBoundsInParent().getHeight() <= image.getHeight());
            ((StackPane) scene.get().getRoot()).getChildren().clear();
        });
    }
}
