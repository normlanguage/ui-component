package dev.normlanguage.ui.component;

import javafx.beans.property.Property;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.VBox;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

public class Form extends VBox {
    public static final class Field<T> {
        private final String name;
        private final Node control;
        private final Property<T> value;
        private final T initial;
        private final Predicate<T> validator;
        private Field(String name, Node control, Property<T> value, Predicate<T> validator) {
            this.name = name;
            this.control = control;
            this.value = value;
            this.initial = value.getValue();
            this.validator = validator;
        }
        public String getName() { return name; }
        public Node getControl() { return control; }
        public Property<T> valueProperty() { return value; }
        public T getValue() { return value.getValue(); }
        public boolean validate() {
            boolean valid = validator.test(value.getValue());
            control.pseudoClassStateChanged(PseudoClass.getPseudoClass("invalid"), !valid);
            return valid;
        }
        public void reset() {
            value.setValue(initial);
            control.pseudoClassStateChanged(PseudoClass.getPseudoClass("invalid"), false);
        }
    }
    private final Map<String, Field<?>> fields = new LinkedHashMap<>();
    private Runnable onSubmit = () -> {};
    public Form() { getStyleClass().add("norm-form"); }
    public Field<String> addField(String name, TextInputControl control, Predicate<String> validator) {
        return addField(name, control, control.textProperty(), validator);
    }
    public <T> Field<T> addField(String name, Node control, Property<T> value, Predicate<T> validator) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(control);
        Objects.requireNonNull(value);
        Objects.requireNonNull(validator);
        if (fields.containsKey(name)) throw new IllegalArgumentException("Duplicate field: " + name);
        var field = new Field<>(name, control, value, validator);
        fields.put(name, field);
        getChildren().add(control);
        return field;
    }
    public Map<String, Field<?>> getFields() { return java.util.Collections.unmodifiableMap(fields); }
    public boolean validate() {
        boolean valid = true;
        for (Field<?> field : fields.values()) valid &= field.validate();
        return valid;
    }
    public void setOnSubmit(Runnable action) { onSubmit = Objects.requireNonNull(action); }
    public boolean submit() { if (!validate()) return false; onSubmit.run(); return true; }
    public void reset() { fields.values().forEach(Field::reset); }
}
