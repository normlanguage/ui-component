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

public final class NavigationExamples {
    private NavigationExamples() {}

    public static java.util.List<Gallery.Component> components(App app) {
        var examples = new java.util.ArrayList<Gallery.Component>();
        examples.add(new Gallery.Component(Gallery.Category.GENERAL, "FloatButton", "悬浮按钮", () -> {
            var layer = new StackPane(new Label("Use the floating actions"));
            layer.setPrefHeight(180);
            var first = new FloatButton("Add");
            first.setOnAction(event -> app.getMessages().success("Added"));
            var second = new FloatButton("Help");
            second.setOnAction(event -> app.getMessages().success("Help selected"));
            new FloatButton.Group(first, second).attachTo(layer);
            return layer;
        }));
        examples.add(new Gallery.Component(Gallery.Category.GENERAL, "Icon", "图标", () -> new Space(new Icon("mdi2h-home"), new Label("Material Design home icon"))));
        examples.add(new Gallery.Component(Gallery.Category.GENERAL, "Typography", "排版", () -> {
            var text = new Typography("Double click the Edit menu, or use the context menu");
            text.setCopyable(true);
            text.setEditable(true);
            text.setOnEditCommit(event -> app.getMessages().success("Text updated"));
            var edit = new dev.normlanguage.ui.component.Button("Edit text");
            edit.setOnAction(event -> text.beginEdit());
            return new VBox(8, text, edit);
        }));
        examples.add(new Gallery.Component(Gallery.Category.LAYOUT, "Divider", "分割线", () -> new VBox(8, new Label("Above"), new Divider(), new Label("Below"))));
        examples.add(new Gallery.Component(Gallery.Category.LAYOUT, "Flex", "弹性布局", () -> {
            var stretch = new dev.normlanguage.ui.component.Button("Grow with space");
            var fixed = new dev.normlanguage.ui.component.Button("Fixed");
            var flex = new Flex(stretch, fixed);
            flex.setGrow(stretch, 1);
            var wrapping = new dev.normlanguage.ui.component.Button("Toggle wrapping");
            wrapping.setOnAction(event -> flex.setWrap(!flex.isWrap()));
            return new VBox(8, flex, wrapping);
        }));
        examples.add(new Gallery.Component(Gallery.Category.LAYOUT, "Grid", "栅格", () -> {
            var grid = new Grid();
            grid.setResponsiveColumns(240, 2);
            grid.setResponsiveColumns(480, 3);
            for (int i = 1; i <= 6; i++) grid.addItem(new dev.normlanguage.ui.component.Button("Cell " + i), 1);
            return grid;
        }));
        examples.add(new Gallery.Component(Gallery.Category.LAYOUT, "Layout", "布局", () -> {
            var layout = new Layout();
            layout.setTop(new Label("Header"));
            layout.setLeft(new Label("Sidebar"));
            layout.setCenter(new Label("Content"));
            layout.setBottom(new Label("Footer"));
            return layout;
        }));
        examples.add(new Gallery.Component(Gallery.Category.LAYOUT, "Masonry", "瀑布流", () -> {
            var masonry = new Masonry();
            for (int i = 0; i < 6; i++) {
                var tile = new Label("Tile " + (i + 1));
                tile.setMinHeight(40 + (i % 3) * 35);
                masonry.getChildren().add(tile);
            }
            masonry.setPrefHeight(240);
            return masonry;
        }));
        examples.add(new Gallery.Component(Gallery.Category.LAYOUT, "Space", "间距", () -> new Space(new dev.normlanguage.ui.component.Button("One"),
                new dev.normlanguage.ui.component.Button("Two"), new dev.normlanguage.ui.component.Button("Three"))));
        examples.add(new Gallery.Component(Gallery.Category.LAYOUT, "Splitter", "分隔面板", () -> {
            var split = new Splitter(new TextArea("Left panel"), new TextArea("Right panel"));
            split.setPrefHeight(160);
            return split;
        }));
        examples.add(new Gallery.Component(Gallery.Category.NAVIGATION, "Anchor", "锚点", () -> {
            var first = new Label("Section one");
            var second = new Label("Section two");
            first.setMinHeight(160);
            second.setMinHeight(160);
            var scroll = new ScrollPane(new VBox(first, second));
            scroll.setPrefHeight(100);
            var anchor = new Anchor(scroll);
            anchor.getItems().addAll(new Anchor.Item("One", first), new Anchor.Item("Two", second));
            return new VBox(8, anchor, scroll);
        }));
        examples.add(new Gallery.Component(Gallery.Category.NAVIGATION, "Breadcrumb", "面包屑", () -> {
            var crumb = new Breadcrumb();
            crumb.getItems().addAll(new Breadcrumb.Item("Home", () -> app.getMessages().success("Home")),
                    new Breadcrumb.Item("Library", () -> app.getMessages().success("Library")));
            return crumb;
        }));
        examples.add(new Gallery.Component(Gallery.Category.NAVIGATION, "Dropdown", "下拉菜单", () -> {
            var action = new dev.normlanguage.ui.component.Button("Choose option");
            action.setOnAction(event -> app.getMessages().success("Option chosen"));
            return new Dropdown("Open menu", new VBox(action));
        }));
        examples.add(new Gallery.Component(Gallery.Category.NAVIGATION, "Menu", "导航菜单", () -> {
            var item = new MenuItem("New document");
            item.setOnAction(event -> app.getMessages().success("New document"));
            var menu = new Menu();
            menu.add("File", item);
            return menu;
        }));
        examples.add(new Gallery.Component(Gallery.Category.NAVIGATION, "Pagination", "分页", () -> {
            var pages = new Pagination(95, 10);
            var selected = new Label("Page 1");
            pages.currentPageProperty().addListener((observable, old, page) -> selected.setText("Page " + page));
            return new VBox(8, pages, selected);
        }));
        examples.add(new Gallery.Component(Gallery.Category.NAVIGATION, "Steps", "步骤条", () -> {
            var steps = new Steps("Details", "Confirm", "Complete");
            var next = new dev.normlanguage.ui.component.Button("Next step");
            next.setOnAction(event -> steps.setCurrentStep((steps.getCurrentStep() + 1) % steps.getSteps().size()));
            return new VBox(8, steps, next);
        }));
        examples.add(new Gallery.Component(Gallery.Category.NAVIGATION, "Tabs", "标签页", () -> {
            var tabs = new Tabs();
            tabs.add("First", new Label("First page"));
            tabs.add("Second", new Label("Second page"));
            return tabs;
        }));
        return java.util.List.copyOf(examples);
    }
}
