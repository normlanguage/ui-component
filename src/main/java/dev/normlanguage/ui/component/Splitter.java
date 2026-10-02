package dev.normlanguage.ui.component;

import javafx.scene.Node;
import javafx.scene.control.SplitPane;

public class Splitter extends SplitPane {
    public Splitter(Node... children) {
        super(children);
        getStyleClass().add("norm-splitter");
    }
}
