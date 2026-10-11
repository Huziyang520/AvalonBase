package com.avalon.base.gui.util;

/**
 * Mouse-button helpers for the GLFW-era input API (1.20.2 - 1.21.8).
 *
 * <p>These versions deliver the raw GLFW button id: {@code Screen.mouseClicked}
 * receives the index directly and the left button is {@code 0}, matching
 * {@code AbstractWidget.isValidClickButton(int)} which tests {@code button == 0}.
 * That method is {@code protected}, so custom-drawn controls (which are not
 * {@code AbstractWidget}s and hit-test themselves) need this helper to decide
 * whether a click was a left click.</p>
 *
 * <p><b>Version note</b>: keep this value per-version. From 26.3 onwards (SDL
 * input API) the left button is {@code 1}; do not copy that value back here.</p>
 */
public final class MouseButtons {

    /** Left mouse button id under GLFW (1.20.2 - 1.21.8). */
    private static final int LEFT_BUTTON = 0;

    private MouseButtons() {
    }

    /** True when the given GLFW mouse-button id is a left-button press. */
    public static boolean isLeft(int button) {
        return button == LEFT_BUTTON;
    }
}
