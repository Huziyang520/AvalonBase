package com.avalon.base.gui.panel;

import com.avalon.base.gui.util.TextFit;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Abstract screen base for the "panel" UI type, a port of the source's
 * {@code ThemedScreen}.
 *
 * <p>Unlike the original, {@link #panelW}/{@link #panelH} are mutable so a
 * subclass can compute the ideal size (e.g. via
 * {@link com.avalon.base.gui.util.AutoLayout#constrainPanelW}) before binding
 * widgets. Toast colors are read from the active {@link PanelPalette} and the
 * toast text is fitted so it never overflows the toast box.</p>
 */
public abstract class PanelScreen extends Screen {
    protected int panelW;
    protected int panelH;
    protected int left;
    protected int top;
    protected Component toast;
    protected boolean toastError;
    protected long toastUntil;

    protected PanelScreen(Component title, int panelW, int panelH) {
        super(title);
        this.panelW = panelW;
        this.panelH = panelH;
    }

    @Override
    protected void init() {
        reposition();
        buildWidgets();
    }

    /** Set the panel size and recenter it on the screen. */
    protected void setPanelSize(int w, int h) {
        this.panelW = w;
        this.panelH = h;
        reposition();
    }

    /** Recenter the panel using the current panel size. */
    protected void reposition() {
        left = (width - panelW) / 2;
        top = (height - panelH) / 2;
    }

    protected abstract void buildWidgets();

    public void showToast(Component message, boolean error) {
        this.toast = message;
        this.toastError = error;
        this.toastUntil = System.currentTimeMillis() + 4000L;
    }

    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float delta) {
        g.fill(0, 0, width, height, 0xB0050608);
        PanelTheme.panel(g, left, top, panelW, panelH);
        PanelTheme.header(g, font, title, left + 1, top + 2, panelW - 2);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        super.render(g, mouseX, mouseY, delta);
        renderContents(g, mouseX, mouseY, delta);
        if (toast != null && System.currentTimeMillis() < toastUntil) {
            PanelPalette p = PanelTheme.palette();
            String text = TextFit.fit(font, toast.getString(), panelW - 28);
            int w = Math.min(panelW - 20, font.width(text) + 16);
            int x = left + (panelW - w) / 2;
            int y = top + panelH - 30;
            g.fill(x, y, x + w, y + 14, toastError ? p.toastErrorBg : p.toastOkBg);
            g.renderOutline(x, y, w, 14, toastError ? p.danger : p.ok);
            g.drawCenteredString(font, Component.literal(text), x + w / 2, y + 3, PanelTheme.palette().text);
        } else if (toast != null) {
            toast = null;
        }
    }

    protected void renderContents(GuiGraphics g, int mouseX, int mouseY, float delta) {}

    @Override
    public boolean isPauseScreen() { return false; }

    protected boolean inside(int mx, int my, int x, int y, int w, int h) {
        return mx >= x && my >= y && mx < x + w && my < y + h;
    }
}