package com.avalon.base.gui.dialog;

import com.avalon.base.gui.panel.PanelButton;
import com.avalon.base.gui.panel.PanelPalette;
import com.avalon.base.gui.panel.PanelScreen;
import com.avalon.base.gui.panel.PanelTheme;
import com.avalon.base.gui.util.MouseButtons;
import com.avalon.base.gui.util.TextFit;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

import java.util.List;
import java.util.function.Consumer;

/**
 * Compact confirmation dialog opened from the mod list before editing the
 * local (main-menu) configuration. The checkbox state is held purely in this
 * dialog: it is reported to the caller only when the player presses confirm,
 * so cancelling never persists anything.
 */
public class LocalConfigNoticeDialog extends PanelScreen {

    private static final int PANEL_W = 260;
    private static final int MIN_PANEL_H = 132;
    private static final int BTN_W = 72;
    private static final int BTN_H = 18;
    private static final int BOX = 11;

    private final Screen parentScreen;
    private final Component message;
    private final Component checkboxLabel;
    private final Component cancelLabel;
    private final Component confirmLabel;
    private final Consumer<Boolean> onConfirm;

    private List<String> lines;
    private boolean checked;
    private boolean fired;

    public LocalConfigNoticeDialog(Screen parent, Component title, Component message, Component checkboxLabel,
                                   Component cancelLabel, Component confirmLabel, Consumer<Boolean> onConfirm) {
        super(title, PANEL_W, MIN_PANEL_H);
        this.parentScreen = parent;
        this.message = message;
        this.checkboxLabel = checkboxLabel;
        this.cancelLabel = cancelLabel;
        this.confirmLabel = confirmLabel;
        this.onConfirm = onConfirm;
    }

    @Override
    protected void buildWidgets() {
        this.lines = TextFit.wrap(font, message.getString(), PANEL_W - 24);
        int neededH = 64 + lines.size() * 11;
        if (neededH > panelH) {
            setPanelSize(PANEL_W, neededH);
        }
        int rowY = top + panelH - 10 - BTN_H;
        int confirmX = left + panelW - 12 - BTN_W;
        int cancelX = confirmX - 6 - BTN_W;
        addRenderableWidget(new PanelButton(cancelX, rowY, BTN_W, BTN_H, cancelLabel,
                PanelButton.Style.NORMAL, this::backToParent));
        addRenderableWidget(new PanelButton(confirmX, rowY, BTN_W, BTN_H, confirmLabel,
                PanelButton.Style.PRIMARY, this::doConfirm));
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        PanelPalette p = PanelTheme.palette();
        int y = top + 26;
        for (String line : lines) {
            g.text(font, line, left + 12, y, p.text, false);
            y += 11;
        }
        int boxX = left + 12;
        int boxY = top + panelH - 10 - BTN_H + (BTN_H - BOX) / 2;
        boolean boxHover = inside((int) mouseX, (int) mouseY, boxX - 2, boxY - 2, BOX + 8 + font.width(checkboxLabel), BOX + 4);
        g.fill(boxX, boxY, boxX + BOX, boxY + BOX, p.bgInput);
        g.outline(boxX, boxY, BOX, BOX, checked ? p.accent : boxHover ? p.accentBright : p.accentDim);
        if (checked) {
            drawCheck(g, boxX, boxY, p.accentBright);
        }
        g.text(font, checkboxLabel, boxX + BOX + 4, boxY + 2, boxHover ? p.accentBright : p.textDim, false);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (MouseButtons.isLeft(event)) {
            int boxX = left + 12;
            int boxY = top + panelH - 10 - BTN_H + (BTN_H - BOX) / 2;
            if (inside((int) event.x(), (int) event.y(), boxX - 2, boxY - 2, BOX + 8 + font.width(checkboxLabel), BOX + 4)) {
                this.checked = !this.checked;
                Minecraft.getInstance().getSoundManager().play(
                        SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
                return true;
            }
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.isConfirmation()) {
            doConfirm();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        backToParent();
    }

    private void doConfirm() {
        if (fired) {
            return;
        }
        fired = true;
        onConfirm.accept(checked);
    }

    private void backToParent() {
        if (minecraft != null) {
            minecraft.setScreenAndShow(parentScreen);
        }
    }

    /** Hand-drawn check mark filling the interior of the checkbox. */
    private static void drawCheck(GuiGraphicsExtractor g, int x, int y, int color) {
        int cx = x + BOX / 2;
        int cy = y + BOX / 2;
        for (int i = 0; i < 3; i++) {
            g.fill(cx - 3 + i, cy + 2 - i, cx - 2 + i, cy + 3 - i, color);
            g.fill(cx - 1 + i, cy + i, cx + i, cy + 1 + i, color);
        }
    }
}
