package dev.normlanguage.ui.component.gallery;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TitledPane;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

final class DocumentationView {
    private DocumentationView() {}
    static Label paragraph(String text, String style) {
        var label = new Label(text);
        label.setWrapText(true);
        label.setMinWidth(0);
        label.setMaxWidth(Double.MAX_VALUE);
        label.getStyleClass().add(style);
        return label;
    }
    static VBox section(String id, String title) {
        var box = new VBox(16, paragraph(title, "gallery-section-heading"));
        box.setId("gallery-doc-" + id);
        box.setMinWidth(0);
        return box;
    }
    static VBox api(Gallery.Component component) {
        var box = section("api", "API · JavaFX");
        var type = GalleryDocumentation.type(component.name());
        var parent = type.getSuperclass();
        String inheritance = parent == null || parent == Object.class ? "" : " 继承 " + parent.getTypeName() + "，可继续使用其公开属性和事件。";
        box.getChildren().add(paragraph("以下声明来自当前编译的组件，列出本类的公开构造器、方法与嵌套类型。" + inheritance,
                "gallery-doc-paragraph"));
        for (var entry : GalleryDocumentation.api(component.name())) {
            var kind = new Label(entry.kind());
            kind.getStyleClass().add("gallery-api-kind");
            kind.setMinWidth(44);
            String signature = java.util.regex.Pattern.compile("(?:[a-z_][\\w$]*\\.)+([A-Z][\\w$]*)")
                    .matcher(entry.signature()).replaceAll(match -> match.group().equals("java.util.List")
                            ? match.group() : match.group(1).replace('$', '.'));
            var declaration = paragraph(signature, "gallery-api-signature");
            HBox.setHgrow(declaration, Priority.ALWAYS);
            var row = new HBox(16, kind, declaration);
            row.getStyleClass().add("gallery-api-row");
            box.getChildren().add(row);
        }
        return box;
    }
    static VBox source(Gallery.Component component) {
        var box = section("source", "示例源码");
        box.getChildren().add(paragraph("下面是当前运行示例与组件的完整 Java 源文件。展开后可搜索、选择和复制；同一文件中的其他场景也可作为组合参考。",
                "gallery-doc-paragraph"));
        int index = 0;
        for (var path : GalleryDocumentation.sources(component)) {
            String source = GalleryDocumentation.source(path);
            var area = new TextArea(source);
            area.setId("gallery-source-text-" + index);
            area.setEditable(false);
            area.setWrapText(false);
            area.setPrefRowCount(16);
            area.getStyleClass().add("gallery-source-text");
            var copy = new Button("复制源码");
            copy.setId("gallery-source-copy-" + index);
            copy.getStyleClass().add("text");
            copy.setOnAction(event -> {
                var clipboard = new ClipboardContent();
                clipboard.putString(source);
                Clipboard.getSystemClipboard().setContent(clipboard);
                copy.setText("已复制");
            });
            var search = new javafx.scene.control.TextField();
            search.setPromptText("查找方法或文本");
            var find = new Button("查找下一处");
            find.getStyleClass().add("text");
            var result = new Label();
            Runnable searchNext = () -> {
                String query = search.getText();
                if (query.isEmpty()) return;
                String displayed = area.getText();
                int found = displayed.indexOf(query, area.getSelection().getEnd());
                if (found < 0) found = displayed.indexOf(query);
                if (found < 0) result.setText("未找到");
                else { area.requestFocus(); area.selectRange(found, found + query.length()); result.setText(""); }
            };
            find.setOnAction(event -> searchNext.run());
            search.setOnAction(event -> searchNext.run());
            search.setMinWidth(90);
            HBox.setHgrow(search, Priority.ALWAYS);
            var toolbar = new HBox(10, search, find, result, copy);
            toolbar.getStyleClass().add("gallery-source-toolbar");
            var panel = new VBox(10, toolbar, area);
            panel.setMinWidth(0);
            var expander = new TitledPane(path.substring(path.lastIndexOf('/') + 1), panel);
            expander.getStyleClass().add("gallery-source-panel");
            expander.setId("gallery-source-panel-" + index++);
            expander.setExpanded(false);
            expander.setAnimated(false);
            expander.setMinWidth(0);
            box.getChildren().add(expander);
        }
        return box;
    }
}
