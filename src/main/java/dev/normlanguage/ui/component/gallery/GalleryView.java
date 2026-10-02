package dev.normlanguage.ui.component.gallery;

import dev.normlanguage.ui.component.App;
import dev.normlanguage.ui.component.ComponentConfig;
import dev.normlanguage.ui.component.Input;
import dev.normlanguage.ui.component.Util;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class GalleryView extends BorderPane implements AutoCloseable {
    private final App app;
    private final List<Gallery.Palette> palettes;
    private final List<Gallery.Component> catalog;
    private final VBox navigation = new VBox(4);
    private final ScrollPane content = new ScrollPane();
    private final Input search = new Input();
    private final ChoiceBox<String> paletteChoice = new ChoiceBox<>();
    private final ChoiceBox<ComponentConfig.Density> densityChoice = new ChoiceBox<>();
    private final ToggleButton darkToggle = new ToggleButton("深色");
    private final Label title = new Label();
    private Gallery.Palette palette;
    private Gallery.Component selection;
    private String query = "";
    private final List<Node> activeExamples = new ArrayList<>();
    private boolean closed;

    GalleryView(App app, List<Gallery.Palette> palettes, List<Gallery.Component> catalog) {
        this.app = Objects.requireNonNull(app);
        this.palettes = List.copyOf(palettes);
        this.catalog = List.copyOf(catalog);
        if (this.palettes.isEmpty()) throw new IllegalArgumentException("At least one palette is required");
        if (this.palettes.stream().map(Gallery.Palette::name).distinct().count() != this.palettes.size())
            throw new IllegalArgumentException("Palette names must be unique");
        palette = this.palettes.getFirst();
        getStyleClass().add("gallery-root");
        getStylesheets().add(Objects.requireNonNull(getClass().getResource("gallery.css")).toExternalForm());
        setId("gallery-browser");
        setLeft(sidebar());
        setTop(toolbar());
        content.setFitToWidth(true);
        content.setId("gallery-scroll");
        content.getStyleClass().add("gallery-scroll");
        setCenter(content);
        paletteChoice.setId("gallery-palette");
        densityChoice.setId("gallery-density");
        darkToggle.setId("gallery-dark-toggle");
        paletteChoice.getItems().setAll(this.palettes.stream().map(Gallery.Palette::name).toList());
        paletteChoice.getSelectionModel().selectFirst();
        paletteChoice.getSelectionModel().selectedItemProperty().addListener((observable, before, current) -> {
            if (current != null) selectPalette(current);
        });
        densityChoice.getItems().setAll(ComponentConfig.Density.values());
        densityChoice.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(ComponentConfig.Density value) {
                return switch (value) {
                    case COMPACT -> "紧凑";
                    case STANDARD -> "标准";
                    case SPACIOUS -> "宽松";
                };
            }
            @Override public ComponentConfig.Density fromString(String value) {
                return densityChoice.getItems().stream().filter(item -> toString(item).equals(value)).findFirst().orElseThrow();
            }
        });
        densityChoice.getSelectionModel().select(app.getEffectiveConfig().density());
        densityChoice.getSelectionModel().selectedItemProperty().addListener((observable, before, current) -> {
            if (current != null) setDensity(current);
        });
        darkToggle.selectedProperty().addListener((observable, before, current) -> setDark(current));
        search.textProperty().addListener((observable, before, current) -> setQuery(current));
        app.setThemeCss(palette.light());
        selectHome();
    }

    public List<Gallery.Component> catalog() { return catalog; }
    public List<Gallery.Component> visibleComponents() {
        if (query.isBlank()) return catalog;
        String needle = query.toLowerCase(Locale.ROOT);
        return catalog.stream().filter(component -> component.name().toLowerCase(Locale.ROOT).contains(needle)
                || component.chinese().contains(query)).toList();
    }
    public String selectedComponent() { return selection == null ? null : selection.name(); }
    public void setQuery(String value) {
        Util.requireFxThread();
        query = Objects.requireNonNull(value).strip();
        if (!search.getText().equals(value)) search.setText(value);
        renderNavigation();
        if (selection == null) renderOverview();
    }
    public void selectComponent(String name) {
        Util.requireFxThread();
        selection = catalog.stream().filter(component -> component.name().equals(name)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown component: " + name));
        renderNavigation();
        renderDetail();
    }
    public void selectHome() {
        Util.requireFxThread();
        selection = null;
        renderNavigation();
        renderOverview();
    }
    public void selectPalette(String name) {
        Util.requireFxThread();
        palette = palettes.stream().filter(candidate -> candidate.name().equals(name)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown palette: " + name));
        if (!Objects.equals(paletteChoice.getValue(), name)) paletteChoice.setValue(name);
        app.setThemeCss(darkToggle.isSelected() ? palette.dark() : palette.light());
    }
    public void setDark(boolean value) {
        Util.requireFxThread();
        if (darkToggle.isSelected() != value) darkToggle.setSelected(value);
        app.setThemeCss(value ? palette.dark() : palette.light());
        darkToggle.setText(value ? "浅色" : "深色");
    }
    public void setDensity(ComponentConfig.Density value) {
        Util.requireFxThread();
        Objects.requireNonNull(value);
        if (densityChoice.getValue() != value) densityChoice.setValue(value);
        var current = app.getEffectiveConfig();
        app.setConfig(new ComponentConfig(current.fontFamily(), current.fontSize(), value,
                current.radius(), current.motionEnabled(), current.locale()));
    }

    private VBox sidebar() {
        var brand = new Label("Norm UI");
        brand.getStyleClass().add("gallery-brand");
        search.setId("gallery-search");
        search.setPromptText("搜索组件");
        navigation.setId("gallery-sidebar");
        var navigationScroll = new ScrollPane(navigation);
        navigationScroll.setFitToWidth(true);
        navigationScroll.getStyleClass().add("gallery-navigation-scroll");
        var sidebar = new VBox(24, brand, search, navigationScroll);
        sidebar.getStyleClass().add("gallery-sidebar");
        sidebar.setPrefWidth(248);
        VBox.setVgrow(navigationScroll, Priority.ALWAYS);
        return sidebar;
    }

    private HBox toolbar() {
        title.getStyleClass().add("gallery-toolbar-title");
        var spacer = new javafx.scene.layout.Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        var paletteLabel = new Label("主题色");
        var densityLabel = new Label("密度");
        var bar = new HBox(12, title, spacer, paletteLabel, paletteChoice, densityLabel, densityChoice, darkToggle);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("gallery-toolbar");
        return bar;
    }

    private void renderNavigation() {
        navigation.getChildren().clear();
        var home = new Button("组件总览");
        home.getStyleClass().add("gallery-navigation-item");
        if (selection == null) home.getStyleClass().add("selected");
        home.setOnAction(event -> selectHome());
        navigation.getChildren().add(home);
        var visible = visibleComponents();
        for (var category : Gallery.Category.values()) {
            var group = visible.stream().filter(component -> component.category() == category).toList();
            if (group.isEmpty()) continue;
            var caption = new Label(category.chinese());
            caption.getStyleClass().add("gallery-navigation-heading");
            navigation.getChildren().add(caption);
            for (var component : group) {
                var item = new Button(component.name() + "  " + component.chinese());
                item.getStyleClass().add("gallery-navigation-item");
                if (selection == component) item.getStyleClass().add("selected");
                item.setOnAction(event -> selectComponent(component.name()));
                navigation.getChildren().add(item);
            }
        }
    }

    private void renderOverview() {
        clearExamples();
        title.setText("组件总览");
        var page = new VBox(34);
        page.setId("gallery-overview");
        page.getStyleClass().add("gallery-page");
        var heading = new Label("组件总览");
        heading.getStyleClass().add("gallery-page-title");
        var subheading = new Label(visibleComponents().size() + " 个组件 · JavaFX");
        subheading.getStyleClass().add("gallery-secondary");
        page.getChildren().add(new VBox(6, heading, subheading));
        for (var category : Gallery.Category.values()) {
            var group = visibleComponents().stream().filter(component -> component.category() == category).toList();
            if (group.isEmpty()) continue;
            var groupHeading = new Label(category.chinese());
            groupHeading.getStyleClass().add("gallery-section-title");
            var tiles = new FlowPane(12, 12);
            for (var component : group) {
                var tile = new Button(component.name() + "\n" + component.chinese());
                tile.getStyleClass().add("gallery-tile");
                tile.setOnAction(event -> selectComponent(component.name()));
                tiles.getChildren().add(tile);
            }
            page.getChildren().add(new VBox(12, groupHeading, tiles));
        }
        content.setContent(page);
        content.setVvalue(0);
    }

    private void renderDetail() {
        clearExamples();
        title.setText(selection.category().chinese() + " / " + selection.name());
        var page = new VBox(28);
        page.setId("gallery-detail");
        page.getStyleClass().add("gallery-page");
        var back = new Button("← 组件总览");
        back.getStyleClass().add("gallery-back");
        back.setOnAction(event -> selectHome());
        var heading = new Label(selection.name());
        heading.getStyleClass().add("gallery-page-title");
        var subtitle = new Label(selection.chinese());
        subtitle.getStyleClass().add("gallery-secondary");
        var demo = selection.factory().get();
        demo.setId("gallery-example");
        activeExamples.add(demo);
        var preview = new VBox(18, new Label("基础用法"), demo);
        preview.getStyleClass().add("gallery-preview");
        page.getChildren().addAll(back, new VBox(6, heading, subtitle), preview);
        if (selection.name().equals("Button")) {
            var variants = new HBox(12);
            for (var kind : List.of("outlined", "text", "danger")) {
                var button = new dev.normlanguage.ui.component.Button(kind);
                button.getStyleClass().add(kind);
                button.setOnAction(event -> app.getMessages().success(kind));
                variants.getChildren().add(button);
            }
            var variantPreview = new VBox(18, new Label("样式"), variants);
            variantPreview.getStyleClass().add("gallery-preview");
            page.getChildren().add(variantPreview);
        }
        if (demo instanceof javafx.scene.control.Control) {
            var disabled = selection.factory().get();
            if (disabled instanceof javafx.scene.control.Control control) {
                control.setDisable(true);
                activeExamples.add(disabled);
                var disabledPreview = new VBox(18, new Label("禁用状态"), disabled);
                disabledPreview.getStyleClass().add("gallery-preview");
                page.getChildren().add(disabledPreview);
            } else Util.closeTree(disabled);
        }
        content.setContent(page);
        content.setVvalue(0);
    }

    private void clearExamples() {
        content.setContent(null);
        for (var example : activeExamples) Util.closeTree(example);
        activeExamples.clear();
    }

    @Override public void close() {
        Util.requireFxThread();
        if (closed) return;
        closed = true;
        clearExamples();
    }
}
