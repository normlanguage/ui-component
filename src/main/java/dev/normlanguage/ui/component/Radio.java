package dev.normlanguage.ui.component;

public class Radio extends javafx.scene.control.RadioButton {
    public Radio() { this(""); }
    public Radio(String text) { super(text); getStyleClass().add("norm-radio"); }
}
