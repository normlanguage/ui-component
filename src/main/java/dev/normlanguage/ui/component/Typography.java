package dev.normlanguage.ui.component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;

public class Typography extends Label {
    private final BooleanProperty copyable = new SimpleBooleanProperty(this, "copyable");
    private final BooleanProperty editable = new SimpleBooleanProperty(this, "editable");
    private final MenuItem copy = new MenuItem("Copy");
    private final MenuItem edit = new MenuItem("Edit");
    private final ContextMenu menu = new ContextMenu(copy, edit);
    private final TextField editor = new TextField();
    private Node previousGraphic;
    private ContentDisplay previousDisplay;
    private EventHandler<ActionEvent> onEditCommit;
    private boolean editing;
    private boolean canceling;

    public Typography() { this(""); }
    public Typography(String text) {
        super(text);
        getStyleClass().add("norm-typography");
        copy.setOnAction(event -> {
            var content = new ClipboardContent();
            content.putString(getText());
            Clipboard.getSystemClipboard().setContent(content);
        });
        edit.setOnAction(event -> beginEdit());
        editor.setOnAction(event -> commitEdit());
        editor.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                canceling = true;
                cancelEdit();
                event.consume();
            }
        });
        editor.focusedProperty().addListener((observable, old, focused) -> {
            if (!focused && editing && !canceling) commitEdit();
            canceling = false;
        });
        copyable.addListener((observable, old, enabled) -> refreshMenu());
        editable.addListener((observable, old, enabled) -> refreshMenu());
    }
    public BooleanProperty copyableProperty() { return copyable; }
    public boolean isCopyable() { return copyable.get(); }
    public void setCopyable(boolean value) { copyable.set(value); }
    public BooleanProperty editableProperty() { return editable; }
    public boolean isEditable() { return editable.get(); }
    public void setEditable(boolean value) { editable.set(value); }
    public boolean isEditing() { return editing; }
    public void setOnEditCommit(EventHandler<ActionEvent> handler) { onEditCommit = handler; }
    public EventHandler<ActionEvent> getOnEditCommit() { return onEditCommit; }
    public void beginEdit() {
        if (!isEditable() || editing) return;
        editing = true;
        previousGraphic = getGraphic();
        previousDisplay = getContentDisplay();
        editor.setText(getText());
        setGraphic(editor);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        editor.requestFocus();
        editor.selectAll();
    }
    public void commitEdit() {
        if (!editing) return;
        String next = editor.getText();
        finishEdit();
        setText(next);
        if (onEditCommit != null) onEditCommit.handle(new ActionEvent(this, this));
    }
    public void cancelEdit() {
        if (editing) finishEdit();
    }
    private void finishEdit() {
        editing = false;
        setGraphic(previousGraphic);
        setContentDisplay(previousDisplay);
        previousGraphic = null;
        previousDisplay = null;
    }
    private void refreshMenu() {
        copy.setVisible(isCopyable());
        edit.setVisible(isEditable());
        setContextMenu(isCopyable() || isEditable() ? menu : null);
    }
}
