package dev.normlanguage.ui.component;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.css.PseudoClass;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.List;

public final class Tour implements AutoCloseable {
    public record Step(Node target, String title, String description) {
        public Step { java.util.Objects.requireNonNull(target); java.util.Objects.requireNonNull(title); java.util.Objects.requireNonNull(description); }
    }
    private final List<Step> steps;
    private final ReadOnlyIntegerWrapper index = new ReadOnlyIntegerWrapper(this, "index", -1);
    private Popover popup;
    private App owner;
    public Tour(List<Step> steps) { this.steps = List.copyOf(steps); }
    public ReadOnlyIntegerProperty indexProperty() { return index.getReadOnlyProperty(); }
    public int getIndex() { return index.get(); }
    public void start() { if (!steps.isEmpty()) goTo(0); }
    public void next() { if (index.get() + 1 < steps.size()) goTo(index.get() + 1); else close(); }
    public void previous() { if (index.get() > 0) goTo(index.get() - 1); }
    public void goTo(int position) {
        Util.requireFxThread();
        var step = steps.get(position);
        close();
        index.set(position);
        var previous = new Button("Previous");
        previous.setDisable(position == 0);
        previous.setOnAction(event -> previous());
        var next = new Button(position == steps.size() - 1 ? "Finish" : "Next");
        next.setOnAction(event -> next());
        var dismiss = new Button("Close");
        dismiss.setOnAction(event -> close());
        step.target().pseudoClassStateChanged(PseudoClass.getPseudoClass("tour-target"), true);
        popup = new Popover(step.target(), new VBox(8, new Label(step.title()), new Label(step.description()), new HBox(8, previous, next, dismiss)));
        popup.setOnHidden(this::close);
        try {
            owner = Util.app(step.target());
            owner.own(this);
            popup.show();
        } catch (RuntimeException failure) { close(); throw failure; }
    }
    @Override public void close() {
        Util.requireFxThread();
        if (popup != null) {
            var current = popup;
            popup = null;
            current.close();
        }
        if (index.get() >= 0) {
            var target = steps.get(index.get()).target();
            target.pseudoClassStateChanged(PseudoClass.getPseudoClass("tour-target"), false);
        }
        if (owner != null) { owner.release(this); owner = null; }
        index.set(-1);
    }
}
