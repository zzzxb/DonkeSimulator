package cn.tofucat.donkey.condition;

/**
 * 条件系统统一入口
 * 提供最常用的静态方法，减少调用链长度
 *
 * 用法（静态导入）：
 *   import static cn.tofucat.donkey.condition.Conditions.*;
 *
 *   setEvaluator(new MyGameEvaluator());
 *   register(condition);
 *   if (isUnlocked("item_sword")) { ... }
 */
public class Conditions {

    /**
     * 检查条件是否满足
     */
    public static boolean isMet(Condition condition) {
        return ConditionChecker.isMet(condition);
    }

    /**
     * 检查目标是否已解锁
     */
    public static boolean isUnlocked(String targetId) {
        return ConditionManager.getInstance().isUnlocked(targetId);
    }

    /**
     * 检查目标是否已解锁（带类型限定）
     */
    public static boolean isUnlocked(UnlockableTarget target, String targetId) {
        return ConditionManager.getInstance().isUnlocked(target, targetId);
    }

    /**
     * 注册条件
     */
    public static void register(Condition condition) {
        ConditionManager.getInstance().register(condition);
    }

    /**
     * 设置评估器
     */
    public static void setEvaluator(ConditionEvaluator evaluator) {
        ConditionChecker.setEvaluator(evaluator);
    }

    /**
     * 获取当前评估器
     */
    public static ConditionEvaluator getEvaluator() {
        return ConditionChecker.getEvaluator();
    }

    /**
     * 注册自定义条件类型
     */
    public static void registerCustom(String typeName, CustomConditionChecker.CustomCheck check) {
        CustomConditionChecker.register(typeName, check);
    }

    /**
     * 刷新指定目标
     */
    public static void refresh(String targetId) {
        ConditionManager.getInstance().refresh(targetId);
    }

    /**
     * 刷新所有条件
     */
    public static void refreshAll() {
        ConditionManager.getInstance().refreshAll();
    }

    /**
     * 添加监听器
     */
    public static void addListener(ConditionManager.ConditionListener listener) {
        ConditionManager.getInstance().addListener(listener);
    }
}
