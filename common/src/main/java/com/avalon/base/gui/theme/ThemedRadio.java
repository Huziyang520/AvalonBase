package com.avalon.base.gui.theme;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * 主题化单选点。使用勾选框同款样式（drawToggle）。
 */
public final class ThemedRadio {
    public final int x, relY;
    final Component label;
    final boolean selected, enabled;

    public ThemedRadio(int x, int relY, Component label, boolean selected, boolean enabled) {
        this.x = x;
        this.relY = relY;
        this.label = label;
        this.selected = selected;
        this.enabled = enabled;
    }

    public void render(GuiGraphics g, Font font, GuiTheme theme, int y, int mouseX, int mouseY) {
        boolean hover = enabled && contains(mouseX, mouseY, font, y);
        // 使用勾选框同款样式（drawToggle，on=selected）
        theme.drawToggle(g, x, y, selected ? 1f : 0f, enabled);
        int tc = !enabled ? theme.disabledColor()
                : (selected ? theme.titleColor() : (hover ? theme.titleColor() : theme.textColor()));
        // 统一留白间距（与 ThemedToggle 一致）
        g.drawString(font, label, x + 24, y + 1, tc, false);
    }

    public boolean contains(double mx, double my, Font font, int y) {
        return mx >= x && mx <= x + 24 + font.width(label) && my >= y && my <= y + 10;
    }

    public boolean isClicked(double mx, double my, Font font, int y) {
        return enabled && contains(mx, my, font, y);
    }
}
