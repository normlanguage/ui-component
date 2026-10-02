package dev.normlanguage.ui.component;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public final class Alert extends HBox {
    private final Button dismiss = new Button("×");
    public Alert(String title, Node content) {
        super(12);
        var body = new VBox(4, new Label(title), content);
        HBox.setHgrow(body, Priority.ALWAYS);
        getChildren().addAll(body, dismiss);
        getStyleClass().add("norm-card");
        dismiss.setOnAction(event -> dismiss());
    }
    public void setClosable(boolean value) { dismiss.setVisible(value); dismiss.setManaged(value); }
    public void dismiss() { setVisible(false); setManaged(false); }
}
