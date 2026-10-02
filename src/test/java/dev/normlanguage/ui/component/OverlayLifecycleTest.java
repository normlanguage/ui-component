package dev.normlanguage.ui.component;

import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class OverlayLifecycleTest extends FxTest {
    private static final class ResourceNode extends VBox implements AutoCloseable {
        private final AtomicInteger closes = new AtomicInteger();
        @Override public void close() { closes.incrementAndGet(); }
    }

    @Test void ownedAndExternalContentHaveOneOwner() throws Exception {
        fx(() -> {
            var old = new ResourceNode();
            var current = new ResourceNode();
            var owner = new App(old);
            owner.setContent(current);
            assertEquals(1, old.closes.get());
            owner.close();
            assertEquals(1, current.closes.get());

            var externallyOwned = new ResourceNode();
            var rendered = new App(externallyOwned, ContentOwnership.EXTERNAL);
            rendered.close();
            assertEquals(0, externallyOwned.closes.get());
            assertNull(rendered.getContent());
            externallyOwned.close();
            assertEquals(1, externallyOwned.closes.get());
        });
    }

    @Test void closeNodeDoesNotCloseItsChildren() throws Exception {
        fx(() -> {
            var child = new ResourceNode();
            var parent = new VBox(child);
            Util.closeNode(parent);
            assertEquals(0, child.closes.get());
            Util.closeTree(parent);
            assertEquals(1, child.closes.get());
        });
    }

    @Test void nestedOwnedScopesCloseEachResourceOnce() throws Exception {
        fx(() -> {
            var child = new ResourceNode();
            var local = new ConfigProvider(child);
            var app = new App(local);
            Util.closeTree(app);
            assertEquals(1, child.closes.get());
            assertTrue(local.isClosed());
            assertTrue(app.isClosed());
        });
    }

    @Test void modalAndPopoverCanHideAndReopenWithoutApp() throws Exception {
        fx(() -> {
            var anchor = new Button("Open");
            var stage = new Stage();
            stage.setScene(new Scene(new VBox(anchor), 340, 220));
            stage.show();
            try {
                var modalBody = new ResourceNode();
                var modal = new Modal(anchor, "Details", modalBody);
                modal.show();
                modal.hide();
                assertFalse(modal.isShowing());
                assertEquals(0, modalBody.closes.get());
                modal.show();
                modal.close();
                assertEquals(1, modalBody.closes.get());
                assertThrows(IllegalStateException.class, modal::show);

                var popBody = new ResourceNode();
                var popover = new Popover(anchor, popBody);
                popover.show();
                popover.hide();
                assertFalse(popover.isShowing());
                assertEquals(0, popBody.closes.get());
                popover.show();
                popover.close();
                assertEquals(1, popBody.closes.get());
                assertThrows(IllegalStateException.class, popover::show);
            } finally { stage.close(); }
        });
    }

    @Test void drawerUsesExplicitHostWithoutAppAndCanReopen() throws Exception {
        fx(() -> {
            var anchor = new Button("Open");
            var host = OverlayHost.wrap(new VBox(anchor));
            var stage = new Stage();
            stage.setScene(new Scene(host, 400, 260));
            stage.show();
            try {
                var body = new ResourceNode();
                var drawer = new Drawer(anchor, body, Side.RIGHT);
                drawer.show();
                assertTrue(drawer.isShowing());
                assertEquals(1, host.overlayLayer().getChildren().size());
                drawer.hide();
                assertFalse(drawer.isShowing());
                assertEquals(0, host.overlayLayer().getChildren().size());
                assertEquals(0, body.closes.get());
                drawer.show();
                drawer.close();
                assertEquals(1, body.closes.get());
                assertThrows(IllegalStateException.class, drawer::show);
            } finally { stage.close(); }
        });
    }

    @Test void externalOverlayBodyStaysOwnedByRenderer() throws Exception {
        fx(() -> {
            var anchor = new Button("Open");
            var body = new ResourceNode();
            var stage = new Stage();
            stage.setScene(new Scene(new VBox(anchor), 340, 220));
            stage.show();
            try {
                var modal = new Modal(anchor, "Details", body, ContentOwnership.EXTERNAL);
                modal.show();
                modal.close();
                assertEquals(0, body.closes.get());
                var popover = new Popover(anchor, body, ContentOwnership.EXTERNAL);
                popover.show();
                popover.close();
                assertEquals(0, body.closes.get());
            } finally { stage.close(); }
        });
    }

    @Test void dropdownAndPopconfirmRemainReusableAfterHide() throws Exception {
        fx(() -> {
            var dropdown = new Dropdown("Menu", new Label("Item"));
            var anchor = new Button("Confirm");
            var host = new VBox(dropdown, anchor);
            var stage = new Stage();
            stage.setScene(new Scene(host, 340, 220));
            stage.show();
            try {
                dropdown.fire();
                assertTrue(dropdown.isShowing());
                dropdown.fire();
                assertFalse(dropdown.isShowing());
                dropdown.fire();
                assertTrue(dropdown.isShowing());
                dropdown.close();

                var confirm = new Popconfirm(anchor, "Delete?", () -> {});
                confirm.show();
                confirm.hide();
                confirm.show();
                assertTrue(confirm.isShowing());
                confirm.close();
            } finally { stage.close(); }
        });
    }
}
