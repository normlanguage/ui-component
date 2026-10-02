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
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public final class NavigationExamples {
    private NavigationExamples() {}

    public static java.util.List<Gallery.Component> components(App app) {
        var examples = new java.util.ArrayList<Gallery.Component>();
        examples.add(new Gallery.Component(Gallery.Category.GENERAL, "FloatButton", "悬浮按钮", () -> {
            var context = new VBox(12, new Label("项目任务"), new Label("今天需要完成的工作"),
                    new Label("• 审核设计稿"), new Label("• 更新组件文档"), new Label("• 处理反馈"));
            context.getStyleClass().add("nav-floating-content");
            var layer = new StackPane(context);
            layer.getStyleClass().add("nav-floating-stage");
            layer.setPrefHeight(220);
            var first = new FloatButton("新建");
            first.setOnAction(event -> app.getMessages().success("已创建项目"));
            var second = new FloatButton("帮助");
            second.setOnAction(event -> app.getMessages().success("打开帮助"));
            new FloatButton.Group(first, second).attachTo(layer);
            return layer;
        }));
        examples.add(new Gallery.Component(Gallery.Category.GENERAL, "Icon", "图标", () -> {
            var icons = new FlowPane(14, 14);
            for (var item : new String[][] {{"mdi2h-home", "首页"}, {"mdi2m-magnify", "搜索"},
                    {"mdi2c-cog", "设置"}, {"mdi2p-plus", "新建"}, {"mdi2d-delete", "删除"}}) {
                var icon = new Icon(item[0]);
                icon.setIconSize(24);
                var sample = new VBox(10, icon, new Label(item[1]));
                sample.getStyleClass().add("nav-icon-sample");
                icons.getChildren().add(sample);
            }
            return icons;
        }));
        examples.add(new Gallery.Component(Gallery.Category.GENERAL, "Typography", "排版", () -> {
            var heading = new Typography("把重要信息放在前面");
            heading.getStyleClass().add("nav-type-heading");
            var caption = new Typography("文档 · 5 分钟阅读");
            caption.getStyleClass().add("nav-type-caption");
            var text = new Typography("一段可以编辑，也可以复制的正文内容。");
            text.setCopyable(true);
            text.setEditable(true);
            text.setOnEditCommit(event -> app.getMessages().success("Text updated"));
            var edit = new dev.normlanguage.ui.component.Button("编辑正文");
            edit.setOnAction(event -> text.beginEdit());
            return new VBox(12, heading, caption, text, edit);
        }));
        examples.add(new Gallery.Component(Gallery.Category.LAYOUT, "Divider", "分割线", () ->
                new VBox(16, new Label("账户设置"), new Divider(),
                        new Label("个人信息与安全选项分组显示，便于快速扫描。"))));
        examples.add(new Gallery.Component(Gallery.Category.LAYOUT, "Flex", "弹性布局", () -> {
            var stretch = new VBox(8, new Label("待办任务"), new Label("设计评审 · 文档更新 · 发布检查"));
            stretch.getStyleClass().add("nav-layout-tile");
            var fixed = new VBox(8, new Label("操作"), new Label("查看详情"));
            fixed.getStyleClass().add("nav-layout-tile");
            var flex = new Flex(stretch, fixed);
            flex.setGrow(stretch, 1);
            flex.setPrefWidth(520);
            var wrapping = new dev.normlanguage.ui.component.Button("切换换行");
            wrapping.setOnAction(event -> flex.setWrap(!flex.isWrap()));
            return new VBox(16, flex, wrapping);
        }));
        examples.add(new Gallery.Component(Gallery.Category.LAYOUT, "Grid", "栅格", () -> {
            var grid = new Grid();
            grid.setResponsiveColumns(360, 2);
            grid.setResponsiveColumns(650, 3);
            for (int i = 1; i <= 6; i++) {
                var tile = new VBox(8, new Label("模块 " + i), new Label(i % 2 == 0 ? "次要信息" : "概览信息"));
                tile.getStyleClass().add("nav-layout-tile");
                grid.addItem(tile, i == 1 ? 2 : 1);
            }
            return grid;
        }));
        examples.add(new Gallery.Component(Gallery.Category.LAYOUT, "Layout", "布局", () -> {
            var layout = new Layout();
            layout.setId("nav-layout-primary");
            layout.getStyleClass().add("nav-app-layout");
            var top = new HBox(16, new Label("工作台"), new Label("项目"), new Label("团队"));
            top.getStyleClass().add("nav-layout-header");
            var left = new VBox(12, new Label("概览"), new Label("任务"), new Label("成员"));
            left.getStyleClass().add("nav-layout-sidebar");
            var center = new VBox(12, new Label("项目概览"), new Label("在这里呈现当前项目的关键数据与待办事项。"));
            center.getStyleClass().add("nav-layout-main");
            var footer = new Label("最近更新 · 今天");
            footer.getStyleClass().add("nav-layout-footer");
            layout.setTop(top);
            layout.setLeft(left);
            layout.setCenter(center);
            layout.setBottom(footer);
            layout.setPrefHeight(260);
            var toggle = new dev.normlanguage.ui.component.Button("切换侧栏");
            toggle.setId("nav-layout-toggle");
            toggle.setOnAction(event -> layout.setLeft(layout.getLeft() == null ? left : null));
            return new VBox(14, layout, toggle);
        }));
        examples.add(new Gallery.Component(Gallery.Category.LAYOUT, "Masonry", "瀑布流", () -> {
            var masonry = new Masonry();
            masonry.setId("nav-masonry-primary");
            masonry.setMinimumColumnWidth(170);
            for (int i = 0; i < 12; i++) {
                var tile = new VBox(8, new Label("灵感 " + (i + 1)), new Label(i % 2 == 0 ? "图文卡片" : "记录与分享"));
                tile.getStyleClass().add("nav-masonry-tile");
                tile.setPrefHeight(80 + (i % 4) * 35);
                masonry.getChildren().add(tile);
            }
            var add = new dev.normlanguage.ui.component.Button("添加卡片");
            add.setId("nav-masonry-add");
            add.setOnAction(event -> {
                var tile = new VBox(8, new Label("新卡片"), new Label("刚刚添加"));
                tile.getStyleClass().add("nav-masonry-tile");
                tile.setPrefHeight(110);
                masonry.getChildren().add(tile);
            });
            return new VBox(16, masonry, add);
        }));
        examples.add(new Gallery.Component(Gallery.Category.LAYOUT, "Space", "间距", () -> {
            var save = new dev.normlanguage.ui.component.Button("保存");
            save.setOnAction(event -> app.getMessages().success("已保存"));
            var preview = new dev.normlanguage.ui.component.Button("预览");
            preview.getStyleClass().add("outlined");
            preview.setOnAction(event -> app.getMessages().success("打开预览"));
            var cancel = new dev.normlanguage.ui.component.Button("取消");
            cancel.getStyleClass().add("text");
            cancel.setOnAction(event -> app.getMessages().success("已取消"));
            return new VBox(14, new Label("编辑文档"), new Label("保存更改或先查看预览。"), new Space(save, preview, cancel));
        }));
        examples.add(new Gallery.Component(Gallery.Category.LAYOUT, "Splitter", "分隔面板", () -> {
            var fileList = new VBox(12, new Label("文件列表"), new Label("README.md"),
                    new Label("Architecture.md"), new Label("Components.md"));
            fileList.getStyleClass().add("nav-splitter-list");
            var editor = new TextArea("# 组件文档\n\n在右侧编辑文档内容，拖动分隔线调整宽度。\n\n- 基础组件\n- 数据录入\n- 反馈组件");
            var split = new Splitter(fileList, editor);
            split.setPrefHeight(230);
            return split;
        }));
        examples.add(new Gallery.Component(Gallery.Category.NAVIGATION, "Anchor", "锚点", () -> {
            var first = new VBox(8, new Label("概览"), new Label("先了解这个页面包含哪些配置。"));
            var second = new VBox(8, new Label("配置"), new Label("根据业务需要调整设置。"));
            var third = new VBox(8, new Label("结果"), new Label("确认设置并查看最终结果。"));
            for (var section : java.util.List.of(first, second, third)) {
                section.getStyleClass().add("nav-anchor-content");
                section.setMinHeight(150);
            }
            var scroll = new ScrollPane(new VBox(first, second, third));
            scroll.setFitToWidth(true);
            scroll.setPrefHeight(190);
            var anchor = new Anchor(scroll);
            anchor.setId("nav-anchor-primary");
            anchor.getItems().addAll(new Anchor.Item("概览", first), new Anchor.Item("配置", second), new Anchor.Item("结果", third));
            return new VBox(12, anchor, scroll);
        }));
        examples.add(new Gallery.Component(Gallery.Category.NAVIGATION, "Breadcrumb", "面包屑", () -> {
            var crumb = new Breadcrumb();
            crumb.getItems().addAll(new Breadcrumb.Item("首页", () -> app.getMessages().success("首页")),
                    new Breadcrumb.Item("工作区", () -> app.getMessages().success("工作区")),
                    new Breadcrumb.Item("项目文档", () -> app.getMessages().success("项目文档")),
                    new Breadcrumb.Item("设计规范", () -> app.getMessages().success("设计规范")));
            return new VBox(14, crumb, new Label("当前位置：设计规范"));
        }));
        examples.add(new Gallery.Component(Gallery.Category.NAVIGATION, "Dropdown", "下拉菜单", () -> {
            var selected = new Label("尚未选择操作");
            var edit = new dev.normlanguage.ui.component.Button("编辑文档");
            var share = new dev.normlanguage.ui.component.Button("分享链接");
            var archive = new dev.normlanguage.ui.component.Button("归档文档");
            for (var button : java.util.List.of(edit, share, archive))
                button.setOnAction(event -> selected.setText("已选择：" + button.getText()));
            var dropdown = new Dropdown("更多操作", new VBox(8, edit, share, archive));
            return new VBox(12, new Label("文档操作"), dropdown, selected);
        }));
        examples.add(new Gallery.Component(Gallery.Category.NAVIGATION, "Menu", "导航菜单", () -> {
            var menu = new Menu();
            var create = new MenuItem("新建文档");
            create.setOnAction(event -> app.getMessages().success("新建文档"));
            var open = new MenuItem("打开文档");
            open.setOnAction(event -> app.getMessages().success("打开文档"));
            var save = new MenuItem("保存");
            save.setOnAction(event -> app.getMessages().success("已保存"));
            menu.add("文件", create, open, save);
            var copy = new MenuItem("复制");
            copy.setOnAction(event -> app.getMessages().success("已复制"));
            var paste = new MenuItem("粘贴");
            paste.setOnAction(event -> app.getMessages().success("已粘贴"));
            menu.add("编辑", copy, paste);
            var zoom = new MenuItem("放大");
            zoom.setOnAction(event -> app.getMessages().success("已放大"));
            menu.add("视图", zoom);
            return new VBox(10, menu, new Label("使用文件、编辑和视图菜单完成文档操作。"));
        }));
        examples.add(new Gallery.Component(Gallery.Category.NAVIGATION, "Pagination", "分页", () -> {
            var records = java.util.stream.IntStream.rangeClosed(1, 24).mapToObj(index -> "文档 " + index).toList();
            var pages = new Pagination(records.size(), 4);
            pages.setId("nav-pagination-primary");
            var list = new VBox(8);
            Runnable refresh = () -> {
                list.getChildren().clear();
                int start = (pages.getCurrentPage() - 1) * pages.getPageSize();
                records.stream().skip(start).limit(pages.getPageSize())
                        .forEach(record -> list.getChildren().add(new Label(record)));
            };
            pages.currentPageProperty().addListener((observable, old, page) -> refresh.run());
            refresh.run();
            return new VBox(14, new Label("项目文档"), list, pages);
        }));
        examples.add(new Gallery.Component(Gallery.Category.NAVIGATION, "Steps", "步骤条", () -> {
            var steps = new Steps("填写资料", "确认内容", "完成提交");
            steps.setId("nav-steps-primary");
            var current = new Label("当前：填写资料");
            steps.currentStepProperty().addListener((observable, before, value) -> current.setText("当前：" + steps.getSteps().get(value.intValue())));
            var next = new dev.normlanguage.ui.component.Button("下一步");
            next.setId("nav-steps-next");
            next.setOnAction(event -> steps.setCurrentStep((steps.getCurrentStep() + 1) % steps.getSteps().size()));
            return new VBox(16, steps, current, next);
        }));
        examples.add(new Gallery.Component(Gallery.Category.NAVIGATION, "Tabs", "标签页", () -> {
            var tabs = new Tabs();
            tabs.add("概览", new VBox(8, new Label("项目概览"), new Label("查看最新进度与待处理事项。")));
            tabs.add("成员", new VBox(8, new Label("团队成员"), new Label("管理协作者与访问权限。")));
            tabs.add("设置", new VBox(8, new Label("项目设置"), new Label("按需要调整工作区偏好。")));
            tabs.setPrefHeight(210);
            return tabs;
        }));
        return java.util.List.copyOf(examples);
    }
}
