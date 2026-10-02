package dev.normlanguage.ui.component;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
public class Tag extends HBox {
    public static final EventType<Event> CLOSE = new EventType<>(Event.ANY, "NORM_TAG_CLOSE");
    private final Label label;
    private final Button close = new Button("×");
    private final BooleanProperty closable = new SimpleBooleanProperty(this, "closable");
    private Runnable onClose;
    public Tag(String text) {
        getStyleClass().add("norm-tag");
        label = new Label(text);
        getChildren().addAll(label, close);
        close.visibleProperty().bind(closable);
        close.managedProperty().bind(closable);
        close.setOnAction(event -> {
            fireEvent(new Event(CLOSE));
            if (onClose != null) onClose.run();
        });
    }
    public String getText() { return label.getText(); }
    public void setText(String text) { label.setText(text); }
    public boolean isClosable() { return closable.get(); }
    public void setClosable(boolean value) { closable.set(value); }
    public BooleanProperty closableProperty() { return closable; }
    public void setOnClose(Runnable action) { onClose = action; }
}
