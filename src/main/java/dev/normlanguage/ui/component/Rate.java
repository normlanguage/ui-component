package dev.normlanguage.ui.component;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;

public class Rate extends HBox {
    private final RatingProperty value;
    private final int maximum;
    public Rate() { this(5); }
    public Rate(int maximum) {
        if (maximum < 1) throw new IllegalArgumentException("maximum must be positive");
        this.maximum = maximum;
        value = new RatingProperty();
        for (int rating = 1; rating <= maximum; rating++) {
            final int selected = rating;
            var star = new ToggleButton("★");
            star.setAccessibleText("Rate " + rating + " of " + maximum);
            star.setOnAction(event -> {
                if (value.isBound()) star.setSelected(selected <= value.get());
                else setValue(selected);
            });
            getChildren().add(star);
        }
        value.addListener((observable, old, current) -> {
            for (int index = 0; index < maximum; index++)
                ((ToggleButton) getChildren().get(index)).setSelected(index < current.intValue());
        });
        getStyleClass().add("norm-rate");
    }
    public IntegerProperty valueProperty() { return value; }
    public int getValue() { return value.get(); }
    public void setValue(int rating) { value.set(rating); }
    private final class RatingProperty extends SimpleIntegerProperty {
        private ObservableValue<? extends Number> source;
        private boolean writingBinding;
        private final ChangeListener<Number> sourceChanged = (observable, old, rating) -> accept(rating == null ? 0 : rating.intValue());
        private final WeakChangeListener<Number> weakSourceChanged = new WeakChangeListener<>(sourceChanged);
        private RatingProperty() { super(Rate.this, "value", 0); }
        private void accept(int rating) {
            if (rating < 0 || rating > maximum) throw new IllegalArgumentException("Rating outside range");
            writingBinding = true;
            try { super.set(rating); }
            finally { writingBinding = false; }
        }
        @Override public void set(int rating) {
            if (isBound()) throw new IllegalStateException("A bound rating cannot be set directly");
            accept(rating);
        }
        @Override public void bind(ObservableValue<? extends Number> source) {
            if (this.source == source) return;
            if (source == null) throw new NullPointerException("source");
            Number current = source.getValue();
            int initial = current == null ? 0 : current.intValue();
            if (initial < 0 || initial > maximum) throw new IllegalArgumentException("Rating outside range");
            unbind();
            this.source = source;
            source.addListener(weakSourceChanged);
            accept(initial);
        }
        @Override public void unbind() {
            if (source == null) return;
            source.removeListener(weakSourceChanged);
            source = null;
        }
        @Override public boolean isBound() { return source != null && !writingBinding; }
    }
}
