package dev.normlanguage.ui.component.gallery;

import java.util.HashSet;
import java.util.List;
import java.util.Comparator;

final class ComponentCatalog {
    private ComponentCatalog() {}

    static List<Gallery.Component> validate(List<Gallery.Component> components) {
        var names = new HashSet<String>();
        for (var component : components) {
            if (component.category() == null || component.name() == null || component.name().isBlank()
                    || component.chinese() == null || component.chinese().isBlank() || component.factory() == null)
                throw new IllegalStateException("Incomplete gallery component");
            if (!names.add(component.name())) throw new IllegalStateException("Duplicate gallery component: " + component.name());
        }
        return components.stream().sorted(Comparator.comparing(Gallery.Component::category)
                .thenComparing(Gallery.Component::name)).toList();
    }
}
