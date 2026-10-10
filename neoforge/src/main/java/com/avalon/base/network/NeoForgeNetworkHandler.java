package com.avalon.base.network;

import com.avalon.base.Constants;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class NeoForgeNetworkHandler implements INetworkHandler {

    public static final NeoForgeNetworkHandler INSTANCE = new NeoForgeNetworkHandler();

    private static final String PROTOCOL_VERSION = "1";

    private final Map<ResourceLocation, Registration<?>> registrations = new ConcurrentHashMap<>();

    private NeoForgeNetworkHandler() {
    }

    public static void onRegisterPayloadHandlers(final RegisterPayloadHandlerEvent event) {
        final IPayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.play(
                AvalonPayload.TYPE,
                AvalonPayload::new,
                (payload, context) -> handle(payload, context)
        );
    }

    @Override
    public <T> void registerMessage(ResourceLocation channel, Class<T> clazz,
                                    BiConsumer<T, FriendlyByteBuf> encoder,
                                    Function<FriendlyByteBuf, T> decoder,
                                    Consumer<MessageContext<T>> handler) {
        registrations.put(channel, new Registration<>(encoder, decoder, handler));
    }

    @Override
    public <T> void sendToServer(ResourceLocation channel, T message) {
        PacketDistributor.SERVER.noArg().send(wrap(channel, message));
    }

    @Override
    public <T> void sendToAll(MinecraftServer server, ResourceLocation channel, T message) {
        PacketDistributor.ALL.noArg().send(wrap(channel, message));
    }

    @Override
    public <T> void sendToPlayer(ServerPlayer player, ResourceLocation channel, T message) {
        PacketDistributor.PLAYER.with(player).send(wrap(channel, message));
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

    @SuppressWarnings("unchecked")
    private static void handle(final AvalonPayload payload, final IPayloadContext context) {
        Registration<?> registration = INSTANCE.registrations.get(payload.channel());
        if (registration == null) {
            return;
        }
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.wrappedBuffer(payload.data()));
        Registration<Object> typed = (Registration<Object>) registration;
        Object message = typed.decoder().apply(buf);
        ServerPlayer sender = context.player()
                .filter(ServerPlayer.class::isInstance)
                .map(ServerPlayer.class::cast)
                .orElse(null);
        boolean clientSide = context.flow() == PacketFlow.CLIENTBOUND;
        MessageContext<Object> wrapped =
                new MessageContext<>(message, context.workHandler()::execute, sender, clientSide);
        context.workHandler().execute(() -> typed.handler().accept(wrapped));
    }

    private record Registration<T>(
            BiConsumer<T, FriendlyByteBuf> encoder,
            Function<FriendlyByteBuf, T> decoder,
            Consumer<MessageContext<T>> handler
    ) {
    }

    public static final class AvalonPayload implements CustomPacketPayload {

        public static final ResourceLocation TYPE =
                new ResourceLocation(Constants.MOD_ID, "main");

        private final ResourceLocation channel;
        private final byte[] data;

        public AvalonPayload(ResourceLocation channel, byte[] data) {
            this.channel = channel;
            this.data = data.clone();
        }

        public AvalonPayload(FriendlyByteBuf buf) {
            this.channel = buf.readResourceLocation();
            byte[] incoming = new byte[buf.readableBytes()];
            buf.readBytes(incoming);
            this.data = incoming;
        }

        public ResourceLocation channel() {
            return channel;
        }

        public byte[] data() {
            return data.clone();
        }

        @Override
        public void write(FriendlyByteBuf buf) {
            buf.writeResourceLocation(channel);
            buf.writeBytes(data);
        }

        @Override
        public ResourceLocation id() {
            return TYPE;
        }
    }
}