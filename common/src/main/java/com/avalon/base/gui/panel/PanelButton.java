package com.avalon.base.gui.panel;

import com.avalon.base.gui.util.TextFit;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

/**
 * Self-drawn button for the "panel" UI type, a faithful port of the source's
 * {@code ThemedButton} with {@link Style} states.
 *
 * <p>All colors are read from {@link PanelTheme#palette()} (the dark-gold
 * button colors that were hard-coded in the original are now palette slots),
 * and long labels are truncated with {@link TextFit#fit} instead of the
 * original ellipsize helper.</p>
 */
public class PanelButton extends AbstractWidget {
    public enum Style { NORMAL, PRIMARY, DANGER, TAB }

    private final Runnable onPress;
    private Style style;
    private boolean selected;

    public PanelButton(int x, int y, int w, int h, Component label, Style style, Runnable onPress) {
        super(x, y, w, h, label);
        this.style = style;
        this.onPress = onPress;
    }

    public PanelButton withTooltip(Component text) {
        setTooltip(Tooltip.create(text));
        return this;
    }

    public void setSelected(boolean selected) { this.selected = selected; }
    public boolean isSelected() { return selected; }
    public void setStyle(Style style) { this.style = style; }

    @Override
    protected void renderWidget(GuiGraphics g, int mouseX, int mouseY, float delta) {
        int x = getX(), y = getY(), w = getWidth(), h = getHeight();
        boolean hover = isHoveredOrFocused() && active;
        PanelPalette p = PanelTheme.palette();
        int fill, border, text;
        switch (style) {
            case PRIMARY -> {
                fill = !active ? p.btnDisabled : hover ? p.btnPrimaryHover : p.btnPrimary;
                border = !active ? p.btnDisabledBorder : p.accent;
                text = !active ? p.textMuted : p.accentBright;
            }
            case DANGER -> {
                fill = !active ? p.btnDisabled : hover ? p.btnDangerHover : p.dangerDim;
                border = !active ? p.btnDisabledBorder : p.danger;
                text = !active ? p.textMuted : p.text;
            }
            case TAB -> {
                fill = selected ? p.bgRowSelected : hover ? p.bgRowHover : p.bgRow;
                border = selected ? p.accent : p.border;
                text = selected ? p.accentBright : hover ? p.text : p.textDim;
            }
            default -> {
                fill = !active ? p.btnDisabled : hover ? p.bgRowHover : p.bgRow;
                border = !active ? p.btnDisabledBorder : hover ? p.accentDim : p.border;
                text = !active ? p.textMuted : p.text;
            }
        }
        g.fill(x, y, x + w, y + h, fill);
        g.renderOutline(x, y, w, h, border);
        if (style == Style.TAB && selected) {
            g.fill(x, y + 1, x + 2, y + h - 1, p.accent);
        }
        var font = Minecraft.getInstance().font;
        String label = TextFit.fit(font, getMessage().getString(), w - 8);
        if (style == Style.TAB) {
            g.drawString(font, label, x + 7, y + (h - 8) / 2, text, false);
        } else {
            g.drawCenteredString(font, Component.literal(label), x + w / 2, y + (h - 8) / 2, text);
        }
    }

    @Override
    public void onClick(MouseButtonEvent event, boolean doubleClick) {
        onPress.run();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (active && visible && event.isConfirmation()) {
            playDownSound(Minecraft.getInstance().getSoundManager());
            onPress.run();
            return true;
        }
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        defaultButtonNarrationText(output);
    }
}