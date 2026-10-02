package dev.normlanguage.ui.component.gallery;

import dev.normlanguage.ui.component.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.Objects;

public final class DisplayExamples {
    private DisplayExamples() {}
    private record Person(SimpleStringProperty name, SimpleStringProperty role, SimpleStringProperty team) {}
    private static final String LANDSCAPE_URL = Objects.requireNonNull(
            DisplayExamples.class.getResource("images/alpine-lake.png")).toExternalForm();

    static javafx.scene.image.Image landscape() { return new javafx.scene.image.Image(LANDSCAPE_URL); }

    public static java.util.List<Gallery.Component> components(App app) {
        var examples = new java.util.ArrayList<Gallery.Component>();
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Avatar", "头像", () -> {
            var avatar = new Avatar("NL");
            var photo = new Avatar("AL");
            photo.setImage(landscape());
            var alternate = new Avatar("UI");
            var change = new dev.normlanguage.ui.component.Button("切换照片");
            change.setOnAction(event -> photo.setImage(photo.getImage() == null ? landscape() : null));
            return new HBox(16, avatar, photo, alternate, change);
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Badge", "徽标数", () -> {
            var badge = new Badge(new Avatar("收"));
            badge.setCount(3);
            var updates = new Badge(new Avatar("更"));
            updates.setCount(12);
            var add = new dev.normlanguage.ui.component.Button("收到消息");
            add.setOnAction(event -> badge.setCount(badge.getCount() + 1));
            return new HBox(18, badge, new Label("收件箱"), updates, new Label("更新"), add);
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Calendar", "日历", () -> {
            var calendar = new dev.normlanguage.ui.component.Calendar();
            calendar.setDayContentFactory(date -> switch (date.getDayOfMonth()) {
                case 5, 12, 21 -> new Label("•");
                default -> null;
            });
            var selected = new Label();
            selected.textProperty().bind(calendar.valueProperty().asString("选中日期：%s"));
            var content = new VBox(12, calendar, selected);
            content.setMaxWidth(Double.MAX_VALUE);
            return content;
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Card", "卡片", () -> {
            var card = new Card("Norm UI", new VBox(8, new Label("组件库设计系统"),
                    new Label("73 个 JavaFX 组件 · 明暗主题")));
            var rename = new dev.normlanguage.ui.component.Button("标记为已关注");
            rename.setOnAction(event -> card.setTitle("Norm UI · 已关注"));
            card.setBottom(rename);
            card.setPrefWidth(360);
            return card;
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Carousel", "走马灯", () -> {
            var photo = new ImageView(landscape());
            photo.setFitWidth(520);
            photo.setPreserveRatio(true);
            var carousel = new Carousel(java.util.List.of(
                    new VBox(8, photo, new Label("阿尔卑斯湖 · 晨光")),
                    new VBox(8, new Label("任意 JavaFX 节点"), new Label("图片、文本和卡片都可以成为幻灯片")),
                    new VBox(8, new Label("主题同步"), new Label("切换主题时保持当前位置"))));
            var position = new Label();
            position.textProperty().bind(carousel.indexProperty().add(1).asString("第 %d / 3 张"));
            var next = new dev.normlanguage.ui.component.Button("下一张");
            next.setOnAction(event -> carousel.next());
            var previous = new dev.normlanguage.ui.component.Button("上一张");
            previous.setOnAction(event -> carousel.previous());
            return new VBox(12, carousel, new HBox(12, previous, next, position));
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Collapse", "折叠面板", () -> {
            var panel = new Collapse("组件由什么组成？", new Label("原生节点 · 主题系统 · 可组合控件"));
            panel.setExpanded(true);
            return panel;
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Descriptions", "描述列表", () -> {
            var descriptions = new Descriptions();
            descriptions.add("项目", new Label("Norm UI"));
            descriptions.add("平台", new Label("JavaFX 25"));
            descriptions.add("组件", new Label("73 项"));
            descriptions.add("主题", new Label("明暗模式 · 局部作用域"));
            return descriptions;
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Empty", "空状态", () -> {
            var empty = new Empty("没有找到符合条件的项目");
            var clear = new dev.normlanguage.ui.component.Button("清除筛选");
            clear.setOnAction(event -> empty.setDescription("已清除筛选，可以重新搜索"));
            return new VBox(16, empty, clear);
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Image", "图片", () -> {
            var image = new dev.normlanguage.ui.component.Image(landscape());
            image.setPreviewable(true);
            image.getImageView().setFitWidth(540);
            image.getImageView().setFitHeight(320);
            return new VBox(10, image, new Label("阿尔卑斯湖 · 点击图片预览"));
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "List", "列表", () -> {
            var list = new dev.normlanguage.ui.component.List<String>();
            list.getItems().addAll("设计主题切换", "实现表格筛选", "检查键盘交互", "完善文档索引", "发布组件示例");
            list.getSelectionModel().select(0);
            list.setPrefSize(480, 225);
            var selected = new Label();
            selected.textProperty().bind(list.getSelectionModel().selectedItemProperty().asString("当前任务：%s"));
            return new VBox(10, list, selected);
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Listy", "虚拟列表", () -> {
            var list = new Listy<String>();
            for (int i = 1; i <= 10_000; i++) list.getItems().add("记录 " + i);
            list.setPrefSize(480, 240);
            var jump = new dev.normlanguage.ui.component.Button("定位第 5000 条");
            jump.setOnAction(event -> { list.getSelectionModel().select(4999); list.scrollTo(4999); });
            return new VBox(10, list, jump);
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "QRCode", "二维码", () ->
                new VBox(10, new QRCode("https://github.com/normlanguage/ui-component", 168),
                        new Label("扫描访问 ui-component 仓库"))));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Segmented", "分段控制器", () -> {
            var segmented = new Segmented<>(java.util.List.of("日", "周", "月"));
            segmented.setValue("周");
            var selected = new Label();
            selected.textProperty().bind(segmented.valueProperty().asString("当前维度：%s"));
            return new VBox(12, segmented, selected);
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Statistic", "统计数值", () -> {
            var downloads = new Statistic("本周下载", 1280);
            var contributors = new Statistic("贡献者", 42);
            var stars = new Statistic("Star", 318);
            return new HBox(36, downloads, contributors, stars);
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Table", "表格", () -> {
            var rows = FXCollections.observableArrayList(
                new Person(new SimpleStringProperty("Ada"), new SimpleStringProperty("Engineer"), new SimpleStringProperty("Platform")),
                new Person(new SimpleStringProperty("Lin"), new SimpleStringProperty("Designer"), new SimpleStringProperty("Design")),
                new Person(new SimpleStringProperty("Maya"), new SimpleStringProperty("Engineer"), new SimpleStringProperty("UI")),
                new Person(new SimpleStringProperty("Noah"), new SimpleStringProperty("Writer"), new SimpleStringProperty("Docs")),
                new Person(new SimpleStringProperty("Iris"), new SimpleStringProperty("Researcher"), new SimpleStringProperty("Design")),
                new Person(new SimpleStringProperty("Leo"), new SimpleStringProperty("Engineer"), new SimpleStringProperty("Platform")));
            var table = new Table<Person>();
            table.setSource(rows);
            table.editableColumn("姓名", Person::name, (person, value) -> person.name().set(value)).setPrefWidth(160);
            table.observableColumn("岗位", Person::role).setPrefWidth(180);
            table.observableColumn("团队", Person::team).setPrefWidth(180);
            var search = new TextField();
            search.setPromptText("按姓名或团队筛选");
            search.textProperty().addListener((o,a,b) -> table.setPredicate(person ->
                    person.name().get().toLowerCase().contains(b.toLowerCase())
                            || person.team().get().toLowerCase().contains(b.toLowerCase())));
            table.setPrefSize(700, 310);
            return new VBox(10, search, table, new Label("点击表头排序，双击姓名编辑"));
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Tag", "标签", () -> {
            return new HBox(10, new Tag("已就绪"), new Tag("待审核"), new Tag("需处理"));
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Timeline", "时间轴", () -> {
            var timeline = new dev.normlanguage.ui.component.Timeline();
            timeline.add("09:00", new Label("设计稿确认"));
            timeline.add("10:15", new Label("组件实现完成"));
            timeline.add("13:40", new Label("交互测试通过"));
            timeline.add("16:20", new Label("文档发布"));
            return timeline;
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Tree", "树形控件", () -> {
            var tree = new Tree<String>("工作区");
            tree.setCheckable(true);
            var src = new javafx.scene.control.CheckBoxTreeItem<String>("src");
            src.getChildren().addAll(new javafx.scene.control.CheckBoxTreeItem<>("main"),
                    new javafx.scene.control.CheckBoxTreeItem<>("test"));
            src.setExpanded(true);
            tree.getRoot().getChildren().addAll(src,
                    new javafx.scene.control.CheckBoxTreeItem<>("docs"),
                    new javafx.scene.control.CheckBoxTreeItem<>("resources"));
            tree.setPrefSize(480, 260);
            return tree;
        }));
        return java.util.List.copyOf(examples);
    }

}
