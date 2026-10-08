**English** | [中文](#中文)

---

# AvalonBase

## For players

AvalonBase is a **shared support library**. It does not add any gameplay by itself — installing it alone changes nothing you can see.

It is the common base that other mods build their interface and settings on. If a mod you use lists AvalonBase as a **required** or **optional** dependency:

- **required** → install it, otherwise that mod will not start;
- **optional** → install it to unlock the mod's in-game settings screen / editor (the mod still works without it, but you can only edit its config file by hand).

Just drop it into the same `mods` folder as the mod that needs it. Nothing to configure.

## For developers

AvalonBase is a MultiLoader (Fabric + NeoForge) library that provides the boring-but-everywhere parts of a mod. **The public API is append-only: new capabilities are added, existing signatures are never changed.**

| You want | AvalonBase provides |
|---|---|
| A good-looking settings screen | `gui/theme` + `gui/screen` (`AvalonConfigScreen`) |
| Ready-made widgets (button / toggle / radio / scrolling list) | `gui/theme`, `gui/panel` |
| Open / close screen animations, smooth list scrolling | `gui/anim` |
| Config IO as TOML (with hot reload) | `config/AvalonToml` |
| Client ⇄ server messaging | `network/AvalonNetwork` |
| Platform detection / optional dependency probing | `platform/Services` |

### Documentation index

| Document | Content |
|---|---|
| [`docts/Animations.md`](docts/Animations.md) | Animation API: types, easing, `ScreenAnim` / `ScrollAnim`, how to wire a screen |
| [`docts/InterfaceAndThemes.md`](docts/InterfaceAndThemes.md) | Theme system, widgets, config screen base class, dialogs, cursor utility |
| [`docts/ConfigAndNetworking.md`](docts/ConfigAndNetworking.md) | Config IO, networking, platform services, optional dependency (voluntary linker) pattern |

### Minimal usage

```java
public class MyScreen extends AvalonConfigScreen {
    public MyScreen(Screen parent, boolean localEdit) {
        super(Component.translatable("gui.mymod.title"), parent, localEdit);
        setTheme(new ModernTheme());                       // 末影紫
        configureAnimations(true, ScreenAnimType.SCALE_BOUNCE, ScreenAnimType.SCALE_BOUNCE);
        playOpenAnimation();
    }

    @Override public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        mouseX = (int) animation().localX(mouseX, width);   // 动画期间鼠标坐标换算
        mouseY = (int) animation().localY(mouseY, height);
        renderBackdrop(g);
        beginAnimatedRender(g);
        // ... your own drawing ...
        endAnimatedRender(g);
    }

    @Override protected void saveConfig() { /* write your config */ }
}
```

## Links

- Project page: <https://www.curseforge.com/minecraft/mc-mods/avalonbase>
- Feedback (backup): <https://issue.mengcai.online/>

## License

MIT — author: Huziyang520

---
---

<a id="中文"></a>

[English](#avalonbase) | **中文**

---

# AvalonBase

## 面向玩家

AvalonBase 是一个**通用前置库**。它本身不添加任何玩法 —— 只装它一个，你不会有任何可感知的变化。

它是别的模组用来搭界面、存配置、做联机同步的"公共零件库"。如果你在用的某个模组把它列为**必需**或**可选**前置：

- **必需** → 必须一起装，否则那个模组无法启动；
- **可选** → 装了才能用那个模组的游戏内设置界面 / 编辑界面（不装也能跑，但只能手改它的配置文件）。

把它和需要它的模组放进同一个 `mods` 文件夹即可，本身不需要任何配置。

## 面向开发者

AvalonBase 是 MultiLoader（Fabric + NeoForge）前置库，提供"每个模组都要写、但写起来很烦"的那部分能力。**公开 API 只增不改**：只新增能力，绝不修改既有签名。

| 你想要 | AvalonBase 提供 |
|---|---|
| 好看的设置界面 | `gui/theme` + `gui/screen`（`AvalonConfigScreen`） |
| 现成控件（按钮 / 开关 / 单选 / 滚动列表） | `gui/theme`、`gui/panel` |
| 界面开/关动画、列表平滑滑动 | `gui/anim` |
| TOML 配置读写（含热加载） | `config/AvalonToml` |
| 客户端 ⇄ 服务端通信 | `network/AvalonNetwork` |
| 平台判定 / 可选前置探测 | `platform/Services` |

### 文档索引

| 文档 | 内容 |
|---|---|
| [`docts/Animations.md`](docts/Animations.md) | 动画 API：类型、缓动、`ScreenAnim` / `ScrollAnim`、屏幕接线方式 |
| [`docts/InterfaceAndThemes.md`](docts/InterfaceAndThemes.md) | 主题系统、控件、配置屏基类、弹窗、光标工具 |
| [`docts/ConfigAndNetworking.md`](docts/ConfigAndNetworking.md) | 配置读写、网络、平台服务、可选前置（voluntary linker）写法 |

### 最小用法

```java
public class MyScreen extends AvalonConfigScreen {
    public MyScreen(Screen parent, boolean localEdit) {
        super(Component.translatable("gui.mymod.title"), parent, localEdit);
        setTheme(new ModernTheme());                       // 末影紫
        configureAnimations(true, ScreenAnimType.SCALE_BOUNCE, ScreenAnimType.SCALE_BOUNCE);
        playOpenAnimation();
    }

    @Override public void extractRenderState(GuiGraphicsExtractor g, int mouseX, int mouseY, float partialTick) {
        mouseX = (int) animation().localX(mouseX, width);   // 动画期间鼠标坐标换算
        mouseY = (int) animation().localY(mouseY, height);
        renderBackdrop(g);
        beginAnimatedRender(g);
        // ... 你自己的绘制 ...
        endAnimatedRender(g);
    }

    @Override protected void saveConfig() { /* 保存你的配置 */ }
}
```

## 相关链接

- 项目主页：<https://www.curseforge.com/minecraft/mc-mods/avalonbase>
- 备用反馈地址：<https://issue.mengcai.online/>

## 许可证

MIT —— 作者：Huziyang520
