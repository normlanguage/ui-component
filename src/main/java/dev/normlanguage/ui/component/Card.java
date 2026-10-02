package dev.normlanguage.ui.component;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
public class Card extends BorderPane {
    private final Label title = new Label();
    private Node header;
    public Card(String heading, Node content) {
        getStyleClass().add("norm-card");
        title.getStyleClass().add("norm-card-title");
        title.setText(heading);
        setHeader(null);
        setCenter(content);
    }
    public void setTitle(String value) { title.setText(value); setHeader(header); }
    public void setHeader(Node value) {
        header = value;
        setTop(value != null ? value : title.getText().isBlank() ? null : title);
    }
    public String getTitle() { return title.getText(); }
}
