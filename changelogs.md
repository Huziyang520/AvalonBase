# Changelog — AvalonBase

## 1.5.1

- Added an animation API: `ScreenAnim` (several open/close animation types plus easing, can be disabled) and `ScrollAnim` (smooth list scrolling).
  新增动画 API：`ScreenAnim`（多种界面打开/关闭动画类型与缓动，可关闭）与 `ScrollAnim`（列表平滑滚动）。
- Added `ScreenAnim.suspend / resume`, so full-screen overlays such as the read-only overlay can leave the transform and no longer scale with the animation.
  新增 `ScreenAnim.suspend / resume`，整屏浮层（如只读遮罩）可临时退出变换，不再随动画缩放。
- `AvalonConfigScreen` now uses the vanilla background pass: the menu background (panorama + blur) in the main menu and the standard dim layer in-world; secondary dialogs follow the same rule.
  配置界面基类 `AvalonConfigScreen` 背景改用原版背景通道：主菜单显示菜单背景（全景图 + 模糊），世界内为标准暗化层；二级弹窗同规则。
- Added and refined themed controls: checkbox-style radio (`ThemedRadio`) and sliding toggle (`ThemedToggle`, with over-wide label truncation plus full-text hover).
  新增与完善主题化控件：勾选框式单选 `ThemedRadio` 与滑动开关 `ThemedToggle`（支持超宽标签截断 + 悬停全文）。
- Unified the "card left edge + 6" inset across cards and controls.
  统一「卡片左缘 + 6」内边距口径。
- Hover tooltips in the "Vanilla style textures" theme now use the vanilla tooltip.
  「原版风格纹理」主题下的悬停提示框改用原版提示框。
- Added developer docs: `docts/Animations.md`, `docts/ConfigAndNetworking.md`, `docts/InterfaceAndThemes.md`.
  新增开发者文档：`docts/Animations.md`、`docts/ConfigAndNetworking.md`、`docts/InterfaceAndThemes.md`。
