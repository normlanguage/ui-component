package dev.normlanguage.ui.component.gallery;

import dev.normlanguage.ui.component.App;
import dev.normlanguage.ui.component.AutoComplete;
import dev.normlanguage.ui.component.Cascader;
import dev.normlanguage.ui.component.Checkbox;
import dev.normlanguage.ui.component.ColorPicker;
import dev.normlanguage.ui.component.DatePicker;
import dev.normlanguage.ui.component.Form;
import dev.normlanguage.ui.component.Input;
import dev.normlanguage.ui.component.InputNumber;
import dev.normlanguage.ui.component.Mentions;
import dev.normlanguage.ui.component.Radio;
import dev.normlanguage.ui.component.Rate;
import dev.normlanguage.ui.component.Select;
import dev.normlanguage.ui.component.Slider;
import dev.normlanguage.ui.component.Switch;
import dev.normlanguage.ui.component.TimePicker;
import dev.normlanguage.ui.component.Transfer;
import dev.normlanguage.ui.component.TreeSelect;
import dev.normlanguage.ui.component.Upload;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.VBox;

import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public final class InputExamples {
    private InputExamples() {}
    public static Map<String, Supplier<Node>> examples(App app) {
        var examples = new LinkedHashMap<String, Supplier<Node>>();
        examples.put("AutoComplete", () -> {
            var control = new AutoComplete();
            control.setPromptText("Search city");
            control.setSuggestions(List.of("Singapore", "Seoul", "Sydney", "Tokyo"));
            return control;
        });
        examples.put("Cascader", () -> {
            var control = new Cascader<String>(List.of(
            new Cascader.Item<>("Asia", "Asia", List.of(
                new Cascader.Item<>("Singapore", "Singapore", List.of()),
                new Cascader.Item<>("Japan", "Japan", List.of()))),
            new Cascader.Item<>("Europe", "Europe", List.of(), true)));
            control.setChildrenProvider(item -> CompletableFuture.completedFuture(List.of(
                new Cascader.Item<>("France", "France", List.of()),
                new Cascader.Item<>("Germany", "Germany", List.of()))));
            return control;
        });
        examples.put("Checkbox", () -> new Checkbox("Send email updates"));
        examples.put("ColorPicker", ColorPicker::new);
        examples.put("DatePicker", () -> new VBox(8, new DatePicker(), new DatePicker.Range()));
        examples.put("Form", () -> {
            var form = new Form();
            var name = new Input();
            name.setPromptText("Required name");
            form.addField("name", name, text -> !text.isBlank());
            var enabled = new Checkbox("Enable account");
            form.addField("enabled", enabled, enabled.selectedProperty(), Boolean.TRUE::equals);
            var result = new Label();
            form.setOnSubmit(() -> result.setText("Saved: " + name.getText()));
            var send = new javafx.scene.control.Button("Submit");
            send.setOnAction(event -> { if (!form.submit()) result.setText("Complete required fields"); });
            return new VBox(8, form, send, result);
        });
        examples.put("Input", () -> {
            var control = new Input();
            control.setPromptText("Type a name");
            var password = Input.password();
            password.setPromptText("Password");
            var multiline = Input.multiline();
            multiline.setPromptText("Notes");
            return new VBox(8, control, password, multiline);
        });
        examples.put("InputNumber", () -> {
            var number = new InputNumber(new java.math.BigDecimal("0"), new java.math.BigDecimal("100"),
                new java.math.BigDecimal("50.00"), new java.math.BigDecimal("0.25"));
            number.setScale(2);
            return number;
        });
        examples.put("Mentions", () -> {
            var control = new Mentions();
            control.setPromptText("Type @ to mention a person");
            control.setSuggestions(List.of("alice", "bob", "charlie"));
            return control;
        });
        examples.put("Radio", () -> {
            var group = new ToggleGroup();
            var first = new Radio("First");
            var second = new Radio("Second");
            first.setToggleGroup(group);
            second.setToggleGroup(group);
            first.setSelected(true);
            return new VBox(8, first, second);
        });
        examples.put("Rate", () -> new Rate(5));
        examples.put("Select", () -> {
            var select = new Select<String>();
            select.getItems().addAll("Small", "Medium", "Large");
            select.setPromptText("Choose a size");
            var options = javafx.collections.FXCollections.observableArrayList("Small", "Medium", "Large");
            return new VBox(8, select, Select.searchable(options), Select.multiple(options));
        });
        examples.put("Slider", () -> new VBox(8, new Slider(0, 100, 40), new Slider.Range(0, 100, 20, 80)));
        examples.put("Switch", () -> new Switch("Enable notifications"));
        examples.put("TimePicker", () -> {
            var picker = new TimePicker(30);
            picker.setAllowedRange(java.time.LocalTime.of(8, 0), java.time.LocalTime.of(18, 0));
            return picker;
        });
        examples.put("Transfer", () -> new Transfer<>(List.of("Alpha", "Beta", "Gamma", "Delta")));
        examples.put("TreeSelect", () -> {
            var root = new TreeItem<String>("root");
            var fruit = new TreeItem<String>("Fruit");
            fruit.getChildren().add(new TreeItem<>("Apple"));
            fruit.getChildren().add(new TreeItem<>("Orange"));
            var vegetable = new TreeItem<String>("Vegetable");
            vegetable.getChildren().add(new TreeItem<>("Carrot"));
            root.getChildren().add(fruit);
            root.getChildren().add(vegetable);
            return new TreeSelect<>(root);
        });
        examples.put("Upload", () -> {
            var upload = new Upload();
            Path destination = Path.of(System.getProperty("java.io.tmpdir"), "ui-component-demo-uploads");
            upload.setUploader((file, progress) -> CompletableFuture.runAsync(() -> {
                try {
                    Files.createDirectories(destination);
                    Files.copy(file.toPath(), destination.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
                    progress.accept(1);
                } catch (java.io.IOException error) { throw new UncheckedIOException(error); }
            }));
            app.own(upload);
            return new VBox(8, new Label("Files are copied to " + destination), upload);
        });
        return java.util.Collections.unmodifiableMap(examples);
    }
}
