package dev.normlanguage.ui.component;

import dev.normlanguage.ui.component.gallery.GalleryExamples;
import dev.normlanguage.ui.component.gallery.InputExampleSections;
import dev.normlanguage.ui.component.gallery.InputExamples;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Region;
import javafx.scene.Scene;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class InputExampleSectionsTest extends FxTest {
    @Test void colorPreviewKeepsSelectedPaintAfterCssAndConfigurationChanges() throws Exception {
        fx(() -> {
            var app = new App();
            var stage = new javafx.stage.Stage();
            try {
                var component = InputExamples.components(app).stream().filter(entry -> entry.name().equals("ColorPicker")).findFirst().orElseThrow();
                var sections = GalleryExamples.create(component, app);
                app.setContent(new javafx.scene.layout.VBox(sections.getFirst().content(), sections.getLast().content()));
                stage.setScene(new Scene(app, 900, 700)); stage.show();
                app.applyCss(); app.layout();
                var swatch = (Region) app.lookup("#demo-brand-swatch");
                assertNotNull(swatch.getBackground());
                assertEquals(javafx.scene.paint.Color.web("#1677FF"), swatch.getBackground().getFills().getFirst().getFill());
                var preview = (Label) app.lookup("#demo-color-preview");
                assertEquals(javafx.scene.paint.Color.web("#1677FF"), preview.getTextFill());
                assertEquals(javafx.scene.paint.Color.web("#f0f5ff"), preview.getBackground().getFills().getFirst().getFill());
                var picker = (ColorPicker) app.lookup(".norm-color-picker");
                picker.setValue(javafx.scene.paint.Color.TOMATO);
                app.applyCss(); app.layout();
                assertEquals(javafx.scene.paint.Color.TOMATO, swatch.getBackground().getFills().getFirst().getFill());
                app.setConfig(new ComponentConfig("System", 16, ComponentConfig.Density.COMPACT, 6, false, java.util.Locale.US));
                app.applyCss(); app.layout();
                assertEquals(javafx.scene.paint.Color.TOMATO, swatch.getBackground().getFills().getFirst().getFill());
            } finally { app.close(); stage.close(); }
        });
    }
    @Test void everyInputPageHasDistinctUsefulExamples() throws Exception {
        fx(() -> {
            var app = new App();
            try {
                var components = InputExamples.components(app);
                assertEquals(18, components.size());
                for (var component : components) {
                    var sections = InputExampleSections.create(component, app);
                    assertTrue(sections.size() >= 2 && sections.size() <= 3, component.name());
                    assertEquals("gallery-example", sections.getFirst().content().getId(), component.name());
                    assertEquals(sections.size(), new HashSet<>(sections.stream().map(GalleryExamples.Section::id).toList()).size());
                    assertEquals(sections.size(), new HashSet<>(sections.stream().map(GalleryExamples.Section::content).toList()).size());
                    for (var section : sections) {
                        assertFalse(section.title().isBlank(), component.name());
                        assertFalse(section.content().isDisabled(), component.name());
                    }
                }
            } finally { app.close(); }
        });
    }

    @Test void treeSelectStartsWithReadablePromptAndTracksSelection() throws Exception {
        fx(() -> {
            var root = new TreeItem<>("项目");
            var child = new TreeItem<>("产品设计");
            root.getChildren().add(child);
            var tree = new TreeSelect<>(root);
            var trigger = (Button) tree.getChildren().getFirst();
            assertTrue(root.isExpanded());
            assertFalse(trigger.getText().isBlank());
            assertTrue(trigger.getMinWidth() >= 120);
            tree.setPromptText("选择团队");
            assertEquals("选择团队", trigger.getText());
            tree.select(child);
            assertEquals("产品设计", trigger.getText());
            tree.setPromptText("选择项目");
            assertEquals("产品设计", trigger.getText());
        });
    }

    @Test void orderQuantityExampleUpdatesRealTotal() throws Exception {
        fx(() -> {
            var app = new App();
            try {
                var component = InputExamples.components(app).stream()
                        .filter(entry -> entry.name().equals("InputNumber")).findFirst().orElseThrow();
                app.setContent(component.factory().get());
                new Scene(app);
                var quantity = (InputNumber) app.lookup("#demo-quantity");
                var total = (Label) app.lookup("#demo-total");
                assertNotNull(quantity);
                assertNotNull(total);
                quantity.setValue(new BigDecimal("3"));
                assertTrue(total.getText().contains("267"));
            } finally { app.close(); }
        });
    }

    @Test void secondaryChoicesAndUploadHaveReadableInitialState() throws Exception {
        fx(() -> {
            var app = new App();
            try {
                var components = InputExamples.components(app);
                var cascade = (Cascader<?>) InputExampleSections.create(components.stream()
                        .filter(entry -> entry.name().equals("Cascader")).findFirst().orElseThrow(), app)
                        .get(1).content().lookup("#demo-cascader-lazy");
                assertNotNull(cascade);
                assertEquals(1, cascade.getValue().size());

                var select = InputExampleSections.create(components.stream()
                        .filter(entry -> entry.name().equals("Select")).findFirst().orElseThrow(), app).get(1).content();
                var searchable = (Select.Searchable<?>) select.lookup("#demo-select-searchable");
                var multiple = (Select.Multiple<?>) select.lookup("#demo-select-multiple");
                assertNotNull(searchable);
                assertNotNull(searchable.getSelect().getValue());
                assertNotNull(multiple);
                assertFalse(multiple.getSelectedItems().isEmpty());

                var color = InputExampleSections.create(components.stream()
                        .filter(entry -> entry.name().equals("ColorPicker")).findFirst().orElseThrow(), app).getFirst().content();
                var swatch = (Region) color.lookup("#demo-brand-swatch");
                assertNotNull(swatch);
                assertTrue(swatch.getPrefWidth() >= 48);
                assertNotNull(swatch.getBackground());

                var upload = InputExampleSections.create(components.stream()
                        .filter(entry -> entry.name().equals("Upload")).findFirst().orElseThrow(), app).getFirst().content();
                var list = (javafx.scene.control.ListView<?>) upload.lookup(".list-view");
                assertNotNull(list);
                assertTrue(list.getPrefHeight() <= 112);
            } finally { app.close(); }
        });
    }
}
