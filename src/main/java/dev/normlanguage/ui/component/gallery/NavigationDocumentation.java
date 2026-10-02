package dev.normlanguage.ui.component.gallery;

import java.util.List;
import java.util.Map;

public final class NavigationDocumentation {
    private NavigationDocumentation() {}

    public static Map<String, GalleryDocumentation.Page> pages() {
        return Map.ofEntries(
                Map.entry("Button", page(
                        "按钮发起即时操作，并通过主次、状态和图标表达操作的性质。组件继承 JavaFX Button，可直接使用原生事件与属性。",
                        "用于提交、保存、打开等明确动作。一个操作区域应突出最重要的动作，其余操作保持次要视觉层级；耗时操作期间可切换加载状态，避免重复触发。",
                        List.of(
                                example("types", "同一操作组展示主按钮、默认按钮及低强调度样式；这些外观由样式类决定。", "点击主要按钮及带文字说明的变体可看到操作提示；默认按钮用于对比原生外观。"),
                                example("sizes", "三个独立 ConfigProvider 分别应用宽松、标准和紧凑密度，用于比较不同界面密度。", "切换页面上方密度后，局部示例仍保留各自设定的密度。"),
                                example("states", "正常、加载和禁用状态适用于可执行、进行中及暂不可执行的操作。", "加载按钮不会派发动作；禁用按钮也不能点击。"),
                                example("icons", "图标与文字共同提示搜索、设置、新建和删除等动作。", "点击任一按钮会出现对应提示。"),
                                example("danger", "危险样式区分破坏性操作的不同强调度。", "点击会显示操作提示；真正删除流程仍须由业务层决定是否确认。"),
                                example("usage", "最小 Java 示例使用 Button 构造器和 JavaFX 的 setOnAction 注册动作。", "点击复制代码即可把示例复制到剪贴板。")),
                        List.of(
                                note("事件与状态", "继承 JavaFX Button；使用 setOnAction 处理动作。loadingProperty 为 true 时 fire 不触发动作，任务完成后应恢复状态。"),
                                note("外观与作用域", "默认 Button 使用主样式，outlined、text、danger 是额外样式类；密度通过 ConfigProvider 作用于节点子树。"),
                                note("图标按钮", "仅显示图标时应提供可读文本或 Tooltip，让操作含义可被理解。")))),
                Map.entry("FloatButton", page(
                        "悬浮按钮把重要的辅助操作固定在一个 StackPane 的角落。多个操作可组合成紧凑的悬浮组。",
                        "适合在可滚动工作区中始终可见的反馈、返回或快捷操作。把它附着到明确的容器，避免覆盖表单提交和主要内容。",
                        List.of(
                                example("basic", "任务面板上的两个悬浮动作组成一组，并贴靠面板右下角。", "点击按钮会显示对应提示，面板中的普通内容保持可访问。"),
                                example("variant", "单个反馈入口附着在独立的 StackPane。", "点击反馈按钮会显示提示。")),
                        List.of(
                                note("容器", "attachTo 接受 StackPane，并把按钮添加到容器的子节点；同一节点已有父节点时不能再次附着。"),
                                note("定位", "悬浮位置相对于所附着的容器，不是相对于整个窗口。")))),
                Map.entry("Icon", page(
                        "图标使用 Ikonli 的图标字面量，在 JavaFX 场景图中绘制可缩放符号。它可单独展示，也可作为按钮的 graphic。",
                        "适合为常见操作补充视觉线索。需要清晰的动作名称时，让图标与文字共同出现；不要只凭图形传达关键操作。",
                        List.of(
                                example("basic", "网格展示搜索、设置、添加等常见符号及其名称。", "这是静态图标预览，可观察图标与说明文字的配对。"),
                                example("variant", "把 Icon 放入按钮 graphic，组成可操作的搜索、设置和新建按钮。", "点击按钮会出现对应的操作提示。")),
                        List.of(
                                note("图标来源", "构造器接受 Ikonli 图标字面量；可用图标取决于应用包含的图标包。"),
                                note("可访问名称", "图标仅表示图形；交互含义仍应由按钮文字或 Tooltip 承担。")))),
                Map.entry("Typography", page(
                        "排版组件基于 JavaFX Label，可在同一套主题中呈现标题与正文，并按需提供复制和内联编辑。",
                        "适合文档标题、说明文字和可轻量修改的短文本。需要多行编辑或复杂校验时，应使用专门的输入控件。",
                        List.of(
                                example("basic", "可编辑文本展示短标题与说明，并提供复制与编辑入口。", "启动编辑后可输入新文字；按 Enter 提交，按 Escape 放弃。"),
                                example("variant", "标题、正文和复制动作展示信息层级。", "点击复制正文会把当前文本写入系统剪贴板。")),
                        List.of(
                                note("编辑生命周期", "editableProperty 控制编辑入口；提交时文本更新并触发 onEditCommit，取消时保留旧文本。"),
                                note("上下文菜单", "copyableProperty 和 editableProperty 决定右键菜单中的操作。")))),
                Map.entry("Divider", page(
                        "分割线以水平或垂直方向区分相邻内容，直接使用 JavaFX Separator 的尺寸与布局行为。",
                        "当两个内容组需要视觉边界、却仍属于同一页面时使用。先用间距建立层次，再用分割线强化边界，避免每个元素都画线。",
                        List.of(
                                example("basic", "账户设置和安全内容之间使用水平分割线。", "观察分组关系；此示例无需操作。"),
                                example("variant", "并排信息块之间放置垂直分割线。", "观察左右内容的边界；此示例无需操作。")),
                        List.of(
                                note("方向", "构造时可选择 HORIZONTAL 或 VERTICAL；垂直分割线需要所在容器给出可见高度。"),
                                note("布局", "Divider 是 JavaFX Separator，可参与 HBox、VBox 等原生布局。")))),
                Map.entry("Flex", page(
                        "弹性布局把多个 JavaFX 节点放在同一行或列，并支持对齐、按权重扩展和换行。",
                        "适合工具栏、操作组与宽度会变化的任务区域。内容可能溢出时开启换行；需要固定比例分栏时可设置子节点的扩展权重。",
                        List.of(
                                example("basic", "任务区与操作区共同占用一行，宽度变化时按配置分配空间。", "调整窗口宽度可观察两个区域如何重新排列。"),
                                example("variant", "三个内容项开启换行并可在左、右对齐之间切换。", "点击切换对齐，观察项目位置变化。")),
                        List.of(
                                note("扩展权重", "setGrow 只接受当前 Flex 的子节点；关闭换行时按权重分配剩余主轴空间。"),
                                note("换行", "setWrap(true) 使用 JavaFX FlowPane 的换行布局；节点尺寸仍由自身首选尺寸参与计算。")))),
                Map.entry("Grid", page(
                        "栅格按照容器宽度选择列数，并让项目占据一列或连续多列。等宽列有助于建立稳定的仪表盘结构。",
                        "适合卡片总览、指标面板与响应式模块布局。先确定可用宽度断点，再为重点模块设置跨列宽度。",
                        List.of(
                                example("basic", "六个仪表盘模块依据宽度切换列数，重点模块跨两列。", "调整窗口宽度，观察列数与卡片宽度变化。"),
                                example("variant", "重点卡片跨两列，其余卡片占一列，展示视觉主次。", "缩窄窗口时跨列数会限制在当前可用列数内。")),
                        List.of(
                                note("响应式项目", "setResponsiveColumns 设置最小宽度与列数，addItem 指定项目跨度；宽度依据 Grid 实际布局尺寸计算。"),
                                note("原生约束", "Grid 继承 JavaFX GridPane；使用 add 手动布置的节点仍可按原生行列约束管理。")))),
                Map.entry("Layout", page(
                        "布局组件提供 JavaFX BorderPane 的上、下、左、右和中心区域，便于搭建应用骨架。",
                        "适合窗口级导航、工作区和主内容分区。把核心内容放在中心区域，并让边栏保持可预测的最小宽度。",
                        List.of(
                                example("basic", "示例包含页头、侧栏、主内容和页脚，构成完整的小型工作区。", "点击侧栏开关可以观察内容区域重新分配空间。"),
                                example("variant", "紧凑工作区保留页头与主内容，展示更小的应用骨架。", "观察容器缩放时中心区域如何填充剩余空间。")),
                        List.of(
                                note("区域行为", "Layout 继承 BorderPane；setTop、setLeft、setCenter 等方法使用 JavaFX 原生区域布局。"),
                                note("内容尺寸", "中心区域接收剩余空间；侧栏节点应设置合理的首选宽度以避免挤压内容。")))),
                Map.entry("Masonry", page(
                        "瀑布流把不同高度的卡片依次放到当前最短的列，减少不规则内容之间的空隙。",
                        "适合图库、收藏和高度不一致的内容卡片。列宽应满足卡片可读性；内容变化时组件会重新计算列数与位置。",
                        List.of(
                                example("basic", "十二张不同高度的卡片组成多行瀑布流。", "点击添加卡片可看到新卡片进入当前最短列。"),
                                example("variant", "更窄的最小列宽让同一容器容纳更多列。", "调整窗口宽度，观察列数及卡片位置变化。")),
                        List.of(
                                note("列宽与间距", "minimumColumnWidth 与 gap 决定可容纳的列数；容器宽度不足时退化为一列。"),
                                note("尺寸测量", "Masonry 的首选高度依赖可用宽度；应放在能向其传递实际宽度的布局容器内。")))),
                Map.entry("Space", page(
                        "间距组件把一组节点按统一水平距离排列，适合紧凑的同类操作。",
                        "用于保存、预览、取消等同层级操作的成组排列。它只管理节点之间的距离，较复杂的换行或伸缩布局应选 Flex。",
                        List.of(
                                example("basic", "保存、预览和取消按钮采用同一间距排列。", "点击各按钮会显示对应操作提示。"),
                                example("variant", "滑块实时调整同一组按钮之间的距离。", "拖动滑块，可直接比较不同间距。")),
                        List.of(
                                note("基础布局", "Space 继承 JavaFX HBox；spacing 属性改变相邻子节点的水平间隔。"),
                                note("组合", "可在 Space 中放入任意 JavaFX Node，包括本组件库和原生控件。")))),
                Map.entry("Splitter", page(
                        "分隔面板允许用户拖动分隔条，重新分配相邻内容区域的空间。",
                        "适合文件列表与编辑器、编辑与预览等需要临时调整空间的界面。为两侧内容留出足够的最小可用尺寸。",
                        List.of(
                                example("basic", "文件列表与文档编辑区左右并列。", "拖动中间分隔条，观察两侧宽度变化。"),
                                example("variant", "编辑区与预览区上下排列。", "拖动水平分隔条，观察上下高度变化。")),
                        List.of(
                                note("原生行为", "Splitter 继承 JavaFX SplitPane；方向、分隔位置和子节点均由原生 API 管理。"),
                                note("嵌入布局", "给分隔面板确定的可用高度，内部编辑器才能获得稳定空间。")))),
                Map.entry("Anchor", page(
                        "锚点把目录条目与 ScrollPane 中的目标节点关联，用于快速跳转长内容。滚动位置也会更新当前条目。",
                        "适合有多个章节的说明页、表单或设置页。目录文本应与实际章节一致，目标节点必须位于关联的滚动内容中。",
                        List.of(
                                example("basic", "目录连接快速开始、配置与更多内容等章节。", "点击目录项可滚动到对应章节，并更新当前高亮。"),
                                example("variant", "短内容演示滚动位置与当前锚点的联动。", "点击滚动到设置，再观察当前条目随滚动切换。")),
                        List.of(
                                note("目标关系", "Anchor.Item 持有标题和目标 Node；调用 scrollTo 时，目标必须属于关联 ScrollPane 的内容场景。"),
                                note("生命周期", "Anchor 会监听滚动位置；不再使用时调用 close 停止内部动画。")))),
                Map.entry("Breadcrumb", page(
                        "面包屑按层级显示当前位置，并允许用户回到路径中的上级节点。",
                        "适合项目、文件夹或文档等层层进入的界面。保持路径层级清晰，每一项应对应真实导航动作。",
                        List.of(
                                example("basic", "首页、工作区、项目文档和设计规范组成完整路径。", "点击任一层级会显示该层级的导航提示。"),
                                example("variant", "路径可在项目层级后动态增减详情项。", "点击进入详情切换路径，再点击面包屑可触发相应动作。")),
                        List.of(
                                note("路径数据", "getItems 返回可观察列表；增删 Breadcrumb.Item 会更新显示。"),
                                note("导航动作", "每个 Item 的 action 由调用方提供；组件只呈现路径并运行对应动作。")))),
                Map.entry("Dropdown", page(
                        "下拉菜单把不常用的操作收纳到触发按钮后面，以浮层呈现任意 JavaFX 内容。",
                        "适合文档操作、更多选项等次要动作集合。菜单项宜简短且可辨认，主要操作仍应直接可见。",
                        List.of(
                                example("basic", "更多操作包含编辑、分享和归档，选择结果显示在旁边。", "点击触发按钮打开浮层，再选一项观察反馈文字。"),
                                example("variant", "较短的菜单只提供编辑和分享。", "再次点击触发按钮可关闭菜单；点击菜单项可看到提示。")),
                        List.of(
                                note("内容", "构造器接受任意 Node 作为浮层内容；每个菜单项的动作由该 Node 自行处理。"),
                                note("生命周期", "Dropdown 持有 Popover；组件退出使用时调用 close 关闭浮层。")))),
                Map.entry("Menu", page(
                        "菜单栏按文件、编辑、视图等任务组织命令，沿用 JavaFX MenuBar 和 MenuItem 的键盘及菜单行为。",
                        "适合持续使用的桌面工作区，而不是少量临时操作。菜单标题按任务命名，并把实际动作绑定到每个 MenuItem。",
                        List.of(
                                example("basic", "文件、编辑和视图菜单组织新建、保存、复制等文档命令。", "打开菜单并选择项目，会出现对应提示。"),
                                example("variant", "精简菜单保留文件和编辑两组常用命令。", "展开各菜单并选择动作，观察提示。")),
                        List.of(
                                note("原生菜单", "Menu 继承 JavaFX MenuBar；add 创建并返回 JavaFX Menu，参数是 MenuItem。"),
                                note("动作归属", "MenuItem 的 onAction 才执行命令；菜单栏不管理应用业务状态。")))),
                Map.entry("Pagination", page(
                        "分页组件记录总条数、每页条数和当前页，并提供前一页、后一页操作。",
                        "适合已分组的大量记录。页面内容需要由调用方根据 currentPage 与 pageSize 选择和刷新，分页组件自身不持有数据源。",
                        List.of(
                                example("basic", "二十四条文档以每页四条显示，分页状态驱动可见列表。", "点击前后页按钮，观察文档行与页码同步变化。"),
                                example("variant", "每页条数可在十、二十、三十之间切换。", "更改下拉选项后，观察总页数与当前页更新。")),
                        List.of(
                                note("数据与视图", "通过 currentPageProperty 监听翻页，在外部切片并刷新实际数据视图。"),
                                note("边界", "pageSize 必须大于零；当前页会限制在 1 到总页数之间。")))),
                Map.entry("Steps", page(
                        "步骤条表示有顺序的流程阶段，明确当前步骤及已完成、待完成部分。",
                        "适合填写资料、确认内容、提交结果等线性流程。步骤状态应由实际业务进度驱动，而不是仅凭视觉跳转代替数据校验。",
                        List.of(
                                example("basic", "填写、确认、完成的三阶段流程显示当前步骤文字。", "点击下一步，步骤条和当前步骤说明同时更新。"),
                                example("variant", "前进与返回按钮演示双向浏览步骤。", "点击按钮，当前阶段在有效范围内移动。")),
                        List.of(
                                note("步骤数据", "getSteps 返回可观察列表；添加或删除标题会刷新显示。"),
                                note("索引", "currentStep 使用从零开始的索引；设置为列表之外的值会被拒绝。")))),
                Map.entry("Tabs", page(
                        "标签页在同一区域切换不同内容视图，保留紧凑的页面层级。",
                        "适合项目概览、成员和设置等平级视图。每个标签应有明确标题，并把互不相关的流程分到独立页面。",
                        List.of(
                                example("basic", "概览、成员和设置标签分别显示不同的项目内容。", "点击标签页签，观察内容区域切换。"),
                                example("variant", "文档与活动标签之外，还能按需加入新标签。", "点击添加标签，新增页签和内容立即出现。")),
                        List.of(
                                note("原生选择", "Tabs 继承 JavaFX TabPane；可通过 selectionModel 控制或观察当前标签。"),
                                note("标签内容", "add 接受标题和 JavaFX Node，并返回 Tab 以便进一步配置关闭等原生属性。")))));
    }

    private static GalleryDocumentation.Page page(String summary, String whenToUse,
                                                  List<GalleryDocumentation.Example> examples,
                                                  List<GalleryDocumentation.Note> notes) {
        return new GalleryDocumentation.Page(summary, whenToUse, examples, notes);
    }

    private static GalleryDocumentation.Example example(String sectionId, String explanation, String interaction) {
        return new GalleryDocumentation.Example(sectionId, explanation, interaction);
    }

    private static GalleryDocumentation.Note note(String title, String body) {
        return new GalleryDocumentation.Note(title, body);
    }
}
