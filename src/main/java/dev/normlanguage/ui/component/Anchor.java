package dev.normlanguage.ui.component;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.Objects;

public class Anchor extends VBox implements AutoCloseable {
    private final Motion motion = new Motion(this);
    public record Item(String text, Node target) {
        public Item {
            Objects.requireNonNull(text);
            Objects.requireNonNull(target);
        }
    }
    private final ObservableList<Item> items = FXCollections.observableArrayList();
    private final ObjectProperty<ScrollPane> scrollPane = new SimpleObjectProperty<>(this, "scrollPane") {
        @Override public void set(ScrollPane value) { super.set(Objects.requireNonNull(value)); }
    };
    private final ObjectProperty<Item> activeItem = new SimpleObjectProperty<>(this, "activeItem");
    private final InvalidationListener positionChanged = this::updateActive;
    private boolean connected;

    public Anchor(ScrollPane scrollPane) {
        super(4);
        getStyleClass().add("norm-anchor");
        this.scrollPane.set(scrollPane);
        items.addListener((javafx.collections.ListChangeListener<Item>) change -> refresh());
        this.scrollPane.addListener((observable, old, current) -> {
            if (connected) {
                if (old != null) old.vvalueProperty().removeListener(positionChanged);
                current.vvalueProperty().addListener(positionChanged);
            }
            refresh();
        });
        sceneProperty().addListener((observable, old, scene) -> {
            if (connected) getScrollPane().vvalueProperty().removeListener(positionChanged);
            connected = scene != null;
            if (connected) {
                getScrollPane().vvalueProperty().addListener(positionChanged);
                updateActive(getScrollPane().vvalueProperty());
            }
        });
        activeItem.addListener(observable -> refresh());
    }
    public ObservableList<Item> getItems() { return items; }
    public ObjectProperty<ScrollPane> scrollPaneProperty() { return scrollPane; }
    public ScrollPane getScrollPane() { return scrollPane.get(); }
    public void setScrollPane(ScrollPane value) { scrollPane.set(value); }
    public ObjectProperty<Item> activeItemProperty() { return activeItem; }
    public Item getActiveItem() { return activeItem.get(); }
    public void scrollTo(Item item) {
        Objects.requireNonNull(item);
        var pane = getScrollPane();
        var content = pane.getContent();
        if (content == null || item.target().getScene() == null || content.getScene() != item.target().getScene())
            throw new IllegalStateException("Target must belong to the scroll content");
        var targetBounds = content.sceneToLocal(item.target().localToScene(item.target().getBoundsInLocal()));
        var contentHeight = content.getLayoutBounds().getHeight();
        var visibleHeight = pane.getViewportBounds().getHeight();
        pane.setVvalue(Math.clamp(targetBounds.getMinY() / Math.max(1, contentHeight - visibleHeight), 0.0, 1.0));
        activeItem.set(item);
    }
    private void updateActive(javafx.beans.Observable ignored) {
        var pane = getScrollPane();
        var content = pane.getContent();
        if (content == null || content.getScene() == null) return;
        double viewportY = pane.getVvalue() * Math.max(0, content.getLayoutBounds().getHeight() - pane.getViewportBounds().getHeight());
        Item latest = null;
        for (var item : items) {
            if (item.target().getScene() != content.getScene()) continue;
            double y = content.sceneToLocal(item.target().localToScene(item.target().getBoundsInLocal())).getMinY();
            if (y <= viewportY + 1 && (latest == null || y > content.sceneToLocal(latest.target().localToScene(latest.target().getBoundsInLocal())).getMinY())) latest = item;
        }
        if (latest != null && latest != activeItem.get()) activeItem.set(latest);
    }
    private void refresh() {
        getChildren().clear();
        for (var item : items) {
            var link = new Hyperlink(item.text());
            link.setOnAction(event -> {
                var pane = getScrollPane();
                double before = pane.getVvalue();
                scrollTo(item);
                double destination = pane.getVvalue();
                pane.setVvalue(before);
                motion.animate(Motion.ENTER, new javafx.animation.KeyValue(pane.vvalueProperty(), destination, Motion.EASING));
            });
            if (item == activeItem.get()) link.getStyleClass().add("norm-anchor-active");
            getChildren().add(link);
        }
    }
    @Override public void close() { motion.close(); }
}
