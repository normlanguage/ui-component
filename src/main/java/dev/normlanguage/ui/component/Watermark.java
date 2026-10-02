package dev.normlanguage.ui.component;

import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public final class Watermark extends StackPane {
    private final Canvas overlay = new Canvas();
    private final Text themePaint = new Text();
    private final javafx.beans.property.StringProperty text = new javafx.beans.property.SimpleStringProperty(this, "text", "");
    private final javafx.beans.property.ObjectProperty<Color> color = new javafx.beans.property.SimpleObjectProperty<>(this, "color");
    private final javafx.beans.property.ObjectProperty<javafx.scene.image.Image> image = new javafx.beans.property.SimpleObjectProperty<>(this, "image");
    private final javafx.beans.InvalidationListener imageLoaded = observable -> requestLayout();
    private final javafx.beans.WeakInvalidationListener weakImageLoaded = new javafx.beans.WeakInvalidationListener(imageLoaded);
    public Watermark(Node content, String text) {
        this.text.set(text);
        overlay.setMouseTransparent(true);
        themePaint.setStyle("-fx-fill: -norm-muted;");
        themePaint.setVisible(false);
        themePaint.setManaged(false);
        getChildren().addAll(content, overlay, themePaint);
        this.text.addListener(observable -> requestLayout());
        color.addListener(observable -> requestLayout());
        image.addListener((observable, previous, current) -> {
            if (previous != null) previous.progressProperty().removeListener(weakImageLoaded);
            if (current != null) current.progressProperty().addListener(weakImageLoaded);
            requestLayout();
        });
        themePaint.fillProperty().addListener(observable -> requestLayout());
    }
    public void setContent(Node value) { getChildren().set(0, value); }

    public Watermark(Node content, javafx.scene.image.Image image) {
        this(content, "");
        setImage(image);
    }
    public javafx.beans.property.StringProperty textProperty() { return text; }
    public javafx.beans.property.ObjectProperty<Color> colorProperty() { return color; }
    public javafx.beans.property.ObjectProperty<javafx.scene.image.Image> imageProperty() { return image; }
    public javafx.scene.image.Image getImage() { return image.get(); }
    public void setImage(javafx.scene.image.Image value) { image.set(value); }
    @Override protected void layoutChildren() {
        super.layoutChildren();
        overlay.setWidth(getWidth()); overlay.setHeight(getHeight());
        var graphics = overlay.getGraphicsContext2D();
        graphics.clearRect(0, 0, getWidth(), getHeight());
        var mark = image.get();
        if (mark != null) {
            if (mark.getWidth() <= 0 || mark.getHeight() <= 0 || mark.isError()) return;
            graphics.setGlobalAlpha(0.18);
            for (double y = 40; y < getHeight() + mark.getHeight(); y += mark.getHeight() + 80) {
                for (double x = 10; x < getWidth() + mark.getWidth(); x += mark.getWidth() + 80) {
                    graphics.save(); graphics.translate(x, y); graphics.rotate(-25);
                    graphics.drawImage(mark, 0, 0); graphics.restore();
                }
            }
        } else {
            graphics.setGlobalAlpha(color.get() == null ? 0.18 : 1.0);
            graphics.setFill(color.get() == null ? themePaint.getFill() : color.get()); graphics.setFont(Font.font(18));
            for (double y = 40; y < getHeight() + 100; y += 120) {
                for (double x = 10; x < getWidth() + 100; x += 220) {
                    graphics.save(); graphics.translate(x, y); graphics.rotate(-25);
                    graphics.fillText(text.get(), 0, 0); graphics.restore();
                }
            }
        }
    }
}
