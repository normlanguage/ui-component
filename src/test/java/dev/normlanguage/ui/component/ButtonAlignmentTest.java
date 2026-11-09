package dev.normlanguage.ui.component;

import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.transform.Scale;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ButtonAlignmentTest extends FxTest {
    @Test void visibleIconsAreCenteredAtDifferentSizesAndScales() throws Exception {
        fx(() -> {
            for (var literal : new String[]{"mdi2s-stop", "mdi2c-content-copy", "mdi2r-refresh"}) {
                for (int size : new int[]{18, 22, 24}) {
                    var button = new Button("复制地址");
                    button.setIconLiteral(literal);
                    button.setIconSize(size);
                    button.setIconOnly(true);
                    render(button, 40);
                    for (int scale : new int[]{1, 2}) {
                        var parameters = new SnapshotParameters();
                        parameters.setTransform(new Scale(scale, scale));
                        var image = button.snapshot(parameters, null);
                        var bounds = darkBounds(image, 0, (int) image.getWidth());
                        assertEquals(20, (bounds[0] + bounds[2] + 1) / (2.0 * scale), 1, literal + " horizontal center");
                        assertEquals(20, (bounds[1] + bounds[3] + 1) / (2.0 * scale), 1, literal + " vertical center");
                    }
                    button.close();
                }
            }
        });
    }

    @Test void chineseLabelsAndIconsShareVisualCenter() throws Exception {
        fx(() -> {
            var labels = new String[]{"启动代理", "打开代理配置"};
            var icons = new String[]{"mdi2p-play-outline", "mdi2c-cog-outline"};
            for (int index = 0; index < labels.length; index++) {
                var button = new Button(labels[index]);
                button.setIconLiteral(icons[index]);
                render(button, 170);
                var parameters = new SnapshotParameters();
                parameters.setTransform(new Scale(2, 2));
                var image = button.snapshot(parameters, null);
                var graphic = button.getGraphic();
                int split = (int) Math.ceil(graphic.localToScene(graphic.getBoundsInLocal()).getMaxX() * 2);
                var icon = darkBounds(image, 0, split);
                var text = darkBounds(image, split, (int) image.getWidth());
                assertEquals(20, (text[1] + text[3] + 1) / 4.0, 1, labels[index]);
                assertEquals((text[1] + text[3]) / 4.0, (icon[1] + icon[3]) / 4.0, 1, labels[index] + " icon and text");
                capture(button, java.nio.file.Path.of("build/screenshots/button-" + index + ".png"));
                button.close();
            }
        });
    }

    private static void render(Button button, int width) {
        button.setFont(Font.font("Microsoft YaHei UI", 14));
        button.setStyle("-fx-background-color:white;-fx-border-width:0;-fx-padding:8;-fx-text-fill:black;");
        button.setManaged(false);
        var root = new Pane(button);
        new Scene(root, 200, 60);
        root.applyCss();
        button.resizeRelocate(0, 0, width, 40);
        button.layout();
    }

    private static int[] darkBounds(WritableImage image, int fromX, int toX) {
        int left = toX, top = (int) image.getHeight(), right = -1, bottom = -1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = fromX; x < toX; x++) {
                var color = image.getPixelReader().getColor(x, y);
                if (color.getOpacity() > .9 && color.getRed() < .4 && color.getGreen() < .4 && color.getBlue() < .4) {
                    left = Math.min(left, x); right = Math.max(right, x);
                    top = Math.min(top, y); bottom = Math.max(bottom, y);
                }
            }
        }
        assertTrue(right >= left && bottom >= top, "button must render visible content");
        return new int[]{left, top, right, bottom};
    }
}
