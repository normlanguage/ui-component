package dev.normlanguage.ui.component;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public final class Popconfirm implements AutoCloseable {
    private final Popover popover;
    public Popconfirm(Node anchor, String question, Runnable confirmed) {
        var yes = new Button("Confirm");
        var no = new Button("Cancel");
        popover = new Popover(anchor, new VBox(8, new Label(question), new HBox(8, no, yes)));
        yes.setOnAction(event -> { hide(); confirmed.run(); });
        no.setOnAction(event -> hide());
    }
    public void show() { popover.show(); }
    public void hide() { popover.hide(); }
    public boolean isShowing() { return popover.isShowing(); }
    @Override public void close() { popover.close(); }
}
