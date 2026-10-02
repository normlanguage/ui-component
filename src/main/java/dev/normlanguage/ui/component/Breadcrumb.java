package dev.normlanguage.ui.component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class Breadcrumb extends HBox {
    public record Item(String text, Runnable action) {
        public Item {
            Objects.requireNonNull(text);
            Objects.requireNonNull(action);
        }
    }
    private final ObservableList<Item> items = FXCollections.observableArrayList();

    public Breadcrumb() {
        super(4);
        getStyleClass().add("norm-breadcrumb");
        items.addListener((javafx.collections.ListChangeListener<Item>) change -> refresh());
    }
    public ObservableList<Item> getItems() { return items; }
    private void refresh() {
        getChildren().clear();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) getChildren().add(new Label("/"));
            var item = items.get(i);
            var link = new Hyperlink(item.text());
            link.setOnAction(event -> item.action().run());
            getChildren().add(link);
        }
    }
}
