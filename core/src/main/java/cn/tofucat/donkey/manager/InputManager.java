package cn.tofucat.donkey.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.IntIntMap;

/**
 * 输入管理器，提供按键映射、触摸检测和链式条件调用。
 *
 * <p>支持两种使用方式：
 *
 * <p><b>1. 传统方式（返回 boolean）：</b>
 * <pre>
 * if (InputManager.isJustPressed(InputAction.RESET)) {
 *     reset();
 * }
 * </pre>
 *
 * <p><b>2. 链式方式（返回 Condition）：</b>
 * <pre>
 * InputManager.whenJustPressed(InputAction.RESET).then(this::reset);
 * InputManager.whenTouchJustPressed().then(() -> startGame());
 * </pre>
 *
 * @author zzzxb
 * @since 2026/8/15
 */
public class InputManager {

    /**
     * 输入动作常量，用于按键映射。
     */
    public interface InputAction {
        /** 重置动作 */
        int RESET = 1;
        int QUITE = 2;
        int PLUS = 3;
    }

    private static final IntIntMap keyMap = new IntIntMap();

    static {
        keyMap.put(InputAction.RESET, Input.Keys.R);
        keyMap.put(InputAction.QUITE, Input.Keys.Q);
        keyMap.put(InputAction.PLUS, Input.Keys.EQUALS);
    }

    private InputManager() {
    }

    /**
     * 检查指定动作是否正在被按住。
     *
     * <p>按住期间每帧都返回 true，适用于持续移动、长按等场景。
     *
     * @param action 动作常量，如 {@link InputAction#RESET}
     * @return true 如果按键正在被按住
     */
    public static boolean isPressed(int action) {
        int key = keyMap.get(action, -1);
        return key != -1 && Gdx.input.isKeyPressed(key);
    }

    /**
     * 检查指定动作是否刚被按下。
     *
     * <p>只在按下那一帧返回 true，适用于点击、触发事件等场景。
     *
     * @param action 动作常量，如 {@link InputAction#RESET}
     * @return true 如果按键刚被按下
     */
    public static boolean isJustPressed(int action) {
        int key = keyMap.get(action, -1);
        return key != -1 && Gdx.input.isKeyJustPressed(key);
    }

    /**
     * 检查屏幕是否正在被触摸。
     *
     * <p>触摸期间每帧都返回 true。
     *
     * @return true 如果屏幕正在被触摸
     */
    public static boolean isTouchDown() {
        return Gdx.input.isTouched();
    }

    /**
     * 检查屏幕是否刚被触摸。
     *
     * <p>只在触摸按下那一帧返回 true。
     *
     * @return true 如果屏幕刚被触摸
     */
    public static boolean isTouchJustPressed() {
        return Gdx.input.justTouched();
    }

    /**
     * 检查指定动作是否刚被按下，返回条件对象支持链式调用。
     *
     * <p>使用示例：
     * <pre>
     * InputManager.whenJustPressed(InputAction.RESET).then(this::reset);
     * </pre>
     *
     * @param action 动作常量，如 {@link InputAction#RESET}
     * @return 条件对象，可调用 {@link Condition#then(Runnable)} 执行动作
     */
    public static Condition whenJustPressed(int action) {
        return new Condition(isJustPressed(action));
    }

    /**
     * 检查指定动作是否正在被按住，返回条件对象支持链式调用。
     *
     * <p>使用示例：
     * <pre>
     * InputManager.whenPressed(InputAction.RESET).then(() -> zoomIn());
     * </pre>
     *
     * @param action 动作常量，如 {@link InputAction#RESET}
     * @return 条件对象，可调用 {@link Condition#then(Runnable)} 执行动作
     */
    public static Condition whenPressed(int action) {
        return new Condition(isPressed(action));
    }

    /**
     * 检查屏幕是否刚被触摸，返回条件对象支持链式调用。
     *
     * <p>使用示例：
     * <pre>
     * InputManager.whenTouchJustPressed().then(() -> startGame());
     * </pre>
     *
     * @return 条件对象，可调用 {@link Condition#then(Runnable)} 执行动作
     */
    public static Condition whenTouchJustPressed() {
        return new Condition(isTouchJustPressed());
    }

    /**
     * 检查屏幕是否正在被触摸，返回条件对象支持链式调用。
     *
     * <p>使用示例：
     * <pre>
     * InputManager.whenTouchDown().then(() -> movePlayer());
     * </pre>
     *
     * @return 条件对象，可调用 {@link Condition#then(Runnable)} 执行动作
     */
    public static Condition whenTouchDown() {
        return new Condition(isTouchDown());
    }

    /**
     * 获取当前触摸位置（屏幕坐标）。
     *
     * <p>坐标原点在屏幕左上角，与 LibGDX 的输入坐标一致。
     *
     * @return 触摸位置的 Vector2，如果没有触摸则返回 (0, 0)
     */
    public static Vector2 getTouchPosition() {
        return new Vector2(Gdx.input.getX(), Gdx.input.getY());
    }

    /**
     * 获取当前触摸位置（屏幕坐标），存入指定的 Vector2 对象。
     *
     * <p>避免每次调用都创建新对象，适合高频调用场景。
     *
     * @param out 用于存储结果的 Vector2 对象
     * @return out 参数本身
     */
    public static Vector2 getTouchPosition(Vector2 out) {
        out.set(Gdx.input.getX(), Gdx.input.getY());
        return out;
    }

    /**
     * 重新映射按键。
     *
     * <p>使用示例：
     * <pre>
     * InputManager.remap(InputAction.RESET, Input.Keys.F5);
     * </pre>
     *
     * @param action  动作常量，如 {@link InputAction#RESET}
     * @param keyCode 新的按键代码，如 {@link Input.Keys#F5}
     */
    public static void remap(int action, int keyCode) {
        keyMap.put(action, keyCode);
    }

    /**
     * 获取动作对应的按键代码。
     *
     * @param action 动作常量，如 {@link InputAction#RESET}
     * @return 按键代码，如果未映射则返回 -1
     */
    public static int getKeyCode(int action) {
        return keyMap.get(action, -1);
    }

    /**
     * 条件对象，封装一个布尔值并提供链式调用方法。
     *
     * <p>通常不直接创建，而是通过 {@link InputManager#whenJustPressed(int)} 等方法获取。
     *
     * <p>使用示例：
     * <pre>
     * new Condition(true).then(() -> System.out.println("条件满足"));
     * new Condition(false).orElse(() -> System.out.println("条件不满足"));
     * </pre>
     */
    public static class Condition {

        private final boolean value;

        /**
         * 构造条件对象。
         *
         * @param value 条件值
         */
        public Condition(boolean value) {
            this.value = value;
        }

        /**
         * 如果条件满足，执行指定的动作。
         *
         * <p>使用示例：
         * <pre>
         * InputManager.whenJustPressed(InputAction.RESET).then(this::reset);
         * </pre>
         *
         * @param action 条件满足时执行的动作
         * @return this，支持链式调用
         */
        public Condition then(Runnable action) {
            if (value && action != null) {
                action.run();
            }
            return this;
        }

        /**
         * 如果条件不满足，执行指定的动作。
         *
         * <p>使用示例：
         * <pre>
         * InputManager.whenJustPressed(InputAction.RESET)
         *     .then(this::reset)
         *     .orElse(() -> log("按键未按下"));
         * </pre>
         *
         * @param action 条件不满足时执行的动作
         * @return this，支持链式调用
         */
        public Condition orElse(Runnable action) {
            if (!value && action != null) {
                action.run();
            }
            return this;
        }

        /**
         * 获取条件值，用于传统的 if 判断。
         *
         * <p>使用示例：
         * <pre>
         * if (InputManager.whenJustPressed(InputAction.RESET).get()) {
         *     reset();
         * }
         * </pre>
         *
         * @return true 如果条件满足
         */
        public boolean get() {
            return value;
        }
    }
}
