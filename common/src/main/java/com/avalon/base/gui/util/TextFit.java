package com.avalon.base.gui.util;

import net.minecraft.client.gui.Font;

import java.util.ArrayList;
import java.util.List;

/**
 * Text measuring and fitting helpers used to keep text inside panels,
 * especially for long English strings that would otherwise overflow.
 *
 * <p>All methods are stateless. They operate on a {@link Font} so that width
 * measurement is exact for the current locale (latin vs full-width glyphs).</p>
 */
public final class TextFit {

    /** Ellipsis suffix used by {@link #fit}. */
    public static final String ELLIPSIS = "...";

    private TextFit() {
    }

    /**
     * Exact width of {@code text} in pixels.
     */
    public static int width(Font font, String text) {
        return font.width(text);
    }

    /**
     * Fit {@code text} into at most {@code maxW} pixels, appending "{@code ...}"
     * when truncated.
     */
    public static String fit(Font font, String text, int maxW) {
        if (text == null || font.width(text) <= maxW) {
            return text == null ? "" : text;
        }
        int target = maxW - font.width(ELLIPSIS);
        if (target <= 0) {
            return ELLIPSIS;
        }
        return font.plainSubstrByWidth(text, target) + ELLIPSIS;
    }

    /**
     * Fit {@code text} into at most {@code maxW} pixels without an ellipsis.
     */
    public static String plainFit(Font font, String text, int maxW) {
        if (text == null) {
            return "";
        }
        if (maxW <= 0) {
            return "";
        }
        if (font.width(text) <= maxW) {
            return text;
        }
        return font.plainSubstrByWidth(text, maxW);
    }

    /**
     * Greedily wrap {@code text} into lines each at most {@code maxW} pixels wide.
     *
     * <p>Uses {@link Font#plainSubstrByWidth(String, int)} for measuring so that
     * segment boundaries are exact. Preferred over {@code StringSplitter} because
     * it has zero external API dependency.</p>
     */
    public static List<String> wrap(Font font, String text, int maxW) {
        List<String> lines = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return lines;
        }
        if (maxW <= 0) {
            lines.add(text);
            return lines;
        }
        String remaining = text;
        while (!remaining.isEmpty()) {
            if (font.width(remaining) <= maxW) {
                lines.add(remaining);
                break;
            }
            String seg = font.plainSubstrByWidth(remaining, maxW);
            if (seg.isEmpty()) {
                // Defensive: avoid infinite loop if a single glyph exceeds maxW.
                lines.add(remaining);
                break;
            }
            lines.add(seg);
            remaining = remaining.substring(seg.length());
        }
        return lines;
    }
}