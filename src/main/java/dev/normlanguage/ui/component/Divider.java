package dev.normlanguage.ui.component;

import javafx.geometry.Orientation;
import javafx.scene.control.Separator;

public class Divider extends Separator {
    public Divider() { this(Orientation.HORIZONTAL); }
    public Divider(Orientation orientation) {
        super(orientation);
        getStyleClass().add("norm-divider");
    }
}
