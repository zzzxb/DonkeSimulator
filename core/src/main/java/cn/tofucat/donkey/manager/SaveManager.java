package cn.tofucat.donkey.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * 存档管理器，支持多存档槽位。
 *
 * <p>使用示例：
 * <pre>
 * SaveManager.init();
 * SaveManager.saveSlot(0, data);
 * GameData data = SaveManager.loadSlot(0);
 * </pre>
 *
 * @author zzzxb
 * @since 2026/8/15
 */
public class SaveManager {

    private static final String PREFS_NAME = "game_save_data";
    private static Preferences prefs;

    private SaveManager() {}

    /**
     * 初始化存档管理器。
     */
    public static void init() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
    }

    /**
     * 保存游戏数据到指定存档槽。
     *
     * @param slot 存档槽（0 ~ 9）
     * @param data 游戏数据
     */
    public static void saveSlot(int slot, GameData data) {
        String prefix = "slot_" + slot + "_";
        prefs.putString(prefix + "player_name", data.playerName);
        prefs.putInteger(prefix + "level", data.level);
        prefs.putInteger(prefix + "score", data.score);
        prefs.putFloat(prefix + "progress", data.progress);
        prefs.putLong(prefix + "timestamp", System.currentTimeMillis());
        prefs.flush();
    }

    /**
     * 从指定存档槽加载游戏数据。
     *
     * @param slot 存档槽（0 ~ 9）
     * @return 游戏数据，如果存档不存在则返回 null
     */
    public static GameData loadSlot(int slot) {
        String prefix = "slot_" + slot + "_";
        if (!prefs.contains(prefix + "player_name")) {
            return null;
        }
        GameData data = new GameData();
        data.playerName = prefs.getString(prefix + "player_name", "");
        data.level = prefs.getInteger(prefix + "level", 0);
        data.score = prefs.getInteger(prefix + "score", 0);
        data.progress = prefs.getFloat(prefix + "progress", 0f);
        data.timestamp = prefs.getLong(prefix + "timestamp", 0L);
        return data;
    }

    /**
     * 检查存档槽是否存在。
     *
     * @param slot 存档槽（0 ~ 9）
     * @return true 如果存档存在
     */
    public static boolean hasSlot(int slot) {
        String prefix = "slot_" + slot + "_";
        return prefs.contains(prefix + "player_name");
    }

    /**
     * 删除指定存档槽。
     *
     * @param slot 存档槽（0 ~ 9）
     */
    public static void deleteSlot(int slot) {
        String prefix = "slot_" + slot + "_";
        prefs.remove(prefix + "player_name");
        prefs.remove(prefix + "level");
        prefs.remove(prefix + "score");
        prefs.remove(prefix + "progress");
        prefs.remove(prefix + "timestamp");
        prefs.flush();
    }

    /**
     * 获取存档修改时间。
     *
     * @param slot 存档槽（0 ~ 9）
     * @return 时间戳，如果存档不存在则返回 0
     */
    public static long getTimestamp(int slot) {
        String prefix = "slot_" + slot + "_";
        return prefs.getLong(prefix + "timestamp", 0L);
    }

    /**
     * 清空所有存档。
     */
    public static void clearAll() {
        prefs.clear();
        prefs.flush();
    }

    /**
     * 游戏数据类。
     */
    public static class GameData {
        public String playerName = "";
        public int level = 1;
        public int score = 0;
        public float progress = 0f;
        public long timestamp = 0L;
    }
}
