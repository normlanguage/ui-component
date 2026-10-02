package dev.normlanguage.ui.component.gallery;

import java.util.List;
import java.util.Map;

import static dev.normlanguage.ui.component.gallery.GalleryDocumentation.Example;
import static dev.normlanguage.ui.component.gallery.GalleryDocumentation.Note;
import static dev.normlanguage.ui.component.gallery.GalleryDocumentation.Page;

public final class DisplayDocumentation {
    private DisplayDocumentation() {}

    public static Map<String, Page> pages() {
        return Map.ofEntries(
                Map.entry("Avatar", page(
                        "以文字缩写或图片表示一个人、团队或对象。没有图片时显示构造时给出的回退文字。",
                        "适合成员列表、评论和工作区标识。图片会在头像内居中裁切；需要完整展示图片时使用 Image。",
                        example("basic", "并排展示文字头像与图片头像，照片可随时替换。", "点击“切换照片”，图片头像切换为回退文字；再次点击恢复照片。"),
                        example("states", "对比不同回退文字和图片头像，并实时修改文字。", "在输入框修改“WY”，左侧头像的文字立即更新；图片头像保持原样。"),
                        note("图片与回退", "setImage(null) 会显示文字；设置图片后文字仍保留，供下次回退使用。"),
                        note("节点组合", "Avatar 是 JavaFX StackPane，可直接放入列表单元格、Badge 或其他布局。"))),
                Map.entry("Badge", page(
                        "在任意 JavaFX 节点角落显示未处理数量。计数为零时隐藏数字标记。",
                        "适合收件箱、通知和待办计数。把需要标记的内容节点交给 Badge，业务数据变化时更新 count。",
                        example("basic", "两个头像分别显示收件箱和更新数量。", "点击“收到消息”，收件箱数量增加一条；更新数量不变。"),
                        example("states", "演示有未读与已清零两种状态。", "点击“+1”增加未读数；点击“标记已读”将其归零并隐藏标记。"),
                        note("内容节点", "Badge 包装传入的 Node，计数标记叠放在该节点右上角。"),
                        note("零计数", "setCount(0) 隐藏标记；业务方仍需自行维护实际未读数据。"))),
                Map.entry("Calendar", page(
                        "以整月网格显示日期，并允许选择具体一天。日期单元可嵌入自定义 JavaFX 内容。",
                        "适合排期、预约和每日事件概览。需要只输入一个日期时使用 DatePicker；需要同时看整月内容时使用 Calendar。",
                        example("basic", "在指定日期添加事件点并显示选中的日期。", "点击任意日期，底部选中日期立即更新；左右箭头切换显示月份。"),
                        example("states", "独立展示月份导航、今天定位与日期选择。", "点击“上个月”或“下个月”改变显示月份；点击“今天”回到当天并选中当天。"),
                        note("选中与视图", "value 表示选中的 LocalDate，displayedMonth 表示当前显示的 YearMonth；切换月份不自动改变选中日期。"),
                        note("日期内容", "setDayContentFactory 为每个日期返回 Node 或 null；工厂会在月份、选中日期或配置变化时重新调用。"),
                        note("配置与生命周期", "星期起点和标题使用当前配置的 Locale；不再使用时可调用 close() 释放配置连接。"))),
                Map.entry("Card", page(
                        "用标题和内容节点组织一块独立信息。Card 保留 JavaFX BorderPane 的上下左右区域。",
                        "适合项目摘要、统计分组和任务概览。内容有独立主题和操作时可用卡片；纯文本分隔通常无需卡片。",
                        example("basic", "展示组件库摘要并在底部放置操作按钮。", "点击“标记为已关注”，卡片标题更新为“Norm UI · 已关注”。"),
                        example("states", "并列展示进行中与已完成两张状态卡片。", "对比两个标题及其内容，观察不同状态如何通过内容表达。"),
                        note("内容组成", "构造器接收标题和任意 Node；底部操作可以通过继承的 BorderPane 区域设置。"),
                        note("标题更新", "setTitle 只更新标题文本，不替换卡片内容和操作节点。"))),
                Map.entry("Carousel", page(
                        "在同一位置轮流展示多个 JavaFX 节点。支持手动导航与可选的定时播放。",
                        "适合图片、公告和同级内容的轮播。重要操作不应只放在会自动离开的幻灯片里。",
                        example("basic", "图片和文本节点构成三张幻灯片，并显示当前位置。", "点击“上一张”或“下一张”，幻灯片切换且页码同步更新；到边界后循环。"),
                        example("states", "演示自动播放开关和运行状态。", "点击“开始播放”，状态变为已开启；再次点击关闭。窗口显示且允许动画时才实际计时。"),
                        note("播放条件", "自动播放需要至少两张幻灯片、可见窗口以及启用的 motion 配置；节点离开场景后定时器停止。"),
                        note("生命周期", "不再使用时调用 close()，释放定时器、过渡动画及窗口监听器。"),
                        note("页码范围", "index 从零开始；setIndex 必须指向现有幻灯片。"))),
                Map.entry("Collapse", page(
                        "通过标题展开或收起一块内容。底层是 JavaFX TitledPane，可放入任意内容节点。",
                        "适合常见问题、次要设置和按需阅读的说明。核心操作和必须立即看到的信息应直接展示。",
                        example("basic", "初始展开一个组件结构说明。", "点击标题收起内容，再次点击重新展开。"),
                        example("states", "多个独立面板组成常见问题列表。", "逐项点击标题，各面板独立展开或收起，不会自动关闭其他项。"),
                        note("展开状态", "可使用继承的 expanded 属性控制和观察状态；多个 Collapse 互不排斥。"),
                        note("动效与生命周期", "展开动画跟随当前配置的 motionEnabled；不再使用时调用 close() 释放配置连接。"))),
                Map.entry("Descriptions", page(
                        "将一组名称和值按两列逐行排布，值可以是任意 JavaFX 节点。",
                        "适合订单、项目和用户资料等只读详情。需要编辑字段和校验时使用 Form。",
                        example("basic", "列出项目、平台、组件和主题信息。", "阅读左侧字段名与右侧对应的值；这些内容是静态只读展示。"),
                        example("states", "以订单编号、状态、负责人和日期组成详情。", "对照每一行的标签和值，观察文字节点在同一网格中排列。"),
                        note("行添加", "每次 add(label, value) 在下一行追加标签及内容节点；值不限于 Label。"),
                        note("布局", "Descriptions 继承 JavaFX GridPane，列宽、间距与对齐可按场景继续配置。"))),
                Map.entry("Empty", page(
                        "用简短说明表达当前容器没有可展示内容，并可配合外部操作。",
                        "适合首次使用、无搜索结果和无通知等状态。应说明缺少什么，并给用户提供合适的下一步。",
                        example("basic", "搜索无结果时显示说明和清除筛选按钮。", "点击“清除筛选”，空状态文案更新，提示可以重新搜索。"),
                        example("states", "对比搜索无结果和暂无通知两种语境。", "点击“重置搜索”，第一块说明更新，第二块仍保持原样。"),
                        note("内容边界", "Empty 自身只管理说明文字；按钮等补救操作应由调用方组合到布局中。"),
                        note("动态文案", "setDescription 直接更新当前说明，适合配合筛选或加载结果变化。"))),
                Map.entry("Image", page(
                        "展示 JavaFX 图片，并可在点击时打开原图预览。既可接收图片对象，也可按来源地址加载。",
                        "适合图库、内容预览和详情配图。需要小尺寸人物标识时使用 Avatar；需要完整图片和放大查看时使用 Image。",
                        example("basic", "显示风景图片并启用预览。", "点击图片，在 Modal 中查看按场景大小限制的原图。"),
                        example("states", "同一图片以大、小两种尺寸展示。", "点击任意尺寸的图片，均可预览其原始图片。"),
                        note("图片尺寸", "示例通过 getImageView() 设置 fitWidth 和 fitHeight；ImageView 保持图片宽高比。"),
                        note("加载与错误", "来源地址在节点进入场景时异步加载；离开场景会取消当前加载，重新挂载后再次加载。"),
                        note("预览前提", "预览需要节点已加入 Scene 且图片成功加载；未满足条件时 showPreview 会抛出异常。"))),
                Map.entry("List", page(
                        "以可选择的纵向行展示同类条目。它直接继承 JavaFX ListView，保留原生选择模型。",
                        "适合任务、消息和简单记录。条目很多且高度固定时可使用 Listy。",
                        example("basic", "展示五项任务并回显当前选中项。", "点击不同任务，底部“当前任务”立即更新。"),
                        example("states", "用可变列表展示添加与移除操作。", "输入任务并点击“添加”后出现新行；选择一行并点击“移除选中”将其删除。"),
                        note("数据源", "getItems() 返回 JavaFX ObservableList；增删元素会直接更新列表。"),
                        note("选择", "getSelectionModel() 提供单选、多选等原生 ListView 行为，示例使用单选。"))),
                Map.entry("Listy", page(
                        "针对大量等高条目设置固定单元高度的列表，沿用 ListView 的选择与滚动能力。",
                        "适合日志、事件流等大量同质记录。若每行高度随内容变化，应先评估固定行高是否合适。",
                        example("basic", "加载一万条记录并提供远距离定位按钮。", "点击“定位第 5000 条”，第 5000 行被选中并滚动到可见区域。"),
                        example("states", "展示一万条事件日志的定位和回顶。", "点击“定位中间”跳到第 5000 条；点击“回到顶部”返回第一条，文字同步显示位置。"),
                        note("固定行高", "默认单元高度为 32；setRowHeight 可调整，但必须传入正数。"),
                        note("数据与滚动", "Listy 继承 List，仍使用 ObservableList 和 JavaFX 原生虚拟化单元格。"))),
                Map.entry("QRCode", page(
                        "将文字即时编码为二维码图片。内容或尺寸变化后重新生成像素图。",
                        "适合分享链接、设备配对和票据内容。编码前应确认文本确实是用户希望传递的数据。",
                        example("basic", "为 ui-component 仓库地址生成二维码。", "使用扫码设备可读取示例内预设的仓库地址。"),
                        example("states", "通过输入框改变二维码内容。", "输入非空文本后点击“生成二维码”，图案立即按新文本重绘。"),
                        note("编码来源", "QRCode 使用 ZXing 生成二维码，不对输入文本做网址校验。"),
                        note("尺寸", "构造器与 setSize 的尺寸单位为像素，且必须大于零。"))),
                Map.entry("Segmented", page(
                        "在一组同级选项中快速切换单个当前值。选中状态与 value 属性同步。",
                        "适合日、周、月等互斥视图和简短模式选择。选项较多或文字较长时应使用其他选择控件。",
                        example("basic", "在日、周、月之间切换统计维度。", "点击任一分段，选中高亮随之移动，底部文字显示当前维度。"),
                        example("states", "切换概览、活动和设置的说明。", "点击不同分段，下方内容立即变为对应视图说明。"),
                        note("类型", "Segmented<T> 保留选项值的类型；按钮文字由值的 toString 结果生成。"),
                        note("动态选项", "getItems() 是 ObservableList；更新选项后按钮重建，并尽量保留仍存在的选中值。"))),
                Map.entry("Statistic", page(
                        "突出显示一项数值及其标题，数值变化时立即重排显示。",
                        "适合仪表盘、结果摘要和实时计数。多个指标可并排组合；趋势和上下文应由周围内容说明。",
                        example("basic", "并排展示下载、贡献者和 Star 三项数值。", "比较不同标题对应的值；该示例不自动更新。"),
                        example("states", "演示实时计数的增加、减少和重置。", "点击“+1”“-1”或“重置”，显示值立即变化；示例不会降到零以下。"),
                        note("数字格式", "默认依当前配置的 Locale 格式化数值，也可通过 setFormat 指定 NumberFormat。"),
                        note("生命周期", "不再使用时调用 close()，释放与配置变化相关的连接。"))),
                Map.entry("Table", page(
                        "以列定义展示结构化行数据，并复用 JavaFX TableView 的选择、排序和单元格编辑。",
                        "适合需要比较多个字段、筛选或排序的数据集合。简单单列条目可使用 List。",
                        example("basic", "展示六位成员的姓名、岗位和团队，并提供过滤与姓名编辑。", "输入姓名或团队可筛选；点击表头排序；双击姓名并提交后原始行属性更新。"),
                        example("states", "展示任务及字符长度，演示选择与排序。", "点击列标题改变顺序；点击任意行，底部显示所选任务。"),
                        note("数据流", "setSource 接收 ObservableList，并通过 FilteredList 和 SortedList 连接筛选与表头排序；先设置 source 再设置 predicate。"),
                        note("列数据", "observableColumn 读取 ObservableValue 并随属性更新；column 包装普通值，editableColumn 通过提交回调写回原始对象。"),
                        note("原生能力", "Table 继承 TableView，选择模型、单元格工厂及列宽等由 JavaFX 控件管理。"))),
                Map.entry("Tag", page(
                        "用短标签标记状态、分类或属性。需要移除时可显示关闭按钮并处理关闭事件。",
                        "适合少量简短分类和筛选条件。较长的解释性内容应使用正文而非标签。",
                        example("basic", "展示已就绪、待审核和需处理三种短标签。", "阅读并对比标签文本；这一组标签不可关闭。"),
                        example("states", "展示可关闭标签并允许添加新标签。", "点击标签上的关闭按钮将其移除；点击“添加标签”会追加一个新标签。"),
                        note("关闭事件", "setClosable(true) 显示关闭按钮；点击只发出 CLOSE 事件，移除节点由调用方决定。"),
                        note("文本更新", "setText 可修改标签文字，不影响可关闭状态。"))),
                Map.entry("Timeline", page(
                        "按顺序展示带时间标记的事件内容。每一条内容可以是任意 JavaFX 节点。",
                        "适合构建记录、审批过程和操作历史。事件顺序由添加顺序决定，组件不会自行按时间排序。",
                        example("basic", "展示设计、实现、测试和文档发布四个节点。", "沿时间顺序阅读每一条记录；该示例是静态历史。"),
                        example("states", "以更细的时间点记录一次构建流程。", "按 08:45 至 09:30 的顺序查看构建、测试、截图和发布等待状态。"),
                        note("添加顺序", "add(time, content) 会在末尾追加一行；time 是显示文本，不参与解析或排序。"),
                        note("内容节点", "content 可以是 Label 或其他 Node；布局由 Timeline 的 VBox 容器承载。"))),
                Map.entry("Tree", page(
                        "以可展开层级展示父子关系，并沿用 JavaFX TreeView 的选择模型。可启用复选及异步子节点加载。",
                        "适合文件目录、组织结构和分层导航。数据具有父子关系时使用 Tree；平面条目使用 List。",
                        example("basic", "显示工作区目录，并启用复选框。", "展开或收起 src；勾选节点后可通过 getCheckedValues 获取已选值。"),
                        example("states", "显示指南和组件两个层级，并反馈当前位置。", "点击不同节点，底部“当前位置”立即更新；可折叠或展开分支。"),
                        note("复选节点", "setCheckable(true) 使用 JavaFX CheckBoxTreeCell；getCheckedValues 读取 CheckBoxTreeItem 的选中状态。"),
                        note("懒加载", "setLazyChildren 接收返回 CompletableFuture 的提供者；节点展开时请求，收起或离开场景时取消待完成请求。"),
                        note("错误状态", "异步加载失败可通过 loadErrorProperty 观察；更换提供者会清除先前生成的子节点。")))
        );
    }

    private static Page page(String summary, String whenToUse, Example basic, Example states, Note... notes) {
        return new Page(summary, whenToUse, List.of(basic, states), List.of(notes));
    }

    private static Example example(String sectionId, String explanation, String interaction) {
        return new Example(sectionId, explanation, interaction);
    }

    private static Note note(String title, String body) {
        return new Note(title, body);
    }
}
