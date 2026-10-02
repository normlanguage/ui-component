package dev.normlanguage.ui.component;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.layout.HBox;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SelectionLinkTest extends FxTest {
    @Test void synchronizesReadOnlySelectionAndReleasesListener() throws Exception {
        fx(() -> {
            var selected = new ReadOnlyObjectWrapper<>("first");
            var received = new ArrayList<String>();
            var link = new SelectionLink<>(new HBox(), selected.getReadOnlyProperty(), selected::set);
            link.update("second", received::add);
            assertEquals("second", selected.get());
            assertTrue(received.isEmpty());
            selected.set("third");
            assertEquals(java.util.List.of("third"), received);
            link.close();
            link.close();
            selected.set("fourth");
            assertEquals(java.util.List.of("third"), received);
        });
    }

    @Test void explicitlyOwnedObservationIsReleasedOnce() throws Exception {
        fx(() -> {
            var selected = new ReadOnlyObjectWrapper<>("first");
            var released = new AtomicInteger();
            var link = new SelectionLink<>(new HBox(), selected.getReadOnlyProperty(), selected::set, released::incrementAndGet);
            link.close();
            link.close();
            assertEquals(1, released.get());
        });
    }

    @Test void unbindingKeepsNativeSelectionWithoutWritingTheOldModel() throws Exception {
        fx(() -> {
            var selected = new ReadOnlyObjectWrapper<>("first");
            var previous = new ArrayList<String>();
            var current = new ArrayList<String>();
            var link = new SelectionLink<>(new HBox(), selected.getReadOnlyProperty(), selected::set);
            link.update("first", previous::add);
            link.unbind();
            selected.set("second");
            assertEquals("second", selected.get());
            assertTrue(previous.isEmpty());
            link.update("second", current::add);
            selected.set("third");
            assertTrue(previous.isEmpty());
            assertEquals(java.util.List.of("third"), current);
            link.close();
        });
    }
}
