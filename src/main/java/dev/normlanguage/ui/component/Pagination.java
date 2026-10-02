package dev.normlanguage.ui.component;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class Pagination extends HBox {
    private final IntegerProperty totalItems = new SimpleIntegerProperty(this, "totalItems") {
        @Override public void set(int value) {
            if (value < 0) throw new IllegalArgumentException("Total cannot be negative");
            super.set(value);
        }
    };
    private final IntegerProperty pageSize = new SimpleIntegerProperty(this, "pageSize", 10) {
        @Override public void set(int value) {
            if (value < 1) throw new IllegalArgumentException("Page size must be positive");
            super.set(value);
        }
    };
    private final IntegerProperty currentPage = new SimpleIntegerProperty(this, "currentPage", 1) {
        @Override public void set(int value) { super.set(Math.clamp(value, 1, getPageCount())); }
    };
    private final Button previous = new Button("‹");
    private final Button following = new Button("›");
    private final Label summary = new Label();

    public Pagination(int totalItems, int pageSize) {
        super(8);
        getStyleClass().add("norm-pagination");
        previous.setOnAction(event -> previous());
        following.setOnAction(event -> next());
        getChildren().addAll(previous, summary, following);
        this.totalItems.addListener(observable -> refresh());
        this.pageSize.addListener(observable -> refresh());
        this.currentPage.addListener(observable -> refresh());
        setPageSize(pageSize);
        setTotalItems(totalItems);
        refresh();
    }
    public IntegerProperty totalItemsProperty() { return totalItems; }
    public int getTotalItems() { return totalItems.get(); }
    public void setTotalItems(int value) {
        totalItems.set(value);
    }
    public IntegerProperty pageSizeProperty() { return pageSize; }
    public int getPageSize() { return pageSize.get(); }
    public void setPageSize(int value) {
        pageSize.set(value);
    }
    public IntegerProperty currentPageProperty() { return currentPage; }
    public int getCurrentPage() { return currentPage.get(); }
    public void setCurrentPage(int value) { currentPage.set(value); }
    public int getPageCount() { return Math.max(1, (int) ((getTotalItems() + (long) getPageSize() - 1) / getPageSize())); }
    public void first() { setCurrentPage(1); }
    public void previous() { setCurrentPage(getCurrentPage() - 1); }
    public void next() { setCurrentPage(getCurrentPage() + 1); }
    public void last() { setCurrentPage(getPageCount()); }
    private void refresh() {
        int page = Math.clamp(getCurrentPage(), 1, getPageCount());
        if (currentPage.get() != page) { currentPage.set(page); return; }
        summary.setText(page + " / " + getPageCount());
        previous.setDisable(page == 1);
        following.setDisable(page == getPageCount());
    }
}
