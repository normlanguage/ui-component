package dev.normlanguage.ui.component;

import dev.normlanguage.ui.component.gallery.Gallery;
import dev.normlanguage.ui.component.gallery.GalleryView;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@Tag("theme-rendering")
class GalleryInteractionTest extends FxTest {
    @Test void feedbackControlsOpenRealSurfacesAndMotionFramesAreCaptured() throws Exception {
        var light = Files.readString(Path.of("build/themes/light.css"));
        var actions = java.util.Map.of("Drawer", "编辑项目设置", "Modal", "编辑成员资料", "Popover", "查看项目详情",
                "Message", "保存更改", "Notification", "模拟构建完成", "Popconfirm", "删除项目", "Tour", "开始引导");
        for (var entry : actions.entrySet()) {
            var app = new App[1];
            var view = new GalleryView[1];
            var stage = new Stage[1];
            fx(() -> {
                app[0] = new App();
                var c = Gallery.defaultConfig();
                app[0].setConfig(new ComponentConfig(c.fontFamily(), 18, c.density(), c.radius(), true, c.locale()));
                view[0] = Gallery.createView(app[0], List.of(new Gallery.Palette("蓝色", light, light)));
                view[0].selectComponent(entry.getKey());
                app[0].setContent(view[0]);
                stage[0] = new Stage(); stage[0].setScene(new Scene(app[0], 1440, 1000)); stage[0].show();
                app[0].applyCss(); app[0].layout();
                var action = view[0].lookup("#gallery-example").lookupAll(".button").stream()
                        .filter(node -> node instanceof javafx.scene.control.Button button && button.getText().equals(entry.getValue()))
                        .map(javafx.scene.control.Button.class::cast).findFirst().orElseThrow();
                action.fire();
            });
            try {
                if (entry.getKey().equals("Drawer")) {
                    for (int frame = 0; frame < 4; frame++) {
                        Thread.sleep(65);
                        int index = frame;
                        fx(() -> capture(app[0], Path.of("build/previews/audit-after/motion/drawer-" + index + ".png")));
                    }
                } else Thread.sleep(300);
                fx(() -> {
                    if (entry.getKey().equals("Message")) assertEquals(1, app[0].getMessages().getVisibleCount());
                    else if (entry.getKey().equals("Notification")) assertEquals(1, app[0].getNotifications().getVisibleCount());
                    else if (entry.getKey().equals("Drawer")) assertTrue(app[0].getChildren().size() > 1);
                    else assertTrue(Window.getWindows().stream().anyMatch(window -> window != stage[0] && window.isShowing()));
                    capture(app[0], Path.of("build/previews/audit-after/interaction/" + entry.getKey() + ".png"));
                    int index = 0;
                    for (var window : List.copyOf(Window.getWindows()))
                        if (window != stage[0] && window.isShowing() && window.getScene() != null)
                            capture(window.getScene().getRoot(), Path.of("build/previews/audit-after/interaction/" + entry.getKey() + "-popup-" + index++ + ".png"));
                });
            } finally { fx(() -> { app[0].close(); stage[0].close(); }); }
        }
    }
}
