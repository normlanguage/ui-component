package dev.normlanguage.ui.component;

import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class Skeleton extends VBox {
    public Skeleton(int lines) {
        super(10);
        setLines(lines);
    }
    public void setLines(int lines) {
        if (lines < 1) throw new IllegalArgumentException("At least one line is required");
        if (getChildren().size() == lines) return;
        for (var node : getChildren()) ((Region) node).maxWidthProperty().unbind();
        getChildren().clear();
        for (int index = 0; index < lines; index++) {
            var line = new Region();
            line.setMinHeight(16);
            line.setPrefHeight(16);
            line.setStyle("-fx-background-color: -norm-divider; -fx-background-radius: 4;");
            if (index == lines - 1) line.maxWidthProperty().bind(widthProperty().multiply(0.65));
            getChildren().add(line);
        }
    }
}
