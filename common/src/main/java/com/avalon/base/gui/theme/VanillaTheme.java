package com.avalon.base.gui.theme;

import net.minecraft.client.gui.GuiGraphics;

/**
 * 原版箱子风格主题。纯代码渲染，不依赖 PNG 贴图。
 */
public final class VanillaTheme implements GuiTheme {

    @Override public void drawPanel(GuiGraphics g, int x, int y, int w, int h) {
        // 外边框
        g.fill(x - 1, y - 1, x + w + 1, y + h + 1, 0xFF000000);
        // 主填充
        g.fill(x, y, x + w, y + h, 0xFFC6C6C6);
        // 左上高光（模拟圆角减淡）
        g.fill(x, y, x + w - 2, y + 2, 0xFFFFFFFF);
        g.fill(x, y, x + 2, y + h - 2, 0xFFFFFFFF);
        // 右下阴影
        g.fill(x + 2, y + h - 2, x + w, y + h, 0xFF555555);
        g.fill(x + w - 2, y + 2, x + w, y + h, 0xFF555555);
        // 四角圆角过渡（削去尖角）
        g.fill(x, y, x + 1, y + 1, 0xFFAAAAAA);
        g.fill(x + w - 1, y, x + w, y + 1, 0xFF666666);
        g.fill(x, y + h - 1, x + 1, y + h, 0xFF666666);
        g.fill(x + w - 1, y + h - 1, x + w, y + h, 0xFF444444);
    }

    @Override public void drawCard(GuiGraphics g, int x, int y, int w, int h) {
        g.fill(x, y, x + w, y + h, 0xFF9D9D9D);
        g.fill(x, y, x + w, y + 1, 0xFF373737);
        g.fill(x, y, x + 1, y + h, 0xFF373737);
        g.fill(x, y + h - 1, x + w, y + h, 0xFFFFFFFF);
        g.fill(x + w - 1, y, x + w, y + h, 0xFFFFFFFF);
    }

    @Override public void drawButton(GuiGraphics g, int x, int y, int w, int h, float hover, boolean active, ButtonRole role) {}

    @Override public void drawRadio(GuiGraphics g, int x, int y, boolean selected, boolean enabled) {
        drawCard(g, x + 1, y + 1, 10, 10);
        if (selected) g.fill(x + 4, y + 4, x + 9, y + 9, enabled ? 0xFF202020 : 0xFF666666);
    }

    @Override public void drawToggle(GuiGraphics g, int x, int y, float on, boolean enabled) {
        drawCard(g, x, y, 10, 10);
        if (on >= 0.5f) {
            // 居中填充 6x6
            g.fill(x + 2, y + 2, x + 8, y + 8, enabled ? 0xFF202020 : 0xFF666666);
        }
    }

    @Override public void drawScrollTrack(GuiGraphics g, int x, int y, int w, int h) { g.fill(x, y, x + w, y + h, 0xFF8B8B8B); }
    @Override public void drawScrollThumb(GuiGraphics g, int x, int y, int w, int h) { g.fill(x, y, x + w, y + h, 0xFF373737); }
    @Override public void drawIcon(GuiGraphics g, String icon, int x, int y, float alpha) {
        int col = ((int) (alpha * 255) << 24) | 0x373737;
        if ("x".equals(icon)) {
            // 2px宽叉号，9x9区域内居中
            for (int i = 0; i < 5; i++) {
                g.fill(x + i, y + i, x + i + 2, y + i + 2, col);
                g.fill(x + 7 - i, y + i, x + 9 - i, y + i + 2, col);
            }
        } else {
            g.fill(x + 4, y + 1, x + 5, y + 8, col);
            g.fill(x + 1, y + 4, x + 8, y + 5, col);
        }
    }

    @Override public int titleColor() { return 0x404040; }
    @Override public int labelColor() { return 0x404040; }
    @Override public int textColor() { return 0x303030; }
    @Override public int disabledColor() { return 0x808080; }
    @Override public int okColor() { return 0x2E7D32; }
    @Override public int warnColor() { return 0xB02020; }
    @Override public boolean vanillaButtons() { return true; }
    @Override public boolean animated() { return false; }
}
