package com.avalon.base.gui.screen;

import com.avalon.base.gui.theme.GuiTheme;
import com.avalon.base.gui.theme.ModernTheme;
import com.avalon.base.gui.theme.ThemedButton;
import com.avalon.base.gui.theme.VanillaTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import com.mojang.blaze3d.platform.cursor.CursorTypes;

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
    /** 配置屏背景色（近不透明深色，压暗游戏画面防闪屏）。 */
    private static final int BACKDROP_COLOR = 0xC0101010;

    protected static final GuiTheme VANILLA_THEME = new VanillaTheme();
    protected static final GuiTheme MODERN_THEME = new ModernTheme();

    protected boolean canEdit;
    /** Parent screen (e.g. the mod list) to return to; null falls back to the default close behavior. */
    protected Screen parentScreen;
    /** Local-edit mode is opened from the main menu: edits write the local config file and never send packets. */
    protected boolean localEdit;
    protected int guiLeft, guiTop;
    protected int blScroll;

    protected GuiTheme theme;

    protected AvalonConfigScreen(Component title) {
        this(title, null, false);
    }

    protected AvalonConfigScreen(Component title, Screen parent, boolean localEdit) {
        super(title);
        this.parentScreen = parent;
        this.localEdit = localEdit;
        this.theme = MODERN_THEME; // 默认末影紫
    }

    /**
     * 面板内容底部相对纵坐标（含边框），用于小窗时保证底边框不超出屏幕。
     */
    private static final int CONTENT_BOTTOM_REL = CONTENT_MAX_Y + 14;

    @Override
    public void onClose() {
        if (parentScreen != null && minecraft != null) {
            minecraft.setScreenAndShow(parentScreen);
        } else {
            super.onClose();
        }
    }

    /**
     * 计算面板顶部纵坐标：优先居中，但保证面板底边框（content 底部 + 边框下沿）始终留在屏幕内。
     * 小窗（原版 GUI 缩放后可用高度不足）时上移以保住底边可见，避免底边框跑出屏幕。
     */
    protected int computeGuiTop() {
        int center = Math.max(6, (height - GUI_HEIGHT) / 2);
        return Math.min(center, height - 6 - CONTENT_BOTTOM_REL);
    }

    @Override
    protected void init() {
        guiLeft = (width - GUI_WIDTH) / 2;
        guiTop = computeGuiTop();
        canEdit = localEdit || (minecraft != null && minecraft.player != null
                && minecraft.player.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER));
        blScroll = Math.max(0, blScroll);
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

    /**
     * 背景通道：直接铺一层近不透明的深色背景。
     *
     * <p>26.2 渲染分 {@code extractBackground}（背景）与 {@code extractRenderState}（内容）两趟。
     * 若此处置空，打开本屏（{@link #isPauseScreen()} 为 true）时原版暂停画面的模糊暗背景消失，
     * 只靠 {@link #renderBackdrop} 的 33% 透明层遮不住清晰发亮的游戏画面，会出现"闪一下"。
     * 因此这里与 renderBackdrop 用同一深色铺满全屏，从背景趟就压暗游戏画面。</p>
     */
    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, BACKDROP_COLOR);
    }

    /**
     * 自绘深色背景；业务子类应在自绘内容之前调用一次。
     * 颜色近不透明，避免游戏画面透出造成闪屏。
     */
    protected void renderBackdrop(GuiGraphicsExtractor graphics) {
        graphics.fill(0, 0, this.width, this.height, BACKDROP_COLOR);
    }

    /**
     * 无编辑权限（只读）时叠加一层半透明遮罩，令自绘的卡片/文字呈只读观感；
     * 有编辑权限（OP）时不绘制，保持界面清晰。
     */
    protected void renderReadonlyOverlay(GuiGraphicsExtractor graphics) {
        if (canEdit) return;
        graphics.fill(0, 0, this.width, this.height, 0x50000000);
    }

    /**
     * 顶层渲染：只读遮罩 + 自绘控件。业务子类覆写时，应在自绘卡片/文字之后以
     * {@code super.extractRenderState(...)} 结尾调用本方法——无权限时遮罩盖住自绘内容，而保存/取消等控件
     * 绘制于遮罩之上保持清晰；有权限时不加遮罩，界面全部清晰。
     */
    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        renderReadonlyOverlay(graphics);
        for (var child : children()) {
            if (child instanceof Renderable renderable) {
                renderable.extractRenderState(graphics, mouseX, mouseY, partialTick);
            }
        }
    }

    protected void msg(String key) {
        if (minecraft != null && minecraft.player != null)
            minecraft.player.sendSystemMessage(Component.translatable(key));
    }

    protected void msg(String key, Object... args) {
        if (minecraft != null && minecraft.player != null)
            minecraft.player.sendSystemMessage(Component.translatable(key, args));
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
    protected void renderStatus(GuiGraphicsExtractor graphics, int bottomY) {
        graphics.text(font,
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
    protected void drawScrollbar(GuiGraphicsExtractor graphics, int listSize, int listStartY) {
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
    protected void updateCursor(GuiGraphicsExtractor graphics, boolean showHand) {
        // 26.3：光标必须经 GuiGraphicsExtractor.requestCursor 请求（帧末统一 apply）；
        // 直接调 Window.selectCursor 会被帧末的 applyCursor 覆盖，导致自绘控件悬停无手形。
        // 只在需要手形时请求；不请求箭头，以免覆盖原版控件（按钮/输入框）自己请求的光标。
        if (showHand) graphics.requestCursor(CursorTypes.POINTING_HAND);
    }

    @Override
    public void removed() {
        super.removed();
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}
