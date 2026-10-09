package com.avalon.base.gui.util;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

/**
 * Auto-layout helpers that prevent text from overflowing panels.
 *
 * <p>Business mods previously used hand-fixed panel widths and single-point
 * {@code ellipsize}; individual rows such as "item name + price" overflowed,
 * especially in English. These helpers compute widths/positions from content
 * so rendering stays inside the panel.</p>
 */
public final class AutoLayout {

    /** Absolute minimum panel width used as a hard lower bound. */
    public static final int MIN_PANEL_W = 180;

    private AutoLayout() {
    }

    /**
     * Narrowest panel width (in pixels) that can host every {@code row} when
     * {@code padX} pixels of horizontal padding are reserved on each side.
     */
    public static int minPanelWidth(Font font, Iterable<String> rows, int padX) {
        int max = 0;
        for (String row : rows) {
            max = Math.max(max, font.width(row));
        }
        return max + padX * 2;
    }

    /**
     * Clamp a desired panel width into the valid range
     * {@code [MIN_PANEL_W, screenW - 10]}.
     */
    public static int constrainPanelW(int desiredW, int screenW) {
        return Math.max(MIN_PANEL_W, Math.min(desiredW, screenW - 10));
    }

    /**
     * Draw {@code s} right-aligned so its right edge is at {@code xRight}
     * while never crossing {@code minX}. If there is not enough room the text
     * is truncated with {@link TextFit#fit}.
     */
    public static void drawRight(GuiGraphics g, Font font, String s,
                                 int xRight, int minX, int y, int color) {
        int avail = xRight - minX;
        String t = avail <= 0 ? "" : TextFit.fit(font, s, avail);
        g.drawString(font, t, xRight - font.width(t), y, color, false);
    }

    /**
     * A fitted left/right pair (e.g. item name + amount on one row).
     *
     * @param left  the flexible left text (usually a name, may be truncated)
     * @param right the right text (usually a price/amount, preferred intact)
     */
    public record FitPair(String left, String right) {
    }

    /**
     * Distribute {@code totalW} pixels between a left label and a right value
     * separated by {@code gap} pixels. The right value is preserved as long as
     * possible; the left label absorbs truncation. When the left gets too small
     * (&lt; 24px) the row is split roughly 6:4 and both sides may be truncated.
     */
    public static FitPair fitPair(Font font, String left, String right, int totalW, int gap) {
        int rightW = font.width(right);
        int leftAvail = totalW - gap - rightW;
        if (leftAvail >= 24) {
            return new FitPair(TextFit.fit(font, left, leftAvail), right);
        }
        int allocL = Math.max(0, (int) (totalW * 0.6f) - gap / 2);
        int allocR = totalW - allocL - gap;
        if (allocR < 0) {
            allocR = 0;
        }
        return new FitPair(TextFit.fit(font, left, allocL), TextFit.fit(font, right, allocR));
    }
}