package cn.tofucat.donkey.condition;

/**
 * 解锁条件类型
 * 定义了所有可能的解锁条件种类
 */
public enum ConditionType {

    /** 无任何条件限制，默认解锁 */
    NONE,

    /** 玩家达到指定等级 */
    LEVEL,

    /** 完成任务 */
    QUEST,

    /** 达成成就 */
    ACHIEVEMENT,

    /** 已解锁指定区域 */
    AREA,

    /** 已解锁指定功能 */
    FEATURE,

    /** 拥有指定道具 */
    ITEM,

    /** 消耗指定数量的资源（金币、钻石等） */
    RESOURCE,

    /** 游戏时间达到指定值（小时） */
    PLAY_TIME,

    /** 特定日期时间后解锁 */
    DATE_TIME,

    /** 完成指定数量的任务 */
    QUEST_COUNT,

    /** 击杀指定数量的怪物 */
    KILL_COUNT,

    /** 用户自定义条件（配合 CustomConditionChecker 使用） */
    CUSTOM
}
