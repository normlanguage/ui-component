package dev.normlanguage.ui.component.gallery;

import dev.normlanguage.ui.component.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

public final class Gallery {
    private Gallery() {}
    public static void saveThemes(String light, String dark) throws java.io.IOException {
        var directory = java.nio.file.Path.of("build", "themes");
        java.nio.file.Files.createDirectories(directory);
        java.nio.file.Files.writeString(directory.resolve("light.css"), light);
        java.nio.file.Files.writeString(directory.resolve("dark.css"), dark);
    }
    public static Map<String, Supplier<Node>> examples(App app) {
        var demos = new LinkedHashMap<String, Supplier<Node>>();
        demos.putAll(NavigationExamples.examples(app));
        demos.putAll(InputExamples.examples(app));
        demos.putAll(DisplayExamples.examples(app));
        demos.put("Button", () -> {
            var button = new dev.normlanguage.ui.component.Button("Save changes");
            button.setOnAction(event -> app.getMessages().success("Saved"));
            return button;
        });
        demos.put("Popover", () -> {
            var trigger = new dev.normlanguage.ui.component.Button("Open details");
            var popup = new Popover(trigger, new Label("This content follows the owner's theme."));
            trigger.setOnAction(event -> popup.show());
            return trigger;
        });
        demos.put("Tooltip", () -> {
            var target = new dev.normlanguage.ui.component.Button("Hover or focus here");
            app.own(new dev.normlanguage.ui.component.Tooltip(target, "Keyboard focus also shows this hint."));
            return target;
        });
        demos.put("Tour", () -> {
            var first = new dev.normlanguage.ui.component.Button("First step");
            var second = new dev.normlanguage.ui.component.Button("Second step");
            var tour = new Tour(java.util.List.of(new Tour.Step(first, "Welcome", "Start here"), new Tour.Step(second, "Next", "Then continue here")));
            first.setOnAction(event -> tour.start());
            return new HBox(12, first, second);
        });
        demos.put("Alert", () -> new Alert("Information", new Label("This notice can be dismissed.")));
        demos.put("Drawer", () -> {
            var trigger = new dev.normlanguage.ui.component.Button("Open drawer");
            var drawer = new Drawer(trigger, new VBox(12, new Label("Preferences"), new Input("Editable content")), Side.RIGHT);
            trigger.setOnAction(event -> drawer.show());
            return trigger;
        });
        demos.put("Message", () -> {
            var trigger = new dev.normlanguage.ui.component.Button("Show message");
            trigger.setOnAction(event -> app.getMessages().success("Your changes have been saved."));
            return trigger;
        });
        demos.put("Modal", () -> {
            var trigger = new dev.normlanguage.ui.component.Button("Open dialog");
            var modal = new Modal(trigger, "Edit profile", new VBox(12, new Label("Display name"), new Input("Norm")));
            trigger.setOnAction(event -> modal.show());
            return trigger;
        });
        demos.put("Notification", () -> {
            var trigger = new dev.normlanguage.ui.component.Button("Show notification");
            trigger.setOnAction(event -> app.getNotifications().show("Build complete", new Label("All changes are ready.")));
            return trigger;
        });
        demos.put("Popconfirm", () -> {
            var trigger = new dev.normlanguage.ui.component.Button("Delete item");
            var confirmation = new Popconfirm(trigger, "Delete this item?", () -> app.getMessages().success("Item deleted"));
            trigger.setOnAction(event -> confirmation.show());
            return trigger;
        });
        demos.put("Progress", () -> new Progress(0.64));
        demos.put("Result", () -> new Result("Ready", "Your workspace is up to date", new dev.normlanguage.ui.component.Button("Continue")));
        demos.put("Skeleton", () -> new Skeleton(4));
        demos.put("Spin", () -> {
            var spin = new Spin(new Label("Data loading"));
            spin.setSpinning(true); spin.setMinHeight(140);
            var toggle = new dev.normlanguage.ui.component.Button("Toggle loading");
            toggle.setOnAction(event -> spin.setSpinning(!spin.isSpinning()));
            return new VBox(12, spin, toggle);
        });
        demos.put("Watermark", () -> {
            var mark = new Watermark(new Input("Content remains interactive"), "Norm workspace");
            mark.setMinHeight(240); return mark;
        });
        demos.put("Affix", () -> {
            var scroll = new ScrollPane();
            var fixed = new Affix(scroll, new Label("Pinned while scrolling"));
            var body = new VBox(20, new Label("Scroll down"), fixed);
            for (int i = 0; i < 20; i++) body.getChildren().add(new Label("Section " + (i + 1)));
            scroll.setContent(body); scroll.setPrefHeight(300);
            app.own(fixed); return scroll;
        });
        demos.put("App", () -> new Label("This gallery is mounted inside an App. Messages and dialogs belong to this root."));
        demos.put("BorderBeam", () -> {
            var beam = new BorderBeam(new Label("Animated border"));
            beam.setMinSize(240, 140); app.own(beam); return beam;
        });
        demos.put("ConfigProvider", () -> new ConfigProvider(new VBox(12, new Label("Scoped configuration"), new Input("Local content"))));
        demos.put("Util", () -> {
            var check = new dev.normlanguage.ui.component.Button("Find owning root");
            check.setOnAction(event -> Util.app(check).getMessages().success("Root found")); return check;
        });
        return java.util.Collections.unmodifiableMap(demos);
    }

    public static void open(String light, String dark) throws InterruptedException {
        var finished = new CountDownLatch(1);
        Platform.startup(() -> {
            var app = new App();
            app.setThemeCss(light);
            var demos = examples(app);
            var names = FXCollections.observableArrayList(demos.keySet().stream().sorted().toList());
            var filtered = new FilteredList<>(names);
            var navigation = new ListView<>(filtered);
            var search = new Input(); search.setPromptText("Find a component");
            search.textProperty().addListener((observable, previous, value) -> filtered.setPredicate(name -> name.toLowerCase(java.util.Locale.ROOT).contains(value.toLowerCase(java.util.Locale.ROOT))));
            var sidebar = new VBox(12, new Label("COMPONENTS · " + demos.size()), search, navigation);
            sidebar.setPrefWidth(230);
            VBox.setVgrow(navigation, javafx.scene.layout.Priority.ALWAYS);
            var title = new Label("Norm UI Component");
            title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
            var preview = new StackPane(); preview.setPadding(new Insets(32));
            var heading = new Label(); heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
            var toggle = new dev.normlanguage.ui.component.Button("Switch theme");
            var darkMode = new javafx.beans.property.SimpleBooleanProperty();
            toggle.setOnAction(event -> { darkMode.set(!darkMode.get()); app.setThemeCss(darkMode.get() ? dark : light); });
            var page = new VBox(24, new HBox(24, title, toggle), heading, preview);
            page.setPadding(new Insets(24));
            var layout = new BorderPane(page); layout.setLeft(sidebar);
            layout.setPadding(new Insets(20));
            app.setContent(layout);
            navigation.getSelectionModel().selectedItemProperty().addListener((observable, previous, selected) -> {
                if (selected == null) return;
                Node old = preview.getChildren().isEmpty() ? null : preview.getChildren().getFirst();
                preview.getChildren().clear();
                closeTree(old);
                heading.setText(selected);
                preview.getChildren().setAll(demos.get(selected).get());
            });
            navigation.getSelectionModel().select("Button");
            var stage = new Stage(); stage.setTitle("Norm UI Component");
            stage.setScene(new Scene(app, 1120, 780));
            stage.setOnHidden(event -> {
                closeTree(preview); app.close(); finished.countDown(); Platform.exit();
            });
            stage.show();
        });
        finished.await();
    }

    public static void closeTree(Node node) {
        Util.closeTree(node);
    }
}
