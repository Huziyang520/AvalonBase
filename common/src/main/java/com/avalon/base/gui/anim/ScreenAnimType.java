package com.avalon.base.gui.anim;

/**
 * 界面开/关动画类型（AvalonBase 通用能力，业务模组自行挑选）。
 *
 * <p>所有类型都只依赖 {@code GuiGraphicsExtractor.pose()}（26.3 的 2D 仿射矩阵栈），
 * 因此**原样子控件（按钮 / 输入框）也会一起变换**，不需要逐元素换算坐标。
 *
 * <p>语义（“进入”方向 = 开屏时的运动方向；关屏时同一类型反向播放）：
 * <ul>
 *   <li>{@link #NONE}：无动画（默认，向后兼容）。</li>
 *   <li>{@link #FADE}：黑幕淡入/淡出（内容不位移）。</li>
 *   <li>{@link #SLIDE_UP} / {@link #SLIDE_DOWN} / {@link #SLIDE_LEFT} / {@link #SLIDE_RIGHT}：
 *       从下 / 上 / 右 / 左滑入，关屏时滑回原处。</li>
 *   <li>{@link #SCALE_BOUNCE}：由小放大并**回弹过冲**（经典“弹跳”开场）。</li>
 *   <li>{@link #POP_ZOOM}：由略大收缩到 1（弹入），适合二级弹窗。</li>
 *   <li>{@link #DROP_BOUNCE}：自上而下落下并**弹性触底**（ease-out-bounce）。</li>
 * </ul>
 */
public enum ScreenAnimType {

    NONE(0f, Easing.NONE),
    FADE(180f, Easing.EASE_OUT_CUBIC),
    SLIDE_UP(220f, Easing.EASE_OUT_CUBIC),
    SLIDE_DOWN(220f, Easing.EASE_OUT_CUBIC),
    SLIDE_LEFT(220f, Easing.EASE_OUT_CUBIC),
    SLIDE_RIGHT(220f, Easing.EASE_OUT_CUBIC),
    SCALE_BOUNCE(320f, Easing.EASE_OUT_BACK),
    POP_ZOOM(200f, Easing.EASE_OUT_BACK),
    DROP_BOUNCE(420f, Easing.EASE_OUT_BOUNCE);

    /** 缓动函数。 */
    public enum Easing {
        NONE,
        EASE_OUT_CUBIC,
        /** 末段过冲（弹跳的来源），{@code eased} 可能 &gt; 1。 */
        EASE_OUT_BACK,
        /** 触底弹跳，{@code eased} 可能 &gt; 1。 */
        EASE_OUT_BOUNCE;

        public float apply(float t) {
            float x = t < 0f ? 0f : (t > 1f ? 1f : t);
            return switch (this) {
                case NONE -> x;
                case EASE_OUT_CUBIC -> 1f - (1f - x) * (1f - x) * (1f - x);
                case EASE_OUT_BACK -> {
                    float c1 = 1.70158f;
                    float c3 = c1 + 1f;
                    float p = x - 1f;
                    yield 1f + c3 * p * p * p + c1 * p * p;
                }
                case EASE_OUT_BOUNCE -> {
                    float n1 = 7.5625f;
                    float d1 = 2.75f;
                    float p = x;
                    if (p < 1f / d1) {
                        yield n1 * p * p;
                    } else if (p < 2f / d1) {
                        p -= 1.5f / d1;
                        yield n1 * p * p + 0.75f;
                    } else if (p < 2.5f / d1) {
                        p -= 2.25f / d1;
                        yield n1 * p * p + 0.9375f;
                    } else {
                        p -= 2.625f / d1;
                        yield n1 * p * p + 0.984375f;
                    }
                }
            };
        }
    }

    private final float durationMs;
    private final Easing easing;

    ScreenAnimType(float durationMs, Easing easing) {
        this.durationMs = durationMs;
        this.easing = easing;
    }

    /** 默认时长（毫秒）。 */
    public float defaultDurationMs() {
        return durationMs;
    }

    /** 该类型的缓动值（0..1，过冲型可能 &gt; 1）。 */
    public float ease(float t) {
        return easing.apply(t);
    }

    public boolean isNone() {
        return this == NONE;
    }

    /** 是否为纯黑幕类型（不做位姿变换）。 */
    public boolean isVeil() {
        return this == FADE;
    }
}
