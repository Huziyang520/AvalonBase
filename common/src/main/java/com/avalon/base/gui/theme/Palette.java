package com.avalon.base.gui.theme;

/**
 * 配色盘（不可变数据对象），把现代主题的所有颜色抽象为语义化色槽。
 *
 * <p>{@link ModernTheme} 不再把颜色写死在渲染方法里，而是持有一个 {@link Palette}，
 * 渲染时从对应色槽取色。于是：
 * <ul>
 *   <li><b>加新配色</b> = 新定义一个 {@link Palette}（或新增 {@link ThemePreset} 枚举项）；</li>
 *   <li><b>自定义配色</b> = 用 {@link #builder()} 构造一个自己的 {@link Palette}；</li>
 *   <li>渲染逻辑完全不用动。</li>
 * </ul>
 *
 * <p>所有色值均以 {@code 0xAARRGGBB} 的 {@code int} 表达（A 为透明度，RR/GG/BB 为红绿蓝）。
 * 本类为不可变对象，构造后各字段不再改变，可安全地被多个主题共享。
 */
public final class Palette {

    // ---- 面板 / 背景 ----
    /** 面板渐变顶部颜色。 */
    public final int panelTop;
    /** 面板渐变底部颜色。 */
    public final int panelBottom;
    /** 面板外阴影颜色（含透明度）。 */
    public final int panelShadow;

    // ---- 边框 / 卡片 ----
    /** 面板外框线颜色。 */
    public final int border;
    /** 卡片边框颜色。 */
    public final int cardBorder;
    /** 卡片填充颜色。 */
    public final int cardFill;

    // ---- 按钮 ----
    /** 主按钮（PRIMARY）常态颜色。 */
    public final int btnPrimary;
    /** 主按钮（PRIMARY）悬停颜色。 */
    public final int btnPrimaryHover;
    /** 中性按钮（NEUTRAL）常态颜色。 */
    public final int btnNeutral;
    /** 中性按钮（NEUTRAL）悬停颜色。 */
    public final int btnNeutralHover;
    /** 禁用按钮颜色。 */
    public final int btnDisabled;
    /** 按钮文字颜色（亮色主题下应为深色，避免白字不可见）。 */
    public final int btnText;

    // ---- 开关 / 勾选 ----
    /** 勾选框底盒颜色。 */
    public final int toggleBox;
    /** 勾选框选中填充颜色。 */
    public final int toggleFill;
    /** 勾选框边框颜色。 */
    public final int toggleBorder;

    // ---- 滚动条 ----
    /** 滚动条轨道颜色。 */
    public final int scrollTrack;
    /** 滚动条滑块颜色。 */
    public final int scrollThumb;
    /** 滚动条滑块高光颜色。 */
    public final int scrollThumbHighlight;

    // ---- 强调色 / 文本 ----
    /** 强调色（图标、勾选填充、标签文字等统一使用）。 */
    public final int accent;
    /** 标题文字颜色。 */
    public final int title;
    /** 标签文字颜色。 */
    public final int label;
    /** 正文文字颜色。 */
    public final int text;
    /** 禁用文字颜色。 */
    public final int disabled;
    /** 成功 / 正常状态文字颜色。 */
    public final int ok;
    /** 警告 / 错误文字颜色。 */
    public final int warn;

    private Palette(Builder b) {
        this.panelTop = b.panelTop;
        this.panelBottom = b.panelBottom;
        this.panelShadow = b.panelShadow;
        this.border = b.border;
        this.cardBorder = b.cardBorder;
        this.cardFill = b.cardFill;
        this.btnPrimary = b.btnPrimary;
        this.btnPrimaryHover = b.btnPrimaryHover;
        this.btnNeutral = b.btnNeutral;
        this.btnNeutralHover = b.btnNeutralHover;
        this.btnDisabled = b.btnDisabled;
        this.btnText = b.btnText;
        this.toggleBox = b.toggleBox;
        this.toggleFill = b.toggleFill;
        this.toggleBorder = b.toggleBorder;
        this.scrollTrack = b.scrollTrack;
        this.scrollThumb = b.scrollThumb;
        this.scrollThumbHighlight = b.scrollThumbHighlight;
        this.accent = b.accent;
        this.title = b.title;
        this.label = b.label;
        this.text = b.text;
        this.disabled = b.disabled;
        this.ok = b.ok;
        this.warn = b.warn;
    }

    /** 由预设枚举取配色盘（便捷方法）。 */
    public static Palette from(ThemePreset preset) {
        return preset.palette();
    }

    /** 开启一个配色盘构造器，用于自定义配色。 */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 配色盘构造器。所有色槽均有默认值（末影紫），
     * 自定义时只需覆写想要改变的色槽即可。
     */
    public static final class Builder {
        int panelTop = 0xFF1A1428;
        int panelBottom = 0xFF0D0A1A;
        int panelShadow = 0x44000000;
        int border = 0xFF5A4A7A;
        int cardBorder = 0xFF3A2D52;
        int cardFill = 0xFF16111E;
        int btnPrimary = 0xFF4A2D7A;
        int btnPrimaryHover = 0xFF6A4D9A;
        int btnNeutral = 0xFF2E2A3E;
        int btnNeutralHover = 0xFF4E4A5E;
        int btnDisabled = 0xFF2A2538;
        int btnText = 0xFFFFFFFF;
        int toggleBox = 0xFF2E2A3E;
        int toggleFill = 0xFFB47AE8;
        int toggleBorder = 0xFF5A4A7A;
        int scrollTrack = 0xFF16111E;
        int scrollThumb = 0xFF5A4A7A;
        int scrollThumbHighlight = 0xFF7A6A9A;
        int accent = 0xFFB47AE8;
        int title = 0xFFEDE6FA;
        int label = 0xFFB47AE8;
        int text = 0xFFC8C2D8;
        int disabled = 0xFF5A5568;
        int ok = 0xFF7FE0A0;
        int warn = 0xFFE05555;

        Builder() {
        }

        public Builder panelTop(int v) { this.panelTop = v; return this; }
        public Builder panelBottom(int v) { this.panelBottom = v; return this; }
        public Builder panelShadow(int v) { this.panelShadow = v; return this; }
        public Builder border(int v) { this.border = v; return this; }
        public Builder cardBorder(int v) { this.cardBorder = v; return this; }
        public Builder cardFill(int v) { this.cardFill = v; return this; }
        public Builder btnPrimary(int v) { this.btnPrimary = v; return this; }
        public Builder btnPrimaryHover(int v) { this.btnPrimaryHover = v; return this; }
        public Builder btnNeutral(int v) { this.btnNeutral = v; return this; }
        public Builder btnNeutralHover(int v) { this.btnNeutralHover = v; return this; }
        public Builder btnDisabled(int v) { this.btnDisabled = v; return this; }
        public Builder btnText(int v) { this.btnText = v; return this; }
        public Builder toggleBox(int v) { this.toggleBox = v; return this; }
        public Builder toggleFill(int v) { this.toggleFill = v; return this; }
        public Builder toggleBorder(int v) { this.toggleBorder = v; return this; }
        public Builder scrollTrack(int v) { this.scrollTrack = v; return this; }
        public Builder scrollThumb(int v) { this.scrollThumb = v; return this; }
        public Builder scrollThumbHighlight(int v) { this.scrollThumbHighlight = v; return this; }
        public Builder accent(int v) { this.accent = v; return this; }
        public Builder title(int v) { this.title = v; return this; }
        public Builder label(int v) { this.label = v; return this; }
        public Builder text(int v) { this.text = v; return this; }
        public Builder disabled(int v) { this.disabled = v; return this; }
        public Builder ok(int v) { this.ok = v; return this; }
        public Builder warn(int v) { this.warn = v; return this; }

        public Palette build() {
            return new Palette(this);
        }
    }
}
