package dev.normlanguage.ui.component;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.DoubleConsumer;
import java.util.function.Function;

public class Upload extends VBox implements AutoCloseable {
    public enum Status { READY, UPLOADING, COMPLETE, FAILED, CANCELLED }
    public static final class Item {
        private final File file;
        private final ObjectProperty<Status> status = new SimpleObjectProperty<>(this, "status", Status.READY);
        private final DoubleProperty progress = new SimpleDoubleProperty(this, "progress");
        private AsyncRequest<Void> work;
        private long generation;
        private Item(File file) { this.file = file; }
        public File getFile() { return file; }
        public Status getStatus() { return status.get(); }
        public ReadOnlyObjectProperty<Status> statusProperty() { return status; }
        public double getProgress() { return progress.get(); }
        public ReadOnlyDoubleProperty progressProperty() { return progress; }
        @Override public String toString() {
            return file.getName() + " · " + status.get() + (status.get() == Status.UPLOADING ? " " + Math.round(progress.get() * 100) + "%" : "");
        }
    }
    private final ObservableList<Item> items = FXCollections.observableArrayList();
    private final ListView<Item> list = new ListView<>(items);
    private BiFunction<File, DoubleConsumer, CompletableFuture<Void>> uploader;
    private boolean closed;
    public Upload() {
        var choose = new Button("Choose files");
        var retry = new Button("Retry");
        var cancel = new Button("Cancel");
        choose.setOnAction(event -> {
            var chooser = new FileChooser();
            var window = getScene() == null ? null : getScene().getWindow();
            var selected = chooser.showOpenMultipleDialog(window);
            if (selected != null) addFiles(selected);
        });
        retry.setOnAction(event -> {
            var item = list.getSelectionModel().getSelectedItem();
            if (uploader != null && item != null && item.getStatus() != Status.UPLOADING) upload(item);
        });
        cancel.setOnAction(event -> {
            var item = list.getSelectionModel().getSelectedItem();
            if (item != null && item.getStatus() == Status.UPLOADING) cancel(item);
        });
        list.setCellFactory(view -> new javafx.scene.control.ListCell<>() {
            private Item current;
            private javafx.beans.value.ChangeListener<Status> statusListener;
            private javafx.beans.value.ChangeListener<Number> progressListener;
            @Override protected void updateItem(Item item, boolean empty) {
                super.updateItem(item, empty);
                if (current != null) {
                    current.status.removeListener(statusListener);
                    current.progress.removeListener(progressListener);
                }
                current = empty ? null : item;
                if (current == null) { setText(null); return; }
                statusListener = (observable, old, status) -> setText(current.toString());
                progressListener = (observable, old, progress) -> setText(current.toString());
                current.status.addListener(statusListener);
                current.progress.addListener(progressListener);
                setText(current.toString());
            }
        });
        setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) event.acceptTransferModes(TransferMode.COPY);
            event.consume();
        });
        setOnDragDropped(event -> {
            var files = event.getDragboard().getFiles();
            if (!files.isEmpty()) addFiles(files);
            event.setDropCompleted(!files.isEmpty());
            event.consume();
        });
        sceneProperty().addListener((observable, old, scene) -> {
            if (old != null && scene == null) for (Item item : items) if (item.getStatus() == Status.UPLOADING) cancel(item);
        });
        getChildren().addAll(new HBox(choose, retry, cancel), list);
        getStyleClass().add("norm-upload");
    }
    public void setUploader(Function<File, CompletableFuture<Void>> uploader) {
        Objects.requireNonNull(uploader);
        setUploader((file, progress) -> uploader.apply(file));
    }
    public void setUploader(BiFunction<File, DoubleConsumer, CompletableFuture<Void>> uploader) {
        this.uploader = Objects.requireNonNull(uploader);
    }
    public ObservableList<Item> getItems() { return FXCollections.unmodifiableObservableList(items); }
    public ListView<Item> getListView() { return list; }
    public void addFiles(List<File> files) {
        for (File file : files) addFile(file);
    }
    public Item addFile(File file) {
        if (closed) throw new IllegalStateException("Upload is closed");
        var item = new Item(Objects.requireNonNull(file));
        items.add(item);
        if (uploader != null) upload(item);
        return item;
    }
    public void upload(Item item) {
        if (closed) throw new IllegalStateException("Upload is closed");
        if (!items.contains(item)) throw new IllegalArgumentException("Unknown item");
        if (uploader == null) throw new IllegalStateException("Uploader is not configured");
        if (item.status.get() == Status.UPLOADING) throw new IllegalStateException("Already uploading");
        item.progress.set(0);
        item.status.set(Status.UPLOADING);
        long generation = ++item.generation;
        var activeUploader = uploader;
        item.work = new AsyncRequest<>(() -> activeUploader.apply(item.file, progress ->
            Platform.runLater(() -> {
                if (!closed && item.generation == generation && item.status.get() == Status.UPLOADING)
                    item.progress.set(Math.clamp(progress, 0, 1));
            })));
        var work = item.work;
        work.result().whenComplete((ignored, failure) -> Platform.runLater(() -> {
            if (closed || item.work != work || item.status.get() == Status.CANCELLED) return;
            item.status.set(failure == null ? Status.COMPLETE : Status.FAILED);
            if (failure == null) item.progress.set(1);
        }));
    }
    public void cancel(Item item) {
        if (!items.contains(item)) throw new IllegalArgumentException("Unknown item");
        item.generation++;
        item.status.set(Status.CANCELLED);
        if (item.work != null) item.work.cancel();
    }
    @Override public void close() {
        if (closed) return;
        for (Item item : items) if (item.status.get() == Status.UPLOADING) cancel(item);
        closed = true;
    }
}
