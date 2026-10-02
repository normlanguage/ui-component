package dev.normlanguage.ui.component;

public class Switch extends javafx.scene.control.ToggleButton implements AutoCloseable {
    private final Motion motion = new Motion(this);
    public Switch() { this(""); }
    public Switch(String text) {
        super(text);
        var thumb = new javafx.scene.layout.Region();
        thumb.getStyleClass().add("norm-switch-thumb");
        thumb.setTranslateX(-7);
        selectedProperty().addListener((observable, old, selected) -> motion.animate(Motion.STANDARD,
                new javafx.animation.KeyValue(thumb.translateXProperty(), selected ? 7 : -7, Motion.EASING)));
        var track = new javafx.scene.layout.StackPane(thumb);
        track.getStyleClass().add("norm-switch-track");
        setGraphic(track);
        setContentDisplay(javafx.scene.control.ContentDisplay.LEFT);
        setGraphicTextGap(8);
        setAccessibleText(text.isBlank() ? "Switch" : text);
        getStyleClass().add("norm-switch");
    }
    @Override public void close() { motion.close(); }
}
