# ui-component

`ui-component` 为 Norm 及其他 JavaFX UI 库提供可直接放入场景树的控件。组件使用 JavaFX 25 的节点、属性、集合和事件；颜色由独立的 [`theme`](https://github.com/normlanguage/theme) 模块生成。仓库中的 [Norm 模块](ui/component/module.norm)名为 `ui.component`。

JavaFX 应用可以直接使用 Java 控件：

```java
@Override public void start(Stage stage) {
    var save = new dev.normlanguage.ui.component.Button("保存");
    var root = new dev.normlanguage.ui.component.App(save);
    stage.setScene(new Scene(root, 640, 480));
    stage.setOnHidden(event -> root.close());
    stage.show();
}
```

Norm 应用通过 [主题连接](ui/component/connection.norm)把 `ThemeSource` 交给组件根节点。主题快照由 `theme` 生成；组件根节点负责把变化应用到 JavaFX 场景和弹层。局部主题由 `ConfigProvider` 承载。现成的交互示例在 [Gallery](src/main/java/dev/normlanguage/ui/component/gallery/Gallery.java) 和 [Norm 示例](samples/gallery/application.norm)中。

[集合构造入口](ui/component/collections.norm)接收 Norm 的 `List<T>`，封装与 JavaFX 控件的转换，输入列表按快照传入。JavaFX 控件自身的可观察列表用于后续界面更新。列表展示控件在 Norm 中名为 `ListView`。

使用日期、时间、数值精度或区域类型的 Norm 应用需要依赖 [`jdk.base`](https://github.com/normlanguage/jdk-base)。这些类型的声明以该仓库的 `jdk/base/module.norm` 为准；本库的 [模块声明](ui/component/module.norm)引用它，不另定义同名类型。

字体、密度、圆角和动效使用独立的 [ComponentConfig](src/main/java/dev/normlanguage/ui/component/ComponentConfig.java)。将配置设在 `App` 或局部 `ConfigProvider` 上，子树与从中打开的弹层会继承最近的配置；主题变化和配置变化各自更新，不会覆盖对方。

## 本地构建

需要 JDK 25。Gradle 会为当前系统解析 JavaFX 25.0.2、Ikonli 与 ZXing。

```powershell
.\gradlew.bat compileJava --console=plain
.\gradlew.bat test -PtestSource=LayoutNavigationTest --tests '*LayoutNavigationTest' --console=plain
```

其他定向测试入口见 [验证工作流](.github/workflows/verify.yml)。[Gallery](src/main/java/dev/normlanguage/ui/component/gallery/Gallery.java) 提供可折叠分类、搜索和全部组件的可操作详情；详情页按示例分组，支持页内导航与窄窗口单列布局。顶部可切换主题色、明暗模式和密度，侧栏可开关顺滑动效。配色在 [Norm 示例](samples/gallery/application.norm)中交给 `theme` 生成。

```powershell
.\scripts\gallery.ps1
.\scripts\gallery.ps1 -Verify
```

[gallery.ps1](scripts/gallery.ps1) 的 `-Verify` 验证已打包的 Norm API，并导出真实主题供渲染测试使用；全部组件的浅色、深色截图保存在 `build/previews/audit-after`，弹层与动画帧另列在该目录中。依赖这些主题产物的定向验证由 `themeRenderingTest` 任务执行，不在普通 `test` 中运行。

Norm 适配需要支持 `jdkModule` 的 Norm 构建、本地已构建的 `theme` 仓库和相邻的 `jdk-base` 仓库。[prepare.ps1](scripts/prepare.ps1) 构建本地 Java 工件并解析模块；`ThemeRoot`、`JdkRoot` 参数可指定依赖目录，`NORM_EXECUTABLE` 环境变量可指定 Norm 命令路径。Gradle 的 `publish` 目标仅写入本仓库的 `build/repository`，不代表远程包已发布。

仓库边界和生命周期见 [架构](docs/architecture.md)；全部 73 项入口见 [组件索引](docs/components.md)。
