package com.avalon.base.gui.panel;

import com.avalon.base.gui.theme.Palette;

/**
 * Immutable palette for the dark gold "panel" UI type.
 *
 * <p>This is a separate palette from {@link Palette}: the panel UI keeps its
 * own full set of semantic slots (GOLD three-tier accent, neutral/danger
 * buttons, header gradient, toast backgrounds) without modifying the existing
 * modern-theme palette. {@link #from(Palette)} provides a partial bridge so a
 * business screen can loosely tint the panel UI from an existing theme.</p>
 *
 * <p>All values are {@code 0xAARRGGBB} ints as used by the rest of AvalonBase.</p>
 */
public final class PanelPalette {

    // ---- panel / background ----
    public final int bgOuter;
    public final int bgPanel;
    public final int bgPanelAlt;
    public final int bgRow;
    public final int bgRowHover;
    public final int bgRowSelected;
    public final int bgInput;

    // ---- accent (GOLD three-tier) ----
    public final int accent;
    public final int accentDim;
    public final int accentBright;

    // ---- status colors ----
    public final int danger;
    public final int dangerDim;
    public final int ok;

    // ---- text ----
    public final int text;
    public final int textDim;
    public final int textMuted;

    // ---- frame ----
    public final int border;
    public final int shadow;

    // ---- header gradient ----
    public final int headerTop;
    public final int headerBottom;

    // ---- buttons (absorbed from the source ThemedButton hardcodes) ----
    public final int btnPrimary;
    public final int btnPrimaryHover;
    public final int btnPrimaryBorder;
    public final int btnDanger;
    public final int btnDangerHover;
    public final int btnDisabled;
    public final int btnDisabledBorder;

    // ---- toast ----
    public final int toastErrorBg;
    public final int toastOkBg;

    private PanelPalette(Builder b) {
        this.bgOuter = b.bgOuter;
        this.bgPanel = b.bgPanel;
        this.bgPanelAlt = b.bgPanelAlt;
        this.bgRow = b.bgRow;
        this.bgRowHover = b.bgRowHover;
        this.bgRowSelected = b.bgRowSelected;
        this.bgInput = b.bgInput;
        this.accent = b.accent;
        this.accentDim = b.accentDim;
        this.accentBright = b.accentBright;
        this.danger = b.danger;
        this.dangerDim = b.dangerDim;
        this.ok = b.ok;
        this.text = b.text;
        this.textDim = b.textDim;
        this.textMuted = b.textMuted;
        this.border = b.border;
        this.shadow = b.shadow;
        this.headerTop = b.headerTop;
        this.headerBottom = b.headerBottom;
        this.btnPrimary = b.btnPrimary;
        this.btnPrimaryHover = b.btnPrimaryHover;
        this.btnPrimaryBorder = b.btnPrimaryBorder;
        this.btnDanger = b.btnDanger;
        this.btnDangerHover = b.btnDangerHover;
        this.btnDisabled = b.btnDisabled;
        this.btnDisabledBorder = b.btnDisabledBorder;
        this.toastErrorBg = b.toastErrorBg;
        this.toastOkBg = b.toastOkBg;
    }

    /**
     * The default dark gold palette reproducing the source UI's exact colors.
     */
    public static final PanelPalette GILDED_DARK = builder()
            .bgOuter(0xFF0B0D12)
            .bgPanel(0xFF161A22)
            .bgPanelAlt(0xFF1C212B)
            .bgRow(0xFF20262F)
            .bgRowHover(0xFF2B3340)
            .bgRowSelected(0xFF3A3220)
            .bgInput(0xFF0E1015)
            .accent(0xFFD8B04C)
            .accentDim(0xFF8A6E2B)
            .accentBright(0xFFF5DF97)
            .danger(0xFFE0463C)
            .dangerDim(0xFF7A2A25)
            .ok(0xFF6FD66F)
            .text(0xFFEDEDED)
            .textDim(0xFF9AA3B2)
            .textMuted(0xFF5F6673)
            .border(0xFF2E3440)
            .shadow(0x66000000)
            .headerTop(0xFF2A2415)
            .headerBottom(0xFF161A22)
            .btnPrimary(0xFF6B5019)
            .btnPrimaryHover(0xFF8C6A22)
            .btnPrimaryBorder(0xFFD8B04C)
            .btnDanger(0xFF7A2A25)
            .btnDangerHover(0xFF8F2F28)
            .btnDisabled(0xFF3A3A3A)
            .btnDisabledBorder(0xFF505050)
            .toastErrorBg(0xE06A1F1B)
            .toastOkBg(0xE01F4A26)
            .build();

    /**
     * Partial bridge from an existing {@link Palette}. Only slots with a clear
     * semantic match are mapped; every other slot keeps the {@link #GILDED_DARK}
     * default. The accent/border/text slots drive the visible tint, so switching
     * to another theme still renders recognizably.
     */
    public static PanelPalette from(Palette p) {
        return builder()
                .bgPanel(p.panelBottom)
                .bgInput(p.cardFill)
                .border(p.border)
                .accent(p.accent)
                .accentBright(p.title)
                .text(p.text)
                .textDim(p.label)
                .textMuted(p.disabled)
                .ok(p.ok)
                .danger(p.warn)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        int bgOuter = 0xFF0B0D12;
        int bgPanel = 0xFF161A22;
        int bgPanelAlt = 0xFF1C212B;
        int bgRow = 0xFF20262F;
        int bgRowHover = 0xFF2B3340;
        int bgRowSelected = 0xFF3A3220;
        int bgInput = 0xFF0E1015;
        int accent = 0xFFD8B04C;
        int accentDim = 0xFF8A6E2B;
        int accentBright = 0xFFF5DF97;
        int danger = 0xFFE0463C;
        int dangerDim = 0xFF7A2A25;
        int ok = 0xFF6FD66F;
        int text = 0xFFEDEDED;
        int textDim = 0xFF9AA3B2;
        int textMuted = 0xFF5F6673;
        int border = 0xFF2E3440;
        int shadow = 0x66000000;
        int headerTop = 0xFF2A2415;
        int headerBottom = 0xFF161A22;
        int btnPrimary = 0xFF6B5019;
        int btnPrimaryHover = 0xFF8C6A22;
        int btnPrimaryBorder = 0xFFD8B04C;
        int btnDanger = 0xFF7A2A25;
        int btnDangerHover = 0xFF8F2F28;
        int btnDisabled = 0xFF3A3A3A;
        int btnDisabledBorder = 0xFF505050;
        int toastErrorBg = 0xE06A1F1B;
        int toastOkBg = 0xE01F4A26;

        Builder() {
        }

        public Builder bgOuter(int v) { this.bgOuter = v; return this; }
        public Builder bgPanel(int v) { this.bgPanel = v; return this; }
        public Builder bgPanelAlt(int v) { this.bgPanelAlt = v; return this; }
        public Builder bgRow(int v) { this.bgRow = v; return this; }
        public Builder bgRowHover(int v) { this.bgRowHover = v; return this; }
        public Builder bgRowSelected(int v) { this.bgRowSelected = v; return this; }
        public Builder bgInput(int v) { this.bgInput = v; return this; }
        public Builder accent(int v) { this.accent = v; return this; }
        public Builder accentDim(int v) { this.accentDim = v; return this; }
        public Builder accentBright(int v) { this.accentBright = v; return this; }
        public Builder danger(int v) { this.danger = v; return this; }
        public Builder dangerDim(int v) { this.dangerDim = v; return this; }
        public Builder ok(int v) { this.ok = v; return this; }
        public Builder text(int v) { this.text = v; return this; }
        public Builder textDim(int v) { this.textDim = v; return this; }
        public Builder textMuted(int v) { this.textMuted = v; return this; }
        public Builder border(int v) { this.border = v; return this; }
        public Builder shadow(int v) { this.shadow = v; return this; }
        public Builder headerTop(int v) { this.headerTop = v; return this; }
        public Builder headerBottom(int v) { this.headerBottom = v; return this; }
        public Builder btnPrimary(int v) { this.btnPrimary = v; return this; }
        public Builder btnPrimaryHover(int v) { this.btnPrimaryHover = v; return this; }
        public Builder btnPrimaryBorder(int v) { this.btnPrimaryBorder = v; return this; }
        public Builder btnDanger(int v) { this.btnDanger = v; return this; }
        public Builder btnDangerHover(int v) { this.btnDangerHover = v; return this; }
        public Builder btnDisabled(int v) { this.btnDisabled = v; return this; }
        public Builder btnDisabledBorder(int v) { this.btnDisabledBorder = v; return this; }
        public Builder toastErrorBg(int v) { this.toastErrorBg = v; return this; }
        public Builder toastOkBg(int v) { this.toastOkBg = v; return this; }

        public PanelPalette build() {
            return new PanelPalette(this);
        }
    }
}