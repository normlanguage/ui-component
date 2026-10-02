package dev.normlanguage.ui.component.gallery;

import dev.normlanguage.ui.component.App;
import dev.normlanguage.ui.component.Button;
import dev.normlanguage.ui.component.ComponentConfig;
import dev.normlanguage.ui.component.ConfigProvider;
import dev.normlanguage.ui.component.Icon;
import dev.normlanguage.ui.component.Util;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Control;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.geometry.Pos;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class GalleryExamples {
    private GalleryExamples() {}

    public record Section(String id, String title, Node content, boolean fullWidth) {
        public Section {
            Objects.requireNonNull(id);
            Objects.requireNonNull(title);
            Objects.requireNonNull(content);
        }
    }

    public static List<Section> create(Gallery.Component component, App app) {
        Util.requireFxThread();
        Objects.requireNonNull(component);
        Objects.requireNonNull(app);
        var sections = component.name().equals("Button") ? buttonSections(component, app) : basicSections(component);
        var stylesheet = Objects.requireNonNull(GalleryExamples.class.getResource("examples.css")).toExternalForm();
        for (var section : sections) {
            if (section.content() instanceof FlowPane flow) flow.prefWrapLengthProperty().bind(flow.widthProperty());
            if (section.content() instanceof Parent parent) parent.getStylesheets().add(stylesheet);
        }
        return List.copyOf(sections);
    }

    private static List<Section> basicSections(Gallery.Component component) {
        var demo = component.factory().get();
        demo.setId("gallery-example");
        var sections = new ArrayList<Section>();
        sections.add(new Section("basic", "基础用法", demo, true));
        if (demo instanceof Control) {
            var disabled = component.factory().get();
            if (disabled instanceof Control control) {
                control.setDisable(true);
                sections.add(new Section("disabled", "禁用状态", control, true));
            } else Util.closeTree(disabled);
        }
        return sections;
    }

    private static List<Section> buttonSections(Gallery.Component component, App app) {
        var primary = (Button) component.factory().get();
        primary.setText("主要按钮");
        primary.setId("gallery-example");
        var basic = new FlowPane(12, 12, primary,
                new javafx.scene.control.Button("默认按钮"),
                button("虚线按钮", app, "outlined", "gallery-example-dashed"),
                button("文字按钮", app, "text"),
                button("链接按钮", app, "text", "gallery-example-link"));
        basic.setAlignment(Pos.CENTER_LEFT);
        basic.getStyleClass().add("gallery-example-row");

        var config = app.getEffectiveConfig();
        var sizes = new FlowPane(12, 12,
                sizedButton("大号按钮", app, config, ComponentConfig.Density.SPACIOUS),
                sizedButton("标准按钮", app, config, ComponentConfig.Density.STANDARD),
                sizedButton("小号按钮", app, config, ComponentConfig.Density.COMPACT));
        sizes.setAlignment(Pos.CENTER_LEFT);
        sizes.getStyleClass().add("gallery-example-row");

        var loading = button("加载中", app);
        loading.setId("gallery-loading-button");
        var indicator = new ProgressIndicator();
        indicator.setPrefSize(16, 16);
        indicator.setMinSize(16, 16);
        indicator.setMaxSize(16, 16);
        loading.setGraphic(indicator);
        loading.setLoading(true);
        var disabled = button("禁用状态", app);
        disabled.setDisable(true);
        var states = new FlowPane(12, 12, button("正常状态", app), loading, disabled);
        states.setAlignment(Pos.CENTER_LEFT);
        states.getStyleClass().add("gallery-example-row");

        var search = iconButton("搜索", "mdi2m-magnify", app);
        search.setId("gallery-icon-search");
        var icons = new FlowPane(12, 12, search,
                iconButton("设置", "mdi2c-cog", app),
                iconButton("新建", "mdi2p-plus", app),
                iconButton("删除", "mdi2d-delete", app));
        icons.setAlignment(Pos.CENTER_LEFT);
        icons.getStyleClass().add("gallery-example-row");

        var danger = button("危险按钮", app, "danger");
        danger.setId("gallery-danger-button");
        var dangerOutlined = button("删除", app, "danger", "outlined", "gallery-danger-outlined");
        dangerOutlined.setId("gallery-danger-outlined");
        var dangerText = button("文字删除", app, "danger", "text", "gallery-danger-text");
        dangerText.setId("gallery-danger-text");
        var dangerous = new FlowPane(12, 12, danger, dangerOutlined, dangerText);
        dangerous.setAlignment(Pos.CENTER_LEFT);
        dangerous.getStyleClass().add("gallery-example-row");

        var source = "import dev.normlanguage.ui.component.Button;\n\n"
                + "Button button = new Button(\"Save changes\");\n"
                + "button.setOnAction(event -> System.out.println(\"Saved\"));";
        var code = new TextArea(source);
        code.setId("gallery-code");
        code.setEditable(false);
        code.setWrapText(false);
        code.setPrefRowCount(4);
        var copy = new javafx.scene.control.Button("复制代码");
        copy.setId("gallery-copy");
        copy.setGraphic(new Icon("mdi2c-content-copy"));
        copy.setOnAction(event -> {
            var content = new ClipboardContent();
            content.putString(source);
            Clipboard.getSystemClipboard().setContent(content);
        });
        var spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        var codeHeader = new HBox(12, new javafx.scene.control.Label("Java"), spacer, copy);
        codeHeader.getStyleClass().add("gallery-code-header");
        var usage = new VBox(codeHeader, code);
        usage.getStyleClass().add("gallery-code-panel");

        return List.of(
                new Section("types", "基础类型", basic, true),
                new Section("sizes", "尺寸", sizes, false),
                new Section("states", "状态", states, false),
                new Section("icons", "图标按钮", icons, false),
                new Section("danger", "危险操作", dangerous, false),
                new Section("usage", "使用示例", usage, true));
    }

    private static Button button(String label, App app, String... styles) {
        var button = new Button(label);
        button.getStyleClass().addAll(styles);
        button.setOnAction(event -> app.getMessages().success(label));
        return button;
    }

    private static Button iconButton(String label, String literal, App app) {
        var button = button(label, app, "outlined");
        button.setGraphic(new Icon(literal));
        return button;
    }

    private static ConfigProvider sizedButton(String label, App app, ComponentConfig config,
                                               ComponentConfig.Density density) {
        var scope = new ConfigProvider(button(label, app)) {
            @Override public void close() {
                themeCssProperty().unbind();
                super.close();
            }
        };
        scope.setId("gallery-size-" + density.name().toLowerCase(java.util.Locale.ROOT));
        scope.themeCssProperty().bind(app.themeCssProperty());
        scope.setConfig(new ComponentConfig(config.fontFamily(), config.fontSize(), density,
                config.radius(), config.motionEnabled(), config.locale()));
        scope.setMaxWidth(Region.USE_PREF_SIZE);
        return scope;
    }
}
