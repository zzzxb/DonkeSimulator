package cn.tofucat.donkey.condition;

/**
 * 条件项 - 单个条件
 * 用于 {@link Condition} 中的 conditions 数组
 */
public class ConditionItem {

    /** 条件类型 */
    public ConditionType type;

    /** 条件值（如等级数、任务ID、资源类型:数量） */
    public String value;

    public ConditionItem() {}

    public ConditionItem(ConditionType type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("ConditionItem{type=%s, value=%s}", type, value);
    }
}
