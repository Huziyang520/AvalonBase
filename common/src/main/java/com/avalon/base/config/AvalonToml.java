package com.avalon.base.config;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 通用的 TOML 配置读写框架，与具体业务解耦。
 *
 * <p>提供能力：
 * <ul>
 *   <li>读取/写入 TOML 文件</li>
 *   <li>子表（section）的字符串列表读写（统一子键为 {@code blacklist}）</li>
 *   <li>基于文件修改时间戳的热加载判定</li>
 *   <li>写盘时为指定小节注入解释性注释（TOML 解析时自动忽略）</li>
 * </ul>
 *
 * <p>业务模组将配置类设计为静态字段持有本类即可复用这些能力。
 */
public class AvalonToml {
    private static final Logger LOGGER = LogUtils.getLogger();

    private final Path path;
    private final String modId;
    private volatile long lastModified = -1L;

    public AvalonToml(String modId, Path path) {
        this.modId = modId;
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    public String getModId() {
        return modId;
    }

    /**
     * 确保配置文件存在；不存在则写默认（需由调用方先用 put/set 组装好默认值）。
     */
    public void ensureExists(Config defaultConfig) {
        try {
            Files.createDirectories(path.getParent());
            if (!Files.exists(path) || Files.size(path) == 0) {
                write(defaultConfig);
            }
        } catch (IOException e) {
            LOGGER.error("[{}] Failed to create config dir", modId, e);
        }
    }

    /**
     * 读取配置文件为 Config。
     */
    public Config read() {
        try {
            String text = Files.readString(path);
            lastModified = Files.getLastModifiedTime(path).toMillis();
            return new TomlParser().parse(text);
        } catch (IOException e) {
            LOGGER.error("[{}] Failed to read config, using defaults", modId, e);
            return Config.inMemory();
        }
    }

    /**
     * 读取并同时记录时间戳（供热加载比对）。
     */
    public Config readTracked() {
        try {
            lastModified = Files.getLastModifiedTime(path).toMillis();
        } catch (IOException ignored) {
        }
        return read();
    }

    /**
     * 写入配置到文件。
     */
    public void write(Config cfg) {
        try {
            String toml = new TomlWriter().writeToString(cfg);
            Files.writeString(path, toml);
        } catch (IOException e) {
            LOGGER.error("[{}] Failed to write config", modId, e);
        }
    }

    /**
     * 写入配置并在指定小节前插入注释。
     */
    public void write(Config cfg, String sectionMarker, String comment) {
        try {
            String toml = new TomlWriter().writeToString(cfg);
            toml = insertComment(toml, sectionMarker, comment);
            Files.writeString(path, toml);
        } catch (IOException e) {
            LOGGER.error("[{}] Failed to write config", modId, e);
        }
    }

    /**
     * 判断配置文件是否被外部修改（时间戳变化）。
     */
    public boolean isChanged() {
        try {
            return Files.getLastModifiedTime(path).toMillis() != lastModified;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 读取指定小节的字符串列表（统一取子键 blacklist）。
     */
    public static List<String> getSubList(Config cfg, String section) {
        Config sub = cfg.get(section);
        if (sub == null) return new ArrayList<>();
        return new ArrayList<>(sub.getOrElse("blacklist", Collections.emptyList()));
    }

    /**
     * 写入指定小节的字符串列表（统一用子键 blacklist）。
     */
    public static void setSubList(Config cfg, String section, List<String> list) {
        Config sub = Config.inMemory();
        sub.set("blacklist", list);
        cfg.set(section, sub);
    }

    /**
     * 在 TOML 字符串的指定小节标记前插入注释。
     */
    public static String insertComment(String toml, String sectionMarker, String comment) {
        int idx = toml.indexOf(sectionMarker);
        if (idx < 0) return toml; // 未找到则原样返回
        return toml.substring(0, idx) + comment + toml.substring(idx);
    }
}
