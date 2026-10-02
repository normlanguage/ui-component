package dev.normlanguage.ui.component.gallery;

import dev.normlanguage.ui.component.*;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DisplayExampleSections {
    private DisplayExampleSections() {}

    public static List<GalleryExamples.Section> create(Gallery.Component component, App app) {
        Util.requireFxThread();
        Objects.requireNonNull(component);
        Objects.requireNonNull(app);
        var primary = component.factory().get();
        primary.setId("gallery-example");
        var basic = new GalleryExamples.Section("basic", "基础用法", surface(primary), wide(component.name()));
        var state = new GalleryExamples.Section("states", stateTitle(component.name()),
                surface(comparison(component.name())), wide(component.name()));
        var sections = List.of(basic, state);
        var stylesheet = Objects.requireNonNull(DisplayExampleSections.class.getResource("display-examples.css")).toExternalForm();
        for (var section : sections) if (section.content() instanceof Parent parent) parent.getStylesheets().add(stylesheet);
        return sections;
    }

    private static boolean wide(String name) {
        return switch (name) {
            case "Calendar", "Carousel", "Image", "Listy", "QRCode", "Table", "Tree" -> true;
            default -> false;
        };
    }

    private static String stateTitle(String name) {
        return switch (name) {
            case "Avatar" -> "文字回退与头像尺寸";
            case "Badge" -> "计数与清零";
            case "Calendar" -> "月份导航与今日定位";
            case "Card" -> "状态卡片";
            case "Carousel" -> "自动播放";
            case "Collapse" -> "常见问题";
            case "Descriptions" -> "订单详情";
            case "Empty" -> "不同空状态";
            case "Image" -> "缩略图与预览";
            case "List" -> "动态列表";
            case "Listy" -> "大数据定位";
            case "QRCode" -> "输入内容生成";
            case "Segmented" -> "视图切换";
            case "Statistic" -> "实时指标";
            case "Table" -> "选择与排序";
            case "Tag" -> "可关闭标签";
            case "Timeline" -> "运行记录";
            case "Tree" -> "层级导航";
            default -> throw new IllegalArgumentException("Unknown display component: " + name);
        };
    }

    private static VBox surface(Node content) {
        var panel = new VBox(16, content);
        panel.getStyleClass().add("gallery-demo-surface");
        panel.getStyleClass().add("gallery-display-surface");
        return panel;
    }

    private static HBox row(Node... nodes) {
        var row = new HBox(16, nodes);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("gallery-demo-row");
        return row;
    }

    private static Label caption(String text) {
        var label = new Label(text);
        label.getStyleClass().add("gallery-demo-caption");
        return label;
    }

    private static dev.normlanguage.ui.component.Button action(String text, Runnable run) {
        var button = new dev.normlanguage.ui.component.Button(text);
        button.setOnAction(event -> run.run());
        return button;
    }

    private static Node comparison(String name) {
        return switch (name) {
            case "Avatar" -> avatarStates();
            case "Badge" -> badgeStates();
            case "Calendar" -> calendarNavigation();
            case "Card" -> cardStates();
            case "Carousel" -> carouselPlayback();
            case "Collapse" -> collapseQuestions();
            case "Descriptions" -> orderDescriptions();
            case "Empty" -> emptyStates();
            case "Image" -> imageSizes();
            case "List" -> dynamicList();
            case "Listy" -> virtualList();
            case "QRCode" -> editableQr();
            case "Segmented" -> segmentedViews();
            case "Statistic" -> liveStatistic();
            case "Table" -> sortableTable();
            case "Tag" -> removableTags();
            case "Timeline" -> activityTimeline();
            case "Tree" -> directoryTree();
            default -> throw new IllegalArgumentException("Unknown display component: " + name);
        };
    }

    private static Node avatarStates() {
        var initials = new Avatar("WY");
        var letter = new Avatar("N");
        var photo = new Avatar("AL");
        photo.setImage(DisplayExamples.landscape());
        var name = new TextField("WY");
        name.setPrefWidth(92);
        name.textProperty().addListener((observable, old, value) -> initials.setText(value));
        return new VBox(18, row(initials, letter, photo), row(caption("修改回退文字"), name));
    }

    private static Node badgeStates() {
        var unread = new Badge(new Avatar("未"));
        unread.setCount(5);
        var empty = new Badge(new Avatar("已"));
        empty.setCount(0);
        return new VBox(18, row(unread, caption("未读通知"), empty, caption("已处理")), row(
                action("+1", () -> unread.setCount(unread.getCount() + 1)),
                action("标记已读", () -> unread.setCount(0)),
                caption("0 条时徽标隐藏")));
    }

    private static Node calendarNavigation() {
        var calendar = new dev.normlanguage.ui.component.Calendar();
        var selected = new Label();
        selected.textProperty().bind(calendar.valueProperty().asString("选中：%s"));
        var content = new VBox(12, calendar, row(
                action("上个月", calendar::previousMonth),
                action("今天", () -> calendar.setValue(LocalDate.now())),
                action("下个月", calendar::nextMonth)), selected);
        content.setMaxWidth(Double.MAX_VALUE);
        return content;
    }

    private static Node cardStates() {
        var active = new Card("进行中", new VBox(8, new Label("组件示例扩展"), caption("18 / 18 个展示控件")));
        var complete = new Card("已完成", new VBox(8, new Label("主题连接"), caption("明暗切换已验证")));
        active.setPrefWidth(240);
        complete.setPrefWidth(240);
        return new VBox(12, active, complete);
    }

    private static Node carouselPlayback() {
        var carousel = new Carousel(List.of(
                slide("快速开始", "将组件放入现有 JavaFX Scene"),
                slide("主题切换", "状态与焦点随节点保持"),
                slide("局部作用域", "弹层跟随所属配置")));
        carousel.setPrefSize(520, 160);
        var status = new Label("自动播放已关闭");
        var toggle = action("开始播放", () -> {
            carousel.setAutoPlay(!carousel.isAutoPlay());
            status.setText(carousel.isAutoPlay() ? "自动播放已开启" : "自动播放已关闭");
        });
        return new VBox(12, carousel, row(toggle, status));
    }

    private static VBox slide(String title, String detail) {
        var slide = new VBox(10, new Label(title), caption(detail));
        slide.getStyleClass().add("gallery-display-slide");
        return slide;
    }

    private static Node collapseQuestions() {
        var first = new Collapse("可以嵌入现有应用吗？", new Label("可以，App 可直接放入 Scene。"));
        first.setExpanded(true);
        var second = new Collapse("主题可以局部覆盖吗？", new Label("可以为子树指定独立主题。"));
        var third = new Collapse("组件可以组合吗？", new Label("可以组合 JavaFX 原生节点。"));
        return new VBox(2, first, second, third);
    }

    private static Node orderDescriptions() {
        var details = new Descriptions();
        details.add("订单编号", new Label("NORM-2026-018"));
        details.add("状态", new Label("已完成"));
        details.add("负责人", new Label("设计系统团队"));
        details.add("交付日期", new Label("2026-10-02"));
        return details;
    }

    private static Node emptyStates() {
        var noSearch = new Empty("没有匹配的搜索结果");
        var noMessages = new Empty("暂时没有通知");
        var reset = action("重置搜索", () -> noSearch.setDescription("筛选条件已重置"));
        return new VBox(16, noSearch, noMessages, reset);
    }

    private static Node imageSizes() {
        var large = new dev.normlanguage.ui.component.Image(DisplayExamples.landscape());
        large.setPreviewable(true);
        large.getImageView().setFitWidth(340);
        large.getImageView().setFitHeight(200);
        var small = new dev.normlanguage.ui.component.Image(DisplayExamples.landscape());
        small.setPreviewable(true);
        small.getImageView().setFitWidth(170);
        small.getImageView().setFitHeight(100);
        return new VBox(12, row(large, small), caption("同一图片可按场景缩放；点击任一图片预览原图"));
    }

    private static Node dynamicList() {
        var list = new dev.normlanguage.ui.component.List<String>();
        list.getItems().addAll("待评审 · Calendar", "开发中 · Carousel", "已完成 · Table");
        list.setPrefSize(480, 190);
        var input = new TextField();
        input.setPromptText("输入新的任务");
        input.setPrefWidth(250);
        var add = action("添加", () -> {
            if (!input.getText().isBlank()) { list.getItems().add(input.getText().trim()); input.clear(); }
        });
        var remove = action("移除选中", () -> {
            var selected = list.getSelectionModel().getSelectedItem();
            if (selected != null) list.getItems().remove(selected);
        });
        return new VBox(10, list, input, row(add, remove));
    }

    private static Node virtualList() {
        var list = new Listy<String>();
        for (int i = 1; i <= 10_000; i++) list.getItems().add("事件日志 · " + i);
        list.setPrefSize(500, 230);
        var selected = new Label("当前未选择");
        list.getSelectionModel().selectedIndexProperty().addListener((observable, old, value) ->
                selected.setText(value.intValue() < 0 ? "当前未选择" : "已定位第 " + (value.intValue() + 1) + " 条"));
        return new VBox(10, list, row(
                action("定位中间", () -> { list.getSelectionModel().select(4999); list.scrollTo(4999); }),
                action("回到顶部", () -> { list.getSelectionModel().select(0); list.scrollTo(0); }), selected));
    }

    private static Node editableQr() {
        var code = new QRCode("https://github.com/normlanguage", 168);
        var input = new TextField(code.getText());
        input.setPrefWidth(370);
        var apply = action("生成二维码", () -> { if (!input.getText().isBlank()) code.setText(input.getText()); });
        return new VBox(14, row(code, new VBox(8, caption("输入网址或文本"), input, apply)),
                caption("编码内容由输入框决定"));
    }

    private static Node segmentedViews() {
        var segmented = new Segmented<>(List.of("概览", "活动", "设置"));
        segmented.setValue("概览");
        var output = new Label("概览：查看团队关键指标");
        segmented.valueProperty().addListener((observable, old, value) -> output.setText(value == null ? "请选择视图" : switch (value) {
            case "活动" -> "活动：查看最近变更";
            case "设置" -> "设置：管理工作区选项";
            default -> "概览：查看团队关键指标";
        }));
        return new VBox(16, segmented, output);
    }

    private static Node liveStatistic() {
        var counter = new Statistic("当前访问", 128);
        return new VBox(14, counter, row(
                action("+1", () -> counter.setValue(counter.getValue().intValue() + 1)),
                action("-1", () -> counter.setValue(Math.max(0, counter.getValue().intValue() - 1))),
                action("重置", () -> counter.setValue(128))));
    }

    private static Node sortableTable() {
        var rows = FXCollections.observableArrayList("设计系统", "组件图库", "主题管理", "交互测试", "发布流程");
        var table = new Table<String>();
        table.setSource(rows);
        table.column("任务", value -> value).setPrefWidth(330);
        table.column("长度", String::length).setPrefWidth(130);
        table.setPrefSize(520, 250);
        var selected = new Label("选择一项查看结果");
        table.getSelectionModel().selectedItemProperty().addListener((observable, old, value) ->
                selected.setText(value == null ? "选择一项查看结果" : "已选择：" + value));
        return new VBox(10, table, selected, caption("点击列标题可排序"));
    }

    private static Node removableTags() {
        var chips = new FlowPane(8, 8);
        chips.getStyleClass().add("gallery-demo-row");
        var labels = new ArrayList<>(List.of("JavaFX", "主题", "组件", "图库"));
        for (var label : labels) {
            var tag = new Tag(label);
            tag.setClosable(true);
            tag.addEventHandler(Tag.CLOSE, event -> chips.getChildren().remove(tag));
            chips.getChildren().add(tag);
        }
        var add = action("添加标签", () -> {
            var tag = new Tag("新标签");
            tag.setClosable(true);
            tag.addEventHandler(Tag.CLOSE, event -> chips.getChildren().remove(tag));
            chips.getChildren().add(tag);
        });
        return new VBox(14, chips, add);
    }

    private static Node activityTimeline() {
        var timeline = new dev.normlanguage.ui.component.Timeline();
        timeline.add("08:45", new Label("构建开始"));
        timeline.add("09:02", new Label("单元测试通过"));
        timeline.add("09:16", new Label("截图审查完成"));
        timeline.add("09:30", new Label("等待发布"));
        return timeline;
    }

    private static Node directoryTree() {
        var tree = new Tree<String>("项目");
        var guide = new CheckBoxTreeItem<>("指南");
        guide.getChildren().addAll(new CheckBoxTreeItem<>("快速开始"), new CheckBoxTreeItem<>("主题配置"));
        guide.setExpanded(true);
        var components = new CheckBoxTreeItem<>("组件");
        components.getChildren().addAll(new CheckBoxTreeItem<>("数据展示"), new CheckBoxTreeItem<>("数据录入"));
        components.setExpanded(true);
        tree.getRoot().getChildren().addAll(guide, components);
        tree.setPrefSize(480, 260);
        var selected = new Label("选择左侧节点");
        tree.getSelectionModel().selectedItemProperty().addListener((observable, old, item) ->
                selected.setText(item == null ? "选择左侧节点" : "当前位置：" + item.getValue()));
        return new VBox(10, tree, selected);
    }
}
