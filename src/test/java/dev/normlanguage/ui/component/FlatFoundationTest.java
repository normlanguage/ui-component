package dev.normlanguage.ui.component;

import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import static org.junit.jupiter.api.Assertions.*;

@Tag("theme-rendering")
class FlatFoundationTest extends FxTest {
    @Test void splitterKeepsInteractiveDividerWithoutNativeBevels() throws Exception {
        var theme = Files.readString(Path.of("build/themes/light.css"));
        fx(() -> {
            var splitter = new Splitter(new Input("Left"), new Input("Right"));
            var app = new App(splitter);
            app.setThemeCss(theme);
            var stage = new Stage();
            stage.setScene(new Scene(app, 500, 250));
            stage.show();
            try {
                app.applyCss(); app.layout();
                var divider = (javafx.scene.layout.Region) splitter.lookup(".split-pane-divider");
                assertNotNull(divider);
                assertEquals(1, divider.getBackground().getFills().size());
                assertInstanceOf(Color.class, divider.getBackground().getFills().getFirst().getFill());
                splitter.setDividerPosition(0, .7);
                app.layout();
                assertEquals(.7, splitter.getDividerPositions()[0], .03);
            } finally { app.close(); stage.close(); }
        });
    }

    @Test void buttonAppearancesResolveSemanticColorsInBothThemes() throws Exception {
        var themes = java.util.List.of(Files.readString(Path.of("build/themes/light.css")),
                Files.readString(Path.of("build/themes/dark.css")));
        fx(() -> {
            var primary = new Button("Primary");
            var outlined = new Button("Outlined");
            outlined.getStyleClass().add("outlined");
            var plain = new Button("Text");
            plain.getStyleClass().add("text");
            var danger = new Button("Danger");
            danger.getStyleClass().add("danger");
            var app = new App(new VBox(12, primary, outlined, plain, danger));
            var stage = new Stage();
            stage.setScene(new Scene(app, 420, 250));
            stage.show();
            try {
                for (var theme : themes) {
                    app.setThemeCss(theme);
                    app.applyCss(); app.layout();
                    var primaryFill = primary.getBackground().getFills().getFirst().getFill();
                    assertNotEquals(primaryFill, danger.getBackground().getFills().getFirst().getFill());
                    assertNotEquals(primaryFill, outlined.getBackground().getFills().getFirst().getFill());
                    assertEquals(0, ((Color) plain.getBackground().getFills().getFirst().getFill()).getOpacity());
                    assertEquals(0, ((Color) plain.getBorder().getStrokes().getFirst().getTopStroke()).getOpacity());
                }
            } finally { app.close(); stage.close(); }
        });
    }

    @Test void nativeAndComponentButtonsHaveFlatSurfacesAndConsistentDensity() throws Exception {
        var theme = Files.readString(Path.of("build/themes/light.css"));
        fx(() -> {
            var nativeButton = new javafx.scene.control.Button("Cancel");
            var primary = new Button("Create project");
            var app = new App(new VBox(12, nativeButton, primary));
            app.setThemeCss(theme);
            var stage = new Stage();
            stage.setScene(new Scene(app, 420, 200));
            stage.show();
            try {
                app.applyCss(); app.layout();
                for (var button : java.util.List.of(nativeButton, primary)) {
                    assertEquals(1, button.getBackground().getFills().size());
                    assertInstanceOf(Color.class, button.getBackground().getFills().getFirst().getFill());
                    assertEquals(32, button.getHeight(), 0.5);
                    assertNull(button.getEffect());
                }
                var normal = primary.getBackground().getFills().getFirst().getFill();
                primary.pseudoClassStateChanged(PseudoClass.getPseudoClass("hover"), true);
                app.applyCss();
                assertNotEquals(normal, primary.getBackground().getFills().getFirst().getFill());
                primary.setDisable(true);
                app.applyCss();
                assertEquals(1, primary.getOpacity());
                app.setConfig(new ComponentConfig("System", 14, ComponentConfig.Density.COMPACT, 4, false, Locale.ENGLISH));
                app.applyCss(); app.layout();
                assertEquals(32 * .85, nativeButton.getHeight(), 1);
                assertEquals(nativeButton.getHeight(), primary.getHeight(), .5);
                assertEquals(4, primary.getBackground().getFills().getFirst().getRadii().getTopLeftHorizontalRadius());
            } finally { app.close(); stage.close(); }
        });
    }
}
