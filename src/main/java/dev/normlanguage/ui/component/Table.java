package dev.normlanguage.ui.component;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.BiConsumer;
public class Table<T> extends TableView<T> {
    private ObservableList<T> source;
    private FilteredList<T> filtered;
    private SortedList<T> sorted;
    public Table() { getStyleClass().add("norm-table"); }
    public void setSource(ObservableList<T> rows) {
        if (sorted != null) sorted.comparatorProperty().unbind();
        source = java.util.Objects.requireNonNull(rows);
        filtered = new FilteredList<>(rows);
        sorted = new SortedList<>(filtered);
        sorted.comparatorProperty().bind(comparatorProperty());
        setItems(sorted);
    }
    public ObservableList<T> getSource() { return source; }
    public void setPredicate(Predicate<T> predicate) {
        if (filtered == null) throw new IllegalStateException("Set source before filter");
        filtered.setPredicate(predicate);
    }
    public <V> TableColumn<T,V> observableColumn(String heading, Function<T,ObservableValue<V>> value) {
        var column = new TableColumn<T,V>(heading);
        column.setCellValueFactory(cell -> value.apply(cell.getValue()));
        getColumns().add(column);
        return column;
    }
    public TableColumn<T,String> editableColumn(String heading, Function<T,ObservableValue<String>> value, BiConsumer<T,String> commit) {
        var column = observableColumn(heading, value);
        column.setCellFactory(TextFieldTableCell.forTableColumn());
        column.setEditable(true);
        setEditable(true);
        column.setOnEditCommit(event -> commit.accept(event.getRowValue(), event.getNewValue()));
        return column;
    }
    public <V> TableColumn<T,V> column(String heading, Function<T,V> value) {
        var column = new TableColumn<T,V>(heading);
        column.setCellValueFactory(cell -> new ReadOnlyObjectWrapper<>(value.apply(cell.getValue())));
        getColumns().add(column);
        return column;
    }
}
