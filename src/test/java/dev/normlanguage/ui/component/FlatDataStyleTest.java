package dev.normlanguage.ui.component;

import javafx.collections.FXCollections;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.TableRow;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@org.junit.jupiter.api.Tag("theme-rendering")
class FlatDataStyleTest extends FxTest {
    private static final Path THEMES = Path.of("build", "themes");
    private static final Path PREVIEWS = Path.of("build", "previews");

    @Test void tableAndCalendarUseFlatThemeSurfacesWhileKeepingInteraction() throws Exception {
        for (String theme : new String[] {"light", "dark"}) {
            fx(() -> {
                var table = new Table<String>();
                table.setSource(FXCollections.observableArrayList("Alpha", "Beta", "Gamma"));
                table.column("Name", value -> value);
                table.setPrefSize(420, 220);
                var calendar = new Calendar();
                var list = new List<String>();
                list.getItems().addAll("First", "Second", "Third");
                list.setPrefSize(180, 220);
                var card = new Card("Overview", new Label("Flat surface"));
                card.setPrefWidth(220);
                var content = new VBox(20, new HBox(20, table, calendar), new HBox(20, list, card));
                content.setPadding(new Insets(24));
                var app = new App(content);
                var stage = new Stage();
                stage.setScene(new Scene(app, 1080, 640));
                stage.show();
                try {
                    app.setThemeCss(Files.readString(THEMES.resolve(theme + ".css")));
                    app.applyCss();
                    app.layout();
                    var header = (Region) table.lookup(".column-header-background");
                    assertNotNull(header);
                    assertInstanceOf(Color.class, header.getBackground().getFills().getLast().getFill());
                    assertNotEquals(app.getBackground().getFills().getLast().getFill(), table.getBackground().getFills().getLast().getFill());
                    assertEquals(table.getBackground().getFills().getLast().getFill(), card.getBackground().getFills().getLast().getFill());
                    var row = (TableRow<?>) table.lookup(".table-row-cell");
                    assertNotNull(row);
                    table.getSelectionModel().select(1);
                    list.getSelectionModel().select(1);
                    calendar.nextMonth();
                    calendar.setValue(calendar.getDisplayedMonth().atDay(3));
                    assertEquals(1, table.getSelectionModel().getSelectedIndex());
                    assertEquals("Second", list.getSelectionModel().getSelectedItem());
                    assertNotNull(calendar.lookup(".norm-calendar-day"));
                    app.applyCss();
                    app.layout();
                    var selected = calendar.lookup(".norm-calendar-day.selected");
                    assertNotNull(selected);
                    assertInstanceOf(Color.class, ((Region) selected).getBackground().getFills().getLast().getFill());
                    var ordinaryDay = (Region) calendar.lookup(".norm-calendar-day");
                    assertNotNull(ordinaryDay);
                    assertEquals(0, ordinaryDay.getBorder().getStrokes().getLast().getWidths().getTop());
                    save(stage.getScene(), "flat-data-" + theme + ".png");
                } catch (Exception failure) {
                    throw new RuntimeException(failure);
                } finally {
                    app.close();
                    stage.close();
                }
            });
        }
    }

    @Test void navigationAndStatusControlsRemainInteractive() throws Exception {
        fx(() -> {
            var tabs = new Tabs();
            Tab first = tabs.add("Overview", new Label("Overview content"));
            Tab second = tabs.add("Details", new Label("Details content"));
            var pagination = new Pagination(75, 10);
            var steps = new Steps("Prepare", "Review", "Done");
            var tree = new Tree<>("Root");
            tree.getRoot().getChildren().add(new TreeItem<>("Child"));
            tree.setPrefSize(220, 180);
            var collapse = new Collapse("More", new Label("Additional details"));
            var avatar = new Avatar("AB");
            var badge = new Badge(avatar);
            badge.setCount(3);
            var tag = new Tag("Ready");
            tag.setClosable(true);
            var progress = new Progress(0.45);
            var status = new HBox(16, badge, tag, progress);
            var content = new VBox(18, tabs, pagination, steps, tree, collapse, status);
            content.setPadding(new Insets(24));
            var app = new App(content);
            var stage = new Stage();
            stage.setScene(new Scene(app, 780, 700));
            stage.show();
            try {
                app.setThemeCss(Files.readString(THEMES.resolve("light.css")));
                app.applyCss();
                app.layout();
                assertEquals(Pos.TOP_RIGHT, StackPane.getAlignment(badge.lookup(".norm-badge-indicator")));
                tabs.getSelectionModel().select(second);
                pagination.next();
                steps.setCurrentStep(1);
                collapse.setExpanded(true);
                tree.getSelectionModel().select(tree.getRoot().getChildren().getFirst());
                assertSame(second, tabs.getSelectionModel().getSelectedItem());
                assertNotSame(first, tabs.getSelectionModel().getSelectedItem());
                assertEquals(2, pagination.getCurrentPage());
                assertEquals(1, steps.getCurrentStep());
                assertTrue(collapse.isExpanded());
                assertEquals("Child", tree.getSelectionModel().getSelectedItem().getValue());
                assertEquals(3, badge.getCount());
                assertTrue(tag.isClosable());
                save(stage.getScene(), "flat-navigation-light.png");
            } catch (Exception failure) {
                throw new RuntimeException(failure);
            } finally {
                app.close();
                stage.close();
            }
        });
    }

    @Test void nestedNativeListsAndTreeShareTheRaisedSurface() throws Exception {
        fx(() -> {
            var transfer = new Transfer<>(java.util.List.of("Available", "More"));
            var root = new TreeItem<>("Root");
            var child = new TreeItem<>("Choice");
            root.getChildren().add(child);
            var treeSelect = new TreeSelect<>(root);
            var content = new VBox(16, transfer, treeSelect);
            content.setPadding(new Insets(24));
            var app = new App(content);
            var stage = new Stage();
            stage.setScene(new Scene(app, 760, 420));
            stage.show();
            try {
                app.setThemeCss(Files.readString(THEMES.resolve("light.css")));
                app.applyCss();
                app.layout();
                ((Button) treeSelect.getChildren().getFirst()).fire();
                assertTrue(treeSelect.isShowing());
                var popupRoot = treeSelect.getTreeView().getScene().getRoot();
                popupRoot.applyCss();
                popupRoot.layout();
                var listFill = transfer.getAvailableView().getBackground().getFills().getLast().getFill();
                var treeFill = treeSelect.getTreeView().getBackground().getFills().getLast().getFill();
                assertInstanceOf(Color.class, listFill);
                assertEquals(listFill, treeFill);
                treeSelect.select(child);
                assertEquals("Choice", treeSelect.getSelectedValue());
            } catch (Exception failure) {
                throw new RuntimeException(failure);
            } finally {
                app.close();
                stage.close();
            }
        });
    }

    private static void save(Scene scene, String name) throws Exception {
        var image = scene.snapshot(null);
        var output = new BufferedImage((int) image.getWidth(), (int) image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < output.getHeight(); y++)
            for (int x = 0; x < output.getWidth(); x++) output.setRGB(x, y, image.getPixelReader().getArgb(x, y));
        Files.createDirectories(PREVIEWS);
        assertTrue(ImageIO.write(output, "png", PREVIEWS.resolve(name).toFile()));
    }
}
