package dev.normlanguage.ui.component;
import javafx.scene.control.ListView;
public class List<T> extends ListView<T> {
    public List() { getStyleClass().add("norm-list"); }
}
