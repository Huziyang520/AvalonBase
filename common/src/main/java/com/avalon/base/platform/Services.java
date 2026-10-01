package com.avalon.base.platform;

import com.avalon.base.Constants;
import com.avalon.base.platform.services.IPlatformHelper;

import java.util.ServiceLoader;

/**
 * 平台服务加载器。使用 Java 内置 ServiceLoader 在 Forge/Fabric 之间切换实现。
 * 各平台在 META-INF/services 下提供实现类的全限定名。
 */
public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

    public static <T> T load(Class<T> clazz) {
        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        Constants.LOG.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
