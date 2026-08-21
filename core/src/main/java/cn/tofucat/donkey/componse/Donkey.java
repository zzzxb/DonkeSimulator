package cn.tofucat.donkey.componse;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 *
 * @author zzzxb
 * 2026/8/15
 */
public class Donkey {
    private Sprite sprite;
    private float rotationDegrees;

    public Donkey(Sprite sprite) {
        this.sprite = sprite;
        this.rotationDegrees = 0.2f;
    }

    public float getRotationDegrees() {
        return rotationDegrees;
    }

    public Sprite getSprite() {
        return sprite;
    }
}
