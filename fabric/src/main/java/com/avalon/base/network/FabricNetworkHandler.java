package com.avalon.base.network;

import com.avalon.base.network.INetworkHandler.MessageContext;

import io.netty.buffer.ByteBuf;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Fabric 网络实现，封装 Fabric Networking (Payload) API，实现 Common 的 INetworkHandler 抽象。
 *
 * <p>Fabric 1.20.5+ 起 Networking API 从 (ResourceLocation, FriendlyByteBuf) 重写为
 * CustomPacketPayload + PayloadTypeRegistry 模型。这里用一个统一的集合型 payload 承载
 * channel id 与消息原始字节，在接收端按 channel 路由到对应编解码/处理器，从而保持
 * Common 的 ResourceLocation + encoder/decoder 抽象不变。
 *
 * <p>服务端与客户端接收器需分别在对应环境注册，因此 {@link #registerMessage} 仅记录信息，
 * 实际注册由 {@link #registerServer()}（ModInitializer.onInitialize）与
 * {@link #registerClient()}（ClientModInitializer.onInitializeClient）统一执行。
 * 两方向共用同一 payload 类型，接收器注册幂等，重复调用无副作用。
 */
public class FabricNetworkHandler implements INetworkHandler {

    public static final FabricNetworkHandler INSTANCE = new FabricNetworkHandler();

    private static final ResourceLocation PAYLOAD_ID = new ResourceLocation("avalonbase", "network");

    // 注册表：channel -> 编解码与处理回调
    private static final Map<ResourceLocation, Registration<?>> REGISTRY = new ConcurrentHashMap<>();

    private static boolean receiverRegistered = false;

    private FabricNetworkHandler() {
    }

    /** 统一集合型 payload：channel + 消息原始字节。 */
    public record ChannelPayload(ResourceLocation channel, byte[] data) implements CustomPacketPayload {
        public static final Type<ChannelPayload> TYPE = new Type<>(PAYLOAD_ID);
        public static final StreamCodec<ByteBuf, ChannelPayload> STREAM_CODEC = StreamCodec.composite(
                ResourceLocation.STREAM_CODEC, ChannelPayload::channel,
                ByteBufCodecs.BYTE_ARRAY, ChannelPayload::data,
                ChannelPayload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    /** 服务端注册：注册 payload 类型与全局接收器（幂等，仅执行一次）。 */
    public static synchronized void registerServer() {
        if (receiverRegistered) return;
        PayloadTypeRegistry.playC2S().register(ChannelPayload.TYPE, ChannelPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(ChannelPayload.TYPE, ChannelPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(ChannelPayload.TYPE, (payload, ctx) -> {
            Registration<?> reg = REGISTRY.get(payload.channel());
            if (reg == null) return;
            reg.acceptServer(payload, ctx.server()::execute, ctx.player());
        });
        ClientPlayNetworking.registerGlobalReceiver(ChannelPayload.TYPE, (payload, ctx) -> {
            Registration<?> reg = REGISTRY.get(payload.channel());
            if (reg == null) return;
            reg.acceptClient(payload, ctx.client()::execute);
        });
        receiverRegistered = true;
    }

    /** 客户端注册：与 {@link #registerServer()} 同一套注册，幂等。 */
    public static synchronized void registerClient() {
        registerServer();
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
        ClientPlayNetworking.send(wrap(channel, require(channel), message));
    }

    @Override
    public <T> void sendToAll(MinecraftServer server, ResourceLocation channel, T message) {
        ChannelPayload payload = wrap(channel, require(channel), message);
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    @Override
    public <T> void sendToPlayer(ServerPlayer player, ResourceLocation channel, T message) {
        ServerPlayNetworking.send(player, wrap(channel, require(channel), message));
    }

    private <T> Registration<T> require(ResourceLocation channel) {
        @SuppressWarnings("unchecked")
        Registration<T> reg = (Registration<T>) REGISTRY.get(channel);
        if (reg == null) throw new IllegalStateException("Channel not registered: " + channel);
        return reg;
    }

    private static <T> ChannelPayload wrap(ResourceLocation channel, Registration<T> reg, T message) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        reg.encoder.accept(message, buf);
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        return new ChannelPayload(channel, bytes);
    }

    /** 单条通道的注册信息（编码/解码/业务处理回调）。 */
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

        void acceptServer(ChannelPayload payload, Consumer<Runnable> executor, ServerPlayer sender) {
            T message = decode(payload);
            handler.accept(new MessageContext<>(message, executor, sender, false));
        }

        void acceptClient(ChannelPayload payload, Consumer<Runnable> executor) {
            T message = decode(payload);
            handler.accept(new MessageContext<>(message, executor, null, true));
        }

        @SuppressWarnings("unchecked")
        private T decode(ChannelPayload payload) {
            FriendlyByteBuf buf = PacketByteBufs.create();
            buf.writeBytes(payload.data());
            return decoder.apply(buf);
        }
    }
}
