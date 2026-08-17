package com.avalon.base.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Fabric 网络实现，封装 Fabric Networking API，实现 Common 的 INetworkHandler 抽象。
 *
 * <p>Fabric 的服务端与客户端接收器需分别在对应环境注册，因此 {@link #registerMessage} 仅记录注册信息，
 * 实际接收器由 {@link #registerServer()}（ModInitializer.onInitialize）与
 * {@link #registerClient()}（ClientModInitializer.onInitializeClient）统一遍历注册。
 */
public class FabricNetworkHandler implements INetworkHandler {

    public static final FabricNetworkHandler INSTANCE = new FabricNetworkHandler();

    // 注册表：channel -> 编解码与处理回调
    private static final Map<ResourceLocation, Registration<?>> REGISTRY = new ConcurrentHashMap<>();

    private FabricNetworkHandler() {
    }

    /** 服务端注册：遍历注册表为每个通道注册服务端接收器。 */
    public static void registerServer() {
        REGISTRY.forEach((channel, reg) -> reg.registerServer(channel));
    }

    /** 客户端注册：遍历注册表为每个通道注册客户端接收器。 */
    public static void registerClient() {
        REGISTRY.forEach((channel, reg) -> reg.registerClient(channel));
    }

    @Override
    public <T> void registerMessage(ResourceLocation channel, Class<T> clazz,
                                    BiConsumer<T, FriendlyByteBuf> encoder,
                                    Function<FriendlyByteBuf, T> decoder,
                                    Consumer<MessageContext<T>> handler) {
        REGISTRY.put(channel, new Registration<T>(encoder, decoder, handler));
    }

    @Override
    public <T> void sendToServer(ResourceLocation channel, T message) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        @SuppressWarnings("unchecked")
        Registration<T> reg = (Registration<T>) REGISTRY.get(channel);
        if (reg == null) throw new IllegalStateException("Channel not registered: " + channel);
        reg.encoder.accept(message, buf);
        ClientPlayNetworking.send(channel, buf);
    }

    @Override
    public <T> void sendToAll(MinecraftServer server, ResourceLocation channel, T message) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            sendToPlayer(player, channel, message);
        }
    }

    @Override
    public <T> void sendToPlayer(ServerPlayer player, ResourceLocation channel, T message) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        @SuppressWarnings("unchecked")
        Registration<T> reg = (Registration<T>) REGISTRY.get(channel);
        if (reg == null) throw new IllegalStateException("Channel not registered: " + channel);
        reg.encoder.accept(message, buf);
        ServerPlayNetworking.send(player, channel, buf);
    }

    /** 单条通道的注册信息（服务端/客户端各一份接收器）。 */
    private static final class Registration<T> {
        private final BiConsumer<T, FriendlyByteBuf> encoder;
        private final Function<FriendlyByteBuf, T> decoder;
        private final Consumer<MessageContext<T>> handler;

        Registration(BiConsumer<T, FriendlyByteBuf> encoder,
                     Function<FriendlyByteBuf, T> decoder,
                     Consumer<MessageContext<T>> handler) {
            this.encoder = encoder;
            this.decoder = decoder;
            this.handler = handler;
        }

        void registerServer(ResourceLocation channel) {
            ServerPlayNetworking.registerGlobalReceiver(channel, (server, player, handler, buf, responseSender) -> {
                T message = decoder.apply(buf);
                MessageContext<T> ctx = new MessageContext<>(message, server::execute, player, false);
                server.execute(() -> this.handler.accept(ctx));
            });
        }

        void registerClient(ResourceLocation channel) {
            ClientPlayNetworking.registerGlobalReceiver(channel, (client, handler, buf, responseSender) -> {
                T message = decoder.apply(buf);
                MessageContext<T> ctx = new MessageContext<>(message, client::execute, null, true);
                client.execute(() -> this.handler.accept(ctx));
            });
        }
    }
}
