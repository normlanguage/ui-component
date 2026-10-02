package dev.normlanguage.ui.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class FloatButton extends Button {
    public FloatButton() { this(""); }
    public FloatButton(String text) {
        super(text);
        getStyleClass().add("norm-float-button");
    }
    public FloatButton attachTo(StackPane layer) {
        if (getParent() != null) throw new IllegalStateException("Float button already has a parent");
        layer.getChildren().add(this);
        StackPane.setAlignment(this, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(this, new Insets(16));
        return this;
    }
    public static class Group extends VBox {
        public Group(FloatButton... buttons) {
            super(8, buttons);
            getStyleClass().add("norm-float-button-group");
            setAlignment(Pos.CENTER_RIGHT);
            setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        }
        public Group attachTo(StackPane layer) {
            if (getParent() != null) throw new IllegalStateException("Float button group already has a parent");
            layer.getChildren().add(this);
            StackPane.setAlignment(this, Pos.BOTTOM_RIGHT);
            StackPane.setMargin(this, new Insets(16));
            return this;
        }
    }
}
