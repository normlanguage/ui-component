package dev.normlanguage.ui.component;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class Image extends StackPane {
    private final ImageView view = new ImageView();
    private final StringProperty source = new SimpleStringProperty(this, "source");
    private final BooleanProperty previewable = new SimpleBooleanProperty(this, "previewable");
    private final Label error = new Label("Image unavailable");
    private javafx.scene.image.Image request;
    private javafx.scene.image.Image directImage;

    public Image() {
        getStyleClass().add("norm-image");
        view.setPreserveRatio(true);
        getChildren().addAll(view, error);
        error.setVisible(false);
        error.setManaged(false);
        setOnMouseClicked(event -> {
            if (isPreviewable() && request != null && !request.isError() && request.getProgress() == 1) showPreview();
        });
        source.addListener((observable, old, value) -> {
            directImage = null;
            if (getScene() != null) load();
        });
        sceneProperty().addListener((observable, old, value) -> {
            if (value == null && old != null && directImage == null) {
                if (request != null) request.cancel();
                request = null;
                view.setImage(null);
                error.setVisible(false);
            } else if (value != null && directImage == null) load();
        });
    }

    public Image(String source) {
        this();
        setSource(source);
    }

    public Image(javafx.scene.image.Image image) {
        this();
        setImage(image);
    }

    public StringProperty sourceProperty() { return source; }
    public String getSource() { return source.get(); }
    public void setSource(String value) { source.set(value); }
    public ImageView getImageView() { return view; }
    public void setImage(javafx.scene.image.Image image) {
        if (request != null) request.cancel();
        source.set(null);
        directImage = image;
        request = image;
        view.setImage(image);
        error.setVisible(false);
    }
    public BooleanProperty previewableProperty() { return previewable; }
    public boolean isPreviewable() { return previewable.get(); }
    public void setPreviewable(boolean value) { previewable.set(value); }
    public Modal showPreview() {
        if (getScene() == null) throw new IllegalStateException("Image must be attached to a scene");
        if (request == null || request.isError() || request.getProgress() < 1) throw new IllegalStateException("Image is not loaded");
        var preview = new ImageView(request);
        preview.setPreserveRatio(true);
        preview.setFitWidth(Math.min(getScene().getWidth() * 0.8, request.getWidth()));
        preview.setFitHeight(Math.min(getScene().getHeight() * 0.8, request.getHeight()));
        var modal = new Modal(this, "Image", preview);
        modal.show();
        return modal;
    }

    private void load() {
        if (request != null) request.cancel();
        request = source.get() == null || source.get().isBlank() ? null : new javafx.scene.image.Image(source.get(), true);
        view.setImage(request);
        error.setVisible(false);
        if (request != null) {
            var current = request;
            current.errorProperty().addListener((o,a,b) -> {
                if (request == current) error.setVisible(b);
            });
        }
    }
}
