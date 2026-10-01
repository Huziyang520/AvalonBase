package com.avalon.base.gui.theme;

import com.avalon.base.gui.util.TextFit;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/**
 * 主题化单选点。使用勾选框同款样式（drawToggle）。
 *
 * <p>支持可选的最大标签宽度 {@code maxLabelW}：超过该宽度的长标签（尤其是英文串）会在
 * {@link TextFit#fit} 里按宽度截断并追加省略号，确保文字不溢出面板/卡片边框，即自动排版的一部分。</p>
 */
public final class ThemedRadio {
    public final int x, relY;
    final Component label;
    final boolean selected, enabled;
    /** 标签最大可用宽度（px）。&lt;=0 表示不限宽（向后兼容）。 */
    private final int maxLabelW;

    public ThemedRadio(int x, int relY, Component label, boolean selected, boolean enabled) {
        this(x, relY, label, selected, enabled, -1);
    }

    public ThemedRadio(int x, int relY, Component label, boolean selected, boolean enabled, int maxLabelW) {
        this.x = x;
        this.relY = relY;
        this.label = label;
        this.selected = selected;
        this.enabled = enabled;
        this.maxLabelW = maxLabelW;
    }

    /** 实际绘制 / 命中的标签文本：超宽时按 maxLabelW 截断加省略号。 */
    private String displayText(Font font) {
        String raw = label.getString();
        if (maxLabelW > 0 && font.width(raw) > maxLabelW) {
            return TextFit.fit(font, raw, maxLabelW);
        }
        return raw;
    }

    public void render(GuiGraphicsExtractor g, Font font, GuiTheme theme, int y, int mouseX, int mouseY) {
        boolean hover = enabled && contains(mouseX, mouseY, font, y);
        // 使用勾选框同款样式（drawToggle，on=selected）
        theme.drawToggle(g, x, y, selected ? 1f : 0f, enabled);
        int tc = !enabled ? theme.disabledColor()
                : (selected ? theme.titleColor() : (hover ? theme.titleColor() : theme.textColor()));
        // 统一留白间距（与 ThemedToggle 一致）
        g.text(font, Component.literal(displayText(font)), x + 24, y + 1, tc, false);
    }

    public boolean contains(double mx, double my, Font font, int y) {
        return mx >= x && mx <= x + 24 + font.width(displayText(font)) && my >= y && my <= y + 10;
    }

    /**
     * Returns the full label text when the label is currently truncated AND
     * the pointer is over the control, so the screen can show a hover box with
     * the original text. Returns {@code null} otherwise (not truncated, or not
     * hovered). Enabled state is intentionally ignored: read-only users still
     * need to read the full label.
     */
    public String truncatedTooltip(Font font, double mx, double my, int y) {
        String raw = label.getString();
        if (maxLabelW > 0 && font.width(raw) > maxLabelW && contains(mx, my, font, y)) {
            return raw;
        }
        return null;
    }

    public boolean isClicked(double mx, double my, Font font, int y) {
        return enabled && contains(mx, my, font, y);
    }
}