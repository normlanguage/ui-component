package dev.normlanguage.ui.component;

import dev.normlanguage.ui.component.gallery.Gallery;
import dev.normlanguage.ui.component.gallery.GalleryDocumentation;
import dev.normlanguage.ui.component.gallery.GalleryExamples;
import org.junit.jupiter.api.Test;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;

class GalleryDocumentationTest extends FxTest {
    @Test void everyPageExplainsItsActualExamplesAndExposesItsRealSource() throws Exception {
        fx(() -> {
            var app = new App();
            try {
                for (var component : Gallery.components(app)) {
                    var page = GalleryDocumentation.page(component.name());
                    assertFalse(page.summary().isBlank(), component.name());
                    assertFalse(page.whenToUse().isBlank(), component.name());
                    assertTrue(page.notes().size() >= 2, component.name());
                    var examples = GalleryExamples.create(component, app);
                    try {
                        assertEquals(examples.stream().map(GalleryExamples.Section::id).collect(Collectors.toSet()),
                                page.examples().stream().map(GalleryDocumentation.Example::sectionId).collect(Collectors.toSet()), component.name());
                        assertEquals(examples.size(), page.examples().size(), component.name());
                        for (var example : page.examples()) {
                            assertFalse(example.explanation().isBlank(), component.name());
                            assertFalse(example.interaction().isBlank(), component.name());
                        }
                        assertFalse(GalleryDocumentation.api(component.name()).isEmpty(), component.name());
                        for (var source : GalleryDocumentation.sources(component)) {
                            assertEquals(Files.readString(Path.of("src/main/java", source)), GalleryDocumentation.source(source));
                        }
                    } catch (java.io.IOException exception) {
                        throw new java.io.UncheckedIOException(exception);
                    } finally { examples.forEach(section -> Util.closeTree(section.content())); }
                }
                assertEquals(Set.copyOf(Gallery.components(app).stream().map(Gallery.Component::name).toList()), GalleryDocumentation.names());
            } finally { app.close(); }
        });
    }

    @Test void apiUsesCompiledSignaturesIncludingGenericTypesAndInheritedOwner() {
        assertTrue(GalleryDocumentation.api("Button").stream().anyMatch(entry -> entry.signature().equals("void setLoading(boolean value)")));
        assertTrue(GalleryDocumentation.api("Table").stream().anyMatch(entry -> entry.signature().contains("T")));
        assertEquals(javafx.scene.control.Button.class, GalleryDocumentation.type("Button").getSuperclass());
        assertThrows(IllegalArgumentException.class, () -> GalleryDocumentation.page("Missing"));
    }
}
