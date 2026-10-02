package dev.normlanguage.ui.component;

public class Switch extends javafx.scene.control.ToggleButton {
    public Switch() { this(""); }
    public Switch(String text) { super(text); getStyleClass().add("norm-switch"); }
}
