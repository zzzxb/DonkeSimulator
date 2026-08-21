package cn.tofucat.donkey.condition;

/**
 * 条件评估器基类
 * <p>
 * 用户只需要继承这个类，重写自己用到的条件方法即可。
 * 没有重写的方法默认返回 false。
 * <p>
 * 示例：
 * <pre>
 * public class GameEvaluator extends BaseConditionEvaluator {
 *     private final PlayerManager player = PlayerManager.getInstance();
 *
 *     {@literal @}Override
 *     public boolean evalLevel(int requiredLevel) {
 *         return player.getLevel() >= requiredLevel;
 *     }
 *
 *     {@literal @}Override
 *     public boolean evalQuest(String questId) {
 *         return player.isQuestCompleted(questId);
 *     }
 * }
 * </pre>
 */
public abstract class BaseConditionEvaluator implements ConditionEvaluator {

    @Override
    public boolean evalLevel(int value) {
        return false;
    }

    @Override
    public boolean evalQuest(String value) {
        return false;
    }

    @Override
    public boolean evalAchievement(String value) {
        return false;
    }

    @Override
    public boolean evalArea(String value) {
        return false;
    }

    @Override
    public boolean evalFeature(String value) {
        return false;
    }

    @Override
    public boolean evalItem(String value) {
        return false;
    }

    @Override
    public boolean evalResource(String resourceType, int value) {
        return false;
    }

    @Override
    public boolean evalPlayTime(int value) {
        return false;
    }

    @Override
    public boolean evalDateTime(String value) {
        return false;
    }

    @Override
    public boolean evalQuestCount(int value) {
        return false;
    }

    @Override
    public boolean evalKillCount(String monsterType, int value) {
        return false;
    }

    /**
     * 解析条件值
     * 格式：类型:值，如 "mill:10" 或 "gold:100.5"
     * <p>
     * 如果没有 ":"，则 name 为整个字符串，value 为空
     *
     * @param input 输入的字符串
     * @return ConditionValue 对象
     */
    protected ConditionValue parse(String input) {
        if (input == null || input.isEmpty()) {
            return new ConditionValue("", "");
        }
        int index = input.indexOf(":");
        if (index == -1) {
            return new ConditionValue(input, "");
        }
        return new ConditionValue(input.substring(0, index), input.substring(index + 1));
    }
}
