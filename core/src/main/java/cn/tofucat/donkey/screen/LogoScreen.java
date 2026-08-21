package cn.tofucat.donkey.screen;

import cn.tofucat.donkey.config.Config;
import cn.tofucat.donkey.manager.ScreenManager;
import cn.tofucat.donkey.utils.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * 品牌开场画面，展示多个 Logo 并带淡入淡出效果。
 *
 * <p>流程：黑屏 → 自己的 Logo（淡入→保持→淡出）→ LibGDX Logo（淡入→保持→淡出）→ 主菜单
 * <p>任意点击或按键可快速跳过当前 Logo。
 *
 * @author zzzxb
 * @since 2026/8/15
 */
public class LogoScreen extends ScreenAbstract {

    private static final float WORLD_WIDTH = 800;
    private static final float WORLD_HEIGHT = 480;

    /**
     * Logo 配置项
     */
    private static class LogoItem {
        final String path;
        final float fadeIn;
        final float hold;
        final float fadeOut;
        Texture texture;

        LogoItem(String path, float fadeIn, float hold, float fadeOut) {
            this.path = path;
            this.fadeIn = fadeIn;
            this.hold = hold;
            this.fadeOut = fadeOut;
        }
    }

    private OrthographicCamera camera;
    private FitViewport viewport;
    private SpriteBatch batch;
    private final float fadeIn = Config.getFloat("opening.logo.fadeIn", 0.8f);
    private final float fadeHold = Config.getFloat("opening.logo.fadeHold", 1.5f);
    private final float fadeOut = Config.getFloat("opening.logo.fadeOut", 0.6f);

    private final LogoItem[] logos = {
        new LogoItem("WuMei.png", fadeIn, fadeHold, fadeOut),
    };

    private int currentIndex = 0;
    private float alpha = 0f;
    private float timer = 0f;
    private int phase = 0; // 0=淡入, 1=保持, 2=淡出
    private boolean allDone = false;
    private boolean isSkipping = false;
    private boolean texturesLoaded = false;

    private static final float SKIP_SPEED = 4.0f;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera);
        viewport.apply();
        camera.position.set(WORLD_WIDTH / 2, WORLD_HEIGHT / 2, 0);
        camera.update();

        batch = new SpriteBatch();

        // 延迟加载纹理（在 render 中加载，避免卡顿）
        texturesLoaded = false;

        Log.debug("LogoScreen", "Logo 开场已启动");
    }

    @Override
    public void render(float delta) {
        // 第一帧加载纹理
        if (!texturesLoaded) {
            loadTextures();
            texturesLoaded = true;
        }

        // 处理跳过
        handleSkipInput();

        // 更新动画
        if (!allDone) {
            updateAnimation(delta);
        }

        // 绘制
        ScreenUtils.clear(Color.BLACK);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        if (currentIndex < logos.length && !allDone) {
            LogoItem current = logos[currentIndex];
            if (current.texture != null) {
                batch.setColor(1f, 1f, 1f, alpha);
                Texture tex = current.texture;
                float x = (WORLD_WIDTH - tex.getWidth()) / 2;
                float y = (WORLD_HEIGHT - tex.getHeight()) / 2;
                batch.draw(tex, x, y);
                batch.setColor(Color.WHITE);
            }
        }

        batch.end();

        // 完成后跳转
        if (allDone) {
            Log.debug("LogScreen", "Logo 开场完成，切换到游戏屏幕");
            ScreenManager.setScreen(new GameScreen());
        }
    }

    /**
     * 加载所有纹理
     */
    private void loadTextures() {
        for (LogoItem item : logos) {
            if (item.texture == null) {
                try {
                    item.texture = new Texture(item.path);
                    Log.debug(this, "加载纹理: {}", item.path);
                } catch (Exception e) {
                    Log.error(this, "加载纹理失败: {}", item.path, e);
                }
            }
        }
    }

    /**
     * 更新动画
     */
    private void updateAnimation(float delta) {
        if (currentIndex >= logos.length) {
            allDone = true;
            return;
        }

        LogoItem current = logos[currentIndex];
        float speed = isSkipping ? SKIP_SPEED : 1f;
        timer += delta * speed;

        switch (phase) {
            case 0: // 淡入
                alpha = MathUtils.clamp(timer / current.fadeIn, 0f, 1f);
                if (timer >= current.fadeIn) {
                    phase = 1;
                    timer = 0f;
                    alpha = 1f;
                    Log.debug(this, "Logo[" + currentIndex + "] 淡入完成，进入保持");
                }
                break;

            case 1: // 保持
                if (timer >= current.hold) {
                    phase = 2;
                    timer = 0f;
                    Log.debug(this, "Logo[" + currentIndex + "] 保持完成，进入淡出");
                }
                break;

            case 2: // 淡出
                alpha = 1f - MathUtils.clamp(timer / current.fadeOut, 0f, 1f);
                if (timer >= current.fadeOut) {
                    nextLogo();
                }
                break;
        }
    }

    /**
     * 切换到下一个 Logo
     */
    private void nextLogo() {
        alpha = 0f;
        timer = 0f;
        phase = 0;
        currentIndex++;
        isSkipping = false;
        Log.debug(this, "切换到下一个 Logo, index=" + currentIndex);

        if (currentIndex >= logos.length) {
            allDone = true;
            Log.debug(this, "所有 Logo 展示完成");
        }
    }

    /**
     * 处理跳过输入
     */
    private void handleSkipInput() {
        if (allDone) return;

        boolean skip = Gdx.input.isTouched()
            || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
            || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
            || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY);

        if (!skip) return;

        switch (phase) {
            case 0: // 淡入中 → 立即进入淡出
                LogoItem current = logos[currentIndex];
                timer = current.fadeIn + current.hold;
                phase = 2;
                timer = 0f;
                alpha = 1f;
                isSkipping = false;
                Log.debug(this, "跳过：淡入阶段 → 进入淡出");
                break;

            case 1: // 保持中 → 立即进入淡出
                phase = 2;
                timer = 0f;
                isSkipping = false;
                Log.debug(this, "跳过：保持阶段 → 进入淡出");
                break;

            case 2: // 淡出中 → 加速淡出
                isSkipping = true;
                Log.debug(this, "跳过：加速淡出");
                break;
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
            batch = null;
        }
        for (LogoItem item : logos) {
            if (item.texture != null) {
                item.texture.dispose();
                item.texture = null;
            }
        }
        Log.info(this, "Logo 开场资源已释放");
    }

    @Override
    public void hide() {
        Log.debug(this, "Logo 开场被隐藏");
    }

    @Override
    public void pause() {
        Log.debug(this, "Logo 开场被暂停");
    }

    @Override
    public void resume() {
        Log.debug(this, "Logo 开场被恢复");
    }
}
