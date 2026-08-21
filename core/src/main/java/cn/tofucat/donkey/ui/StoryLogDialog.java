package cn.tofucat.donkey.ui;

import cn.tofucat.donkey.config.Config;
import cn.tofucat.donkey.core.GameState;
import cn.tofucat.donkey.core.GameStateManager;
import cn.tofucat.donkey.manager.SoundManager;
import cn.tofucat.donkey.manager.StoryManager;
import cn.tofucat.donkey.utils.FontManager;
import cn.tofucat.donkey.utils.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

public class StoryLogDialog {

    private final Stage stage;
    private final Array<StoryManager.StoryLogEntry> entries;
    private int currentIndex = 0;
    private VisWindow window;
    private Label titleLabel;
    private Label contentLabel;
    private VisLabel hintLabel;
    private boolean isAnimating = false;
    private boolean isFinished = false;
    private boolean canClick = false;  // ✅ 是否可以点击
    private final UIAbstract.UIResourceHolder backgroundHolder;

    // 防误触延迟（默认 1 秒）
    private float clickDelay = 1.0f;

    public StoryLogDialog(TextureAtlas atlas, Stage stage, Array<StoryManager.StoryLogEntry> entries) {
        this.stage = stage;
        this.entries = entries;
        this.clickDelay = Config.getFloat("story.click.delay", 1.0f);
        this.backgroundHolder = new UIAbstract.UIResourceHolder(atlas.findRegion("background/story"));
    }

    public void setClickDelay(float seconds) {
        this.clickDelay = seconds;
    }

    public void show() {
        if (entries == null || entries.size == 0) {
            Log.warn(StoryLogDialog.class, "没有可显示的日记");
            return;
        }
        GameStateManager.push(GameState.DIALOG);
        currentIndex = 0;
        isFinished = false;
        createWindow();
        showEntry(currentIndex);
    }

    private void createWindow() {
        window = new VisWindow("");
        window.setModal(true);
        window.setMovable(false);
        window.setBackground(backgroundHolder.regionDrawable);
//        window.setBackground(createBackground());

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        window.setSize(width, height);
        window.center();

        VisTable table = new VisTable();
        table.pad(40);
        table.top();

        // 标题：32px，黑色，居中
        BitmapFont titleFont = FontManager.getFont("sys_regular", 32);
        Label.LabelStyle titleStyle = new Label.LabelStyle(titleFont, Color.BLACK);
        titleLabel = new Label("", titleStyle);
        titleLabel.setAlignment(Align.center);
        titleLabel.setWrap(true);
        table.add(titleLabel).padBottom(10).fillX().center().row();

        // 内容：24px，黑色，左对齐，可换行
        BitmapFont contentFont = FontManager.getFont("sys_regular", 24);
        Label.LabelStyle contentStyle = new Label.LabelStyle(contentFont, Color.BLACK);
        contentLabel = new Label("", contentStyle);
        contentLabel.setAlignment(Align.topLeft);
        contentLabel.setWrap(true);
        table.add(contentLabel).padTop(10).fill().expand().row();

        // 底部提示：16px，灰色，居中
        BitmapFont hintFont = FontManager.getFont("sys_regular", 16);
        Label.LabelStyle hintStyle = new Label.LabelStyle(hintFont, Color.GRAY);
        hintLabel = new VisLabel("", hintStyle);
        hintLabel.setAlignment(Align.center);
        table.add(hintLabel).padTop(20).fillX().center();

        window.add(table).fill().expand();

        window.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isAnimating && canClick) {
                    onWindowClick();
                }
            }
        });

        stage.addActor(window);

        // 入场动画
        window.setScale(0.1f);
        window.setColor(1, 1, 1, 0);
        window.addAction(
            Actions.parallel(
                Actions.scaleTo(1, 1, 0.35f, Interpolation.swingOut),
                Actions.alpha(1, 0.3f)
            )
        );
    }

    private void showEntry(int index) {
        if (index >= entries.size) {
            closeWindow();
            return;
        }

        StoryManager.StoryLogEntry entry = entries.get(index);

        SoundManager.playMusic("audio/evening-breeze.mp3");
        if (entry.getId().equals("story_001")) {
            SoundManager.playSfx("story/1.mp3");
        }
        String titleText = "第 " + entry.getChapter() + " 章 · " + entry.getTitle();
        if (entries.size > 1) {
            titleText += " (" + (index + 1) + "/" + entries.size + ")";
        }
        titleLabel.setText(titleText);
        contentLabel.setText(entry.getContent());

        if (index == entries.size - 1) {
            hintLabel.setText("─── 点击关闭 ───");
        } else {
            hintLabel.setText("─── 点击继续 ───");
        }

        // ✅ 刚显示时不能点击
        canClick = false;

        // 淡入动画
        contentLabel.setColor(1, 1, 1, 0);
        contentLabel.addAction(Actions.alpha(1, 0.3f));
        titleLabel.setColor(1, 1, 1, 0);
        titleLabel.addAction(Actions.alpha(1, 0.3f));

        // ✅ 延迟 clickDelay 秒后，允许点击
        window.addAction(
            Actions.sequence(
                Actions.delay(clickDelay),
                Actions.run(() -> {
                    canClick = true;
                    Log.debug(StoryLogDialog.class, "已延迟 {} 秒，可以点击", clickDelay);
                })
            )
        );
    }

    private void onWindowClick() {
        if (isAnimating) return;

        if (currentIndex >= entries.size - 1) {
            // 最后一条：直接关闭
            closeWindow();
            SoundManager.playMusic("audio/bgm.mp3");
            GameStateManager.pop();
            return;
        }

        // 翻页
        isAnimating = true;
        currentIndex++;

        window.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.alpha(0, 0.2f),
                    Actions.scaleTo(0.9f, 0.9f, 0.2f)
                ),
                Actions.run(() -> {
                    showEntry(currentIndex);
                    window.addAction(
                        Actions.parallel(
                            Actions.alpha(1, 0.25f),
                            Actions.scaleTo(1, 1, 0.25f, Interpolation.swingOut)
                        )
                    );
                    isAnimating = false;
                })
            )
        );
    }

    public void close() {
        closeWindow();
    }

    private void closeWindow() {
        if (isAnimating) return;
        isAnimating = true;
        canClick = false;

        window.addAction(
            Actions.sequence(
                Actions.parallel(
                    Actions.scaleTo(0.1f, 0.1f, 0.3f, Interpolation.swingIn),
                    Actions.alpha(0, 0.3f)
                ),
                Actions.removeActor(),
                Actions.run(() -> {
                    isAnimating = false;
                    isFinished = true;
                    Log.debug(StoryLogDialog.class, "日记弹窗关闭");
                })
            )
        );
    }

    public boolean isFinished() {
        return isFinished;
    }

    private TextureRegionDrawable createBackground() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0.95f, 0.9f, 0.8f, 0.95f));
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }
}
