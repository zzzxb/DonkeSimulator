package cn.tofucat.donkey.manager;

import cn.tofucat.donkey.condition.BaseConditionEvaluator;
import cn.tofucat.donkey.condition.ConditionValue;

/**
 *
 * @author zzzxb
 * 2026/8/19
 */
public class GameEvaluator extends BaseConditionEvaluator {
    @Override
    public boolean evalQuestCount(int value) {
        return PlayerManager.getInstance().getTotalCoils() >= value;
    }

    @Override
    public boolean evalItem(String value) {
        ConditionValue parse = parse(value);
        return switch (parse.getName()) {
            case "mill" -> MillManager.getInstance().millSize() >= parse.getInt();
            case "donkey" -> MillManager.getInstance().getDonkeyCount() >= parse.getInt();
            default -> throw new IllegalStateException("Unexpected value: " + parse.getName());
        };
    }
}
