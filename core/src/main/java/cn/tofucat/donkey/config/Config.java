package cn.tofucat.donkey.config;

import com.badlogic.gdx.Gdx;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 全局配置存储 - 提供线程安全的配置访问
 * 所有配置项存储在此，通过静态方法全局访问
 */
public class Config {
    // 使用 ConcurrentHashMap 保证线程安全
    private static final ConcurrentMap<String, String> CONFIG_MAP = new ConcurrentHashMap<>();

    /**
     * 存入配置项（内部使用）
     */
    static void put(String key, String value) {
        if (key != null && value != null) {
            CONFIG_MAP.put(key, value);
        }
    }

    /**
     * 清空所有配置（内部使用）
     */
    static void clear() {
        CONFIG_MAP.clear();
    }

    // ========== 公共 API ==========

    /**
     * 获取字符串配置
     */
    public static String getString(String key) {
        return getString(key, null);
    }

    /**
     * 获取字符串配置（带默认值）
     */
    public static String getString(String key, String defaultValue) {
        checkInitialized();
        String value = CONFIG_MAP.get(key);
        return value != null ? value : defaultValue;
    }

    /**
     * 获取整数配置
     */
    public static int getInt(String key) {
        return getInt(key, 0);
    }

    public static int getInt(String key, int defaultValue) {
        String value = getString(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            Gdx.app.error("Config", "配置项 " + key + " 不是有效的整数: " + value);
            return defaultValue;
        }
    }

    /**
     * 获取布尔配置（支持 true/false, 1/0, yes/no, on/off）
     */
    public static boolean getBoolean(String key) {
        return getBoolean(key, false);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = getString(key);
        if (value == null) {
            return defaultValue;
        }
        value = value.trim().toLowerCase();
        return value.equals("true") || value.equals("1") ||
            value.equals("yes") || value.equals("on");
    }

    /**
     * 获取浮点数配置
     */
    public static float getFloat(String key) {
        return getFloat(key, 0f);
    }

    public static float getFloat(String key, float defaultValue) {
        String value = getString(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Float.parseFloat(value.trim());
        } catch (NumberFormatException e) {
            Gdx.app.error("Config", "配置项 " + key + " 不是有效的浮点数: " + value);
            return defaultValue;
        }
    }

    /**
     * 获取长整数配置
     */
    public static long getLong(String key) {
        return getLong(key, 0L);
    }

    public static long getLong(String key, long defaultValue) {
        String value = getString(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            Gdx.app.error("Config", "配置项 " + key + " 不是有效的长整数: " + value);
            return defaultValue;
        }
    }

    /**
     * 获取 double 配置
     */
    public static double getDouble(String key) {
        return getDouble(key, 0.0);
    }

    public static double getDouble(String key, double defaultValue) {
        String value = getString(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            Gdx.app.error("Config", "配置项 " + key + " 不是有效的 double: " + value);
            return defaultValue;
        }
    }

    /**
     * 检查配置是否存在
     */
    public static boolean hasKey(String key) {
        checkInitialized();
        return CONFIG_MAP.containsKey(key);
    }

    /**
     * 获取所有配置项数量
     */
    public static int size() {
        checkInitialized();
        return CONFIG_MAP.size();
    }

    /**
     * 打印所有配置（调试用）
     */
    public static void printAll() {
        checkInitialized();
        Gdx.app.debug("Config", "========== 所有配置项 (" + CONFIG_MAP.size() + ") ==========");
        for (ConcurrentMap.Entry<String, String> entry : CONFIG_MAP.entrySet()) {
            Gdx.app.debug("Config", entry.getKey() + " = " + entry.getValue());
        }
        Gdx.app.debug("Config", "====================================================");
    }

    /**
     * 检查是否已初始化
     */
    private static void checkInitialized() {
        if (!ConfigManager.isInitialized()) {
            throw new IllegalStateException(
                "Config 未初始化，请先调用 ConfigManager.init(path, prefix)"
            );
        }
    }
}
