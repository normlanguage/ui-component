package dev.normlanguage.ui.component;

import javafx.scene.control.TextField;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ValueLinkTest extends FxTest {
    @Test void modelUpdatesDoNotEchoAndEventsUseLatestBinding() throws Exception {
        fx(() -> {
            var input = new TextField();
            var link = ValueLink.text(input);
            var first = new ArrayList<String>();
            var latest = new ArrayList<String>();
            link.update("initial", first::add);
            assertEquals("initial", input.getText());
            assertTrue(first.isEmpty());
            input.selectRange(1, 4);
            link.update("initial", latest::add);
            assertEquals(1, input.getSelection().getStart());
            assertEquals(4, input.getSelection().getEnd());
            input.setText("edited");
            assertTrue(first.isEmpty());
            assertEquals(java.util.List.of("edited"), latest);
            link.close(); link.close();
            input.setText("detached");
            assertEquals(java.util.List.of("edited"), latest);
            assertThrows(IllegalStateException.class, () -> link.update("late", latest::add));
        });
    }
}
