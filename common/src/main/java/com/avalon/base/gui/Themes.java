package com.avalon.base.gui;

import com.avalon.base.gui.theme.GuiTheme;
import com.avalon.base.gui.theme.ModernTheme;
import com.avalon.base.gui.theme.Palette;
import com.avalon.base.gui.theme.ThemePreset;

/**
 * 全局主题入口（静态门面）。
 *
 * <p>供业务模组在任意地方获取"当前生效的主题"，无需各自维护主题实例：
 *
 * <pre>{@code
 * GuiTheme theme = Themes.current();          // 获取当前主题
 * Themes.setPreset(ThemePreset.OCEAN_BLUE);   // 切换到某内置预设
 * Themes.setCustom(Palette.builder()...build()); // 使用自定义配色
 * }</pre>
 *
 * <p>内部缓存 {@link GuiTheme} 实例，避免每次调用都新建。默认预设为
 * {@link ThemePreset#ENDER_PURPLE}（末影紫）。切换预设后，可选地由调用方触发
 * {@link com.avalon.base.config.ThemeConfig#save()} 进行持久化。
 */
public final class Themes {

    private static ThemePreset current = ThemePreset.ENDER_PURPLE;
    private static GuiTheme cached;

    private Themes() {
    }

    /** 当前生效的预设（若使用 {@link #setCustom} 则为 {@code null}）。 */
    public static ThemePreset currentPreset() {
        return current;
    }

    /** 获取当前主题实例（惰性构造并缓存）。 */
    public static GuiTheme current() {
        if (cached == null) {
            cached = new ModernTheme(current == null
                    ? Palette.builder().build()
                    : current.palette());
        }
        return cached;
    }

    /** 切换到某个内置预设，并清空缓存。 */
    public static void setPreset(ThemePreset preset) {
        current = preset == null ? ThemePreset.ENDER_PURPLE : preset;
        cached = null;
    }

    /** 使用自定义配色盘，并清空缓存（此时 {@link #currentPreset()} 返回 {@code null}）。 */
    public static void setCustom(Palette palette) {
        current = null;
        cached = palette == null ? new ModernTheme() : new ModernTheme(palette);
    }

    /** 重置为默认末影紫。 */
    public static void reset() {
        current = ThemePreset.ENDER_PURPLE;
        cached = null;
    }
}
