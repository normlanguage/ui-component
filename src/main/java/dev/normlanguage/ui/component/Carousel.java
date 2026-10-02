package dev.normlanguage.ui.component;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import java.util.List;
public class Carousel extends StackPane implements AutoCloseable {
    private final ObservableList<Node> slides = FXCollections.observableArrayList();
    private final IntegerProperty index = new SimpleIntegerProperty(this, "index", 0) {
        @Override public void set(int value) {
            if (value < 0 || (slides.isEmpty() ? value != 0 : value >= slides.size()))
                throw new IndexOutOfBoundsException(value);
            super.set(value);
        }
    };
    private final BooleanProperty autoPlay = new SimpleBooleanProperty(this, "autoPlay");
    private final javafx.animation.Timeline timer = new javafx.animation.Timeline();
    private final ConfigurationConnection configuration = new ConfigurationConnection(this, this::updateTimer);
    private final javafx.beans.InvalidationListener windowChanged = observable -> updateWindow();
    private final javafx.beans.InvalidationListener showingChanged = observable -> updateTimer();
    private final javafx.beans.value.ChangeListener<javafx.scene.Scene> sceneChanged = (observable, previous, current) -> {
        if (previous != null) previous.windowProperty().removeListener(windowChanged);
        if (current != null) current.windowProperty().addListener(windowChanged);
        updateWindow();
    };
    private javafx.stage.Window window;
    private boolean closed;
    private Duration interval = Duration.seconds(5);
    public Carousel() { this(java.util.List.of()); }
    public Carousel(List<Node> items) {
        getStyleClass().add("norm-carousel");
        slides.addAll(items);
        slides.addListener((javafx.collections.ListChangeListener<Node>) change -> showCurrent());
        index.addListener((o,a,b) -> showCurrent());
        autoPlay.addListener((o,a,b) -> updateTimer());
        visibleProperty().addListener((o,a,b) -> updateTimer());
        sceneProperty().addListener(sceneChanged);
        configuration.connect();
        showCurrent();
    }
    public ObservableList<Node> getSlides() { return slides; }
    public IntegerProperty indexProperty() { return index; }
    public int getIndex() { return index.get(); }
    public void setIndex(int value) {
        if (slides.isEmpty()) throw new IndexOutOfBoundsException(value);
        index.set(value);
    }
    public void next() { if (!slides.isEmpty()) index.set((index.get() + 1) % slides.size()); }
    public void previous() { if (!slides.isEmpty()) index.set((index.get() + slides.size() - 1) % slides.size()); }
    public BooleanProperty autoPlayProperty() { return autoPlay; }
    public boolean isAutoPlay() { return autoPlay.get(); }
    public boolean isPlaying() { return timer.getStatus() == javafx.animation.Animation.Status.RUNNING; }
    public void setAutoPlay(boolean value) { autoPlay.set(value); }
    public Duration getInterval() { return interval; }
    public void setInterval(Duration value) {
        if (value == null || value.lessThanOrEqualTo(Duration.ZERO)) throw new IllegalArgumentException("interval");
        interval = value;
        updateTimer();
    }
    private void updateTimer() {
        timer.stop();
        if (!closed && autoPlay.get() && ConfigurationConnection.resolve(this).motionEnabled()
                && isVisible() && window != null && window.isShowing() && slides.size() > 1) {
            timer.getKeyFrames().setAll(new KeyFrame(interval, event -> next()));
            timer.setCycleCount(javafx.animation.Animation.INDEFINITE);
            timer.play();
        }
    }
    private void updateWindow() {
        var current = getScene() == null ? null : getScene().getWindow();
        if (window != current) {
            if (window != null) window.showingProperty().removeListener(showingChanged);
            window = current;
            if (window != null) window.showingProperty().addListener(showingChanged);
        }
        updateTimer();
    }
    private void showCurrent() {
        getChildren().clear();
        if (slides.isEmpty()) { index.set(0); updateTimer(); return; }
        if (index.get() >= slides.size()) index.set(slides.size() - 1);
        getChildren().add(slides.get(index.get()));
        updateTimer();
    }
    @Override public void close() {
        Util.requireFxThread();
        if (closed) return;
        closed = true;
        timer.stop();
        configuration.close();
        sceneProperty().removeListener(sceneChanged);
        if (getScene() != null) getScene().windowProperty().removeListener(windowChanged);
        if (window != null) window.showingProperty().removeListener(showingChanged);
        window = null;
    }
}
