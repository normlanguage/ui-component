package dev.normlanguage.ui.component;

import dev.normlanguage.ui.component.gallery.Gallery;
import dev.normlanguage.ui.component.gallery.GalleryView;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.WritableImage;
import javafx.stage.Stage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("theme-rendering")
class GalleryBrowserTest extends FxTest {
    @Test void documentationLayoutGroupsNavigationAndExamplesWithoutOverflow() throws Exception {
        var light = Files.readString(Path.of("build/themes/light.css"));
        fx(() -> {
            var app = new App();
            var view = Gallery.createView(app, List.of(new Gallery.Palette("Blue", light, light)));
            app.setContent(view);
            var stage = new Stage();
            stage.setScene(new Scene(app, 1487, 1058));
            stage.show();
            try {
                app.applyCss(); app.layout();
                assertNotNull(view.getTop().lookup("#gallery-search"));
                assertNull(view.lookup("#gallery-nav-Input"));
                ((javafx.scene.control.Button) view.lookup("#gallery-category-INPUT")).fire();
                assertNotNull(view.lookup("#gallery-nav-Input"));
                view.selectComponent("Button");
                app.applyCss(); app.layout(); app.layout();
                assertEquals(6, ((Anchor) view.lookup("#gallery-section-index")).getItems().size());
                var sections = (javafx.scene.layout.GridPane) view.lookup("#gallery-sections");
                assertEquals(2, sections.getColumnConstraints().size());
                assertTrue(sections.getBoundsInParent().getHeight() < 850);
                app.resize(900, 720);
                app.layout(); app.applyCss(); app.layout(); view.layout();
                assertFalse(view.getRight().isManaged());
                assertEquals(1, sections.getColumnConstraints().size());
                capture(app, "gallery-button-compact.png");
                assertTrue(view.lookup("#gallery-dark-toggle").localToScene(view.lookup("#gallery-dark-toggle").getBoundsInLocal()).getMaxX() <= 900, () -> "Toolbar: " + view.getTop().getBoundsInParent() + " toggle: " + view.lookup("#gallery-dark-toggle").localToScene(view.lookup("#gallery-dark-toggle").getBoundsInLocal()));
            } finally { app.close(); stage.close(); }
        });
    }

    @Test void catalogDrivesOverviewSearchAndDetails() throws Exception {
        var light = Files.readString(Path.of("build", "themes", "light.css"));
        var dark = Files.readString(Path.of("build", "themes", "dark.css"));
        fx(() -> {
            var app = new App();
            var view = Gallery.createView(app, List.of(new Gallery.Palette("Default", light, dark)));
            app.setContent(view);
            var stage = new Stage();
            stage.setScene(new Scene(app, 1200, 800));
            stage.show();
            try {
                assertEquals(73, view.catalog().size());
                assertEquals(7, view.catalog().stream().map(Gallery.Component::category).distinct().count());
                assertEquals(73, view.visibleComponents().size());
                assertInstanceOf(ScrollPane.class, view.getCenter());
                assertNotNull(view.lookup("#gallery-overview"));
                ((Input) view.lookup("#gallery-search")).setText("button");
                assertEquals(List.of("Button", "FloatButton"), view.visibleComponents().stream().map(Gallery.Component::name).toList());
                view.selectComponent("Button");
                assertEquals("Button", view.selectedComponent());
                assertNotNull(view.lookup("#gallery-detail"));
                view.selectHome();
                assertNull(view.selectedComponent());
                assertNotNull(view.lookup("#gallery-overview"));
            } finally { app.close(); stage.close(); }
        });
    }

    @Test void paletteAndDensityControlsUpdateOwningApp() throws Exception {
        var light = Files.readString(Path.of("build", "themes", "light.css"));
        var dark = Files.readString(Path.of("build", "themes", "dark.css"));
        fx(() -> {
            var app = new App();
            var view = Gallery.createView(app, List.of(
                    new Gallery.Palette("Default", light, dark),
                    new Gallery.Palette("Alternate", dark, light)));
            app.setContent(view);
            var stage = new Stage();
            stage.setScene(new Scene(app, 1200, 800));
            stage.show();
            try {
                view.selectComponent("Input");
                var input = (Input) ((javafx.scene.layout.VBox) view.lookup("#gallery-example")).getChildren().getFirst();
                input.setText("Draft value");
                ((ChoiceBox<String>) view.lookup("#gallery-palette")).getSelectionModel().select("Alternate");
                assertEquals(dark, app.getThemeCss());
                ((ToggleButton) view.lookup("#gallery-dark-toggle")).fire();
                assertEquals(light, app.getThemeCss());
                ((ChoiceBox<ComponentConfig.Density>) view.lookup("#gallery-density")).getSelectionModel().select(ComponentConfig.Density.COMPACT);
                assertEquals(ComponentConfig.Density.COMPACT, app.getEffectiveConfig().density());
                ((ToggleButton) view.lookup("#gallery-dark-toggle")).fire();
                assertEquals(dark, app.getThemeCss());
                assertEquals("Input", view.selectedComponent());
                assertEquals("Draft value", input.getText());
            } finally { app.close(); stage.close(); }
        });
    }

    @Test void realThemeRendersOverviewAndDetail() throws Exception {
        var lightPath = Path.of("build", "themes", "light.css");
        var darkPath = Path.of("build", "themes", "dark.css");
        var light = Files.readString(lightPath);
        var dark = Files.readString(darkPath);
        var app = new App[1];
        var view = new GalleryView[1];
        var stage = new Stage[1];
        fx(() -> {
            app[0] = new App();
            var defaults = ComponentConfig.defaults();
            app[0].setConfig(new ComponentConfig(defaults.fontFamily(), 18, defaults.density(),
                    defaults.radius(), defaults.motionEnabled(), defaults.locale()));
            view[0] = Gallery.createView(app[0], List.of(new Gallery.Palette("Default", light, dark)));
            app[0].setContent(view[0]);
            stage[0] = new Stage();
            stage[0].setScene(new Scene(app[0], 1487, 1058));
            stage[0].show();
        });
        try {
            fx(() -> capture(app[0], "gallery-overview-light.png"));
            fx(() -> view[0].selectComponent("Button"));
            fx(() -> capture(app[0], "gallery-button-light.png"));
            fx(() -> { view[0].setDark(true); view[0].selectHome(); });
            fx(() -> capture(app[0], "gallery-overview-dark.png"));
            fx(() -> view[0].selectComponent("Button"));
            fx(() -> capture(app[0], "gallery-button-dark.png"));
            fx(() -> view[0].setDark(false));
            for (var component : view[0].catalog()) {
                fx(() -> view[0].selectComponent(component.name()));
                fx(() -> {
                    app[0].applyCss(); app[0].layout();
                    assertEquals(component.name(), view[0].selectedComponent());
                    assertNotNull(view[0].lookup("#gallery-detail"));
                    var example = view[0].lookup("#gallery-example");
                    assertNotNull(example, component.name());
                    assertTrue(example.getBoundsInParent().getWidth() > 0, component.name());
                    assertTrue(example.getBoundsInParent().getHeight() > 0, component.name());
                    if (component.name().equals("Input")) capture(app[0], "gallery-input-light.png");
                    if (component.name().equals("Table")) capture(app[0], "gallery-table-light.png");
                });
            }
            for (var name : List.of("gallery-overview-light.png", "gallery-button-light.png",
                    "gallery-overview-dark.png", "gallery-button-dark.png", "gallery-input-light.png", "gallery-table-light.png"))
                assertTrue(Files.size(Path.of("build", "previews", name)) > 1000, name);
        } finally {
            fx(() -> { app[0].close(); stage[0].close(); });
        }
    }

    private static void capture(App app, String name) {
        app.applyCss(); app.layout();
        var file = Path.of("build", "previews", name);
        try {
            Files.createDirectories(file.getParent());
            WritableImage image = app.snapshot(null, null);
            var pixels = image.getPixelReader();
            var output = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            for (int y = 0; y < output.getHeight(); y++)
                for (int x = 0; x < output.getWidth(); x++) output.setRGB(x, y, pixels.getArgb(x, y));
            ImageIO.write(output, "png", file.toFile());
        } catch (IOException error) { throw new UncheckedIOException(error); }
    }
}
