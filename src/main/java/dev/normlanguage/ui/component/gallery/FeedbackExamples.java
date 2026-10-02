package dev.normlanguage.ui.component.gallery;

import dev.normlanguage.ui.component.*;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import java.util.List;

public final class FeedbackExamples {
    private FeedbackExamples() {}
    public static List<Gallery.Component> components(App app) {
        return List.of(
                entry(Gallery.Category.DISPLAY, "Popover", "气泡卡片", app),
                entry(Gallery.Category.DISPLAY, "Tooltip", "文字提示", app),
                entry(Gallery.Category.DISPLAY, "Tour", "漫游式引导", app),
                entry(Gallery.Category.FEEDBACK, "Alert", "警告提示", app),
                entry(Gallery.Category.FEEDBACK, "Drawer", "抽屉", app),
                entry(Gallery.Category.FEEDBACK, "Message", "全局提示", app),
                entry(Gallery.Category.FEEDBACK, "Modal", "对话框", app),
                entry(Gallery.Category.FEEDBACK, "Notification", "通知提醒框", app),
                entry(Gallery.Category.FEEDBACK, "Popconfirm", "气泡确认框", app),
                entry(Gallery.Category.FEEDBACK, "Progress", "进度条", app),
                entry(Gallery.Category.FEEDBACK, "Result", "结果", app),
                entry(Gallery.Category.FEEDBACK, "Skeleton", "骨架屏", app),
                entry(Gallery.Category.FEEDBACK, "Spin", "加载中", app),
                entry(Gallery.Category.FEEDBACK, "Watermark", "水印", app),
                entry(Gallery.Category.OTHER, "Affix", "固钉", app),
                entry(Gallery.Category.OTHER, "App", "包裹组件", app),
                entry(Gallery.Category.OTHER, "BorderBeam", "边框流光", app),
                entry(Gallery.Category.OTHER, "ConfigProvider", "全局化配置", app),
                entry(Gallery.Category.OTHER, "Util", "工具类", app));
    }
    private static Gallery.Component entry(Gallery.Category category, String name, String chinese, App app) {
        return new Gallery.Component(category, name, chinese, () -> example(name, app, false));
    }
    public static List<GalleryExamples.Section> create(Gallery.Component component, App app) {
        var primary = component.factory().get();
        primary.setId("gallery-example");
        String second = switch (component.name()) {
            case "Popover", "Drawer" -> "不同方向";
            case "Tooltip" -> "键盘焦点提示";
            case "Progress", "Spin", "Skeleton" -> "完成状态";
            case "Result" -> "空结果与重试";
            case "BorderBeam", "ConfigProvider" -> "局部配置";
            case "Affix" -> "另一处滚动容器";
            default -> "场景与状态";
        };
        return List.of(new GalleryExamples.Section("basic", "交互示例", primary, true),
                new GalleryExamples.Section("states", second, example(component.name(), app, true), true));
    }
    private static Node example(String name, App app, boolean alternate) {
        return switch (name) {
            case "Popover" -> {
                var details = new Descriptions();
                details.add("负责人", new Label("陈悦 · 产品设计"));
                details.add("状态", new Tag("进行中"));
                details.add("交付", new Label("10 月 18 日"));
                var trigger = new Button(alternate ? "查看项目成员" : "查看项目详情");
                var popover = new Popover(trigger, panel("设计系统 · Sprint 08", details));
                popover.setSide(alternate ? Side.RIGHT : Side.BOTTOM);
                trigger.setOnAction(event -> popover.show());
                yield panel("项目概览", new Label("统一桌面体验，当前已完成组件整理与设计评审。"),
                        row(new Tag("设计系统"), new Tag("本周交付")), trigger);
            }
            case "Tooltip" -> {
                var save = new Button(alternate ? "聚焦此按钮" : "保存草稿");
                save.setGraphic(new Icon("mdi2c-content-save-outline"));
                new Tooltip(save, alternate ? "按 Tab 聚焦也能阅读提示" : "保存当前修改，稍后继续编辑");
                save.setOnAction(event -> app.getMessages().success("草稿已保存"));
                var duplicate = new Button("复制链接");
                new Tooltip(duplicate, "复制当前项目的分享链接");
                duplicate.setOnAction(event -> {
                    var clipboard = new javafx.scene.input.ClipboardContent();
                    clipboard.putString("https://github.com/normlanguage/ui-component");
                    javafx.scene.input.Clipboard.getSystemClipboard().setContent(clipboard);
                    app.getMessages().success("项目链接已复制");
                });
                yield panel(alternate ? "无鼠标操作" : "轻量操作提示", row(save, duplicate),
                        new Label(alternate ? "使用 Tab 在操作之间移动，Enter 执行操作。" : "悬停在操作上查看提示。"));
            }
            case "Tour" -> {
                var title = new Label(alternate ? "团队空间" : "欢迎使用 Norm 工作台");
                var create = new Button("新建项目");
                var invite = new Button("邀请成员");
                var tour = new Tour(List.of(new Tour.Step(create, "创建第一个项目", "将任务集中到一个项目中。"),
                        new Tour.Step(invite, "邀请协作者", "一起管理进度与反馈。")));
                var start = new Button(alternate ? "重新体验引导" : "开始引导");
                start.setOnAction(event -> tour.start());
                create.setOnAction(event -> app.getMessages().success("项目已创建"));
                invite.setOnAction(event -> app.getMessages().success("邀请已准备好"));
                yield panel("两步开始协作", title, row(create, invite), start);
            }
            case "Alert" -> {
                var first = new Alert(alternate ? "需要处理" : "同步完成", new Label(alternate
                        ? "还有 2 个项目需要补充负责人。" : "工作区的所有更改已同步。"));
                var second = new Alert(alternate ? "离线模式" : "版本更新", new Label(alternate
                        ? "重新连接后将继续同步。" : "新的组件示例已经可以使用。"));
                yield new VBox(16, first, second);
            }
            case "Drawer" -> {
                var open = new Button(alternate ? "从左侧打开" : "编辑项目设置");
                var body = panel("项目设置", new Label("项目名称"), new Input("桌面设计系统"),
                        new Label("负责人"), new Input("陈悦"));
                var drawer = new Drawer(open, body, alternate ? Side.LEFT : Side.RIGHT);
                var save = new Button("保存设置");
                var cancel = new Button("取消");
                cancel.getStyleClass().add("outlined");
                save.setOnAction(event -> { drawer.close(); app.getMessages().success("设置已保存"); });
                cancel.setOnAction(event -> drawer.close());
                body.getChildren().add(row(save, cancel));
                open.setOnAction(event -> drawer.show());
                yield panel("桌面设计系统", new Label("12 位成员 · 最近更新于今天 09:40"),
                        row(new Tag("团队项目"), new Tag("私有")), open);
            }
            case "Message" -> {
                var save = new Button(alternate ? "提示网络异常" : "保存更改");
                save.setOnAction(event -> app.getMessages().success(alternate ? "网络暂时不可用，请稍后重试。" : "更改已保存"));
                var next = new Button(alternate ? "再次尝试" : "提交审核");
                next.setOnAction(event -> app.getMessages().success(alternate ? "已重新连接" : "已提交审核"));
                yield panel(alternate ? "操作反馈" : "项目已准备就绪", new Label("桌面组件规范 · 24 项检查通过"), row(save, next));
            }
            case "Modal" -> {
                var open = new Button(alternate ? "查看发布确认" : "编辑成员资料");
                var body = panel(alternate ? "发布新版本" : "成员资料", new Label(alternate ? "确认发布当前工作区中的更改。" : "显示名称"),
                        new Input(alternate ? "v1.0 · 桌面组件" : "陈悦"));
                var modal = new Modal(open, alternate ? "确认发布" : "编辑成员", body);
                var submit = new Button(alternate ? "发布" : "保存");
                var cancel = new Button("取消");
                cancel.getStyleClass().add("outlined");
                submit.setOnAction(event -> { modal.close(); app.getMessages().success("操作已完成"); });
                cancel.setOnAction(event -> modal.close());
                body.getChildren().add(row(cancel, submit));
                open.setOnAction(event -> modal.show());
                yield panel(alternate ? "版本发布" : "团队成员", new Label(alternate ? "包含组件文档、示例和主题设置。" : "陈悦 · 产品设计师 · 最近活跃于今天"), open);
            }
            case "Notification" -> {
                var show = new Button(alternate ? "查看协作邀请" : "模拟构建完成");
                show.setOnAction(event -> app.getNotifications().show(alternate ? "新的协作邀请" : "构建已完成",
                        new Label(alternate ? "林可邀请你加入桌面设计系统。" : "73 个组件已准备好，打开 Gallery 查看。")));
                yield panel(alternate ? "团队消息" : "后台任务", new Label(alternate ? "团队邀请与待办集中显示在右上角。" : "组件库 / 主分支 / 最新构建"),
                        new Progress(alternate ? 1 : 0.76), show);
            }
            case "Popconfirm" -> {
                var status = new Label(alternate ? "草稿仍保留在当前工作区。" : "项目包含 8 个任务与 3 位成员。" );
                var remove = new Button(alternate ? "放弃草稿" : "删除项目");
                remove.getStyleClass().add("danger");
                var confirmation = new Popconfirm(remove, alternate ? "放弃未保存的草稿？" : "确认删除这个项目？",
                        () -> status.setText(alternate ? "草稿已放弃" : "项目已删除"));
                remove.setOnAction(event -> confirmation.show());
                yield panel(alternate ? "未保存的更改" : "项目管理", status, remove);
            }
            case "Progress" -> {
                var progress = new Progress(alternate ? 1 : 0.36);
                progress.setMaxWidth(Double.MAX_VALUE);
                var value = new Label();
                value.textProperty().bind(javafx.beans.binding.Bindings.format("%.0f%%", progress.progressProperty().multiply(100)));
                var increase = new Button("推进 10%");
                var reset = new Button("重新开始");
                increase.setOnAction(event -> progress.setProgress(Math.min(1, progress.getProgress() + 0.1)));
                reset.setOnAction(event -> progress.setProgress(0));
                yield panel(alternate ? "文件上传完成" : "组件构建中", row(new Label("当前进度"), value), progress, row(increase, reset));
            }
            case "Result" -> {
                var retry = new Button(alternate ? "重新检查" : "打开工作区");
                retry.setOnAction(event -> app.getMessages().success(alternate ? "检查已完成" : "工作区已准备好"));
                yield new Result(alternate ? "暂无匹配的项目" : "项目创建成功", alternate ? "调整筛选条件后重新检查。" : "现在可以邀请成员并开始协作。", retry);
            }
            case "Skeleton" -> {
                var skeleton = new Skeleton(4);
                var loaded = panel("桌面设计系统", new Label("以统一主题构建一致的桌面交互体验。"), row(new Tag("设计"), new Tag("JavaFX")));
                var slot = new VBox(alternate ? loaded : skeleton);
                var toggle = new Button("切换加载状态");
                toggle.setOnAction(event -> slot.getChildren().setAll(slot.getChildren().contains(skeleton) ? loaded : skeleton));
                yield panel("项目摘要", slot, toggle);
            }
            case "Spin" -> {
                var spin = new Spin(panel("正在同步的项目", new Label("设计系统 · 组件文档 · 团队资源"), new Progress(0.62)));
                spin.setSpinning(!alternate);
                spin.setMinHeight(160);
                var toggle = new Button("切换加载状态");
                toggle.setOnAction(event -> spin.setSpinning(!spin.isSpinning()));
                yield new VBox(16, spin, toggle);
            }
            case "Watermark" -> {
                var mark = new Watermark(panel("内部项目文档", new Label("桌面设计系统 / 交互规范"),
                        new Input("内容保持可编辑"), new Label("主题、组件和交互由统一基础库提供。")), alternate ? "仅供团队使用" : "Norm · 内部资料");
                mark.setMinHeight(220);
                yield mark;
            }
            case "Affix" -> {
                var scroll = new ScrollPane();
                scroll.setFitToWidth(true);
                var action = new Button("保存文档");
                action.setOnAction(event -> app.getMessages().success("文档已保存"));
                var fixed = new Affix(scroll, row(new Label("文档操作"), action));
                var body = new VBox(24, new Label(alternate ? "团队协作指南" : "组件设计规范"), fixed);
                for (var section : List.of("一致性", "信息层级", "交互反馈", "可访问性", "主题与密度")) {
                    var block = panel(section, new Label("在当前页面中保持清晰、稳定的内容组织。"));
                    block.setMinHeight(100);
                    body.getChildren().add(block);
                }
                scroll.setContent(body); scroll.setPrefHeight(300);
                yield scroll;
            }
            case "App", "Util" -> {
                var input = new Input("桌面设计系统");
                var status = new Label("就绪");
                var action = new Button(name.equals("App") ? "保存并显示提示" : "查找所属应用");
                action.setOnAction(event -> {
                    var owner = Util.app(action);
                    status.setText(owner == app ? "已连接当前应用" : "已连接局部应用");
                    if (alternate) owner.getNotifications().show("工作区已连接", new Label(input.getText()));
                    else owner.getMessages().success("已保存：" + input.getText());
                });
                yield panel(alternate ? "通知与资源归属" : "应用中的操作反馈", input, row(action, status));
            }
            case "BorderBeam" -> {
                var action = new Button("开始体验");
                action.setOnAction(event -> app.getMessages().success("欢迎体验 Norm UI"));
                var beam = new BorderBeam(panel(alternate ? "聚焦重点内容" : "新的组件体验", new Label("统一主题 · 轻量交互 · 原生桌面"), action));
                beam.setMinHeight(180);
                var toggle = new Switch("播放动画");
                toggle.setSelected(!alternate);
                beam.setAnimated(!alternate);
                beam.animatedProperty().bind(toggle.selectedProperty());
                yield new VBox(16, beam, toggle);
            }
            case "ConfigProvider" -> {
                var scope = new ConfigProvider() {
                    @Override public void close() { themeCssProperty().unbind(); super.close(); }
                };
                scope.themeCssProperty().bind(app.themeCssProperty());
                var input = new Input("局部配置中的输入内容");
                var toggle = new Button(alternate ? "切换局部动效" : "切换局部密度");
                var check = new Switch("即时预览");
                check.setSelected(true);
                toggle.setOnAction(event -> {
                    var c = scope.getEffectiveConfig();
                    scope.setConfig(new ComponentConfig(c.fontFamily(), c.fontSize(), alternate ? c.density() :
                            c.density() == ComponentConfig.Density.COMPACT ? ComponentConfig.Density.SPACIOUS : ComponentConfig.Density.COMPACT,
                            c.radius(), alternate ? !c.motionEnabled() : c.motionEnabled(), c.locale()));
                });
                scope.setContent(panel(alternate ? "局部动效设置" : "独立密度范围", input, row(check, toggle)));
                yield scope;
            }
            default -> throw new IllegalArgumentException(name);
        };
    }
    private static VBox panel(String title, Node... nodes) {
        var heading = new Label(title);
        heading.getStyleClass().add("gallery-demo-title");
        var panel = new VBox(16, heading);
        panel.getChildren().addAll(nodes);
        panel.getStyleClass().add("gallery-demo-surface");
        return panel;
    }
    private static FlowPane row(Node... nodes) {
        var row = new FlowPane(12, 12, nodes);
        row.prefWrapLengthProperty().bind(row.widthProperty());
        row.getStyleClass().add("gallery-demo-row");
        return row;
    }
}
