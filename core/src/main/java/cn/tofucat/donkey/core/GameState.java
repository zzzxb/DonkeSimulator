package cn.tofucat.donkey.core;

/**
 * 游戏状态枚举。
 *
 * @author zzzxb
 * @since 2026/8/15
 */
public enum GameState {
    /** 主菜单 */
    MENU,
    /** 游戏进行中 */
    PLAYING,
    /** 暂停 */
    PAUSED,
    /** 对话中 */
    DIALOG,
    /** 过场动画 */
    CUTSCENE,
    /** 游戏结束 */
    GAME_OVER,
    /** 游戏开场等待*/
    OPEN_WAITING,
    /** 游戏开场动画播放*/
    OPEN_ANIMATING,
}
