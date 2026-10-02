package dev.normlanguage.ui.component.gallery;

import dev.normlanguage.ui.component.Anchor;
import dev.normlanguage.ui.component.App;
import dev.normlanguage.ui.component.Breadcrumb;
import dev.normlanguage.ui.component.Button;
import dev.normlanguage.ui.component.Divider;
import dev.normlanguage.ui.component.Dropdown;
import dev.normlanguage.ui.component.Flex;
import dev.normlanguage.ui.component.FloatButton;
import dev.normlanguage.ui.component.Grid;
import dev.normlanguage.ui.component.Icon;
import dev.normlanguage.ui.component.Layout;
import dev.normlanguage.ui.component.Masonry;
import dev.normlanguage.ui.component.Pagination;
import dev.normlanguage.ui.component.Space;
import dev.normlanguage.ui.component.Splitter;
import dev.normlanguage.ui.component.Steps;
import dev.normlanguage.ui.component.Tabs;
import dev.normlanguage.ui.component.Typography;
import dev.normlanguage.ui.component.Util;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Objects;

public final class NavigationExampleSections {
    private NavigationExampleSections() {}

    public static List<GalleryExamples.Section> create(Gallery.Component component, App app) {
        Util.requireFxThread();
        Objects.requireNonNull(component);
        Objects.requireNonNull(app);
        var demo = component.factory().get();
        demo.setId("gallery-example");
        var variant = variant(component.name(), app);
        boolean fullWidth = switch (component.name()) {
            case "Flex", "Grid", "Layout", "Masonry", "Anchor" -> true;
            default -> false;
        };
        var sections = List.of(
                new GalleryExamples.Section("basic", basicTitle(component.name()), demo, fullWidth),
                new GalleryExamples.Section("variant", variantTitle(component.name()), variant, fullWidth));
        var stylesheet = Objects.requireNonNull(NavigationExampleSections.class.getResource("navigation-examples.css")).toExternalForm();
        for (var section : sections)
            if (section.content() instanceof Parent parent) parent.getStylesheets().add(stylesheet);
        return sections;
    }

    private static String basicTitle(String name) {
        return switch (name) {
            case "FloatButton" -> "悬浮操作";
            case "Icon" -> "常用图标";
            case "Typography" -> "可编辑文本";
            case "Divider" -> "水平分组";
            case "Flex" -> "弹性分配";
            case "Grid" -> "响应式栅格";
            case "Layout" -> "应用布局";
            case "Masonry" -> "动态卡片";
            case "Space" -> "基础间距";
            case "Splitter" -> "可拖拽面板";
            case "Anchor" -> "页面锚点";
            case "Breadcrumb" -> "层级路径";
            case "Dropdown" -> "操作菜单";
            case "Menu" -> "菜单栏";
            case "Pagination" -> "翻页";
            case "Steps" -> "分步流程";
            case "Tabs" -> "内容切换";
            default -> throw new IllegalArgumentException("Unknown navigation example: " + name);
        };
    }

    private static String variantTitle(String name) {
        return switch (name) {
            case "FloatButton" -> "单个悬浮按钮";
            case "Icon" -> "图标与操作";
            case "Typography" -> "标题与正文";
            case "Divider" -> "垂直分割";
            case "Flex" -> "对齐与换行";
            case "Grid" -> "跨列布局";
            case "Layout" -> "紧凑工作区";
            case "Masonry" -> "窄列排布";
            case "Space" -> "调整间距";
            case "Splitter" -> "上下分屏";
            case "Anchor" -> "滚动联动";
            case "Breadcrumb" -> "动态路径";
            case "Dropdown" -> "多项操作";
            case "Menu" -> "多级菜单";
            case "Pagination" -> "调整每页数量";
            case "Steps" -> "前进与返回";
            case "Tabs" -> "动态标签页";
            default -> throw new IllegalArgumentException("Unknown navigation example: " + name);
        };
    }

    private static Node variant(String name, App app) {
        return switch (name) {
            case "FloatButton" -> {
                var layer = new StackPane(new Label("操作始终位于容器右下角"));
                layer.getStyleClass().add("nav-floating-stage");
                layer.setPrefHeight(190);
                var button = new FloatButton("反馈");
                button.setOnAction(event -> app.getMessages().success("反馈入口"));
                button.attachTo(layer);
                yield layer;
            }
            case "Icon" -> {
                var row = new HBox(12);
                for (var item : new String[][] {{"mdi2m-magnify", "搜索"}, {"mdi2c-cog", "设置"}, {"mdi2p-plus", "新建"}}) {
                    var button = action(item[1], app);
                    button.getStyleClass().add("outlined");
                    button.setGraphic(new Icon(item[0]));
                    row.getChildren().add(button);
                }
                yield surface(row);
            }
            case "Typography" -> {
                var heading = new Typography("清晰的信息层级");
                heading.getStyleClass().add("nav-type-heading");
                var body = new Typography("标题承载主题，正文补充操作所需的信息。");
                body.setCopyable(true);
                var copy = action("复制正文", app);
                copy.setOnAction(event -> {
                    var content = new javafx.scene.input.ClipboardContent();
                    content.putString(body.getText());
                    javafx.scene.input.Clipboard.getSystemClipboard().setContent(content);
                });
                yield surface(heading, body, copy);
            }
            case "Divider" -> {
                var divider = new Divider(Orientation.VERTICAL);
                divider.setPrefHeight(70);
                var row = new HBox(18, new VBox(new Label("账户"), new Label("个人资料")), divider,
                        new VBox(new Label("安全"), new Label("密码与验证")));
                row.setAlignment(Pos.CENTER_LEFT);
                yield surface(row);
            }
            case "Flex" -> {
                var cards = new Flex(new Label("第一项"), new Label("第二项"), new Label("第三项"));
                cards.getChildren().forEach(child -> child.getStyleClass().add("nav-layout-tile"));
                cards.setAlignment(Pos.CENTER_RIGHT);
                cards.setWrap(true);
                var toggle = action("切换对齐", app);
                toggle.setOnAction(event -> cards.setAlignment(cards.getAlignment() == Pos.CENTER_RIGHT ? Pos.CENTER_LEFT : Pos.CENTER_RIGHT));
                yield surface(cards, toggle);
            }
            case "Grid" -> {
                var grid = new Grid();
                grid.setResponsiveColumns(300, 2);
                grid.setResponsiveColumns(600, 4);
                for (int i = 1; i <= 5; i++) {
                    var tile = new Label(i == 1 ? "跨两列的重点模块" : "模块 " + i);
                    tile.getStyleClass().add("nav-layout-tile");
                    tile.setMaxWidth(Double.MAX_VALUE);
                    grid.addItem(tile, i == 1 ? 2 : 1);
                }
                yield surface(grid);
            }
            case "Layout" -> {
                var layout = new Layout();
                layout.getStyleClass().add("nav-app-layout");
                var top = new Label("知识库");
                top.getStyleClass().add("nav-layout-header");
                var center = new VBox(8, new Label("最近文档"), new Label("产品说明 · 团队手册 · 设计资源"));
                center.getStyleClass().add("nav-layout-main");
                layout.setTop(top);
                layout.setCenter(center);
                layout.setPrefHeight(180);
                yield layout;
            }
            case "Masonry" -> {
                var masonry = new Masonry();
                masonry.setMinimumColumnWidth(140);
                for (int i = 1; i <= 8; i++) {
                    var tile = new VBox(new Label("收藏 " + i));
                    tile.getStyleClass().add("nav-masonry-tile");
                    tile.setPrefHeight(65 + (i % 3) * 35);
                    masonry.getChildren().add(tile);
                }
                yield masonry;
            }
            case "Space" -> {
                var controls = new Space(action("保存", app), action("预览", app), action("取消", app));
                var slider = new Slider(0, 32, 8);
                slider.setPrefWidth(220);
                slider.valueProperty().addListener((observable, before, value) -> controls.setSpacing(value.doubleValue()));
                yield surface(controls, new Label("拖动滑块调整按钮之间的距离"), slider);
            }
            case "Splitter" -> {
                var split = new Splitter(new TextArea("上方面板：编辑内容"), new TextArea("下方面板：预览结果"));
                split.setOrientation(Orientation.VERTICAL);
                split.setPrefHeight(230);
                yield split;
            }
            case "Anchor" -> {
                var first = new VBox(new Label("快速开始"));
                var second = new VBox(new Label("更多设置"));
                first.setMinHeight(170);
                second.setMinHeight(170);
                var scroll = new ScrollPane(new VBox(first, second));
                scroll.setFitToWidth(true);
                scroll.setPrefHeight(130);
                var anchor = new Anchor(scroll);
                anchor.getItems().addAll(new Anchor.Item("开始", first), new Anchor.Item("设置", second));
                var active = new Label("当前：开始");
                anchor.activeItemProperty().addListener((observable, before, item) -> {
                    if (item != null) active.setText("当前：" + item.text());
                });
                var move = action("滚动到设置", app);
                move.setOnAction(event -> scroll.setVvalue(1));
                yield surface(anchor, active, scroll, move);
            }
            case "Breadcrumb" -> {
                var crumb = new Breadcrumb();
                crumb.getItems().add(new Breadcrumb.Item("首页", () -> app.getMessages().success("首页")));
                crumb.getItems().add(new Breadcrumb.Item("项目", () -> app.getMessages().success("项目")));
                var add = action("进入详情", app);
                add.setOnAction(event -> {
                    if (crumb.getItems().size() == 2) crumb.getItems().add(new Breadcrumb.Item("详情", () -> app.getMessages().success("详情")));
                    else crumb.getItems().removeLast();
                });
                yield surface(crumb, add);
            }
            case "Dropdown" -> {
                var edit = action("编辑", app);
                var share = action("分享", app);
                var menu = new Dropdown("更多操作", new VBox(8, edit, share));
                yield surface(menu, new Label("点击按钮打开包含两项操作的菜单"));
            }
            case "Menu" -> {
                var menu = new dev.normlanguage.ui.component.Menu();
                var open = new MenuItem("打开文档");
                open.setOnAction(event -> app.getMessages().success("打开文档"));
                var save = new MenuItem("保存文档");
                save.setOnAction(event -> app.getMessages().success("保存文档"));
                menu.add("文件", open, save);
                var copy = new MenuItem("复制");
                copy.setOnAction(event -> app.getMessages().success("已复制"));
                menu.add("编辑", copy);
                yield surface(menu);
            }
            case "Pagination" -> {
                var pages = new Pagination(120, 10);
                var sizes = new ChoiceBox<Integer>();
                sizes.getItems().addAll(10, 20, 30);
                sizes.setValue(10);
                sizes.valueProperty().addListener((observable, before, value) -> pages.setPageSize(value));
                yield surface(new HBox(14, new Label("每页条数"), sizes), pages);
            }
            case "Steps" -> {
                var steps = new Steps("开始", "处理中", "已完成");
                var back = action("上一步", app);
                back.setOnAction(event -> steps.setCurrentStep(Math.max(0, steps.getCurrentStep() - 1)));
                var next = action("下一步", app);
                next.setOnAction(event -> steps.setCurrentStep(Math.min(steps.getSteps().size() - 1, steps.getCurrentStep() + 1)));
                yield surface(steps, new HBox(12, back, next));
            }
            case "Tabs" -> {
                var tabs = new Tabs();
                tabs.add("文档", new Label("团队文档与规范"));
                tabs.add("活动", new Label("最新协作记录"));
                tabs.setPrefHeight(170);
                var add = action("添加标签", app);
                add.setOnAction(event -> tabs.add("新标签 " + (tabs.getTabs().size() + 1), new Label("新建内容")));
                yield surface(tabs, add);
            }
            default -> throw new IllegalArgumentException("Unknown navigation example: " + name);
        };
    }

    private static VBox surface(Node... nodes) {
        var surface = new VBox(14, nodes);
        surface.getStyleClass().add("gallery-demo-surface");
        return surface;
    }

    private static Button action(String text, App app) {
        var button = new Button(text);
        button.setOnAction(event -> app.getMessages().success(text));
        return button;
    }
}
