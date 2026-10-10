package com.avalon.base;

import com.avalon.base.client.AvalonBaseClientMod;
import com.avalon.base.network.AvalonNetwork;
import com.avalon.base.network.NeoForgeNetworkHandler;
import com.mojang.logging.LogUtils;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

/**
 * AvalonBase NeoForge 入口。作为支持库，本模组仅初始化网络访问点，
 * 不注册任何业务事件；业务能力由依赖它的业务模组各自挂载。
 */
@Mod(Constants.MOD_ID)
public class AvalonBaseMod {
    private static final Logger LOGGER = LogUtils.getLogger();

    public AvalonBaseMod(IEventBus modEventBus, Dist dist) {
        // 注册 payload 类型：NeoForge 需在启动时通过 RegisterPayloadHandlersEvent 一次性注册。
        // 由于注册发生在各模组 commonSetup 的并行初始化之前，这里先于注入 handler 完成监听注册。
        modEventBus.addListener(NeoForgeNetworkHandler::onRegisterPayloadHandlers);
        // 尽早注入网络实现，避免下游模组在并行初始化时读取到未注入的 handler。
        AvalonNetwork.set(NeoForgeNetworkHandler.INSTANCE);
        // 客户端能力（demo 命令）仅在客户端挂载，避免专用服务器加载客户端类。
        if (dist.isClient()) {
            AvalonBaseClientMod.registerCommands(NeoForge.EVENT_BUS);
        }
        LOGGER.info("AvalonBase loaded!");
    }
}
