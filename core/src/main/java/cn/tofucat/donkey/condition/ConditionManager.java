package cn.tofucat.donkey.condition;

import cn.tofucat.donkey.config.JsonConfig;
import cn.tofucat.donkey.utils.Log;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

public class ConditionManager {

    private static ConditionManager instance;

    private final ObjectMap<String, Condition> conditions = new ObjectMap<>();
    private final Array<ConditionListener> listeners = new Array<>();

    private ConditionManager() {}

    public static ConditionManager getInstance() {
        if (instance == null) {
            instance = new ConditionManager();
        }
        return instance;
    }

    /**
     * 从 JSON 配置批量注册条件
     * JSON 格式：
     * {
     *   "id": "item_xxx",
     *   "unlock_conditions": [
     *     { "type": "LEVEL", "value": "5" },
     *     { "type": "QUEST", "value": "quest_xxx" }
     *   ]
     * }
     */
    public void registerFromJson(Array<JsonConfig> itemArray) {
        int count = 0;
        for (int i = 0; i < itemArray.size; i++) {
            JsonConfig itemJson = itemArray.get(i);
            String itemId = itemJson.getString("id");
            if (itemId == null || itemId.isEmpty()) {
                Log.error(ConditionManager.class, "物品 ID 为空，跳过");
                continue;
            }

            // ✅ 读取 unlock_conditions 数组
            Array<JsonConfig> conditionsArray = itemJson.getArray("unlock_conditions");
            if (conditionsArray == null || conditionsArray.size == 0) {
                // ✅ 兼容旧格式：单个 unlock 对象
                JsonConfig unlockJson = itemJson.getJsonObject("unlock");
                if (unlockJson != null) {
                    conditionsArray = new Array<>();
                    conditionsArray.add(unlockJson);
                } else {
                    Log.debug(ConditionManager.class, "物品 {} 无解锁条件，默认解锁", itemId);
                    continue;
                }
            }

            try {
                ConditionItem[] items = new ConditionItem[conditionsArray.size];
                for (int j = 0; j < conditionsArray.size; j++) {
                    JsonConfig cond = conditionsArray.get(j);
                    String typeStr = cond.getString("type");
                    String value = cond.getString("value");
                    ConditionType type = ConditionType.valueOf(typeStr);
                    items[j] = new ConditionItem(type, value);
                    Log.debug(ConditionManager.class, "  条件 {}: {}={}", j, typeStr, value);
                }

                Condition condition = new Condition();
                condition.target = UnlockableTarget.SHOP_ITEM;
                condition.targetId = itemId;
                condition.conditions = items;

                register(condition);
                count++;
                Log.debug(ConditionManager.class, "注册条件: {} ({} 个条件)", itemId, items.length);

            } catch (Exception e) {
                Log.error(ConditionManager.class, "从 JSON 注册条件失败: {}", itemId);
                Log.error(ConditionManager.class, "错误详情: {}", e.getMessage());
            }
        }
        Log.debug(ConditionManager.class, "从 JSON 注册 {} 个条件", count);
    }

    public void register(Condition condition) {
        if (condition == null || condition.targetId == null) {
            Log.error(ConditionManager.class, "注册失败：condition 或 targetId 为空");
            return;
        }
        conditions.put(condition.targetId, condition);
        Log.debug(ConditionManager.class, "注册条件: {}", condition.targetId);
    }

    public void registerAll(Condition... conditions) {
        for (Condition condition : conditions) {
            register(condition);
        }
    }

    // ========== 查询 ==========

    public boolean isUnlocked(String targetId) {
        Condition condition = conditions.get(targetId);
        if (condition == null) {
            return true;
        }
        return ConditionChecker.isMet(condition);
    }

    public boolean isUnlocked(UnlockableTarget target, String targetId) {
        Condition condition = conditions.get(targetId);
        if (condition == null) {
            return true;
        }
        if (condition.target != target) {
            return false;
        }
        return ConditionChecker.isMet(condition);
    }

    public Condition getCondition(String targetId) {
        return conditions.get(targetId);
    }

    public boolean hasCondition(String targetId) {
        return conditions.containsKey(targetId);
    }

    public Array<String> getAllTargetIds() {
        Array<String> result = new Array<>();
        for (ObjectMap.Entry<String, Condition> entry : conditions) {
            result.add(entry.key);
        }
        return result;
    }

    // ========== 刷新 ==========

    public void refresh(String targetId) {
        Condition condition = conditions.get(targetId);
        if (condition != null) {
            boolean isUnlocked = ConditionChecker.isMet(condition);
            notifyListeners(targetId, isUnlocked);
        }
    }

    public void refreshAll() {
        for (ObjectMap.Entry<String, Condition> entry : conditions) {
            boolean isUnlocked = ConditionChecker.isMet(entry.value);
            notifyListeners(entry.key, isUnlocked);
        }
    }

    public void refreshByType(ConditionType type) {
        for (ObjectMap.Entry<String, Condition> entry : conditions) {
            Condition condition = entry.value;
            if (condition.hasMultipleConditions()) {
                for (ConditionItem item : condition.conditions) {
                    if (item.type == type) {
                        boolean isUnlocked = ConditionChecker.isMet(condition);
                        notifyListeners(entry.key, isUnlocked);
                        break;
                    }
                }
            } else if (condition.type == type) {
                boolean isUnlocked = ConditionChecker.isMet(condition);
                notifyListeners(entry.key, isUnlocked);
            }
        }
    }

    // ========== 监听器 ==========

    public void addListener(ConditionListener listener) {
        if (listener != null && !listeners.contains(listener, true)) {
            listeners.add(listener);
        }
    }

    public void removeListener(ConditionListener listener) {
        listeners.removeValue(listener, true);
    }

    public void clearListeners() {
        listeners.clear();
    }

    private void notifyListeners(String targetId, boolean isUnlocked) {
        for (ConditionListener listener : listeners) {
            listener.onConditionChanged(targetId, isUnlocked);
        }
    }

    public void clear() {
        conditions.clear();
        listeners.clear();
        Log.debug(ConditionManager.class, "清空所有条件和监听器");
    }

    public interface ConditionListener {
        void onConditionChanged(String targetId, boolean isUnlocked);
    }
}
