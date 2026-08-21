package cn.tofucat.donkey.ui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 *
 * @author zzzxb
 * 2026/8/18
 */
public abstract class UIAbstract {

    protected static class UIResourceHolder {
        TextureAtlas.AtlasRegion atlasRegion;
        TextureRegionDrawable regionDrawable;

        public UIResourceHolder(TextureAtlas.AtlasRegion atlasRegion) {
            this.atlasRegion = atlasRegion;
            this.regionDrawable = new TextureRegionDrawable(atlasRegion);
        }
    }
}
