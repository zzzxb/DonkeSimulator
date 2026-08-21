package cn.tofucat.donkey.condition;

/**
 * 可解锁的目标类型
 * 定义了什么内容可以被解锁
 */
public enum UnlockableTarget {

    // ===== 物品类 =====
    /** 商店物品 */
    SHOP_ITEM,

    /** 装备 */
    EQUIPMENT,

    /** 道具 */
    CONSUMABLE,

    // ===== 功能类 =====
    /** 系统功能（如锻造、合成） */
    FEATURE,

    /** 玩法模式（如困难模式） */
    GAME_MODE,

    // ===== 区域类 =====
    /** 地图区域 */
    AREA,

    /** 副本 */
    DUNGEON,

    // ===== 特权类 =====
    /** 玩家特权 */
    PERK,

    /** VIP等级 */
    VIP_LEVEL,

    // ===== 其他 =====
    /** 任务 */
    QUEST,

    /** 皮肤/外观 */
    SKIN
}
