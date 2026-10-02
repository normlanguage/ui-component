package dev.normlanguage.ui.component;

import dev.normlanguage.ui.component.gallery.DisplayExampleSections;
import dev.normlanguage.ui.component.gallery.DisplayExamples;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class DisplayExampleSectionsTest extends FxTest {
    @Test void everyDisplayComponentHasDistinctRealExamples() throws Exception {
        fx(() -> {
            var app = new App();
            try {
                var names = new HashSet<String>();
                for (var component : DisplayExamples.components(app)) {
                    assertTrue(names.add(component.name()));
                    var sections = DisplayExampleSections.create(component, app);
                    assertTrue(sections.size() >= 2, component.name());
                    var ids = new HashSet<String>();
                    for (var section : sections) {
                        assertTrue(ids.add(section.id()), component.name());
                        assertFalse(section.title().isBlank(), component.name());
                        assertNotNull(section.content());
                        Util.closeTree(section.content());
                    }
                }
                assertEquals(18, names.size());
            } finally {
                app.close();
            }
        });
    }

    @Test void calendarWithMixedDayContentKeepsRowsAligned() throws Exception {
        fx(() -> {
            var calendar = new Calendar();
            calendar.setDayContentFactory(date -> date.getDayOfMonth() % 5 == 0 ? new Label("•") : null);
            var app = new App(calendar);
            var stage = new Stage();
            stage.setScene(new Scene(app, 680, 560));
            stage.show();
            try {
                app.applyCss();
                app.layout();
                var grid = (GridPane) calendar.getCenter();
                assertTrue(grid.getWidth() > 500, "calendar should occupy the available content width");
                assertEquals(49, grid.getChildren().size());
                for (var node : grid.getChildren()) {
                    var row = GridPane.getRowIndex(node);
                    if (row == null || row == 0) continue;
                    assertInstanceOf(VBox.class, node);
                    var box = (VBox) node;
                    assertEquals(2, box.getChildren().size());
                    assertInstanceOf(Button.class, box.getChildren().getFirst());
                    assertTrue(((Button) box.getChildren().getFirst()).getWidth() >= 38);
                }
                for (int row = 1; row <= 6; row++) {
                    int selectedRow = row;
                    var baseline = grid.getChildren().stream().filter(node -> Integer.valueOf(selectedRow).equals(GridPane.getRowIndex(node)))
                            .map(node -> ((VBox) node).getChildren().getFirst().localToScene(0, 0).getY()).toList();
                    assertEquals(7, baseline.size());
                    for (double y : baseline) assertEquals(baseline.getFirst(), y, 0.5);
                }
            } finally {
                app.close();
                stage.close();
            }
        });
    }

    @Test void segmentedProgrammaticSelectionUpdatesToggle() throws Exception {
        fx(() -> {
            var segmented = new Segmented<>(java.util.List.of("日", "周", "月"));
            segmented.setValue("周");
            assertTrue(((ToggleButton) segmented.getChildren().get(1)).isSelected());
            ((ToggleButton) segmented.getChildren().get(2)).fire();
            assertEquals("月", segmented.getValue());
        });
    }

    @Test void avatarCropsLandscapeToSquare() throws Exception {
        fx(() -> {
            var avatar = new Avatar("AL");
            var image = new Image(getClass().getResource("gallery/images/alpine-lake.png").toExternalForm());
            avatar.setImage(image);
            var view = (ImageView) avatar.getChildren().get(1);
            assertNotNull(view.getViewport());
            assertEquals(view.getViewport().getWidth(), view.getViewport().getHeight(), 0.01);
            assertNotNull(view.getClip());
        });
    }

    @Test void statisticExposesDistinctTitleAndValueStyling() throws Exception {
        fx(() -> {
            var statistic = new Statistic("本周下载", 1280);
            assertTrue(statistic.getChildren().get(0).getStyleClass().contains("norm-statistic-title"));
            assertTrue(statistic.getChildren().get(1).getStyleClass().contains("norm-statistic-value"));
            statistic.close();
        });
    }
}
