package cn.tofucat.donkey.config;

// ==================== ConfigManager.java ====================

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.util.Properties;
import java.util.regex.Pattern;

/**
 * 配置加载器 - 专门负责扫描和加载 properties 配置文件
 * 职责单一：扫描 -> 加载 -> 存入 Config
 */
public class ConfigManager {
    private static final String DEFAULT_PREFIX = "config";
    private static String filePrefix = DEFAULT_PREFIX;
    private static boolean initialized = false;

    public static void init() {
        init(DEFAULT_PREFIX);
    }

    /**
     * 使用默认前缀 "config" 初始化
     * 扫描路径下所有 config_*.properties 文件
     */
    public static void init(String internalPath) {
        init(internalPath, DEFAULT_PREFIX);
    }

    /**
     * 使用自定义前缀初始化
     * 扫描路径下所有 {prefix}_*.properties 文件
     *
     * @param internalPath 资源目录路径，如 "config/" 或 ""
     * @param prefix       文件名前缀，如 "app" 会匹配 app_*.properties
     */
    public static void init(String internalPath, String prefix) {
        if (initialized) {
            Gdx.app.debug("ConfigManager", "已初始化，忽略重复调用");
            return;
        }

        if (prefix == null || prefix.trim().isEmpty()) {
            Gdx.app.error("ConfigManager", "文件前缀不能为空，使用默认前缀: " + DEFAULT_PREFIX);
            filePrefix = DEFAULT_PREFIX;
        } else {
            filePrefix = prefix.trim();
        }

        String fullPath = internalPath != null ? internalPath : "";
        if (!fullPath.isEmpty() && !fullPath.endsWith("/")) {
            fullPath += "/";
        }

        // 清空旧配置
        Config.clear();

        // 加载配置文件
        int count = loadConfigs(fullPath);
        initialized = true;

        Gdx.app.debug("ConfigManager", String.format(
            "初始化完成 | 前缀: %s | 路径: %s | 加载配置项: %d",
            filePrefix, fullPath, count
        ));
    }

    /**
     * 扫描并加载所有匹配的配置文件
     *
     * @return 加载的配置项总数
     */
    private static int loadConfigs(String path) {
        FileHandle dir = Gdx.files.internal(path);

        // 匹配模式：前缀_任意内容.properties
        Pattern pattern = Pattern.compile("^" + Pattern.quote(filePrefix) + "_[^.]+?\\.properties$");
        int totalCount = 0;
        int fileCount = 0;

        for (FileHandle file : dir.list()) {
            String fileName = file.name();
            if (pattern.matcher(fileName).matches()) {
                int count = loadProperties(file);
                if (count > 0) {
                    totalCount += count;
                    fileCount++;
                    Gdx.app.log("ConfigManager", "加载: " + fileName + " (" + count + " 项)");
                }
            }
        }

        // 同时加载基础配置文件：前缀.properties
        FileHandle baseFile = Gdx.files.internal(path + filePrefix + ".properties");
        if (baseFile.exists()) {
            int count = loadProperties(baseFile);
            if (count > 0) {
                totalCount += count;
                fileCount++;
                Gdx.app.log("ConfigManager", "加载: " + filePrefix + ".properties (" + count + " 项)");
            }
        }

        Gdx.app.log("ConfigManager", "共加载 " + fileCount + " 个配置文件");
        return totalCount;
    }

    /**
     * 加载单个 properties 文件到 Config
     *
     * @return 加载的配置项数量
     */
    private static int loadProperties(FileHandle file) {
        Properties props = new Properties();
        try {
            props.load(file.read());
            int count = 0;
            for (String key : props.stringPropertyNames()) {
                String value = props.getProperty(key);
                // 直接存入 Config，如果 key 重复会覆盖
                Config.put(key, value);
                count++;
            }
            return count;
        } catch (Exception e) {
            Gdx.app.error("ConfigManager", "加载失败: " + file.name(), e);
            return 0;
        }
    }

    /**
     * 重新加载配置（热更新）
     */
    public static void reload(String internalPath, String prefix) {
        initialized = false;
        Config.clear();
        init(internalPath, prefix);
    }

    /**
     * 重新加载配置（使用之前的路径和前缀）
     */
    public static void reload() {
        Gdx.app.log("ConfigManager", "reload() 需要重新指定路径和前缀，请使用 reload(path, prefix)");
    }

    /**
     * 检查是否已初始化
     */
    public static boolean isInitialized() {
        return initialized;
    }

    /**
     * 获取当前文件前缀
     */
    public static String getFilePrefix() {
        return filePrefix;
    }
}
