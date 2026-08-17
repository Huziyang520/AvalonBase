# AvalonBase

> 一个给 Minecraft 模组作者用的"公共零件库"，帮你快速做出漂亮的模组。

## 这是什么？

简单说，AvalonBase 是一个 **Minecraft 1.20.1 的通用前置库**。

想象一下：你写了很多模组，发现每个模组都要重复写一堆"跟游戏玩法无关、但是每个模组都用得上"的代码——比如：

- 一个好看的配置界面
- 存配置、读配置
- 客户端和服务端之间发消息
- 判断现在跑的是 Forge 还是 Fabric

这些事每个模组都要做，但做起来又很麻烦。**AvalonBase 把这些通用的东西都做好了**，你只需要把它当"前置依赖"挂上，就能直接拿来用，专心写你自己的玩法逻辑。

## 一句话总结

> 你负责想"我的模组要干什么"，AvalonBase 负责帮你把"界面、配置、通信"这些杂活都办好。

---

## 它能帮你做什么？

| 你想要的 | AvalonBase 提供的能力 |
|---|---|
| 一个好看的模组设置界面 | `gui/theme` + `gui/screen` |
| 界面里的按钮、开关、单选、滚动列表 | `gui/theme`（现成控件） |
| 鼠标悬停时显示手形光标 | `gui/GuiCursor`（通用光标工具） |
| 界面换成不同颜色风格 | 主题预设（多配色，见下文） |
| 把配置存到文件里，下次启动还在 | `config/AvalonToml` |
| 客户端和服务端之间传数据 | `network/AvalonNetwork` |
| 判断运行平台 / 某个模组装没装 | `platform/Services` |

**适合做这些模组**：权限/命令管理、带设置界面的工具模组、需要客户端服务端同步的模组、想同时支持 Forge 和 Fabric 的模组。

---

## 最推荐的功能：多配色主题

这是 AvalonBase 里最好用的一个部分。你只需要一行代码，就能得到一个现代风格的模组设置界面，而且**可以随意换颜色**。

### 内置了哪些颜色？

一共 **5 套**配色，开箱即用：

| 预设名 | 风格 | 主色调 |
|---|---|---|
| `ENDER_PURPLE` | 末影紫（**默认**，重点推荐） | 紫黑渐变 + 亮紫强调 |
| `OCEAN_BLUE` | 海洋蓝 | 蓝黑渐变 + 天蓝强调 |
| `EMERALD_GREEN` | 翡翠绿 | 绿黑渐变 + 翠绿强调 |
| `EMBER_RED` | 熔岩红 | 红黑渐变 + 橙红强调 |
| `PORCELAIN` | 米白（浅色） | 浅色背景 + 深色文字 |

> **末影紫是默认风格**，也是大多数场景下最好看、最推荐的一档。如果你不确定用哪个，直接用默认的末影紫就好。

### 怎么换颜色？（给开发者）

```java
import com.avalon.base.gui.Themes;
import com.avalon.base.gui.theme.ThemePreset;

// 切换到海洋蓝
Themes.setPreset(ThemePreset.OCEAN_BLUE);

// 或者取当前主题，直接用在你的界面上
GuiTheme theme = Themes.current();
```

### 想自定义颜色？完全可以

除了内置的 5 套，你还可以用 `Palette`（配色盘）自己定义任意颜色：

```java
import com.avalon.base.gui.theme.Palette;

Palette myPalette = Palette.builder()
        .accent(0xFF00FF88)   // 强调色改成青绿
        .panelTop(0xFF101010) // 面板顶部颜色
        .build();

Themes.setCustom(myPalette);
```

配色盘里共有约 24 个"色槽"（面板、边框、按钮、开关、滚动条、文字等），你想改哪个改哪个，没改的用默认末影紫。

### 想记住用户选的颜色？

有 `ThemeConfig` 帮你把选择存到配置文件，下次启动自动恢复。你只需要在平台入口初始化一次：

```java
ThemeConfig.init(configDir);  // 传入配置目录
ThemeConfig.load();           // 读回上次的预设
```

切换颜色后调用 `ThemeConfig.save()` 即可落盘。还有一个现成的 `ThemePresetPicker` 控件，帮你画一个"左右箭头切换颜色"的按钮条，直接嵌进你的设置界面就能用。

---

## 其它能力速览

### 主题系统 `gui/theme`

- 纯代码画出来的界面，**不依赖任何图片**，干干净净。
- 控件都是现成的：`ThemedButton`（按钮）、`ThemedToggle`（开关）、`ThemedRadio`（单选）。
- 两种基础风格：`ModernTheme`（现代风格，可换配色）、`VanillaTheme`（原版箱子风格）。

### 光标工具 `gui/GuiCursor`

- `GuiCursor` 是一个通用光标工具（静态门面），帮你统一管理 GLFW 的手形/箭头光标。
- 用法很简单：悬停时调 `GuiCursor.applyHand()`，需要箭头时调 `GuiCursor.applyArrow()`，或 `GuiCursor.apply(true/false)` 二选一。
- 关键建议：**悬停时置手形、离开时不主动写箭头**，把"恢复箭头"交给原版 `Screen` 自己处理，避免多个按钮每帧互相覆盖光标、干扰原版按键的手形。

```java
import com.avalon.base.gui.GuiCursor;

// 在按钮的 renderWidget 里：
if (isHoveredOrFocused() && active) {
    GuiCursor.applyHand();  // 悬停显示手形，离开不覆盖
}
```

### 配置界面基类 `gui/screen`

- `AvalonConfigScreen` 是一个现成的配置页面模板。
- 你只要继承它、实现一个 `saveConfig()` 方法，就自动拥有：保存/取消按钮、编辑权限判断、滚动列表、点击音效等。

### 配置读写 `config/AvalonToml`

- 把配置存成 TOML 文件，支持读取、写入、热加载（文件被改能自动发现）。

### 网络 `network/AvalonNetwork`

- 把客户端和服务端之间的"传消息"封装好，你只需定义消息内容即可。

### 平台服务 `platform/Services`

- `Services.PLATFORM` 告诉你当前是 Forge 还是 Fabric、某个模组装没装、是不是开发环境。

---

## 怎么把它加进你的模组？

1. 先构建出 AvalonBase 的 jar 文件。
2. 在你的模组里，把这个 jar 作为依赖加上（编译期用 `compileOnly`，运行期作为前置 mod 要求玩家也装上）。
3. 在模组描述文件里声明它：Fabric 在 `fabric.mod.json` 的 `depends` 加 `"avalonbase"`，Forge 在 `mods.toml` 里加依赖项。

具体依赖坐标和示例见项目里的构建说明。

---

## 一个最简单的例子

```java
// 1. 定义一个配置界面
public class MyScreen extends AvalonConfigScreen {
    public MyScreen() {
        super(Component.translatable("gui.mymod.title"));
        setTheme(Themes.current());   // 用当前全局主题
        addSaveCancelButtons();
    }
    @Override protected void saveConfig() {
        // 保存你的配置
    }
}

// 2. 在模组初始化时加载主题
ThemeConfig.init(configDir);
ThemeConfig.load();
```

就这么多。剩下的界面、按钮、颜色、保存逻辑，AvalonBase 都帮你处理好了。

---

## 技术信息

- 游戏版本：Minecraft 1.20.1
- 支持平台：Forge 47.2.30 / Fabric Loader 0.16.9
- Java 17
- 构建：MultiLoader（common / fabric / forge 三模块）
- 作者：Huziyang520

## License

MIT
