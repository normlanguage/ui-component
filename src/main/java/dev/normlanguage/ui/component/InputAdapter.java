package dev.normlanguage.ui.component;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.scene.control.TreeItem;
import javafx.scene.paint.Color;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.io.File;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public final class InputAdapter {
    @FunctionalInterface
    public interface Progress {
        void report(double value);
    }

    private InputAdapter() {}

    public static void inputSubmit(Input control, Runnable action) {
        control.setOnAction(action == null ? null : event -> action.run());
    }

    public static ValueLink<String> inputValue(Input control) { return ValueLink.text(control); }
    public static ValueLink<String> mentionsValue(Mentions control) { return ValueLink.text(control); }
    public static ValueLink<String> autoCompleteValue(AutoComplete control) { return ValueLink.text(control.getEditor()); }
    public static ValueLink<Boolean> checkboxValue(Checkbox control) { return ValueLink.checked(control); }
    public static ValueLink<Boolean> radioValue(Radio control) { return ValueLink.toggled(control); }
    public static ValueLink<Boolean> switchValue(Switch control) { return ValueLink.toggled(control); }
    public static ValueLink<LocalDate> dateValue(DatePicker control) { return new ValueLink<>(control, control.valueProperty()); }
    public static ValueLink<LocalTime> timeValue(TimePicker control) { return new ValueLink<>(control, control.valueProperty()); }
    public static <T> Select<T> select(List<T> options) { return new Select<>(FXCollections.observableArrayList(options)); }
    public static <T> Select.Multiple<T> selectMultiple(List<T> options) { return Select.multiple(FXCollections.observableArrayList(options)); }
    public static <T> Select.Searchable<T> selectSearchable(List<T> options) { return Select.searchable(FXCollections.observableArrayList(options)); }
    public static <T> void selectOptions(Select<T> control, List<T> options) {
        if (control.getItems().equals(options)) return;
        control.getItems().setAll(options);
        if (!options.contains(control.getValue())) control.setValue(null);
    }
    public static <T> void multipleOptions(Select.Multiple<T> control, List<T> options) {
        if (!control.getOptions().equals(options)) control.getOptions().setAll(options);
    }
    public static <T> void searchableOptions(Select.Searchable<T> control, List<T> options) {
        if (!control.getOptions().equals(options)) control.getOptions().setAll(options);
    }
    public static <T> void cascaderOptions(Cascader<T> control, List<Cascader.Item<T>> options) {
        if (!control.getItems().equals(options)) control.getItems().setAll(options);
    }
    public static <T> TreeSelect<T> treeSelect(List<Cascader.Item<T>> options) {
        var root = new TreeItem<T>();
        for (var option : options) root.getChildren().add(treeItem(option));
        var control = new TreeSelect<>(root);
        control.getProperties().put("norm-tree-options", List.copyOf(options));
        return control;
    }
    public static <T> void treeOptions(TreeSelect<T> control, List<Cascader.Item<T>> options) {
        if (Objects.equals(control.getProperties().get("norm-tree-options"), options)) return;
        var root = new TreeItem<T>();
        for (var option : options) root.getChildren().add(treeItem(option));
        control.setRootItem(root);
        control.getProperties().put("norm-tree-options", List.copyOf(options));
    }
    public static <T> void transferOptions(Transfer<T> control, List<T> options) {
        if (Objects.equals(control.getProperties().get("norm-transfer-options"), options)) return;
        control.setItems(options);
        control.getProperties().put("norm-transfer-options", List.copyOf(options));
    }
    private static <T> TreeItem<T> treeItem(Cascader.Item<T> option) {
        var result = new TreeItem<>(option.value());
        for (var child : option.children()) result.getChildren().add(treeItem(child));
        return result;
    }
    public static <T> ValueLink<T> selectValue(Select<T> control) { return new ValueLink<>(control, control.valueProperty()); }
    public static ValueLink<DatePicker.DateRange> dateRangeValue(DatePicker.Range control) { return new ValueLink<>(control, control.valueProperty()); }
    public static SelectionLink<BigDecimal> numberValue(InputNumber control) {
        return new SelectionLink<>(control, control.valueProperty(), control::setValue);
    }
    public static SelectionLink<Integer> rateValue(Rate control) {
        var observed = Bindings.createObjectBinding(control::getValue, control.valueProperty());
        return new SelectionLink<>(control, observed, control::setValue, observed::dispose);
    }
    public static SelectionLink<Double> sliderValue(Slider control) {
        var observed = Bindings.createObjectBinding(control::getValue, control.valueProperty());
        return new SelectionLink<>(control, observed, control::setValue, observed::dispose);
    }
    public static SelectionLink<List<Double>> sliderRangeValue(Slider.Range control) {
        var observed = Bindings.createObjectBinding(() -> List.of(control.getStart(), control.getEnd()),
            control.getStartSlider().valueProperty(), control.getEndSlider().valueProperty());
        return new SelectionLink<>(control, observed, values -> {
            if (values.size() != 2) throw new IllegalArgumentException("Range needs two values");
            control.setRange(values.get(0), values.get(1));
        }, observed::dispose);
    }
    public static void sliderRangeBounds(Slider.Range control, double minimum, double maximum) {
        if (!(minimum <= control.getStart() && control.getEnd() <= maximum))
            throw new IllegalArgumentException("Selected range outside bounds");
        control.getStartSlider().setMin(minimum);
        control.getStartSlider().setMax(maximum);
        control.getEndSlider().setMin(minimum);
        control.getEndSlider().setMax(maximum);
    }
    public static SelectionLink<String> colorHexValue(ColorPicker control) {
        var observed = Bindings.createObjectBinding(() -> colorHex(control), control.valueProperty());
        return new SelectionLink<>(control, observed, hex -> control.setValue(Color.web(hex)), observed::dispose);
    }
    public static String colorHex(ColorPicker control) {
        var value = control.getValue();
        return String.format(Locale.ROOT, "#%02X%02X%02X%02X",
            Math.round(value.getRed() * 255), Math.round(value.getGreen() * 255),
            Math.round(value.getBlue() * 255), Math.round(value.getOpacity() * 255));
    }
    public static <T> SelectionLink<List<T>> cascaderPath(Cascader<T> control) {
        return new SelectionLink<>(control, control.valueProperty(), control::selectPath);
    }
    public static <T> SelectionLink<T> treeSelectValue(TreeSelect<T> control) {
        return new SelectionLink<>(control, control.valueProperty(), value -> {
            if (value == null) {
                control.getTreeView().getSelectionModel().clearSelection();
                return;
            }
            var queue = new ArrayDeque<TreeItem<T>>();
            queue.add(control.getRootItem());
            while (!queue.isEmpty()) {
                var item = queue.removeFirst();
                if (Objects.equals(item.getValue(), value)) {
                    control.select(item);
                    return;
                }
                queue.addAll(item.getChildren());
            }
            throw new IllegalArgumentException("Unknown tree selection");
        });
    }
    public static <T> SelectionLink<List<T>> multipleValue(Select.Multiple<T> control) {
        var observed = Bindings.createObjectBinding(() -> List.copyOf(control.getSelectedItems()), control.getSelectedItems());
        return new SelectionLink<>(control, observed, selected -> {
            for (T item : List.copyOf(control.getSelectedItems())) if (!selected.contains(item)) control.deselect(item);
            for (T item : selected) if (!control.getSelectedItems().contains(item)) control.select(item);
        }, observed::dispose);
    }
    public static <T> SelectionLink<List<T>> transferValue(Transfer<T> control) {
        var observed = Bindings.createObjectBinding(() -> List.copyOf(control.getSelectedItems()), control.getSelectedItems());
        return new SelectionLink<>(control, observed, selected -> {
            for (T item : List.copyOf(control.getSelectedItems())) if (!selected.contains(item)) control.remove(item);
            for (T item : selected) if (!control.getSelectedItems().contains(item)) control.select(item);
        }, observed::dispose);
    }
    public static void uploadTaskHandler(Upload control, BiFunction<File, Progress, CompletableFuture<Void>> uploader) {
        Objects.requireNonNull(uploader);
        control.setUploader((file, progress) -> uploader.apply(file, progress::accept));
    }
}
