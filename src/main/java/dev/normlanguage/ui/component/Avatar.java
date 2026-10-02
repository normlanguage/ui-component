package dev.normlanguage.ui.component;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
public class Avatar extends StackPane {
    private final ImageView image = new ImageView();
    private final Label fallback = new Label();
    public Avatar(String text) {
        getStyleClass().add("norm-avatar");
        fallback.setText(text);
        image.setPreserveRatio(true);
        image.setFitWidth(36);
        image.setFitHeight(36);
        getChildren().addAll(fallback, image);
        image.visibleProperty().bind(image.imageProperty().isNotNull());
        fallback.visibleProperty().bind(image.imageProperty().isNull());
    }
    public void setImage(Image value) { image.setImage(value); }
    public Image getImage() { return image.getImage(); }
    public void setText(String value) { fallback.setText(value); }
    public String getText() { return fallback.getText(); }
}
