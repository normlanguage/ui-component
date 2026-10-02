package dev.normlanguage.ui.component;

public class Switch extends javafx.scene.control.ToggleButton {
    public Switch() { this(""); }
    public Switch(String text) {
        super(text);
        var thumb = new javafx.scene.layout.Region();
        thumb.getStyleClass().add("norm-switch-thumb");
        var track = new javafx.scene.layout.StackPane(thumb);
        track.getStyleClass().add("norm-switch-track");
        setGraphic(track);
        setContentDisplay(javafx.scene.control.ContentDisplay.LEFT);
        setGraphicTextGap(8);
        setAccessibleText(text.isBlank() ? "Switch" : text);
        getStyleClass().add("norm-switch");
    }
}
