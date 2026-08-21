package cn.tofucat.donkey.componse;

import cn.tofucat.donkey.core.GameStateManager;
import cn.tofucat.donkey.manager.MillManager;
import cn.tofucat.donkey.manager.PlayerManager;
import cn.tofucat.donkey.manager.SoundManager;
import cn.tofucat.donkey.utils.DebugDrawer;
import cn.tofucat.donkey.utils.Log;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 *
 * @author zzzxb
 * 2026/8/15
 */
public class Mill {
    private Sprite spriteTop;
    private Sprite spriteBottom;
    private Sprite[] numberSprites = new Sprite[13];
    private Donkey donkey;

    private boolean isRotating;
    private int clicks;
    private float rotationDegrees;
    private float limitDegrees;
    private float countDegrees;

    public void initMill() {
        this.limitDegrees = MillManager.getInstance().getLimitDegrees();
        this.rotationDegrees = MillManager.getInstance().getRotationDegrees();
        countDegrees = 0;
        stopRotating();
    }

    public void update(float delta) {
        if (isRotating) {
            if (donkey != null) {
                rotationDegrees = donkey.getRotationDegrees();
                donkey.getSprite().rotate(rotationDegrees);
            }
            spriteTop.rotate(rotationDegrees);
            countDegrees += rotationDegrees;
            if (countDegrees >= limitDegrees) {
                isRotating = false;
                countDegrees = 0;
                if (GameStateManager.isPlaying()) {
                    // 只有游戏开始时后点击磨盘转动才算数
                    clicks--;
                    Log.debug("推送磨盘", "当前推动 {} 次", clicks);
                }
            }
        }
    }

    public void draw(SpriteBatch batch) {
        spriteBottom.draw(batch);
        spriteTop.draw(batch);
        if (donkey != null) {
            donkey.getSprite().draw(batch);
        }
        if (PlayerManager.getInstance().isAbacus()) {
            numberSprites[clicks].draw(batch);
        }
    }

    public void debugDrawer(SpriteBatch batch) {
        DebugDrawer.drawRect(batch, spriteBottom.getX(), spriteBottom.getY(), spriteBottom.getWidth(), spriteBottom.getHeight());
        DebugDrawer.drawRect(batch, spriteTop.getX(), spriteTop.getY(), spriteTop.getWidth(), spriteTop.getHeight());
    }

    public void addNumberSprite(int index, Sprite sprite) {
        numberSprites[index] = sprite;
    }

    public void startRotate() {
        if (!isRotating) {
            SoundManager.playSfx("audio/mill.mp3", 0.5f);
            isRotating = true;
        }
    }

    public void setLimitDegrees(float degrees) {
        this.limitDegrees = degrees;
    }

    public float getLimitDegrees() {
        return limitDegrees;
    }

    public Donkey getDonkey() {
        return donkey;
    }

    public void setDonkey(Donkey donkey) {
        this.donkey = donkey;
    }

    public void setSpriteTop(Sprite sprite) {
        this.spriteTop = sprite;
    }

    public void setSpriteBottom(Sprite sprite) {
        this.spriteBottom = sprite;
    }

    public Sprite getSpriteTop() {
        return spriteTop;
    }

    public Sprite getSpriteBottom() {
        return spriteBottom;
    }

    public int getClicks() {
        return clicks;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public void setRotationDegrees(float rotationDegrees) {
        this.rotationDegrees = rotationDegrees;
    }

    public float getRotationDegrees() {
        return rotationDegrees;
    }

    public void stopRotating() {
        this.isRotating = false;
    }

    public boolean isRotating() {
        return isRotating;
    }
}
