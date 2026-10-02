package dev.normlanguage.ui.component;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public final class Result extends VBox {
    public Result(String title, String description, Node... actions) {
        super(12);
        setAlignment(Pos.CENTER);
        var heading = new Label(title);
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        var detail = new Label(description);
        detail.setWrapText(true);
        getChildren().addAll(heading, detail);
        getChildren().addAll(actions);
    }
}
