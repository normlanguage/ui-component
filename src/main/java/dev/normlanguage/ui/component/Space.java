package dev.normlanguage.ui.component;

import javafx.scene.Node;
import javafx.scene.layout.HBox;

public class Space extends HBox {
    public Space(Node... children) {
        super(8, children);
        getStyleClass().add("norm-space");
    }
}
