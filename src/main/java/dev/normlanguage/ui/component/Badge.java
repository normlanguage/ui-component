package dev.normlanguage.ui.component;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;
public class Badge extends StackPane {
    private final IntegerProperty count = new SimpleIntegerProperty(this, "count");
    private final Label indicator = new Label();
    public Badge(Node content) {
        getStyleClass().add("norm-badge");
        getChildren().addAll(content, indicator);
        indicator.getStyleClass().add("norm-badge-indicator");
        StackPane.setAlignment(indicator, Pos.TOP_RIGHT);
        count.addListener((o,a,b) -> update());
        update();
    }
    private void update() { indicator.setText(Integer.toString(count.get())); indicator.setVisible(count.get() > 0); }
    public IntegerProperty countProperty() { return count; }
    public int getCount() { return count.get(); }
    public void setCount(int value) { count.set(value); }
}
