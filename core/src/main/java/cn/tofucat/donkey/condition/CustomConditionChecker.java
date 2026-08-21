package cn.tofucat.donkey.condition;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * 自定义条件检查器
 * 支持用户注册自定义条件类型
 *
 * 用法：
 *   CustomConditionChecker.register("VIP", value -> {
 *       int requiredLevel = Integer.parseInt(value);
 *       return PlayerManager.getInstance().getVipLevel() >= requiredLevel;
 *   });
 */
public class CustomConditionChecker {

    private static final ObjectMap<String, CustomCheck> customChecks = new ObjectMap<>();

    /**
     * 自定义条件检查接口
     */
    public interface CustomCheck {
        boolean check(String value);
    }

    /**
     * 注册自定义条件类型
     * @param typeName 条件类型名称（对应 Condition.value 中的前缀）
     * @param check    检查逻辑
     */
    public static void register(String typeName, CustomCheck check) {
        if (typeName == null || typeName.isEmpty() || check == null) {
            Gdx.app.error("CustomConditionChecker", "注册失败：typeName 或 check 为空");
            return;
        }
        customChecks.put(typeName, check);
        Gdx.app.debug("CustomConditionChecker", "注册自定义条件: " + typeName);
    }

    /**
     * 检查自定义条件
     * @param typeName 条件类型名称
     * @param value    条件值
     * @return 是否满足
     */
    public static boolean checkCustom(String typeName, String value) {
        if (typeName == null || typeName.isEmpty()) {
            Gdx.app.error("CustomConditionChecker", "检查失败：typeName 为空");
            return false;
        }
        CustomCheck check = customChecks.get(typeName);
        if (check == null) {
            Gdx.app.error("CustomConditionChecker", "未注册的条件类型: " + typeName);
            return false;
        }
        return check.check(value);
    }

    /**
     * 检查是否已注册指定类型
     */
    public static boolean isRegistered(String typeName) {
        return customChecks.containsKey(typeName);
    }

    /**
     * 取消注册指定类型
     */
    public static void unregister(String typeName) {
        customChecks.remove(typeName);
        Gdx.app.debug("CustomConditionChecker", "取消注册: " + typeName);
    }

    /**
     * 清空所有自定义条件
     */
    public static void clear() {
        customChecks.clear();
        Gdx.app.debug("CustomConditionChecker", "清空所有自定义条件");
    }
}
