package dev.normlanguage.ui.component;

import dev.normlanguage.ui.component.gallery.Gallery;
import dev.normlanguage.ui.component.gallery.GalleryExamples;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Tag("theme-rendering")
class GalleryExamplesTest extends FxTest {
    @Test void buttonDetailsHaveSixRealWorkingSectionsAndCopyableJava() throws Exception {
        var light = Files.readString(Path.of("build", "themes", "light.css"));
        var dark = Files.readString(Path.of("build", "themes", "dark.css"));
        fx(() -> {
            var app = new App();
            app.setThemeCss(light);
            var button = Gallery.components(app).stream().filter(component -> component.name().equals("Button")).findFirst().orElseThrow();
            var sections = GalleryExamples.create(button, app);
            assertEquals(List.of("types", "sizes", "states", "icons", "danger", "usage"),
                    sections.stream().map(GalleryExamples.Section::id).toList());
            assertEquals(List.of(true, false, false, false, false, true),
                    sections.stream().map(GalleryExamples.Section::fullWidth).toList());
            var content = new VBox();
            for (var section : sections) content.getChildren().add(section.content());
            app.setContent(content);
            var stage = new Stage();
            stage.setScene(new Scene(app, 1000, 760));
            stage.show();
            try {
                app.applyCss(); app.layout();
                assertTrue(sections.stream().limit(5).allMatch(section -> section.content() instanceof FlowPane));
                assertInstanceOf(Button.class, app.lookup("#gallery-example"));
                assertEquals("主要按钮", ((Button) app.lookup("#gallery-example")).getText());
                var large = (ConfigProvider) app.lookup("#gallery-size-spacious");
                var compact = (ConfigProvider) app.lookup("#gallery-size-compact");
                assertEquals(light, large.getThemeCss());
                assertEquals(ComponentConfig.Density.SPACIOUS, large.getEffectiveConfig().density());
                assertEquals(ComponentConfig.Density.COMPACT, compact.getEffectiveConfig().density());
                app.setThemeCss(dark);
                assertEquals(dark, large.getThemeCss());
                assertEquals(dark, compact.getThemeCss());
                assertNotNull(app.lookup("#gallery-loading-button"));
                assertTrue(((Button) app.lookup("#gallery-loading-button")).isLoading());
                assertNotNull(app.lookup("#gallery-icon-search"));
                assertNotNull(app.lookup("#gallery-danger-button"));
                var outlinedDanger = (Color) ((Button) app.lookup("#gallery-danger-outlined")).getTextFill();
                var textDanger = (Color) ((Button) app.lookup("#gallery-danger-text")).getTextFill();
                assertTrue(outlinedDanger.getRed() > outlinedDanger.getGreen());
                assertTrue(textDanger.getRed() > textDanger.getGreen());
                var code = (TextArea) app.lookup("#gallery-code");
                assertTrue(code.getText().contains("new Button(\"Save changes\")"));
                assertTrue(code.getText().contains("setOnAction"));
                assertFalse(code.getText().contains("ButtonType"));
                ((javafx.scene.control.Button) app.lookup("#gallery-copy")).fire();
                assertEquals(code.getText(), Clipboard.getSystemClipboard().getString());
            } finally { app.close(); stage.close(); }
        });
    }

    @Test void otherComponentsReuseTheirRegisteredExampleAndDisabledState() throws Exception {
        var light = Files.readString(Path.of("build", "themes", "light.css"));
        fx(() -> {
            var app = new App();
            app.setThemeCss(light);
            var checkbox = Gallery.components(app).stream().filter(component -> component.name().equals("Checkbox")).findFirst().orElseThrow();
            var sections = GalleryExamples.create(checkbox, app);
            assertEquals(List.of("basic", "disabled"), sections.stream().map(GalleryExamples.Section::id).toList());
            var content = new VBox(sections.stream().map(GalleryExamples.Section::content).toArray(javafx.scene.Node[]::new));
            app.setContent(content);
            var stage = new Stage();
            stage.setScene(new Scene(app, 700, 400));
            stage.show();
            try {
                app.applyCss(); app.layout();
                assertInstanceOf(Checkbox.class, app.lookup("#gallery-example"));
                assertTrue(((Checkbox) sections.get(1).content()).isDisabled());
            } finally { app.close(); stage.close(); }
        });
    }
}
