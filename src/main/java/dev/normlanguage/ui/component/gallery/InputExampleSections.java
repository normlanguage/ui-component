package dev.normlanguage.ui.component.gallery;

import dev.normlanguage.ui.component.*;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public final class InputExampleSections {
    private InputExampleSections() {}

    public static List<GalleryExamples.Section> create(Gallery.Component component, App app) {
        Objects.requireNonNull(component);
        Objects.requireNonNull(app);
        var name = component.name();
        var first = component.factory().get();
        first.setId("gallery-example");
        var second = alternate(name, app);
        return switch (name) {
            case "AutoComplete" -> pair("城市搜索", first, "异步搜索", second);
            case "Cascader" -> pair("地区选择", first, "按需加载", second);
            case "Checkbox" -> pair("通知偏好", first, "三态选择", second);
            case "ColorPicker" -> pair("品牌主色", first, "文字与背景", second);
            case "DatePicker" -> pair("预约日期", first, "出行区间", second);
            case "Form" -> pair("创建账户", first, "活动报名", second);
            case "Input" -> pair("实时预览", first, "密码与备注", second);
            case "InputNumber" -> pair("订单数量", first, "精确金额", second);
            case "Mentions" -> pair("团队协作", first, "客户支持", second);
            case "Radio" -> pair("配送方式", first, "支付方式", second);
            case "Rate" -> pair("商品评价", first, "服务反馈", second);
            case "Select" -> pair("方案选择", first, "搜索与多选", second);
            case "Slider" -> pair("预算调整", first, "价格区间", second);
            case "Switch" -> pair("消息通知", first, "隐私设置", second);
            case "TimePicker" -> pair("预约时段", first, "工作时间", second);
            case "Transfer" -> pair("项目成员", first, "权限分配", second);
            case "TreeSelect" -> pair("商品分类", first, "组织结构", second);
            case "Upload" -> pair("单文件上传", first, "批量与拖放", second);
            default -> throw new IllegalArgumentException("Unknown input component: " + name);
        };
    }

    static Node primary(String name, App app) {
        Objects.requireNonNull(app);
        return switch (name) {
            case "AutoComplete" -> citySearch();
            case "Cascader" -> regionCascade();
            case "Checkbox" -> notificationCheckboxes();
            case "ColorPicker" -> brandColor();
            case "DatePicker" -> appointmentDate();
            case "Form" -> accountForm();
            case "Input" -> greetingInput();
            case "InputNumber" -> orderQuantity();
            case "Mentions" -> teamMentions();
            case "Radio" -> shippingRadio();
            case "Rate" -> productRate();
            case "Select" -> planSelect();
            case "Slider" -> budgetSlider();
            case "Switch" -> notificationSwitch();
            case "TimePicker" -> appointmentTime();
            case "Transfer" -> teamTransfer();
            case "TreeSelect" -> categoryTree();
            case "Upload" -> uploadDemo(app, false);
            default -> throw new IllegalArgumentException("Unknown input component: " + name);
        };
    }

    private static Node alternate(String name, App app) {
        return switch (name) {
            case "AutoComplete" -> asyncSearch();
            case "Cascader" -> lazyCascade();
            case "Checkbox" -> threeStateCheckbox();
            case "ColorPicker" -> textAndBackgroundColor();
            case "DatePicker" -> travelRange();
            case "Form" -> eventForm();
            case "Input" -> credentialsInput();
            case "InputNumber" -> exactAmount();
            case "Mentions" -> supportMentions();
            case "Radio" -> paymentRadio();
            case "Rate" -> serviceRate();
            case "Select" -> searchableSelect();
            case "Slider" -> priceRange();
            case "Switch" -> privacySwitch();
            case "TimePicker" -> workTime();
            case "Transfer" -> permissionTransfer();
            case "TreeSelect" -> organizationTree();
            case "Upload" -> uploadDemo(app, true);
            default -> throw new IllegalArgumentException("Unknown input component: " + name);
        };
    }

    private static List<GalleryExamples.Section> pair(String firstTitle, Node first, String secondTitle, Node second) {
        return List.of(new GalleryExamples.Section("scenario", firstTitle, first, true),
                new GalleryExamples.Section("variations", secondTitle, second, true));
    }

    private static VBox surface(Node... nodes) {
        var box = new VBox(12, nodes);
        box.getStyleClass().add("gallery-demo-surface");
        box.getStylesheets().add(Objects.requireNonNull(InputExampleSections.class.getResource("input-examples.css")).toExternalForm());
        return box;
    }

    private static HBox row(Node... nodes) {
        var box = new HBox(12, nodes);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getStyleClass().add("gallery-demo-row");
        return box;
    }

    private static Label caption(String text) {
        var label = new Label(text);
        label.getStyleClass().add("gallery-demo-caption");
        return label;
    }

    private static Button action(String text, Runnable effect) {
        var button = new Button(text);
        button.setOnAction(event -> effect.run());
        return button;
    }

    private static void preview(Region region, Color color) {
        region.setBackground(new Background(new BackgroundFill(color, new CornerRadii(6), javafx.geometry.Insets.EMPTY)));
    }

    private static Node citySearch() {
        var cities = List.of("新加坡", "上海", "深圳", "首尔", "悉尼", "东京");
        var search = new AutoComplete();
        search.setPromptText("输入城市名称");
        search.setSuggestions(cities);
        search.setPrefWidth(260);
        var result = caption("可搜索 6 座城市");
        search.valueProperty().addListener((observable, old, selected) -> result.setText("目的地：" + selected));
        return surface(row(search, action("选择东京", () -> search.setValue("东京"))), result);
    }

    private static Node regionCascade() {
        var region = new Cascader<String>(List.of(
                new Cascader.Item<>("亚洲", "亚洲", List.of(
                        new Cascader.Item<>("新加坡", "新加坡", List.of()),
                        new Cascader.Item<>("日本", "日本", List.of()))),
                new Cascader.Item<>("欧洲", "欧洲", List.of(
                        new Cascader.Item<>("法国", "法国", List.of()),
                        new Cascader.Item<>("德国", "德国", List.of())))));
        region.selectPath(List.of("亚洲", "新加坡"));
        var result = caption("已选：亚洲 / 新加坡");
        region.valueProperty().addListener((observable, old, path) -> result.setText("已选：" + String.join(" / ", path)));
        return surface(region, result);
    }

    private static Node notificationCheckboxes() {
        var email = new Checkbox("邮件通知");
        var sms = new Checkbox("短信通知");
        var app = new Checkbox("应用内通知");
        email.setSelected(true);
        app.setSelected(true);
        var result = caption("已开启 2 项通知");
        Runnable update = () -> result.setText("已开启 " +
                ((email.isSelected() ? 1 : 0) + (sms.isSelected() ? 1 : 0) + (app.isSelected() ? 1 : 0)) + " 项通知");
        email.selectedProperty().addListener((observable, old, value) -> update.run());
        sms.selectedProperty().addListener((observable, old, value) -> update.run());
        app.selectedProperty().addListener((observable, old, value) -> update.run());
        return surface(row(email, sms, app), result);
    }

    private static Node brandColor() {
        var picker = new ColorPicker();
        picker.setValue(Color.web("#1677ff"));
        var swatch = new Region();
        swatch.setId("demo-brand-swatch");
        swatch.setPrefSize(68, 32);
        swatch.setMinSize(68, 32);
        swatch.getStyleClass().add("gallery-input-swatch");
        preview(swatch, picker.getValue());
        var result = caption("品牌主色 #1677FF");
        picker.valueProperty().addListener((observable, old, color) -> {
            preview(swatch, color);
            result.setText("RGBA " + toHex(color) + " · 透明度 " + Math.round(color.getOpacity() * 100) + "%");
        });
        return surface(row(picker, swatch), result);
    }

    private static Node appointmentDate() {
        var date = new DatePicker();
        date.setValue(LocalDate.now().plusDays(1));
        var result = caption("预约日期：" + date.getValue());
        date.valueProperty().addListener((observable, old, value) -> result.setText("预约日期：" + (value == null ? "未选择" : value)));
        return surface(row(date, action("下周同日", () -> date.setValue(LocalDate.now().plusWeeks(1)))), result);
    }

    private static Node accountForm() {
        var form = new Form();
        form.setSpacing(8);
        var name = new Input();
        name.setPromptText("姓名（必填）");
        name.setPrefWidth(280);
        form.addField("name", name, value -> !value.isBlank());
        var email = new Input();
        email.setPromptText("邮箱（必填）");
        email.setPrefWidth(280);
        form.addField("email", email, value -> value.contains("@"));
        var result = caption("填写信息后创建账户");
        form.setOnSubmit(() -> result.setText("已创建账户：" + name.getText()));
        return surface(form, row(action("创建账户", () -> {
            if (!form.submit()) result.setText("请填写姓名和有效邮箱");
        }), action("重置", () -> { form.reset(); result.setText("已清空表单"); })), result);
    }

    private static Node greetingInput() {
        var name = new Input("林一");
        name.setPromptText("输入姓名");
        name.setPrefWidth(260);
        var result = caption("你好，林一");
        name.textProperty().addListener((observable, old, value) -> result.setText("你好，" + (value.isBlank() ? "访客" : value)));
        return surface(name, result);
    }

    private static Node orderQuantity() {
        var quantity = new InputNumber(new BigDecimal("1"), new BigDecimal("20"), new BigDecimal("2"), BigDecimal.ONE);
        quantity.setId("demo-quantity");
        var result = caption("单价 ¥89 · 合计 ¥178");
        result.setId("demo-total");
        quantity.valueProperty().addListener((observable, old, value) ->
                result.setText("单价 ¥89 · 合计 ¥" + value.multiply(new BigDecimal("89")).toPlainString()));
        return surface(row(caption("购买数量"), quantity), result);
    }

    private static Node teamMentions() {
        var composer = new Mentions();
        composer.setSuggestions(List.of("小林", "阿敏", "产品组"));
        composer.setText("本周发布计划，请 ");
        composer.setPrefRowCount(2);
        composer.setMaxWidth(440);
        var result = caption("输入 @ 可选择团队成员");
        composer.textProperty().addListener((observable, old, value) -> result.setText("消息：" + value));
        return surface(composer, row(action("提及小林", () -> {
            composer.appendText("@小");
            composer.positionCaret(composer.getLength());
            composer.insertMention("小林");
        }), result));
    }

    private static Node shippingRadio() {
        var standard = new Radio("标准配送 · 免费");
        var express = new Radio("次日送达 · ¥18");
        var group = new ToggleGroup();
        standard.setToggleGroup(group);
        express.setToggleGroup(group);
        standard.setSelected(true);
        var result = caption("当前：标准配送");
        group.selectedToggleProperty().addListener((observable, old, choice) ->
                result.setText("当前：" + (choice == express ? "次日送达" : "标准配送")));
        return surface(row(standard, express), result);
    }

    private static Node productRate() {
        var rating = new Rate(5);
        rating.setValue(4);
        var result = caption("商品体验：4 / 5 星");
        rating.valueProperty().addListener((observable, old, value) -> result.setText("商品体验：" + value + " / 5 星"));
        return surface(row(rating, result));
    }

    private static Node planSelect() {
        var plans = new Select<String>();
        plans.getItems().addAll("基础版", "专业版", "团队版");
        plans.setValue("专业版");
        plans.setPrefWidth(180);
        var result = caption("当前方案：专业版");
        plans.valueProperty().addListener((observable, old, value) -> result.setText("当前方案：" + value));
        return surface(row(plans, result));
    }

    private static Node budgetSlider() {
        var budget = new Slider(0, 10000, 3500);
        budget.setPrefWidth(300);
        var result = caption("月度预算 ¥3,500");
        budget.valueProperty().addListener((observable, old, value) ->
                result.setText("月度预算 ¥" + String.format("%,d", Math.round(value.doubleValue()))));
        return surface(row(budget, result));
    }

    private static Node notificationSwitch() {
        var push = new Switch("推送通知");
        push.setSelected(true);
        var result = caption("新消息会实时提醒");
        push.selectedProperty().addListener((observable, old, selected) ->
                result.setText(selected ? "新消息会实时提醒" : "通知已暂停"));
        return surface(row(push, result));
    }

    private static Node appointmentTime() {
        var time = new TimePicker(30);
        time.setAllowedRange(LocalTime.of(9, 0), LocalTime.of(18, 0));
        time.setValue(LocalTime.of(10, 30));
        time.setPrefWidth(150);
        var result = caption("预约开始：10:30");
        time.valueProperty().addListener((observable, old, value) -> result.setText("预约开始：" + value));
        return surface(row(time, result));
    }

    private static Node teamTransfer() {
        var transfer = new Transfer<>(List.of("林一", "阿敏", "陈悦", "周远"));
        transfer.select("林一");
        transfer.getAvailableView().setPrefSize(170, 150);
        transfer.getSelectedView().setPrefSize(170, 150);
        var result = caption("项目成员：林一");
        transfer.getSelectedItems().addListener((javafx.collections.ListChangeListener<String>) change ->
                result.setText("项目成员：" + String.join("、", transfer.getSelectedItems())));
        return surface(transfer, result);
    }

    private static Node categoryTree() {
        var root = new TreeItem<>("商品");
        var electronics = new TreeItem<>("数码产品");
        var phone = new TreeItem<>("手机");
        electronics.getChildren().addAll(phone, new TreeItem<>("耳机"));
        root.getChildren().addAll(electronics, new TreeItem<>("家居"));
        var picker = new TreeSelect<>(root);
        picker.setPromptText("选择商品分类");
        picker.select(phone);
        var result = caption("已选分类：手机");
        picker.valueProperty().addListener((observable, old, value) -> result.setText("已选分类：" + value));
        return surface(row(picker, result));
    }

    private static Node uploadDemo(App app, boolean batch) {
        var upload = new Upload();
        upload.getListView().setPrefHeight(batch ? 112 : 96);
        upload.setUploader((file, progress) -> CompletableFuture.runAsync(() -> {
            try {
                var destination = Path.of(System.getProperty("java.io.tmpdir"), "ui-component-demo-uploads");
                Files.createDirectories(destination);
                Files.copy(file.toPath(), destination.resolve(file.getName()), StandardCopyOption.REPLACE_EXISTING);
                progress.accept(1);
            } catch (IOException failure) { throw new UncheckedIOException(failure); }
        }));
        app.own(upload);
        var result = caption(batch ? "可拖放多个文件，也可取消后重试" : "可选择文件或拖入下方区域");
        var add = action(batch ? "添加两份示例素材" : "上传示例文件", () -> {
            try {
                var first = Files.createTempFile("norm-ui-", batch ? "-团队素材.txt" : "-项目方案.pdf");
                Files.writeString(first, "Norm UI gallery upload sample");
                if (batch) {
                    var second = Files.createTempFile("norm-ui-", "-项目文案.txt");
                    Files.writeString(second, "Norm UI gallery copy sample");
                    upload.addFiles(List.of(first.toFile(), second.toFile()));
                    result.setText("已加入 2 份素材；选择条目可重试或取消");
                } else {
                    var item = upload.addFile(first.toFile());
                    item.statusProperty().addListener((observable, old, status) ->
                            result.setText("项目方案.pdf · " + status));
                }
            } catch (IOException failure) { result.setText("示例文件创建失败：" + failure.getMessage()); }
        });
        return surface(row(add, result), upload);
    }

    private static String toHex(Color color) {
        return String.format("#%02X%02X%02X", Math.round(color.getRed() * 255),
                Math.round(color.getGreen() * 255), Math.round(color.getBlue() * 255));
    }

    private static Node asyncSearch() {
        var products = List.of("设计系统", "设计规范", "数据看板", "订单中心", "用户中心");
        var search = new AutoComplete();
        search.setPromptText("搜索工作区");
        search.setPrefWidth(260);
        search.setProvider(query -> CompletableFuture.completedFuture(products.stream()
                .filter(item -> item.contains(query)).toList()));
        var result = caption("从工作区项目中搜索");
        search.loadingProperty().addListener((observable, old, loading) -> {
            if (loading) result.setText("正在搜索…");
            else result.setText("找到 " + search.getSuggestions().size() + " 个项目");
        });
        search.valueProperty().addListener((observable, old, selected) -> result.setText("打开：" + selected));
        return surface(row(search, result));
    }

    private static Node lazyCascade() {
        var company = new Cascader.Item<String>("星河科技", "星河科技", List.of(), true);
        var cascade = new Cascader<>(List.of(company));
        cascade.setChildrenProvider(item -> CompletableFuture.completedFuture(List.of(
                new Cascader.Item<>("产品团队", "产品团队", List.of()),
                new Cascader.Item<>("工程团队", "工程团队", List.of()),
                new Cascader.Item<>("运营团队", "运营团队", List.of()))));
        cascade.setId("demo-cascader-lazy");
        cascade.selectPath(List.of("星河科技"));
        var result = caption("组织：星河科技 · 展开以加载团队");
        cascade.valueProperty().addListener((observable, old, path) -> result.setText("组织：" + String.join(" / ", path)));
        return surface(cascade, result);
    }

    private static Node threeStateCheckbox() {
        var parent = new Checkbox("全部文件");
        parent.setAllowIndeterminate(true);
        parent.setIndeterminate(true);
        var document = new Checkbox("文档");
        var image = new Checkbox("图片");
        document.setSelected(true);
        var result = caption("当前已选：文档");
        Runnable update = () -> {
            if (document.isSelected() && image.isSelected()) { parent.setIndeterminate(false); parent.setSelected(true); }
            else if (!document.isSelected() && !image.isSelected()) { parent.setIndeterminate(false); parent.setSelected(false); }
            else parent.setIndeterminate(true);
            result.setText("当前已选：" + (document.isSelected() ? "文档 " : "") + (image.isSelected() ? "图片" : ""));
        };
        document.selectedProperty().addListener((observable, old, value) -> update.run());
        image.selectedProperty().addListener((observable, old, value) -> update.run());
        parent.setOnAction(event -> { document.setSelected(parent.isSelected()); image.setSelected(parent.isSelected()); });
        return surface(row(parent, document, image), result);
    }

    private static Node textAndBackgroundColor() {
        var foreground = new ColorPicker();
        foreground.setValue(Color.web("#1677ff"));
        var background = new ColorPicker();
        background.setValue(Color.web("#f0f5ff"));
        var preview = new Label("实时预览 · 色彩搭配");
        preview.getStyleClass().add("gallery-input-color-preview");
        var result = caption("文字 " + toHex(foreground.getValue()) + " · 背景 " + toHex(background.getValue()));
        Runnable update = () -> {
            preview.setTextFill(foreground.getValue());
            preview(preview, background.getValue());
            result.setText("文字 " + toHex(foreground.getValue()) + " · 背景 " + toHex(background.getValue()));
        };
        foreground.valueProperty().addListener((observable, old, color) -> update.run());
        background.valueProperty().addListener((observable, old, color) -> update.run());
        update.run();
        return surface(row(caption("文字"), foreground, caption("背景"), background), preview, result);
    }

    private static Node travelRange() {
        var range = new DatePicker.Range();
        var start = LocalDate.now().plusWeeks(1);
        range.setValue(new DatePicker.DateRange(start, start.plusDays(3)));
        var result = caption("出行：" + start + " → " + start.plusDays(3));
        range.valueProperty().addListener((observable, old, value) ->
                result.setText("出行：" + value.start() + " → " + value.end()));
        return surface(range, result);
    }

    private static Node eventForm() {
        var form = new Form();
        form.setSpacing(8);
        var contact = new Input();
        contact.setPromptText("联系人");
        contact.setPrefWidth(280);
        form.addField("contact", contact, value -> !value.isBlank());
        var consent = new Checkbox("同意接收活动提醒");
        form.addField("consent", consent, consent.selectedProperty(), Boolean.TRUE::equals);
        var result = caption("填写联系人并确认提醒");
        form.setOnSubmit(() -> result.setText(contact.getText() + "，报名成功"));
        return surface(form, row(action("确认报名", () -> {
            if (!form.submit()) result.setText("请完成报名信息");
        }), action("重新填写", () -> { form.reset(); result.setText("已重置报名信息"); })), result);
    }

    private static Node credentialsInput() {
        var password = Input.password();
        password.setPromptText("设置登录密码");
        password.setMaxWidth(300);
        var notes = Input.multiline();
        notes.setPromptText("补充备注（最多 80 字）");
        notes.setPrefRowCount(2);
        notes.setMaxWidth(440);
        var result = caption("密码 0 位 · 备注 0/80 字");
        Runnable update = () -> result.setText("密码 " + password.getText().length() + " 位 · 备注 " + notes.getText().length() + "/80 字");
        password.textProperty().addListener((observable, old, value) -> update.run());
        notes.textProperty().addListener((observable, old, value) -> update.run());
        return surface(password, notes, result);
    }

    private static Node exactAmount() {
        var amount = new InputNumber(new BigDecimal("0"), new BigDecimal("10000"), new BigDecimal("128.50"), new BigDecimal("0.25"));
        amount.setScale(2);
        var result = caption("付款金额 ¥128.50 · 每次增减 ¥0.25");
        amount.valueProperty().addListener((observable, old, value) ->
                result.setText("付款金额 ¥" + value.toPlainString() + " · 每次增减 ¥0.25"));
        return surface(row(caption("精确金额"), amount), result);
    }

    private static Node supportMentions() {
        var message = new Mentions();
        message.setSuggestions(List.of("客服小羽", "技术支持", "售后团队"));
        message.setText("这个问题需要 ");
        message.setPrefRowCount(2);
        message.setMaxWidth(440);
        var result = caption("输入 @ 分配工单");
        message.textProperty().addListener((observable, old, value) -> result.setText("工单留言：" + value));
        return surface(message, row(action("指派技术支持", () -> {
            message.appendText("@技术");
            message.positionCaret(message.getLength());
            message.insertMention("技术支持");
        }), result));
    }

    private static Node paymentRadio() {
        var card = new Radio("银行卡");
        var wallet = new Radio("电子钱包");
        var group = new ToggleGroup();
        card.setToggleGroup(group);
        wallet.setToggleGroup(group);
        wallet.setSelected(true);
        var result = caption("付款方式：电子钱包");
        group.selectedToggleProperty().addListener((observable, old, choice) ->
                result.setText("付款方式：" + (choice == card ? "银行卡" : "电子钱包")));
        return surface(row(card, wallet), result);
    }

    private static Node serviceRate() {
        var rating = new Rate(5);
        rating.setValue(5);
        var result = caption("服务满意度：5 / 5 星");
        rating.valueProperty().addListener((observable, old, value) -> result.setText("服务满意度：" + value + " / 5 星"));
        return surface(row(rating, action("清除评分", () -> rating.setValue(0))), result);
    }

    private static Node searchableSelect() {
        var options = FXCollections.observableArrayList("设计", "研发", "产品", "运营", "市场");
        var searchable = Select.searchable(options);
        searchable.setId("demo-select-searchable");
        searchable.getSearchField().setPromptText("筛选团队");
        searchable.getSearchField().setMaxWidth(220);
        searchable.getSelect().setPrefWidth(220);
        searchable.getSelect().setValue("设计");
        var multiple = Select.multiple(options);
        multiple.setId("demo-select-multiple");
        multiple.select("研发");
        var result = caption("主团队：设计 · 协作团队：研发");
        searchable.getSelect().valueProperty().addListener((observable, old, value) -> result.setText("主团队：" + value));
        multiple.getSelectedItems().addListener((javafx.collections.ListChangeListener<String>) change ->
                result.setText("协作团队：" + String.join("、", multiple.getSelectedItems())));
        return surface(row(searchable, multiple), result);
    }

    private static Node priceRange() {
        var range = new Slider.Range(0, 1000, 200, 700);
        range.getStartSlider().setPrefWidth(180);
        range.getEndSlider().setPrefWidth(180);
        var result = caption("价格区间：¥200 – ¥700");
        Runnable update = () -> result.setText("价格区间：¥" + Math.round(range.getStart()) + " – ¥" + Math.round(range.getEnd()));
        range.getStartSlider().valueProperty().addListener((observable, old, value) -> update.run());
        range.getEndSlider().valueProperty().addListener((observable, old, value) -> update.run());
        return surface(range, result);
    }

    private static Node privacySwitch() {
        var publicProfile = new Switch("公开个人资料");
        var onlineStatus = new Switch("显示在线状态");
        onlineStatus.setSelected(true);
        var result = caption("资料仅自己可见 · 在线状态可见");
        Runnable update = () -> result.setText((publicProfile.isSelected() ? "资料公开" : "资料仅自己可见") +
                " · " + (onlineStatus.isSelected() ? "在线状态可见" : "在线状态隐藏"));
        publicProfile.selectedProperty().addListener((observable, old, value) -> update.run());
        onlineStatus.selectedProperty().addListener((observable, old, value) -> update.run());
        return surface(row(publicProfile, onlineStatus), result);
    }

    private static Node workTime() {
        var start = new TimePicker(15);
        start.setAllowedRange(LocalTime.of(8, 0), LocalTime.of(12, 0));
        start.setValue(LocalTime.of(9, 0));
        var end = new TimePicker(15);
        end.setAllowedRange(LocalTime.of(13, 0), LocalTime.of(20, 0));
        end.setValue(LocalTime.of(18, 0));
        var result = caption("工作时段：09:00 – 18:00");
        Runnable update = () -> result.setText("工作时段：" + start.getValue() + " – " + end.getValue());
        start.valueProperty().addListener((observable, old, value) -> update.run());
        end.valueProperty().addListener((observable, old, value) -> update.run());
        return surface(row(caption("开始"), start, caption("结束"), end), result);
    }

    private static Node permissionTransfer() {
        var transfer = new Transfer<>(List.of("查看报表", "编辑项目", "管理成员", "发布内容"));
        transfer.select("查看报表");
        transfer.getAvailableView().setPrefSize(170, 150);
        transfer.getSelectedView().setPrefSize(170, 150);
        var result = caption("已授予：查看报表");
        transfer.getSelectedItems().addListener((javafx.collections.ListChangeListener<String>) change ->
                result.setText("已授予：" + String.join("、", transfer.getSelectedItems())));
        return surface(transfer, result);
    }

    private static Node organizationTree() {
        var root = new TreeItem<>("公司");
        var design = new TreeItem<>("设计团队");
        design.getChildren().addAll(new TreeItem<>("视觉设计"), new TreeItem<>("用户研究"));
        var engineering = new TreeItem<>("工程团队");
        engineering.getChildren().addAll(new TreeItem<>("前端开发"), new TreeItem<>("后端开发"));
        root.getChildren().addAll(design, engineering);
        var picker = new TreeSelect<>(root);
        picker.setPromptText("选择负责团队");
        picker.select(design);
        var result = caption("负责团队：设计团队");
        picker.valueProperty().addListener((observable, old, value) -> result.setText("负责团队：" + value));
        return surface(row(picker, result));
    }

}
