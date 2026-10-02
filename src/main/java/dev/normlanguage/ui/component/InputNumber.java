package dev.normlanguage.ui.component;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.css.PseudoClass;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class InputNumber extends Spinner<BigDecimal> {
    private final BigDecimal minimum;
    private final BigDecimal maximum;
    private final BigDecimal step;
    private final ReadOnlyBooleanWrapper draftValid = new ReadOnlyBooleanWrapper(this, "draftValid", true);
    private int scale = -1;
    public InputNumber() { this(new BigDecimal("-1E+100"), new BigDecimal("1E+100"), BigDecimal.ZERO, BigDecimal.ONE); }
    public InputNumber(double minimum, double maximum, double initial, double step) {
        this(BigDecimal.valueOf(minimum), BigDecimal.valueOf(maximum), BigDecimal.valueOf(initial), BigDecimal.valueOf(step));
    }
    public InputNumber(BigDecimal minimum, BigDecimal maximum, BigDecimal initial, BigDecimal step) {
        this.minimum = Objects.requireNonNull(minimum);
        this.maximum = Objects.requireNonNull(maximum);
        this.step = Objects.requireNonNull(step);
        Objects.requireNonNull(initial);
        if (minimum.compareTo(maximum) > 0 || initial.compareTo(minimum) < 0 || initial.compareTo(maximum) > 0 || step.signum() <= 0)
            throw new IllegalArgumentException("Invalid numeric bounds, initial value, or step");
        setValueFactory(new DecimalValueFactory(initial));
        setEditable(true);
        getEditor().setTextFormatter(new TextFormatter<String>(change ->
            change.getControlNewText().matches("-?(?:\\d*)(?:\\.\\d*)?") ? change : null));
        getEditor().textProperty().addListener((observable, old, text) -> setDraftValid(true));
        getEditor().setOnAction(event -> commitEditor());
        getEditor().focusedProperty().addListener((observable, old, focused) -> {
            if (!focused) {
                commitEditor();
                if (!isDraftValid()) {
                    getEditor().setText(getValueFactory().getConverter().toString(getValue()));
                    setDraftValid(true);
                }
            }
        });
        getStyleClass().add("norm-input-number");
    }
    public BigDecimal getMinimum() { return minimum; }
    public BigDecimal getMaximum() { return maximum; }
    public BigDecimal getStep() { return step; }
    public int getScale() { return scale; }
    public void setScale(int digits) {
        if (digits < 0) throw new IllegalArgumentException("Scale must be nonnegative");
        var rounded = getValue().setScale(digits, RoundingMode.HALF_UP);
        if (rounded.compareTo(minimum) < 0 || rounded.compareTo(maximum) > 0)
            throw new IllegalArgumentException("Rounded value outside bounds");
        scale = digits;
        setValue(rounded);
    }
    public ReadOnlyBooleanProperty draftValidProperty() { return draftValid.getReadOnlyProperty(); }
    public boolean isDraftValid() { return draftValid.get(); }
    public void setValue(double value) { setValue(BigDecimal.valueOf(value)); }
    public void setValue(BigDecimal value) {
        Objects.requireNonNull(value);
        BigDecimal normalized = scale < 0 ? value : value.setScale(scale, RoundingMode.HALF_UP);
        if (normalized.compareTo(minimum) < 0 || normalized.compareTo(maximum) > 0)
            throw new IllegalArgumentException("Value outside bounds");
        getValueFactory().setValue(normalized);
        getEditor().setText(normalized.toPlainString());
        setDraftValid(true);
    }
    private void setDraftValid(boolean valid) {
        draftValid.set(valid);
        pseudoClassStateChanged(PseudoClass.getPseudoClass("invalid"), !valid);
    }
    private void commitEditor() {
        try { setValue(getValueFactory().getConverter().fromString(getEditor().getText())); }
        catch (RuntimeException invalid) { setDraftValid(false); }
    }
    private final class DecimalValueFactory extends SpinnerValueFactory<BigDecimal> {
        private DecimalValueFactory(BigDecimal initial) {
            setConverter(new StringConverter<>() {
                @Override public String toString(BigDecimal number) { return number == null ? "" : number.toPlainString(); }
                @Override public BigDecimal fromString(String text) {
                    BigDecimal parsed = new BigDecimal(text);
                    BigDecimal normalized = scale < 0 ? parsed : parsed.setScale(scale, RoundingMode.HALF_UP);
                    if (normalized.compareTo(minimum) < 0 || normalized.compareTo(maximum) > 0)
                        throw new IllegalArgumentException("Value outside bounds");
                    return normalized;
                }
            });
            setValue(initial);
        }
        @Override public void decrement(int steps) {
            BigDecimal next = getValue().subtract(step.multiply(BigDecimal.valueOf(steps)));
            setValue(next.compareTo(minimum) < 0 ? minimum : scaled(next));
        }
        @Override public void increment(int steps) {
            BigDecimal next = getValue().add(step.multiply(BigDecimal.valueOf(steps)));
            setValue(next.compareTo(maximum) > 0 ? maximum : scaled(next));
        }
        private BigDecimal scaled(BigDecimal value) { return scale < 0 ? value : value.setScale(scale, RoundingMode.HALF_UP); }
    }
}
