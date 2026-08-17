package com.avalon.base.gui.theme;

import java.util.Locale;

/**
 * 内置主题预设（配色方案目录）。
 *
 * <p>每个枚举项对应一套完整的 {@link Palette}，并带有一个语言键 {@code displayKey}
 * 用于在 UI 中显示可翻译的名称。预设顺序即 {@link com.avalon.base.gui.Themes} /
 * {@link ThemePresetPicker} 中的切换顺序。
 *
 * <p>扩展方式：新增一个枚举项（提供 displayKey 与 Palette）即可，渲染逻辑无需改动。
 */
public enum ThemePreset {

    /**
     * 末影紫（暗色，<b>默认</b>）。原始现代主题配色，紫黑渐变 + 紫色强调。
     */
    ENDER_PURPLE("theme.avalonbase.preset.ender_purple", Palette.builder()
            .panelTop(0xFF1A1428).panelBottom(0xFF0D0A1A).panelShadow(0x44000000)
            .border(0xFF5A4A7A).cardBorder(0xFF3A2D52).cardFill(0xFF16111E)
            .btnPrimary(0xFF4A2D7A).btnPrimaryHover(0xFF6A4D9A)
            .btnNeutral(0xFF2E2A3E).btnNeutralHover(0xFF4E4A5E)
            .btnDisabled(0xFF2A2538).btnText(0xFFFFFFFF)
            .toggleBox(0xFF2E2A3E).toggleFill(0xFFB47AE8).toggleBorder(0xFF5A4A7A)
            .scrollTrack(0xFF16111E).scrollThumb(0xFF5A4A7A).scrollThumbHighlight(0xFF7A6A9A)
            .accent(0xFFB47AE8)
            .title(0xFFEDE6FA).label(0xFFB47AE8).text(0xFFC8C2D8)
            .disabled(0xFF5A5568).ok(0xFF7FE0A0).warn(0xFFE05555)
            .build()),

    /**
     * 海洋蓝（暗色）。蓝黑渐变 + 天蓝强调，冷静克制的深海质感。
     */
    OCEAN_BLUE("theme.avalonbase.preset.ocean_blue", Palette.builder()
            .panelTop(0xFF1A2430).panelBottom(0xFF0B121A).panelShadow(0x44000000)
            .border(0xFF3D5A75).cardBorder(0xFF2A4055).cardFill(0xFF131C26)
            .btnPrimary(0xFF2D6DA0).btnPrimaryHover(0xFF4D8DC0)
            .btnNeutral(0xFF22303E).btnNeutralHover(0xFF3E4C5E)
            .btnDisabled(0xFF1E2833).btnText(0xFFFFFFFF)
            .toggleBox(0xFF22303E).toggleFill(0xFF4DA8E0).toggleBorder(0xFF3D5A75)
            .scrollTrack(0xFF131C26).scrollThumb(0xFF3D5A75).scrollThumbHighlight(0xFF5D7A95)
            .accent(0xFF4DA8E0)
            .title(0xFFE6F0FA).label(0xFF4DA8E0).text(0xFFC2CCD8)
            .disabled(0xFF55606E).ok(0xFF7FE0A0).warn(0xFFE05555)
            .build()),

    /**
     * 翡翠绿（暗色）。绿黑渐变 + 翠绿强调，森林般的沉稳质感。
     */
    EMERALD_GREEN("theme.avalonbase.preset.emerald_green", Palette.builder()
            .panelTop(0xFF12201A).panelBottom(0xFF09120E).panelShadow(0x44000000)
            .border(0xFF2D5A44).cardBorder(0xFF20453A).cardFill(0xFF0F1A15)
            .btnPrimary(0xFF2D7A55).btnPrimaryHover(0xFF4D9A75)
            .btnNeutral(0xFF1E2E26).btnNeutralHover(0xFF3A4E42)
            .btnDisabled(0xFF18241E).btnText(0xFFFFFFFF)
            .toggleBox(0xFF1E2E26).toggleFill(0xFF3ED08A).toggleBorder(0xFF2D5A44)
            .scrollTrack(0xFF0F1A15).scrollThumb(0xFF2D5A44).scrollThumbHighlight(0xFF4D7A64)
            .accent(0xFF3ED08A)
            .title(0xFFE4F5EC).label(0xFF3ED08A).text(0xFFC0D8CC)
            .disabled(0xFF55655E).ok(0xFF7FE0A0).warn(0xFFE05555)
            .build()),

    /**
     * 熔岩红（暗色）。红黑渐变 + 橙红强调，炽热的熔岩质感。
     */
    EMBER_RED("theme.avalonbase.preset.ember_red", Palette.builder()
            .panelTop(0xFF2A1210).panelBottom(0xFF1A0A08).panelShadow(0x44000000)
            .border(0xFF7A3A30).cardBorder(0xFF522A24).cardFill(0xFF1E0E0C)
            .btnPrimary(0xFF7A2D22).btnPrimaryHover(0xFF9A4D42)
            .btnNeutral(0xFF30201E).btnNeutralHover(0xFF50403E)
            .btnDisabled(0xFF261815).btnText(0xFFFFFFFF)
            .toggleBox(0xFF30201E).toggleFill(0xFFE06050).toggleBorder(0xFF7A3A30)
            .scrollTrack(0xFF1E0E0C).scrollThumb(0xFF7A3A30).scrollThumbHighlight(0xFF9A5A50)
            .accent(0xFFE06050)
            .title(0xFFFAE6E0).label(0xFFE06050).text(0xFFD8C2BE)
            .disabled(0xFF655550).ok(0xFF7FE0A0).warn(0xFFE05555)
            .build()),

    /**
     * 米白（浅色）。浅色高对比方案，米白背景 + 深色文字，适合明亮场景。
     */
    PORCELAIN("theme.avalonbase.preset.porcelain", Palette.builder()
            .panelTop(0xFFF7F4EE).panelBottom(0xFFEAE6DE).panelShadow(0x22000000)
            .border(0xFFB8AFA0).cardBorder(0xFFD0C8BA).cardFill(0xFFFFFFFF)
            .btnPrimary(0xFF5A4A7A).btnPrimaryHover(0xFF7A6A9A)
            .btnNeutral(0xFFE6E1D8).btnNeutralHover(0xFFD6D0C6)
            .btnDisabled(0xFFE0DCD4).btnText(0xFFFFFFFF)
            .toggleBox(0xFFE6E1D8).toggleFill(0xFF5A4A7A).toggleBorder(0xFFB8AFA0)
            .scrollTrack(0xFFEAE6DE).scrollThumb(0xFFB8AFA0).scrollThumbHighlight(0xFF9A9080)
            .accent(0xFF5A4A7A)
            .title(0xFF2A2A2E).label(0xFF5A4A7A).text(0xFF4A4A50)
            .disabled(0xFFA0A0A6).ok(0xFF2D8A55).warn(0xFFC04040)
            .build());

    private final String displayKey;
    private final Palette palette;

    ThemePreset(String displayKey, Palette palette) {
        this.displayKey = displayKey;
        this.palette = palette;
    }

    /** 用于 UI 显示的语言键。 */
    public String displayKey() {
        return displayKey;
    }

    /** 该预设对应的配色盘。 */
    public Palette palette() {
        return palette;
    }

    /**
     * 按名称安全解析预设（大小写不敏感）。无法匹配时回退到 {@link #ENDER_PURPLE}。
     *
     * @param name 枚举名（如 {@code "ocean_blue"} 或 {@code "OCEAN_BLUE"}）
     */
    public static ThemePreset byName(String name) {
        if (name == null) return ENDER_PURPLE;
        for (ThemePreset p : values()) {
            if (p.name().equalsIgnoreCase(name)) return p;
        }
        // 兼容小写连字符写法（如 "ocean-blue"）
        String normalized = name.trim().toLowerCase(Locale.ROOT).replace('-', '_');
        for (ThemePreset p : values()) {
            if (p.name().toLowerCase(Locale.ROOT).equals(normalized)) return p;
        }
        return ENDER_PURPLE;
    }

    /** 取下一个预设（循环）。用于切换控件。 */
    public ThemePreset next() {
        ThemePreset[] all = values();
        return all[(ordinal() + 1) % all.length];
    }

    /** 取上一个预设（循环）。用于切换控件。 */
    public ThemePreset previous() {
        ThemePreset[] all = values();
        return all[(ordinal() + all.length - 1) % all.length];
    }
}
