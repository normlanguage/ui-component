package dev.normlanguage.ui.component;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class Tabs extends TabPane {
    public Tabs() { getStyleClass().add("norm-tabs"); }
    public Tab add(String title, Node content) {
        var tab = new Tab(title, content);
        getTabs().add(tab);
        return tab;
    }
}
