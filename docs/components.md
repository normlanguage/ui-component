# 组件索引

组件的名称、构造参数和行为以 [公开 Norm 模块](../ui/component/module.norm)所导出的源码为准；此页只提供领域入口，避免维护第二份 API 清单。JavaFX 控件的原生实现位于 [Java 源码目录](../src/main/java/dev/normlanguage/ui/component)，与 Norm Widget 的投影边界见 [架构](architecture.md)。

| 领域 | Norm Widget | 交互示例 |
| --- | --- | --- |
| 通用 | [general.norm](../ui/component/general.norm) | [general.norm](../samples/gallery/general.norm) |
| 布局 | [layout.norm](../ui/component/layout.norm) | [layout.norm](../samples/gallery/layout.norm) |
| 导航 | [navigation.norm](../ui/component/navigation.norm) | [navigation.norm](../samples/gallery/navigation.norm) |
| 数据录入 | [inputs.norm](../ui/component/inputs.norm) | [inputs.norm](../samples/gallery/inputs.norm) |
| 数据展示 | [display.norm](../ui/component/display.norm) | [display.norm](../samples/gallery/display.norm) |
| 反馈 | [feedback.norm](../ui/component/feedback.norm) | [overlays.norm](../samples/gallery/overlays.norm) |
| 状态与提示 | [status.norm](../ui/component/status.norm)、[notices.norm](../ui/component/notices.norm) | [status.norm](../samples/gallery/status.norm)、[overlays.norm](../samples/gallery/overlays.norm) |
| 根配置与组合 | [configuration.norm](../ui/component/configuration.norm)、[surface.norm](../ui/component/surface.norm) | [status.norm](../samples/gallery/status.norm) |

[Norm 组件测试](../ui/component/tests)覆盖 Widget 入口，[JavaFX 定向测试](../src/test/java/dev/normlanguage/ui/component)覆盖原生控件与投影桥。原生控件需要直接嵌入 JavaFX 应用时，以对应 Java 类为入口；Norm 应用直接使用本表的 Widget。
