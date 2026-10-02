package dev.normlanguage.ui.component;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
public class Descriptions extends GridPane {
    private int rows;
    public Descriptions() { getStyleClass().add("norm-descriptions"); }
    public void add(String label, Node value) {
        add(new Label(label), 0, rows);
        add(value, 1, rows++);
    }
}
