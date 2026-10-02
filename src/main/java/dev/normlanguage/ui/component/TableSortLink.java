package dev.normlanguage.ui.component;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.scene.control.TableColumn;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

public final class TableSortLink<T> implements AutoCloseable {
    private final Table<T> table;
    private final ReadOnlyObjectWrapper<TableSortState> current = new ReadOnlyObjectWrapper<>();
    private final SelectionLink<TableSortState> delegate;
    private final ListChangeListener<TableColumn<T,?>> orderChanged = change -> observe();
    private final ChangeListener<TableColumn.SortType> directionChanged = (observable, previous, next) -> publish();
    private TableColumn<T,?> observed;
    private boolean suspended;
    private boolean closed;

    public TableSortLink(Table<T> table) {
        Util.requireFxThread();
        this.table = Objects.requireNonNull(table);
        delegate = new SelectionLink<>(table, current.getReadOnlyProperty(), this::apply);
        table.getSortOrder().addListener(orderChanged);
        observe();
    }

    public void columns(List<String> headings, Function<T,List<String>> values) {
        Util.requireFxThread();
        if (closed) throw new IllegalStateException("Sort link is closed");
        suspended = true;
        try { DisplayAdapter.tableColumns(table, headings, values); }
        finally { suspended = false; }
        observe();
    }

    public void update(TableSortState sort, Consumer<TableSortState> changed) {
        delegate.update(sort, changed);
    }

    public void unbind() { delegate.unbind(); }

    private void apply(TableSortState sort) {
        if (sort == null) {
            table.getSortOrder().clear();
            return;
        }
        for (var column : table.getColumns()) {
            if (Objects.equals(column.getText(), sort.heading())) {
                column.setSortType(sort.descending() ? TableColumn.SortType.DESCENDING : TableColumn.SortType.ASCENDING);
                table.getSortOrder().setAll(column);
                table.sort();
                return;
            }
        }
        throw new IllegalArgumentException("Unknown sort column: " + sort.heading());
    }

    private void observe() {
        if (observed != null) observed.sortTypeProperty().removeListener(directionChanged);
        observed = table.getSortOrder().isEmpty() ? null : table.getSortOrder().getFirst();
        if (observed != null) observed.sortTypeProperty().addListener(directionChanged);
        publish();
    }

    private void publish() {
        if (suspended) return;
        var next = observed == null ? null : new TableSortState(observed.getText(),
                observed.getSortType() == TableColumn.SortType.DESCENDING);
        if (!Objects.equals(current.get(), next)) current.set(next);
    }

    @Override public void close() {
        Util.requireFxThread();
        if (closed) return;
        closed = true;
        table.getSortOrder().removeListener(orderChanged);
        if (observed != null) observed.sortTypeProperty().removeListener(directionChanged);
        observed = null;
        delegate.close();
    }
}
