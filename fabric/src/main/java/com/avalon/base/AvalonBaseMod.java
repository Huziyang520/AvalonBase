package com.avalon.base;

import com.avalon.base.network.AvalonNetwork;
import com.avalon.base.network.FabricNetworkHandler;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

/**
 * AvalonBase Fabric 入口。初始化网络访问点并注册服务端接收器。
 */
public class AvalonBaseMod implements ModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        AvalonNetwork.set(FabricNetworkHandler.INSTANCE);
        FabricNetworkHandler.registerServer();
        LOGGER.info("AvalonBase loaded!");
    }
}
