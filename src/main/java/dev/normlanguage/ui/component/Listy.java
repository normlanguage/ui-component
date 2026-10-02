package dev.normlanguage.ui.component;
public class Listy<T> extends List<T> {
    public Listy() {
        getStyleClass().add("norm-listy");
        setFixedCellSize(32);
    }
    public void setRowHeight(double height) {
        if (height <= 0) throw new IllegalArgumentException("row height");
        setFixedCellSize(height);
    }
}
