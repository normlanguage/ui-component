package dev.normlanguage.ui.component.gallery;

import dev.normlanguage.ui.component.Anchor;
import dev.normlanguage.ui.component.App;
import dev.normlanguage.ui.component.ComponentConfig;
import dev.normlanguage.ui.component.Icon;
import dev.normlanguage.ui.component.Input;
import dev.normlanguage.ui.component.Util;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public final class GalleryView extends BorderPane implements AutoCloseable {
    private final App app;
    private final List<Gallery.Palette> palettes;
    private final List<Gallery.Component> catalog;
    private final VBox navigation = new VBox(6);
    private final ScrollPane content = new ScrollPane();
    private final Input search = new Input();
    private final ChoiceBox<String> paletteChoice = new ChoiceBox<>();
    private final ChoiceBox<ComponentConfig.Density> densityChoice = new ChoiceBox<>();
    private final ToggleButton darkToggle = new ToggleButton();
    private final Anchor sectionIndex = new Anchor(content);
    private final EnumSet<Gallery.Category> expanded = EnumSet.of(Gallery.Category.GENERAL);
    private final GridPane sections = new GridPane();
    private List<GalleryExamples.Section> currentSections = List.of();
    private final List<Node> activeExamples = new ArrayList<>();
    private Gallery.Palette palette;
    private Gallery.Component selection;
    private String query = "";
    private boolean closed;
    private int columns;

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
        setTop(toolbar());
        setLeft(sidebar());
        content.setFitToWidth(true);
        content.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        content.setId("gallery-scroll");
        content.getStyleClass().add("gallery-scroll");
        setCenter(content);
        sectionIndex.setId("gallery-section-index");
        var indexTitle = new Label("本页内容");
        indexTitle.getStyleClass().add("gallery-index-title");
        var index = new VBox(12, indexTitle, sectionIndex);
        index.getStyleClass().add("gallery-index");
        index.managedProperty().bind(index.visibleProperty());
        setRight(index);
        sections.setId("gallery-sections");
        sections.getStyleClass().add("gallery-sections");
        widthProperty().addListener(observable -> refreshLayout());
        content.viewportBoundsProperty().addListener(observable -> refreshLayout());
        paletteChoice.getItems().setAll(this.palettes.stream().map(Gallery.Palette::name).toList());
        paletteChoice.getSelectionModel().selectFirst();
        paletteChoice.valueProperty().addListener((observable, before, current) -> {
            if (current != null) selectPalette(current);
        });
        densityChoice.getItems().setAll(ComponentConfig.Density.values());
        densityChoice.setConverter(new javafx.util.StringConverter<>() {
            @Override public String toString(ComponentConfig.Density value) {
                return switch (value) { case COMPACT -> "紧凑"; case STANDARD -> "标准"; case SPACIOUS -> "宽松"; };
            }
            @Override public ComponentConfig.Density fromString(String value) {
                return densityChoice.getItems().stream().filter(item -> toString(item).equals(value)).findFirst().orElseThrow();
            }
        });
        densityChoice.setValue(app.getEffectiveConfig().density());
        densityChoice.valueProperty().addListener((observable, before, current) -> { if (current != null) setDensity(current); });
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
        if (!search.getText().equals(value)) { search.setText(value); return; }
        renderNavigation();
        if (selection == null) renderOverview();
    }
    public void selectComponent(String name) {
        Util.requireFxThread();
        selection = catalog.stream().filter(component -> component.name().equals(name)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown component: " + name));
        expanded.add(selection.category());
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
        if (!Objects.equals(paletteChoice.getValue(), name)) { paletteChoice.setValue(name); return; }
        app.setThemeCss(darkToggle.isSelected() ? palette.dark() : palette.light());
    }
    public void setDark(boolean value) {
        Util.requireFxThread();
        if (darkToggle.isSelected() != value) { darkToggle.setSelected(value); return; }
        app.setThemeCss(value ? palette.dark() : palette.light());
        darkToggle.setGraphic(new Icon(value ? "mdi2w-weather-sunny" : "mdi2w-weather-night"));
        darkToggle.setAccessibleText(value ? "切换浅色主题" : "切换深色主题");
    }
    public void setDensity(ComponentConfig.Density value) {
        Util.requireFxThread();
        Objects.requireNonNull(value);
        if (densityChoice.getValue() != value) { densityChoice.setValue(value); return; }
        var current = app.getEffectiveConfig();
        app.setConfig(new ComponentConfig(current.fontFamily(), current.fontSize(), value,
                current.radius(), current.motionEnabled(), current.locale()));
    }

    private HBox toolbar() {
        var norm = new Label("Norm");
        norm.getStyleClass().add("gallery-wordmark-accent");
        var ui = new Label("UI");
        var brand = new HBox(4, norm, ui);
        brand.getStyleClass().add("gallery-wordmark");
        brand.setAlignment(Pos.CENTER_LEFT);
        var subtitle = new Label("JavaFX 组件库");
        subtitle.getStyleClass().add("gallery-brand-caption");
        subtitle.visibleProperty().bind(widthProperty().greaterThanOrEqualTo(1080));
        subtitle.managedProperty().bind(subtitle.visibleProperty());
        var branding = new HBox(20, brand, subtitle);
        branding.setAlignment(Pos.CENTER_LEFT);
        branding.getStyleClass().add("gallery-branding");
        search.setId("gallery-search");
        search.setPromptText("搜索组件…");
        search.setOnAction(event -> {
            var found = visibleComponents();
            if (!found.isEmpty()) selectComponent(found.getFirst().name());
        });
        search.setMinWidth(60);
        HBox.setHgrow(search, Priority.ALWAYS);
        var searchBox = new HBox(10, new Icon("mdi2m-magnify"), search);
        searchBox.setAlignment(Pos.CENTER_LEFT);
        searchBox.getStyleClass().add("gallery-search-box");
        searchBox.setMinWidth(130);
        searchBox.setMaxWidth(600);
        HBox.setHgrow(searchBox, Priority.ALWAYS);
        paletteChoice.setId("gallery-palette");
        densityChoice.setId("gallery-density");
        darkToggle.setId("gallery-dark-toggle");
        darkToggle.getStyleClass().add("gallery-theme-toggle");
        darkToggle.setGraphic(new Icon("mdi2w-weather-night"));
        darkToggle.setAccessibleText("切换深色主题");
        paletteChoice.setAccessibleText("主题色");
        densityChoice.setAccessibleText("密度");
        var gap = new Region();
        HBox.setHgrow(gap, Priority.ALWAYS);
        var paletteLabel = new Label("主题色");
        var densityLabel = new Label("密度");
        paletteLabel.getStyleClass().add("gallery-toolbar-caption");
        densityLabel.getStyleClass().add("gallery-toolbar-caption");
        var bar = new HBox(12, branding, searchBox, gap, paletteLabel, paletteChoice, densityLabel, densityChoice, darkToggle);
        bar.setAlignment(Pos.CENTER_LEFT);
        bar.getStyleClass().add("gallery-toolbar");
        return bar;
    }
    private VBox sidebar() {
        navigation.setId("gallery-sidebar");
        var scroll = new ScrollPane(navigation);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("gallery-navigation-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);
        var total = new Label("共 " + catalog.size() + " 个组件");
        var caption = new Label("Norm UI · JavaFX");
        var footer = new VBox(10, total, caption);
        footer.getStyleClass().add("gallery-sidebar-footer");
        var sidebar = new VBox(16, scroll, footer);
        sidebar.getStyleClass().add("gallery-sidebar");
        return sidebar;
    }
    private void renderNavigation() {
        navigation.getChildren().clear();
        var home = new Button("组件总览", new Icon("mdi2v-view-grid-outline"));
        home.getStyleClass().add("gallery-category");
        if (selection == null) home.getStyleClass().add("selected");
        home.setOnAction(event -> selectHome());
        navigation.getChildren().add(home);
        for (var category : Gallery.Category.values()) {
            var group = visibleComponents().stream().filter(component -> component.category() == category).toList();
            if (group.isEmpty()) continue;
            boolean open = expanded.contains(category) || !query.isBlank();
            var label = new Label(category.chinese());
            var spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            var icon = new Icon(switch (category) {
                case GENERAL -> "mdi2v-view-dashboard-outline";
                case LAYOUT -> "mdi2v-view-quilt-outline";
                case NAVIGATION -> "mdi2c-compass-outline";
                case INPUT -> "mdi2p-pencil-outline";
                case DISPLAY -> "mdi2v-view-carousel-outline";
                case FEEDBACK -> "mdi2m-message-outline";
                case OTHER -> "mdi2c-cube-outline";
            });
            var row = new HBox(12, icon, label, spacer, new Icon(open ? "mdi2c-chevron-up" : "mdi2c-chevron-down"));
            row.setAlignment(Pos.CENTER_LEFT);
            var heading = new Button();
            heading.setGraphic(row);
            row.prefWidthProperty().bind(heading.widthProperty().subtract(24));
            heading.setId("gallery-category-" + category.name());
            heading.setAccessibleText(category.chinese() + (open ? "，已展开" : "，已折叠"));
            heading.getStyleClass().add("gallery-category");
            heading.setOnAction(event -> {
                if (!expanded.remove(category)) expanded.add(category);
                renderNavigation();
            });
            navigation.getChildren().add(heading);
            if (open) for (var component : group) {
                var item = new Button(component.name() + "  " + component.chinese());
                item.setId("gallery-nav-" + component.name());
                item.getStyleClass().add("gallery-navigation-item");
                if (selection == component) item.getStyleClass().add("selected");
                item.setOnAction(event -> selectComponent(component.name()));
                navigation.getChildren().add(item);
            }
        }
    }
    private void renderOverview() {
        clearExamples();
        var page = new VBox(28);
        page.setId("gallery-overview");
        page.getStyleClass().add("gallery-page");
        var title = new Label(query.isBlank() ? "组件总览" : "搜索结果");
        title.getStyleClass().add("gallery-page-title");
        var count = new Label(visibleComponents().size() + " 个组件");
        count.getStyleClass().add("gallery-secondary");
        page.getChildren().add(new VBox(10, title, count));
        for (var category : Gallery.Category.values()) {
            var group = visibleComponents().stream().filter(component -> component.category() == category).toList();
            if (group.isEmpty()) continue;
            var heading = new Label(category.chinese());
            heading.getStyleClass().add("gallery-section-heading");
            var links = new FlowPane(12, 10);
            for (var component : group) {
                var link = new Button(component.name() + "  " + component.chinese());
                link.getStyleClass().add("gallery-overview-link");
                link.setOnAction(event -> selectComponent(component.name()));
                links.getChildren().add(link);
            }
            page.getChildren().add(new VBox(12, heading, links));
        }
        if (visibleComponents().isEmpty()) page.getChildren().add(new Label("没有匹配的组件"));
        content.setContent(page);
        content.setVvalue(0);
        refreshLayout();
    }
    private void renderDetail() {
        clearExamples();
        var title = new Label(selection.name() + " " + selection.chinese());
        title.getStyleClass().add("gallery-page-title");
        var header = new VBox(10, title);
        if (selection.name().equals("Button")) {
            var description = new Label("触发一个操作。");
            description.getStyleClass().add("gallery-secondary");
            header.getChildren().add(description);
        }
        currentSections = GalleryExamples.create(selection, app);
        for (var section : currentSections) {
            activeExamples.add(section.content());
            var heading = new Label(section.title());
            heading.getStyleClass().add("gallery-section-heading");
            var block = new VBox(24, heading, section.content());
            block.setId("gallery-section-" + section.id());
            block.getStyleClass().add("gallery-section");
            block.setMinWidth(0);
            if (section.content() instanceof Region region) region.setMinWidth(0);
            sections.getChildren().add(block);
            sectionIndex.getItems().add(new Anchor.Item(section.title(), block));
        }
        var page = new VBox(40, header, sections);
        if (!sectionIndex.getItems().isEmpty()) sectionIndex.activeItemProperty().set(sectionIndex.getItems().getFirst());
        page.setId("gallery-detail");
        page.getStyleClass().add("gallery-page");
        content.setContent(page);
        content.setVvalue(0);
        columns = 0;
        refreshLayout();
    }
    private void refreshLayout() {
        getRight().setVisible(selection != null && getWidth() >= 1320);
        pseudoClassStateChanged(javafx.css.PseudoClass.getPseudoClass("compact"), getWidth() < 1080);
        int next = content.getViewportBounds().getWidth() >= 760 ? 2 : 1;
        if (next == columns) return;
        columns = next;
        sections.getColumnConstraints().clear();
        for (int i = 0; i < columns; i++) {
            var column = new ColumnConstraints();
            column.setPercentWidth(100.0 / columns);
            column.setHgrow(Priority.ALWAYS);
            column.setMinWidth(0);
            sections.getColumnConstraints().add(column);
        }
        int row = 0;
        int col = 0;
        for (int i = 0; i < currentSections.size(); i++) {
            var node = sections.getChildren().get(i);
            boolean full = currentSections.get(i).fullWidth();
            if (full && col != 0) { row++; col = 0; }
            GridPane.setConstraints(node, col, row, full ? columns : 1, 1);
            GridPane.setValignment(node, javafx.geometry.VPos.TOP);
            if (full || ++col == columns) { row++; col = 0; }
        }
    }
    private void clearExamples() {
        sectionIndex.getItems().clear();
        content.setContent(null);
        sections.getChildren().clear();
        for (var example : activeExamples) Util.closeTree(example);
        activeExamples.clear();
        currentSections = List.of();
    }
    @Override public void close() {
        Util.requireFxThread();
        if (closed) return;
        closed = true;
        clearExamples();
    }
}
