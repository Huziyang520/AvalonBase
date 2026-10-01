package com.avalon.base;

import com.avalon.base.network.AvalonNetwork;
import com.avalon.base.network.ForgeNetworkHandler;
import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * AvalonBase Forge 入口。作为支持库，本模组仅初始化网络访问点，
 * 不注册任何业务事件；业务能力由依赖它的业务模组各自挂载。
 */
@Mod(Constants.MOD_ID)
public class AvalonBaseMod {
    private static final Logger LOGGER = LogUtils.getLogger();

    public AvalonBaseMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        AvalonNetwork.set(ForgeNetworkHandler.INSTANCE);
        LOGGER.info("AvalonBase loaded!");
    }
}
