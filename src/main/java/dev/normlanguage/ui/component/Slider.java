package dev.normlanguage.ui.component;

public class Slider extends javafx.scene.control.Slider {
    public Slider() { getStyleClass().add("norm-slider"); }
    public Slider(double min, double max, double value) { super(min, max, value); getStyleClass().add("norm-slider"); }
    public static class Range extends javafx.scene.layout.HBox {
        private final Slider start;
        private final Slider end;
        private boolean updating;
        public Range(double minimum, double maximum, double startValue, double endValue) {
            if (!(minimum <= startValue && startValue <= endValue && endValue <= maximum))
                throw new IllegalArgumentException("Invalid range");
            start = new Slider(minimum, maximum, startValue);
            end = new Slider(minimum, maximum, endValue);
            start.setAccessibleText("Range start");
            end.setAccessibleText("Range end");
            start.valueProperty().addListener((observable, old, value) -> {
                if (!updating && value.doubleValue() > end.getValue()) start.setValue(end.getValue());
            });
            end.valueProperty().addListener((observable, old, value) -> {
                if (!updating && value.doubleValue() < start.getValue()) end.setValue(start.getValue());
            });
            setSpacing(8);
            getChildren().addAll(start, end);
            getStyleClass().add("norm-range-slider");
        }
        public Slider getStartSlider() { return start; }
        public Slider getEndSlider() { return end; }
        public double getStart() { return start.getValue(); }
        public double getEnd() { return end.getValue(); }
        public void setRange(double from, double to) {
            if (!(start.getMin() <= from && from <= to && to <= start.getMax()))
                throw new IllegalArgumentException("Invalid range");
            updating = true;
            start.setValue(from);
            end.setValue(to);
            updating = false;
        }
    }
}
