package com.avalon.base.client;

import com.avalon.base.gui.panel.PanelDemoScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.Commands;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;

/**
 * AvalonBase NeoForge 客户端能力。目前仅注册 {@code /avalonbase-demo} 客户端命令。
 *
 * <p>仅应在上层 {@code AvalonBaseMod} 通过 Dist 判定到客户端时调用，
 * 避免在专用服务器上加载客户端 {@link PanelDemoScreen} 类。</p>
 */
public final class AvalonBaseClientMod {

    private AvalonBaseClientMod() {
    }

    /** 在游戏总线上挂载客户端命令注册监听（{@code RegisterClientCommandsEvent} 仅客户端触发）。 */
    public static void registerCommands(IEventBus gameBus) {
        gameBus.addListener(AvalonBaseClientMod::registerClientCommands);
    }

    private static void registerClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("avalonbase-demo").executes(context -> {
            Minecraft.getInstance().setScreenAndShow(new PanelDemoScreen());
            return 1;
        }));
    }
}