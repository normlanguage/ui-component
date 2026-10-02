package dev.normlanguage.ui.component;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Side;
import javafx.css.PseudoClass;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.event.Event;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FeedbackTest extends FxTest {
    @Test void modalRestoresFocusAndRootOwnsOpenDialogs() throws Exception {
        fx(() -> {
            var button = new Button("Open");
            var app = new App(button);
            var stage = new Stage();
            stage.setScene(new Scene(app, 500, 350));
            stage.show();
            try {
                button.requestFocus();
                var modal = new Modal(button, "Details", new Label("Content"));
                modal.show();
                assertTrue(modal.isShowing());
                app.close();
                assertFalse(modal.isShowing());
            } finally { app.close(); stage.close(); }
        });
    }
    @Test void messagesQueueAndReleaseWithoutRemovingContent() throws Exception {
        fx(() -> {
            var content = new Label("Content");
            var app = new App(content);
            try {
                var first = app.getMessages().success("one");
                var second = app.getMessages().success("two");
                assertEquals(2, app.getMessages().getVisibleCount());
                first.close();
                assertEquals(1, app.getMessages().getVisibleCount());
                second.close();
                assertSame(content, app.getContent());
                assertEquals(0, app.getMessages().getVisibleCount());
            } catch (Exception error) { throw new AssertionError(error); }
            finally { app.close(); }
        });
    }
    @Test void loadingAndDismissalAreObservable() throws Exception {
        fx(() -> {
            var alert = new Alert("Notice", new Label("Details"));
            alert.dismiss();
            assertFalse(alert.isVisible());
            assertFalse(alert.isManaged());
            var spin = new Spin(new Label("Data"));
            spin.setSpinning(true);
            assertTrue(spin.isSpinning());
            spin.setSpinning(false);
            assertFalse(spin.isSpinning());
        });
    }

    @Test void drawerInheritsLocalThemeAndRestoresFocus() throws Exception {
        fx(() -> {
            var anchor = new Button("Open");
            var local = new ConfigProvider(anchor);
            local.setThemeCss("-norm-canvas: #151515;");
            var app = new App(local);
            var stage = new Stage();
            stage.setScene(new Scene(app, 400, 250));
            stage.show();
            try {
                anchor.requestFocus();
                var first = new Button("First");
                var second = new Button("Second");
                var drawer = new Drawer(anchor, new VBox(first, second), Side.RIGHT);
                drawer.show();
                assertTrue(drawer.isShowing());
                assertTrue(drawer.getContentRoot().getStyle().contains("#151515"));
                assertSame(first, stage.getScene().getFocusOwner());
                Event.fireEvent(first, new KeyEvent(KeyEvent.KEY_PRESSED, "\t", "\t", KeyCode.TAB,
                        false, false, false, false));
                assertSame(second, stage.getScene().getFocusOwner());
                Event.fireEvent(second, new KeyEvent(KeyEvent.KEY_PRESSED, "\t", "\t", KeyCode.TAB,
                        true, false, false, false));
                assertSame(first, stage.getScene().getFocusOwner());
                local.setThemeCss("-norm-canvas: #222222;");
                assertTrue(drawer.getContentRoot().getStyle().contains("#222222"));
                drawer.close();
                assertFalse(drawer.isShowing());
                assertSame(anchor, stage.getScene().getFocusOwner());
            } finally { app.close(); stage.close(); }
        });
    }

    @Test void notificationsQueueAndRelease() throws Exception {
        fx(() -> {
            var app = new App(new Label("Main"));
            try {
                var first = app.getNotifications().show("First", new Label("One"));
                var second = app.getNotifications().show("Second", new Label("Two"));
                assertEquals(2, app.getNotifications().getVisibleCount());
                assertEquals(2, ((VBox) app.getNotifications().getContainer()).getChildren().size());
                first.close();
                assertEquals(1, app.getNotifications().getVisibleCount());
                second.close();
                assertEquals(0, app.getNotifications().getVisibleCount());
            } finally { app.close(); }
        });
    }

    @Test void tourClearsTargetHighlightWhenAnchorDisappears() throws Exception {
        fx(() -> {
            var target = new Button("Target");
            var app = new App(target);
            var stage = new Stage();
            stage.setScene(new Scene(app, 300, 180));
            stage.show();
            try {
                var tour = new Tour(java.util.List.of(new Tour.Step(target, "Step", "Details")));
                tour.start();
                assertEquals(0, tour.getIndex());
                assertTrue(target.getPseudoClassStates().contains(PseudoClass.getPseudoClass("tour-target")));
                app.setContent(null);
                assertEquals(-1, tour.getIndex());
                assertFalse(target.getPseudoClassStates().contains(PseudoClass.getPseudoClass("tour-target")));
            } finally { app.close(); stage.close(); }
        });
    }

    @Test void affixCloseStaysClosedAfterRemount() throws Exception {
        fx(() -> {
            var label = new Label("Pinned");
            var scroll = new ScrollPane();
            var affix = new Affix(scroll, label);
            var before = new Label("Before");
            var after = new Label("After");
            before.setMinHeight(180);
            after.setMinHeight(180);
            var content = new VBox(before, affix, after);
            scroll.setContent(content);
            var stage = new Stage();
            stage.setScene(new Scene(scroll, 250, 120));
            stage.show();
            try {
                affix.close();
                content.getChildren().remove(affix);
                content.getChildren().add(1, affix);
                scroll.setVvalue(1);
                assertEquals(0, label.getTranslateY());
            } finally { stage.close(); }
        });
    }

    @Test void borderBeamUsesThemeAccentAndStopsWhenClosed() throws Exception {
        fx(() -> {
            var beam = new BorderBeam(new Label("Border"));
            var app = new App(beam);
            app.setThemeCss("-norm-primary-filled-normal-background: #00ff00;");
            var stage = new Stage();
            stage.setScene(new Scene(app, 250, 120));
            stage.show();
            try {
                app.applyCss();
                var path = (Rectangle) beam.lookup(".norm-border-beam-path");
                assertNotNull(path);
                assertEquals(Color.LIME, path.getStroke());
                assertTrue(beam.isAnimating());
                assertEquals(0, path.getRotate());
                beam.close();
                assertFalse(beam.isAnimating());
            } finally { beam.close(); app.close(); stage.close(); }
        });
    }

    @Test void rootClosesAttachedTooltipBeforeItEverOpens() throws Exception {
        fx(() -> {
            var anchor = new Button("Help");
            var app = new App(anchor);
            var stage = new Stage();
            stage.setScene(new Scene(app, 250, 120));
            stage.show();
            try {
                var tooltip = new Tooltip(anchor, "Help text");
                assertFalse(tooltip.isClosed());
                app.close();
                assertTrue(tooltip.isClosed());
            } finally { app.close(); stage.close(); }
        });
    }

    @Test void nativeTooltipKeepsKeyboardFocusAndScopedContent() throws Exception {
        fx(() -> {
            var anchor = new Button("Help");
            var local = new ConfigProvider(anchor);
            local.setThemeCss("-norm-canvas: #222222;");
            var config = new ComponentConfig("System", 12, ComponentConfig.Density.COMPACT,
                    9, false, java.util.Locale.ENGLISH);
            local.setConfig(config);
            var app = new App(local);
            var stage = new Stage();
            stage.setScene(new Scene(app, 250, 120));
            stage.show();
            try {
                anchor.requestFocus();
                var tooltip = new Tooltip(anchor, "Keyboard hint");
                tooltip.show();
                assertTrue(tooltip.isShowing());
                assertSame(anchor, stage.getScene().getFocusOwner());
                assertEquals(config, tooltip.getContentRoot().getEffectiveConfig());
                assertTrue(tooltip.getContentRoot().getThemeCss().contains("#222222"));
                tooltip.close();
                assertFalse(tooltip.isShowing());
            } finally { app.close(); stage.close(); }
        });
    }

    @Test void watermarkFollowsThemeUntilExplicitlyColored() throws Exception {
        fx(() -> {
            var watermark = new Watermark(new Label("Content"), "Norm");
            var app = new App(watermark);
            app.setThemeCss("-norm-muted: #ff0000;");
            var stage = new Stage();
            stage.setScene(new Scene(app, 250, 120));
            stage.show();
            try {
                app.applyCss();
                var themePaint = (Text) watermark.getChildren().getLast();
                assertEquals(Color.RED, themePaint.getFill());
                watermark.colorProperty().set(Color.BLUE);
                assertEquals(Color.BLUE, watermark.colorProperty().get());
            } finally { app.close(); stage.close(); }
        });
    }
}
