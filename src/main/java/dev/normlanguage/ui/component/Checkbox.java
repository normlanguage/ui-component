package dev.normlanguage.ui.component;

public class Checkbox extends javafx.scene.control.CheckBox {
    public Checkbox() { this(""); }
    public Checkbox(String text) { super(text); getStyleClass().add("norm-checkbox"); }
}
