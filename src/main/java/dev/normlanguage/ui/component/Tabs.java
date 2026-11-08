package dev.normlanguage.ui.component;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class Tabs extends TabPane {
    public Tabs() { getStyleClass().add("norm-tabs"); setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE); }
    public void setHeaderVisible(boolean visible) {
        if (visible) getStyleClass().remove("norm-tabs-headerless");
        else if (!getStyleClass().contains("norm-tabs-headerless")) getStyleClass().add("norm-tabs-headerless");
    }
    public Tab add(String title, Node content) {
        var tab = new Tab(title, content);
        getTabs().add(tab);
        return tab;
    }
}
