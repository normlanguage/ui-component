package dev.normlanguage.ui.component;

import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class LifecycleTest extends FxTest {
    @Test void staleThemeCallbacksAndFailingDisconnectCannotSurviveDetach() throws Exception {
        fx(() -> {
            var callbacks = new java.util.ArrayList<java.util.function.Consumer<String>>();
            var releases = new AtomicInteger();
            var scope = new ConfigProvider();
            scope.connectTheme(callback -> {
                callbacks.add(callback);
                callback.accept("-norm-canvas: #ffffff;");
                return releases::incrementAndGet;
            });
            var parent = new VBox(scope); new Scene(parent);
            parent.getChildren().clear();
            callbacks.getFirst().accept("-norm-canvas: #111111;");
            assertEquals("-norm-canvas: #ffffff;", scope.getThemeCss());
            parent.getChildren().add(scope);
            callbacks.getFirst().accept("-norm-canvas: #222222;");
            assertEquals("-norm-canvas: #ffffff;", scope.getThemeCss());
            scope.close();
            assertEquals(2, releases.get());
            var failures = new AtomicInteger();
            var broken = new App(); new Scene(broken);
            broken.connectTheme(callback -> () -> { failures.incrementAndGet(); throw new IllegalStateException("failure"); });
            assertThrows(IllegalStateException.class, broken::close);
            broken.close();
            assertEquals(1, failures.get());
        });
    }
    @Test void themeSubscriptionFollowsSceneMembership() throws Exception {
        fx(() -> {
            var subscriptions = new AtomicInteger();
            var root = new App();
            root.connectTheme(changed -> {
                subscriptions.incrementAndGet();
                changed.accept("-norm-canvas: #ffffff;");
                return subscriptions::decrementAndGet;
            });
            assertEquals(0, subscriptions.get());
            var parent = new VBox(root);
            new Scene(parent);
            assertEquals(1, subscriptions.get());
            parent.getChildren().clear();
            assertEquals(0, subscriptions.get());
            parent.getChildren().add(root);
            assertEquals(1, subscriptions.get());
            root.close();
            assertEquals(0, subscriptions.get());
        });
    }
    @Test void paletteChangePreservesEditingAndScopedPopup() throws Exception {
        fx(() -> {
            var input = new Input();
            var local = new ConfigProvider(input);
            local.setThemeCss("-norm-canvas: #111111; -norm-text: #eeeeee;");
            var root = new App(local);
            var stage = new Stage();
            stage.setScene(new Scene(root, 400, 250));
            stage.show();
            try {
                input.setText("editing");
                input.selectRange(1, 4);
                var popover = new Popover(input, new javafx.scene.control.Label("details"));
                popover.show();
                root.setThemeCss("-norm-canvas: #ffffff; -norm-text: #222222;");
                assertEquals("editing", input.getText());
                assertEquals(1, input.getSelection().getStart());
                assertEquals(4, input.getSelection().getEnd());
                assertTrue(popover.isShowing());
                assertTrue(popover.getContentRoot().getStyle().contains("#111111"));
                root.close();
                assertFalse(popover.isShowing());
            } finally { root.close(); stage.close(); }
        });
    }

    @Test void detachIsReusableAndCloseReleasesOwnedResourcesOnce() throws Exception {
        fx(() -> {
            var root = new App(new Button("Save"));
            var container = new VBox(root);
            var scene = new Scene(container);
            var releases = new AtomicInteger();
            root.own(releases::incrementAndGet);
            container.getChildren().clear();
            assertFalse(root.isClosed());
            container.getChildren().add(root);
            assertSame(scene, root.getScene());
            root.close();
            root.close();
            assertEquals(1, releases.get());
            assertThrows(IllegalStateException.class, () -> root.own(() -> {}));
        });
    }
}
