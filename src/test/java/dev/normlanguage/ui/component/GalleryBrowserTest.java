package dev.normlanguage.ui.component;

import dev.normlanguage.ui.component.gallery.Gallery;
import dev.normlanguage.ui.component.gallery.GalleryDocumentation;
import dev.normlanguage.ui.component.gallery.GalleryView;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("theme-rendering")
class GalleryBrowserTest extends FxTest {
    @Test void documentationNavigationSourceSearchAndCopyWorkInRealScene() throws Exception {
        var light = Files.readString(Path.of("build/themes/light.css"));
        fx(() -> {
            var app = new App();
            var defaults = Gallery.defaultConfig();
            app.setConfig(new ComponentConfig(defaults.fontFamily(), defaults.fontSize(), defaults.density(), defaults.radius(), false, defaults.locale()));
            var view = Gallery.createView(app, java.util.List.of(new Gallery.Palette("Blue", light, light)));
            app.setContent(view);
            var stage = new javafx.stage.Stage();
            stage.setScene(new javafx.scene.Scene(app, 1487, 1058));
            stage.show();
            try {
                view.selectComponent("Input");
                app.applyCss(); app.layout(); app.layout();
                var anchor = (Anchor) view.lookup("#gallery-section-index");
                assertEquals(java.util.List.of("何时使用", "实时预览", "密码与备注", "API", "使用说明", "示例源码"),
                        anchor.getItems().stream().map(Anchor.Item::text).toList());
                var scroll = (javafx.scene.control.ScrollPane) view.getCenter();
                scroll.setVvalue(1);
                app.layout();
                var source = (javafx.scene.control.TitledPane) view.lookup("#gallery-source-panel-0");
                source.setExpanded(true);
                app.applyCss(); app.layout(); app.layout();
                var copy = (javafx.scene.control.Button) view.lookup("#gallery-source-copy-0");
                copy.fire();
                assertEquals(GalleryDocumentation.source(GalleryDocumentation.sources(view.catalog().stream()
                        .filter(component -> component.name().equals("Input")).findFirst().orElseThrow()).getFirst()),
                        javafx.scene.input.Clipboard.getSystemClipboard().getString());
                var search = (javafx.scene.control.TextField) source.lookup(".text-field");
                search.setText("greetingInput");
                search.fireEvent(new javafx.event.ActionEvent());
                var code = (javafx.scene.control.TextArea) source.lookup(".text-area");
                assertEquals("greetingInput", code.getSelectedText());
                scroll.setVvalue(1); app.layout();
                FxTest.capture(app, Path.of("build/previews/documentation-source-wide.png"));
                app.resize(900, 720); app.layout(); app.applyCss(); app.layout();
                scroll.setVvalue(1); app.layout();
                var toolbar = source.lookup(".gallery-source-toolbar");
                assertTrue(toolbar.getBoundsInParent().getWidth() <= 610);
                FxTest.capture(app, Path.of("build/previews/documentation-source-compact.png"));
            } finally { app.close(); stage.close(); }
        });
    }
    @Test void documentationLayoutGroupsNavigationAndExamplesWithoutOverflow() throws Exception {
        var light = Files.readString(Path.of("build/themes/light.css"));
        fx(() -> {
            var app = new App();
            var presentation = Gallery.defaultConfig();
            app.setConfig(new ComponentConfig(presentation.fontFamily(), presentation.fontSize(), presentation.density(),
                    presentation.radius(), false, presentation.locale()));
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
                assertEquals(10, ((Anchor) view.lookup("#gallery-section-index")).getItems().size());
                var sections = (javafx.scene.layout.VBox) view.lookup("#gallery-sections");
                assertEquals(4, sections.getChildren().size());
                assertTrue(sections.getBoundsInParent().getHeight() < 1400);
                view.selectComponent("Masonry");
                app.applyCss(); app.layout(); app.layout();
                assertTrue(sections.getHeight() < 1600);
                view.selectComponent("Button");
                app.applyCss(); app.layout(); app.layout();
                app.resize(900, 720);
                app.layout(); app.applyCss(); app.layout(); view.layout();
                assertFalse(view.getRight().isManaged());
                assertEquals(6, sections.getChildren().size());
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
                var input = (Input) view.lookup("#gallery-example").lookup(".text-field");
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
            var defaults = Gallery.defaultConfig();
            app[0].setConfig(new ComponentConfig(defaults.fontFamily(), 18, defaults.density(),
                    defaults.radius(), false, defaults.locale()));
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
                    assertTrue(((Anchor) view[0].lookup("#gallery-section-index")).getItems().size() >= 2, component.name());
                    capture(app[0], "audit-after/light/" + component.name() + ".png");
                    FxTest.capture(((ScrollPane) view[0].getCenter()).getContent(), Path.of("build/previews/audit-after/full", component.name() + ".png"));
                    view[0].setDark(true);
                    capture(app[0], "audit-after/dark/" + component.name() + ".png");
                    view[0].setDark(false);
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
        FxTest.capture(app, Path.of("build", "previews", name));
    }
}
