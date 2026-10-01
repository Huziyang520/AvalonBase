package com.avalon.base.client;

import com.avalon.base.network.FabricNetworkHandler;
import net.fabricmc.api.ClientModInitializer;

/**
 * AvalonBase Fabric 客户端入口。注册客户端网络接收器。
 */
public class AvalonBaseClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricNetworkHandler.registerClient();
    }
}
