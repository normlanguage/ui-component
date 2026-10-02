package dev.normlanguage.ui.component;

import javafx.animation.KeyValue;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MotionTest extends FxTest {
    @Test void interruptedMotionUsesCurrentValuesAndReducedMotionSettlesImmediately() throws Exception {
        fx(() -> {
            var node = new VBox();
            var root = new App(node);
            new Scene(root, 400, 300);
            var motion = new Motion(node);
            motion.enter(0, 16);
            assertEquals(0, node.getOpacity());
            assertEquals(16, node.getTranslateY());
            node.setOpacity(0.5);
            motion.animate(Motion.FAST, new KeyValue(node.opacityProperty(), 0.8, Motion.EASING));
            assertEquals(0.5, node.getOpacity());
            var c = root.getEffectiveConfig();
            root.setConfig(new ComponentConfig(c.fontFamily(), c.fontSize(), c.density(), c.radius(), false, c.locale()));
            assertEquals(0.8, node.getOpacity());
            motion.enter(0, 16);
            assertEquals(1, node.getOpacity());
            assertEquals(0, node.getTranslateY());
            motion.close(); root.close();
        });
    }
    @Test void detachmentCompletesMotionAndSwitchHonorsLiveConfiguration() throws Exception {
        fx(() -> {
            var control = new Switch("通知");
            var root = new App(control);
            new Scene(root, 400, 300);
            root.applyCss(); root.layout();
            control.setSelected(true);
            var thumb = control.lookup(".norm-switch-thumb");
            assertEquals(-7, thumb.getTranslateX());
            var c = root.getEffectiveConfig();
            root.setConfig(new ComponentConfig(c.fontFamily(), c.fontSize(), c.density(), c.radius(), false, c.locale()));
            assertEquals(7, thumb.getTranslateX());
            var motion = new Motion(control);
            motion.enter(0, 16);
            root.setContent(null);
            assertEquals(1, control.getOpacity());
            assertEquals(0, control.getTranslateY());
            motion.close(); control.close(); root.close();
        });
    }
}
