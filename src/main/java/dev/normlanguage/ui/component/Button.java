package dev.normlanguage.ui.component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;

public class Button extends javafx.scene.control.Button {
    private final BooleanProperty loading = new SimpleBooleanProperty(this, "loading");
    public Button() { this(""); }
    public Button(String text) {
        super(text);
        getStyleClass().add("norm-button");
        loading.addListener((observable, old, value) -> pseudoClassStateChanged(PseudoClass.getPseudoClass("loading"), value));
    }
    public BooleanProperty loadingProperty() { return loading; }
    public boolean isLoading() { return loading.get(); }
    public void setLoading(boolean value) { loading.set(value); }
    @Override public void fire() { if (!isLoading()) super.fire(); }
}
