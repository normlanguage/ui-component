package dev.normlanguage.ui.component;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.geometry.Rectangle2D;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
public class Avatar extends StackPane {
    private final ImageView image = new ImageView();
    private final Label fallback = new Label();
    private final ChangeListener<Number> dimensions = (observable, old, value) -> updateViewport();
    private final WeakChangeListener<Number> weakDimensions = new WeakChangeListener<>(dimensions);
    public Avatar(String text) {
        getStyleClass().add("norm-avatar");
        fallback.setText(text);
        image.setPreserveRatio(true);
        image.setFitWidth(36);
        image.setFitHeight(36);
        image.setClip(new Circle(18, 18, 18));
        getChildren().addAll(fallback, image);
        image.visibleProperty().bind(image.imageProperty().isNotNull());
        fallback.visibleProperty().bind(image.imageProperty().isNull());
    }
    public void setImage(Image value) {
        var previous = image.getImage();
        if (previous != null) {
            previous.widthProperty().removeListener(weakDimensions);
            previous.heightProperty().removeListener(weakDimensions);
        }
        image.setImage(value);
        if (value != null) {
            value.widthProperty().addListener(weakDimensions);
            value.heightProperty().addListener(weakDimensions);
        }
        updateViewport();
    }
    private void updateViewport() {
        var source = image.getImage();
        if (source == null || source.getWidth() <= 0 || source.getHeight() <= 0) {
            image.setViewport(null);
            return;
        }
        double side = Math.min(source.getWidth(), source.getHeight());
        image.setViewport(new Rectangle2D((source.getWidth() - side) / 2, (source.getHeight() - side) / 2, side, side));
    }
    public Image getImage() { return image.getImage(); }
    public void setText(String value) { fallback.setText(value); }
    public String getText() { return fallback.getText(); }
}
