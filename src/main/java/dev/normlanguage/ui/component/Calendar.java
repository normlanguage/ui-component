package dev.normlanguage.ui.component;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.util.function.Function;

public class Calendar extends BorderPane implements AutoCloseable {
    private final ObjectProperty<LocalDate> value = new SimpleObjectProperty<>(this, "value", LocalDate.now());
    private final ObjectProperty<YearMonth> displayedMonth = new SimpleObjectProperty<>(this, "displayedMonth", YearMonth.now()) {
        @Override public void set(YearMonth month) { super.set(java.util.Objects.requireNonNull(month)); }
    };
    private final GridPane days = new GridPane();
    private final Label heading = new Label();
    private final ConfigurationConnection configuration = new ConfigurationConnection(this, this::render);
    private Function<LocalDate, Node> dayContentFactory;

    public Calendar() {
        getStyleClass().add("norm-calendar");
        var previous = new Button("‹");
        var next = new Button("›");
        previous.setOnAction(event -> previousMonth());
        next.setOnAction(event -> nextMonth());
        var header = new HBox(previous, heading, next);
        header.getStyleClass().add("norm-calendar-header");
        setTop(header);
        days.getStyleClass().add("norm-calendar-days");
        setCenter(days);
        value.addListener((o,a,b) -> render());
        displayedMonth.addListener((o,a,b) -> render());
        configuration.connect();
    }

    public ObjectProperty<LocalDate> valueProperty() { return value; }
    public LocalDate getValue() { return value.get(); }
    public void setValue(LocalDate selected) {
        value.set(selected);
        if (selected != null) displayedMonth.set(YearMonth.from(selected));
    }
    public ObjectProperty<YearMonth> displayedMonthProperty() { return displayedMonth; }
    public YearMonth getDisplayedMonth() { return displayedMonth.get(); }
    public void setDisplayedMonth(YearMonth month) { displayedMonth.set(java.util.Objects.requireNonNull(month)); }
    public void previousMonth() { displayedMonth.set(displayedMonth.get().minusMonths(1)); }
    public void nextMonth() { displayedMonth.set(displayedMonth.get().plusMonths(1)); }
    public void setDayContentFactory(Function<LocalDate, Node> factory) { dayContentFactory = factory; render(); }

    private void render() {
        var month = displayedMonth.get();
        Locale locale = ConfigurationConnection.resolve(this).locale();
        heading.setText(month.format(DateTimeFormatter.ofPattern("LLLL yyyy", locale)));
        days.getChildren().clear();
        int firstWeekday = WeekFields.of(locale).getFirstDayOfWeek().getValue();
        for (int column = 0; column < 7; column++) {
            var weekday = DayOfWeek.of((firstWeekday - 1 + column) % 7 + 1);
            days.add(new Label(weekday.getDisplayName(TextStyle.SHORT, locale)), column, 0);
        }
        var first = month.atDay(1);
        var start = first.minusDays(Math.floorMod(first.getDayOfWeek().getValue() - firstWeekday, 7));
        for (int offset = 0; offset < 42; offset++) {
            var date = start.plusDays(offset);
            var day = new Button(Integer.toString(date.getDayOfMonth()));
            day.getStyleClass().add("norm-calendar-day");
            day.setOnAction(event -> setValue(date));
            if (!YearMonth.from(date).equals(month)) day.getStyleClass().add("outside-month");
            if (date.equals(value.get())) day.getStyleClass().add("selected");
            var content = dayContentFactory == null ? null : dayContentFactory.apply(date);
            days.add(content == null ? day : new VBox(day, content), offset % 7, offset / 7 + 1);
        }
    }
    @Override public void close() { configuration.close(); }
}
