package cn.tofucat.donkey.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * 全局 JSON 配置管理器
 * 使用 JsonReader 解析 JSON，避免 SerializationException
 */
public class JsonConfigLoader {

    private static final JsonReader jsonReader = new JsonReader();
    private static final ObjectMap<String, JsonValue> cache = new ObjectMap<>();
    private static final ObjectMap<String, Long> cacheTime = new ObjectMap<>();
    private static boolean debug = false;

    /**
     * 加载 JSON 文件，返回 JsonConfig 包装类
     */
    public static JsonConfig load(String path) {
        return load(path, false);
    }

    /**
     * 强制重新加载（忽略缓存）
     */
    public static JsonConfig reload(String path) {
        return load(path, true);
    }

    private static JsonConfig load(String path, boolean forceReload) {
        if (!forceReload && cache.containsKey(path)) {
            if (debug) {
                Gdx.app.debug("JsonConfigLoader", "使用缓存: " + path);
            }
            return new JsonConfig(cache.get(path), path);
        }

        try {
            FileHandle file = Gdx.files.internal(path);
            if (!file.exists()) {
                Gdx.app.error("JsonConfigLoader", "文件不存在: " + path);
                return null;
            }

            long startTime = System.currentTimeMillis();
            JsonValue root = jsonReader.parse(file);
            long loadTime = System.currentTimeMillis() - startTime;

            cache.put(path, root);
            cacheTime.put(path, loadTime);

            if (debug) {
                Gdx.app.debug("JsonConfigLoader", String.format(
                    "加载完成: %s (耗时: %dms, 大小: %d bytes)",
                    path, loadTime, file.length()
                ));
            }

            return new JsonConfig(root, path);

        } catch (Exception e) {
            Gdx.app.error("JsonConfigLoader", "加载失败: " + path, e);
            return null;
        }
    }

    public static boolean isLoaded(String path) {
        return cache.containsKey(path);
    }

    public static void clearCache(String path) {
        cache.remove(path);
        cacheTime.remove(path);
        if (debug) {
            Gdx.app.debug("JsonConfigLoader", "清除缓存: " + path);
        }
    }

    public static void clearAllCache() {
        cache.clear();
        cacheTime.clear();
        if (debug) {
            Gdx.app.debug("JsonConfigLoader", "清除所有缓存");
        }
    }

    public static String getCacheStats() {
        return String.format("缓存文件数: %d", cache.size);
    }

    public static void setDebug(boolean enabled) {
        debug = enabled;
    }

    public static void printCacheInfo() {
        Gdx.app.debug("JsonConfigLoader", "========== 缓存信息 ==========");
        for (ObjectMap.Entry<String, JsonValue> entry : cache) {
            long loadTime = cacheTime.get(entry.key, 0L);
            Gdx.app.debug("JsonConfigLoader", String.format("  %s (耗时: %dms)", entry.key, loadTime));
        }
        Gdx.app.debug("JsonConfigLoader", "================================");
    }
}
