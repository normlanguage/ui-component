package dev.normlanguage.ui.component.gallery;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Executable;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public final class GalleryDocumentation {
    private GalleryDocumentation() {}
    public record Example(String sectionId, String explanation, String interaction) {
        public Example { Objects.requireNonNull(sectionId); Objects.requireNonNull(explanation); Objects.requireNonNull(interaction); }
    }
    public record Note(String title, String body) {
        public Note { Objects.requireNonNull(title); Objects.requireNonNull(body); }
    }
    public record Page(String summary, String whenToUse, List<Example> examples, List<Note> notes) {
        public Page {
            Objects.requireNonNull(summary); Objects.requireNonNull(whenToUse);
            examples = List.copyOf(examples); notes = List.copyOf(notes);
        }
        public Example example(String id) {
            return examples.stream().filter(example -> example.sectionId().equals(id)).findFirst().orElseThrow();
        }
    }
    public record ApiEntry(String kind, String signature) {}
    private static final Map<String, Page> PAGES;
    static {
        var pages = new LinkedHashMap<String, Page>();
        for (var domain : List.of(NavigationDocumentation.pages(), InputDocumentation.pages(),
                DisplayDocumentation.pages(), FeedbackDocumentation.pages())) {
            domain.forEach((name, page) -> {
                if (pages.putIfAbsent(name, page) != null) throw new IllegalStateException("Duplicate documentation: " + name);
            });
        }
        PAGES = Map.copyOf(pages);
    }
    public static Set<String> names() { return PAGES.keySet(); }
    public static Page page(String name) {
        var page = PAGES.get(name);
        if (page == null) throw new IllegalArgumentException("Unknown component: " + name);
        return page;
    }
    public static Class<?> type(String name) {
        page(name);
        try { return Class.forName("dev.normlanguage.ui.component." + name); }
        catch (ClassNotFoundException exception) { throw new IllegalStateException(exception); }
    }
    public static List<ApiEntry> api(String name) {
        var type = type(name);
        var entries = new ArrayList<ApiEntry>();
        for (var constructor : type.getConstructors()) entries.add(new ApiEntry("构造", type.getSimpleName() + parameters(constructor)));
        for (var method : type.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers()) || method.isSynthetic() || method.isBridge()) continue;
            String generics = Arrays.stream(method.getTypeParameters()).map(Object::toString).collect(Collectors.joining(", "));
            String prefix = Modifier.isStatic(method.getModifiers()) ? "static " : "";
            if (!generics.isEmpty()) prefix += "<" + generics + "> ";
            entries.add(new ApiEntry("方法", prefix + method.getGenericReturnType().getTypeName() + " " + method.getName() + parameters(method)));
        }
        for (var nested : type.getDeclaredClasses()) {
            if (!Modifier.isPublic(nested.getModifiers())) continue;
            String signature = nested.getSimpleName();
            if (nested.isEnum()) signature += " { " + Arrays.stream(nested.getEnumConstants()).map(Object::toString).collect(Collectors.joining(", ")) + " }";
            else if (nested.isRecord()) signature += "(" + Arrays.stream(nested.getRecordComponents()).map(field -> field.getGenericType().getTypeName() + " " + field.getName()).collect(Collectors.joining(", ")) + ")";
            entries.add(new ApiEntry("类型", signature));
        }
        return entries.stream().sorted(Comparator.comparing(ApiEntry::kind).thenComparing(ApiEntry::signature)).toList();
    }
    private static String parameters(Executable executable) {
        return "(" + Arrays.stream(executable.getParameters()).map(parameter -> parameter.getParameterizedType().getTypeName() + " " + parameter.getName()).collect(Collectors.joining(", ")) + ")";
    }
    public static List<String> sources(Gallery.Component component) {
        String root = "dev/normlanguage/ui/component/";
        String examples = component.name().equals("Button") ? "GalleryExamples" : switch (component.category()) {
            case GENERAL, LAYOUT, NAVIGATION -> "NavigationExampleSections";
            case INPUT -> "InputExampleSections";
            case DISPLAY -> Set.of("Popover", "Tooltip", "Tour").contains(component.name()) ? "FeedbackExamples" : "DisplayExampleSections";
            case FEEDBACK, OTHER -> "FeedbackExamples";
        };
        var sources = new ArrayList<>(List.of(root + "gallery/" + examples + ".java", root + component.name() + ".java"));
        if (examples.equals("NavigationExampleSections")) sources.add(root + "gallery/NavigationExamples.java");
        if (examples.equals("DisplayExampleSections")) sources.add(root + "gallery/DisplayExamples.java");
        return List.copyOf(sources);
    }
    public static String source(String path) {
        try (var stream = GalleryDocumentation.class.getResourceAsStream("/gallery-source/" + path)) {
            if (stream == null) throw new IllegalArgumentException("Missing source: " + path);
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) { throw new UncheckedIOException(exception); }
    }
}
