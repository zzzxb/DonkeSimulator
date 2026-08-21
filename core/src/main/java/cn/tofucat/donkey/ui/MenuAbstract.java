package cn.tofucat.donkey.ui;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisImageButton;

/**
 *
 * @author zzzxb
 * 2026/8/16
 */
public abstract class MenuAbstract extends UIAbstract {

    public abstract void createMenuButton();

    protected VisImageButton.VisImageButtonStyle createImageButtonStyle(UIResourceHolder up, UIResourceHolder down, UIResourceHolder over) {
        VisImageButton.VisImageButtonStyle style = new VisImageButton.VisImageButtonStyle();
        style.imageUp = new TextureRegionDrawable(up.regionDrawable);
        style.imageDown = new TextureRegionDrawable(down.regionDrawable);
        style.imageOver = new TextureRegionDrawable(over.regionDrawable);
        return style;
    }
}
