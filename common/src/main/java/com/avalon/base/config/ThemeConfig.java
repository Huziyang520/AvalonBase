package com.avalon.base.config;

import com.avalon.base.Constants;
import com.avalon.base.gui.Themes;
import com.avalon.base.gui.theme.ThemePreset;
import com.electronwill.nightconfig.core.Config;

import java.nio.file.Path;

/**
 * 主题预设的持久化。
 *
 * <p>把当前生效的 {@link ThemePreset} 名称写入 AvalonBase 自己的 TOML 配置，
 * 下次启动时自动恢复。底层复用 {@link AvalonToml}。
 *
 * <p><b>配置路径注入：</b>common 层不直接依赖 Forge / Fabric 的加载器 API，而是由平台侧
 * 在初始化时调用 {@link #init(Path)} 注入配置目录。未注入时退化为<b>仅内存模式</b>
 * （不读盘、不写盘，但切换主题仍即时生效）。
 *
 * <p>典型用法：
 * <pre>{@code
 * // 平台入口（forge/fabric）启动时：
 * ThemeConfig.init(FMLPaths.CONFIGDIR.get());   // 或 FabricLoader.getInstance().getConfigDir()
 * ThemeConfig.load();                            // 读回上次保存的预设
 *
 * // 切换预设时（如 ThemePresetPicker 点击后）：
 * Themes.setPreset(next);
 * ThemeConfig.save();                            // 落盘
 * }</pre>
 */
public final class ThemeConfig {

    private static final String FILE_NAME = "avalonbase.toml";
    private static final String KEY_PRESET = "preset";

    private static volatile AvalonToml toml;

    private ThemeConfig() {
    }

    /** 是否已完成路径注入。 */
    public static boolean isInitialized() {
        return toml != null;
    }

    /**
     * 注入配置目录（由 forge/fabric 平台入口调用）。仅首次生效。
     *
     * @param configDir 平台配置目录（如 {@code FMLPaths.CONFIGDIR.get()}）
     */
    public static synchronized void init(Path configDir) {
        if (toml == null && configDir != null) {
            toml = new AvalonToml(Constants.MOD_ID, configDir.resolve(FILE_NAME));
        }
    }

    /**
     * 从磁盘读回上次保存的预设并应用。未初始化时退化为内存模式（保持当前预设不变）。
     */
    public static synchronized void load() {
        if (toml == null) return;
        Config cfg = toml.read();
        String name = cfg.getOrElse(KEY_PRESET, "");
        ThemePreset preset = ThemePreset.byName(name);
        if (!preset.name().equalsIgnoreCase(String.valueOf(name))) {
            // 解析失败（未知值），回退默认并忽略，避免覆盖用户手写的旧值
            if (name != null && !name.isEmpty()) {
                Constants.LOG.warn("[{}] Unknown theme preset '{}', falling back to {}",
                        Constants.MOD_ID, name, ThemePreset.ENDER_PURPLE.name());
            }
        }
        Themes.setPreset(preset);
    }

    /**
     * 将当前预设写入磁盘。未初始化时仅内存生效，不写盘。
     */
    public static synchronized void save() {
        if (toml == null) return;
        ThemePreset preset = Themes.currentPreset();
        if (preset == null) return; // 自定义配色不持久化（预设名未知）
        Config cfg = Config.inMemory();
        cfg.set(KEY_PRESET, preset.name().toLowerCase(java.util.Locale.ROOT));
        toml.write(cfg, KEY_PRESET,
                "# AvalonBase 主题预设。可选值（大小写不敏感）：\n"
                + "#   ender_purple（末影紫，默认）/ ocean_blue / emerald_green / ember_red / porcelain\n");
    }
}
