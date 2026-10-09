package com.avalon.base.gui.util;

import net.minecraft.client.input.MouseButtonEvent;

/**
 * Mouse-button helpers for the MC 26.3 input API.
 *
 * <p>MC 26.3 replaced GLFW with SDL, which changed the mouse-button numbering:
 * the left button is now {@code 1} (it was {@code 0} under GLFW). Vanilla reflects
 * this in {@code AbstractWidget.isValidClickButton(MouseButtonInfo)} which tests
 * {@code button() == 1}. That method is {@code protected}, so custom-drawn controls
 * (which are not {@code AbstractWidget}s and hit-test themselves) need this helper
 * to decide whether a click was a left click.</p>
 *
 * <p><b>Version note</b>: keep this value per-version. On 26.2 and older (GLFW) the
 * left button is {@code 0}; do not copy this {@code 1} back to those versions.</p>
 */
public final class MouseButtons {

    /** Left mouse button id on MC 26.3 (SDL). GLFW-era versions use {@code 0}. */
    private static final int LEFT_BUTTON = 1;

    private MouseButtons() {
    }

    /** True when the event is a left-button press. */
    public static boolean isLeft(MouseButtonEvent event) {
        return event != null && event.button() == LEFT_BUTTON;
    }
}
