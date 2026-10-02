package dev.normlanguage.ui.component;

import org.kordamp.ikonli.javafx.FontIcon;

public class Icon extends FontIcon {
    public Icon() {
        getStyleClass().add("norm-icon");
    }
    public Icon(String iconLiteral) {
        super(iconLiteral);
        getStyleClass().add("norm-icon");
    }
}
