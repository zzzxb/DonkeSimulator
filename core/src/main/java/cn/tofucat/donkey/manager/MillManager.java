package cn.tofucat.donkey.manager;

import cn.tofucat.donkey.componse.Donkey;
import cn.tofucat.donkey.componse.Mill;
import cn.tofucat.donkey.condition.ConditionManager;
import cn.tofucat.donkey.config.GameConsts;
import cn.tofucat.donkey.ui.HudUI;
import cn.tofucat.donkey.ui.ShopItemData;
import cn.tofucat.donkey.ui.StoryLogDialog;
import cn.tofucat.donkey.ui.Toast;
import cn.tofucat.donkey.utils.CameraController;
import cn.tofucat.donkey.utils.Log;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.Viewport;


/**
 *
 * @author zzzxb
 * 2026/8/15
 */
public class MillManager {
    private static MillManager instance;

    private final int COLUMN_MAX = 8;
    public static final int MAX_MILL = 48;
    private final float UPGRADE_ROTATION_DEGREES = 0.5f;
    private final Array<Mill> mills = new Array<>();
    private TextureAtlas textureAtlas;

    // 点击次数最大等级
    public final static int CLICK_LEVEL_MAX = 12;
    // 点击次数当前等级
    private int clickLevel = 1;
    // 当前点击限制次数
    private int limitClicks;

    // 最大旋转角度
    public final static float ROTATION_DEGREES_MAX = 360f;
    private final int DEGREES_LEVEL_MAX = 6;
    // 旋转每帧旋转角度
    private float rotationDegrees;
    // 限制旋转角度
    private float limitDegrees;
    private int degreesLevel = 1;
    private int flourProduction = 1;
    private int donkeyCount = 0;

    private MillManager() {
    }

    public static MillManager getInstance() {
        if (instance == null) {
            instance = new MillManager();
        }
        return instance;
    }

    public MillManager init(TextureAtlas textureAtlas) {
        this.textureAtlas = textureAtlas;
        clickLevel = 1;
        limitClicks = CLICK_LEVEL_MAX;
        rotationDegrees = UPGRADE_ROTATION_DEGREES;
        limitDegrees = ROTATION_DEGREES_MAX / limitClicks;
        mills.clear();
        mills.add(createMill());
        return instance;
    }

    public boolean buyDonkey() {
        if (millSize() <= donkeyCount) {
            Log.info(this, "需要更多的磨盘才能购买小毛驴");
            return false;
        }
        for (int i = 0; i < mills.size; i++) {
            Mill mill = mills.get(i);
            if (mill.getDonkey() != null) continue;
            Sprite sprite = textureAtlas.createSprite("hud/donkey_transparent");
            sprite.setSize(16, 16);
            sprite.setOrigin(mill.getSpriteTop().getOriginX(), mill.getSpriteTop().getOriginY());
            sprite.setPosition(mill.getSpriteTop().getX(), mill.getSpriteTop().getY());
            mill.setDonkey(new Donkey(sprite));
            donkeyCount++;
            break;
        }
        return true;
    }

    public boolean upgradeFlourProduction() {
        this.flourProduction += 1;
        return true;
    }

    public boolean upgradeMillRotationDegrees() {
        this.degreesLevel = Math.min(DEGREES_LEVEL_MAX, degreesLevel + 1);
        rotationDegrees = Math.max(6, rotationDegrees + 0.2f);
        for (int i = 0; i < millSize(); i++) {
            mills.get(i).setRotationDegrees(rotationDegrees);
        }
        Log.info(this, "升级推磨速度, 最大等级: {} 当前等级: {}, 速度: {}m/s",
            DEGREES_LEVEL_MAX, this.degreesLevel, this.rotationDegrees / DEGREES_LEVEL_MAX);
        return true;
    }

    public boolean upgradeStrength() {
        this.clickLevel = Math.min(CLICK_LEVEL_MAX, this.clickLevel + 1);
        this.limitClicks = CLICK_LEVEL_MAX - Math.max(0, this.clickLevel - 1);
        this.limitDegrees = ROTATION_DEGREES_MAX / limitClicks;
        for (int i = 0; i < millSize(); i++) {
            mills.get(i).setClicks(this.limitClicks);
        }
        Log.info(this, "升级推磨力量, 最大等级: {} 当前等级: {}, 一圈需要推动: {}次",
            CLICK_LEVEL_MAX, this.clickLevel, this.limitClicks);
        return true;
    }

    public boolean addMill(CameraController cameraController, Mill mill) {
        if (mills.size >= MAX_MILL || cameraController.isMoving() || cameraController.isZooming()) {
            return false;
        }
        mills.add(mill);
        int millsSize = getMills().size;
        float cameraX = cameraController.getCamera().position.x;
        float cameraY = cameraController.getCamera().position.y;
        if (millsSize <= COLUMN_MAX) {
            cameraX += mill.getSpriteBottom().getWidth() / 2f;
            cameraController.smoothZoomOut(cameraController.getCamera().zoom + 0.08f, 0.2f);
        }
        if (millsSize % COLUMN_MAX == 1) {
            cameraY -= mill.getSpriteBottom().getHeight() / 2f;
            cameraController.smoothZoomOut(cameraController.getCamera().zoom + 0.02f, 0.2f);
        }
        cameraController.smoothMoveTo(cameraX, cameraY, 0.2f);
        return true;
    }

    public Mill createMill() {
        Mill mill = new Mill();
        mill.setClicks(limitClicks);
        mill.setLimitDegrees(limitDegrees);
        mill.setRotationDegrees(rotationDegrees);
        Sprite spriteTop = textureAtlas.createSprite("mill/mill_top");
        Sprite spriteBottom = textureAtlas.createSprite("mill/mill_bottom");
        mill.setSpriteTop(spriteTop);
        mill.setSpriteBottom(spriteBottom);
        spriteTop.setSize(64, 64);
        spriteTop.setOrigin(spriteTop.getWidth() / 2, spriteTop.getHeight() / 2);
        spriteTop.setPosition(
            ((GameConsts.WINDOW_WIDTH_MIN - spriteTop.getWidth()) / 2) + ((mills.size % COLUMN_MAX) * spriteTop.getWidth()) + 16,
            ((GameConsts.WINDOW_HEIGHT_MAX - spriteTop.getHeight()) / 2) - ((int) (mills.size / COLUMN_MAX) * spriteTop.getHeight()));
        spriteBottom.setPosition(spriteTop.getX(), spriteTop.getY());
        spriteBottom.setSize(64, 64);
        for (int i = 0; i <= CLICK_LEVEL_MAX; i++) {
            Sprite sprite = textureAtlas.createSprite("char/" + i);
            sprite.setSize(8, 8);
            sprite.setPosition(spriteTop.getX() + spriteTop.getWidth() - 12, spriteTop.getY() + spriteTop.getHeight() - 12);
            mill.addNumberSprite(i, sprite);
        }
        spriteTop.setOrigin(spriteTop.getWidth() / 2, spriteTop.getHeight() / 2);
        return mill;
    }

    public void update(Viewport viewport, Stage stage, HudUI hud, float delta) {
        for (int i = 0; i < millSize(); i++) {
            Mill mill = mills.get(i);
            if (mill.getDonkey() != null) {
                mill.startRotate();
            }
            mill.update(delta);
            if (mill.getClicks() <= 0) {
                mill.setClicks(limitClicks);
                PlayerManager.getInstance().addCoils(1);
                StoryManager.getInstance().checkAndShow(stage);
                refreshTip(stage);
                hud.createLabel(viewport, i, "+" + flourProduction, mill.getSpriteTop());
                PlayerManager.getInstance().addFlour(flourProduction);
                if(mill.getDonkey() != null) {
                    PlayerManager.getInstance().addFlour(-1);
                }
            }
        }
    }

    private void refreshTip(Stage stage) {
        Array<ShopItemData> shopItems = ShopManager.getInstance().getShopItems();
        if (shopItems == null || shopItems.isEmpty()) return;

        for (int i = 0; i < shopItems.size; i++) {
            ShopItemData data = shopItems.get(i);
            if (data.isTip()) continue;

            boolean isUnlocked = ConditionManager.getInstance().isUnlocked(data.getId());
            if (isUnlocked) {
                data.disableTip();
                String tip = "解锁 " + data.getName();
                Toast.show(stage, tip);
            }
        }
    }

    public int millSize() {
        return mills.size;
    }

    public Array<Mill> getMills() {
        return mills;
    }

    public float getLimitDegrees() {
        return limitDegrees;
    }

    public float getRotationDegrees() {
        return rotationDegrees;
    }

    public int getDonkeyCount() {
        return donkeyCount;
    }
}
