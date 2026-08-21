package cn.tofucat.donkey.ui;

import cn.tofucat.donkey.utils.FontManager;
import cn.tofucat.donkey.utils.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

/**
 * 提示框工具 - 在屏幕中央显示简短提示，自动消失
 */
public class Toast {

    /**
     * 显示提示
     * @param stage UI Stage
     * @param text 提示文字
     * @param duration 显示时长（秒）
     */
    public static void show(Stage stage, String text, float duration) {
        if (stage == null) return;

        Table toast = new Table();
        toast.setBackground(createBackground());

        // 文字
        BitmapFont font = FontManager.getFont("sys_regular", 20);
        if (font == null) {
            font = FontManager.getFont("sys_regular", 16);
        }
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);
        Label label = new Label(text, style);
        label.setAlignment(Align.center);
        toast.add(label).pad(20, 30, 20, 30);

        // 居中
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        toast.setSize(width * 0.6f, 80);
        toast.setPosition((width - toast.getWidth()) / 2, (height - toast.getHeight()) / 2);

        stage.addActor(toast);

        // 入场动画：淡入 + 缩放
        toast.setScale(0.8f);
        toast.setColor(1, 1, 1, 0);
        toast.addAction(Actions.sequence(
            Actions.delay(0.3f),
            Actions.parallel(
                Actions.scaleTo(1, 1, 0.2f, Interpolation.swingOut),
                Actions.alpha(1, 0.2f)
            ),
            Actions.delay(duration),
            Actions.parallel(
                Actions.scaleTo(0.8f, 0.8f, 0.2f, Interpolation.swingIn),
                Actions.alpha(0, 0.2f)
            ),
            Actions.removeActor()
        ));
    }

    /**
     * 显示提示（默认 1.5 秒）
     */
    public static void show(Stage stage, String text) {
        if(text == null || text.isBlank()) return;
        show(stage, text, 1.5f);
    }

    private static TextureRegionDrawable createBackground() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.1f, 0.1f, 0.15f, 0.85f));
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }
}
