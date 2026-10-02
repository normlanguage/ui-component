package dev.normlanguage.ui.component;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Grid extends GridPane {
    private record Item(Node node, int span) {}
    private final ArrayList<Item> items = new ArrayList<>();
    private final NavigableMap<Double, Integer> responsiveColumns = new TreeMap<>();

    public Grid() {
        getStyleClass().add("norm-grid");
        setHgap(8);
        setVgap(8);
        responsiveColumns.put(0.0, 1);
        widthProperty().addListener(observable -> reflow());
        getChildren().addListener((javafx.collections.ListChangeListener<Node>) change -> items.removeIf(item -> !getChildren().contains(item.node())));
    }
    public void add(Node child, int column, int row, int columnSpan, int rowSpan) {
        super.add(child, column, row, columnSpan, rowSpan);
    }
    public void setResponsiveColumns(double minimumWidth, int columns) {
        if (minimumWidth < 0 || !Double.isFinite(minimumWidth)) throw new IllegalArgumentException("Minimum width must be finite and nonnegative");
        if (columns < 1) throw new IllegalArgumentException("Columns must be positive");
        responsiveColumns.put(minimumWidth, columns);
        reflow();
    }
    public void addItem(Node child, int span) {
        if (span < 1) throw new IllegalArgumentException("Span must be positive");
        getChildren().add(child);
        items.add(new Item(child, span));
        reflow();
    }
    public int getCurrentColumns() { return responsiveColumns.floorEntry(Math.max(0, getWidth())).getValue(); }
    private void reflow() {
        int columns = getCurrentColumns();
        int column = 0;
        int row = 0;
        for (var item : items) {
            int span = Math.min(columns, item.span());
            if (column + span > columns) { row++; column = 0; }
            GridPane.setColumnIndex(item.node(), column);
            GridPane.setRowIndex(item.node(), row);
            GridPane.setColumnSpan(item.node(), span);
            column += span;
            if (column == columns) { row++; column = 0; }
        }
    }
}
