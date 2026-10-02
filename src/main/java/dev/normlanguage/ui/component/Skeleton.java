package dev.normlanguage.ui.component;

import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public final class Skeleton extends VBox {
    public Skeleton(int lines) {
        super(10);
        if (lines < 1) throw new IllegalArgumentException("At least one line is required");
        for (int index = 0; index < lines; index++) {
            var line = new Region();
            line.setMinHeight(16);
            line.setPrefHeight(16);
            line.setStyle("-fx-background-color: -norm-outline; -fx-background-radius: 4;");
            if (index == lines - 1) line.maxWidthProperty().bind(widthProperty().multiply(0.65));
            getChildren().add(line);
        }
    }
}
