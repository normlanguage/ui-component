package dev.normlanguage.ui.component.gallery;

import dev.normlanguage.ui.component.gallery.GalleryDocumentation.Example;
import dev.normlanguage.ui.component.gallery.GalleryDocumentation.Note;
import dev.normlanguage.ui.component.gallery.GalleryDocumentation.Page;

import java.util.List;
import java.util.Map;

public final class InputDocumentation {
    private InputDocumentation() {}

    public static Map<String, Page> pages() {
        return Map.ofEntries(
                Map.entry("AutoComplete", page(
                        "输入文字时展示匹配建议，并将选中的建议作为当前值。既可使用本地候选项，也可接入异步查询。",
                        "适合城市、项目等候选项较多且用户知道关键词的场景。若只需从少量固定选项中选择，直接使用 Select 更清楚。",
                        example("scenario", "预置六座城市，输入内容会过滤本地建议；“选择东京”演示从程序设置当前值。", "输入城市名并选中建议，或点击“选择东京”，下方目的地随当前值更新。"),
                        example("variations", "工作区列表由异步提供者按查询文字返回，控件在加载时公开 loading 状态。", "输入“设计”或“中心”查看匹配项目，选择一项后显示将打开的工作区。"),
                        note("候选项来源", "本地候选项由建议列表提供；远程查询使用返回 CompletableFuture 的 provider，调用方负责查询数据。"),
                        note("异步结果", "新查询会取代旧查询；控件离开场景时取消待处理请求并关闭建议菜单，重新挂载后可继续搜索。"))),
                Map.entry("Cascader", page(
                        "通过连续的下拉层级选择一条路径，当前值是一组按层级排列的节点值。子节点可以预先提供，也可以在展开时异步获取。",
                        "适合地区、组织等天然有父子关系的选项。若只有单层选项，使用 Select 可减少一次层级操作。",
                        example("scenario", "亚洲与欧洲下预置国家，初始路径为“亚洲 / 新加坡”。", "更换大洲再选国家，已选路径会按层级显示。"),
                        example("variations", "“星河科技”是按需加载节点，选择公司后才通过异步提供者取得团队。", "展开公司并选择产品、工程或运营团队，组织路径随之更新。"),
                        note("路径数据", "每一级选项使用 Cascader.Item 表示；读取当前路径时使用只读 valueProperty，而不是分别收集每个下拉框。"),
                        note("延迟加载", "只对标记为 lazy 的节点调用子项 provider；替换选项或离开场景时，过期加载结果不会继续更新界面。"))),
                Map.entry("Checkbox", page(
                        "独立开关式选择项，多个选项可以同时选中。三态模式还可表达一组子项的部分选中状态。",
                        "适合并列偏好、权限等可同时成立的选择。互斥选项应使用 Radio，立即生效的单一设置可使用 Switch。",
                        example("scenario", "邮件、短信和应用内通知分别保存选中状态。", "勾选或取消任意通知渠道，下面即时列出当前启用的渠道。"),
                        example("variations", "“全部文件”对应文档和图片两个子项，部分选中时显示不确定状态。", "分别切换文档与图片，观察父项状态；点击父项可同时切换两个子项。"),
                        note("状态来源", "每个 Checkbox 的 selectedProperty 是该选项的状态来源；汇总文案由这些状态派生。"),
                        note("三态用途", "indeterminate 表示部分选择，不能把它等同于已选或未选；提交数据时应明确处理子项。"))),
                Map.entry("ColorPicker", page(
                        "将 JavaFX 颜色选择器与透明度滑块组合，统一给出包含 alpha 的 Color 值。",
                        "适合可视化配置品牌色、文字色和背景色。颜色会直接影响可读性，应用时应在实际内容上预览。",
                        example("scenario", "初始品牌主色为蓝色，色块显示当前 RGBA 结果。", "更换颜色或拖动透明度滑块，色块和颜色说明同步变化。"),
                        example("variations", "分别选择文字和背景色，预览区域直接使用两个颜色值渲染。", "调整任一颜色或透明度，观察文字与背景的组合效果。"),
                        note("颜色值", "getValue 和 valueProperty 返回 javafx.scene.paint.Color，包含不透明度；不要只读取内部原生颜色选择器。"),
                        note("实时预览", "示例把当前值应用到 JavaFX Region 背景和文字填充；实际业务仍应检查不同主题下的对比度。"))),
                Map.entry("DatePicker", page(
                        "使用原生 JavaFX 日期选择行为选择单日，或用 Range 将开始与结束日期作为一个区间值。",
                        "适合预约日期、旅行计划等日历日期，不适合表达具体时刻；时刻另用 TimePicker。",
                        example("scenario", "预约日期初始为明天，“下周同日”从当前日期重新设定。", "在日历中选日期或点击快捷按钮，预约日期说明随之更新。"),
                        example("variations", "出行区间同时展示开始和结束日期，区间值限制开始不晚于结束。", "改变出发或返程日期，下面显示新的旅行区间。"),
                        note("值的类型", "单日使用 LocalDate；区间使用 DatePicker.DateRange，适合以一个值保存起止日期。"),
                        note("区间约束", "区间日历会限制不合顺序的日期；设置整个区间时也必须满足开始日期不晚于结束日期。"))),
                Map.entry("Form", page(
                        "把输入控件、验证条件、提交动作与重置行为组织在一起。提交前验证所有已登记字段。",
                        "适合需要收集多项信息并统一确认的流程。单独一个输入项通常无需引入 Form。",
                        example("scenario", "姓名不能为空，邮箱需含 @；提交成功后显示新建账户的姓名。", "留空或输入无效邮箱后点击“创建账户”观察验证反馈；填写有效内容后再次提交，或用“重置”恢复初始值。"),
                        example("variations", "报名表把联系人文本与是否同意提醒一同纳入验证。", "填写联系人并勾选提醒后点击“确认报名”；“重新填写”清除本次输入。"),
                        note("字段边界", "addField 接收控件及其 JavaFX Property；验证器处理该属性的真实值，字段名在同一表单内必须唯一。"),
                        note("提交与重置", "submit 只有在所有字段有效时才执行提交回调；reset 恢复添加字段时记录的初始值并清除无效状态。"))),
                Map.entry("Input", page(
                        "基于 JavaFX 文本输入控件提供单行输入，另有密码和多行文本工厂。文本变化可直接用于界面反馈。",
                        "适合姓名、搜索词、密码和短备注等自由文本。结构化数字、日期或固定选项应使用对应的专用控件。",
                        example("scenario", "预置姓名“林一”，问候语监听文本属性实时更新。", "修改或清空姓名，下面的问候语立即变为当前姓名或访客。"),
                        example("variations", "密码输入使用遮蔽控件，备注使用多行文本；示例只显示字符数。", "输入密码和备注，查看两个计数分别变化。"),
                        note("原生输入", "密码与多行输入保留 JavaFX 的 PasswordField 和 TextArea 行为，适合键盘、输入法及选择文本。"),
                        note("敏感内容", "密码示例只展示长度，不把密码原文拼到预览或日志中；提交时由调用方处理实际文本。"))),
                Map.entry("InputNumber", page(
                        "使用 BigDecimal 表达有边界、有步长的数字，支持精度设置与编辑器输入。",
                        "适合金额、数量等需要明确范围和增减步长的值。金额尤其应使用十进制值，避免二进制浮点误差。",
                        example("scenario", "订单数量限制在 1 到 20，初始值为 2；单价固定为 ¥89。", "用箭头调整或输入数量，合计金额按当前 BigDecimal 值重新计算。"),
                        example("variations", "付款金额以两位小数显示，每次增减 ¥0.25，范围为 0 到 10000。", "调整金额或手动输入有效小数，下面显示提交前的精确金额。"),
                        note("编辑过程", "编辑器允许临时输入尚未完成的数字；按回车或失去焦点时提交，draftValid 可表示草稿是否有效。"),
                        note("范围与精度", "初始值和后续值必须位于边界内；setScale 统一控制小数位，业务计算读取 BigDecimal 值。"))),
                Map.entry("Mentions", page(
                        "在多行消息里输入 @ 提及预置候选人或团队，选择后把完整提及插入光标位置。",
                        "适合协作消息和工单分派等需要在自然语言中指向具体对象的场景。纯粹选择人员时，Select 更直接。",
                        example("scenario", "团队消息预置小林、阿敏和产品组；快捷按钮演示插入“小林”。", "在空格后输入 @ 过滤候选项并选择，或点击“提及小林”，消息预览更新。"),
                        example("variations", "工单留言预置客服、技术支持与售后团队。", "输入 @技术 选中候选项，或点击“指派技术支持”，查看工单留言中的提及。"),
                        note("光标位置", "提及只在当前光标前存在有效 @ 片段时插入；普通文本或未形成提及片段不会被替换。"),
                        note("弹层生命周期", "候选弹层属于该文本控件；控件离开场景后会关闭，候选数据由 setSuggestions 提供。"))),
                Map.entry("Radio", page(
                        "一组互斥选项中的单选控件，依靠 JavaFX ToggleGroup 保持组内只选一个。",
                        "适合配送、支付等选项较少、需要同时看见全部候选值的场景。选项很多时可改用 Select。",
                        example("scenario", "标准配送与次日送达共享一个选择组，初始选中标准配送。", "切换配送方式，当前方案说明同步改变。"),
                        example("variations", "银行卡和电子钱包共享另一组，初始选中电子钱包。", "选择付款方式，当前付款方式随之更新。"),
                        note("互斥关系", "将同一问题的 Radio 放入同一个 ToggleGroup；不同业务问题应分别建立组。"),
                        note("状态读取", "选择组的 selectedToggleProperty 表示当前选项，示例由它派生结果文案。"))),
                Map.entry("Rate", page(
                        "用可选中的星级表达整数评分，默认五级，也可设置其他最大等级。",
                        "适合满意度、商品体验等离散主观评价。连续数值范围或精确金额应使用 Slider 或 InputNumber。",
                        example("scenario", "商品评价初始为 4 星，选中不同星级后更新体验分数。", "点击任意星级，下面显示新的 0 到 5 分值。"),
                        example("variations", "服务满意度初始为满分，并提供清除评分操作。", "重新评分或点击“清除评分”，观察结果归零。"),
                        note("数值边界", "valueProperty 是整数属性，写入值必须位于 0 与最大星级之间；0 表示尚未评分。"),
                        note("属性绑定", "评分值可以参与 JavaFX 属性绑定；绑定后由来源属性控制，界面点击不能直接改写绑定值。"))),
                Map.entry("Select", page(
                        "从候选项中选择一个值，也提供带搜索过滤的单选组合和多选弹层。",
                        "适合方案、团队等选项明确且数量中等的场景。用户需要自由输入和异步建议时可使用 AutoComplete。",
                        example("scenario", "基础版、专业版和团队版是固定候选项，初始选择专业版。", "展开下拉框切换方案，当前方案文案随值变化。"),
                        example("variations", "主团队使用搜索过滤后的单选，多选弹层用于协作团队；两者共享同一候选列表。", "输入团队名称缩小单选列表，或打开多选弹层勾选协作团队，查看结果文案。"),
                        note("数据来源", "Select 直接使用 JavaFX ObservableList；搜索组合对候选列表建立过滤视图，多选结果由 selectedItems 提供。"),
                        note("弹层归属", "多选使用跟随控件的 Popover，控件卸载时关闭；若候选列表变更，已移除的选择项会同步剔除。"))),
                Map.entry("Slider", page(
                        "通过拖动滑块调整连续数值；Range 将起点与终点组合成一个受约束区间。",
                        "适合预算和价格区间等更关注大致位置的数值。需要精确键入时使用 InputNumber。",
                        example("scenario", "月度预算在 0 到 10000 之间调整，初始为 ¥3500。", "拖动滑块，金额按当前数值取整显示。"),
                        example("variations", "两个滑块控制 0 到 1000 的价格下限与上限，初始为 ¥200–¥700。", "分别拖动两端，价格区间随之变化且下限不会超过上限。"),
                        note("单值类型", "单滑块沿用 JavaFX Slider 的 double valueProperty，显示金额时示例明确取整。"),
                        note("区间关系", "Range 维护起点不大于终点的约束；业务保存时同时读取 getStart 和 getEnd。"))),
                Map.entry("Switch", page(
                        "表达一个可立即切换的开/关状态，状态由 JavaFX selectedProperty 提供。",
                        "适合通知、在线状态等立即生效的设置。若用户需要先勾选多个条件再统一提交，使用 Checkbox。",
                        example("scenario", "推送通知初始开启，提示文字说明消息提醒是否生效。", "点击开关，查看通知被启用或暂停的反馈。"),
                        example("variations", "公开个人资料与显示在线状态是两个独立设置。", "分别切换两个开关，隐私说明显示对应组合。"),
                        note("状态反馈", "状态文案监听 selectedProperty；界面与业务状态应从同一个属性派生。"),
                        note("操作语义", "开关适合无需额外确认即可生效的二值配置，不应代替提交表单中的必选确认项。"))),
                Map.entry("TimePicker", page(
                        "按可配置的分钟步长列出可选时间，并可限定一天内允许选择的时段。",
                        "适合预约开始时间、工作时段等只关心时分的输入。涉及日期时应与 DatePicker 分开建模。",
                        example("scenario", "预约时间以 30 分钟为步长，只提供 09:00 到 18:00 的时段，初始为 10:30。", "展开时段列表或编辑时间，预约开始说明随选择变化。"),
                        example("variations", "工作开始与结束各用一个时间选择器，均以 15 分钟为步长，并分别限制上午与下午范围。", "调整开始或结束时间，工作时段文本即时更新。"),
                        note("值的类型", "当前值是 java.time.LocalTime；下拉选项按分钟步长生成并受允许范围过滤。"),
                        note("多个时间字段", "两个 TimePicker 彼此独立；需要跨字段的先后校验时，应在表单或业务层设置规则。"))),
                Map.entry("Transfer", page(
                        "用左右两个可多选列表在可用项与已选项之间移动数据。",
                        "适合从较长候选列表中分配项目成员或权限。候选项很少时，Checkbox 更紧凑。",
                        example("scenario", "四位成员中“林一”已在项目内，其余人员留在可用列表。", "在左侧选一人或多人并点右箭头加入项目；在右侧选中后点左箭头移除，成员说明更新。"),
                        example("variations", "“查看报表”已授予，其余权限可继续分配。", "左右移动权限，查看已授予权限的实时列表。"),
                        note("列表状态", "availableItems 和 selectedItems 是只读观察视图；应通过 select、remove 或界面箭头改变分配。"),
                        note("批量操作", "两侧 ListView 支持多选，箭头会一次移动当前选中的全部条目。"))),
                Map.entry("TreeSelect", page(
                        "在树形弹层中选择一个节点，并在触发按钮上显示该节点值。",
                        "适合商品分类和组织结构等需要理解上下级关系的单项选择。若必须保留完整路径值，使用 Cascader。",
                        example("scenario", "商品树包含数码产品下的手机与耳机，初始选中手机。", "打开树形菜单并选其他分类，按钮和已选分类说明一起变化。"),
                        example("variations", "组织树列出设计与工程团队及其岗位，初始选择设计团队。", "展开团队并选择其他节点，负责团队说明显示所选节点。"),
                        note("树节点", "控件接收 JavaFX TreeItem 根节点；程序选择时应传入这棵树中的节点，选中值是节点本身的值。"),
                        note("弹层生命周期", "触发按钮负责开合树形 Popover；选择节点后弹层关闭，控件从场景卸载时也会关闭。"))),
                Map.entry("Upload", page(
                        "选择或拖入本地文件，交给调用方提供的异步上传器处理，并展示每个文件的状态与进度。",
                        "适合用户提交附件、素材或多份文件。上传目标和传输方式属于应用逻辑，由 uploader 决定。",
                        example("scenario", "示例按钮生成一份本地测试文件，上传器把文件复制到系统临时目录。", "点击“上传示例文件”后查看列表中的进度和完成状态；也可选择自己的文件或拖入列表区域。"),
                        example("variations", "批量示例一次生成两份素材，并使用与单文件相同的上传器。", "点击“添加两份示例素材”或拖入多个文件；选中条目后可重试未进行的任务；本地复制通常很快完成，取消只对尚未结束的任务有效。"),
                        note("上传职责", "setUploader 接收文件和进度回调并返回 CompletableFuture；控件负责展示生命周期，不替应用决定服务端协议。"),
                        note("资源释放", "Upload 实现 AutoCloseable；创建它的 App 应持有并在关闭时释放。控件卸载时会取消进行中的任务。"))));
    }

    private static Page page(String summary, String whenToUse, Example scenario, Example variations, Note... notes) {
        return new Page(summary, whenToUse, List.of(scenario, variations), List.of(notes));
    }

    private static Example example(String sectionId, String explanation, String interaction) {
        return new Example(sectionId, explanation, interaction);
    }

    private static Note note(String title, String body) {
        return new Note(title, body);
    }
}
