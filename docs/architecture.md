# 架构与生命周期

公开契约以 [Norm 模块声明](../ui/component/module.norm)、[主题连接](../ui/component/connection.norm)和 [Java 源码](../src/main/java/dev/normlanguage/ui/component)为准。此页只记录跨组件的边界。

```text
应用 / ui.fx / 其他 JavaFX UI 库
                  ↓
          ui.component + JavaFX
                  ↓
                theme
```

`ui.component` 不引用上层 `ui` 的 Widget、渲染器或 DI。Java 工件依赖 JavaFX；Norm 适配层依赖 `fx.controls` 和 `theme`，并复用它们已拥有的公开类型。颜色推导只发生在 `theme`；[connection.norm](../ui/component/connection.norm)把主题快照映射为 JavaFX 样式变量。

样式入口为 [components.css](../src/main/resources/dev/normlanguage/ui/component/components.css)，按输入与数据展示职责引用子样式表。几何配置以 [ComponentConfig](../src/main/java/dev/normlanguage/ui/component/ComponentConfig.java)为唯一来源；展示应用的页面布局由 [gallery.css](../src/main/resources/dev/normlanguage/ui/component/gallery/gallery.css)管理。

日期、时间、数值精度和区域值由 [`jdk.base`](https://github.com/normlanguage/jdk-base) 的 `jdk/base/module.norm` 定义。[本模块的依赖声明](../ui/component/module.norm)引用它；调用方使用这些类型时也需声明 `jdk.base` 依赖。

一个 [App](../src/main/java/dev/normlanguage/ui/component/App.java) 是一个组件树的资源所有者，可放进现有 `Scene`。它不启动 JavaFX，也不关闭传入的 `ThemeSource`。[ConfigProvider](../src/main/java/dev/normlanguage/ui/component/ConfigProvider.java)连接或覆盖局部主题。场景挂载时订阅主题，临时脱离场景时解除订阅，重新挂载时重新连接；最终 `close()` 停止后续连接并释放根节点拥有的资源。主题通知可以来自其他线程，进入 JavaFX 后再更新样式；其它控件变更和关闭操作遵守 JavaFX 应用线程规则。

[ComponentConfig](../src/main/java/dev/normlanguage/ui/component/ComponentConfig.java)集中保存字体、密度、圆角、动效和区域设置。`ConfigProvider` 分别保存主题 CSS 与可继承的组件配置，合并字体声明，并为密度、圆角加载作用于本子树的实际 JavaFX 样式表。弹层继承触发点的配置；动画控件读取当前作用域的动效开关。区域设置是类型化配置值，具体组件的文案与格式以各自实现为准。

弹层使用触发控件所属的根节点和最近的局部主题。[ThemeConnection](../src/main/java/dev/normlanguage/ui/component/ThemeConnection.java)是 Popover、Modal、Drawer 的主题继承入口；各弹层分别管理自己的显示、焦点与关闭。`Message` 与 `Notification` 归所属 `App` 管理，不创建全局窗口服务。异步和动画控件的关闭入口可由组件索引定位到各自实现。

[组件索引](components.md)列出公开范围；[Gallery](../src/main/java/dev/normlanguage/ui/component/gallery/Gallery.java)提供每项可操作示例；[测试目录](../src/test/java/dev/normlanguage/ui/component)验证实际 JavaFX 窗口、主题与交互。

Gallery 页面结构见 [GalleryView](../src/main/java/dev/normlanguage/ui/component/gallery/GalleryView.java)，示例分组见 [GalleryExamples](../src/main/java/dev/normlanguage/ui/component/gallery/GalleryExamples.java)；导航和页内目录由同一组件目录与示例列表派生。

有限动效共用 [Motion](../src/main/java/dev/normlanguage/ui/component/Motion.java) 的节奏和生命周期，读取现有 `ComponentConfig.motionEnabled`。分组示例按领域放在 [gallery](../src/main/java/dev/normlanguage/ui/component/gallery) 中；[GalleryInteractionTest](../src/test/java/dev/normlanguage/ui/component/GalleryInteractionTest.java) 验证并截图真实弹层。
