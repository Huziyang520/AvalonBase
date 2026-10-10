package com.avalon.base.gui.screen;

import com.avalon.base.gui.GuiCursor;
import com.avalon.base.gui.anim.ScreenAnim;
import com.avalon.base.gui.anim.ScreenAnimType;
import com.avalon.base.gui.theme.GuiTheme;
import com.avalon.base.gui.theme.ModernTheme;
import com.avalon.base.gui.theme.ThemedButton;
import com.avalon.base.gui.theme.VanillaTheme;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;

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
    /** Parent screen (e.g. the mod list) to return to; null falls back to the default close behavior. */
    protected Screen parentScreen;
    /** Local-edit mode is opened from the main menu: edits write the local config file and never send packets. */
    protected boolean localEdit;
    protected int guiLeft, guiTop;
    protected int blScroll;

    protected GuiTheme theme;

    /** 本屏动画器；未调用 {@link #configureAnimations} 时恒为“无动画”。 */
    private ScreenAnim animation = ScreenAnim.disabled();

    /** 真正切屏是否已执行：防止关闭动画收尾与 tick 同时触发导致重复切屏。 */
    private boolean closeDone;

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

    /**
     * 配置本屏的开/关动画。业务子类在自己的构造函数里调用一次即可；
     * <b>不调用 = 无动画</b>（保持向后兼容，不影响既有调用方）。
     *
     * @param enabled 是否启用（业务模组的「启用动画效果」开关直接传进来）
     * @param open    开屏动画类型
     * @param close   关屏动画类型
     */
    protected void configureAnimations(boolean enabled, ScreenAnimType open, ScreenAnimType close) {
        this.animation = enabled ? new ScreenAnim(open, close) : ScreenAnim.disabled();
        this.closeDone = false;
    }

    /** 播放开屏动画（在构造函数里紧接 {@link #configureAnimations} 调用一次）。 */
    protected void playOpenAnimation() {
        animation.playOpen();
    }

    /** 本屏动画器（需要自行做坐标换算或查询进度时使用）。 */
    public ScreenAnim animation() {
        return animation;
    }

    /**
     * 动画帧开始：推进时间轴并施加位姿变换。业务子类应在 {@code render} 首行
     * （背景铺色之后）调用，末行调用 {@link #endAnimatedRender}；并把 mouseX/mouseY 换成
     * 动画坐标系：{@code mouseX = (int) animation().localX(mouseX, width);}
     */
    protected void beginAnimatedRender(GuiGraphics graphics) {
        animation.beginFrame(graphics, width, height);
    }

    /** 动画帧结束：撤销位姿变换 + 绘制黑幕，并兜底“关闭动画已播完”的收尾（不依赖 tick）。 */
    protected void endAnimatedRender(GuiGraphics graphics) {
        animation.endFrame(graphics, width, height);
        if (animation.isCloseFinished()) doClose();
    }

    /**
     * 真正切屏：返回父界面（无父界面则走原版默认关闭）。有关闭动画时由动画播完后调用。
     */
    protected void doClose() {
        if (closeDone) return;
        closeDone = true;
        if (parentScreen != null && minecraft != null) {
            minecraft.setScreen(parentScreen);
        } else {
            super.onClose();
        }
    }

    @Override
    public void onClose() {
        // 有关闭动画 → 先播动画，播完再由 doClose() 真正切屏
        if (animation.beginClose()) return;
        doClose();
    }

    @Override
    public void tick() {
        super.tick();
        animation.tick();
        if (animation.isCloseFinished()) doClose();
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
                && minecraft.player.hasPermissions(2));
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
     * 抑制 1.21.1 的四参背景。该背景会执行 {@code processBlurEffect}（模糊）+ 菜单贴图，
     * 若在自绘内容完成后经 {@code super.render} 再次调用，会把已自绘内容整片模糊/盖暗，
     * 导致「有编辑权限(OP)时界面也被误当无权限而模糊」。此处置空，业务自绘背景请调用
     * {@link #renderBackdrop(GuiGraphics)}。
     */
    @Override
    public void renderBackground(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 防闪（SKILL.md §10.28）：退场动画播完的那一帧，本屏不再绘制自己的背景，
        // 并在背景阶段就交回父界面——否则本屏背景与父界面背景会叠在同一帧，看到一次跳变。
        animation.tick();
        if (animation.isClosing() && animation.isCloseFinished()) {
            doClose(); // 与本次 return 成对：不可只留一半
            return;
        }
        // no-op：见上述说明
    }

    /**
     * 自绘半透明黑背景，替代 1.21.1 会模糊内容的四参背景。业务子类应在自绘内容之前调用一次。
     */
    protected void renderBackdrop(GuiGraphics graphics) {
        graphics.fill(0, 0, this.width, this.height, 0x55000000);
    }

    /**
     * 无编辑权限（只读）时叠加一层半透明遮罩，令自绘的卡片/文字呈只读观感；
     * 有编辑权限（OP）时不绘制，保持界面清晰。
     */
    protected void renderReadonlyOverlay(GuiGraphics graphics) {
        if (canEdit) return;
        graphics.fill(0, 0, this.width, this.height, 0x50000000);
    }

    /**
     * 顶层渲染：只读遮罩 + 自绘控件。业务子类覆写 {@link #render} 时，应在自绘卡片/文字之后以
     * {@code super.render(...)} 结尾调用本方法——无权限时遮罩盖住自绘内容，而保存/取消等控件
     * 绘制于遮罩之上保持清晰；有权限时不加遮罩，界面全部清晰。
     */
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        // 只读遮罩必须整屏、且不随开/关动画缩放：先临时退出动画变换再铺，铺完恢复
        boolean suspended = animation.suspend(graphics);
        renderReadonlyOverlay(graphics);
        if (suspended) animation.resume(graphics, width, height);
        for (var child : children()) {
            if (child instanceof Renderable renderable) {
                renderable.render(graphics, mouseX, mouseY, partialTick);
            }
        }
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
        // 只在需要手形时设置；不设置箭头，以免覆盖原版控件（按钮/输入框）自己设置的光标。
        if (showHand) GuiCursor.applyHand();
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
