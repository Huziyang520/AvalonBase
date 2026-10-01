package com.avalon.base.gui.theme;

import net.minecraft.client.gui.GuiGraphics;

/**
 * 可视化编辑页面的主题抽象。供业务模组的 Screen 复用。
 * 实现有 {@link VanillaTheme}（原版箱式）与 {@link ModernTheme}（末影紫渐变）。
 */
public interface GuiTheme {

    void drawPanel(GuiGraphics g, int x, int y, int w, int h);

    void drawCard(GuiGraphics g, int x, int y, int w, int h);

    void drawButton(GuiGraphics g, int x, int y, int w, int h, float hover, boolean active, ButtonRole role);

    void drawRadio(GuiGraphics g, int x, int y, boolean selected, boolean enabled);

    void drawToggle(GuiGraphics g, int x, int y, float on, boolean enabled);

    void drawScrollTrack(GuiGraphics g, int x, int y, int w, int h);

    void drawScrollThumb(GuiGraphics g, int x, int y, int w, int h);

    void drawIcon(GuiGraphics g, String icon, int x, int y, float alpha);

    int titleColor();

    int labelColor();

    int textColor();

    int disabledColor();

    int okColor();

    int warnColor();

    boolean vanillaButtons();

    boolean animated();

    enum ButtonRole { PRIMARY, NEUTRAL }
}
