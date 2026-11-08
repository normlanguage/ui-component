package dev.normlanguage.ui.component;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

/** A fixed-size avatar with a centered, circular image and a loading/error fallback. */
public class Avatar extends StackPane {
    private final Circle portrait = new Circle(18, 18, 18);
    private final Label fallback = new Label();
    private final ChangeListener<Number> loaded = (observable, old, value) -> updatePortrait();
    private final WeakChangeListener<Number> weakLoaded = new WeakChangeListener<>(loaded);
    private Image image;

    public Avatar(String text) {
        getStyleClass().add("norm-avatar");
        fallback.setText(text);
        portrait.setVisible(false);
        getChildren().addAll(fallback, portrait);
    }

    public void setImage(Image value) {
        if (image == value) return;
        if (image != null) image.progressProperty().removeListener(weakLoaded);
        image = value;
        if (image != null) image.progressProperty().addListener(weakLoaded);
        updatePortrait();
    }

    private void updatePortrait() {
        boolean ready = image != null && !image.isError() && image.getProgress() >= 1
                && image.getWidth() > 0 && image.getHeight() > 0;
        portrait.setVisible(ready);
        fallback.setVisible(!ready);
        if (!ready) {
            portrait.setFill(null);
            return;
        }
        double scale = 36 / Math.min(image.getWidth(), image.getHeight());
        double width = image.getWidth() * scale;
        double height = image.getHeight() * scale;
        portrait.setFill(new ImagePattern(image, (36 - width) / 2, (36 - height) / 2, width, height, false));
    }

    public Image getImage() { return image; }
    public void setText(String value) { fallback.setText(value); }
    public String getText() { return fallback.getText(); }
}
