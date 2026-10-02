package dev.normlanguage.ui.component;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.PopupControl;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

@Tag("theme-rendering")
class ThemeRenderingTest extends FxTest {
    private static final Path THEMES = Path.of("build", "themes");
    private static final Path PREVIEWS = Path.of("build", "previews");

    @Test void realControlsRenderBothThemesAndPreserveEditing() throws Exception {
        String light = fixture("light.css");
        String dark = fixture("dark.css");
        try (var warnings = new CssWarnings()) {
            fx(() -> {
                var input = new Input("editing");
                input.setPrefWidth(220);
                var button = new Button("Save");
                var icon = new Icon("mdi2h-home");
                var select = new Select<String>();
                select.getItems().addAll("One", "Two", "Three");
                select.setValue("Two");
                select.setPrefWidth(150);
                var date = new DatePicker();
                date.setPrefWidth(180);
                var slider = new Slider(0, 100, 42);
                slider.setPrefWidth(430);
                var table = new Table<String>();
                table.setSource(FXCollections.observableArrayList("Alpha", "Beta", "Gamma"));
                table.column("Item", item -> item);
                table.setPrefSize(460, 260);
                var calendar = new Calendar();
                var title = new Label("Component theme preview");
                title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
                var subtitle = new Label("Theme switching keeps control instances and editing state intact.");
                subtitle.getStyleClass().add("norm-muted");
                var tableCard = new VBox(12, new Label("Table"), table);
                tableCard.getStyleClass().add("norm-card");
                var calendarCard = new VBox(12, new Label("Calendar"), calendar);
                calendarCard.getStyleClass().add("norm-card");
                var content = new VBox(20,
                    title, subtitle,
                    new HBox(12, icon, button, input, select, date),
                    new VBox(8, new Label("Slider"), slider),
                    new HBox(20, tableCard, calendarCard));
                content.setPadding(new Insets(24));
                var scroll = new ScrollPane(content);
                scroll.setFitToWidth(true);
                var app = new App(scroll);
                var stage = new Stage();
                stage.setScene(new Scene(app, 1040, 680));
                stage.show();
                try {
                    app.setThemeCss(light);
                    stage.getScene().getRoot().applyCss();
                    stage.getScene().getRoot().layout();
                    Color lightCanvas = fill(app);
                    Paint lightInput = paint(input);
                    Paint lightButton = paint(button);
                    Paint lightIcon = icon.getIconColor();
                    save(stage.getScene(), "light.png");
                    input.requestFocus();
                    input.selectRange(1, 4);
                    app.setThemeCss(dark);
                    stage.getScene().getRoot().applyCss();
                    stage.getScene().getRoot().layout();
                    Color darkCanvas = fill(app);
                    Paint darkInput = paint(input);
                    Paint darkButton = paint(button);
                    Paint darkIcon = icon.getIconColor();
                    save(stage.getScene(), "dark.png");
                    assertTrue(lightCanvas.getBrightness() > darkCanvas.getBrightness());
                    assertNotEquals(lightInput, darkInput);
                    assertNotEquals(lightButton, darkButton);
                    assertNotEquals(lightIcon, darkIcon);
                    assertEquals("editing", input.getText());
                    assertTrue(input.isFocused());
                    assertEquals(1, input.getSelection().getStart());
                    assertEquals(4, input.getSelection().getEnd());
                    assertEquals("Two", select.getValue());
                    assertEquals(42, slider.getValue());
                } finally { app.close(); stage.close(); }
            });
            warnings.assertClean();
        }
    }

    @Test void nativeChoiceAndDatePopupsFollowTheme() throws Exception {
        String light = fixture("light.css");
        String dark = fixture("dark.css");
        try (var warnings = new CssWarnings()) {
            var appRef = new AtomicReference<App>();
            var stageRef = new AtomicReference<Stage>();
            var selectRef = new AtomicReference<Select<String>>();
            var dateRef = new AtomicReference<DatePicker>();
            fx(() -> {
                var select = new Select<String>();
                select.getItems().addAll("One", "Two");
                var date = new DatePicker();
                var app = new App(new HBox(12, select, date));
                var stage = new Stage();
                stage.setScene(new Scene(app, 460, 180));
                stage.show();
                appRef.set(app);
                stageRef.set(stage);
                selectRef.set(select);
                dateRef.set(date);
            });
            try {
                var comboLight = new AtomicReference<Color>();
                var dateLight = new AtomicReference<Color>();
                fx(() -> {
                    appRef.get().setThemeCss(light);
                    appRef.get().applyCss();
                    selectRef.get().show();
                });
                fx(() -> {
                    comboLight.set(popupFill(".list-view"));
                    selectRef.get().hide();
                    dateRef.get().show();
                });
                fx(() -> {
                    dateLight.set(popupFill(".date-picker-popup"));
                    dateRef.get().hide();
                    appRef.get().setThemeCss(dark);
                    appRef.get().applyCss();
                    selectRef.get().show();
                });
                fx(() -> {
                    Color comboDark = popupFill(".list-view");
                    selectRef.get().hide();
                    dateRef.get().show();
                    assertNotEquals(comboLight.get(), comboDark);
                });
                fx(() -> {
                    Color dateDark = popupFill(".date-picker-popup");
                    dateRef.get().hide();
                    assertNotEquals(dateLight.get(), dateDark);
                });
            } finally {
                fx(() -> { appRef.get().close(); stageRef.get().close(); });
            }
            warnings.assertClean();
        }
    }

    @Test void nativePopupsInheritNearestConfigScope() throws Exception {
        String light = fixture("light.css");
        String dark = fixture("dark.css");
        try (var warnings = new CssWarnings()) {
            var appRef = new AtomicReference<App>();
            var localRef = new AtomicReference<ConfigProvider>();
            var stageRef = new AtomicReference<Stage>();
            var selectRef = new AtomicReference<Select<String>>();
            var dateRef = new AtomicReference<DatePicker>();
            fx(() -> {
                var select = new Select<String>();
                select.getItems().addAll("One", "Two");
                var date = new DatePicker();
                var local = new ConfigProvider(new HBox(12, select, date));
                var app = new App(local);
                app.setThemeCss(light);
                local.setThemeCss(light);
                var stage = new Stage();
                stage.setScene(new Scene(app, 460, 180));
                stage.show();
                appRef.set(app);
                localRef.set(local);
                stageRef.set(stage);
                selectRef.set(select);
                dateRef.set(date);
            });
            try {
                var choiceLight = new AtomicReference<Color>();
                var dateLight = new AtomicReference<Color>();
                fx(() -> selectRef.get().show());
                fx(() -> {
                    choiceLight.set(popupFill(".list-view"));
                    selectRef.get().hide();
                    dateRef.get().show();
                });
                fx(() -> {
                    dateLight.set(popupFill(".date-picker-popup"));
                    dateRef.get().hide();
                    localRef.get().setThemeCss(dark);
                    appRef.get().applyCss();
                    selectRef.get().show();
                });
                fx(() -> {
                    assertNotEquals(choiceLight.get(), popupFill(".list-view"));
                    selectRef.get().hide();
                    dateRef.get().show();
                });
                fx(() -> {
                    assertNotEquals(dateLight.get(), popupFill(".date-picker-popup"));
                    dateRef.get().hide();
                    assertEquals(light, appRef.get().getThemeCss());
                });
            } finally {
                fx(() -> { appRef.get().close(); stageRef.get().close(); });
            }
            warnings.assertClean();
        }
    }

    private static String fixture(String name) throws IOException {
        Path path = THEMES.resolve(name);
        assertTrue(Files.isRegularFile(path), "Missing exported theme fixture: " + path);
        return Files.readString(path);
    }
    private static Color fill(Region region) {
        var resolved = paint(region);
        assertInstanceOf(Color.class, resolved);
        return (Color) resolved;
    }
    private static Paint paint(Region region) {
        assertNotNull(region.getBackground(), "No computed background for " + region.getClass().getSimpleName());
        assertFalse(region.getBackground().getFills().isEmpty(), "No computed fill for " + region.getClass().getSimpleName());
        return region.getBackground().getFills().getLast().getFill();
    }
    private static Color popupFill(String selector) {
        PopupControl popup = Window.getWindows().stream()
            .filter(Window::isShowing)
            .filter(PopupControl.class::isInstance)
            .map(PopupControl.class::cast)
            .findFirst().orElseThrow(() -> new AssertionError("Native popup is not showing"));
        popup.getScene().getRoot().applyCss();
        var target = popup.getScene().getRoot().lookup(selector);
        assertInstanceOf(Region.class, target, "Native popup is missing " + selector);
        return fill((Region) target);
    }
    private static void save(Scene scene, String name) {
        var image = scene.snapshot(null);
        var output = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        var pixels = image.getPixelReader();
        for (int y = 0; y < output.getHeight(); y++)
            for (int x = 0; x < output.getWidth(); x++) output.setRGB(x, y, pixels.getArgb(x, y));
        try {
            Files.createDirectories(PREVIEWS);
            assertTrue(ImageIO.write(output, "png", PREVIEWS.resolve(name).toFile()));
        } catch (IOException failure) { throw new IllegalStateException("Could not save theme preview", failure); }
    }
    private static final class CssWarnings implements AutoCloseable {
        private final CopyOnWriteArrayList<String> messages = new CopyOnWriteArrayList<>();
        private final Handler handler = new Handler() {
            @Override public void publish(LogRecord record) {
                String name = record.getLoggerName();
                if (record.getLevel().intValue() >= Level.WARNING.intValue() && name != null &&
                    (name.contains("Css") || name.contains("css"))) messages.add(name + ": " + record.getMessage());
            }
            @Override public void flush() {}
            @Override public void close() {}
        };
        private CssWarnings() { Logger.getLogger("").addHandler(handler); }
        private void assertClean() {
            assertTrue(messages.isEmpty(), () -> "JavaFX CSS warnings: " + messages.stream().limit(5).toList());
        }
        @Override public void close() { Logger.getLogger("").removeHandler(handler); }
    }
}
