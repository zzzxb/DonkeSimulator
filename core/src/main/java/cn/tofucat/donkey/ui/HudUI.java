package cn.tofucat.donkey.ui;

import cn.tofucat.donkey.config.GameConsts;
import cn.tofucat.donkey.core.GameState;
import cn.tofucat.donkey.manager.MillManager;
import cn.tofucat.donkey.utils.CameraController;
import cn.tofucat.donkey.utils.FontManager;
import cn.tofucat.donkey.utils.Log;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;

/**
 *
 * @author zzzxb
 * 2026/8/18
 */
public class HudUI extends UIAbstract {
    private Stage stage;
    private TextureAtlas atlas;
    private VisLabel flourLabel;
    private UIResourceHolder flourHolder;
    private VisLabel[] labels;
    private Label.LabelStyle style = new Label.LabelStyle(FontManager.getFont("sys_regular", 16), Color.BLACK);

    public HudUI(Stage stage, TextureAtlas atlas) {
        this.stage = stage;
        this.atlas = atlas;
        this.labels = new VisLabel[MillManager.MAX_MILL];
        flourHolder = new UIResourceHolder(atlas.findRegion("hud/flour_a"));
    }

    public void createLabel(Viewport viewport, int index, String flour, Sprite sprite) {
        VisLabel label = labels[index];
        if (label == null) {
            label = new VisLabel(flour, style);
            labels[index] = label;
            label.setColor(Color.BLACK);
            // ✅ 设置原点为中心（这样缩放从中心展开）
            label.setOrigin(Align.center);
            stage.addActor(label);
        }

        // ✅ 如果 Actor 已被移除，重新添加到 Stage
        if (label.getStage() == null) {
            stage.addActor(label);
        }

        // 计算屏幕坐标
        Vector3 worldPos = new Vector3(
            sprite.getX() + sprite.getWidth() / 2,
            sprite.getY() + sprite.getHeight() / 2,
            0
        );
        Vector3 screenPos = viewport.project(worldPos);

        // ✅ 重置状态
        label.setPosition(screenPos.x, screenPos.y);
        label.setScale(0f);                    // 从 0 开始
        label.setColor(0, 0, 0, 1f);           // 完全不透明
        label.setText(flour);

        // ✅ 清除之前的 Action（防止冲突）
        label.clearActions();

        label.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.moveBy(0, 30, 1f, Interpolation.pow2Out),
                    Actions.alpha(0f, 0.8f, Interpolation.fade)
                ),
                Actions.removeActor()  // 动画结束后移除
            )
        );
    }

    public void createIcon() {
        VisImage image = new VisImage(flourHolder.regionDrawable);
        image.setSize(16, 16);
        image.setPosition(16, stage.getHeight() - 24);
        flourLabel = new VisLabel("0", Color.BLACK);
        flourLabel.setSize(16, 16);
        flourLabel.setPosition(40, stage.getHeight() - 24);
        stage.addActor(image);
        stage.addActor(flourLabel);
    }

    public void setFlourLabel(int num) {
        if (flourLabel != null) {
            flourLabel.setText(num);
        }
    }
}
