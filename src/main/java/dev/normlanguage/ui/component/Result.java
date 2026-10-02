package dev.normlanguage.ui.component;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public final class Result extends VBox {
    private final Label heading = new Label();
    private final Label detail = new Label();
    public Result(String title, String description, Node... actions) {
        super(12);
        setAlignment(Pos.CENTER);
        heading.setText(title);
        heading.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        detail.setText(description);
        detail.setWrapText(true);
        getChildren().addAll(heading, detail);
        getChildren().addAll(actions);
    }
    public void setTitle(String value) { heading.setText(value); }
    public void setDescription(String value) { detail.setText(value); }
    public void setActions(java.util.List<Node> actions) {
        getChildren().setAll(heading, detail);
        getChildren().addAll(actions);
    }
}
