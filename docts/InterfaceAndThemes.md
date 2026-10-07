**English** | [中文](#中文)

---

# Interface and themes

Everything UI-related lives in `com.avalon.base.gui`. Nothing depends on textures: the modern look is drawn with plain fills.

## Theme system

| Type | Purpose |
|---|---|
| `GuiTheme` | Interface every theme implements: `drawPanel`, `drawCard`, `drawButton`, `drawRadio`, `drawToggle`, `drawScrollTrack`, `drawScrollThumb`, `drawIcon`, plus `titleColor()`, `labelColor()`, `textColor()`, `disabledColor()`, `okColor()`, `warnColor()`, `vanillaButtons()`, `animated()` |
| `ModernTheme` | Right-angle modern look; colors come from a `Palette`; buttons are drawn by the theme |
| `VanillaTheme` | Vanilla chest-style look; buttons use the vanilla sprite |
| `Palette` | All color slots of a theme (panel, border, cards, buttons, toggle, scrollbar, text…); build with `Palette.builder()` |
| `ThemePreset` | Built-in palettes: `ENDER_PURPLE` (default), `OCEAN_BLUE`, `EMERALD_GREEN`, `EMBER_RED`, `PORCELAIN` |
| `Themes` | Global facade: `current()`, `setPreset(preset)`, `setCustom(palette)`, `reset()`, `currentPreset()` |
| `ThemeConfig` | Persists the chosen preset to `avalonbase.toml`: `init(configDir)`, `load()`, `save()` |
| `ThemePresetPicker` | Ready-made widget strip that switches presets |

```java
GuiTheme theme = Themes.current();           // the active theme, wherever you need it
Themes.setPreset(ThemePreset.OCEAN_BLUE);    // switch to a built-in palette

Themes.setCustom(Palette.builder()           // or define your own colors
        .accent(0xFF00FF88)
        .panelTop(0xFF101010)
        .build());

ThemeConfig.init(configDir);                 // remember the player's choice
ThemeConfig.load();
```

## Widgets

| Widget | Constructor / API |
|---|---|
| `ThemedButton` | `new ThemedButton(x, y, w, h, label, onPress, theme, GuiTheme.ButtonRole.PRIMARY / NEUTRAL, font)` |
| `ThemedToggle` | `new ThemedToggle(x, relY, label, checked, enabled, animated[, maxLabelW])`; `render(g, font, theme, y, mouseX, mouseY)`, `isClicked(...)`, `setChecked(...)`, `isChecked()` |
| `ThemedRadio` | `new ThemedRadio(x, relY, label, selected, enabled[, maxLabelW])` |
| `PanelHover` | `PanelHover.render(graphics, font, text, mouseX, mouseY, screenW, screenH[, color])` — a hover box drawn on top of everything |
| `TextFit` | `TextFit.fit(font, text, maxWidth)` ellipsises a string; `TextFit.wrap(font, text, width)` wraps it into lines |
| `AutoLayout` / `Format` | Layout and number/text formatting helpers |

`maxLabelW` ellipsises long widget labels; `truncatedTooltip(...)` returns the full label when the pointer hovers a truncated one, ready to feed into `PanelHover`.

## `AvalonConfigScreen`

Base class for editor screens. It provides the panel geometry (`guiLeft`, `guiTop`, `yo(relY)`), the edit-permission check (`canEdit`), the theme, the save/cancel buttons and the animation host.

```java
public class MyScreen extends AvalonConfigScreen {
    public MyScreen(Screen parent, boolean localEdit) {
        super(Component.translatable("gui.mymod.title"), parent, localEdit);
        setTheme(new ModernTheme());
        configureAnimations(true, ScreenAnimType.SCALE_BOUNCE, ScreenAnimType.SCALE_BOUNCE);
        playOpenAnimation();
    }

    @Override protected void init() {
        super.init();
        addRenderableWidget(new ThemedButton(guiLeft + 20, yo(194), 55, 20,
                Component.translatable("gui.mymod.save"), b -> onSavePressed(),
                theme, GuiTheme.ButtonRole.PRIMARY, font));
    }

    @Override public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        renderBackdrop(g);
        beginAnimatedRender(g);
        theme.drawCard(g, guiLeft + 8, yo(14), 284, 76);
        g.text(font, Component.translatable("gui.mymod.label"), guiLeft + 16, yo(18), theme.labelColor(), false);
        renderStatus(g, 212);
        endAnimatedRender(g);
    }

    @Override protected void saveConfig() { /* save your config */ }

    // optional labels the base class uses for its buttons
    @Override protected Component saveButtonLabel()   { return Component.translatable("gui.mymod.save"); }
    @Override protected Component cancelButtonLabel() { return Component.translatable("gui.mymod.cancel"); }
    @Override protected Component viewOnlyText()      { return Component.translatable("gui.mymod.view_only"); }
    @Override protected Component canEditText()       { return Component.translatable("gui.mymod.can_edit"); }
}
```

Other helpers: `computeGuiTop()`, `addSaveCancelButtons()`, `renderBackdrop(g)`, `renderReadonlyOverlay(g)`, `renderStatus(g, bottomY)`, `handleListScroll(...)`, `drawScrollbar(...)`, `playClickSound()`, `updateCursor(g, showHand)`, `msg(key[, args])`.

## Other building blocks

- `gui/panel`: panel toolkit (`PanelScreen`, `PanelTheme`, `PanelPalette`, `PanelButton`, `PanelHover`) with `PanelDemoScreen` as a runnable example.
- `gui/dialog`: modal dialogs (`AccessDeniedDialog`, `LocalConfigNoticeDialog`).
- `gui/GuiCursor`: `GuiCursor.requestHand(g)` requests the pointing-hand cursor for the current frame; `GuiCursor.request(g, hand)` does it conditionally.
- `gui/util/MouseButtons`: `MouseButtons.isLeft(event)` for left-click checks.

---
---

<a id="中文"></a>

[English](#interface-and-themes) | **中文**

---

# 界面与主题

界面相关的一切都在 `com.avalon.base.gui`，**不依赖任何贴图**：现代风格完全由纯色绘制。

## 主题系统

| 类型 | 作用 |
|---|---|
| `GuiTheme` | 所有主题实现的接口：`drawPanel`、`drawCard`、`drawButton`、`drawRadio`、`drawToggle`、`drawScrollTrack`、`drawScrollThumb`、`drawIcon`，以及 `titleColor()`、`labelColor()`、`textColor()`、`disabledColor()`、`okColor()`、`warnColor()`、`vanillaButtons()`、`animated()` |
| `ModernTheme` | 直角现代风格；颜色来自 `Palette`；按钮由主题自绘 |
| `VanillaTheme` | 原版箱子风格；按钮使用原版贴图 |
| `Palette` | 一套主题的全部色槽（面板、边框、卡面、按钮、开关、滚动条、文字……），用 `Palette.builder()` 构造 |
| `ThemePreset` | 内置配色：`ENDER_PURPLE`（默认）、`OCEAN_BLUE`、`EMERALD_GREEN`、`EMBER_RED`、`PORCELAIN` |
| `Themes` | 全局门面：`current()`、`setPreset(preset)`、`setCustom(palette)`、`reset()`、`currentPreset()` |
| `ThemeConfig` | 把所选预设持久化到 `avalonbase.toml`：`init(configDir)`、`load()`、`save()` |
| `ThemePresetPicker` | 现成的"切换配色"控件条 |

```java
GuiTheme theme = Themes.current();           // 在需要的地方取当前主题
Themes.setPreset(ThemePreset.OCEAN_BLUE);    // 切到内置配色

Themes.setCustom(Palette.builder()           // 或自定义颜色
        .accent(0xFF00FF88)
        .panelTop(0xFF101010)
        .build());

ThemeConfig.init(configDir);                 // 记住玩家的选择
ThemeConfig.load();
```

## 控件

| 控件 | 构造 / API |
|---|---|
| `ThemedButton` | `new ThemedButton(x, y, w, h, label, onPress, theme, GuiTheme.ButtonRole.PRIMARY / NEUTRAL, font)` |
| `ThemedToggle` | `new ThemedToggle(x, relY, label, checked, enabled, animated[, maxLabelW])`；`render(g, font, theme, y, mouseX, mouseY)`、`isClicked(...)`、`setChecked(...)`、`isChecked()` |
| `ThemedRadio` | `new ThemedRadio(x, relY, label, selected, enabled[, maxLabelW])` |
| `PanelHover` | `PanelHover.render(graphics, font, text, mouseX, mouseY, screenW, screenH[, color])` —— 盖在最上层的悬浮提示框 |
| `TextFit` | `TextFit.fit(font, text, maxWidth)` 截断加省略号；`TextFit.wrap(font, text, width)` 折行 |
| `AutoLayout` / `Format` | 布局与文本/数字格式化助手 |

`maxLabelW` 会截断过长标签；`truncatedTooltip(...)` 在被截断且悬停时返回完整文本，可直接喂给 `PanelHover`。

## `AvalonConfigScreen`

编辑界面的基类，提供面板几何（`guiLeft`、`guiTop`、`yo(relY)`）、编辑权限判定（`canEdit`）、主题、保存/取消按钮与动画宿主。

```java
public class MyScreen extends AvalonConfigScreen {
    public MyScreen(Screen parent, boolean localEdit) {
        super(Component.translatable("gui.mymod.title"), parent, localEdit);
        setTheme(new ModernTheme());
        configureAnimations(true, ScreenAnimType.SCALE_BOUNCE, ScreenAnimType.SCALE_BOUNCE);
        playOpenAnimation();
    }

    @Override protected void init() {
        super.init();
        addRenderableWidget(new ThemedButton(guiLeft + 20, yo(194), 55, 20,
                Component.translatable("gui.mymod.save"), b -> onSavePressed(),
                theme, GuiTheme.ButtonRole.PRIMARY, font));
    }

    @Override public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        renderBackdrop(g);
        beginAnimatedRender(g);
        theme.drawCard(g, guiLeft + 8, yo(14), 284, 76);
        g.text(font, Component.translatable("gui.mymod.label"), guiLeft + 16, yo(18), theme.labelColor(), false);
        renderStatus(g, 212);
        endAnimatedRender(g);
    }

    @Override protected void saveConfig() { /* 保存你的配置 */ }

    // 可选：基类建按钮用的文案钩子
    @Override protected Component saveButtonLabel()   { return Component.translatable("gui.mymod.save"); }
    @Override protected Component cancelButtonLabel() { return Component.translatable("gui.mymod.cancel"); }
    @Override protected Component viewOnlyText()      { return Component.translatable("gui.mymod.view_only"); }
    @Override protected Component canEditText()       { return Component.translatable("gui.mymod.can_edit"); }
}
```

其它助手：`computeGuiTop()`、`addSaveCancelButtons()`、`renderBackdrop(g)`、`renderReadonlyOverlay(g)`、`renderStatus(g, bottomY)`、`handleListScroll(...)`、`drawScrollbar(...)`、`playClickSound()`、`updateCursor(g, showHand)`、`msg(key[, args])`。

## 其它构件

- `gui/panel`：面板工具箱（`PanelScreen`、`PanelTheme`、`PanelPalette`、`PanelButton`、`PanelHover`），`PanelDemoScreen` 是可运行示例。
- `gui/dialog`：模态弹窗（`AccessDeniedDialog`、`LocalConfigNoticeDialog`）。
- `gui/GuiCursor`：`GuiCursor.requestHand(g)` 请求本帧的手形光标；`GuiCursor.request(g, hand)` 为带条件的形式。
- `gui/util/MouseButtons`：`MouseButtons.isLeft(event)`，用于左键判定。
