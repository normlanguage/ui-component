package dev.normlanguage.ui.component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class AutoComplete extends ComboBox<String> {
    private final ObservableList<String> suggestions = FXCollections.observableArrayList();
    private final BooleanProperty loading = new SimpleBooleanProperty(this, "loading");
    private Function<String, CompletableFuture<List<String>>> provider;
    private AsyncRequest<List<String>> request;
    private long revision;
    public AutoComplete() {
        setEditable(true);
        getEditor().textProperty().addListener((observable, oldValue, value) -> {
            String query = value.toLowerCase(Locale.ROOT);
            getItems().setAll(suggestions.stream().filter(item -> item.toLowerCase(Locale.ROOT).contains(query)).toList());
            if (provider != null && getScene() != null) search(value);
        });
        sceneProperty().addListener((observable, old, scene) -> {
            if (scene == null) { revision++; if (request != null) request.cancel(); loading.set(false); hide(); }
            else if (provider != null && !getEditor().getText().isBlank()) search(getEditor().getText());
        });
        getStyleClass().add("norm-auto-complete");
    }
    public ObservableList<String> getSuggestions() { return suggestions; }
    public void setSuggestions(List<String> values) { suggestions.setAll(values); getItems().setAll(values); }
    public BooleanProperty loadingProperty() { return loading; }
    public boolean isLoading() { return loading.get(); }
    public void setProvider(Function<String, CompletableFuture<List<String>>> provider) {
        this.provider = Objects.requireNonNull(provider);
        revision++;
        if (request != null) request.cancel();
        loading.set(false);
        if (getScene() != null && !getEditor().getText().isBlank()) search(getEditor().getText());
    }
    private void search(String query) {
        long current = ++revision;
        var activeProvider = provider;
        if (request != null) request.cancel();
        loading.set(true);
        request = new AsyncRequest<>(() -> activeProvider.apply(query));
        request.result().whenComplete((matches, failure) -> javafx.application.Platform.runLater(() -> {
                if (current != revision) return;
                loading.set(false);
                if (failure == null) {
                    suggestions.setAll(matches);
                    getItems().setAll(matches);
                    if (isFocused() && !matches.isEmpty()) show();
                }
            }));
    }
}
