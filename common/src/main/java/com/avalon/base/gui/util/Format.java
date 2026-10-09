package com.avalon.base.gui.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.ItemStack;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Formatting helpers for numbers, durations and item rarity colors.
 *
 * <p>Stateless (the {@link NumberFormat} instance is used from the client
 * render thread only) and locale-stable via {@link Locale#ROOT}.</p>
 */
public final class Format {

    /** Default text color used when no rarity color can be derived. */
    public static final int DEFAULT_TEXT = 0xFFEDEDED;

    private static final NumberFormat NUMBER = NumberFormat.getIntegerInstance(Locale.ROOT);

    private Format() {
    }

    /**
     * Integer with thousands separators (e.g. {@code 123,456,789}).
     */
    public static String number(long value) {
        return NUMBER.format(value);
    }

    /**
     * {@link #number(long)} as a literal {@link Component}.
     */
    public static Component numberText(long value) {
        return Component.literal(number(value));
    }

    /**
     * Compact human-readable duration from milliseconds:
     * "1d 2h", "3h 5m", "12m", or "0m" for non-positive values.
     */
    public static String duration(long millis) {
        if (millis <= 0) {
            return "0m";
        }
        long minutes = millis / 60_000L;
        long hours = minutes / 60L;
        long days = hours / 24L;
        if (days > 0) {
            return days + "d " + (hours % 24) + "h";
        }
        if (hours > 0) {
            return hours + "h " + (minutes % 60) + "m";
        }
        return Math.max(1, minutes) + "m";
    }

    /**
     * Rarity-based item color as an ARGB int, falling back to
     * {@link #DEFAULT_TEXT} for null/plain-white rarities.
     */
    public static int itemColor(ItemStack stack) {
        TextColor color = TextColor.fromLegacyFormat(stack.getRarity().color());
        if (color == null) {
            return DEFAULT_TEXT;
        }
        int rgb = color.getValue() & 0xFFFFFF;
        if (rgb == 0xFFFFFF) {
            return DEFAULT_TEXT;
        }
        return 0xFF000000 | rgb;
    }
}