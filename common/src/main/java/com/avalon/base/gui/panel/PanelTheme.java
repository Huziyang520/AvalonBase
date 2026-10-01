package com.avalon.base.gui.panel;

import com.avalon.base.gui.util.TextFit;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

/**
 * Static drawing facade for the dark-gold "panel" UI type.
 *
 * This mirrors {@code Theme} from the source code being integrated into
 * AvalonBase, but takes its colors from {@link #palette() the current panel
 * palette} instead of hard-coded constants. Every drawing helper
 * here is a direct reproduction of the original so visuals stay pixel-identical
 * when the palette equals {@link PanelPalette#GILDED_DARK}.</p>
 */
public final class PanelTheme {

    private static final int HEADER_H = 16;

    private static PanelPalette palette = PanelPalette.GILDED_DARK;

    private PanelTheme() {
    }

    /** The palette currently used by all {@link PanelTheme} draw calls. */
    public static PanelPalette palette() {
        return palette;
    }

    /** Switch the active palette (e.g. to {@link PanelPalette#from(Palette)}). */
    public static void setPalette(PanelPalette p) {
        palette = p == null ? PanelPalette.GILDED_DARK : p;
    }

    /** Outer panel frame: shadow, bg layers, gold border and top highlight. */
    public static void panel(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        PanelPalette p = palette;
        g.fill(x + 2, y + 2, x + w + 2, y + h + 2, p.shadow);
        g.fill(x, y, x + w, y + h, p.bgOuter);
        g.fill(x + 1, y + 1, x + w - 1, y + h - 1, p.bgPanel);
        g.outline(x, y, w, h, p.accentDim);
        g.horizontalLine(x + 1, x + w - 2, y + 1, p.accent);
    }

    /** Inner sub-panel: alt background with a plain border. */
    public static void subPanel(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        PanelPalette p = palette;
        g.fill(x, y, x + w, y + h, p.bgPanelAlt);
        g.outline(x, y, w, h, p.border);
    }

    /** Input/inset well: input background with a plain border. */
    public static void inset(GuiGraphicsExtractor g, int x, int y, int w, int h) {
        PanelPalette p = palette;
        g.fill(x, y, x + w, y + h, p.bgInput);
        g.outline(x, y, w, h, p.border);
    }

    /** Panel header bar with a gradient and an (auto-fitted) centered title. */
    public static void header(GuiGraphicsExtractor g, Font font, Component title, int x, int y, int w) {
        PanelPalette p = palette;
        g.fillGradient(x, y, x + w, y + HEADER_H, p.headerTop, p.headerBottom);
        g.horizontalLine(x, x + w - 1, y + HEADER_H, p.accentDim);
        g.centeredText(font, Component.literal(TextFit.fit(font, title.getString(), w - 8)),
                x + w / 2, y + 4, p.accentBright);
    }

    /** Horizontal separator line. */
    public static void divider(GuiGraphicsExtractor g, int x1, int x2, int y) {
        g.horizontalLine(x1, x2, y, palette.border);
    }
}