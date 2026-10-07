package com.avalon.base.gui.anim;

import net.minecraft.util.Util;

/**
 * 列表 / 项目切换的平滑滑动值（“切换项目的滑动动画”，AvalonBase 通用能力）。
 *
 * <p>用途：业务屏的名单列表（例如 {@code blScroll}）在滚轮或切换模式时，只改
 * {@link #setTarget(int)}，渲染时用 {@link #displayValue()} 取整绘制；每帧调用
 * {@link #tick()} 让显示值一阶缓动追上目标，得到“滑过去”的观感。
 *
 * <p>与主题内 {@code Anim}（hover 缓动）同一套时间步进口径（{@code Util.getMillis()} + 限幅 dt），
 * 但值域是“条目索引”而不是 0..1。
 */
public final class ScrollAnim {

    private static final float DEFAULT_SPEED = 14f;

    private final float speed;
    private int target;
    private float value;
    private long lastTime = Util.getMillis();

    public ScrollAnim(int initial) {
        this(initial, DEFAULT_SPEED);
    }

    public ScrollAnim(int initial, float speed) {
        this.target = initial;
        this.value = initial;
        this.speed = speed;
    }

    /** 设置目标条目（越界钳制由调用方负责）。 */
    public void setTarget(int target) {
        this.target = target;
    }

    public int target() {
        return target;
    }

    /** 当前（可含小数的）显示值。 */
    public float value() {
        return value;
    }

    /** 绘制用的整数显示值。 */
    public int displayValue() {
        return Math.round(value);
    }

    /** 立即对齐（例如切模式时重置滚动位置，不需要动画）。 */
    public void snapTo(int value) {
        this.target = value;
        this.value = value;
    }

    public boolean isSettled() {
        return Math.abs(target - value) < 0.01f;
    }

    /** 每帧调用：把显示值向目标推进。 */
    public void tick() {
        long now = Util.getMillis();
        float dt = Math.min(0.1f, (now - lastTime) / 1000f);
        lastTime = now;
        value += (target - value) * Math.min(1f, speed * dt);
        if (Math.abs(target - value) < 0.01f) value = target;
    }
}
