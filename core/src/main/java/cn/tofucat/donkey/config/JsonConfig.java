package cn.tofucat.donkey.config;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

/**
 * JSON 配置数据包装器
 * 支持：
 * 1. 路径访问（如 "items[0].name"）
 * 2. 类型安全的 get 方法（带异常处理）
 * 3. 转 Java Bean / LibGDX Array
 * 4. 内存释放
 */
public class JsonConfig {

    private final JsonValue root;
    private final String path;
    private boolean disposed = false;

    public JsonConfig(JsonValue root, String path) {
        this.root = root;
        this.path = path;
    }

    // ========== 路径访问核心 ==========

    /**
     * 通过路径获取 JsonValue
     * 支持格式: "items[0].name" 或 "items.0.name"
     */
    public JsonValue getValue(String keyPath) {
        checkDisposed();
        if (keyPath == null || keyPath.isEmpty()) {
            return root;
        }
        return getValueByPath(root, keyPath);
    }

    private JsonValue getValueByPath(JsonValue current, String keyPath) {
        String[] parts = keyPath.split("\\.");
        JsonValue result = current;

        for (String part : parts) {
            if (result == null) {
                return null;
            }

            int arrayIndex = -1;
            String key = part;

            if (part.endsWith("]")) {
                int bracketStart = part.indexOf('[');
                if (bracketStart != -1) {
                    key = part.substring(0, bracketStart);
                    String indexStr = part.substring(bracketStart + 1, part.length() - 1);
                    try {
                        arrayIndex = Integer.parseInt(indexStr);
                    } catch (NumberFormatException e) {
                        Gdx.app.error("JsonConfig", "无效的数组索引: " + indexStr);
                        return null;
                    }
                }
            }

            if (!key.isEmpty()) {
                result = result.get(key);
                if (result == null) {
                    return null;
                }
            }

            if (arrayIndex != -1) {
                if (!result.isArray()) {
                    Gdx.app.error("JsonConfig", key + " 不是数组类型");
                    return null;
                }
                result = result.get(arrayIndex);
            }
        }

        return result;
    }

    // ========== 基础类型取值 ==========

    public String getString(String keyPath) {
        return getString(keyPath, null);
    }

    public String getString(String keyPath, String defaultValue) {
        JsonValue value = getValue(keyPath);
        if (value == null) {
            return defaultValue;
        }
        try {
            return value.asString();
        } catch (Exception e) {
            Gdx.app.error("JsonConfig", String.format(
                "[%s] 无法将 %s 转为 String: %s", path, keyPath, value
            ));
            return defaultValue;
        }
    }

    public int getInt(String keyPath) {
        return getInt(keyPath, 0);
    }

    public int getInt(String keyPath, int defaultValue) {
        JsonValue value = getValue(keyPath);
        if (value == null) {
            return defaultValue;
        }
        try {
            return value.asInt();
        } catch (Exception e) {
            Gdx.app.error("JsonConfig", String.format(
                "[%s] 无法将 %s 转为 int: %s", path, keyPath, value
            ));
            return defaultValue;
        }
    }

    public float getFloat(String keyPath) {
        return getFloat(keyPath, 0f);
    }

    public float getFloat(String keyPath, float defaultValue) {
        JsonValue value = getValue(keyPath);
        if (value == null) {
            return defaultValue;
        }
        try {
            return value.asFloat();
        } catch (Exception e) {
            Gdx.app.error("JsonConfig", String.format(
                "[%s] 无法将 %s 转为 float: %s", path, keyPath, value
            ));
            return defaultValue;
        }
    }

    public boolean getBoolean(String keyPath) {
        return getBoolean(keyPath, false);
    }

    public boolean getBoolean(String keyPath, boolean defaultValue) {
        JsonValue value = getValue(keyPath);
        if (value == null) {
            return defaultValue;
        }
        try {
            return value.asBoolean();
        } catch (Exception e) {
            Gdx.app.error("JsonConfig", String.format(
                "[%s] 无法将 %s 转为 boolean: %s", path, keyPath, value
            ));
            return defaultValue;
        }
    }

    // ========== getJsonObject ==========

    /**
     * 获取嵌套的 JsonConfig 对象
     * @param keyPath 键路径，如 "unlock" 或 "items[0].unlock"
     * @return 嵌套的 JsonConfig 对象，如果不存在或不是对象则返回 null
     */
    public JsonConfig getJsonObject(String keyPath) {
        JsonValue value = getValue(keyPath);
        if (value == null || !value.isObject()) {
            return null;
        }
        return new JsonConfig(value, path + "." + keyPath);
    }

    // ========== 数组取值 ==========

    public Array<JsonConfig> getArray(String keyPath) {
        JsonValue value = getValue(keyPath);
        if (value == null || !value.isArray()) {
            return new Array<>();
        }
        Array<JsonConfig> result = new Array<>();
        for (JsonValue item : value) {
            result.add(new JsonConfig(item, path + "." + keyPath));
        }
        return result;
    }

    public Array<String> getStringArray(String keyPath) {
        JsonValue value = getValue(keyPath);
        if (value == null || !value.isArray()) {
            return new Array<>();
        }
        Array<String> result = new Array<>();
        for (JsonValue item : value) {
            result.add(item.asString());
        }
        return result;
    }

    public Array<Integer> getIntArray(String keyPath) {
        JsonValue value = getValue(keyPath);
        if (value == null || !value.isArray()) {
            return new Array<>();
        }
        Array<Integer> result = new Array<>();
        for (JsonValue item : value) {
            result.add(item.asInt());
        }
        return result;
    }

    // ========== 转 Java Bean ==========

    public <T> T toObject(Class<T> clazz) {
        checkDisposed();
        try {
            Json json = new Json();
            return json.fromJson(clazz, root.toString());
        } catch (Exception e) {
            Gdx.app.error("JsonConfig", String.format(
                "[%s] 转对象失败: %s", path, e.getMessage()
            ));
            return null;
        }
    }

    public <T> Array<T> toObjectList(Class<T> clazz) {
        checkDisposed();
        try {
            Json json = new Json();
            return json.fromJson(Array.class, clazz, root.toString());
        } catch (Exception e) {
            Gdx.app.error("JsonConfig", String.format(
                "[%s] 转对象列表失败: %s", path, e.getMessage()
            ));
            return null;
        }
    }

    // ========== 检查 ==========

    public boolean has(String keyPath) {
        return getValue(keyPath) != null;
    }

    public boolean isArray(String keyPath) {
        JsonValue value = getValue(keyPath);
        return value != null && value.isArray();
    }

    public boolean isObject(String keyPath) {
        JsonValue value = getValue(keyPath);
        return value != null && value.isObject();
    }

    public boolean isNull(String keyPath) {
        JsonValue value = getValue(keyPath);
        return value == null || value.isNull();
    }

    // ========== 内存释放 ==========

    public void dispose() {
        if (!disposed && root != null) {
            disposed = true;
        }
    }

    private void checkDisposed() {
        if (disposed) {
            Gdx.app.error("JsonConfig", "对象已被释放，无法使用");
        }
    }

    // ========== 调试 ==========

    public String toJson() {
        return root != null ? root.toString() : "null";
    }

    public String getPath() {
        return path;
    }

    public JsonValue getRaw() {
        return root;
    }

    @Override
    public String toString() {
        return toJson();
    }
}
