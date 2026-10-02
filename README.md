# ui-component

`ui.component` 是面向 Norm 应用的 JavaFX 组件库。公开组件实现 `ui.Widget`，用普通字段保存局部状态，用 `Binding<T>`连接可编辑值；`ui` 渲染器负责重绘、子树复用与释放。颜色由独立的 [`theme`](https://github.com/normlanguage/theme) 生成。

```norm
import ui.Widget
import ui.Column
import ui.Text
import ui.component.Button

class SaveExample implements Widget {
  private Integer saves = 0

  Widget build() {
    Column {
      Text(text: "已保存 " + saves.toString() + " 次")
      Button(text: "保存", action: () { saves = saves + 1 })
    }
  }
}
```

公开入口见 [ui.component 模块](ui/component/module.norm)及 [组件索引](docs/components.md)。[Norm 示例](samples/gallery)展示实际交互；可直接使用的 JavaFX 控件和绑定位于 [Java 源码](src/main/java/dev/normlanguage/ui/component)及底层模块 [`ui.component.fx`](ui/component/fx/module.norm)。应用代码以 Widget 层为入口，JavaFX 原生扩展通过 [`ui` 原生视图协议](https://github.com/normlanguage/ui)接入同一渲染树。

在 Windows 上运行示例：`.\scripts\gallery.ps1`。验证示例：`.\scripts\gallery.ps1 -Verify`。依赖仓库不在同级目录时可传 `-UiRoot`、`-UiFxRoot`、`-ThemeRoot` 和 `-JdkRoot`；Norm 编译器默认使用同级 `Norm` 仓库的构建产物，也可用 `NORM_EXECUTABLE` 指定。

本地构建需要 JDK 25。Gradle 解析对应平台的 JavaFX 依赖；定向 Java 验证可运行：

```powershell
.\gradlew.bat compileJava --console=plain
.\gradlew.bat test -PtestSource=NavigationLayoutAdapterTest --tests '*NavigationLayoutAdapterTest' --console=plain
```

模块分层、主题和生命周期边界见 [架构](docs/architecture.md)。
