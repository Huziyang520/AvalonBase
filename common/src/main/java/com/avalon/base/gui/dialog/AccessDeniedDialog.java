package com.avalon.base.gui.dialog;

import com.avalon.base.gui.panel.PanelButton;
import com.avalon.base.gui.panel.PanelPalette;
import com.avalon.base.gui.panel.PanelScreen;
import com.avalon.base.gui.panel.PanelTheme;
import com.avalon.base.gui.util.TextFit;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Compact modal dialog shown when the remote server has closed the config
 * editing entry. It carries no business logic: a single button (or the
 * enter/escape key) returns the player to the parent screen.
 */
public class AccessDeniedDialog extends PanelScreen {

    private static final int PANEL_W = 240;
    private static final int PANEL_H = 96;
    private static final int BTN_W = 80;
    private static final int BTN_H = 18;

    private final Screen parentScreen;
    private final Component message;
    private final Component buttonLabel;
    private List<String> lines;

    public AccessDeniedDialog(Screen parent, Component title, Component message, Component buttonLabel) {
        super(title, PANEL_W, PANEL_H);
        this.parentScreen = parent;
        this.message = message;
        this.buttonLabel = buttonLabel;
    }

    @Override
    protected void buildWidgets() {
        this.lines = TextFit.wrap(font, message.getString(), panelW - 24);
        int bx = left + (panelW - BTN_W) / 2;
        int by = top + PANEL_H - 10 - BTN_H;
        addRenderableWidget(new PanelButton(bx, by, BTN_W, BTN_H, buttonLabel,
                PanelButton.Style.NORMAL, this::backToParent));
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        PanelPalette p = PanelTheme.palette();
        int y = top + 26;
        for (String line : lines) {
            g.text(font, line, left + 12, y, p.text, false);
            y += 11;
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isConfirmation()) {
            backToParent();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        backToParent();
    }

    private void backToParent() {
        if (minecraft != null) {
            minecraft.setScreenAndShow(parentScreen);
        }
    }
}
