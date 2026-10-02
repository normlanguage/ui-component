package dev.normlanguage.ui.component;

import javafx.collections.ObservableList;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;

public class Select<T> extends ComboBox<T> {
    public Select() { getStyleClass().add("norm-select"); }
    public Select(ObservableList<T> items) { super(items); getStyleClass().add("norm-select"); }
    public static <T> Searchable<T> searchable(ObservableList<T> options) { return new Searchable<>(options); }
    public static <T> Multiple<T> multiple(ObservableList<T> options) { return new Multiple<>(options); }
    public static final class Searchable<T> extends VBox {
        private final ObservableList<T> options;
        private final FilteredList<T> filtered;
        private final TextField search = new TextField();
        private final Select<T> select;
        private Function<? super T, String> label = String::valueOf;
        public Searchable(ObservableList<T> options) {
            this.options = Objects.requireNonNull(options);
            filtered = new FilteredList<>(options);
            select = new Select<>(filtered);
            search.setPromptText("Search options");
            search.textProperty().addListener((observable, old, query) -> {
                String normalized = query.toLowerCase(Locale.ROOT);
                filtered.setPredicate(item -> label.apply(item).toLowerCase(Locale.ROOT).contains(normalized));
            });
            getChildren().addAll(search, select);
            getStyleClass().add("norm-searchable-select");
        }
        public TextField getSearchField() { return search; }
        public Select<T> getSelect() { return select; }
        public ObservableList<T> getOptions() { return options; }
        public void setLabel(Function<? super T, String> label) {
            this.label = Objects.requireNonNull(label);
            String query = search.getText().toLowerCase(Locale.ROOT);
            filtered.setPredicate(item -> label.apply(item).toLowerCase(Locale.ROOT).contains(query));
        }
    }
    public static final class Multiple<T> extends HBox {
        private final ObservableList<T> options;
        private final ObservableList<T> selected = FXCollections.observableArrayList();
        private final FilteredList<T> filtered;
        private final TextField search = new TextField();
        private final ListView<T> list;
        private final Button trigger = new Button("Select options");
        private final Popover popup;
        private final javafx.collections.ListChangeListener<T> optionChange;
        private Function<? super T, String> label = String::valueOf;
        public Multiple(ObservableList<T> options) {
            this.options = Objects.requireNonNull(options);
            optionChange = change -> selected.removeIf(item -> !this.options.contains(item));
            filtered = new FilteredList<>(options);
            list = new ListView<>(filtered);
            search.setPromptText("Search options");
            search.textProperty().addListener((observable, old, query) -> {
                String normalized = query.toLowerCase(Locale.ROOT);
                filtered.setPredicate(item -> label.apply(item).toLowerCase(Locale.ROOT).contains(normalized));
            });
            list.setCellFactory(view -> new javafx.scene.control.ListCell<>() {
                private final CheckBox checkbox = new CheckBox();
                { checkbox.setOnAction(event -> {
                    T item = getItem();
                    if (item != null) { if (checkbox.isSelected()) select(item); else deselect(item); }
                }); }
                @Override protected void updateItem(T item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty || item == null ? null : checkbox);
                    if (!empty && item != null) {
                        checkbox.setText(label.apply(item));
                        checkbox.setSelected(selected.contains(item));
                    }
                }
            });
            selected.addListener((javafx.collections.ListChangeListener<T>) change -> {
                trigger.setText(selected.isEmpty() ? "Select options" : selected.size() + " selected");
                list.refresh();
            });
            options.addListener(new javafx.collections.WeakListChangeListener<>(optionChange));
            popup = new Popover(trigger, new VBox(search, list));
            trigger.setOnAction(event -> {
                if (popup.isShowing()) popup.close();
                else popup.show();
            });
            sceneProperty().addListener((observable, old, scene) -> { if (scene == null) popup.close(); });
            getChildren().add(trigger);
            getStyleClass().add("norm-multiple-select");
        }
        public ObservableList<T> getOptions() { return options; }
        public ObservableList<T> getSelectedItems() { return FXCollections.unmodifiableObservableList(selected); }
        public boolean isShowing() { return popup.isShowing(); }
        public TextField getSearchField() { return search; }
        public void setLabel(Function<? super T, String> label) {
            this.label = Objects.requireNonNull(label);
            String query = search.getText().toLowerCase(Locale.ROOT);
            filtered.setPredicate(item -> label.apply(item).toLowerCase(Locale.ROOT).contains(query));
            list.refresh();
        }
        public void select(T item) {
            if (!options.contains(item)) throw new IllegalArgumentException("Unknown option");
            if (!selected.contains(item)) selected.add(item);
        }
        public void deselect(T item) { selected.remove(item); }
    }
}
