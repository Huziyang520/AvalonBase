package com.avalon.base.gui.theme;

import com.mojang.blaze3d.vertex.PoseStack;

/**
 * Visual editor page theme abstraction. Reused by business mod screens.
 * Implementations: {@link VanillaTheme} (vanilla box style) and {@link ModernTheme} (ender purple gradient).
 */
public interface GuiTheme {

    void drawPanel(PoseStack pose, int x, int y, int w, int h);

    void drawCard(PoseStack pose, int x, int y, int w, int h);

    void drawButton(PoseStack pose, int x, int y, int w, int h, float hover, boolean active, ButtonRole role);

    void drawRadio(PoseStack pose, int x, int y, boolean selected, boolean enabled);

    void drawToggle(PoseStack pose, int x, int y, float on, boolean enabled);

    void drawScrollTrack(PoseStack pose, int x, int y, int w, int h);

    void drawScrollThumb(PoseStack pose, int x, int y, int w, int h);

    void drawIcon(PoseStack pose, String icon, int x, int y, float alpha);

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