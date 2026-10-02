package dev.normlanguage.ui.component;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NoticeHostTest extends FxTest {
    @Test void controlledNoticeClosesOnceAndCanReopen() throws Exception {
        fx(() -> {
            var host = NoticeHost.notification();
            var app = new App(host);
            var stage = new Stage();
            stage.setScene(new Scene(app, 400, 300));
            stage.show();
            var dismissed = new AtomicInteger();
            host.content(new Label("Details"));
            host.update("Ready", 3000, true, dismissed::incrementAndGet);
            assertEquals(1, app.getNotifications().getVisibleCount());
            host.update("Ready", 3000, true, dismissed::incrementAndGet);
            assertEquals(1, app.getNotifications().getVisibleCount());
            host.dismiss();
            assertEquals(1, dismissed.get());
            assertEquals(0, app.getNotifications().getVisibleCount());
            host.update("Ready", 3000, true, dismissed::incrementAndGet);
            assertEquals(1, app.getNotifications().getVisibleCount());
            host.close();
            assertEquals(1, dismissed.get());
            assertEquals(0, app.getNotifications().getVisibleCount());
            app.close();
            stage.close();
        });
    }
}
