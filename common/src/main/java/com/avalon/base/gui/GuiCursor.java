package com.avalon.base.gui;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

/**
 * 通用光标工具（静态门面）：统一持有并设置 GLFW 手形/箭头光标。
 *
 * <p>只提供设置光标的纯工具方法，不做任何屏幕级遍历或帧级判定。
 * 业务模组在自己按钮的绘制代码（如 {@code renderWidget}）里悬停时调用
 * {@link #applyHand()} 显示手形即可。
 *
 * <p>光标句柄惰性创建并缓存，进程内复用，避免重复 {@code glfwCreateStandardCursor}。
 */
public final class GuiCursor {

    private static long handCursor = 0L;
    private static long arrowCursor = 0L;

    private GuiCursor() {
    }

    /** 惰性获取手形光标句柄（{@code GLFW_HAND_CURSOR}）。 */
    public static long hand() {
        if (handCursor == 0L) {
            handCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_HAND_CURSOR);
        }
        return handCursor;
    }

    /** 惰性获取箭头光标句柄（{@code GLFW_ARROW_CURSOR}）。 */
    public static long arrow() {
        if (arrowCursor == 0L) {
            arrowCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
        }
        return arrowCursor;
    }

    /** 把当前窗口光标设为手形。 */
    public static void applyHand() {
        long window = Minecraft.getInstance().getWindow().getWindow();
        GLFW.glfwSetCursor(window, hand());
    }

    /** 把当前窗口光标设为箭头。 */
    public static void applyArrow() {
        long window = Minecraft.getInstance().getWindow().getWindow();
        GLFW.glfwSetCursor(window, arrow());
    }

    /** 按 {@code hand} 设置光标：{@code true} 为手形，{@code false} 为箭头。 */
    public static void apply(boolean hand) {
        if (hand) {
            applyHand();
        } else {
            applyArrow();
        }
    }
}
