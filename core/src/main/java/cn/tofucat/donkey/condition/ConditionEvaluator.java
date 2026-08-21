package cn.tofucat.donkey.condition;

/**
 * 条件评估器接口
 * 用户可以根据自己的游戏逻辑实现这个接口
 *
 * 用法：
 *   ConditionEvaluator evaluator = new MyEvaluator();
 *   ConditionChecker.setEvaluator(evaluator);
 */
public interface ConditionEvaluator {

    /**
     * 评估等级条件是否满足
     */
    boolean evalLevel(int requiredLevel);

    /**
     * 评估任务条件是否满足
     */
    boolean evalQuest(String questId);

    /**
     * 评估成就条件是否满足
     */
    boolean evalAchievement(String achievementId);

    /**
     * 评估区域条件是否满足
     */
    boolean evalArea(String areaId);

    /**
     * 评估功能条件是否满足
     */
    boolean evalFeature(String featureId);

    /**
     * 评估物品条件是否满足
     */
    boolean evalItem(String itemId);

    /**
     * 评估资源条件是否满足
     * @param resourceType 资源类型，如 "gold"
     * @param requiredAmount 所需数量
     */
    boolean evalResource(String resourceType, int requiredAmount);

    /**
     * 评估游戏时间条件是否满足
     */
    boolean evalPlayTime(int requiredHours);

    /**
     * 评估日期条件是否满足
     * @param dateStr 格式 yyyy-MM-dd
     */
    boolean evalDateTime(String dateStr);

    /**
     * 评估任务数量条件是否满足
     */
    boolean evalQuestCount(int requiredCount);

    /**
     * 评估击杀数量条件是否满足
     */
    boolean evalKillCount(String monsterType, int requiredCount);
}
