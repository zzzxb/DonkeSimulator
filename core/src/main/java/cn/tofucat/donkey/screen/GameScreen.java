package cn.tofucat.donkey.screen;

import cn.tofucat.donkey.componse.Mill;
import cn.tofucat.donkey.condition.ConditionChecker;
import cn.tofucat.donkey.condition.ConditionManager;
import cn.tofucat.donkey.config.Config;
import cn.tofucat.donkey.config.ConfigManager;
import cn.tofucat.donkey.config.GameConsts;
import cn.tofucat.donkey.core.GameState;
import cn.tofucat.donkey.core.GameStateManager;
import cn.tofucat.donkey.manager.*;

import static cn.tofucat.donkey.manager.InputManager.InputAction;

import cn.tofucat.donkey.ui.*;
import cn.tofucat.donkey.utils.CameraController;
import cn.tofucat.donkey.utils.DebugDrawer;
import cn.tofucat.donkey.utils.FontManager;
import cn.tofucat.donkey.utils.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;

public class GameScreen extends ScreenAbstract {
    private SpriteBatch batch;
    private FitViewport viewport;
    private OrthographicCamera camera;
    private CameraController cameraController;

    private MillManager millManager;
    private TextureRegion background;
    private TextureAtlas textureAtlas;
    private Rectangle shareRec;
    private Float bgX, bgY;

    private Stage uiStage;
    private ShopUI shopUI;
    private HudUI hud;

    @Override
    public void show() {
        batch = track(new SpriteBatch());
        camera = new OrthographicCamera();
        cameraController = new CameraController(camera);
        viewport = new FitViewport(GameConsts.WORLD_WIDTH, GameConsts.WORLD_HEIGHT, camera);
        viewport.apply();
        shareRec = new Rectangle();
        textureAtlas = track(new TextureAtlas(Gdx.files.internal("ui/ui.atlas")));
        background = textureAtlas.findRegion("background/main");

        // ✅ 先初始化游戏数据（包括 ConditionManager 注册条件）
        initGame();

        // ✅ 再加载 UI（此时条件已注册）
        loadUI();

        setupInputProcessor();
    }


    @Override
    public void render(float delta) {
        handleInput(delta);
        cameraController.update(delta);
        startBlinking(delta);
        millManager.update(viewport, uiStage, hud, delta);
        hud.setFlourLabel(PlayerManager.getInstance().getFlour());
        ScreenUtils.clear(Color.BLACK);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        gameDraw(batch);
        debugDraw(batch);
        batch.end();

        if(GameStateManager.isPlaying() || GameStateManager.is(GameState.DIALOG)) {
            uiStage.act(delta);
            uiStage.draw();
        }
    }

    private void debugCheck() {
        if (Config.getBoolean("draw.debug", false)) {
            Log.info(this, "开启调试绘制工具");
            DebugDrawer.enable();
        }
    }

    private void initGame() {
        ConfigManager.init();
        ConditionChecker.setEvaluator(new GameEvaluator());
        StoryManager.getInstance().setTextureAtlas(textureAtlas);
        ShopManager.getInstance().init(cameraController);
        millManager = MillManager.getInstance().init(textureAtlas);
        GameStateManager.reset(GameState.PLAYING);
        GameStateManager.push(GameState.OPEN_WAITING);
        cameraController.zoomToNearest();
        Sprite spriteTop = MillManager.getInstance().getMills().first().getSpriteTop();
        // 开始游戏镜头跟随第一个磨盘
        cameraController.follow(
            new Vector2(spriteTop.getX() + spriteTop.getWidth() / 2,
                spriteTop.getY() + spriteTop.getHeight() / 2), 3f);
        debugCheck();
    }

    private void loadUI() {
        VisUI.load();
        FontManager.init();
        uiStage = new Stage(new ScreenViewport());
        hud = new HudUI(uiStage, textureAtlas);
        hud.createIcon();
        shopUI = new ShopUI(uiStage, textureAtlas);
        shopUI.createMenuButton();

        ConditionManager.getInstance().addListener((targetId, isUnlocked) -> {
            Log.debug(this, "🔔 条件变化: {} -> {}", targetId, isUnlocked);
            if (shopUI != null) {
                Log.debug(this, "✅ shopUI 不为 null，刷新商店");
                shopUI.refreshAllItems();
            } else {
                Log.debug(this, "❌ shopUI 为 null！");
            }
        });
    }

    private void setupInputProcessor() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        multiplexer.addProcessor(new InputAdapter() {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (GameStateManager.isPlaying()) {
                    Vector3 worldPos = viewport.unproject(new Vector3(screenX, screenY, 0));
                    handleMillClick(worldPos);
                }
                return true;
            }
        });

        Gdx.input.setInputProcessor(multiplexer);
    }

    private void handleInput(float delta) {
        if (((GameStateManager.is(GameState.OPEN_WAITING) && InputManager.isTouchJustPressed()))) {
            GameStateManager.replace(GameState.OPEN_ANIMATING);
            float duration = Config.getFloat("opening.animation.duration", 2f);
            cameraController.smoothZoomOut(0.30f, duration)
                .onZoomComplete(() -> {
                    Mill firstMill = millManager.getMills().first();
                    firstMill.initMill();
                    cameraController.stopFollow();
                    GameStateManager.pop();
                    StoryManager.getInstance().checkAndShow(uiStage);
                });
        }
        if (GameStateManager.isPlaying()) {
            // 开发测试使用
            InputManager.whenJustPressed(InputAction.RESET).then(this::initGame);
            InputManager.whenJustPressed(InputAction.QUITE).then(Gdx.app::exit);
        }
    }

    private void handleMillClick(Vector3 worldPos) {
        for (int i = 0; i < millManager.millSize(); i++) {
            Mill mill = millManager.getMills().get(i);
            Sprite sprite = mill.getSpriteBottom();
            shareRec.set(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
            if (shareRec.contains(worldPos.x, worldPos.y)) {
                mill.startRotate();
                break;
            }
        }
    }

    private void startBlinking(float delta) {
        Mill firstMill = millManager.getMills().first();
        if (GameStateManager.is(GameState.OPEN_ANIMATING)) {
            if (!firstMill.isRotating()) {
                firstMill.setLimitDegrees(360f);
                firstMill.setRotationDegrees(2f);
            }
            firstMill.startRotate();
        }
    }

    private void gameDraw(SpriteBatch batch) {
        batch.draw(background,
            camera.position.x - viewport.getWorldWidth() / 2,
            camera.position.y - viewport.getWorldHeight() / 2,
            camera.viewportWidth, camera.viewportHeight);
        for (int i = 0; i < millManager.getMills().size; i++) {
            millManager.getMills().get(i).draw(batch);
        }
    }

    private void debugDraw(SpriteBatch batch) {
        if (DebugDrawer.isEnabled()) {
            for (int i = 0; i < millManager.getMills().size; i++) {
                millManager.getMills().get(i).debugDrawer(batch);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        cameraController.moveTo(cameraController.getOldX(), cameraController.getOldY());
        camera.update();
        if (uiStage != null) {
            uiStage.getViewport().update(width, height, true);
            uiStage.getCamera().update();
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        DebugDrawer.dispose();
        VisUI.dispose();
    }
}
