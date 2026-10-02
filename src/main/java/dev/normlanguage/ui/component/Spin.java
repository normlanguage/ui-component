package dev.normlanguage.ui.component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;

public final class Spin extends StackPane {
    private final BooleanProperty spinning = new SimpleBooleanProperty(this, "spinning");
    public Spin(Node content) {
        var indicator = new ProgressIndicator();
        indicator.setMaxSize(36, 36);
        var overlay = new StackPane(indicator);
        overlay.visibleProperty().bind(spinning);
        overlay.managedProperty().bind(spinning);
        getChildren().addAll(content, overlay);
    }
    public void setContent(Node value) { getChildren().set(0, value); }

    public BooleanProperty spinningProperty() { return spinning; }
    public boolean isSpinning() { return spinning.get(); }
    public void setSpinning(boolean value) { spinning.set(value); }
}
