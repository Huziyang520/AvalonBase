package com.avalon.base.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Forge 网络实现，封装 SimpleChannel，实现 Common 的 INetworkHandler 抽象。
 *
 * <p>为避免多个下游模组（如 OnlyTP、AuthCmd）共享同一个 Forge SimpleChannel 导致
 * 消息 id / 消息类型映射互相冲突（ClassCastException），这里按通道的 namespace
 * （即下游模组的 mod_id）为每个模组维护<b>独立</b>的 SimpleChannel 与独立的消息 id 计数器。
 *
 * <p>业务模组调用 {@link #registerMessage} 时传入形如 {@code onlytp:update} /
 * {@code authcmd:update} 的通道，namespace 即对应其 mod_id，从而各自注册互不干扰。
 */
public class ForgeNetworkHandler implements INetworkHandler {

    public static final ForgeNetworkHandler INSTANCE = new ForgeNetworkHandler();

    private static final String PROTOCOL_VERSION = "1";

    /** 每个下游模组的独立通道：key = mod_id（通道 namespace） */
    private final Map<String, SimpleChannel> channels = new ConcurrentHashMap<>();
    /** 每个模组通道的消息 id 计数器：key = mod_id */
    private final Map<String, Integer> nextIds = new ConcurrentHashMap<>();

    private ForgeNetworkHandler() {
    }

    private String modIdOf(ResourceLocation channel) {
        return channel.getNamespace();
    }

    private SimpleChannel getOrCreateChannel(String modId) {
        return channels.computeIfAbsent(modId, id -> NetworkRegistry.newSimpleChannel(
                new ResourceLocation(id, "main"),
                () -> PROTOCOL_VERSION,
                PROTOCOL_VERSION::equals,
                PROTOCOL_VERSION::equals
        ));
    }

    private int allocateId(String modId) {
        int id = nextIds.getOrDefault(modId, 0);
        nextIds.put(modId, id + 1);
        return id;
    }

    @Override
    public <T> void registerMessage(ResourceLocation channel, Class<T> clazz,
                                    BiConsumer<T, FriendlyByteBuf> encoder,
                                    Function<FriendlyByteBuf, T> decoder,
                                    Consumer<INetworkHandler.MessageContext<T>> handler) {
        String modId = modIdOf(channel);
        SimpleChannel sc = getOrCreateChannel(modId);
        int id = allocateId(modId);
        sc.registerMessage(id, clazz, encoder, decoder,
                (message, ctx) -> {
                    NetworkEvent.Context forgeCtx = ctx.get();
                    ServerPlayer sender = forgeCtx.getSender();
                    INetworkHandler.MessageContext<T> wrapped = new INetworkHandler.MessageContext<>(
                            message,
                            forgeCtx::enqueueWork,
                            sender,
                            forgeCtx.getDirection().getReceptionSide().isClient()
                    );
                    forgeCtx.enqueueWork(() -> handler.accept(wrapped));
                    forgeCtx.setPacketHandled(true);
                });
    }

    @Override
    public <T> void sendToServer(ResourceLocation channel, T message) {
        getOrCreateChannel(modIdOf(channel)).sendToServer(message);
    }

    @Override
    public <T> void sendToAll(MinecraftServer server, ResourceLocation channel, T message) {
        getOrCreateChannel(modIdOf(channel)).send(PacketDistributor.ALL.noArg(), message);
    }

    @Override
    public <T> void sendToPlayer(ServerPlayer player, ResourceLocation channel, T message) {
        getOrCreateChannel(modIdOf(channel)).send(PacketDistributor.PLAYER.with(() -> player), message);
    }
}
