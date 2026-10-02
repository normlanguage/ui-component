package dev.normlanguage.ui.component;

import javafx.scene.control.Label;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FeedbackContentTest extends FxTest {
    @Test void contentUpdatesPreserveControlChrome() throws Exception {
        fx(() -> {
            var first = new Label("first");
            var second = new Label("second");
            var alert = new Alert("Original", first);
            alert.setTitle("Updated");
            alert.setContent(second);
            assertSame(second, alert.getContent());
            assertEquals("Updated", alert.getTitle());
            var result = new Result("Title", "Description", first);
            result.setActions(java.util.List.of(second));
            assertEquals(3, result.getChildren().size());
            assertSame(second, result.getChildren().get(2));
            var spin = new Spin(first);
            spin.setContent(second);
            assertSame(second, spin.getChildren().getFirst());
            assertEquals(2, spin.getChildren().size());
            var skeleton = new Skeleton(3);
            skeleton.setLines(2);
            assertEquals(2, skeleton.getChildren().size());
        });
    }
}
