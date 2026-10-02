package dev.normlanguage.ui.component;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import java.util.Objects;

public final class Surface extends StackPane {
    private String className = "";
    public Surface() { getStyleClass().add("norm-surface"); }
    public void setContent(Node content) { getChildren().setAll(content); }
    public void setClassName(String value) {
        Objects.requireNonNull(value);
        if (className.equals(value)) return;
        getStyleClass().remove(className);
        className = value;
        if (!value.isBlank()) getStyleClass().add(value);
    }
}
