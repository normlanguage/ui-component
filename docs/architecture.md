# 架构与生命周期

公开模块与依赖以 [`ui.component` 声明](../ui/component/module.norm)和 [`ui.component.fx` 绑定声明](../ui/component/fx/module.norm)为准。当前职责边界如下：

| 层 | 唯一职责 | 源码入口 |
| --- | --- | --- |
| `ui.component` | 向 Norm 应用提供强类型 `Widget`、`Binding` 和组合式子内容 | [组件模块](../ui/component) |
| `ui` / `ui.fx` | 响应式重绘、按类型与键复用、JavaFX 节点混排及作用域关闭 | [`ui` 仓库](https://github.com/normlanguage/ui) / [`ui.fx` 仓库](https://github.com/normlanguage/ui-fx) |
| `ui.component.fx` | 将控件 JAR 和少量强类型投影桥暴露给 Norm Widget 实现 | [绑定声明](../ui/component/fx/module.norm) / [投影适配器](../src/main/java/dev/normlanguage/ui/component/NavigationLayoutAdapter.java) |
| JavaFX 控件 JAR | 原生节点、属性、布局、弹层与资源释放 | [Java 源码](../src/main/java/dev/normlanguage/ui/component) |
| `theme` | 由少量输入色生成完整主题；组件只消费结果 | [`theme` 仓库](https://github.com/normlanguage/theme) / [主题连接](../ui/component/fx/connection.norm) |

Widget 普通字段由 `ui` 观察；字段变化重建描述，同类同键的原生节点和子节点由渲染器保留。组件专属 JavaFX 投影在 [原生组件桥](../ui/component/native.norm)中创建，并由渲染器作用域负责回调失效和释放。原生 JavaFX 资源仍由相应控件的 `close()` 或 [通用关闭入口](../src/main/java/dev/normlanguage/ui/component/Util.java)处理。复杂容器把 Norm 子 Widget 交给统一子树协调，再把最终 JavaFX 节点列表投影进原生布局。

应用根与局部主题作用域分别由 [App](../src/main/java/dev/normlanguage/ui/component/App.java)和 [ConfigProvider](../src/main/java/dev/normlanguage/ui/component/ConfigProvider.java)持有。组件配置的定义位于 [ComponentConfig](../src/main/java/dev/normlanguage/ui/component/ComponentConfig.java)，颜色映射位于 [主题连接](../ui/component/fx/connection.norm)，样式入口位于 [components.css](../src/main/resources/dev/normlanguage/ui/component/components.css)。JavaFX 节点变更、弹层显示和关闭在 JavaFX 应用线程执行。

日期、时间和其他 JDK 类型由 [`jdk.base`](https://github.com/normlanguage/jdk-base) 定义。组件范围以 [模块导出](../ui/component/module.norm)和 [Gallery 示例](../samples/gallery)为索引，实际交互验证见 [Norm 测试](../ui/component/tests)及 [Java 测试](../src/test/java/dev/normlanguage/ui/component)。
