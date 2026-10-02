package dev.normlanguage.ui.component;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;

import java.util.IdentityHashMap;
import java.util.Map;

public class Flex extends FlowPane {
    private final BooleanProperty wrap = new SimpleBooleanProperty(this, "wrap");
    private final Map<Node, Double> grow = new IdentityHashMap<>();

    public Flex(Node... children) {
        super(children);
        getStyleClass().add("norm-flex");
        setHgap(8);
        setVgap(8);
        wrap.addListener(observable -> requestLayout());
        getChildren().addListener((javafx.collections.ListChangeListener<Node>) change -> grow.keySet().retainAll(getChildren()));
    }
    public Flex(Orientation orientation, Node... children) {
        this(children);
        setOrientation(orientation);
    }
    public BooleanProperty wrapProperty() { return wrap; }
    public boolean isWrap() { return wrap.get(); }
    public void setWrap(boolean value) { wrap.set(value); }
    public void setGrow(Node child, double weight) {
        if (!getChildren().contains(child)) throw new IllegalArgumentException("Child must belong to this flex layout");
        if (weight < 0 || !Double.isFinite(weight)) throw new IllegalArgumentException("Grow weight must be finite and nonnegative");
        if (weight == 0) grow.remove(child); else grow.put(child, weight);
        requestLayout();
    }
    public double getGrow(Node child) { return grow.getOrDefault(child, 0.0); }
    @Override protected void layoutChildren() {
        if (isWrap()) { super.layoutChildren(); return; }
        var children = getManagedChildren();
        boolean horizontal = getOrientation() == Orientation.HORIZONTAL;
        double main = horizontal ? getWidth() - snappedLeftInset() - snappedRightInset()
                : getHeight() - snappedTopInset() - snappedBottomInset();
        double cross = Math.max(0, horizontal ? getHeight() - snappedTopInset() - snappedBottomInset()
                : getWidth() - snappedLeftInset() - snappedRightInset());
        double gap = horizontal ? getHgap() : getVgap();
        double base = Math.max(0, children.size() - 1) * gap;
        double weight = 0;
        for (var child : children) {
            base += horizontal ? child.prefWidth(-1) : child.prefHeight(-1);
            weight += getGrow(child);
        }
        double extra = Math.max(0, main - base);
        double position = horizontal ? snappedLeftInset() : snappedTopInset();
        if (weight == 0) {
            if (horizontal) {
                if (getAlignment().getHpos() == javafx.geometry.HPos.CENTER) position += extra / 2;
                else if (getAlignment().getHpos() == javafx.geometry.HPos.RIGHT) position += extra;
            } else {
                if (getAlignment().getVpos() == javafx.geometry.VPos.CENTER) position += extra / 2;
                else if (getAlignment().getVpos() == javafx.geometry.VPos.BOTTOM) position += extra;
            }
        }
        for (var child : children) {
            double mainSize = (horizontal ? child.prefWidth(-1) : child.prefHeight(-1))
                    + (weight == 0 ? 0 : extra * getGrow(child) / weight);
            double crossSize = Math.min(cross, horizontal ? child.prefHeight(mainSize) : child.prefWidth(mainSize));
            double crossOffset = horizontal ? snappedTopInset() : snappedLeftInset();
            var alignment = getAlignment();
            if (horizontal) {
                if (alignment.getVpos() == javafx.geometry.VPos.CENTER) crossOffset += (cross - crossSize) / 2;
                else if (alignment.getVpos() == javafx.geometry.VPos.BOTTOM) crossOffset += cross - crossSize;
                child.resizeRelocate(position, crossOffset, mainSize, crossSize);
            } else {
                if (alignment.getHpos() == javafx.geometry.HPos.CENTER) crossOffset += (cross - crossSize) / 2;
                else if (alignment.getHpos() == javafx.geometry.HPos.RIGHT) crossOffset += cross - crossSize;
                child.resizeRelocate(crossOffset, position, crossSize, mainSize);
            }
            position += mainSize + gap;
        }
    }
}
