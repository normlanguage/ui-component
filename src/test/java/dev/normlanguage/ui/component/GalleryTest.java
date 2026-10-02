package dev.normlanguage.ui.component;

import dev.normlanguage.ui.component.gallery.Gallery;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class GalleryTest extends FxTest {
    @Test void everyRequestedComponentConstructsAndLaysOutInARealWindow() throws Exception {
        fx(() -> {
            var app = new App();
            var viewport = new StackPane();
            app.setContent(viewport);
            var stage = new Stage(); stage.setScene(new Scene(app, 900, 650)); stage.show();
            var requested = Set.of("Button", "FloatButton", "Icon", "Typography", "Divider", "Flex", "Grid", "Layout", "Masonry", "Space", "Splitter", "Anchor", "Breadcrumb", "Dropdown", "Menu", "Pagination", "Steps", "Tabs", "AutoComplete", "Cascader", "Checkbox", "ColorPicker", "DatePicker", "Form", "Input", "InputNumber", "Mentions", "Radio", "Rate", "Select", "Slider", "Switch", "TimePicker", "Transfer", "TreeSelect", "Upload", "Avatar", "Badge", "Calendar", "Card", "Carousel", "Collapse", "Descriptions", "Empty", "Image", "List", "Listy", "Popover", "QRCode", "Segmented", "Statistic", "Table", "Tag", "Timeline", "Tooltip", "Tour", "Tree", "Alert", "Drawer", "Message", "Modal", "Notification", "Popconfirm", "Progress", "Result", "Skeleton", "Spin", "Watermark", "Affix", "App", "BorderBeam", "ConfigProvider", "Util");
            try {
                var examples = Gallery.examples(app);
                assertEquals(requested, examples.keySet());
                for (var entry : examples.entrySet()) {
                    var view = entry.getValue().get();
                    assertNotNull(view, entry.getKey());
                    viewport.getChildren().setAll(view);
                    app.applyCss(); app.layout();
                    assertTrue(view.getBoundsInParent().getWidth() > 0, entry.getKey());
                    viewport.getChildren().clear();
                    Gallery.closeTree(view);
                }
            } finally { app.close(); stage.close(); }
        });
    }
}
