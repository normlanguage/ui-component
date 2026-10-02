package dev.normlanguage.ui.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.DateCell;
import javafx.scene.layout.HBox;

import java.time.LocalDate;

public class DatePicker extends javafx.scene.control.DatePicker {
    public DatePicker() { getStyleClass().add("norm-date-picker"); }
    public record DateRange(LocalDate start, LocalDate end) {
        public DateRange {
            if (start != null && end != null && start.isAfter(end)) throw new IllegalArgumentException("Start is after end");
        }
    }
    public static class Range extends HBox {
        private final DatePicker start = new DatePicker();
        private final DatePicker end = new DatePicker();
        private final ObjectProperty<DateRange> value = new SimpleObjectProperty<>(this, "value", new DateRange(null, null));
        private boolean updating;
        public Range() {
            setSpacing(8);
            start.setPromptText("Start date");
            end.setPromptText("End date");
            start.setDayCellFactory(picker -> new DateCell() {
                @Override public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(!empty && end.getValue() != null && date.isAfter(end.getValue()));
                }
            });
            end.setDayCellFactory(picker -> new DateCell() {
                @Override public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    setDisable(!empty && start.getValue() != null && date.isBefore(start.getValue()));
                }
            });
            start.valueProperty().addListener((observable, old, selected) -> {
                if (updating) return;
                if (selected != null && end.getValue() != null && selected.isAfter(end.getValue())) end.setValue(selected);
                value.set(new DateRange(selected, end.getValue()));
            });
            end.valueProperty().addListener((observable, old, selected) -> {
                if (updating) return;
                if (selected != null && start.getValue() != null && selected.isBefore(start.getValue())) start.setValue(selected);
                value.set(new DateRange(start.getValue(), selected));
            });
            value.addListener((observable, old, range) -> {
                if (updating) return;
                if (range == null) { value.set(old); throw new IllegalArgumentException("Range cannot be null"); }
                updating = true;
                try {
                    start.setValue(range.start());
                    end.setValue(range.end());
                } finally { updating = false; }
            });
            getChildren().addAll(start, end);
            getStyleClass().add("norm-date-range-picker");
        }
        public DatePicker getStartPicker() { return start; }
        public DatePicker getEndPicker() { return end; }
        public ObjectProperty<DateRange> valueProperty() { return value; }
        public DateRange getValue() { return value.get(); }
        public void setValue(DateRange range) {
            if (range == null) throw new IllegalArgumentException("Range cannot be null");
            value.set(range);
        }
    }
}
