package dev.normlanguage.ui.component;

import javafx.collections.ListChangeListener;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;

class SidebarComponentsTest extends FxTest {
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
    @Test void buttonIconsAreReusedAndCanBeRemoved() throws Exception {
        fx(() -> {
            var button = new Button("Connect");
            button.setIconLiteral("mdi2l-link-variant");
            var graphic = button.getGraphic();
            assertInstanceOf(Icon.class, graphic);
            button.setIconLiteral("mdi2l-link-variant");
            assertSame(graphic, button.getGraphic());
            button.setIconLiteral(null);
            assertNull(button.getGraphic());
            button.close();
        });
    }
}
