package cn.tofucat.donkey.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

/**
 * 商店商品 UI 组
 * 包含商品行、图标、名称、描述、价格、状态标签、购买按钮
 *
 * @author zzzxb
 * 2026/8/19
 */
public class ShopItemUIGroup {
    private VisTable row;
    private VisImage icon;
    private VisLabel nameLabel;
    private VisLabel descriptionLabel;
    private VisLabel priceLabel;
    private VisLabel statusLabel;
    private VisImageButton buyBtn;

    // ========== Getters & Setters ==========

    public VisTable getRow() { return row; }
    public void setRow(VisTable row) { this.row = row; }

    public VisImage getIcon() { return icon; }
    public void setIcon(VisImage icon) { this.icon = icon; }

    public VisLabel getNameLabel() { return nameLabel; }
    public void setNameLabel(VisLabel nameLabel) { this.nameLabel = nameLabel; }

    public VisLabel getDescriptionLabel() { return descriptionLabel; }
    public void setDescriptionLabel(VisLabel descriptionLabel) { this.descriptionLabel = descriptionLabel; }

    public VisLabel getPriceLabel() { return priceLabel; }
    public void setPriceLabel(VisLabel priceLabel) { this.priceLabel = priceLabel; }

    public VisLabel getStatusLabel() { return statusLabel; }
    public void setStatusLabel(VisLabel statusLabel) { this.statusLabel = statusLabel; }

    public VisImageButton getBuyBtn() { return buyBtn; }
    public void setBuyBtn(VisImageButton buyBtn) { this.buyBtn = buyBtn; }

    // ========== UI 更新方法 ==========

    /**
     * 显示未解锁提示
     */
    public void showUnlockTip() {
        if (statusLabel != null) {
            statusLabel.setText("🔒 未解锁");
            statusLabel.setVisible(true);
        }
    }

    /**
     * 显示库存不足提示
     */
    public void showStockTip() {
        if (statusLabel != null) {
            statusLabel.setText("📦 已售罄");
            statusLabel.setVisible(true);
        }
    }

    /**
     * 显示余额不足提示
     */
    public void showRichTip() {
        if (statusLabel != null) {
            statusLabel.setText("💸 面粉不足");
            statusLabel.setVisible(true);
        }
    }

    /**
     * 隐藏状态标签
     */
    public void hideStatusLabel() {
        if (statusLabel != null) {
            statusLabel.setVisible(false);
        }
    }

    /**
     * 隐藏购买按钮
     */
    public void hideBuyButton() {
        if (buyBtn != null) {
            buyBtn.setVisible(false);
        }
    }

    /**
     * 显示购买按钮
     */
    public void showBuyButton() {
        if (buyBtn != null) {
            buyBtn.setVisible(true);
        }
    }

    /**
     * 更新价格显示
     */
    public void updatePrice(int newPrice) {
        if (priceLabel != null) {
            priceLabel.setText(newPrice);
        }
    }

    /**
     * 重置所有状态（重新显示时调用）
     */
    public void resetState() {
        if (statusLabel != null) {
            statusLabel.setVisible(false);
        }
        if (buyBtn != null) {
            buyBtn.setVisible(true);
        }
    }
}
