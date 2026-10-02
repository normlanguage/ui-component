package dev.normlanguage.ui.component;

import java.util.Objects;

public record TableSortState(String heading, boolean descending) {
    public TableSortState { Objects.requireNonNull(heading); }
}
