package com.avalon.base.gui.theme;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

/**
 * 主题化按钮。非原版主题走自定义 drawButton 渲染，原版主题退化为原版按钮渲染。
 */
public final class ThemedButton extends Button {
    private final GuiTheme theme;
    private final GuiTheme.ButtonRole role;
    private final Font btnFont;
    private final Anim hoverAnim = new Anim(0f);

    public ThemedButton(int x, int y, int w, int h, Component label, OnPress onPress,
                        GuiTheme theme, GuiTheme.ButtonRole role, Font font) {
        super(x, y, w, h, label, onPress, DEFAULT_NARRATION);
        this.theme = theme;
        this.role = role;
        this.btnFont = font;
    }

    @Override
    public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        if (theme.vanillaButtons()) {
            super.renderWidget(g, mouseX, mouseY, partialTick);
            return;
        }
        hoverAnim.setTarget(isHoveredOrFocused() && active ? 1f : 0f);
        float hover = theme.animated() ? hoverAnim.tick(12f) : hoverAnim.target();
        theme.drawButton(g, getX(), getY(), getWidth(), getHeight(), hover, active, role);
        int tc = active ? buttonTextColor() : theme.disabledColor();
        g.drawString(btnFont, getMessage(),
                getX() + (getWidth() - btnFont.width(getMessage())) / 2,
                getY() + (getHeight() - 8) / 2, tc, false);
    }

    /**
     * 按钮激活态文字色。优先读取 {@link ModernTheme} 配色盘中的 {@code btnText}，
     * 保证亮色主题下文字可读；非 ModernTheme（如 {@link VanillaTheme}）回退白色。
     */
    private int buttonTextColor() {
        if (theme instanceof ModernTheme m) {
            return m.palette().btnText;
        }
        return 0xFFFFFF;
    }
}
