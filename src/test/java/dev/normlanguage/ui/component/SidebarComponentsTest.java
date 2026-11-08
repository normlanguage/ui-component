package dev.normlanguage.ui.component;

import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class SidebarComponentsTest extends FxTest {
    @Test void avatarPaintsImageAndRestoresFallback() throws Exception {
        fx(() -> {
            var avatar = new Avatar("A");
            var image = new javafx.scene.image.WritableImage(36, 36);
            for (int y = 0; y < 36; y++) for (int x = 0; x < 36; x++)
                image.getPixelWriter().setColor(x, y, javafx.scene.paint.Color.CORNFLOWERBLUE);
            avatar.setImage(image);
            new Scene(avatar, 36, 36);
            avatar.applyCss();
            avatar.layout();
            var snapshot = avatar.snapshot(null, null);
            assertEquals(javafx.scene.paint.Color.CORNFLOWERBLUE, snapshot.getPixelReader().getColor(18, 18));
            avatar.setImage(null);
            assertTrue(avatar.getChildren().get(0).isVisible());
            assertFalse(avatar.getChildren().get(1).isVisible());
        });
    }
    @Test void avatarSupportsTransparentLargeImages() throws Exception {
        fx(() -> {
            var avatar = new Avatar("A");
            var image = new javafx.scene.image.WritableImage(100, 100);
            for (int y = 15; y < 85; y++) for (int x = 15; x < 85; x++)
                image.getPixelWriter().setColor(x, y, javafx.scene.paint.Color.CORNFLOWERBLUE);
            avatar.setImage(image);
            new Scene(avatar, 36, 36);
            avatar.applyCss(); avatar.layout();
            var snapshot = avatar.snapshot(null, null);
            assertEquals(javafx.scene.paint.Color.CORNFLOWERBLUE, snapshot.getPixelReader().getColor(18, 18));
        });
    }
    @Test void unchangedTabsRetainSelectionAndContent() throws Exception {
        fx(() -> {
            var tabs = new Tabs();
            var pages = List.<javafx.scene.Node>of(new Label("one"), new Label("two"));
            NavigationLayoutAdapter.tabs(tabs, List.of("one", "two"), pages);
            tabs.getSelectionModel().select(1);
            var changes = new AtomicInteger();
            tabs.getTabs().addListener((ListChangeListener<javafx.scene.control.Tab>) change -> changes.incrementAndGet());
            NavigationLayoutAdapter.tabs(tabs, List.of("one", "two"), pages);
            assertEquals(0, changes.get());
            assertEquals(1, tabs.getSelectionModel().getSelectedIndex());
            assertSame(pages.get(1), tabs.getTabs().get(1).getContent());
        });
    }
    @Test void iconOnlyButtonsKeepAccessibleNamesAndHints() throws Exception {
        fx(() -> {
            var button = new Button("Refresh");
            button.setIconLiteral("mdi2r-refresh");
            button.setIconSize(22);
            button.setIconOnly(true);
            assertEquals(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY, button.getContentDisplay());
            assertEquals("Refresh", button.getAccessibleText());
            assertEquals("Refresh", button.getTooltip().getText());
            button.setIconOnly(false);
            assertEquals(javafx.scene.control.ContentDisplay.LEFT, button.getContentDisplay());
            assertNull(button.getTooltip());
            button.close();
        });
    }
    @Test void buttonIconsAreReusedAndCanBeRemoved() throws Exception {
        fx(() -> {
            var button = new Button("Connect");
            button.setIconLiteral("mdi2l-link-variant");
            var graphic = button.getGraphic();
            assertInstanceOf(javafx.scene.layout.StackPane.class, graphic);
            button.setIconLiteral("mdi2l-link-variant");
            assertSame(graphic, button.getGraphic());
            button.setIconLiteral(null);
            assertNull(button.getGraphic());
            button.close();
        });
    }
}
