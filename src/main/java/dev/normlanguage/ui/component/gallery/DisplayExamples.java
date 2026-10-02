package dev.normlanguage.ui.component.gallery;

import dev.normlanguage.ui.component.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class DisplayExamples {
    private DisplayExamples() {}
    private record Person(SimpleStringProperty name, SimpleStringProperty role) {}

    public static java.util.List<Gallery.Component> components(App app) {
        var examples = new java.util.ArrayList<Gallery.Component>();
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Avatar", "头像", () -> {
            var avatar = new Avatar("NL");
            var change = new dev.normlanguage.ui.component.Button("Change image");
            change.setOnAction(event -> avatar.setImage(avatar.getImage() == null ? sampleImage() : null));
            return new HBox(12, avatar, change);
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Badge", "徽标数", () -> {
            var badge = new Badge(new Label("Inbox"));
            badge.setCount(3);
            var add = new dev.normlanguage.ui.component.Button("Add");
            add.setOnAction(event -> badge.setCount(badge.getCount() + 1));
            return new HBox(12, badge, add);
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Calendar", "日历", () -> {
            var calendar = new dev.normlanguage.ui.component.Calendar();
            calendar.setDayContentFactory(date -> date.getDayOfMonth() % 5 == 0 ? new Label("•") : null);
            var selected = new Label();
            selected.textProperty().bind(calendar.valueProperty().asString());
            return new VBox(8, calendar, selected);
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Card", "卡片", () -> {
            var card = new Card("Project", new Label("Reusable JavaFX controls"));
            var rename = new dev.normlanguage.ui.component.Button("Rename");
            rename.setOnAction(event -> card.setTitle("Norm UI"));
            card.setBottom(rename);
            return card;
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Carousel", "走马灯", () -> {
            var carousel = new Carousel(java.util.List.of(new Label("First slide"), new Label("Second slide"), new Label("Third slide")));
            var next = new dev.normlanguage.ui.component.Button("Next");
            next.setOnAction(event -> carousel.next());
            return new VBox(8, carousel, next);
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Collapse", "折叠面板", () -> new Collapse("Details", new Label("Expanded content"))));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Descriptions", "描述列表", () -> {
            var descriptions = new Descriptions();
            descriptions.add("Name", new Label("Norm"));
            descriptions.add("Backend", new Label("JavaFX"));
            return descriptions;
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Empty", "空状态", () -> new Empty("No results")));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Image", "图片", () -> {
            var image = new dev.normlanguage.ui.component.Image(sampleImage());
            image.setPreviewable(true);
            image.getImageView().setFitWidth(160);
            image.getImageView().setFitHeight(90);
            return image;
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "List", "列表", () -> {
            var list = new dev.normlanguage.ui.component.List<String>();
            list.getItems().addAll("Alpha", "Beta", "Gamma");
            list.setPrefHeight(130);
            return list;
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Listy", "虚拟列表", () -> {
            var list = new Listy<Integer>();
            for (int i = 0; i < 10_000; i++) list.getItems().add(i);
            list.setPrefHeight(180);
            return list;
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "QRCode", "二维码", () -> new QRCode("https://normlanguage.dev", 160)));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Segmented", "分段控制器", () -> {
            var segmented = new Segmented<>(java.util.List.of("Day", "Week", "Month"));
            segmented.setValue("Week");
            return segmented;
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Statistic", "统计数值", () -> {
            var statistic = new Statistic("Downloads", 1280);
            var add = new dev.normlanguage.ui.component.Button("+1");
            add.setOnAction(event -> statistic.setValue(statistic.getValue().longValue() + 1));
            return new VBox(8, statistic, add);
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Table", "表格", () -> {
            var rows = FXCollections.observableArrayList(
                new Person(new SimpleStringProperty("Ada"), new SimpleStringProperty("Engineer")),
                new Person(new SimpleStringProperty("Lin"), new SimpleStringProperty("Designer")));
            var table = new Table<Person>();
            table.setSource(rows);
            table.editableColumn("Name", Person::name, (person, value) -> person.name().set(value));
            table.observableColumn("Role", Person::role);
            var search = new TextField();
            search.setPromptText("Filter names");
            search.textProperty().addListener((o,a,b) -> table.setPredicate(person -> person.name().get().toLowerCase().contains(b.toLowerCase())));
            table.setPrefHeight(170);
            return new VBox(8, search, table);
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Tag", "标签", () -> {
            var tag = new Tag("Closable");
            tag.setClosable(true);
            tag.addEventHandler(Tag.CLOSE, event -> tag.setVisible(false));
            return tag;
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Timeline", "时间轴", () -> {
            var timeline = new dev.normlanguage.ui.component.Timeline();
            timeline.add("09:00", new Label("Created"));
            timeline.add("10:00", new Label("Published"));
            return timeline;
        }));
        examples.add(new Gallery.Component(Gallery.Category.DISPLAY, "Tree", "树形控件", () -> {
            var tree = new Tree<String>("Root");
            tree.setCheckable(true);
            tree.getRoot().getChildren().add(new javafx.scene.control.CheckBoxTreeItem<>("First"));
            tree.getRoot().getChildren().add(new javafx.scene.control.CheckBoxTreeItem<>("Second"));
            tree.setPrefHeight(140);
            return tree;
        }));
        return java.util.List.copyOf(examples);
    }

    private static WritableImage sampleImage() {
        var image = new WritableImage(160, 90);
        var writer = image.getPixelWriter();
        for (int y = 0; y < 90; y++) for (int x = 0; x < 160; x++)
            writer.setArgb(x, y, x < 80 ? 0xff6750a4 : 0xff7ec9b2);
        return image;
    }
}
