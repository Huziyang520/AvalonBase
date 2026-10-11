package com.avalon.base;

import com.avalon.base.client.AvalonBaseClientMod;
import com.avalon.base.network.AvalonNetwork;
import com.avalon.base.network.ForgeNetworkHandler;
import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.slf4j.Logger;

/**
 * AvalonBase Forge 入口。作为支持库，本模组仅初始化网络访问点，
 * 不注册任何业务事件；业务能力由依赖它的业务模组各自挂载。
 */
@Mod(Constants.MOD_ID)
public class AvalonBaseMod {
    private static final Logger LOGGER = LogUtils.getLogger();

    public AvalonBaseMod() {
        // 尽早注入网络实现：Forge 的 FMLCommonSetupEvent 对各模组并行触发，
        // 业务模组的 commonSetup 可能先于 AvalonBase 的 commonSetup 执行。
        AvalonNetwork.set(ForgeNetworkHandler.INSTANCE);
        // 客户端能力（demo 命令）仅在客户端挂载，避免专用服务器加载客户端类。
        if (FMLEnvironment.dist == Dist.CLIENT) {
            AvalonBaseClientMod.registerCommands(MinecraftForge.EVENT_BUS);
        }
        LOGGER.info("AvalonBase loaded!");
    }
}
