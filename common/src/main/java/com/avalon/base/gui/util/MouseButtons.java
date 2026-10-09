package com.avalon.base.gui.util;

import net.minecraft.client.input.MouseButtonEvent;

/**
 * Mouse-button helpers for the MC 1.21.9+ input API ({@code MouseButtonEvent}).
 *
 * <p>This version line runs on GLFW, whose left button id is {@code 0}. Vanilla
 * reflects this in {@code AbstractWidget.isValidClickButton(MouseButtonInfo)},
 * which tests {@code button() == 0}. That method is {@code protected}, so
 * custom-drawn controls (which are not {@code AbstractWidget}s and hit-test
 * themselves) need this helper to decide whether a click was a left click.</p>
 *
 * <p><b>Version note</b>: MC 26.3 (SDL) renumbered the left button to {@code 1};
 * the 26.3 branch keeps its own copy of this file with that value. Do not copy
 * {@code 1} back to this GLFW-era branch.</p>
 */
public final class MouseButtons {

    /** Left mouse button id on GLFW (every 1.21.9 - 26.2 version). */
    private static final int LEFT_BUTTON = 0;

    private MouseButtons() {
    }

    /** True when the event is a left-button press. */
    public static boolean isLeft(MouseButtonEvent event) {
        return event != null && event.button() == LEFT_BUTTON;
    }
}
