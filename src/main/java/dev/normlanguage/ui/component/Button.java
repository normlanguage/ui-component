package dev.normlanguage.ui.component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;

public class Button extends javafx.scene.control.Button implements AutoCloseable {
    private final Motion motion = new Motion(this);
    private final BooleanProperty loading = new SimpleBooleanProperty(this, "loading");
    public Button() { this(""); }
    public Button(String text) {
        super(text);
        getStyleClass().add("norm-button");
        armedProperty().addListener((observable, old, armed) -> {
            double scale = armed ? 0.97 : 1;
            motion.animate(Motion.FAST,
                    new javafx.animation.KeyValue(scaleXProperty(), scale, Motion.EASING),
                    new javafx.animation.KeyValue(scaleYProperty(), scale, Motion.EASING));
        });
        loading.addListener((observable, old, value) -> pseudoClassStateChanged(PseudoClass.getPseudoClass("loading"), value));
    }
    public BooleanProperty loadingProperty() { return loading; }
    public boolean isLoading() { return loading.get(); }
    public void setLoading(boolean value) { loading.set(value); }
    @Override public void close() { motion.close(); }
    @Override public void fire() { if (!isLoading()) super.fire(); }
}
