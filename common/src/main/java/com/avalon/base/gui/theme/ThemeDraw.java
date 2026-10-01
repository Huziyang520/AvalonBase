package com.avalon.base.gui.theme;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.math.Matrix4f;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;

/**
 * 1.18.2 兼容的绘制工具。
 * <p>
 * 1.18.2 中 {@link GuiComponent#fill} 为 {@code public static}，可直接调用；
 * 而 {@code fillGradient} 为 {@code protected static}，包外既不能调用，也无法在
 * 子类中用同名 {@code static} 方法隐藏（会触发"隐藏冲突"编译错误）。
 * 因此本类不继承任何类，直接自实现 {@code fillGradient}（Tesselator 顶点渐变），
 * {@code fill} 则委托给 {@link GuiComponent#fill}。
 */
public final class ThemeDraw {

    private ThemeDraw() {}

    public static void fill(PoseStack pose, int x1, int y1, int x2, int y2, int color) {
        GuiComponent.fill(pose, x1, y1, x2, y2, color);
    }

    /** 手动实现与 {@code GuiComponent.fillGradient} 等价的纵向渐变填充。 */
    public static void fillGradient(PoseStack pose, int x1, int y1, int x2, int y2, int from, int to) {
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        Matrix4f mat = pose.last().pose();
        float fromA = (float) (from >> 24 & 255) / 255.0F;
        float fromR = (float) (from >> 16 & 255) / 255.0F;
        float fromG = (float) (from >> 8 & 255) / 255.0F;
        float fromB = (float) (from & 255) / 255.0F;
        float toA = (float) (to >> 24 & 255) / 255.0F;
        float toR = (float) (to >> 16 & 255) / 255.0F;
        float toG = (float) (to >> 8 & 255) / 255.0F;
        float toB = (float) (to & 255) / 255.0F;
        BufferBuilder bb = Tesselator.getInstance().getBuilder();
        bb.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        bb.vertex(mat, (float) x2, (float) y1, 0.0F).color(fromR, fromG, fromB, fromA).endVertex();
        bb.vertex(mat, (float) x1, (float) y1, 0.0F).color(fromR, fromG, fromB, fromA).endVertex();
        bb.vertex(mat, (float) x1, (float) y2, 0.0F).color(toR, toG, toB, toA).endVertex();
        bb.vertex(mat, (float) x2, (float) y2, 0.0F).color(toR, toG, toB, toA).endVertex();
        bb.end();
        BufferUploader.end(bb);
        RenderSystem.disableBlend();
    }
}