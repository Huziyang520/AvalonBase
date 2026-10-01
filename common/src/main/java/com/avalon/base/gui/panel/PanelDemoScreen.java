package com.avalon.base.gui.panel;

import com.avalon.base.gui.util.AutoLayout;
import com.avalon.base.gui.util.Format;
import com.avalon.base.gui.util.TextFit;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;

import java.util.List;

/**
 * Smoke screen for the panel UI type. Independent of any business domain, it
 * exercises every element of {@code gui.panel} plus the auto-layout helpers:
 * all four button styles, right-aligned overflow values, a fitted name/amount
 * pair, word-wrapped long text and toasts.
 *
 * <p>Open with {@code /avalonbase-demo} (client command, registered per-loader).
 * The panel width is computed from the active locale's strings to demonstrate
 * {@link AutoLayout#constrainPanelW}.</p>
 */
public class PanelDemoScreen extends PanelScreen {

    private PanelButton tabButton;

    public PanelDemoScreen() {
        super(Component.translatable("avalonbase.demo.title"), 0, 0);
    }

    @Override
    protected void buildWidgets() {
        String row = Component.translatable("avalonbase.demo.rowItem").getString()
                + Component.translatable("avalonbase.demo.rowAmount").getString();
        int desired = AutoLayout.minPanelWidth(font, List.of(
                Component.translatable("avalonbase.demo.title").getString(),
                Component.translatable("avalonbase.demo.longText").getString(),
                row), 20);
        setPanelSize(AutoLayout.constrainPanelW(Math.max(360, desired), width), 240);

        int btnW = 72;
        int gap = 8;
        int x = left + 10;
        int y = top + 24;
        addRenderableWidget(new PanelButton(x, y, btnW, 20, t("avalonbase.demo.normal"), PanelButton.Style.NORMAL, () -> {}));
        x += btnW + gap;
        addRenderableWidget(new PanelButton(x, y, btnW, 20, t("avalonbase.demo.primary"), PanelButton.Style.PRIMARY,
                () -> showToast(t("avalonbase.demo.toastOkMsg"), false)));
        x += btnW + gap;
        addRenderableWidget(new PanelButton(x, y, btnW, 20, t("avalonbase.demo.danger"), PanelButton.Style.DANGER,
                () -> showToast(t("avalonbase.demo.toastErrorMsg"), true)));
        x += btnW + gap;
        tabButton = new PanelButton(x, y, btnW, 20, t("avalonbase.demo.tab"), PanelButton.Style.TAB, () -> {});
        tabButton.setSelected(true);
        addRenderableWidget(tabButton);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor g, int mouseX, int mouseY, float delta) {
        int cx = left + 10;
        int cw = panelW - 20;
        int y = top + 56;
        int yy = y;

        // Fitted left/right pair: long name + big amount must both stay inside.
        AutoLayout.FitPair pair = AutoLayout.fitPair(font,
                t("avalonbase.demo.rowItem").getString(),
                t("avalonbase.demo.rowAmount").getString(), cw, 8);
        g.text(font, pair.left(), cx, yy, PanelTheme.palette().text, false);
        AutoLayout.drawRight(g, font, pair.right(), cx + cw, cx, yy, PanelTheme.palette().accentBright);
        yy += 12;

        // Right-aligned duration using Format.
        g.text(font, t("avalonbase.demo.duration"), cx, yy, PanelTheme.palette().textDim, false);
        AutoLayout.drawRight(g, font, Format.duration(149_000_000L), cx + cw, cx + 80, yy, PanelTheme.palette().text);
        yy += 20;

        // Word-wrapped long paragraph.
        for (String line : TextFit.wrap(font, t("avalonbase.demo.longText").getString(), cw)) {
            g.text(font, line, cx, yy, PanelTheme.palette().textDim, false);
            yy += 10;
        }
    }

    private static Component t(String key) {
        return Component.translatable(key);
    }
}