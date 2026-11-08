package dev.normlanguage.ui.component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.css.PseudoClass;

public class Button extends javafx.scene.control.Button implements AutoCloseable {
    private Icon icon;
    private int iconSize = 18;
    private final javafx.scene.layout.StackPane iconBox = new javafx.scene.layout.StackPane();
    private final javafx.scene.control.Tooltip iconHint = new javafx.scene.control.Tooltip();
    private final Motion motion = new Motion(this);
    private final BooleanProperty loading = new SimpleBooleanProperty(this, "loading");
    public Button() { this(""); }
    public Button(String text) {
        super(text);
        getStyleClass().add("norm-button");
        iconBox.getStyleClass().add("norm-button-icon");
        iconHint.textProperty().bind(textProperty());
        setAlignment(javafx.geometry.Pos.CENTER);
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
    public void setIconLiteral(String literal) {
        String normalized = literal == null ? "" : literal;
        if (java.util.Objects.equals(getProperties().get("norm-button-icon"), normalized)) return;
        getProperties().put("norm-button-icon", normalized);
        icon = normalized.isBlank() ? null : new Icon(normalized);
        iconBox.getChildren().clear();
        if (icon != null) {
            icon.setBoundsType(javafx.scene.text.TextBoundsType.VISUAL);
            iconBox.getChildren().add(icon);
            setIconSize(iconSize);
        }
        setGraphic(icon == null ? null : iconBox);
        setGraphicTextGap(8);
    }
    public void setIconSize(int value) {
        iconSize = Math.max(1, value);
        if (icon != null) icon.setIconSize(iconSize);
        iconBox.setMinSize(iconSize + 2, iconSize + 2);
        iconBox.setPrefSize(iconSize + 2, iconSize + 2);
        iconBox.setMaxSize(iconSize + 2, iconSize + 2);
    }
    public void setSelected(boolean value) { pseudoClassStateChanged(PseudoClass.getPseudoClass("selected"), value); }
    public void setIconOnly(boolean value) {
        setContentDisplay(value ? javafx.scene.control.ContentDisplay.GRAPHIC_ONLY : javafx.scene.control.ContentDisplay.LEFT);
        setTooltip(value ? iconHint : null);
        setAccessibleText(value ? getText() : null);
        pseudoClassStateChanged(PseudoClass.getPseudoClass("icon-only"), value);
    }
    @Override public void close() { motion.close(); }
    @Override public void fire() { if (!isLoading()) super.fire(); }
}
