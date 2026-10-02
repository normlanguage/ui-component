package dev.normlanguage.ui.component;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.text.NumberFormat;
public class Statistic extends VBox implements AutoCloseable {
    private final Label title = new Label();
    private final Label display = new Label();
    private final ObjectProperty<Number> value = new SimpleObjectProperty<>(this, "value", 0) {
        @Override public void set(Number number) { super.set(java.util.Objects.requireNonNull(number)); }
    };
    private final ConfigurationConnection configuration = new ConfigurationConnection(this, this::render);
    private NumberFormat format;
    public Statistic(String title, Number initial) {
        getStyleClass().add("norm-statistic");
        this.title.setText(title);
        this.title.getStyleClass().add("norm-statistic-title");
        display.getStyleClass().add("norm-statistic-value");
        getChildren().addAll(this.title, display);
        value.addListener((o,a,b) -> render());
        setValue(initial);
        configuration.connect();
    }
    public ObjectProperty<Number> valueProperty() { return value; }
    public Number getValue() { return value.get(); }
    public void setValue(Number number) { value.set(number); }
    public void setFormat(NumberFormat format) { this.format = format; render(); }
    private void render() {
        var activeFormat = format == null
                ? NumberFormat.getNumberInstance(ConfigurationConnection.resolve(this).locale()) : format;
        display.setText(activeFormat.format(getValue()));
    }
    @Override public void close() { configuration.close(); }
}
