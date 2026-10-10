package com.avalon.base.network;

import com.avalon.base.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * NeoForge 网络实现。基于 NeoForge 的 Payload（CustomPacketPayload）系统，实现 Common 的
 * {@link INetworkHandler} 抽象。
 *
 * <p>与 Forge 不同，NeoForge 的 payload 在启动时通过 {@link RegisterPayloadHandlersEvent}
 * 一次性注册（由 {@code RegisterPayloadHandlersEvent} 携带的 {@link PayloadRegistrar}
 * 完成），每个 payload 类型由其 TYPE 的 id 唯一标识，不存在「同一通道注册冲突」问题。
 *
 * <p>本实现将所有业务通道统一收敛到<b>单个</b> {@link AvalonPayload} 类型上，通道 id
 * 与消息数据编码在 payload 内部；收发时再根据通道 id 查表路由到各下游模组对应的编解码器
 * 与处理回调。这样既满足 NeoForge 的注册模型，又保持与 Forge/Fabric 一致的「按模组独立」
 * 通道语义（key = 通道 namespace，即下游模组的 mod_id）。
 */
public class NeoForgeNetworkHandler implements INetworkHandler {

    public static final NeoForgeNetworkHandler INSTANCE = new NeoForgeNetworkHandler();

    private static final String PROTOCOL_VERSION = "1";

    /** 通道 id（ResourceLocation）→ 该通道的编解码与处理回调注册信息 */
    private final Map<ResourceLocation, Registration<?>> registrations = new ConcurrentHashMap<>();

    private NeoForgeNetworkHandler() {
    }

    /**
     * NeoForge 需要在启动时通过 {@code RegisterPayloadHandlersEvent} 注册 payload 类型。
     * 由 NeoForge 入口在 mod 事件总线上监听本事件并调用。
     */
    public static void onRegisterPayloadHandlers(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playBidirectional(
                AvalonPayload.TYPE,
                AvalonPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(NeoForgeNetworkHandler::handle, NeoForgeNetworkHandler::handle)
        );
    }

    @Override
    public <T> void registerMessage(ResourceLocation channel, Class<T> clazz,
                                    BiConsumer<T, FriendlyByteBuf> encoder,
                                    Function<FriendlyByteBuf, T> decoder,
                                    Consumer<INetworkHandler.MessageContext<T>> handler) {
        registrations.put(channel, new Registration<>(encoder, decoder, handler));
    }

    @Override
    public <T> void sendToServer(ResourceLocation channel, T message) {
        PacketDistributor.sendToServer(wrap(channel, message));
    }

    @Override
    public <T> void sendToAll(MinecraftServer server, ResourceLocation channel, T message) {
        PacketDistributor.sendToAllPlayers(wrap(channel, message));
    }

    @Override
    public <T> void sendToPlayer(ServerPlayer player, ResourceLocation channel, T message) {
        PacketDistributor.sendToPlayer(player, wrap(channel, message));
    }

    @SuppressWarnings("unchecked")
    private <T> AvalonPayload wrap(ResourceLocation channel, T message) {
        Registration<T> registration = (Registration<T>) registrations.get(channel);
        if (registration == null) {
            throw new IllegalStateException("No payload registered for channel: " + channel);
        }
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        registration.encoder().accept(message, buf);
        byte[] data = new byte[buf.readableBytes()];
        buf.readBytes(data);
        buf.release();
        return new AvalonPayload(channel, data);
    }

    /**
     * 收发双端共用同一分发逻辑：按 payload 内携带的通道 id 查表，解码出具体消息并派发。
     */
    private static void handle(final AvalonPayload payload, final IPayloadContext context) {
        Registration<?> registration = INSTANCE.registrations.get(payload.channel());
        if (registration == null) {
            return;
        }
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()));
        dispatch(registration, buf, context);
    }

    @SuppressWarnings("unchecked")
    private static <T> void dispatch(Registration<T> registration, FriendlyByteBuf buf, IPayloadContext context) {
        T message = registration.decoder().apply(buf);
        ServerPlayer sender = context.player() instanceof ServerPlayer sp ? sp : null;
        boolean clientSide = context.flow().isClientbound();
        INetworkHandler.MessageContext<T> wrapped =
                new INetworkHandler.MessageContext<>(message, context::enqueueWork, sender, clientSide);
        context.enqueueWork(() -> registration.handler().accept(wrapped));
    }

    private record Registration<T>(
            BiConsumer<T, FriendlyByteBuf> encoder,
            Function<FriendlyByteBuf, T> decoder,
            Consumer<INetworkHandler.MessageContext<T>> handler
    ) {
    }

    /**
     * 所有业务通道共享的载体 payload：携带通道 id 与已编码的消息字节。
     */
    public record AvalonPayload(ResourceLocation channel, byte[] data) implements CustomPacketPayload {

        public static final CustomPacketPayload.Type<AvalonPayload> TYPE =
                new CustomPacketPayload.Type<>(new ResourceLocation(Constants.MOD_ID, "main"));

        public static final StreamCodec<ByteBuf, AvalonPayload> STREAM_CODEC = StreamCodec.of(
                (buf, payload) -> {
                    ResourceLocation.STREAM_CODEC.encode(buf, payload.channel());
                    buf.writeBytes(payload.data());
                },
                buf -> {
                    ResourceLocation channel = ResourceLocation.STREAM_CODEC.decode(buf);
                    byte[] data = new byte[buf.readableBytes()];
                    buf.readBytes(data);
                    return new AvalonPayload(channel, data);
                }
        );

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }
}
