package dev.normlanguage.ui.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;

import java.util.Objects;

public class ColorPicker extends HBox {
    private final javafx.scene.control.ColorPicker picker = new javafx.scene.control.ColorPicker();
    private final javafx.scene.control.Slider alpha = new javafx.scene.control.Slider(0, 1, 1);
    private final ObjectProperty<Color> value = new SimpleObjectProperty<>(this, "value", Color.WHITE) {
        @Override public void set(Color color) { super.set(Objects.requireNonNull(color)); }
    };
    private boolean updating;
    public ColorPicker() {
        alpha.setAccessibleText("Opacity");
        alpha.setShowTickMarks(true);
        picker.valueProperty().addListener((observable, old, color) -> {
            if (!updating && color != null) value.set(Color.color(color.getRed(), color.getGreen(), color.getBlue(), alpha.getValue()));
        });
        alpha.valueProperty().addListener((observable, old, amount) -> {
            if (!updating) {
                Color color = picker.getValue();
                value.set(Color.color(color.getRed(), color.getGreen(), color.getBlue(), amount.doubleValue()));
            }
        });
        value.addListener((observable, old, color) -> {
            updating = true;
            picker.setValue(Color.color(color.getRed(), color.getGreen(), color.getBlue()));
            alpha.setValue(color.getOpacity());
            updating = false;
        });
        getChildren().addAll(picker, alpha);
        getStyleClass().add("norm-color-picker");
    }
    public ObjectProperty<Color> valueProperty() { return value; }
    public Color getValue() { return value.get(); }
    public void setValue(Color color) { value.set(Objects.requireNonNull(color)); }
    public javafx.scene.control.ColorPicker getNativePicker() { return picker; }
    public javafx.scene.control.Slider getAlphaSlider() { return alpha; }
}
