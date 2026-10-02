package dev.normlanguage.ui.component;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
public class Card extends BorderPane {
    private final Label title = new Label();
    public Card(String heading, Node content) {
        getStyleClass().add("norm-card");
        title.getStyleClass().add("norm-card-title");
        title.setText(heading);
        setTop(title);
        setCenter(content);
    }
    public void setTitle(String value) { title.setText(value); }
    public String getTitle() { return title.getText(); }
}
