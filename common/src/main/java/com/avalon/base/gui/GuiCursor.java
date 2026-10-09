package com.avalon.base.gui;

import com.mojang.blaze3d.platform.cursor.CursorTypes;
import net.minecraft.client.gui.GuiGraphicsExtractor;

/**
 * Generic cursor helper (static facade) for the MC 26.3 cursor API.
 *
 * <p>MC 26.3 replaced GLFW with SDL <b>and</b> reworked how cursors are chosen:
 * the cursor is requested through {@link GuiGraphicsExtractor#requestCursor}
 * during extraction and applied to the window once at the end of the frame
 * ({@code GuiGraphicsExtractor.applyCursor(Window)}). Calling
 * {@code Window.selectCursor(...)} directly is therefore overwritten at frame end
 * and must not be used for hover feedback.
 *
 * <p>Vanilla widgets do this via {@code AbstractWidget.handleCursor(...)}. Custom
 * drawn controls (which are not widgets and hit-test themselves) must request the
 * pointing hand explicitly, exactly like this helper does.
 *
 * <p>Only request the hand when needed; never request the arrow, otherwise it would
 * clobber the cursor requested by vanilla widgets (buttons / text fields) in the
 * same frame.
 */
public final class GuiCursor {

    private GuiCursor() {
    }

    /** Request the pointing-hand cursor for the current frame. */
    public static void requestHand(GuiGraphicsExtractor graphics) {
        graphics.requestCursor(CursorTypes.POINTING_HAND);
    }

    /** Request the pointing hand only when {@code hand} is true. */
    public static void request(GuiGraphicsExtractor graphics, boolean hand) {
        if (hand) {
            graphics.requestCursor(CursorTypes.POINTING_HAND);
        }
    }
}
