package cn.tofucat.donkey.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;

/**
 * 日志工具类
 * <p>
 * 支持三种方式：
 * 1. Log.debug(this, "消息")       → 自动获取类名
 * 2. Log.debug(MyClass.class, "消息") → 自动获取类名
 * 3. Log.debug("Tag", "消息")      → 手动指定 Tag
 * <p>
 * 示例：
 * Log.debug(this, "打开商店");
 * Log.info(PlayerManager.class, "升级: {} -> {}", 5, 6);
 * Log.warn("Shop", "金币不足: {}", 50);
 * Log.error("File", "加载失败: {}", path, ex);
 */
public class Log {

    // ========== 日志级别 ==========

    /**
     * 设置日志级别
     *
     * @param level 可选值: none, error, info, debug
     */
    public static void setLogLevel(String level) {
        if (level == null || level.isBlank()) {
            Gdx.app.setLogLevel(Application.LOG_NONE);
            return;
        }

        Gdx.app.setLogLevel(
            switch (level.toLowerCase()) {
                case "none" -> Application.LOG_NONE;
                case "info" -> Application.LOG_INFO;
                case "debug" -> Application.LOG_DEBUG;
                case "error" -> Application.LOG_ERROR;
                default -> {
                    warn("Log", "未知日志级别: {}，使用默认 NONE", level);
                    yield Application.LOG_NONE;
                }
            }
        );
    }

    // ========== 获取 Tag ==========

    private static String getTag(Object obj) {
        return obj.getClass().getSimpleName();
    }

    private static String getTag(Class<?> clazz) {
        return clazz.getSimpleName();
    }

    // ========== debug ==========

    public static void debug(String tag, String msg) {
        Gdx.app.debug(tag, msg);
    }

    public static void debug(String tag, String format, Object... args) {
        Gdx.app.debug(tag, format(format, args));
    }

    public static void debug(Object obj, String msg) {
        Gdx.app.debug(getTag(obj), msg);
    }

    public static void debug(Object obj, String format, Object... args) {
        Gdx.app.debug(getTag(obj), format(format, args));
    }

    public static void debug(Class<?> clazz, String msg) {
        Gdx.app.debug(getTag(clazz), msg);
    }

    public static void debug(Class<?> clazz, String format, Object... args) {
        Gdx.app.debug(getTag(clazz), format(format, args));
    }

    // ========== info ==========

    public static void info(String tag, String msg) {
        Gdx.app.log(tag, msg);
    }

    public static void info(String tag, String format, Object... args) {
        Gdx.app.log(tag, format(format, args));
    }

    public static void info(Object obj, String msg) {
        Gdx.app.log(getTag(obj), msg);
    }

    public static void info(Object obj, String format, Object... args) {
        Gdx.app.log(getTag(obj), format(format, args));
    }

    public static void info(Class<?> clazz, String msg) {
        Gdx.app.log(getTag(clazz), msg);
    }

    public static void info(Class<?> clazz, String format, Object... args) {
        Gdx.app.log(getTag(clazz), format(format, args));
    }

    // ========== warn ==========

    public static void warn(String tag, String msg) {
        Gdx.app.log(tag, "[WARN] " + msg);
    }

    public static void warn(String tag, String format, Object... args) {
        Gdx.app.log(tag, "[WARN] " + format(format, args));
    }

    public static void warn(Object obj, String msg) {
        Gdx.app.log(getTag(obj), "[WARN] " + msg);
    }

    public static void warn(Object obj, String format, Object... args) {
        Gdx.app.log(getTag(obj), "[WARN] " + format(format, args));
    }

    public static void warn(Class<?> clazz, String msg) {
        Gdx.app.log(getTag(clazz), "[WARN] " + msg);
    }

    public static void warn(Class<?> clazz, String format, Object... args) {
        Gdx.app.log(getTag(clazz), "[WARN] " + format(format, args));
    }

    // ========== error ==========

    public static void error(String tag, String msg) {
        Gdx.app.error(tag, msg);
    }

    public static void error(String tag, String msg, Throwable ex) {
        Gdx.app.error(tag, msg, ex);
    }

    public static void error(String tag, String format, Object... args) {
        Gdx.app.error(tag, format(format, args));
    }

    public static void error(String tag, String format, Throwable ex, Object... args) {
        Gdx.app.error(tag, format(format, args), ex);
    }

    public static void error(Object obj, String msg) {
        Gdx.app.error(getTag(obj), msg);
    }

    public static void error(Object obj, String msg, Throwable ex) {
        Gdx.app.error(getTag(obj), msg, ex);
    }

    public static void error(Object obj, String format, Object... args) {
        Gdx.app.error(getTag(obj), format(format, args));
    }

    public static void error(Object obj, String format, Throwable ex, Object... args) {
        Gdx.app.error(getTag(obj), format(format, args), ex);
    }

    public static void error(Class<?> clazz, String msg) {
        Gdx.app.error(getTag(clazz), msg);
    }

    public static void error(Class<?> clazz, String msg, Throwable ex) {
        Gdx.app.error(getTag(clazz), msg, ex);
    }

    public static void error(Class<?> clazz, String format, Object... args) {
        Gdx.app.error(getTag(clazz), format(format, args));
    }

    public static void error(Class<?> clazz, String format, Throwable ex, Object... args) {
        Gdx.app.error(getTag(clazz), format(format, args), ex);
    }

    // ========== 占位符替换 ==========

    private static String format(String format, Object... args) {
        if (args == null || args.length == 0) {
            return format;
        }
        StringBuilder sb = new StringBuilder();
        int idx = 0, last = 0;
        while (idx < args.length) {
            int pos = format.indexOf("{}", last);
            if (pos == -1) {
                break;
            }
            sb.append(format, last, pos).append(args[idx++]);
            last = pos + 2;
        }
        if (last < format.length()) {
            sb.append(format, last, format.length());
        }
        return sb.toString();
    }
}
