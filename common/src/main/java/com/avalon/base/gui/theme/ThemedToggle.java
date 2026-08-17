package com.avalon.base.gui.theme;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * 主题化滑动开关。非动画主题下直接取目标值。
 */
public final class ThemedToggle {
    public final int x, relY;
    final Component label;
    final boolean enabled;
    private boolean checked;
    private final Anim slide;
    private final boolean animated;

    public ThemedToggle(int x, int relY, Component label, boolean checked, boolean enabled, boolean animated) {
        this.x = x;
        this.relY = relY;
        this.label = label;
        this.checked = checked;
        this.enabled = enabled;
        this.animated = animated;
        this.slide = new Anim(checked ? 1f : 0f);
    }

    public void setChecked(boolean c) {
        this.checked = c;
        this.slide.setTarget(c ? 1f : 0f);
    }

    public boolean isChecked() {
        return checked;
    }

    public void render(GuiGraphics g, Font font, GuiTheme theme, int y, int mouseX, int mouseY) {
        slide.setTarget(checked ? 1f : 0f);
        float on = animated ? slide.tick(11f) : slide.target();
        boolean hover = enabled && contains(mouseX, mouseY, font, y);
        theme.drawToggle(g, x, y, on, enabled);
        int tc = !enabled ? theme.disabledColor() : (hover ? theme.titleColor() : theme.textColor());
        g.drawString(font, label, x + 24, y + 1, tc, false);
    }

    public boolean contains(double mx, double my, Font font, int y) {
        return mx >= x && mx <= x + 24 + font.width(label) && my >= y && my <= y + 10;
    }

    public boolean isClicked(double mx, double my, Font font, int y) {
        return enabled && contains(mx, my, font, y);
    }
}
