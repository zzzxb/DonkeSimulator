package cn.tofucat.donkey.ui;

/**
 * 商店商品数据
 */
public class ShopItemData {
    private String id;
    private String iconName;
    private String name;
    private String description;
    private int price;
    private int stock;
    private int maxStock;
    private float priceMultiplier;  // ✅ 价格上涨倍率
    private boolean isTip;
    private boolean enable;

    public ShopItemData(String id, String iconName, String name, String description,
                        int stock, int maxStock, int price, float priceMultiplier, boolean enable) {
        this.id = id;
        this.iconName = iconName;
        this.name = name;
        this.description = description;
        this.stock = stock;
        this.maxStock = maxStock;
        this.price = price;
        this.priceMultiplier = priceMultiplier;
        this.enable = enable;
    }

    // ========== Getters ==========
    public String getId() { return id; }
    public String getIconName() { return iconName; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public int getPrice() { return price; }
    public int getStock() { return stock; }
    public int getMaxStock() { return maxStock; }
    public float getPriceMultiplier() { return priceMultiplier; }
    public boolean isEnable() { return enable; }  // ✅ 添加这个方法
    public boolean isStock() { return stock > 0 || stock == -1; }  // -1 表示无限库存
    public boolean isTip() { return isTip; }

    // ========== Setters ==========
    public void setPrice(int price) { this.price = price; }
    public void setStock(int stock) { this.stock = stock; }
    public void setEnable(boolean enable) { this.enable = enable; }
    public void disableTip() {this.isTip = true; };

    /**
     * 扣减库存（只有有限库存才扣）
     */
    public boolean subStock(int num) {
        if (stock == -1) return true;  // 无限库存
        if (num <= 0 || stock < num) return false;
        stock -= num;
        return true;
    }

    /**
     * 价格上涨（乘以倍率）
     */
    public void increasePrice() {
        this.price = Math.round(this.price * priceMultiplier);
    }
}
