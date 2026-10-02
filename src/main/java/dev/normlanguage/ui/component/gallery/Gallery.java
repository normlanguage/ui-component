package dev.normlanguage.ui.component.gallery;

import dev.normlanguage.ui.component.*;
import javafx.application.Platform;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;

public final class Gallery {
    private Gallery() {}
    public enum Category {
        GENERAL("通用"), LAYOUT("布局"), NAVIGATION("导航"), INPUT("数据录入"),
        DISPLAY("数据展示"), FEEDBACK("反馈"), OTHER("其他");
        private final String chinese;
        Category(String chinese) { this.chinese = chinese; }
        public String chinese() { return chinese; }
    }
    public record Component(Category category, String name, String chinese, Supplier<Node> factory) {}
    public record Palette(String name, String light, String dark) {
        public Palette {
            java.util.Objects.requireNonNull(name);
            java.util.Objects.requireNonNull(light);
            java.util.Objects.requireNonNull(dark);
            if (name.isBlank()) throw new IllegalArgumentException("Palette name must not be blank");
        }
    }
    public static GalleryView createView(App app, java.util.List<Palette> palettes) {
        return new GalleryView(app, palettes, components(app));
    }
    public static void saveThemes(String light, String dark) throws java.io.IOException {
        var directory = java.nio.file.Path.of("build", "themes");
        java.nio.file.Files.createDirectories(directory);
        java.nio.file.Files.writeString(directory.resolve("light.css"), light);
        java.nio.file.Files.writeString(directory.resolve("dark.css"), dark);
    }
    public static java.util.List<Component> components(App app) {
        var demos = new java.util.ArrayList<Component>();
        demos.addAll(NavigationExamples.components(app));
        demos.addAll(InputExamples.components(app));
        demos.addAll(DisplayExamples.components(app));
        demos.add(new Gallery.Component(Gallery.Category.GENERAL, "Button", "按钮", () -> {
            var button = new dev.normlanguage.ui.component.Button("Save changes");
            button.setOnAction(event -> app.getMessages().success("Saved"));
            return button;
        }));
        demos.add(new Gallery.Component(Gallery.Category.DISPLAY, "Popover", "气泡卡片", () -> {
            var trigger = new dev.normlanguage.ui.component.Button("Open details");
            var popup = new Popover(trigger, new Label("This content follows the owner's theme."));
            trigger.setOnAction(event -> popup.show());
            return trigger;
        }));
        demos.add(new Gallery.Component(Gallery.Category.DISPLAY, "Tooltip", "文字提示", () -> {
            var target = new dev.normlanguage.ui.component.Button("Hover or focus here");
            new dev.normlanguage.ui.component.Tooltip(target, "Keyboard focus also shows this hint.");
            return target;
        }));
        demos.add(new Gallery.Component(Gallery.Category.DISPLAY, "Tour", "漫游式引导", () -> {
            var first = new dev.normlanguage.ui.component.Button("First step");
            var second = new dev.normlanguage.ui.component.Button("Second step");
            var tour = new Tour(java.util.List.of(new Tour.Step(first, "Welcome", "Start here"), new Tour.Step(second, "Next", "Then continue here")));
            first.setOnAction(event -> tour.start());
            return new HBox(12, first, second);
        }));
        demos.add(new Gallery.Component(Gallery.Category.FEEDBACK, "Alert", "警告提示", () -> new Alert("Information", new Label("This notice can be dismissed."))));
        demos.add(new Gallery.Component(Gallery.Category.FEEDBACK, "Drawer", "抽屉", () -> {
            var trigger = new dev.normlanguage.ui.component.Button("Open drawer");
            var drawer = new Drawer(trigger, new VBox(12, new Label("Preferences"), new Input("Editable content")), Side.RIGHT);
            trigger.setOnAction(event -> drawer.show());
            return trigger;
        }));
        demos.add(new Gallery.Component(Gallery.Category.FEEDBACK, "Message", "全局提示", () -> {
            var trigger = new dev.normlanguage.ui.component.Button("Show message");
            trigger.setOnAction(event -> app.getMessages().success("Your changes have been saved."));
            return trigger;
        }));
        demos.add(new Gallery.Component(Gallery.Category.FEEDBACK, "Modal", "对话框", () -> {
            var trigger = new dev.normlanguage.ui.component.Button("Open dialog");
            var modal = new Modal(trigger, "Edit profile", new VBox(12, new Label("Display name"), new Input("Norm")));
            trigger.setOnAction(event -> modal.show());
            return trigger;
        }));
        demos.add(new Gallery.Component(Gallery.Category.FEEDBACK, "Notification", "通知提醒框", () -> {
            var trigger = new dev.normlanguage.ui.component.Button("Show notification");
            trigger.setOnAction(event -> app.getNotifications().show("Build complete", new Label("All changes are ready.")));
            return trigger;
        }));
        demos.add(new Gallery.Component(Gallery.Category.FEEDBACK, "Popconfirm", "气泡确认框", () -> {
            var trigger = new dev.normlanguage.ui.component.Button("Delete item");
            var confirmation = new Popconfirm(trigger, "Delete this item?", () -> app.getMessages().success("Item deleted"));
            trigger.setOnAction(event -> confirmation.show());
            return trigger;
        }));
        demos.add(new Gallery.Component(Gallery.Category.FEEDBACK, "Progress", "进度条", () -> new Progress(0.64)));
        demos.add(new Gallery.Component(Gallery.Category.FEEDBACK, "Result", "结果", () -> new Result("Ready", "Your workspace is up to date", new dev.normlanguage.ui.component.Button("Continue"))));
        demos.add(new Gallery.Component(Gallery.Category.FEEDBACK, "Skeleton", "骨架屏", () -> new Skeleton(4)));
        demos.add(new Gallery.Component(Gallery.Category.FEEDBACK, "Spin", "加载中", () -> {
            var spin = new Spin(new Label("Data loading"));
            spin.setSpinning(true); spin.setMinHeight(140);
            var toggle = new dev.normlanguage.ui.component.Button("Toggle loading");
            toggle.setOnAction(event -> spin.setSpinning(!spin.isSpinning()));
            return new VBox(12, spin, toggle);
        }));
        demos.add(new Gallery.Component(Gallery.Category.FEEDBACK, "Watermark", "水印", () -> {
            var mark = new Watermark(new Input("Content remains interactive"), "Norm workspace");
            mark.setMinHeight(240); return mark;
        }));
        demos.add(new Gallery.Component(Gallery.Category.OTHER, "Affix", "固钉", () -> {
            var scroll = new ScrollPane();
            var fixed = new Affix(scroll, new Label("Pinned while scrolling"));
            var body = new VBox(20, new Label("Scroll down"), fixed);
            for (int i = 0; i < 20; i++) body.getChildren().add(new Label("Section " + (i + 1)));
            scroll.setContent(body); scroll.setPrefHeight(300);
            return scroll;
        }));
        demos.add(new Gallery.Component(Gallery.Category.OTHER, "App", "包裹组件", () -> {
            var action = new dev.normlanguage.ui.component.Button("Show root message");
            action.setOnAction(event -> Util.app(action).getMessages().success("Owned by this App"));
            return new VBox(12, new Label("Application root"), action);
        }));
        demos.add(new Gallery.Component(Gallery.Category.OTHER, "BorderBeam", "边框流光", () -> {
            var beam = new BorderBeam(new Label("Animated border"));
            beam.setMinSize(240, 140); return beam;
        }));
        demos.add(new Gallery.Component(Gallery.Category.OTHER, "ConfigProvider", "全局化配置", () -> {
            var scope = new ConfigProvider();
            var density = new dev.normlanguage.ui.component.Button("Toggle local density");
            var content = new VBox(12, new Input("Local content"), density);
            density.setOnAction(event -> {
                var current = scope.getEffectiveConfig();
                var next = current.density() == ComponentConfig.Density.COMPACT
                        ? ComponentConfig.Density.SPACIOUS : ComponentConfig.Density.COMPACT;
                scope.setConfig(new ComponentConfig(current.fontFamily(), current.fontSize(), next,
                        current.radius(), current.motionEnabled(), current.locale()));
            });
            scope.setContent(content);
            return scope;
        }));
        demos.add(new Gallery.Component(Gallery.Category.OTHER, "Util", "工具类", () -> {
            var check = new dev.normlanguage.ui.component.Button("Find owning root");
            check.setOnAction(event -> Util.app(check).getMessages().success("Root found")); return check;
        }));
        return ComponentCatalog.validate(demos);
    }

    public static Map<String, Supplier<Node>> examples(App app) {
        var examples = new LinkedHashMap<String, Supplier<Node>>();
        for (var component : components(app)) examples.put(component.name(), component.factory());
        return java.util.Collections.unmodifiableMap(examples);
    }

    public static void open(java.util.List<Palette> palettes) throws InterruptedException {
        var finished = new CountDownLatch(1);
        Platform.startup(() -> {
            var app = new App();
            app.setContent(createView(app, palettes));
            var stage = new Stage(); stage.setTitle("Norm UI Component");
            stage.setScene(new Scene(app, 1120, 780));
            stage.setOnHidden(event -> {
                app.close(); finished.countDown(); Platform.exit();
            });
            stage.show();
        });
        finished.await();
    }

}
