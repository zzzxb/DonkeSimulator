package cn.tofucat.donkey.manager;

import cn.tofucat.donkey.utils.Log;

/**
 * 玩家管理器 - 管理玩家资源
 *
 * @author zzzxb
 * 2026/8/18
 */
public class PlayerManager {
    private static PlayerManager instance;
    private int abacus;
    private int flour = 0;
    private int totalCoils;

    private PlayerManager() {
    }

    public static PlayerManager getInstance() {
        if (instance == null) {
            instance = new PlayerManager();
        }
        return instance;
    }

    public int getTotalCoils() {
        return totalCoils;
    }

    public void addCoils(int coils) {
        this.totalCoils += coils;
    }

    public boolean addAbacus() {
        this.abacus += 1;
        return true;
    }

    public boolean isAbacus() {
        return abacus > 0;
    }

    public void addFlour(int amount) {
        Log.info("PlayerManager", "获得面粉: +{}，当前: {} -> {}", amount, flour, flour + amount);
        flour += amount;
    }

    public boolean isRich(int amount) {
        return flour >= amount;
    }

    public boolean payFlour(String name, int amount) {
        if (flour < amount) {
            Log.info("PlayerManager", "面粉不足: 购买 {} 需要 {}，当前: {}",
                name, amount, flour);
            return false;
        }
        Log.info("PlayerManager", "购买 {} 需要 {}, 当前: {} -> {}",
            name, amount, flour, flour - amount);
        flour -= amount;
        return true;
    }

    public int getFlour() {
        return flour;
    }

    /**
     * 重置玩家数据（新游戏时调用）
     */
    public void reset() {
        flour = 0;
        Log.debug("PlayerManager", "重置完成: 面粉 {}", flour);
    }
}
