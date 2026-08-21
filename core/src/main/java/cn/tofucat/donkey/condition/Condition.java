package cn.tofucat.donkey.condition;

/**
 * 解锁条件
 * 描述：要解锁什么目标，需要满足什么条件
 * <p>
 * 支持两种格式：
 * 1. 单个条件：使用 type + value
 * 2. 多个条件：使用 conditions 数组（所有条件必须同时满足，AND 逻辑）
 * <p>
 * 示例（JSON）：
 * <pre>
 * "unlock_conditions": [
 *     { "type": "LEVEL", "value": "5" },
 *     { "type": "QUEST", "value": "quest_blacksmith" }
 * ]
 * </pre>
 */
public class Condition {

    /** 要解锁的目标类型 */
    public UnlockableTarget target;

    /** 目标的具体标识（如物品ID、功能ID、区域ID） */
    public String targetId;

    // ===== 单个条件 =====

    /** 解锁条件类型（单个条件时使用） */
    public ConditionType type = ConditionType.NONE;

    /** 条件值（单个条件时使用） */
    public String value;

    // ===== 多个条件（新格式） =====

    /** 条件列表（多个条件时使用，所有条件必须同时满足） */
    public ConditionItem[] conditions;

    // ===== 组合条件（复杂逻辑） =====

    /** 是否必须同时满足多个子条件（用于组合条件） */
    public boolean isAnd = true;

    /** 子条件列表（用于组合条件） */
    public Condition[] subConditions;

    public Condition() {}

    public Condition(UnlockableTarget target, String targetId, ConditionType type, String value) {
        this.target = target;
        this.targetId = targetId;
        this.type = type;
        this.value = value;
    }

    /**
     * 判断是否为多条件格式
     */
    public boolean hasMultipleConditions() {
        return conditions != null && conditions.length > 0;
    }

    /**
     * 判断是否有子条件（组合条件）
     */
    public boolean hasSubConditions() {
        return subConditions != null && subConditions.length > 0;
    }

    /**
     * 判断是否为无限制（默认解锁）
     */
    public boolean isNone() {
        return type == ConditionType.NONE && !hasMultipleConditions() && !hasSubConditions();
    }

    // ========== 静态工厂方法 ==========

    public static Condition none(UnlockableTarget target, String targetId) {
        return new Condition(target, targetId, ConditionType.NONE, null);
    }

    public static Condition level(UnlockableTarget target, String targetId, int requiredLevel) {
        return new Condition(target, targetId, ConditionType.LEVEL, String.valueOf(requiredLevel));
    }

    public static Condition quest(UnlockableTarget target, String targetId, String questId) {
        return new Condition(target, targetId, ConditionType.QUEST, questId);
    }

    public static Condition achievement(UnlockableTarget target, String targetId, String achievementId) {
        return new Condition(target, targetId, ConditionType.ACHIEVEMENT, achievementId);
    }

    public static Condition area(UnlockableTarget target, String targetId, String areaId) {
        return new Condition(target, targetId, ConditionType.AREA, areaId);
    }

    public static Condition resource(UnlockableTarget target, String targetId, String resourceType, int amount) {
        return new Condition(target, targetId, ConditionType.RESOURCE, resourceType + ":" + amount);
    }

    public static Condition item(UnlockableTarget target, String targetId, String itemId) {
        return new Condition(target, targetId, ConditionType.ITEM, itemId);
    }

    public static Condition questCount(UnlockableTarget target, String targetId, int count) {
        return new Condition(target, targetId, ConditionType.QUEST_COUNT, String.valueOf(count));
    }

    // ========== 多条件构建 ==========

    /**
     * 创建多条件（AND 逻辑）
     */
    public static Condition and(UnlockableTarget target, String targetId, ConditionItem... items) {
        Condition condition = new Condition();
        condition.target = target;
        condition.targetId = targetId;
        condition.conditions = items;
        return condition;
    }

    @Override
    public String toString() {
        if (hasMultipleConditions()) {
            StringBuilder sb = new StringBuilder("Condition{target=" + target + "(" + targetId + "), conditions=[");
            for (int i = 0; i < conditions.length; i++) {
                sb.append(conditions[i]);
                if (i < conditions.length - 1) sb.append(", ");
            }
            sb.append("]}");
            return sb.toString();
        }
        return String.format("Condition{target=%s(%s), type=%s, value=%s}",
            target, targetId, type, value);
    }
}
