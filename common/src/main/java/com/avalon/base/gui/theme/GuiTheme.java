package com.avalon.base.gui.theme;

import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * 可视化编辑页面的主题抽象。供业务模组的 Screen 复用。
 * 实现有 {@link VanillaTheme}（原版箱式）与 {@link ModernTheme}（末影紫渐变）。
 */
public interface GuiTheme {

    void drawPanel(GuiGraphicsExtractor g, int x, int y, int w, int h);

    void drawCard(GuiGraphicsExtractor g, int x, int y, int w, int h);

    void drawButton(GuiGraphicsExtractor g, int x, int y, int w, int h, float hover, boolean active, ButtonRole role);

    void drawRadio(GuiGraphicsExtractor g, int x, int y, boolean selected, boolean enabled);

    void drawToggle(GuiGraphicsExtractor g, int x, int y, float on, boolean enabled);

    void drawScrollTrack(GuiGraphicsExtractor g, int x, int y, int w, int h);

    void drawScrollThumb(GuiGraphicsExtractor g, int x, int y, int w, int h);

    void drawIcon(GuiGraphicsExtractor g, String icon, int x, int y, float alpha);

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
