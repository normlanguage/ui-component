package dev.normlanguage.ui.component;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
public class Collapse extends TitledPane {
    public Collapse(String title, Node content) {
        super(title, content);
        getStyleClass().add("norm-collapse");
        setExpanded(false);
    }
}
