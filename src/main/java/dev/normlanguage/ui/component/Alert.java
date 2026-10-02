package dev.normlanguage.ui.component;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public final class Alert extends HBox {
    private final Button dismiss = new Button("×");
    private final Label heading = new Label();
    private final VBox body = new VBox(4);
    public Alert(String title, Node content) {
        super(12);
        heading.setText(title);
        body.getChildren().addAll(heading, content);
        HBox.setHgrow(body, Priority.ALWAYS);
        getChildren().addAll(body, dismiss);
        getStyleClass().add("norm-card");
        dismiss.setOnAction(event -> dismiss());
    }
    public String getTitle() { return heading.getText(); }
    public void setTitle(String value) { heading.setText(value); }
    public Node getContent() { return body.getChildren().get(1); }
    public void setContent(Node value) { body.getChildren().set(1, value); }
    public void setClosable(boolean value) { dismiss.setVisible(value); dismiss.setManaged(value); }
    public void dismiss() { setVisible(false); setManaged(false); }
}
