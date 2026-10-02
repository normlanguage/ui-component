package dev.normlanguage.ui.component;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;

import java.util.List;
import java.util.Locale;

public class Mentions extends TextArea {
    private final ObservableList<String> suggestions = FXCollections.observableArrayList();
    private final ListView<String> candidates = new ListView<>();
    private final Popover popup = new Popover(this, candidates);
    public Mentions() {
        getStyleClass().add("norm-mentions");
        caretPositionProperty().addListener((observable, old, value) -> updateSuggestions());
        textProperty().addListener((observable, old, value) -> updateSuggestions());
        suggestions.addListener((javafx.collections.ListChangeListener<String>) change -> updateSuggestions());
        candidates.setOnMouseClicked(event -> {
            var selected = candidates.getSelectionModel().getSelectedItem();
            if (selected != null) insertMention(selected);
        });
        candidates.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                var selected = candidates.getSelectionModel().getSelectedItem();
                if (selected != null) insertMention(selected);
                event.consume();
            }
        });
        setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DOWN && popup.isShowing()) {
                candidates.requestFocus();
                candidates.getSelectionModel().selectFirst();
                event.consume();
            }
        });
        sceneProperty().addListener((observable, old, scene) -> { if (scene == null) popup.close(); });
    }
    public ObservableList<String> getSuggestions() { return suggestions; }
    public boolean isSuggestionShowing() { return popup.isShowing(); }
    public void setSuggestions(List<String> values) { suggestions.setAll(values); }
    public void insertMention(String name) {
        int end = getCaretPosition();
        int start = mentionStart();
        if (start < 0) throw new IllegalStateException("No mention at caret");
        replaceText(start, end, "@" + name + " ");
        popup.close();
        requestFocus();
    }
    private int mentionStart() {
        String before = getText().substring(0, getCaretPosition());
        int start = before.lastIndexOf('@');
        if (start < 0 || (start > 0 && !Character.isWhitespace(before.charAt(start - 1)))) return -1;
        for (int index = start + 1; index < before.length(); index++)
            if (Character.isWhitespace(before.charAt(index))) return -1;
        return start;
    }
    private void updateSuggestions() {
        int start = mentionStart();
        if (start < 0 || getScene() == null) { popup.close(); return; }
        String query = getText().substring(start + 1, getCaretPosition()).toLowerCase(Locale.ROOT);
        candidates.getItems().setAll(suggestions.stream().filter(name -> name.toLowerCase(Locale.ROOT).startsWith(query)).toList());
        if (candidates.getItems().isEmpty()) popup.close();
        else if (!popup.isShowing()) popup.show();
    }
}
