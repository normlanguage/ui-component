package dev.normlanguage.ui.component;

import javafx.scene.Node;

import java.util.Objects;

public class Dropdown extends Button implements AutoCloseable {
    private final Popover popup;

    public Dropdown(String text, Node content) {
        super(text);
        getStyleClass().add("norm-dropdown");
        popup = new Popover(this, Objects.requireNonNull(content));
        setOnAction(event -> {
            if (popup.isShowing()) popup.hide(); else popup.show();
        });
    }
    public boolean isShowing() { return popup.isShowing(); }
    public void show() { popup.show(); }
    public void hide() { popup.hide(); }
    @Override public void close() {
        popup.close();
        super.close();
    }
}
