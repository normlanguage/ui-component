package dev.normlanguage.ui.component;

import javafx.beans.property.Property;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleButton;
import java.util.function.Consumer;

public final class ValueLink<T> implements AutoCloseable {
    private final SelectionLink<T> delegate;

    public ValueLink(Node node, Property<T> property) {
        delegate = new SelectionLink<>(node, property, property::setValue);
    }
    public static ValueLink<String> text(TextInputControl node) { return new ValueLink<>(node, node.textProperty()); }
    public static ValueLink<Boolean> checked(CheckBox node) { return new ValueLink<>(node, node.selectedProperty()); }
    public static ValueLink<Boolean> toggled(ToggleButton node) { return new ValueLink<>(node, node.selectedProperty()); }
    public Node node() { return delegate.node(); }
    public void update(T value, Consumer<T> changed) {
        delegate.update(value, changed);
    }
    public void unbind() { delegate.unbind(); }
    @Override public void close() {
        delegate.close();
    }
}
