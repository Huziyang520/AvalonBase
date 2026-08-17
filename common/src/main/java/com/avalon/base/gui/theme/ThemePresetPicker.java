package com.avalon.base.gui.theme;

import com.avalon.base.config.ThemeConfig;
import com.avalon.base.gui.Themes;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * 主题预设切换控件（纯代码渲染，无 PNG）。
 *
 * <p>一个横向的循环切换条：点击左侧箭头切到上一个预设，点击右侧箭头切到下一个，
 * 中间显示当前预设名称。每次切换会更新全局主题 {@link Themes} 并触发
 * {@link ThemeConfig#save()} 落盘，供业务模组直接嵌入配置屏复用。
 *
 * <p>渲染沿用 {@link GuiTheme} 的 {@code drawCard} / {@code drawButton} / 文字色，
 * 因此能随当前主题自适应。切换后立即生效，控件自身的下次渲染即使用新主题。
 */
public final class ThemePresetPicker {

    /** 控件整体宽度（含左右箭头与中间标签）。 */
    public static final int WIDTH = 160;
    /** 控件高度。 */
    public static final int HEIGHT = 20;

    private final int x;
    private final int relY;
    private final boolean enabled;

    public ThemePresetPicker(int x, int relY, boolean enabled) {
        this.x = x;
        this.relY = relY;
        this.enabled = enabled;
    }

    public int relY() {
        return relY;
    }

    /**
     * 渲染控件。
     *
     * @param g       绘制上下文
     * @param font    字体
     * @param theme   用于绘制控件外观的主题（切换后内部会更新全局主题）
     * @param y       屏幕绝对纵坐标（由调用方用基类 {@code yo(relY)} 换算后传入）
     * @param mouseX  鼠标横坐标
     * @param mouseY  鼠标纵坐标
     */
    public void render(GuiGraphics g, Font font, GuiTheme theme, int y, int mouseX, int mouseY) {
        ThemePreset current = Themes.currentPreset();
        if (current == null) current = ThemePreset.ENDER_PURPLE;

        // 左箭头
        boolean leftHover = enabled && containsArrow(mouseX, mouseY, y, true);
        theme.drawButton(g, x, y, 20, HEIGHT, leftHover ? 1f : 0f, enabled,
                GuiTheme.ButtonRole.NEUTRAL);
        int arrowColor = enabled ? (theme instanceof ModernTheme m ? m.palette().btnText : 0xFFFFFF)
                : theme.disabledColor();
        drawLeftArrow(g, font, x, y, arrowColor);

        // 右箭头
        boolean rightHover = enabled && containsArrow(mouseX, mouseY, y, false);
        theme.drawButton(g, x + WIDTH - 20, y, 20, HEIGHT, rightHover ? 1f : 0f, enabled,
                GuiTheme.ButtonRole.NEUTRAL);
        drawRightArrow(g, font, x + WIDTH - 20, y, arrowColor);

        // 中间当前名称
        theme.drawCard(g, x + 20, y, WIDTH - 40, HEIGHT);
        Component name = Component.translatable(current.displayKey());
        int nameColor = enabled ? theme.textColor() : theme.disabledColor();
        g.drawString(font, name,
                x + 20 + ((WIDTH - 40) - font.width(name)) / 2,
                y + (HEIGHT - 8) / 2, nameColor, false);
    }

    /** 鼠标是否落在某个箭头区域。 */
    private boolean containsArrow(double mx, double my, int y, boolean left) {
        int ax = left ? x : x + WIDTH - 20;
        return mx >= ax && mx <= ax + 20 && my >= y && my <= y + HEIGHT;
    }

    /** 命中测试：鼠标是否落在控件内（任一箭头）。 */
    public boolean contains(double mx, double my, int y) {
        return containsArrow(mx, my, y, true) || containsArrow(mx, my, y, false);
    }

    /**
     * 处理点击：命中左/右箭头则切换预设并保存，返回 true。
     *
     * @param mx 鼠标横坐标
     * @param my 鼠标纵坐标
     * @param y  屏幕绝对纵坐标
     */
    public boolean mouseClicked(double mx, double my, int y) {
        if (!enabled) return false;
        boolean left = containsArrow(mx, my, y, true);
        boolean right = containsArrow(mx, my, y, false);
        if (!left && !right) return false;

        ThemePreset current = Themes.currentPreset();
        if (current == null) current = ThemePreset.ENDER_PURPLE;
        Themes.setPreset(left ? current.previous() : current.next());
        ThemeConfig.save();
        return true;
    }

    private static void drawLeftArrow(GuiGraphics g, Font font, int x, int y, int color) {
        int cx = x + 7, cy = y + HEIGHT / 2;
        for (int i = 0; i < 3; i++) {
            g.fill(cx - i, cy - 3 + i, cx - i + 1, cy + 3 - i, color);
        }
    }

    private static void drawRightArrow(GuiGraphics g, Font font, int x, int y, int color) {
        int cx = x + 13, cy = y + HEIGHT / 2;
        for (int i = 0; i < 3; i++) {
            g.fill(cx + i, cy - 3 + i, cx + i + 1, cy + 3 - i, color);
        }
    }
}
