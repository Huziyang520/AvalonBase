package com.avalon.base.gui.anim;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.Util;

/**
 * 界面开/关动画器（AvalonBase 通用能力，组合式：任何 {@code Screen} 都可持有本类实例）。
 *
 * <p><b>用法（业务屏三步）</b>：
 * <pre>{@code
 * private final ScreenAnim anim = new ScreenAnim(ScreenAnimType.SCALE_BOUNCE, ScreenAnimType.SCALE_BOUNCE);
 *
 * // 1) 渲染：首行 beginFrame，末行 endFrame
 * public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
 *     mouseX = (int) anim.localX(mouseX, width);      // 2) 命中测试用动画坐标系
 *     mouseY = (int) anim.localY(mouseY, height);
 *     anim.beginFrame(g, width, height);
 *     ... 原有绘制 ...
 *     anim.endFrame(g, width, height);
 * }
 *
 * // 3) 关闭：动画接管时推迟切屏，播完由 tick/末帧触发
 * public void onClose() { if (!anim.beginClose()) closeNow(); }
 * public void tick() { super.tick(); if (anim.isCloseFinished()) closeNow(); }
 * }</pre>
 *
 * <p>实现要点：只使用 {@code GuiGraphics.pose()}（1.21.x 为 {@code PoseStack}）
 * 做 push/pop + translate/scale，因此**原样子控件与自绘内容一起变换**；黑幕（{@link ScreenAnimType#FADE}）
 * 在 pop 之后绘制，保证铺满整屏。
 *
 * <p>注意：变换只影响绘制，不影响原版控件的点击命中（{@code mouseClicked} 用的是屏幕坐标）。
 * 动画时长只有 200~420ms，这段时间内的点击偏差属于可接受范围，故不做点击反变换。
 */
public final class ScreenAnim {

    /** 滑动类动画的起始位移（px）；也用于下落类动画的初始高度基数。 */
    private static final float SLIDE_DISTANCE = 56f;

    /** {@link ScreenAnimType#SCALE_BOUNCE} 的起始缩放。 */
    private static final float BOUNCE_FROM_SCALE = 0.86f;

    /** {@link ScreenAnimType#POP_ZOOM} 的起始缩放。 */
    private static final float POP_FROM_SCALE = 1.12f;

    private final ScreenAnimType openType;
    private final ScreenAnimType closeType;
    private final float openDurationMs;
    private final float closeDurationMs;

    /** 0 = 开屏起始态，1 = 结束态；关屏阶段由 1 → 0。 */
    private float progress = 1f;
    private boolean closing;
    private boolean closeFinished;
    private long phaseStart = Util.getMillis();

    // ─── 每帧实际施加的变换（供渲染与鼠标反算使用） ───
    private boolean pushed;
    /** 是否处于“临时退出变换”状态（{@link #suspend} 之后、{@link #resume} 之前）。 */
    private boolean suspended;
    private float appliedScale = 1f;
    private float appliedOffsetX;
    private float appliedOffsetY;

    public ScreenAnim(ScreenAnimType openType, ScreenAnimType closeType) {
        this(openType, closeType, openType.defaultDurationMs(), closeType.defaultDurationMs());
    }

    public ScreenAnim(ScreenAnimType openType, ScreenAnimType closeType, float openDurationMs, float closeDurationMs) {
        this.openType = openType == null ? ScreenAnimType.NONE : openType;
        this.closeType = closeType == null ? ScreenAnimType.NONE : closeType;
        this.openDurationMs = openDurationMs;
        this.closeDurationMs = closeDurationMs;
    }

    /** 无动画实例（进度恒为 1，beginFrame/endFrame 为空操作）。 */
    public static ScreenAnim disabled() {
        return new ScreenAnim(ScreenAnimType.NONE, ScreenAnimType.NONE, 0f, 0f);
    }

    /** 是否配置了任一方向的动画。 */
    public boolean isEnabled() {
        return !openType.isNone() || !closeType.isNone();
    }

    /** 播放开屏动画（进度归零重新起跑）。 */
    public void playOpen() {
        closing = false;
        closeFinished = false;
        progress = openType.isNone() ? 1f : 0f;
        phaseStart = Util.getMillis();
    }

    /**
     * 尝试接管关闭流程。
     *
     * @return true 表示动画负责关闭（调用方应直接 return，等到 {@link #isCloseFinished()}
     *         为 true 时再真正切屏）；false 表示无关闭动画，调用方应立即切屏。
     */
    public boolean beginClose() {
        if (closeType.isNone() || closeFinished) return false;
        if (closing) return true;
        closing = true;
        closeFinished = false;
        progress = 1f;
        phaseStart = Util.getMillis();
        return true;
    }

    /** 是否处于关闭阶段。 */
    public boolean isClosing() {
        return closing;
    }

    /** 关闭动画是否已播完（仅关闭阶段可能为 true）。 */
    public boolean isCloseFinished() {
        return closeFinished;
    }

    /** 当前阶段实际使用的类型。 */
    public ScreenAnimType currentType() {
        return closing ? closeType : openType;
    }

    /** 是否正在播放（进度未到收尾态）。 */
    public boolean isActive() {
        return isEnabled() && progress < 1f;
    }

    /** 当前进度：开屏 0→1，关屏 1→0。 */
    public float progress() {
        return progress;
    }

    /** 推进时间轴（由 {@link #beginFrame} 自动调用；也可在 {@code tick()} 里单独调用）。 */
    public void tick() {
        float duration = closing ? closeDurationMs : openDurationMs;
        float elapsed = duration <= 0f ? 1f : Mth.clamp((Util.getMillis() - phaseStart) / duration, 0f, 1f);
        progress = closing ? 1f - elapsed : elapsed;
        if (closing && elapsed >= 1f) closeFinished = true;
    }

    /**
     * 动画帧开始：推进时间轴并施加位姿变换（无动画时为空操作）。
     * 必须在业务绘制之前调用，并在绘制结束后调用 {@link #endFrame}。
     */
    public void beginFrame(GuiGraphics g, int width, int height) {
        tick();
        pushed = false;
        suspended = false;
        appliedScale = 1f;
        appliedOffsetX = 0f;
        appliedOffsetY = 0f;

        ScreenAnimType type = currentType();
        if (type.isNone() || progress >= 1f) return;

        float eased = type.ease(progress);
        switch (type) {
            case SLIDE_UP -> appliedOffsetY = (1f - eased) * SLIDE_DISTANCE;
            case SLIDE_DOWN -> appliedOffsetY = -(1f - eased) * SLIDE_DISTANCE;
            case SLIDE_LEFT -> appliedOffsetX = (1f - eased) * SLIDE_DISTANCE;
            case SLIDE_RIGHT -> appliedOffsetX = -(1f - eased) * SLIDE_DISTANCE;
            case SCALE_BOUNCE -> appliedScale = BOUNCE_FROM_SCALE + (1f - BOUNCE_FROM_SCALE) * eased;
            case POP_ZOOM -> appliedScale = POP_FROM_SCALE - (POP_FROM_SCALE - 1f) * eased;
            case DROP_BOUNCE -> appliedOffsetY = -(1f - eased) * SLIDE_DISTANCE * 2f;
            case FADE, NONE -> {
                // FADE 只画黑幕，不做位姿变换
            }
        }

        // 黑幕类型不 push（省一次矩阵操作，也避免无意义的矩阵累积）
        if (type.isVeil()) return;
        applyTransform(g, width, height);
    }

    /** 施加本帧的位姿变换（push + 缩放/位移）。*/
    private void applyTransform(GuiGraphics g, int width, int height) {
        g.pose().pushPose();
        pushed = true;
        float cx = width / 2f;
        float cy = height / 2f;
        if (appliedScale != 1f) {
            g.pose().translate(cx, cy, 0f);
            g.pose().scale(appliedScale, appliedScale, 1f);
            g.pose().translate(-cx, -cy, 0f);
        }
        if (appliedOffsetX != 0f || appliedOffsetY != 0f) {
            g.pose().translate(appliedOffsetX, appliedOffsetY, 0f);
        }
    }

    /**
     * 临时退出动画变换，用于**必须整屏、不能被缩放/位移**的浮层（例如只读遮罩）。
     *
     * <p>用法：{@code boolean s = anim.suspend(g); 画整屏浮层; if (s) anim.resume(g, width, height);}
     *
     * @return true 表示确实退出过（调用方必须在本帧内配对调用 {@link #resume}）
     */
    public boolean suspend(GuiGraphics g) {
        if (!pushed) return false;
        g.pose().popPose();
        pushed = false;
        suspended = true;
        return true;
    }

    /** 恢复动画变换（与 {@link #suspend} 配对，同一帧内必须成对）。*/
    public void resume(GuiGraphics g, int width, int height) {
        if (!suspended) return;
        suspended = false;
        applyTransform(g, width, height);
    }

    /**
     * 动画帧结束：撤销位姿变换并绘制黑幕（无动画时为空操作）。
     */
    public void endFrame(GuiGraphics g, int width, int height) {
        if (pushed) {
            g.pose().popPose();
            pushed = false;
        }
        if (currentType().isVeil()) {
            int alpha = Math.round((1f - progress) * 235f);
            if (alpha > 0) {
                g.fill(0, 0, width, height, (alpha << 24));
            }
        }
    }

    /**
     * 把屏幕鼠标 X 反算到动画坐标系（供自绘控件的命中测试使用）。
     * 无动画或动画已结束时原样返回。
     */
    public double localX(double mouseX, int width) {
        if (!isActive() || appliedScale == 1f) return mouseX - appliedOffsetX;
        float cx = width / 2f;
        return cx + (mouseX - appliedOffsetX - cx) / appliedScale;
    }

    /** 把屏幕鼠标 Y 反算到动画坐标系。 */
    public double localY(double mouseY, int height) {
        if (!isActive() || appliedScale == 1f) return mouseY - appliedOffsetY;
        float cy = height / 2f;
        return cy + (mouseY - appliedOffsetY - cy) / appliedScale;
    }
}
