package dev.normlanguage.ui.component;

public class Input extends javafx.scene.control.TextField {
    public Input() { getStyleClass().add("norm-input"); }
    public Input(String text) { this(); setText(text); }
    public static javafx.scene.control.PasswordField password() {
        var control = new javafx.scene.control.PasswordField();
        control.getStyleClass().add("norm-input");
        return control;
    }
    public static javafx.scene.control.TextArea multiline() {
        var control = new javafx.scene.control.TextArea();
        control.getStyleClass().add("norm-input");
        return control;
    }
}
