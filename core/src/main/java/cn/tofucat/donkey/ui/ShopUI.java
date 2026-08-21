package cn.tofucat.donkey.ui;

import cn.tofucat.donkey.condition.ConditionManager;
import cn.tofucat.donkey.config.Config;
import cn.tofucat.donkey.manager.ShopManager;
import cn.tofucat.donkey.manager.SoundManager;
import cn.tofucat.donkey.manager.StoryManager;
import cn.tofucat.donkey.utils.FontManager;
import cn.tofucat.donkey.utils.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.*;

public class ShopUI extends MenuAbstract {
    private Stage stage;
    private TextureAtlas atlas;

    UIResourceHolder shopUpHolder;
    UIResourceHolder shopDownHolder;
    UIResourceHolder shopOverHolder;
    UIResourceHolder backgroundHolder;
    UIResourceHolder buyUpHolder;
    UIResourceHolder buyDownHolder;
    UIResourceHolder buyOverHolder;
    ObjectMap<String, ShopItemUIGroup> itemUiGroupMap;


    public ShopUI(Stage stage, TextureAtlas atlas) {
        this.stage = stage;
        this.atlas = atlas;
        this.itemUiGroupMap = new ObjectMap<>();
        shopUpHolder = new UIResourceHolder(atlas.findRegion("button/shop_up"));
        shopDownHolder = new UIResourceHolder(atlas.findRegion("button/shop_down"));
        shopOverHolder = new UIResourceHolder(atlas.findRegion("button/shop_over"));
        buyUpHolder = new UIResourceHolder(atlas.findRegion("button/buy_up"));
        buyDownHolder = new UIResourceHolder(atlas.findRegion("button/buy_down"));
        buyOverHolder = new UIResourceHolder(atlas.findRegion("button/buy_over"));
        backgroundHolder = new UIResourceHolder(atlas.findRegion("background/store"));
    }


    @Override
    public void createMenuButton() {
        VisImageButton shopButton = new VisImageButton(createImageButtonStyle(shopUpHolder, shopDownHolder, shopOverHolder));
        shopButton.setSize(32, 32);
        shopButton.setPosition(32, 8);
        shopButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showShopDialog();
            }
        });
        stage.addActor(shopButton);
    }

    private void showShopDialog() {
        SoundManager.playMusic("audio/store.mp3");
        VisWindow window = new VisWindow("");
        window.setModal(true);
        window.setMovable(false);
        window.setBackground(backgroundHolder.regionDrawable);
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        window.setSize(screenWidth, screenHeight);

        VisTable mainTable = new VisTable();
        mainTable.top().left();

        VisTable itemTable = new VisTable();
        itemTable.setBackground(createColorDrawable(new Color(0, 0, 0, 0.2f)));
        itemTable.top().left().pad(10);

        itemTable.setDebug(Config.getBoolean("draw.debug"));

        Array<ShopItemData> shopItems = ShopManager.getInstance().getShopItems();
        for (int i = 0; i < shopItems.size; i++) {
            ShopItemData item = shopItems.get(i);
            ShopItemUIGroup group = createShopItemRow(item);
            updateItemState(group, item);
            VisTable row = group.getRow();
            itemTable.add(row).colspan(5).fillX().padBottom(5);
            itemTable.row();
        }

        VisScrollPane scrollPane = new VisScrollPane(itemTable);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setForceScroll(false, false);
        scrollPane.setFadeScrollBars(false);
        scrollPane.getStyle().vScroll = null;
        scrollPane.getStyle().vScrollKnob = null;

        // ✅ 底部按钮 - 使用中文样式
        VisTextButton.VisTextButtonStyle buttonStyle =
            new VisTextButton.VisTextButtonStyle(
                VisUI.getSkin().get(VisTextButton.VisTextButtonStyle.class)
            );
        buttonStyle.font = FontManager.getFont("sys_regular", 20);

        VisTable bottomTable = new VisTable();
        bottomTable.right();
        VisTextButton closeBtn = new VisTextButton("离开", buttonStyle);
        closeBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                window.remove();
                SoundManager.playMusic("audio/bgm.mp3");
            }
        });
        bottomTable.add(closeBtn);

        mainTable.add(scrollPane).width(Gdx.graphics.getWidth() - 64).height(Gdx.graphics.getHeight() - 64).row();
        mainTable.add(bottomTable).fillX().padTop(5);

        window.add(mainTable).pad(20);
        stage.addActor(window);
    }

    private TextureRegionDrawable createColorDrawable(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private ShopItemUIGroup createShopItemRow(ShopItemData data) {
        if (itemUiGroupMap.containsKey(data.getId())) {
            return itemUiGroupMap.get(data.getId());
        }

        ShopItemUIGroup group = new ShopItemUIGroup();
        itemUiGroupMap.put(data.getId(), group);

        VisTable row = new VisTable();
        group.setRow(row);
        row.left();

        // 图标
        VisImage icon = new VisImage(atlas.findRegion(data.getIconName()));
        group.setIcon(icon);
        row.add(icon).size(64, 64);

        // 名称
        Label.LabelStyle style = new Label.LabelStyle(FontManager.getFont("sys_regular", 16), Color.BLACK);
        VisLabel nameLabel = new VisLabel();
        nameLabel.setStyle(style);
        group.setNameLabel(nameLabel);
        nameLabel.setText(data.getName());
        nameLabel.setColor(Color.BLACK);
        nameLabel.setEllipsis(true);
        row.add(nameLabel).width(100).padLeft(10).left();

        // 描述
        VisLabel descriptionLabel = new VisLabel();
        group.setDescriptionLabel(descriptionLabel);
        descriptionLabel.setStyle(style);
        descriptionLabel.setText(data.getDescription());
        descriptionLabel.setColor(Color.BLACK);
        descriptionLabel.setEllipsis(true);
        row.add(descriptionLabel).minWidth(120).maxWidth(280).padLeft(10).padRight(10).left().expandX().fillX();

        // 价格：图标 + 数字
        UIResourceHolder flourIconHolder = new UIResourceHolder(atlas.findRegion("hud/flour_a"));
        VisTable priceTable = new VisTable();
        priceTable.left();

        VisImage priceIcon = new VisImage(flourIconHolder.regionDrawable);
        priceIcon.setSize(24, 24);
        priceTable.add(priceIcon).size(24, 24).padRight(4);

        VisLabel priceLabel = new VisLabel();
        group.setPriceLabel(priceLabel);
        priceLabel.setStyle(style);
        priceLabel.setText(String.valueOf(data.getPrice()));
        priceLabel.setColor(Color.GOLD);
        priceTable.add(priceLabel);

        row.add(priceTable).width(60).padLeft(10).left();

        // 状态标签
        VisLabel statusLabel = new VisLabel();
        group.setStatusLabel(statusLabel);
        statusLabel.setStyle(style);
        statusLabel.setColor(Color.RED);
        statusLabel.setVisible(false);
        statusLabel.setAlignment(Align.center);

// 购买按钮
        VisImageButton buyBtn = new VisImageButton(createImageButtonStyle(buyUpHolder, buyDownHolder, buyOverHolder));
        group.setBuyBtn(buyBtn);

// ✅ 购买按钮监听 - 显示 Toast 提示
        buyBtn.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SoundManager.playSfx("audio/pickupCoin.wav", 0.8f);
                ShopManager.BuyResult result = ShopManager.getInstance().buy(data, group);

                switch (result) {
                    case NOT_ENOUGH_FLOUR:
                        Toast.show(stage, "💸 面粉不足！");
                        break;
                    case NOT_UNLOCKED:
                        Toast.show(stage, "🔒 未解锁");
                        break;
                    case NO_STOCK:
                        Toast.show(stage, "📦 已售罄");
                        break;
                    case SUCCESS:
                        // ✅ 只更新当前商品，不刷新全部
                        updateItemState(group, data);
                        break;
                    default:
                        Toast.show(stage, "❌ 购买失败");
                        break;
                }
            }
        });

        // 用 Stack 让状态标签和购买按钮重叠
        Stack actionStack = new Stack();
        actionStack.add(statusLabel);
        actionStack.add(buyBtn);
        row.add(actionStack).size(72, 32).padLeft(10);

        updateItemState(group, data);

        return group;
    }

    private void updateItemState(ShopItemUIGroup group, ShopItemData data) {
        boolean isUnlocked = ConditionManager.getInstance().isUnlocked(data.getId());
        boolean hasStock = data.isStock();
        boolean isEnable = data.isEnable();


        Log.debug(this, "═══════════════════════════════════");
        Log.debug(this, "📦 更新商品状态: {}", data.getName());
        Log.debug(this, "  🔓 isUnlocked = {}", isUnlocked);
        Log.debug(this, "  📦 hasStock   = {}", hasStock);
        Log.debug(this, "  ✅ isEnable   = {}", isEnable);
        Log.debug(this, "  🛒 按钮可见   = {}", isUnlocked && hasStock && isEnable);
        Log.debug(this, "═══════════════════════════════════");

        boolean showBuyBtn = isUnlocked && hasStock && isEnable;
        group.getBuyBtn().setVisible(showBuyBtn);
        group.getBuyBtn().setDisabled(!showBuyBtn);

        VisLabel statusLabel = group.getStatusLabel();
        if (!isUnlocked) {
            statusLabel.setText("🔒 未解锁");
            statusLabel.setVisible(true);
        } else if (!hasStock) {
            statusLabel.setText("📦 已售罄");
            statusLabel.setVisible(true);
        } else if (!isEnable) {
            statusLabel.setText("💸 已禁用");
            statusLabel.setVisible(true);
        } else {
            statusLabel.setVisible(false);
        }

        group.updatePrice(data.getPrice());
    }

    public void refreshAllItems() {
        Log.debug(this, "🔄 refreshAllItems 被调用");
        for (ObjectMap.Entry<String, ShopItemUIGroup> entry : itemUiGroupMap) {
            String itemId = entry.key;
            ShopItemData data = ShopManager.getInstance().getItem(itemId);
            if (data != null) {
                updateItemState(entry.value, data);
            }
        }
    }
}
