package dev.normlanguage.ui.component;

import dev.normlanguage.ui.component.gallery.Gallery;
import dev.normlanguage.ui.component.gallery.NavigationExampleSections;
import javafx.scene.Scene;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

@Tag("theme-rendering")
class NavigationExampleSectionsTest extends FxTest {
    @Test void floatingGroupStaysAtBottomRightAndResponsiveGridFillsColumns() throws Exception {
        var css = Files.readString(Path.of("build", "themes", "light.css"));
        fx(() -> {
            var app = new App();
            app.setThemeCss(css);
            var layer = new StackPane(new Label("内容"));
            var group = new FloatButton.Group(new FloatButton("新建"), new FloatButton("帮助")).attachTo(layer);
            app.setContent(layer);
            var stage = new Stage();
            stage.setScene(new Scene(app, 720, 480));
            stage.show();
            try {
                app.applyCss(); app.layout();
                assertTrue(group.getBoundsInParent().getMinX() > layer.getWidth() / 2);
                assertTrue(group.getBoundsInParent().getMinY() > layer.getHeight() / 2);
                var grid = new Grid();
                grid.setResponsiveColumns(400, 3);
                for (int i = 0; i < 3; i++) grid.addItem(new StackPane(new Label("模块 " + i)), 1);
                app.setContent(grid);
                app.applyCss(); app.layout();
                assertEquals(3, grid.getCurrentColumns());
                assertEquals(3, grid.getColumnConstraints().size());
                assertTrue(grid.getChildren().getFirst().getBoundsInParent().getWidth() > 180);
                assertEquals(grid.getChildren().getFirst().getBoundsInParent().getWidth(),
                        grid.getChildren().get(1).getBoundsInParent().getWidth(), 2);
                grid.getChildren().clear();
                assertTrue(grid.getColumnConstraints().isEmpty());
                var masonry = new Masonry();
                masonry.setMinimumColumnWidth(180);
                for (int i = 0; i < 12; i++) {
                    var tile = new StackPane();
                    tile.setPrefHeight(100 + (i % 3) * 30);
                    masonry.getChildren().add(tile);
                }
                assertEquals(Orientation.HORIZONTAL, masonry.getContentBias());
                assertTrue(masonry.prefHeight(900) < masonry.prefHeight(180) / 2);
            } finally { app.close(); stage.close(); }
        });
    }

    @Test void everyGeneralLayoutAndNavigationExampleHasDistinctLiveSections() throws Exception {
        var css = Files.readString(Path.of("build", "themes", "light.css"));
        fx(() -> {
            var app = new App();
            app.setThemeCss(css);
            var host = new VBox();
            app.setContent(host);
            var stage = new Stage();
            stage.setScene(new Scene(app, 1100, 760));
            stage.show();
            try {
                var components = Gallery.components(app).stream().filter(component ->
                        component.category() == Gallery.Category.GENERAL && !component.name().equals("Button")
                                || component.category() == Gallery.Category.LAYOUT
                                || component.category() == Gallery.Category.NAVIGATION).toList();
                assertEquals(17, components.size());
                for (var component : components) {
                    var sections = NavigationExampleSections.create(component, app);
                    assertTrue(sections.size() >= 2, component.name());
                    assertEquals(sections.size(), new HashSet<>(sections.stream().map(section -> section.id()).toList()).size(), component.name());
                    for (var section : sections) {
                        assertFalse(section.title().isBlank(), component.name());
                        host.getChildren().add(section.content());
                    }
                    app.applyCss(); app.layout();
                    var demo = app.lookup("#gallery-example");
                    assertNotNull(demo, component.name());
                    assertTrue(demo.getBoundsInParent().getWidth() > 0, component.name());
                    host.getChildren().clear();
                    for (var section : sections) Util.closeTree(section.content());
                }
            } finally { app.close(); stage.close(); }
        });
    }

    @Test void keyLayoutAndNavigationDemosHaveObservableBehavior() throws Exception {
        var css = Files.readString(Path.of("build", "themes", "light.css"));
        fx(() -> {
            var app = new App();
            app.setThemeCss(css);
            var host = new VBox();
            app.setContent(host);
            var stage = new Stage();
            stage.setScene(new Scene(app, 1100, 760));
            stage.show();
            try {
                var components = Gallery.components(app);
                var steps = NavigationExampleSections.create(components.stream().filter(component -> component.name().equals("Steps")).findFirst().orElseThrow(), app);
                host.getChildren().setAll(steps.stream().map(section -> section.content()).toList());
                var stepControl = (Steps) app.lookup("#nav-steps-primary");
                assertNotNull(stepControl);
                ((Button) app.lookup("#nav-steps-next")).fire();
                assertEquals(1, stepControl.getCurrentStep());
                host.getChildren().clear();
                steps.forEach(section -> Util.closeTree(section.content()));

                var tabs = NavigationExampleSections.create(components.stream().filter(component -> component.name().equals("Tabs")).findFirst().orElseThrow(), app);
                host.getChildren().setAll(tabs.stream().map(section -> section.content()).toList());
                var tabControl = (Tabs) tabs.getFirst().content();
                assertNotNull(tabControl);
                assertTrue(tabControl.getTabs().size() >= 3);
                tabControl.getSelectionModel().select(1);
                assertEquals(1, tabControl.getSelectionModel().getSelectedIndex());
                host.getChildren().clear();
                tabs.forEach(section -> Util.closeTree(section.content()));

                var layout = NavigationExampleSections.create(components.stream().filter(component -> component.name().equals("Layout")).findFirst().orElseThrow(), app);
                host.getChildren().setAll(layout.stream().map(section -> section.content()).toList());
                var layoutControl = (Layout) app.lookup("#nav-layout-primary");
                assertNotNull(layoutControl.getCenter());
                assertNotNull(layoutControl.getLeft());
                host.getChildren().clear();
                layout.forEach(section -> Util.closeTree(section.content()));

                var masonry = NavigationExampleSections.create(components.stream().filter(component -> component.name().equals("Masonry")).findFirst().orElseThrow(), app);
                host.getChildren().setAll(masonry.stream().map(section -> section.content()).toList());
                var masonryControl = (Masonry) app.lookup("#nav-masonry-primary");
                int before = masonryControl.getChildren().size();
                ((Button) app.lookup("#nav-masonry-add")).fire();
                assertEquals(before + 1, masonryControl.getChildren().size());
                host.getChildren().clear();
                masonry.forEach(section -> Util.closeTree(section.content()));

                var grid = NavigationExampleSections.create(components.stream().filter(component -> component.name().equals("Grid")).findFirst().orElseThrow(), app);
                host.getChildren().setAll(grid.stream().map(section -> section.content()).toList());
                app.applyCss(); app.layout();
                var gridControl = (Grid) grid.getFirst().content();
                assertEquals(6, gridControl.getChildren().size());
                assertEquals(2, javafx.scene.layout.GridPane.getColumnSpan(gridControl.getChildren().getFirst()));
                host.getChildren().clear();
                grid.forEach(section -> Util.closeTree(section.content()));

                var anchor = NavigationExampleSections.create(components.stream().filter(component -> component.name().equals("Anchor")).findFirst().orElseThrow(), app);
                host.getChildren().setAll(anchor.stream().map(section -> section.content()).toList());
                app.applyCss(); app.layout();
                var anchorControl = (Anchor) app.lookup("#nav-anchor-primary");
                assertEquals(3, anchorControl.getItems().size());
                anchorControl.scrollTo(anchorControl.getItems().get(1));
                assertEquals(anchorControl.getItems().get(1), anchorControl.getActiveItem());
                host.getChildren().clear();
                anchor.forEach(section -> Util.closeTree(section.content()));

                var pagination = NavigationExampleSections.create(components.stream().filter(component -> component.name().equals("Pagination")).findFirst().orElseThrow(), app);
                host.getChildren().setAll(pagination.stream().map(section -> section.content()).toList());
                var pages = (Pagination) app.lookup("#nav-pagination-primary");
                assertTrue(((VBox) app.lookup("#gallery-example")).getChildren().stream().anyMatch(node ->
                        node instanceof VBox list && list.getChildren().stream().anyMatch(item -> item instanceof javafx.scene.control.Label label && label.getText().equals("文档 1"))));
                pages.next();
                assertEquals(2, pages.getCurrentPage());
                host.getChildren().clear();
                pagination.forEach(section -> Util.closeTree(section.content()));
            } finally { app.close(); stage.close(); }
        });
    }
}
