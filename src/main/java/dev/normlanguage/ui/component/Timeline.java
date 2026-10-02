package dev.normlanguage.ui.component;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
public class Timeline extends VBox {
    public Timeline() { getStyleClass().add("norm-timeline"); }
    public void add(String time, Node content) {
        var entry = new javafx.scene.layout.HBox(new Label(time), content);
        entry.getStyleClass().add("norm-timeline-entry");
        getChildren().add(entry);
    }
}
