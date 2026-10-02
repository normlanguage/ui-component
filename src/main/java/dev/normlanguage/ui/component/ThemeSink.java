package dev.normlanguage.ui.component;

import java.util.function.Consumer;

public final class ThemeSink {
    private final Consumer<String> receiver;
    ThemeSink(Consumer<String> receiver) { this.receiver = receiver; }
    public void publish(String css) { receiver.accept(css); }
}
