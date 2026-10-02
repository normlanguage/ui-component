package dev.normlanguage.ui.component;

import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class Menu extends MenuBar {
    public Menu() { getStyleClass().add("norm-menu"); }
    public javafx.scene.control.Menu add(String title, MenuItem... items) {
        var menu = new javafx.scene.control.Menu(title);
        menu.getItems().addAll(items);
        getMenus().add(menu);
        return menu;
    }
}
