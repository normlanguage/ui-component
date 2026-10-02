package dev.normlanguage.ui.component.gallery;

import dev.normlanguage.ui.component.Anchor;
import dev.normlanguage.ui.component.App;
import dev.normlanguage.ui.component.Breadcrumb;
import dev.normlanguage.ui.component.Divider;
import dev.normlanguage.ui.component.Dropdown;
import dev.normlanguage.ui.component.Flex;
import dev.normlanguage.ui.component.FloatButton;
import dev.normlanguage.ui.component.Grid;
import dev.normlanguage.ui.component.Icon;
import dev.normlanguage.ui.component.Layout;
import dev.normlanguage.ui.component.Masonry;
import dev.normlanguage.ui.component.Menu;
import dev.normlanguage.ui.component.Pagination;
import dev.normlanguage.ui.component.Space;
import dev.normlanguage.ui.component.Splitter;
import dev.normlanguage.ui.component.Steps;
import dev.normlanguage.ui.component.Tabs;
import dev.normlanguage.ui.component.Typography;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class NavigationExamples {
    private NavigationExamples() {}

    public static Map<String, Supplier<Node>> examples(App app) {
        var examples = new LinkedHashMap<String, Supplier<Node>>();
        examples.put("FloatButton", () -> {
            var layer = new StackPane(new Label("Use the floating actions"));
            layer.setPrefHeight(180);
            var first = new FloatButton("Add");
            first.setOnAction(event -> app.getMessages().success("Added"));
            var second = new FloatButton("Help");
            second.setOnAction(event -> app.getMessages().success("Help selected"));
            new FloatButton.Group(first, second).attachTo(layer);
            return layer;
        });
        examples.put("Icon", () -> new Space(new Icon("mdi2h-home"), new Label("Material Design home icon")));
        examples.put("Typography", () -> {
            var text = new Typography("Double click the Edit menu, or use the context menu");
            text.setCopyable(true);
            text.setEditable(true);
            text.setOnEditCommit(event -> app.getMessages().success("Text updated"));
            var edit = new dev.normlanguage.ui.component.Button("Edit text");
            edit.setOnAction(event -> text.beginEdit());
            return new VBox(8, text, edit);
        });
        examples.put("Divider", () -> new VBox(8, new Label("Above"), new Divider(), new Label("Below")));
        examples.put("Flex", () -> {
            var stretch = new dev.normlanguage.ui.component.Button("Grow with space");
            var fixed = new dev.normlanguage.ui.component.Button("Fixed");
            var flex = new Flex(stretch, fixed);
            flex.setGrow(stretch, 1);
            var wrapping = new dev.normlanguage.ui.component.Button("Toggle wrapping");
            wrapping.setOnAction(event -> flex.setWrap(!flex.isWrap()));
            return new VBox(8, flex, wrapping);
        });
        examples.put("Grid", () -> {
            var grid = new Grid();
            grid.setResponsiveColumns(240, 2);
            grid.setResponsiveColumns(480, 3);
            for (int i = 1; i <= 6; i++) grid.addItem(new dev.normlanguage.ui.component.Button("Cell " + i), 1);
            return grid;
        });
        examples.put("Layout", () -> {
            var layout = new Layout();
            layout.setTop(new Label("Header"));
            layout.setLeft(new Label("Sidebar"));
            layout.setCenter(new Label("Content"));
            layout.setBottom(new Label("Footer"));
            return layout;
        });
        examples.put("Masonry", () -> {
            var masonry = new Masonry();
            for (int i = 0; i < 6; i++) {
                var tile = new Label("Tile " + (i + 1));
                tile.setMinHeight(40 + (i % 3) * 35);
                masonry.getChildren().add(tile);
            }
            masonry.setPrefHeight(240);
            return masonry;
        });
        examples.put("Space", () -> new Space(new dev.normlanguage.ui.component.Button("One"),
                new dev.normlanguage.ui.component.Button("Two"), new dev.normlanguage.ui.component.Button("Three")));
        examples.put("Splitter", () -> {
            var split = new Splitter(new TextArea("Left panel"), new TextArea("Right panel"));
            split.setPrefHeight(160);
            return split;
        });
        examples.put("Anchor", () -> {
            var first = new Label("Section one");
            var second = new Label("Section two");
            first.setMinHeight(160);
            second.setMinHeight(160);
            var scroll = new ScrollPane(new VBox(first, second));
            scroll.setPrefHeight(100);
            var anchor = new Anchor(scroll);
            anchor.getItems().addAll(new Anchor.Item("One", first), new Anchor.Item("Two", second));
            return new VBox(8, anchor, scroll);
        });
        examples.put("Breadcrumb", () -> {
            var crumb = new Breadcrumb();
            crumb.getItems().addAll(new Breadcrumb.Item("Home", () -> app.getMessages().success("Home")),
                    new Breadcrumb.Item("Library", () -> app.getMessages().success("Library")));
            return crumb;
        });
        examples.put("Dropdown", () -> {
            var action = new dev.normlanguage.ui.component.Button("Choose option");
            action.setOnAction(event -> app.getMessages().success("Option chosen"));
            return new Dropdown("Open menu", new VBox(action));
        });
        examples.put("Menu", () -> {
            var item = new MenuItem("New document");
            item.setOnAction(event -> app.getMessages().success("New document"));
            var menu = new Menu();
            menu.add("File", item);
            return menu;
        });
        examples.put("Pagination", () -> {
            var pages = new Pagination(95, 10);
            var selected = new Label("Page 1");
            pages.currentPageProperty().addListener((observable, old, page) -> selected.setText("Page " + page));
            return new VBox(8, pages, selected);
        });
        examples.put("Steps", () -> {
            var steps = new Steps("Details", "Confirm", "Complete");
            var next = new dev.normlanguage.ui.component.Button("Next step");
            next.setOnAction(event -> steps.setCurrentStep((steps.getCurrentStep() + 1) % steps.getSteps().size()));
            return new VBox(8, steps, next);
        });
        examples.put("Tabs", () -> {
            var tabs = new Tabs();
            tabs.add("First", new Label("First page"));
            tabs.add("Second", new Label("Second page"));
            return tabs;
        });
        return examples;
    }
}
