package dev.normlanguage.ui.component;

import dev.normlanguage.ui.component.gallery.FeedbackExamples;
import dev.normlanguage.ui.component.gallery.GalleryExamples;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FeedbackExamplesTest extends FxTest {
    @Test void feedbackScenariosArePopulatedAndProgressIsInteractive() throws Exception {
        fx(() -> {
            var app = new App();
            var host = new VBox(20);
            app.setContent(host);
            var stage = new Stage();
            stage.setScene(new Scene(app, 1100, 850)); stage.show();
            try {
                for (var component : FeedbackExamples.components(app)) {
                    var sections = FeedbackExamples.create(component, app);
                    assertEquals(2, sections.size(), component.name());
                    host.getChildren().setAll(sections.stream().map(GalleryExamples.Section::content).toList());
                    app.applyCss(); app.layout();
                    for (var section : sections) {
                        assertTrue(section.content().getBoundsInParent().getHeight() > 60, component.name());
                        assertFalse(section.content().lookupAll(".label").isEmpty(), component.name());
                    }
                    if (component.name().equals("Progress")) {
                        var primary = sections.getFirst().content();
                        var progress = (Progress) primary.lookup(".progress-bar");
                        var increase = primary.lookupAll(".button").stream().map(Button.class::cast)
                                .filter(button -> button.getText().equals("推进 10%")).findFirst().orElseThrow();
                        increase.fire();
                        assertEquals(0.46, progress.getProgress(), 0.001);
                    }
                    host.getChildren().clear();
                    sections.forEach(section -> Util.closeTree(section.content()));
                }
            } finally { app.close(); stage.close(); }
        });
    }
    @Test void actualAnimationProgressesAndSettlesInAWindow() throws Exception {
        var opacity = new java.util.concurrent.atomic.AtomicReference<Label>();
        var motion = new Motion[1];
        var stage = new Stage[1];
        var app = new App[1];
        fx(() -> {
            var label = new Label("Motion"); opacity.set(label);
            app[0] = new App(label);
            stage[0] = new Stage(); stage[0].setScene(new Scene(app[0], 400, 300)); stage[0].show();
            motion[0] = new Motion(label); motion[0].enter(0, 16);
            assertEquals(0, label.getOpacity());
        });
        try {
            Thread.sleep(100);
            fx(() -> { assertTrue(opacity.get().getOpacity() > 0); assertTrue(opacity.get().getTranslateY() < 16); });
            Thread.sleep(230);
            fx(() -> { assertEquals(1, opacity.get().getOpacity()); assertEquals(0, opacity.get().getTranslateY()); });
        } finally { fx(() -> { motion[0].close(); app[0].close(); stage[0].close(); }); }
    }
}
