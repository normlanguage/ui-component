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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public final class DisplayExamples {
    private DisplayExamples() {}
    private record Person(SimpleStringProperty name, SimpleStringProperty role) {}

    public static Map<String, Supplier<Node>> examples(App app) {
        var examples = new LinkedHashMap<String, Supplier<Node>>();
        examples.put("Avatar", () -> {
            var avatar = new Avatar("NL");
            var change = new dev.normlanguage.ui.component.Button("Change image");
            change.setOnAction(event -> avatar.setImage(avatar.getImage() == null ? sampleImage() : null));
            return new HBox(12, avatar, change);
        });
        examples.put("Badge", () -> {
            var badge = new Badge(new Label("Inbox"));
            badge.setCount(3);
            var add = new dev.normlanguage.ui.component.Button("Add");
            add.setOnAction(event -> badge.setCount(badge.getCount() + 1));
            return new HBox(12, badge, add);
        });
        examples.put("Calendar", () -> {
            var calendar = new dev.normlanguage.ui.component.Calendar();
            calendar.setDayContentFactory(date -> date.getDayOfMonth() % 5 == 0 ? new Label("•") : null);
            var selected = new Label();
            selected.textProperty().bind(calendar.valueProperty().asString());
            return new VBox(8, calendar, selected);
        });
        examples.put("Card", () -> {
            var card = new Card("Project", new Label("Reusable JavaFX controls"));
            var rename = new dev.normlanguage.ui.component.Button("Rename");
            rename.setOnAction(event -> card.setTitle("Norm UI"));
            card.setBottom(rename);
            return card;
        });
        examples.put("Carousel", () -> {
            var carousel = new Carousel(java.util.List.of(new Label("First slide"), new Label("Second slide"), new Label("Third slide")));
            var next = new dev.normlanguage.ui.component.Button("Next");
            next.setOnAction(event -> carousel.next());
            return new VBox(8, carousel, next);
        });
        examples.put("Collapse", () -> new Collapse("Details", new Label("Expanded content")));
        examples.put("Descriptions", () -> {
            var descriptions = new Descriptions();
            descriptions.add("Name", new Label("Norm"));
            descriptions.add("Backend", new Label("JavaFX"));
            return descriptions;
        });
        examples.put("Empty", () -> new Empty("No results"));
        examples.put("Image", () -> {
            var image = new dev.normlanguage.ui.component.Image(sampleImage());
            image.setPreviewable(true);
            image.getImageView().setFitWidth(160);
            image.getImageView().setFitHeight(90);
            return image;
        });
        examples.put("List", () -> {
            var list = new dev.normlanguage.ui.component.List<String>();
            list.getItems().addAll("Alpha", "Beta", "Gamma");
            list.setPrefHeight(130);
            return list;
        });
        examples.put("Listy", () -> {
            var list = new Listy<Integer>();
            for (int i = 0; i < 10_000; i++) list.getItems().add(i);
            list.setPrefHeight(180);
            return list;
        });
        examples.put("QRCode", () -> new QRCode("https://normlanguage.dev", 160));
        examples.put("Segmented", () -> {
            var segmented = new Segmented<>(java.util.List.of("Day", "Week", "Month"));
            segmented.setValue("Week");
            return segmented;
        });
        examples.put("Statistic", () -> {
            var statistic = new Statistic("Downloads", 1280);
            var add = new dev.normlanguage.ui.component.Button("+1");
            add.setOnAction(event -> statistic.setValue(statistic.getValue().longValue() + 1));
            return new VBox(8, statistic, add);
        });
        examples.put("Table", () -> {
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
        });
        examples.put("Tag", () -> {
            var tag = new Tag("Closable");
            tag.setClosable(true);
            tag.addEventHandler(Tag.CLOSE, event -> tag.setVisible(false));
            return tag;
        });
        examples.put("Timeline", () -> {
            var timeline = new dev.normlanguage.ui.component.Timeline();
            timeline.add("09:00", new Label("Created"));
            timeline.add("10:00", new Label("Published"));
            return timeline;
        });
        examples.put("Tree", () -> {
            var tree = new Tree<String>("Root");
            tree.setCheckable(true);
            tree.getRoot().getChildren().add(new javafx.scene.control.CheckBoxTreeItem<>("First"));
            tree.getRoot().getChildren().add(new javafx.scene.control.CheckBoxTreeItem<>("Second"));
            tree.setPrefHeight(140);
            return tree;
        });
        return examples;
    }

    private static WritableImage sampleImage() {
        var image = new WritableImage(160, 90);
        var writer = image.getPixelWriter();
        for (int y = 0; y < 90; y++) for (int x = 0; x < 160; x++)
            writer.setArgb(x, y, x < 80 ? 0xff6750a4 : 0xff7ec9b2);
        return image;
    }
}
