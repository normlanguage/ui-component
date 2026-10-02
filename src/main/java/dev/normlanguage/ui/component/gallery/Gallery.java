package dev.normlanguage.ui.component.gallery;

import dev.normlanguage.ui.component.*;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
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
    public static ComponentConfig defaultConfig() {
        var defaults = ComponentConfig.defaults();
        String family = javafx.scene.text.Font.getFamilies().contains("Microsoft YaHei UI") ? "Microsoft YaHei UI" : defaults.fontFamily();
        return new ComponentConfig(family, 18, defaults.density(), defaults.radius(), defaults.motionEnabled(), defaults.locale());
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
        demos.addAll(FeedbackExamples.components(app));
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
            app.setConfig(defaultConfig());
            var view = createView(app, palettes);
            view.selectComponent("Button");
            app.setContent(view);
            var stage = new Stage(); stage.setTitle("Norm UI Component");
            stage.setScene(new Scene(app, 1440, 1024));
            stage.setMinWidth(900);
            stage.setMinHeight(640);
            stage.setOnHidden(event -> {
                app.close(); finished.countDown(); Platform.exit();
            });
            stage.show();
        });
        finished.await();
    }

}
