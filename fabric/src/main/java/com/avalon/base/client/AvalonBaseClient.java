package com.avalon.base.client;

import com.avalon.base.gui.panel.PanelDemoScreen;
import com.avalon.base.network.FabricNetworkHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.minecraft.client.Minecraft;

/**
 * AvalonBase Fabric 客户端入口。注册客户端网络接收器与 demo 命令。
 */
public class AvalonBaseClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricNetworkHandler.registerClient();
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(ClientCommandManager.literal("avalonbase-demo").executes(context -> {
                    Minecraft.getInstance().setScreen(new PanelDemoScreen());
                    return 1;
                })));
    }
}
