package dev.normlanguage.ui.component;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Node;
import javafx.geometry.Orientation;
import javafx.scene.layout.Pane;

import java.util.Arrays;

public class Masonry extends Pane {
    private final DoubleProperty minimumColumnWidth = new SimpleDoubleProperty(this, "minimumColumnWidth", 180) {
        @Override public void set(double width) {
            if (!(width > 0) || !Double.isFinite(width))
                throw new IllegalArgumentException("Column width must be positive and finite");
            super.set(width);
        }
    };
    private final DoubleProperty gap = new SimpleDoubleProperty(this, "gap", 8) {
        @Override public void set(double value) {
            if (value < 0 || !Double.isFinite(value))
                throw new IllegalArgumentException("Gap must be nonnegative and finite");
            super.set(value);
        }
    };

    public Masonry() {
        getStyleClass().add("norm-masonry");
        minimumColumnWidth.addListener(observable -> requestLayout());
        gap.addListener(observable -> requestLayout());
    }
    public DoubleProperty minimumColumnWidthProperty() { return minimumColumnWidth; }
    public double getMinimumColumnWidth() { return minimumColumnWidth.get(); }
    public void setMinimumColumnWidth(double width) {
        minimumColumnWidth.set(width);
    }
    public DoubleProperty gapProperty() { return gap; }
    public double getGap() { return gap.get(); }
    public void setGap(double value) {
        gap.set(value);
    }
    @Override public Orientation getContentBias() { return Orientation.HORIZONTAL; }
    @Override protected double computePrefHeight(double width) { return measure(width, false); }
    @Override protected double computePrefWidth(double height) { return getMinimumColumnWidth() + snappedLeftInset() + snappedRightInset(); }
    @Override protected void layoutChildren() { measure(getWidth(), true); }

    private double measure(double width, boolean place) {
        double available = Math.max(0, width - snappedLeftInset() - snappedRightInset());
        int columns = Math.max(1, (int) Math.floor((available + getGap()) / (getMinimumColumnWidth() + getGap())));
        double cellWidth = Math.max(0, (available - (columns - 1) * getGap()) / columns);
        double[] heights = new double[columns];
        for (Node child : getManagedChildren()) {
            int column = 0;
            for (int i = 1; i < columns; i++) if (heights[i] < heights[column]) column = i;
            double childHeight = child.prefHeight(cellWidth);
            if (place) child.resizeRelocate(snappedLeftInset() + column * (cellWidth + getGap()),
                    snappedTopInset() + heights[column], cellWidth, childHeight);
            heights[column] += childHeight + getGap();
        }
        return Arrays.stream(heights).max().orElse(0) - (getManagedChildren().isEmpty() ? 0 : getGap())
                + snappedTopInset() + snappedBottomInset();
    }
}
