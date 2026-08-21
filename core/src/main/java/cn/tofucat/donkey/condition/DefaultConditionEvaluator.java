package cn.tofucat.donkey.condition;

import com.badlogic.gdx.Gdx;

/**
 * 默认条件评估器
 * 所有方法都返回 false，用户需要继承并覆盖
 */
public class DefaultConditionEvaluator implements ConditionEvaluator {

    @Override
    public boolean evalLevel(int requiredLevel) {
        Gdx.app.error("Condition", "请实现 evalLevel() 方法");
        return false;
    }

    @Override
    public boolean evalQuest(String questId) {
        Gdx.app.error("Condition", "请实现 evalQuest() 方法");
        return false;
    }

    @Override
    public boolean evalAchievement(String achievementId) {
        Gdx.app.error("Condition", "请实现 evalAchievement() 方法");
        return false;
    }

    @Override
    public boolean evalArea(String areaId) {
        Gdx.app.error("Condition", "请实现 evalArea() 方法");
        return false;
    }

    @Override
    public boolean evalFeature(String featureId) {
        Gdx.app.error("Condition", "请实现 evalFeature() 方法");
        return false;
    }

    @Override
    public boolean evalItem(String itemId) {
        Gdx.app.error("Condition", "请实现 evalItem() 方法");
        return false;
    }

    @Override
    public boolean evalResource(String resourceType, int requiredAmount) {
        Gdx.app.error("Condition", "请实现 evalResource() 方法");
        return false;
    }

    @Override
    public boolean evalPlayTime(int requiredHours) {
        Gdx.app.error("Condition", "请实现 evalPlayTime() 方法");
        return false;
    }

    @Override
    public boolean evalDateTime(String dateStr) {
        Gdx.app.error("Condition", "请实现 evalDateTime() 方法");
        return false;
    }

    @Override
    public boolean evalQuestCount(int requiredCount) {
        Gdx.app.error("Condition", "请实现 evalQuestCount() 方法");
        return false;
    }

    @Override
    public boolean evalKillCount(String monsterType, int requiredCount) {
        Gdx.app.error("Condition", "请实现 evalKillCount() 方法");
        return false;
    }
}
