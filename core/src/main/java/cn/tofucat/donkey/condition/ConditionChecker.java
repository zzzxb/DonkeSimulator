package cn.tofucat.donkey.condition;

import cn.tofucat.donkey.utils.Log;

/**
 * 条件检查器
 * 职责：判断一个 Condition 是否满足
 * <p>
 * 用法：
 * // 初始化时设置评估器
 * ConditionChecker.setEvaluator(new MyGameEvaluator());
 * <p>
 * // 检查条件
 * if (ConditionChecker.isMet(condition)) {
 * // 解锁内容
 * }
 */
public class ConditionChecker {

    private static ConditionEvaluator evaluator = new DefaultConditionEvaluator();

    /**
     * 设置条件评估器（用户自定义）
     */
    public static void setEvaluator(ConditionEvaluator evaluator) {
        if (evaluator != null) {
            ConditionChecker.evaluator = evaluator;
            Log.debug(ConditionChecker.class, "设置自定义评估器: {}", evaluator.getClass().getSimpleName());
        }
    }

    /**
     * 获取当前评估器
     */
    public static ConditionEvaluator getEvaluator() {
        return evaluator;
    }

    /**
     * 检查条件是否满足
     */
    public static boolean isMet(Condition condition) {
        if (condition == null) {
            return true;
        }

        // 1. 优先检查组合条件（子条件）
        if (condition.hasSubConditions()) {
            return checkCombined(condition);
        }

        // 2. 检查多条件数组（AND 逻辑）
        if (condition.hasMultipleConditions()) {
            return checkConditions(condition.conditions);
        }

        // 3. 检查单个条件
        return checkSingleCondition(condition);
    }

    // ========== 核心检查方法（消除重复） ==========

    /**
     * 通用条件检查 - 根据类型和值检查
     */
    private static boolean checkByType(ConditionType type, String value) {
        if (type == null || type == ConditionType.NONE) {
            return true;
        }

        return switch (type) {
            case LEVEL -> evaluator.evalLevel(parseInt(value));
            case QUEST -> evaluator.evalQuest(value);
            case ACHIEVEMENT -> evaluator.evalAchievement(value);
            case AREA -> evaluator.evalArea(value);
            case FEATURE -> evaluator.evalFeature(value);
            case ITEM -> evaluator.evalItem(value);
            case RESOURCE -> evalResource(value);
            case PLAY_TIME -> evaluator.evalPlayTime(parseInt(value));
            case DATE_TIME -> evaluator.evalDateTime(value);
            case QUEST_COUNT -> evaluator.evalQuestCount(parseInt(value));
            case KILL_COUNT -> evalKillCount(value);
            case CUSTOM -> evalCustom(value);
            default -> {
                Log.error(ConditionChecker.class, "未知条件类型: {}", type);
                yield false;
            }
        };
    }

    // ========== 单条件检查 ==========

    private static boolean checkSingleCondition(Condition condition) {
        return checkByType(condition.type, condition.value);
    }

    // ========== 多条件检查 ==========

    private static boolean checkConditions(ConditionItem[] items) {
        if (items == null || items.length == 0) {
            return true;
        }
        for (ConditionItem item : items) {
            if (!checkByType(item.type, item.value)) {
                return false;
            }
        }
        return true;
    }

    // ========== 组合条件检查（AND / OR） ==========

    private static boolean checkCombined(Condition condition) {
        if (condition.subConditions == null || condition.subConditions.length == 0) {
            return true;
        }

        if (condition.isAnd) {
            for (Condition sub : condition.subConditions) {
                if (!isMet(sub)) {
                    return false;
                }
            }
            return true;
        } else {
            for (Condition sub : condition.subConditions) {
                if (isMet(sub)) {
                    return true;
                }
            }
            return false;
        }
    }

    // ========== 资源条件解析 ==========

    /**
     * 解析资源条件
     * 格式：资源类型:数量，如 "gold:100"
     */
    private static boolean evalResource(String value) {
        try {
            String[] parts = value.split(":");
            if (parts.length != 2) {
                Log.error(ConditionChecker.class, "资源条件格式错误: {}", value);
                return false;
            }
            String resourceType = parts[0];
            int requiredAmount = parseInt(parts[1]);
            return evaluator.evalResource(resourceType, requiredAmount);
        } catch (Exception e) {
            Log.error(ConditionChecker.class, "检查资源条件失败: {}", value);
            return false;
        }
    }

    /**
     * 解析击杀条件
     * 格式：怪物类型:数量，如 "slime:50"
     */
    private static boolean evalKillCount(String value) {
        try {
            String[] parts = value.split(":");
            if (parts.length != 2) {
                Log.error(ConditionChecker.class, "击杀条件格式错误: {}", value);
                return false;
            }
            String monsterType = parts[0];
            int requiredCount = parseInt(parts[1]);
            return evaluator.evalKillCount(monsterType, requiredCount);
        } catch (Exception e) {
            Log.error(ConditionChecker.class, "检查击杀条件失败: {}", value);
            return false;
        }
    }

    /**
     * 解析自定义条件
     * 格式：类型名:值，如 "VIP:3"
     */
    private static boolean evalCustom(String value) {
        try {
            String[] parts = value.split(":", 2);
            if (parts.length != 2) {
                Log.error(ConditionChecker.class, "自定义条件格式错误: {}，应为 类型:值", value);
                return false;
            }
            return CustomConditionChecker.checkCustom(parts[0], parts[1]);
        } catch (Exception e) {
            Log.error(ConditionChecker.class, "检查自定义条件失败: {}", value);
            return false;
        }
    }

    private static int parseInt(String value) {
        if (value == null || value.isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            Log.error(ConditionChecker.class, "无效的数字: {}", value);
            return 0;
        }
    }
}
