package dev.normlanguage.ui.component;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.util.List;

public final class FeedbackAdapter {
    private FeedbackAdapter() {}
    public static Alert alert(String title) { return new Alert(title, new StackPane()); }
    public static Result result(String title, String description) { return new Result(title, description); }
    public static Spin spin() { return new Spin(new StackPane()); }
    public static Watermark watermark(String text) { return new Watermark(new StackPane(), text); }
    public static BorderBeam borderBeam() { return new BorderBeam(new StackPane()); }
    public static void actions(Result control, List<Node> actions) { control.setActions(actions); }
    public static void watermarkText(Watermark control, String text) { control.textProperty().set(text); }
    public static AffixHost affix() { return new AffixHost(); }
    public static final class AffixHost extends ScrollPane implements AutoCloseable {
        private final StackPane slot = new StackPane();
        private final Affix affix = new Affix(this, slot);
        private final VBox body = new VBox();
        private AffixHost() { setFitToWidth(true); setContent(body); }
        public void content(Node fixed, Node content) {
            slot.getChildren().setAll(fixed);
            body.getChildren().setAll(affix, content);
        }
        @Override public void close() { affix.close(); }
    }
}
