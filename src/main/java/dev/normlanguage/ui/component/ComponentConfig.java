package dev.normlanguage.ui.component;

import java.util.Locale;
import java.util.Objects;

public record ComponentConfig(String fontFamily, double fontSize, Density density,
                              double radius, boolean motionEnabled, Locale locale) {
    public enum Density {
        COMPACT(0.85), STANDARD(1.0), SPACIOUS(1.15);

        private final double scale;
        Density(double scale) { this.scale = scale; }
        public double scale() { return scale; }
    }

    public ComponentConfig {
        Objects.requireNonNull(fontFamily);
        Objects.requireNonNull(density);
        Objects.requireNonNull(locale);
        if (fontFamily.isBlank() || fontFamily.chars().anyMatch(Character::isISOControl))
            throw new IllegalArgumentException("Font family must be a nonempty CSS string");
        if (!Double.isFinite(fontSize) || fontSize <= 0)
            throw new IllegalArgumentException("Font size must be positive and finite");
        if (!Double.isFinite(radius) || radius < 0)
            throw new IllegalArgumentException("Radius must be nonnegative and finite");
    }

    public static ComponentConfig defaults() {
        return new ComponentConfig("System", 14, Density.STANDARD, 6, true, Locale.getDefault());
    }

    public String toCss() {
        String family = fontFamily.replace("\\", "\\\\").replace("\"", "\\\"");
        return "-fx-font-family: \"" + family + "\";"
                + "-fx-font-size: " + fontSize + "px;";
    }

    String stylesheet() {
        double space = 8 * density.scale();
        return ".norm-button { -fx-background-radius: " + radius + "px; -fx-border-radius: " + radius
                + "px; -fx-padding: " + space + "px " + (2 * space) + "px; }"
                + ".norm-input { -fx-background-radius: " + radius + "px; -fx-padding: "
                + space + "px " + (1.25 * space) + "px; }"
                + ".norm-card, .norm-popover, .norm-message { -fx-background-radius: " + radius
                + "px; -fx-border-radius: " + radius + "px; -fx-padding: " + (2 * space) + "px; }"
                + ".norm-space { -fx-spacing: " + space + "px; }"
                + ".norm-flex, .norm-grid { -fx-hgap: " + space + "px; -fx-vgap: " + space + "px; }";
    }
}
