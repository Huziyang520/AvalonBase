package com.avalon.base.platform.services;

public interface IPlatformHelper {

    /**
     * 获取当前平台名称（Forge / Fabric）。
     */
    String getPlatformName();

    /**
     * 判断指定 mod 是否已加载。
     */
    boolean isModLoaded(String modId);

    /**
     * 是否处于开发环境。
     */
    boolean isDevelopmentEnvironment();

    /**
     * 获取环境类型名称。
     */
    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }
}
