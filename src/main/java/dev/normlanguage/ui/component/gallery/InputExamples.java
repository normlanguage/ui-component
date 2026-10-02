package dev.normlanguage.ui.component.gallery;

import dev.normlanguage.ui.component.App;

import java.util.List;

public final class InputExamples {
    private InputExamples() {}

    public static List<Gallery.Component> components(App app) {
        return List.of(
                component("AutoComplete", "自动完成", app),
                component("Cascader", "级联选择", app),
                component("Checkbox", "多选框", app),
                component("ColorPicker", "颜色选择器", app),
                component("DatePicker", "日期选择框", app),
                component("Form", "表单", app),
                component("Input", "输入框", app),
                component("InputNumber", "数字输入框", app),
                component("Mentions", "提及", app),
                component("Radio", "单选框", app),
                component("Rate", "评分", app),
                component("Select", "选择器", app),
                component("Slider", "滑动输入条", app),
                component("Switch", "开关", app),
                component("TimePicker", "时间选择框", app),
                component("Transfer", "穿梭框", app),
                component("TreeSelect", "树选择", app),
                component("Upload", "上传", app));
    }

    private static Gallery.Component component(String name, String chinese, App app) {
        return new Gallery.Component(Gallery.Category.INPUT, name, chinese,
                () -> InputExampleSections.primary(name, app));
    }
}
