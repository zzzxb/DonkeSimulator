package cn.tofucat.donkey.core;

import java.util.Stack;

/**
 * 游戏状态管理器，管理游戏运行时的状态切换。
 *
 * <p>使用示例：
 * <pre>
 * GameStateManager.push(GameState.PAUSED);  // 进入暂停
 * GameStateManager.pop();                   // 恢复之前状态
 * if (GameStateManager.is(GameState.PLAYING)) {
 *     // 更新游戏逻辑
 * }
 * </pre>
 *
 * @author zzzxb
 * @since 2026/8/15
 */
public class GameStateManager {

    private static GameState currentState = GameState.MENU;
    private static final Stack<GameState> stateStack = new Stack<>();

    private GameStateManager() {}

    /**
     * 设置当前状态（替换栈顶）。
     *
     * @param state 目标状态
     */
    public static void setState(GameState state) {
        currentState = state;
    }

    /**
     * 压入新状态（当前状态入栈，切换到新状态）。
     * <p>适用于：暂停、打开菜单、弹出对话框等场景。
     *
     * @param state 新状态
     */
    public static void push(GameState state) {
        stateStack.push(currentState);
        currentState = state;
    }

    /**
     * 替换栈顶状态（弹出当前状态，压入新状态）。
     * <p>适用于：状态流转但不改变栈深度，如 OPEN_WAITING → OPEN_ANIMATING。
     *
     * @param state 新状态
     * @return 被替换的旧状态
     */
    public static GameState replace(GameState state) {
        return currentState = state;
    }

    /**
     * 弹出状态（恢复栈顶状态）。
     */
    public static void pop() {
        if (!stateStack.isEmpty()) {
            currentState = stateStack.pop();
        }
    }

    /**
     * 判断当前状态是否等于指定状态。
     *
     * @param state 要比较的状态
     * @return true 如果当前状态等于指定状态
     */
    public static boolean is(GameState state) {
        return currentState == state;
    }

    /**
     * 判断当前是否处于游戏进行状态。
     *
     * @return true 如果当前状态为 PLAYING
     */
    public static boolean isPlaying() {
        return currentState == GameState.PLAYING;
    }

    /**
     * 获取当前状态。
     *
     * @return 当前状态
     */
    public static GameState getCurrentState() {
        return currentState;
    }

    /**
     * 清空状态栈，重置为指定状态。
     *
     * @param state 重置后的状态
     */
    public static void reset(GameState state) {
        stateStack.clear();
        currentState = state;
    }
}
