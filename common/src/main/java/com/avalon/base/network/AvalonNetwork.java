package com.avalon.base.network;

import com.avalon.base.Constants;
import com.avalon.base.platform.Services;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Common 侧网络访问点。具体实现由 Forge / Fabric 在初始化时通过 {@link #set(INetworkHandler)} 注入。
 * 这样业务模组的 Common 逻辑无需依赖任何平台 API。
 *
 * <p>业务模组定义自己的消息类与通道 id，经由本类注册与收发。
 *
 * <p><b>并发安全说明：</b>Forge 的 {@code FMLCommonSetupEvent} 会对各模组<b>并行</b>触发，
 * 业务模组（OnlyTP / AuthCmd）的 {@code commonSetup} 可能先于 AvalonBase 的初始化执行。
 * 因此 {@link #get()} 在 handler 尚未注入时会按当前平台<b>懒加载自初始化</b>对应的实现，
 * 从根源上消除「NetworkHandler not initialized」的初始化顺序竞态。
 */
public final class AvalonNetwork {

    private static volatile INetworkHandler handler;

    private AvalonNetwork() {
    }

    /**
     * 平台模组在自身初始化时显式注入实现（正常路径）。
     */
    public static void set(INetworkHandler impl) {
        handler = impl;
    }

    /**
     * 获取网络实现。若尚未注入，则按当前平台懒加载对应实现，保证任何调用方都能安全使用。
     */
    public static INetworkHandler get() {
        if (handler == null) {
            synchronized (AvalonNetwork.class) {
                if (handler == null) {
                    initByPlatform();
                }
            }
        }
        return handler;
    }

    /**
     * 根据当前平台（通过 {@link Services#PLATFORM} 判断）反射创建对应的网络实现。
     * 使用反射以绕过 common 模块对平台模块的编译期依赖。
     */
    private static void initByPlatform() {
        String platform = Services.PLATFORM.getPlatformName();
        String implClass = "Forge".equals(platform)
                ? "com.avalon.base.network.ForgeNetworkHandler"
                : "com.avalon.base.network.FabricNetworkHandler";
        try {
            Class<?> clazz = Class.forName(implClass);
            handler = (INetworkHandler) clazz.getField("INSTANCE").get(null);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to initialize NetworkHandler for platform: " + platform, e);
        }
        Constants.LOG.debug("AvalonNetwork lazily initialized {} for platform {}", handler, platform);
    }

    /**
     * 注册一条消息（通道、类、编解码、处理回调）。
     */
    public static <T> void registerMessage(ResourceLocation channel, Class<T> clazz,
                                           BiConsumer<T, FriendlyByteBuf> encoder,
                                           Function<FriendlyByteBuf, T> decoder,
                                           Consumer<INetworkHandler.MessageContext<T>> handler) {
        get().registerMessage(channel, clazz, encoder, decoder, handler);
    }

    /**
     * 向服务器发送指定通道的消息。
     */
    public static <T> void sendToServer(ResourceLocation channel, T message) {
        get().sendToServer(channel, message);
    }

    /**
     * 向指定服务器上的所有玩家广播指定通道的消息。
     */
    public static <T> void sendToAll(MinecraftServer server, ResourceLocation channel, T message) {
        get().sendToAll(server, channel, message);
    }

    /**
     * 向指定玩家发送指定通道的消息。
     */
    public static <T> void sendToPlayer(ServerPlayer player, ResourceLocation channel, T message) {
        get().sendToPlayer(player, channel, message);
    }
}
