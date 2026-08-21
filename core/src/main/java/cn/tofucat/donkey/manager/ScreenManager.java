package cn.tofucat.donkey.manager;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;

import cn.tofucat.donkey.screen.ScreenAbstract;

/**
 * 屏幕管理器，统一管理游戏屏幕切换。
 *
 * <p>使用示例：
 * <pre>
 * ScreenManager.init(game);
 * ScreenManager.setScreen(new GameScreen());
 * </pre>
 *
 * @author zzzxb
 * @since 2026/8/15
 */
public class ScreenManager {

    private static Game game;

    private ScreenManager() {}

    /**
     * 初始化屏幕管理器。
     *
     * @param game 游戏主类实例
     */
    public static void init(Game game) {
        ScreenManager.game = game;
    }

    /**
     * 切换到指定屏幕。
     *
     * @param screen 目标屏幕
     */
    public static void setScreen(Screen screen) {
        if (game != null) {
            game.setScreen(screen);
        }
    }

    /**
     * 获取当前屏幕。
     *
     * @return 当前屏幕
     */
    public static Screen getScreen() {
        return game != null ? game.getScreen() : null;
    }

    /**
     * 获取当前屏幕并转换为指定类型。
     *
     * @param type 目标类型
     * @param <T>  类型参数
     * @return 当前屏幕，如果类型不匹配则返回 null
     */
    @SuppressWarnings("unchecked")
    public static <T extends Screen> T getScreen(Class<T> type) {
        Screen screen = getScreen();
        if (type.isInstance(screen)) {
            return (T) screen;
        }
        return null;
    }

    /**
     * 获取当前屏幕（假设是 ScreenAbstract 子类）。
     *
     * @return ScreenAbstract 实例，如果类型不匹配则返回 null
     */
    public static ScreenAbstract getScreenAbstract() {
        return getScreen(ScreenAbstract.class);
    }
}
