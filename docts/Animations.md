**English** | [中文](#中文)

---

# Animations

AvalonBase ships a small animation toolkit in `com.avalon.base.gui.anim`. Using it is optional.

| Class | Responsibility |
|---|---|
| `ScreenAnimType` | Animation types (9) + easing curves (4) + default durations |
| `ScreenAnim` | Screen-level animator: timeline, transform, veil, mouse mapping, close hand-over |
| `ScrollAnim` | Smooth list-scrolling value |

## `ScreenAnimType`

| Type | Motion | Default duration | Easing |
|---|---|---|---|
| `NONE` | No animation | — | — |
| `FADE` | Black veil fades in / out | 180 ms | `EASE_OUT_CUBIC` |
| `SLIDE_UP` | Slides in from the bottom | 220 ms | `EASE_OUT_CUBIC` |
| `SLIDE_DOWN` | Slides in from the top | 220 ms | `EASE_OUT_CUBIC` |
| `SLIDE_LEFT` | Slides in from the right | 220 ms | `EASE_OUT_CUBIC` |
| `SLIDE_RIGHT` | Slides in from the left | 220 ms | `EASE_OUT_CUBIC` |
| `SCALE_BOUNCE` | Scales up with an overshoot (bounce) | 320 ms | `EASE_OUT_BACK` |
| `POP_ZOOM` | Shrinks down from slightly larger | 200 ms | `EASE_OUT_BACK` |
| `DROP_BOUNCE` | Drops in and bounces on landing | 420 ms | `EASE_OUT_BOUNCE` |

The direction describes how it **enters**; closing plays the same type reversed. `defaultDurationMs()`, `ease(t)`, `isNone()`, `isVeil()` are available.

## `ScreenAnim`

```java
ScreenAnim anim = new ScreenAnim(ScreenAnimType.SCALE_BOUNCE, ScreenAnimType.SCALE_BOUNCE);

anim.playOpen();                     // start
anim.beginFrame(g, width, height);   // first line of the frame
anim.endFrame(g, width, height);     // last line of the frame
anim.isActive();  anim.progress();   // 0..1
```

Closing:

```java
public void onClose() {
    if (anim.beginClose()) return;   // the animation took over; close later
    doCloseNow();                    // nothing to animate; close now
}

public void tick() {
    super.tick();
    if (anim.isCloseFinished()) doCloseNow();
}
```

`beginClose()` returns `true` when it took over (return immediately), `false` when there is no close animation. `doCloseNow()` should be idempotent.

Self-drawn hit tests use the animation coordinate space:

```java
mouseX = (int) anim.localX(mouseX, width);
mouseY = (int) anim.localY(mouseY, height);
```

`ScreenAnim.disabled()` gives a non-animating instance (frame calls become no-ops).

## Wiring an `AvalonConfigScreen` subclass

```java
// constructor
configureAnimations(enabled, ScreenAnimType.SCALE_BOUNCE, ScreenAnimType.SCALE_BOUNCE);
playOpenAnimation();

// extractRenderState
mouseX = (int) animation().localX(mouseX, width);
mouseY = (int) animation().localY(mouseY, height);
renderBackdrop(graphics);
beginAnimatedRender(graphics);
    // ... your drawing ...
endAnimatedRender(graphics);
```

`onClose()` and `tick()` are already handled by the base class. Not calling `configureAnimations` means no animation.

## `ScrollAnim`

```java
private final ScrollAnim blAnim = new ScrollAnim(0);   // initial index

blAnim.setTarget(blScroll);   // wheel / insert / remove: set the target
blAnim.snapTo(0);             // jump without sliding (e.g. switching data sets)

int idx = blAnim.displayValue() + i;                       // rows
float p = blAnim.value() / Math.max(1, size - visibleRows); // scrollbar
blAnim.tick();                                             // once per frame
```

## Notes

1. Keep the backdrop **outside** the transform: call `beginAnimatedRender` after it, `endAnimatedRender` after your drawing.
2. `FADE` draws its veil after the transform is undone.
3. The transform covers everything drawn in between, including vanilla widgets, so pass animation-space mouse coordinates to custom hit tests.
4. A full-screen layer that must not scale (for example a read-only dim overlay) can step out of the transform with `anim.suspend(g)` and step back in with `anim.resume(g, width, height)`; pair them inside the same frame.

---
---

<a id="中文"></a>

[English](#animations) | **中文**

---

# 动画 API

AvalonBase 在 `com.avalon.base.gui.anim` 里提供一套小型动画工具，用不用都行。

| 类 | 职责 |
|---|---|
| `ScreenAnimType` | 动画类型（9 种）+ 缓动（4 种）+ 默认时长 |
| `ScreenAnim` | 屏幕级动画器：时间轴、变换、黑幕、鼠标换算、关闭接管 |
| `ScrollAnim` | 列表平滑滚动值 |

## `ScreenAnimType`

| 类型 | 动作 | 默认时长 | 缓动 |
|---|---|---|---|
| `NONE` | 无动画 | — | — |
| `FADE` | 黑幕淡入 / 淡出 | 180ms | `EASE_OUT_CUBIC` |
| `SLIDE_UP` | 从下方滑入 | 220ms | `EASE_OUT_CUBIC` |
| `SLIDE_DOWN` | 从上方滑入 | 220ms | `EASE_OUT_CUBIC` |
| `SLIDE_LEFT` | 从右侧滑入 | 220ms | `EASE_OUT_CUBIC` |
| `SLIDE_RIGHT` | 从左侧滑入 | 220ms | `EASE_OUT_CUBIC` |
| `SCALE_BOUNCE` | 放大并回弹过冲（弹跳） | 320ms | `EASE_OUT_BACK` |
| `POP_ZOOM` | 由略大收缩到 1 | 200ms | `EASE_OUT_BACK` |
| `DROP_BOUNCE` | 落下并弹性触底 | 420ms | `EASE_OUT_BOUNCE` |

方向描述的是"怎么进来"；关屏时同一类型反向播放。可用方法：`defaultDurationMs()`、`ease(t)`、`isNone()`、`isVeil()`。

## `ScreenAnim`

```java
ScreenAnim anim = new ScreenAnim(ScreenAnimType.SCALE_BOUNCE, ScreenAnimType.SCALE_BOUNCE);

anim.playOpen();                     // 起跑
anim.beginFrame(g, width, height);   // 帧首
anim.endFrame(g, width, height);     // 帧尾
anim.isActive();  anim.progress();   // 0..1
```

关闭：

```java
public void onClose() {
    if (anim.beginClose()) return;   // 动画接管 → 稍后再关
    doCloseNow();                    // 没有关闭动画 → 立刻关
}

public void tick() {
    super.tick();
    if (anim.isCloseFinished()) doCloseNow();
}
```

`beginClose()` 返回 `true` 表示已接管（立即 `return`），`false` 表示没有关闭动画。`doCloseNow()` 请写成幂等。

自绘控件的命中测试要用动画坐标系：

```java
mouseX = (int) anim.localX(mouseX, width);
mouseY = (int) anim.localY(mouseY, height);
```

`ScreenAnim.disabled()` 返回不动画实例（帧调用都是空操作）。

## 在 `AvalonConfigScreen` 子类里接线

```java
// 构造函数
configureAnimations(enabled, ScreenAnimType.SCALE_BOUNCE, ScreenAnimType.SCALE_BOUNCE);
playOpenAnimation();

// extractRenderState
mouseX = (int) animation().localX(mouseX, width);
mouseY = (int) animation().localY(mouseY, height);
renderBackdrop(graphics);
beginAnimatedRender(graphics);
    // ... 你自己的绘制 ...
endAnimatedRender(graphics);
```

`onClose()` 与 `tick()` 已由基类处理；不调用 `configureAnimations` 就是无动画。

## `ScrollAnim`

```java
private final ScrollAnim blAnim = new ScrollAnim(0);   // 初值 = 起始条目

blAnim.setTarget(blScroll);   // 滚轮 / 删除 / 新增：设目标
blAnim.snapTo(0);             // 不滑动地归位（例如切换数据）

int idx = blAnim.displayValue() + i;                       // 列表行
float p = blAnim.value() / Math.max(1, size - visibleRows); // 滚动条
blAnim.tick();                                             // 每帧一次
```

## 注意

1. 背景层留在变换**之外**：`beginAnimatedRender` 放在背景之后，`endAnimatedRender` 放在绘制之后。
2. `FADE` 的黑幕在变换撤销之后绘制。
3. 变换覆盖其间的全部绘制（含原版控件），因此自绘控件要用动画坐标系的鼠标位置。
4. 一个必须不缩放的 全屏层（例如只读的暗色遮罩层），可以用 `anim.suspend(g)` 从变换中脱离出来，再用 `anim.resume(g, width, height)` 重新进入变换；需要将它们配对放在同一帧内。