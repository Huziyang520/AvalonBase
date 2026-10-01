package com.avalon.base.gui.theme;

import com.avalon.base.gui.util.TextFit;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/**
 * 主题化滑动开关。非动画主题下直接取目标值。
 *
 * <p>支持可选的最大标签宽度 {@code maxLabelW}：超过该宽度的长标签会在 {@link TextFit#fit}
 * 里按宽度截断并追加省略号，确保文字不溢出面板/卡片边框。</p>
 */
public final class ThemedToggle {
    public final int x, relY;
    final Component label;
    final boolean enabled;
    /** 标签最大可用宽度（px）。&lt;=0 表示不限宽（向后兼容）。 */
    private final int maxLabelW;
    private boolean checked;
    private final Anim slide;
    private final boolean animated;

    public ThemedToggle(int x, int relY, Component label, boolean checked, boolean enabled, boolean animated) {
        this(x, relY, label, checked, enabled, animated, -1);
    }

    public ThemedToggle(int x, int relY, Component label, boolean checked, boolean enabled, boolean animated, int maxLabelW) {
        this.x = x;
        this.relY = relY;
        this.label = label;
        this.checked = checked;
        this.enabled = enabled;
        this.animated = animated;
        this.maxLabelW = maxLabelW;
        this.slide = new Anim(checked ? 1f : 0f);
    }

    public void setChecked(boolean c) {
        this.checked = c;
        this.slide.setTarget(c ? 1f : 0f);
    }

    public boolean isChecked() {
        return checked;
    }

    /** 实际绘制的标签文本：超宽时截断加省略号。 */
    private String displayText(Font font) {
        String raw = label.getString();
        if (maxLabelW > 0 && font.width(raw) > maxLabelW) {
            return TextFit.fit(font, raw, maxLabelW);
        }
        return raw;
    }

    public void render(GuiGraphicsExtractor g, Font font, GuiTheme theme, int y, int mouseX, int mouseY) {
        slide.setTarget(checked ? 1f : 0f);
        float on = animated ? slide.tick(11f) : slide.target();
        boolean hover = enabled && contains(mouseX, mouseY, font, y);
        theme.drawToggle(g, x, y, on, enabled);
        int tc = !enabled ? theme.disabledColor() : (hover ? theme.titleColor() : theme.textColor());
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