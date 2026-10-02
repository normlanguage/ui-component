package dev.normlanguage.ui.component;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Window;

import java.util.ArrayList;
import java.util.Objects;

public final class OverlayAdapter extends StackPane implements AutoCloseable {
    private enum Kind { MODAL, DRAWER, POPOVER, TOOLTIP, POPCONFIRM, TOUR }

    private final Kind kind;
    private String text;
    private Side side;
    private Object popup;
    private Node anchor;
    private Node body;
    private Runnable hidden;
    private Runnable confirmed;
    private boolean desiredOpen;
    private boolean controlledOpen;
    private boolean synchronizing;
    private boolean closed;
    private java.util.List<String> tourTitles = java.util.List.of();
    private java.util.List<String> tourDescriptions = java.util.List.of();
    private java.util.List<Node> tourTargets = java.util.List.of();
    private Window window;
    private final javafx.beans.InvalidationListener windowShowing = observable -> reconcile();
    private final javafx.beans.InvalidationListener sceneWindow = observable -> {
        observeWindow();
        reconcile();
    };
    private final javafx.beans.value.ChangeListener<Scene> sceneChanged = (observable, before, after) -> {
        if (before != null) before.windowProperty().removeListener(sceneWindow);
        if (after != null) after.windowProperty().addListener(sceneWindow);
        observeWindow();
        reconcile();
    };

    private OverlayAdapter(Kind kind, String text, Side side) {
        this.kind = kind;
        this.text = text;
        this.side = side;
        getStyleClass().add("norm-overlay-anchor");
        sceneProperty().addListener(sceneChanged);
    }

    public static OverlayAdapter modal(String title) { return new OverlayAdapter(Kind.MODAL, title, null); }
    public static OverlayAdapter drawer(String side) {
        return new OverlayAdapter(Kind.DRAWER, null, Side.valueOf(side.toUpperCase(java.util.Locale.ROOT)));
    }
    public static OverlayAdapter popover() { return new OverlayAdapter(Kind.POPOVER, null, null); }
    public static OverlayAdapter tooltip(String text) { return new OverlayAdapter(Kind.TOOLTIP, text, null); }
    public static OverlayAdapter popconfirm(String question) { return new OverlayAdapter(Kind.POPCONFIRM, question, null); }
    public static OverlayAdapter tour() { return new OverlayAdapter(Kind.TOUR, null, null); }

    public Node contentNode() { return body; }
    public void setText(String value) {
        Util.requireFxThread();
        if (Objects.equals(text, value)) return;
        text = value;
        if (popup != null) {
            disposePopup();
            createPopup();
            reconcile();
        }
    }
    public void setSide(String value) {
        Util.requireFxThread();
        var next = Side.valueOf(value.toUpperCase(java.util.Locale.ROOT));
        if (side == next) return;
        side = next;
        if (popup != null) {
            disposePopup();
            createPopup();
            reconcile();
        }
    }
    public boolean isShowing() {
        return switch (popup) {
            case Modal control -> control.isShowing();
            case Drawer control -> control.isShowing();
            case Popover control -> control.isShowing();
            case Tooltip control -> control.isShowing();
            case Popconfirm control -> control.isShowing();
            case Tour control -> control.getIndex() >= 0;
            case null -> false;
            default -> throw new IllegalStateException("Unknown overlay");
        };
    }

    public void setContent(Node anchor, Node body) {
        Util.requireFxThread();
        if (closed) throw new IllegalStateException("Overlay is closed");
        Objects.requireNonNull(anchor);
        if (this.anchor == anchor && this.body == body) return;
        disposePopup();
        this.anchor = anchor;
        this.body = body;
        getChildren().setAll(anchor);
        createPopup();
        reconcile();
    }

    public void setTourSteps(java.util.List<String> titles, java.util.List<String> descriptions,
            java.util.List<Node> targets) {
        Util.requireFxThread();
        if (kind != Kind.TOUR) throw new IllegalStateException("Not a tour");
        if (titles.size() != descriptions.size() || titles.size() != targets.size())
            throw new IllegalArgumentException("Tour steps differ");
        if (tourTitles.equals(titles) && tourDescriptions.equals(descriptions) && tourTargets.equals(targets)) return;
        tourTitles = java.util.List.copyOf(titles);
        tourDescriptions = java.util.List.copyOf(descriptions);
        tourTargets = java.util.List.copyOf(targets);
        disposePopup();
        var layout = new HBox(12);
        layout.getChildren().setAll(targets);
        getChildren().setAll(layout);
        var steps = new ArrayList<Tour.Step>();
        for (int index = 0; index < targets.size(); index++)
            steps.add(new Tour.Step(targets.get(index), titles.get(index), descriptions.get(index)));
        var tour = new Tour(steps);
        tour.setOnClosed(this::onHidden);
        popup = tour;
        reconcile();
    }

    public void setConfirmed(Runnable action) { confirmed = action; }

    public void setOpen(boolean open, Runnable onHidden) {
        Util.requireFxThread();
        if (closed) throw new IllegalStateException("Overlay is closed");
        hidden = Objects.requireNonNull(onHidden);
        controlledOpen = true;
        desiredOpen = open;
        reconcile();
    }

    public void unbindOpen() {
        Util.requireFxThread();
        if (closed) throw new IllegalStateException("Overlay is closed");
        if (!controlledOpen) return;
        controlledOpen = false;
        hidden = null;
        desiredOpen = false;
        synchronizing = true;
        try { dismiss(); }
        finally { synchronizing = false; }
    }

    public void dismiss() {
        Util.requireFxThread();
        switch (popup) {
            case Modal control -> control.hide();
            case Drawer control -> control.hide();
            case Popover control -> control.hide();
            case Tooltip control -> control.hide();
            case Popconfirm control -> control.hide();
            case Tour control -> control.close();
            case null -> {}
            default -> throw new IllegalStateException("Unknown overlay");
        }
    }

    private void createPopup() {
        if (kind != Kind.TOOLTIP && kind != Kind.POPCONFIRM && body == null) return;
        popup = switch (kind) {
            case MODAL -> new Modal(this, text, body, ContentOwnership.EXTERNAL);
            case DRAWER -> new Drawer(this, body, side, ContentOwnership.EXTERNAL);
            case POPOVER -> new Popover(this, body, ContentOwnership.EXTERNAL);
            case TOOLTIP -> new Tooltip(this, text);
            case POPCONFIRM -> new Popconfirm(this, text, () -> {
                if (confirmed != null) confirmed.run();
            });
            case TOUR -> null;
        };
        switch (popup) {
            case Modal control -> control.setOnHidden(this::onHidden);
            case Drawer control -> control.setOnHidden(this::onHidden);
            case Popover control -> control.setOnHidden(this::onHidden);
            case Tooltip control -> control.setOnHidden(this::onHidden);
            case Popconfirm control -> control.setOnHidden(this::onHidden);
            default -> {}
        }
    }

    private void onHidden() {
        if (synchronizing || closed) return;
        desiredOpen = false;
        if (hidden != null) hidden.run();
    }

    private void observeWindow() {
        var current = getScene() == null ? null : getScene().getWindow();
        if (window == current) return;
        if (window != null && kind != Kind.TOUR) disposePopup();
        if (window != null) window.showingProperty().removeListener(windowShowing);
        window = current;
        if (window != null) window.showingProperty().addListener(windowShowing);
        if (window != null && popup == null && anchor != null && kind != Kind.TOUR) createPopup();
    }

    private void reconcile() {
        if (popup == null || closed) return;
        synchronizing = true;
        try {
            if (!desiredOpen) dismiss();
            else if (!isShowing() && getScene() != null && window != null && window.isShowing()) {
                switch (popup) {
                    case Modal control -> control.show();
                    case Drawer control -> control.show();
                    case Popover control -> control.show();
                    case Tooltip control -> control.show();
                    case Popconfirm control -> control.show();
                    case Tour control -> control.start();
                    default -> throw new IllegalStateException("Unknown overlay");
                }
            }
        } finally { synchronizing = false; }
    }

    private void disposePopup() {
        if (popup == null) return;
        synchronizing = true;
        try {
            switch (popup) {
                case Modal control -> { control.setOnHidden(null); control.close(); }
                case Drawer control -> { control.setOnHidden(null); control.close(); }
                case Popover control -> { control.setOnHidden(null); control.close(); }
                case Tooltip control -> { control.setOnHidden(null); control.close(); }
                case Popconfirm control -> { control.setOnHidden(null); control.close(); }
                case Tour control -> { control.setOnClosed(null); control.close(); }
                default -> throw new IllegalStateException("Unknown overlay");
            }
        } finally {
            popup = null;
            synchronizing = false;
        }
    }

    @Override public void close() {
        Util.requireFxThread();
        if (closed) return;
        closed = true;
        sceneProperty().removeListener(sceneChanged);
        if (getScene() != null) getScene().windowProperty().removeListener(sceneWindow);
        if (window != null) window.showingProperty().removeListener(windowShowing);
        window = null;
        hidden = null;
        confirmed = null;
        disposePopup();
    }
}
