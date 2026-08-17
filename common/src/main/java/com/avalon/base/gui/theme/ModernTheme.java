package com.avalon.base.gui.theme;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;

/**
 * 现代风格主题（直角风格，纯代码渲染，替换 PNG 九宫格）。
 *
 * <p>颜色不再硬编码，而是来自一个 {@link Palette} 配色盘。通过传入不同的 Palette
 * （如 {@link ThemePreset} 内置的末影紫 / 海洋蓝 / 翡翠绿 / 熔岩红 / 米白），即可得到
 * 不同配色的现代主题；也可用 {@link Palette#builder()} 自定义任意配色。
 *
 * <p>无参构造默认使用 {@link ThemePreset#ENDER_PURPLE}（末影紫），保持向后兼容。
 */
public final class ModernTheme implements GuiTheme {

    private final Palette palette;

    /** 默认构造：末影紫配色（向后兼容）。 */
    public ModernTheme() {
        this(ThemePreset.ENDER_PURPLE.palette());
    }

    /** 用指定配色盘构造主题。 */
    public ModernTheme(Palette palette) {
        this.palette = palette;
    }

    /** 该主题使用的配色盘。 */
    public Palette palette() {
        return palette;
    }

    @Override public void drawPanel(GuiGraphics g, int x, int y, int w, int h) {
        // 渐变背景
        g.fillGradient(x, y, x + w, y + h, palette.panelTop, palette.panelBottom);
        // 外阴影（右下偏移）
        RenderSystem.enableBlend();
        g.fill(x + 3, y + 3, x + w + 3, y + h + 3, palette.panelShadow);
        RenderSystem.disableBlend();
        // 直角边框线
        g.fill(x, y, x + w, y + 1, palette.border);
        g.fill(x, y + h - 1, x + w, y + h, palette.border);
        g.fill(x, y, x + 1, y + h, palette.border);
        g.fill(x + w - 1, y, x + w, y + h, palette.border);
    }

    @Override public void drawCard(GuiGraphics g, int x, int y, int w, int h) {
        // 纯色填充 + 直角边框
        g.fill(x, y, x + w, y + h, palette.cardFill);
        g.fill(x, y, x + w, y + 1, palette.cardBorder);
        g.fill(x, y + h - 1, x + w, y + h, palette.cardBorder);
        g.fill(x, y, x + 1, y + h, palette.cardBorder);
        g.fill(x + w - 1, y, x + w, y + h, palette.cardBorder);
    }

    @Override public void drawButton(GuiGraphics g, int x, int y, int w, int h, float hover, boolean active, ButtonRole role) {
        int c = !active ? palette.btnDisabled :
                (role == ButtonRole.PRIMARY ? (hover > 0.01f ? palette.btnPrimaryHover : palette.btnPrimary) :
                 (hover > 0.01f ? palette.btnNeutralHover : palette.btnNeutral));
        g.fill(x, y, x + w, y + h, c);
    }

    @Override public void drawRadio(GuiGraphics g, int x, int y, boolean selected, boolean enabled) {
        // 不再使用，ThemedRadio 已改用 drawToggle
    }

    @Override public void drawToggle(GuiGraphics g, int x, int y, float on, boolean enabled) {
        // 方形勾选框，10x10
        int boxC = enabled ? palette.toggleBox : palette.cardFill;
        int fillC = enabled ? palette.toggleFill : palette.disabled;
        int borderC = enabled ? palette.toggleBorder : palette.cardBorder;
        g.fill(x, y, x + 10, y + 10, boxC);
        g.fill(x, y, x + 10, y + 1, borderC);
        g.fill(x, y + 9, x + 10, y + 10, borderC);
        g.fill(x, y, x + 1, y + 10, borderC);
        g.fill(x + 9, y, x + 10, y + 10, borderC);
        if (on >= 0.5f) {
            g.fill(x + 2, y + 2, x + 8, y + 8, fillC);
        }
    }

    @Override public void drawScrollTrack(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + h, palette.scrollTrack);
    }

    @Override public void drawScrollThumb(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + h, palette.scrollThumb);
        g.fill(x, y, x + w, y + 1, palette.scrollThumbHighlight);
    }

    @Override public void drawIcon(GuiGraphics g, String icon, int x, int y, float alpha) {
        int col = ((int) (alpha * 255) << 24) | (palette.accent & 0xFFFFFF);
        if ("x".equals(icon)) {
            for (int i = 0; i < 5; i++) {
                g.fill(x + i, y + i, x + i + 2, y + i + 2, col);
                g.fill(x + 7 - i, y + i, x + 9 - i, y + i + 2, col);
            }
        } else {
            g.fill(x + 4, y + 1, x + 5, y + 8, col);
            g.fill(x + 1, y + 4, x + 8, y + 5, col);
        }
    }

    @Override public int titleColor() { return palette.title; }
    @Override public int labelColor() { return palette.label; }
    @Override public int textColor() { return palette.text; }
    @Override public int disabledColor() { return palette.disabled; }
    @Override public int okColor() { return palette.ok; }
    @Override public int warnColor() { return palette.warn; }
    @Override public boolean vanillaButtons() { return false; }
    @Override public boolean animated() { return false; }  // 直角风格无动画
}
