package dev.normlanguage.ui.component;

@FunctionalInterface
public interface Handle extends AutoCloseable {
    @Override void close();
}
