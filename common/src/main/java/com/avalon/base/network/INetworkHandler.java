package com.avalon.base.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 网络抽象接口。业务模组的 Common 只依赖本接口，由 Forge / Fabric 分别实现。
 *
 * <p>业务模组定义自己的消息类 {@code T}，并通过 {@link #registerMessage} 提供编解码函数与处理回调，
 * 即可获得平台无关的注册与收发能力。这使 Forge 的 SimpleChannel 与 Fabric 的 Networking API
 * 在 Common 层统一为一个模型。
 */
public interface INetworkHandler {

    /**
     * 注册一条双向消息：通道、消息类、编码/解码函数与处理回调。
     * 由业务模组的平台入口在初始化时调用。
     *
     * @param handler 处理回调，其参数 {@link MessageContext} 提供 enqueueWork 与 sender。
     */
    <T> void registerMessage(ResourceLocation channel, Class<T> clazz,
                             BiConsumer<T, FriendlyByteBuf> encoder,
                             Function<FriendlyByteBuf, T> decoder,
                             Consumer<MessageContext<T>> handler);

    /**
     * 向服务器发送指定消息（客户端 → 服务端）。
     */
    <T> void sendToServer(ResourceLocation channel, T message);

    /**
     * 向指定服务器上的所有玩家广播消息（服务端 → 全部客户端）。
     */
    <T> void sendToAll(MinecraftServer server, ResourceLocation channel, T message);

    /**
     * 向指定玩家发送消息（服务端 → 单个客户端）。
     */
    <T> void sendToPlayer(ServerPlayer player, ResourceLocation channel, T message);

    /**
     * 消息处理上下文，封装跨平台的线程调度与发送方信息。
     */
    final class MessageContext<T> {
        private final T message;
        private final Consumer<Runnable> executor;
        private final ServerPlayer sender;
        private final boolean clientSide;

        public MessageContext(T message, Consumer<Runnable> executor, ServerPlayer sender, boolean clientSide) {
            this.message = message;
            this.executor = executor;
            this.sender = sender;
            this.clientSide = clientSide;
        }

        public T getMessage() {
            return message;
        }

        /**
         * 在游戏线程上执行指定逻辑。
         */
        public void enqueueWork(Runnable runnable) {
            executor.accept(runnable);
        }

        /**
         * 服务端消息的发送者（客户端消息为 null）。
         */
        public ServerPlayer getSender() {
            return sender;
        }

        public boolean isClientSide() {
            return clientSide;
        }
    }
}
