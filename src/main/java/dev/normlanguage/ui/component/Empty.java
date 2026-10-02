package dev.normlanguage.ui.component;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
public class Empty extends StackPane {
    private final Label description = new Label();
    public Empty(String text) {
        getStyleClass().add("norm-empty");
        description.setText(text);
        getChildren().add(description);
    }
    public void setDescription(String text) { description.setText(text); }
    public String getDescription() { return description.getText(); }
}
