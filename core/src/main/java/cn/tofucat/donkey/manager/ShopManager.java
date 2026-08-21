package cn.tofucat.donkey.manager;

import cn.tofucat.donkey.condition.ConditionManager;
import cn.tofucat.donkey.config.JsonConfig;
import cn.tofucat.donkey.config.JsonConfigLoader;
import cn.tofucat.donkey.ui.ShopItemData;
import cn.tofucat.donkey.ui.ShopItemUIGroup;
import cn.tofucat.donkey.utils.CameraController;
import cn.tofucat.donkey.utils.Log;
import com.badlogic.gdx.utils.Array;

public class ShopManager {
    private static ShopManager instance;
    private final JsonConfig shopConfig;
    private Array<ShopItemData> shopItems = new Array<>();
    private CameraController cameraController;

    // 田地等级
    private int fieldLevel = 1;
    private static final int MAX_FIELD_LEVEL = 6;

    private ShopManager() {
        shopConfig = JsonConfigLoader.load("config/shop_items.json");
        ConditionManager.getInstance().registerFromJson(shopConfig.getArray("items"));
    }

    public static ShopManager getInstance() {
        if (instance == null) {
            instance = new ShopManager();
        }
        return instance;
    }

    public void init(CameraController cameraController) {
        this.cameraController = cameraController;
        this.fieldLevel = 1;
        initShopData();
    }

    private void initShopData() {
        Array<JsonConfig> items = shopConfig.getArray("items");
        for (int i = 0; i < items.size; i++) {
            JsonConfig json = items.get(i);
            shopItems.add(new ShopItemData(
                json.getString("id"),
                json.getString("iconName"),
                json.getString("name"),
                json.getString("description"),
                json.getInt("initialStock"),
                json.getInt("maxStock"),
                json.getInt("price"),
                json.getFloat("priceMultiplier", 1.0f),
                json.getBoolean("enable", true)
            ));
        }
    }

    public boolean canUpgradeField() {
        if (fieldLevel >= MAX_FIELD_LEVEL) return false;
        int millCount = MillManager.getInstance().millSize();
        return fieldLevel > (millCount / 8);
    }

    public boolean upgradeField() {
        if (!canUpgradeField()) {
            Log.warn(this, "田地无法升级: 等级 {}，磨盘数量 {}", fieldLevel, MillManager.getInstance().millSize());
            return false;
        }
        fieldLevel++;
        Log.info(this, "田地升级成功: {} -> {}", fieldLevel - 1, fieldLevel);
        return true;
    }

    public int getFieldLevel() { return fieldLevel; }
    public int getMaxFieldLevel() { return MAX_FIELD_LEVEL; }

    /**
     * 购买商品
     * @return 购买结果
     */
    public BuyResult buy(ShopItemData data, ShopItemUIGroup uiGroup) {
        PlayerManager player = PlayerManager.getInstance();
        MillManager millManager = MillManager.getInstance();

        // 1. 检查解锁
        if (!ConditionManager.getInstance().isUnlocked(data.getId())) {
            Log.info(this, "物品未解锁: {}", data.getName());
            return BuyResult.NOT_UNLOCKED;
        }

        // 2. 检查库存
        if (!data.isStock()) {
            Log.info(this, "库存不足: {}", data.getName());
            return BuyResult.NO_STOCK;
        }

        // 3. 检查余额
        if (player.getFlour() < data.getPrice()) {
            Log.info(this, "余额不足: {} 需要 {}", player.getFlour(), data.getPrice());
            return BuyResult.NOT_ENOUGH_FLOUR;
        }

        // 4. 执行购买效果
        boolean success = false;
        switch (data.getId()) {
            case "item_abacus" -> success = PlayerManager.getInstance().addAbacus();
            case "item_strength" -> success = millManager.upgradeStrength();
            case "item_speed" -> success = millManager.upgradeMillRotationDegrees();
            case "item_flour" -> success = millManager.upgradeFlourProduction();
            case "item_mill" -> success = millManager.addMill(cameraController, millManager.createMill());
            case "item_donkey" -> success = millManager.buyDonkey();
            case "item_expand" -> success = upgradeField();
            default -> {
                Log.error(this, "未知商品ID: {}", data.getId());
                return BuyResult.UNKNOWN_ERROR;
            }
        }

        // 5. 支付 & 更新状态
        if (success) {
            player.payFlour(data.getName(), data.getPrice());
            data.subStock(1);

            if (data.isStock()) {
                data.increasePrice();
                if (uiGroup != null) uiGroup.updatePrice(data.getPrice());
            }

            Log.info(this, "购买成功: {}", data.getName());
            return BuyResult.SUCCESS;
        }

        return BuyResult.UNKNOWN_ERROR;
    }

    public Array<ShopItemData> getShopItems() { return shopItems; }

    public ShopItemData getItem(String id) {
        for (ShopItemData item : shopItems) {
            if (item.getId().equals(id)) {
                return item;
            }
        }
        return null;
    }

    // ========== 购买结果枚举 ==========

    public enum BuyResult {
        /** 购买成功 */
        SUCCESS,
        /** 未解锁 */
        NOT_UNLOCKED,
        /** 库存不足 */
        NO_STOCK,
        /** 面粉不足 */
        NOT_ENOUGH_FLOUR,
        /** 未知错误 */
        UNKNOWN_ERROR
    }
}
