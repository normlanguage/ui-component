package dev.normlanguage.ui.component;

import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimePicker extends ComboBox<LocalTime> {
    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private int minuteStep;
    private LocalTime earliest = LocalTime.MIN;
    private LocalTime latest = LocalTime.MAX;
    public TimePicker() { this(15); }
    public TimePicker(int minuteStep) {
        setEditable(true);
        setConverter(new StringConverter<>() {
            @Override public String toString(LocalTime value) { return value == null ? "" : FORMAT.format(value); }
            @Override public LocalTime fromString(String value) {
                if (value == null || value.isBlank()) return null;
                var parsed = LocalTime.parse(value, FORMAT);
                if (parsed.isBefore(earliest) || parsed.isAfter(latest)) throw new IllegalArgumentException("Time outside allowed range");
                return parsed;
            }
        });
        setMinuteStep(minuteStep);
        getStyleClass().add("norm-time-picker");
    }
    public int getMinuteStep() { return minuteStep; }
    public void setMinuteStep(int step) {
        if (step < 1 || step > 24 * 60) throw new IllegalArgumentException("Minute step outside range");
        minuteStep = step;
        refreshOptions();
    }
    public void setAllowedRange(LocalTime earliest, LocalTime latest) {
        if (earliest == null || latest == null || earliest.isAfter(latest)) throw new IllegalArgumentException("Invalid time range");
        this.earliest = earliest;
        this.latest = latest;
        if (getValue() != null && (getValue().isBefore(earliest) || getValue().isAfter(latest))) setValue(null);
        refreshOptions();
    }
    public LocalTime getEarliest() { return earliest; }
    public LocalTime getLatest() { return latest; }
    private void refreshOptions() {
        getItems().clear();
        for (int minute = 0; minute < 24 * 60; minute += minuteStep) {
            var time = LocalTime.of(minute / 60, minute % 60);
            if (!time.isBefore(earliest) && !time.isAfter(latest)) getItems().add(time);
        }
    }
}
