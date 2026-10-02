package dev.normlanguage.ui.component;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollBar;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

@Tag("theme-rendering")
class FlatInputStyleTest extends FxTest {
    @Test void themedEditorsAndIndicatorsUseSolidSingleLayerFills() throws Exception {
        String light = Files.readString(Path.of("build/themes/light.css"));
        String dark = Files.readString(Path.of("build/themes/dark.css"));
        fx(() -> {
            var input = new Input("Text");
            var password = Input.password();
            var area = Input.multiline();
            area.setPrefRowCount(2);
            var number = new InputNumber();
            var select = new Select<String>();
            select.getItems().addAll("One", "Two");
            select.setValue("One");
            var choice = new ChoiceBox<String>();
            choice.getItems().addAll("One", "Two");
            choice.setValue("One");
            var date = new DatePicker();
            date.setValue(java.time.LocalDate.of(2026, 10, 2));
            var color = new ColorPicker();
            var checkbox = new Checkbox("Check");
            var radio = new Radio("Radio");
            var toggle = new Switch("Toggle");
            var slider = new Slider(0, 100, 35);
            slider.setMaxWidth(260);
            var scroll = new ScrollBar();
            scroll.setMaxWidth(260);
            var content = new VBox(10, input, password, area, number, select, choice, date, color,
                    checkbox, radio, toggle, slider, scroll);
            content.setPadding(new Insets(24));
            var app = new App(content);
            var stage = new Stage();
            stage.setScene(new Scene(app, 420, 700));
            stage.show();
            try {
                app.setThemeCss(light);
                app.applyCss();
                app.layout();
                var fieldLight = solid(input);
                for (Region field : List.of(input, password, number, select, choice, date))
                    assertEquals(32, field.getHeight(), 1, field.getStyleClass().toString());
                for (Region field : List.of(input, password, area, number, select, choice, date, color.getNativePicker()))
                    solid(field);
                solid(number.getEditor());
                solid((Region) checkbox.lookup(".box"));
                solid((Region) radio.lookup(".radio"));
                solid((Region) slider.lookup(".track"));
                solid((Region) scroll.lookup(".track"));
                assertTrackContained(slider);
                assertTrackContained(color.getAlphaSlider());
                assertEquals(Color.TRANSPARENT, solid(scroll));
                solid(toggle);
                assertEquals(22, toggle.getHeight(), 1);
                var switchTrack = (Region) toggle.lookup(".norm-switch-track");
                var switchOff = solid(switchTrack);
                toggle.setSelected(true);
                app.applyCss();
                assertEquals(Color.TRANSPARENT, solid(toggle));
                assertNotEquals(switchOff, solid(switchTrack));
                save(stage.getScene(), "flat-inputs-light.png");
                app.setThemeCss(dark);
                app.applyCss();
                app.layout();
                assertNotEquals(fieldLight, solid(input));
                for (Region field : List.of(input, password, area, number, select, choice, date, color.getNativePicker()))
                    solid(field);
                save(stage.getScene(), "flat-inputs-dark.png");
            } finally { app.close(); stage.close(); }
        });
    }

    @Test void nativeChoicePopupUsesSolidBackgroundAcrossThemes() throws Exception {
        String light = Files.readString(Path.of("build/themes/light.css"));
        String dark = Files.readString(Path.of("build/themes/dark.css"));
        var appRef = new AtomicReference<App>();
        var selectRef = new AtomicReference<Select<String>>();
        var stageRef = new AtomicReference<Stage>();
        fx(() -> {
            var select = new Select<String>();
            select.getItems().addAll("One", "Two");
            var app = new App(select);
            var stage = new Stage();
            stage.setScene(new Scene(app, 320, 160));
            stage.show();
            appRef.set(app);
            selectRef.set(select);
            stageRef.set(stage);
        });
        try {
            fx(() -> { appRef.get().setThemeCss(light); appRef.get().applyCss(); selectRef.get().show(); });
            var lightFill = new AtomicReference<Color>();
            fx(() -> { lightFill.set(popupFill()); selectRef.get().hide(); appRef.get().setThemeCss(dark); appRef.get().applyCss(); selectRef.get().show(); });
            fx(() -> { assertNotEquals(lightFill.get(), popupFill()); selectRef.get().hide(); });
        } finally { fx(() -> { appRef.get().close(); stageRef.get().close(); }); }
    }

    @Test void choiceBoxPopupUsesSolidBackgroundAcrossThemes() throws Exception {
        String light = Files.readString(Path.of("build/themes/light.css"));
        String dark = Files.readString(Path.of("build/themes/dark.css"));
        var appRef = new AtomicReference<App>();
        var choiceRef = new AtomicReference<ChoiceBox<String>>();
        var stageRef = new AtomicReference<Stage>();
        fx(() -> {
            var choice = new ChoiceBox<String>();
            choice.getItems().addAll("One", "Two");
            var app = new App(choice);
            var stage = new Stage();
            stage.setScene(new Scene(app, 320, 160));
            stage.show();
            appRef.set(app);
            choiceRef.set(choice);
            stageRef.set(stage);
        });
        try {
            fx(() -> { appRef.get().setThemeCss(light); appRef.get().applyCss(); choiceRef.get().show(); });
            var lightFill = new AtomicReference<Color>();
            fx(() -> { lightFill.set(popupFill(".context-menu")); choiceRef.get().hide(); appRef.get().setThemeCss(dark); appRef.get().applyCss(); choiceRef.get().show(); });
            fx(() -> { assertNotEquals(lightFill.get(), popupFill(".context-menu")); choiceRef.get().hide(); });
        } finally { fx(() -> { appRef.get().close(); stageRef.get().close(); }); }
    }

    @Test void datePopupHeaderUsesSolidFill() throws Exception {
        String light = Files.readString(Path.of("build/themes/light.css"));
        var appRef = new AtomicReference<App>();
        var dateRef = new AtomicReference<DatePicker>();
        var stageRef = new AtomicReference<Stage>();
        fx(() -> {
            var date = new DatePicker();
            var app = new App(date);
            var stage = new Stage();
            stage.setScene(new Scene(app, 320, 160));
            stage.show();
            appRef.set(app);
            dateRef.set(date);
            stageRef.set(stage);
        });
        try {
            fx(() -> { appRef.get().setThemeCss(light); appRef.get().applyCss(); dateRef.get().show(); });
            fx(() -> { popupFill(".month-year-pane"); dateRef.get().hide(); });
        } finally { fx(() -> { appRef.get().close(); stageRef.get().close(); }); }
    }

    @Test void ratingStarsHaveNoButtonPanelAndKeepFocusVisible() throws Exception {
        String light = Files.readString(Path.of("build/themes/light.css"));
        fx(() -> {
            var rate = new Rate(5);
            rate.setValue(3);
            var app = new App(rate);
            var stage = new Stage();
            stage.setScene(new Scene(app, 320, 120));
            stage.show();
            try {
                app.setThemeCss(light);
                app.applyCss();
                var first = (javafx.scene.control.ToggleButton) rate.getChildren().getFirst();
                var last = (javafx.scene.control.ToggleButton) rate.getChildren().getLast();
                assertEquals(Color.TRANSPARENT, solid(first));
                assertEquals(Color.TRANSPARENT, solid(last));
                assertNotEquals(first.getTextFill(), last.getTextFill());
                last.requestFocus();
                app.applyCss();
                assertTrue(last.isFocused());
                assertNotNull(last.getBorder());
                assertFalse(last.getBorder().getStrokes().isEmpty());
            } finally { app.close(); stage.close(); }
        });
    }

    @Test void switchHasTrackAndThumbWithDistinctSelectedPositions() throws Exception {
        fx(() -> {
            var control = new Switch("Notifications");
            var app = new App(control);
            var config = ComponentConfig.defaults();
            app.setConfig(new ComponentConfig(config.fontFamily(), config.fontSize(), config.density(), config.radius(), false, config.locale()));
            var stage = new Stage();
            stage.setScene(new Scene(app, 200, 120));
            stage.show();
            try {
                app.applyCss();
                assertInstanceOf(Region.class, control.getGraphic());
                assertNotEquals(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY, control.getContentDisplay());
                var thumb = (Region) control.lookup(".norm-switch-thumb");
                assertNotNull(thumb);
                assertTrue(control.getWidth() > control.getHeight());
                double first = thumb.getTranslateX();
                control.setSelected(true);
                app.applyCss();
                assertTrue(thumb.getTranslateX() > first);
            } finally { app.close(); stage.close(); }
        });
    }

    private static Color solid(Region region) {
        assertNotNull(region);
        assertNotNull(region.getBackground());
        assertEquals(1, region.getBackground().getFills().size(), region.getStyleClass().toString());
        var fill = region.getBackground().getFills().getFirst().getFill();
        assertInstanceOf(Color.class, fill, region.getStyleClass().toString());
        return (Color) fill;
    }
    private static void assertTrackContained(javafx.scene.control.Slider slider) {
        var track = slider.lookup(".track");
        assertNotNull(track);
        var trackBounds = track.localToScene(track.getBoundsInLocal());
        var sliderBounds = slider.localToScene(slider.getBoundsInLocal());
        assertTrue(trackBounds.getMinX() >= sliderBounds.getMinX() - 1 &&
                   trackBounds.getMaxX() <= sliderBounds.getMaxX() + 1,
                   () -> "Track " + trackBounds + " escapes slider " + sliderBounds);
    }
    private static Color popupFill() {
        return popupFill(".list-view");
    }
    private static Color popupFill(String selector) {
        var target = javafx.stage.Window.getWindows().stream()
                .filter(javafx.stage.Window::isShowing)
                .filter(javafx.scene.control.PopupControl.class::isInstance)
                .map(javafx.scene.control.PopupControl.class::cast)
                .flatMap(popup -> {
                    popup.getScene().getRoot().applyCss();
                    return popup.getScene().getRoot().lookupAll(selector).stream();
                })
                .filter(Region.class::isInstance)
                .map(Region.class::cast)
                .filter(region -> region.getBackground() != null)
                .findFirst().orElseThrow(() -> new AssertionError("Native popup missing " + selector));
        return solid(target);
    }

    private static void save(Scene scene, String name) {
        var image = scene.snapshot(null);
        var output = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        var pixels = image.getPixelReader();
        for (int y = 0; y < output.getHeight(); y++)
            for (int x = 0; x < output.getWidth(); x++) output.setRGB(x, y, pixels.getArgb(x, y));
        try {
            var directory = Path.of("build", "previews");
            Files.createDirectories(directory);
            assertTrue(ImageIO.write(output, "png", directory.resolve(name).toFile()));
        } catch (IOException failure) { throw new IllegalStateException(failure); }
    }
}
