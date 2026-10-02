package dev.normlanguage.ui.component;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CardLayoutTest extends FxTest {
    @Test void cardHeaderCanBeReplacedAndRestoredWithoutReplacingContent() throws Exception {
        fx(() -> {
            var content = new Label("content");
            var card = new Card("title", content);
            var original = card.getTop();
            var header = new Label("status");
            card.setHeader(header);
            assertSame(header, card.getTop());
            assertSame(content, card.getCenter());
            card.setTitle("updated");
            assertSame(header, card.getTop());
            card.setHeader(null);
            assertSame(original, card.getTop());
            assertEquals("updated", ((Label) card.getTop()).getText());
            card.setTitle("");
            assertNull(card.getTop());
        });
    }

    @Test void inputSubmitHandlerCanBeReplacedAndRemoved() throws Exception {
        fx(() -> {
            var input = new Input();
            var calls = new java.util.concurrent.atomic.AtomicInteger();
            InputAdapter.inputSubmit(input, () -> calls.addAndGet(1));
            InputAdapter.inputSubmit(input, () -> calls.addAndGet(2));
            input.fireEvent(new javafx.event.ActionEvent());
            assertEquals(2, calls.get());
            InputAdapter.inputSubmit(input, null);
            input.fireEvent(new javafx.event.ActionEvent());
            assertEquals(2, calls.get());
        });
    }

    @Test void flexRightAlignmentTracksAvailableWidth() throws Exception {
        fx(() -> {
            var action = new Label("action");
            action.setPrefSize(60, 24);
            var row = new Flex(action);
            row.setAlignment(Pos.CENTER_RIGHT);
            row.resize(400, 40);
            row.layout();
            assertEquals(340, action.getLayoutX(), 0.1);
            row.resize(600, 40);
            row.layout();
            assertEquals(540, action.getLayoutX(), 0.1);
        });
    }
}
