package dev.normlanguage.ui.component;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class OverlayAdapterTest extends FxTest {
    @Test void popoverHidesWithoutClosingExternalBodyAndCanReopen() throws Exception {
        fx(() -> {
            var stage = new Stage();
            var content = new StackPane();
            var body = new Button("Body");
            var overlay = OverlayAdapter.popover();
            var anchor = new Button("Anchor");
            content.getChildren().add(overlay);
            stage.setScene(new Scene(content, 300, 200));
            stage.show();
            try {
                overlay.setContent(anchor, body);
                var hidden = new ArrayList<Boolean>();
                overlay.setOpen(true, () -> hidden.add(true));
                assertTrue(overlay.isShowing());
                overlay.setOpen(false, () -> hidden.add(true));
                assertFalse(overlay.isShowing());
                assertSame(body, overlay.contentNode());
                overlay.setOpen(true, () -> hidden.add(true));
                assertTrue(overlay.isShowing());
                overlay.close();
                assertFalse(overlay.isShowing());
                assertEquals(0, hidden.size());
                assertSame(body, overlay.contentNode());
            } finally {
                stage.close();
            }
        });
    }

    @Test void modalUsesExternalBodyAndReportsUserDismissal() throws Exception {
        fx(() -> {
            var stage = new Stage();
            var content = new StackPane();
            var body = new Button("Body");
            var overlay = OverlayAdapter.modal("Details");
            content.getChildren().add(overlay);
            stage.setScene(new Scene(content, 300, 200));
            stage.show();
            try {
                overlay.setContent(new Button("Open"), body);
                var hidden = new ArrayList<Boolean>();
                overlay.setOpen(true, () -> hidden.add(true));
                assertTrue(overlay.isShowing());
                overlay.dismiss();
                assertFalse(overlay.isShowing());
                assertEquals(1, hidden.size());
                overlay.close();
                assertSame(body, overlay.contentNode());
            } finally {
                stage.close();
            }
        });
    }

    @Test void drawerUsesOneOverlayHostAcrossOpenCycles() throws Exception {
        fx(() -> {
            var stage = new Stage();
            var body = new Button("Body");
            var overlay = OverlayAdapter.drawer("RIGHT");
            var layer = OverlayHost.wrap(overlay);
            stage.setScene(new Scene(layer, 400, 250));
            stage.show();
            try {
                overlay.setContent(new Button("Open"), body);
                var hidden = new ArrayList<Boolean>();
                overlay.setOpen(true, () -> hidden.add(true));
                assertTrue(overlay.isShowing());
                overlay.setOpen(false, () -> hidden.add(true));
                assertFalse(overlay.isShowing());
                assertTrue(hidden.isEmpty());
                overlay.setOpen(true, () -> hidden.add(true));
                overlay.dismiss();
                assertEquals(1, hidden.size());
                overlay.close();
                assertSame(body, overlay.contentNode());
            } finally { stage.close(); }
        });
    }

    @Test void tourNavigatesAndReportsFinalDismissal() throws Exception {
        fx(() -> {
            var stage = new Stage();
            var overlay = OverlayAdapter.tour();
            stage.setScene(new Scene(new StackPane(overlay), 400, 250));
            stage.show();
            try {
                overlay.setTourSteps(java.util.List.of("First", "Second"),
                        java.util.List.of("One", "Two"), java.util.List.of(new Button("A"), new Button("B")));
                var hidden = new ArrayList<Boolean>();
                overlay.setOpen(true, () -> hidden.add(true));
                assertTrue(overlay.isShowing());
                overlay.dismiss();
                assertFalse(overlay.isShowing());
                assertEquals(1, hidden.size());
                overlay.close();
            } finally { stage.close(); }
        });
    }

    @Test void modalRecreatesItsWindowOwnerAfterSceneRemount() throws Exception {
        fx(() -> {
            var stage = new Stage();
            var overlay = OverlayAdapter.modal("Details");
            var root = new StackPane(overlay);
            var original = new Scene(root, 300, 200);
            stage.setScene(original);
            stage.show();
            try {
                overlay.setContent(new Button("Open"), new Button("Body"));
                overlay.setOpen(true, () -> {});
                assertTrue(overlay.isShowing());
                stage.setScene(new Scene(new StackPane(), 300, 200));
                assertFalse(overlay.isShowing());
                stage.setScene(original);
                assertTrue(overlay.isShowing());
                overlay.close();
            } finally { stage.close(); }
        });
    }

    @Test void tooltipUnbindsControlledVisibilityAndReturnsToHoverBehavior() throws Exception {
        fx(() -> {
            var stage = new Stage();
            var overlay = OverlayAdapter.tooltip("Help");
            stage.setScene(new Scene(new StackPane(overlay), 300, 200));
            stage.show();
            try {
                overlay.setContent(new Button("Target"), null);
                var dismissed = new ArrayList<Boolean>();
                overlay.setOpen(true, () -> dismissed.add(true));
                assertTrue(overlay.isShowing());
                overlay.unbindOpen();
                assertFalse(overlay.isShowing());
                assertTrue(dismissed.isEmpty());
                overlay.setOpen(true, () -> dismissed.add(true));
                assertTrue(overlay.isShowing());
                overlay.dismiss();
                assertEquals(1, dismissed.size());
                overlay.close();
            } finally { stage.close(); }
        });
    }
}
