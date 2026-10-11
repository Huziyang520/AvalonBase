package com.avalon.base.gui.panel;

import com.avalon.base.gui.util.TextFit;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

import java.util.List;

/**
 * Lightweight hover tooltip rendered in the dark-gold "panel" style:
 * an opaque dark fill, a square dim-gold outline and plain wrapped text.
 *
 * <p>Purpose: reveal the full original text of a truncated label. It is drawn
 * manually (not the vanilla {@code Tooltip}) so business screens keep full
 * control over style, placement and read-only layering. The box prefers the
 * area above the cursor, flips below when there is no room, and is always
 * clamped inside the screen.</p>
 */
public final class PanelHover {

    /** Inner padding between the outline and the text. */
    private static final int PAD = 4;
    /** Text line step in pixels. */
    private static final int LINE_H = 10;
    /** Content is wrapped once it gets wider than this. */
    private static final int MAX_CONTENT_W = 212;
    /** Horizontal distance kept from the cursor. */
    private static final int GAP_X = 12;
    /** Vertical distance kept when flipping to either side. */
    private static final int GAP_Y = 8;
    /** Keep-out distance from the screen edges. */
    private static final int EDGE = 2;

    private PanelHover() {
    }

    /**
     * Draw the hover box for {@code text} near the cursor. Does nothing for
     * {@code null} / empty text. The text uses the palette default (bright gold).
     */
    public static void render(GuiGraphics g, Font font, String text,
                              int mouseX, int mouseY, int screenW, int screenH) {
        render(g, font, text, mouseX, mouseY, screenW, screenH, PanelTheme.palette().accentBright);
    }

    /**
     * Same as {@link #render}, but the caller chooses the text color
     * (ARGB, e.g. {@code 0xFFFF5555} for a red warning-style hint).
     */
    public static void render(GuiGraphics g, Font font, String text,
                              int mouseX, int mouseY, int screenW, int screenH, int textColor) {
        if (text == null || text.isEmpty()) {
            return;
        }
        List<String> lines = TextFit.wrap(font, text, MAX_CONTENT_W);
        if (lines.isEmpty()) {
            return;
        }
        int contentW = 0;
        for (String line : lines) {
            contentW = Math.max(contentW, font.width(line));
        }
        int w = contentW + PAD * 2;
        int h = lines.size() * LINE_H + PAD * 2 - 2;

        // Prefer the right side of the cursor; flip left when it would overflow.
        int x = mouseX + GAP_X;
        if (x + w > screenW - EDGE) {
            x = mouseX - GAP_X - w;
        }
        x = Math.max(EDGE, Math.min(x, screenW - EDGE - w));

        // Prefer above the cursor; drop below when it does not fit.
        int y = mouseY - h - GAP_Y;
        if (y < EDGE) {
            y = mouseY + GAP_Y;
        }
        y = Math.max(EDGE, Math.min(y, screenH - EDGE - h));

        PanelPalette p = PanelTheme.palette();
        // Opaque fill is required: a translucent box becomes unreadable over
        // list rows and the read-only overlay.
        g.fill(x, y, x + w, y + h, p.bgPanel);
        g.renderOutline(x, y, w, h, p.accentDim);
        int ty = y + PAD;
        for (String line : lines) {
            g.drawString(font, line, x + PAD, ty, textColor, false);
            ty += LINE_H;
        }
    }
}
