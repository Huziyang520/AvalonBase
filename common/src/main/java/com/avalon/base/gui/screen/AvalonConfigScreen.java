package com.avalon.base.gui.screen;

import com.avalon.base.gui.theme.GuiTheme;
import com.avalon.base.gui.theme.ModernTheme;
import com.avalon.base.gui.theme.ThemedButton;
import com.avalon.base.gui.theme.VanillaTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

/**
 * 通用配置屏幕基类，供业务模组的可视化编辑页面继承。
 *
 * <p>封装了主题管理（原版/末影紫双主题）、保存/取消按钮布局、编辑权限判定、滚动列表基础设施、
 * 光标管理与点击音效等与具体业务无关的通用能力。业务 Screen 只需实现 {@link #saveConfig()} 与
 * 自身的控件渲染逻辑。
 *
 * <p>屏幕默认宽 300、高 260。子类通过 {@link #yo(int)} 换算相对纵坐标。
 */
public abstract class AvalonConfigScreen extends Screen {

    protected static final int GUI_WIDTH = 300;
    protected static final int GUI_HEIGHT = 260;
    protected static final int CONTENT_MIN_Y = 10;
    protected static final int CONTENT_MAX_Y = 218;
    protected static final int MAX_VISIBLE_ITEMS = 3;
    protected static final int LIST_ITEM_H = 14;

    protected static final GuiTheme VANILLA_THEME = new VanillaTheme();
    protected static final GuiTheme MODERN_THEME = new ModernTheme();

    protected boolean canEdit;
    protected int guiLeft, guiTop;
    protected int blScroll;

    protected GuiTheme theme;
    private long handCursor;
    private long arrowCursor;

    protected AvalonConfigScreen(Component title) {
        super(title);
        this.theme = MODERN_THEME; // 默认末影紫
    }

    @Override
    protected void init() {
        guiLeft = (width - GUI_WIDTH) / 2;
        guiTop = Math.max(10, (height - GUI_HEIGHT) / 2);
        canEdit = minecraft != null && minecraft.player != null && minecraft.player.hasPermissions(2);
        blScroll = Math.max(0, blScroll);
        handCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR);
        arrowCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
    }

    protected void setTheme(GuiTheme t) {
        this.theme = t;
    }

    /**
     * 相对纵坐标换算。
     */
    protected int yo(int relY) {
        return guiTop + relY;
    }

    protected Font f() {
        return font;
    }

    /**
     * 由业务子类实现：保存配置（通常组装网络包发送服务端）。
     */
    protected abstract void saveConfig();

    /**
     * 由业务子类在点击保存按钮时调用。
     */
    protected void onSavePressed() {
        if (!canEdit) return;
        playClickSound();
        saveConfig();
        onClose();
    }

    /**
     * 保存按钮文案。由业务子类覆写为自身语言键（如 {@code Component.translatable("gui.authcmd.save")}）。
     */
    protected Component saveButtonLabel() {
        return Component.literal("Save");
    }

    /**
     * 取消按钮文案。由业务子类覆写为自身语言键（如 {@code Component.translatable("gui.authcmd.cancel")}）。
     */
    protected Component cancelButtonLabel() {
        return Component.literal("Cancel");
    }

    /**
     * 由业务子类在初始化时添加标准保存/取消按钮。
     */
    protected void addSaveCancelButtons() {
        if (canEdit) {
            ThemedButton save = new ThemedButton(guiLeft + GUI_WIDTH - 118, yo(194), 55, 20,
                    saveButtonLabel(), b -> onSavePressed(),
                    theme, GuiTheme.ButtonRole.PRIMARY, font);
            addRenderableWidget(save);
        }
        ThemedButton cancel = new ThemedButton(guiLeft + GUI_WIDTH - 58, yo(194), 55, 20,
                cancelButtonLabel(), b -> onClose(),
                theme, GuiTheme.ButtonRole.NEUTRAL, font);
        addRenderableWidget(cancel);
    }

    protected void playClickSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    protected void msg(String key) {
        if (minecraft != null && minecraft.player != null)
            minecraft.player.displayClientMessage(Component.translatable(key), false);
    }

    protected void msg(String key, Object... args) {
        if (minecraft != null && minecraft.player != null)
            minecraft.player.displayClientMessage(Component.translatable(key, args), false);
    }

    /**
     * 无编辑权限时的底部状态文案。由业务子类覆写为自身语言键。
     */
    protected Component viewOnlyText() {
        return Component.literal("View only, you have no edit permission");
    }

    /**
     * 有编辑权限时的底部状态文案。由业务子类覆写为自身语言键。
     */
    protected Component canEditText() {
        return Component.literal("You have edit permission");
    }

    /**
     * 由子类调用：渲染底部编辑状态文字。
     */
    protected void renderStatus(GuiGraphics graphics, int bottomY) {
        graphics.drawString(font,
                canEdit ? canEditText() : viewOnlyText(),
                guiLeft + 8, yo(bottomY), canEdit ? theme.okColor() : theme.warnColor(), false);
    }

    /**
     * 由子类调用：滚动列表的滚轮处理（返回 true 表示已消费）。
     */
    protected boolean handleListScroll(double mouseX, double mouseY, double delta, int listSize, int listStartY) {
        if (listSize > MAX_VISIBLE_ITEMS) {
            int top = yo(listStartY) - 8, bot = yo(listStartY + MAX_VISIBLE_ITEMS * LIST_ITEM_H) - 8;
            if (mouseY >= top && mouseY < bot) {
                blScroll = Mth.clamp(blScroll - (int) Math.signum(delta), 0, listSize - MAX_VISIBLE_ITEMS);
                return true;
            }
        }
        return false;
    }

    /**
     * 由子类调用：绘制滚动条。
     */
    protected void drawScrollbar(GuiGraphics graphics, int listSize, int listStartY) {
        if (listSize <= MAX_VISIBLE_ITEMS) return;
        int tx = guiLeft + GUI_WIDTH - 16;
        int tTop = yo(listStartY) - 6;
        int tBot = yo(listStartY + MAX_VISIBLE_ITEMS * LIST_ITEM_H) - 8;
        theme.drawScrollTrack(graphics, tx, tTop, 4, tBot - tTop);
        int trackH = tBot - tTop;
        int thumbH = Math.max(8, trackH * MAX_VISIBLE_ITEMS / listSize);
        float p = (float) blScroll / Math.max(1, listSize - MAX_VISIBLE_ITEMS);
        theme.drawScrollThumb(graphics, tx, tTop + Math.round((trackH - thumbH) * p), 4, thumbH);
    }

    /**
     * 由子类调用：根据交互控件是否 hover 设置手形光标。
     */
    protected void updateCursor(boolean showHand) {
        long window = Minecraft.getInstance().getWindow().getWindow();
        GLFW.glfwSetCursor(window, showHand ? handCursor : arrowCursor);
    }

    @Override
    public void removed() {
        super.removed();
        if (this.handCursor != 0) GLFW.glfwDestroyCursor(this.handCursor);
        if (this.arrowCursor != 0) GLFW.glfwDestroyCursor(this.arrowCursor);
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}
