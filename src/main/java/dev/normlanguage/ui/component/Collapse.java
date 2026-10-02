package dev.normlanguage.ui.component;
import javafx.scene.Node;
import javafx.scene.control.TitledPane;
public class Collapse extends TitledPane implements AutoCloseable {
    private final ConfigurationConnection configuration = new ConfigurationConnection(this,
            () -> setAnimated(ConfigurationConnection.resolve(this).motionEnabled()));
    public Collapse(String title, Node content) {
        super(title, content);
        getStyleClass().add("norm-collapse");
        setExpanded(false);
        sceneProperty().addListener((observable, old, scene) -> {
            if (scene == null) configuration.close(); else configuration.connect();
        });
    }
    @Override public void close() { configuration.close(); }
}
