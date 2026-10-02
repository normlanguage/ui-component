package dev.normlanguage.ui.component;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WatermarkTest extends FxTest {
    @Test void imageWatermarkRepeatsAboveContentAndCanReturnToText() throws Exception {
        fx(() -> {
            var image = new WritableImage(16, 12);
            for (int y = 0; y < 12; y++) for (int x = 0; x < 16; x++)
                image.getPixelWriter().setColor(x, y, Color.RED);
            var watermark = new Watermark(new Region(), "Norm");
            watermark.setImage(image);
            var stage = new Stage();
            stage.setScene(new Scene(watermark, 240, 180));
            stage.show();
            try {
                watermark.layout();
                var overlay = (Canvas) watermark.getChildren().get(1);
                assertTrue(overlay.getWidth() > 0 && overlay.getHeight() > 0,
                        "canvas=" + overlay.getWidth() + "x" + overlay.getHeight());
                var snapshot = overlay.snapshot(null, null);
                long imagePixels = 0;
                for (int y = 0; y < snapshot.getHeight(); y++)
                    for (int x = 0; x < snapshot.getWidth(); x++) {
                        var pixel = snapshot.getPixelReader().getColor(x, y);
                        if (pixel.getRed() - pixel.getGreen() > 0.1 && pixel.getOpacity() > 0)
                            imagePixels++;
                    }
                assertTrue(imagePixels > 100, "imagePixels=" + imagePixels + " canvas=" + overlay.getWidth() + "x" + overlay.getHeight());
                watermark.setImage(null);
                watermark.layout();
                assertNull(watermark.getImage());
                assertEquals("Norm", watermark.textProperty().get());
            } finally { stage.close(); }
        });
    }
}
