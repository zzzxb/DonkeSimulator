package cn.tofucat.donkey.condition;

/**
 * 条件构建器
 * 用于便捷地创建 Condition 对象
 *
 * 用法：
 *   Condition condition = ConditionBuilder.create()
 *       .target(UnlockableTarget.SHOP_ITEM)
 *       .targetId("item_sword")
 *       .type(ConditionType.LEVEL)
 *       .value("5")
 *       .build();
 */
public class ConditionBuilder {

    private final Condition condition = new Condition();

    public static ConditionBuilder create() {
        return new ConditionBuilder();
    }

    public ConditionBuilder target(UnlockableTarget target) {
        condition.target = target;
        return this;
    }

    public ConditionBuilder targetId(String targetId) {
        condition.targetId = targetId;
        return this;
    }

    public ConditionBuilder type(ConditionType type) {
        condition.type = type;
        return this;
    }

    public ConditionBuilder value(String value) {
        condition.value = value;
        return this;
    }

    /**
     * AND 组合条件：所有子条件都必须满足
     */
    public ConditionBuilder and(Condition... subConditions) {
        condition.isAnd = true;
        condition.subConditions = subConditions;
        return this;
    }

    /**
     * OR 组合条件：至少一个子条件满足
     */
    public ConditionBuilder or(Condition... subConditions) {
        condition.isAnd = false;
        condition.subConditions = subConditions;
        return this;
    }

    public Condition build() {
        return condition;
    }
}
