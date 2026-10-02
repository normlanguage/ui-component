package dev.normlanguage.ui.component;

import javafx.collections.FXCollections;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InputAdapterTest extends FxTest {
    @Test void numberAndColorLinksRoundTripWithoutEcho() throws Exception {
        fx(() -> {
            var rate = new Rate();
            var rating = InputAdapter.rateValue(rate);
            var ratings = new ArrayList<Integer>();
            rating.update(3, ratings::add);
            assertEquals(3, rate.getValue());
            assertTrue(ratings.isEmpty());
            rate.setValue(4);
            assertEquals(List.of(4), ratings);
            rating.close();
            var picker = new ColorPicker();
            var hex = InputAdapter.colorHexValue(picker);
            var colors = new ArrayList<String>();
            hex.update("#336699CC", colors::add);
            assertEquals("#336699CC", InputAdapter.colorHex(picker));
            picker.setValue(javafx.scene.paint.Color.web("#11223344"));
            assertEquals(List.of("#11223344"), colors);
            hex.close();
        });
    }

    @Test void multipleSelectionUsesOneTypedList() throws Exception {
        fx(() -> {
            var control = Select.multiple(FXCollections.observableArrayList("one", "two", "three"));
            var link = InputAdapter.multipleValue(control);
            var values = new ArrayList<List<String>>();
            link.update(List.of("one", "three"), values::add);
            assertEquals(List.of("one", "three"), List.copyOf(control.getSelectedItems()));
            assertTrue(values.isEmpty());
            control.deselect("one");
            assertEquals(List.of(List.of("three")), values);
            link.close();
        });
    }

    @Test void selectOptionsUpdateKeepsValidSelection() throws Exception {
        fx(() -> {
            var control = InputAdapter.select(List.of("one", "two"));
            control.setValue("two");
            InputAdapter.selectOptions(control, List.of("two", "three"));
            assertEquals("two", control.getValue());
            InputAdapter.selectOptions(control, List.of("three"));
            assertNull(control.getValue());
        });
    }

    @Test void treeFactoryAndCascaderOptionsKeepTypedValues() throws Exception {
        fx(() -> {
            var leaf = new Cascader.Item<>("leaf", "Leaf", List.<Cascader.Item<String>>of());
            var branch = new Cascader.Item<>("branch", "Branch", List.of(leaf));
            var tree = InputAdapter.treeSelect(List.of(branch));
            var chosen = InputAdapter.treeSelectValue(tree);
            chosen.update("leaf", value -> {});
            assertEquals("leaf", tree.getSelectedValue());
            chosen.close();
            InputAdapter.treeOptions(tree, List.of(new Cascader.Item<>("new", "New", List.<Cascader.Item<String>>of())));
            assertNull(tree.getSelectedValue());
            var cascader = new Cascader<>(List.of(branch));
            InputAdapter.cascaderOptions(cascader, List.of(branch));
            cascader.selectPath(List.of("branch", "leaf"));
            assertEquals(List.of("branch", "leaf"), cascader.getValue());
        });
    }

    @Test void transferOptionsPreserveOnlyAvailableSelections() throws Exception {
        fx(() -> {
            var transfer = new Transfer<>(List.of("one", "two"));
            transfer.select("two");
            InputAdapter.transferOptions(transfer, List.of("two", "three"));
            assertEquals(List.of("two"), List.copyOf(transfer.getSelectedItems()));
            assertEquals(List.of("three"), List.copyOf(transfer.getAvailableItems()));
            InputAdapter.transferOptions(transfer, List.of("three"));
            assertTrue(transfer.getSelectedItems().isEmpty());
            assertEquals(List.of("three"), List.copyOf(transfer.getAvailableItems()));
        });
    }

    @Test void uploadHandlerRunsFileActionAndReportsProgress() throws Exception {
        var invoked = new CountDownLatch(1);
        var file = new java.util.concurrent.atomic.AtomicReference<java.io.File>();
        fx(() -> {
            var upload = new Upload();
            InputAdapter.uploadTaskHandler(upload, (selected, progress) -> {
                file.set(selected);
                progress.report(0.5);
                invoked.countDown();
                return java.util.concurrent.CompletableFuture.completedFuture(null);
            });
            upload.addFile(new java.io.File("document.pdf"));
        });
        assertTrue(invoked.await(5, TimeUnit.SECONDS));
        assertEquals("document.pdf", file.get().getName());
    }

    @Test void rangeAndSearchVariantsTrackSelections() throws Exception {
        fx(() -> {
            var range = new Slider.Range(0, 100, 10, 40);
            var selected = InputAdapter.sliderRangeValue(range);
            var changes = new ArrayList<List<Double>>();
            selected.update(List.of(20.0, 70.0), changes::add);
            assertEquals(20.0, range.getStart());
            assertEquals(70.0, range.getEnd());
            assertTrue(changes.isEmpty());
            range.getEndSlider().setValue(80);
            assertEquals(List.of(List.of(20.0, 80.0)), changes);
            InputAdapter.sliderRangeBounds(range, 0, 120);
            assertEquals(120.0, range.getEndSlider().getMax());
            selected.close();
            var search = InputAdapter.selectSearchable(List.of("one", "two"));
            InputAdapter.searchableOptions(search, List.of("two", "three"));
            assertEquals(List.of("two", "three"), List.copyOf(search.getOptions()));
            var multiple = InputAdapter.selectMultiple(List.of("one", "two"));
            multiple.select("one");
            InputAdapter.multipleOptions(multiple, List.of("two", "three"));
            assertTrue(multiple.getSelectedItems().isEmpty());
        });
    }
}
