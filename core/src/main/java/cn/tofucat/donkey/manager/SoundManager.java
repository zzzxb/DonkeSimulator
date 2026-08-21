package cn.tofucat.donkey.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * 音效管理器，统一管理游戏音效和背景音乐。
 *
 * <p>使用示例：
 * <pre>
 * SoundManager.playSfx("sound/click.mp3");
 * SoundManager.playMusic("music/bgm.mp3");
 * SoundManager.setMasterVolume(0.8f);
 * </pre>
 *
 * @author zzzxb
 * @since 2026/8/15
 */
public class SoundManager {

    private static float masterVolume = 1.0f;
    private static float musicVolume = 0.7f;
    private static float sfxVolume = 1.0f;

    private static Music currentMusic;
    private static String currentMusicPath;

    /** 音效缓存，避免重复加载 */
    private static final ObjectMap<String, Sound> soundCache = new ObjectMap<>();

    private SoundManager() {}

    /**
     * 播放音效。
     *
     * @param path 音效文件路径（assets 相对路径）
     */
    public static void playSfx(String path) {
        Sound sound = soundCache.get(path);
        if (sound == null) {
            sound = Gdx.audio.newSound(Gdx.files.internal(path));
            soundCache.put(path, sound);
        }
        sound.play(sfxVolume * masterVolume);
    }

    /**
     * 播放音效，可指定音量。
     *
     * @param path   音效文件路径
     * @param volume 音量（0.0 ~ 1.0）
     */
    public static void playSfx(String path, float volume) {
        Sound sound = soundCache.get(path);
        if (sound == null) {
            sound = Gdx.audio.newSound(Gdx.files.internal(path));
            soundCache.put(path, sound);
        }
        sound.play(MathUtils.clamp(volume, 0f, 1f) * sfxVolume * masterVolume);
    }

    /**
     * 播放背景音乐。
     *
     * @param path 音乐文件路径
     */
    public static void playMusic(String path) {
        if (path.equals(currentMusicPath) && currentMusic != null) {
            return;
        }
        stopMusic();
        currentMusic = Gdx.audio.newMusic(Gdx.files.internal(path));
        currentMusic.setVolume(musicVolume * masterVolume);
        currentMusic.setLooping(true);
        currentMusic.play();
        currentMusicPath = path;
    }

    /**
     * 停止背景音乐。
     */
    public static void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
            currentMusic = null;
            currentMusicPath = null;
        }
    }

    /**
     * 暂停背景音乐。
     */
    public static void pauseMusic() {
        if (currentMusic != null && currentMusic.isPlaying()) {
            currentMusic.pause();
        }
    }

    /**
     * 恢复背景音乐。
     */
    public static void resumeMusic() {
        if (currentMusic != null && !currentMusic.isPlaying()) {
            currentMusic.play();
        }
    }

    /**
     * 设置主音量。
     *
     * @param volume 音量（0.0 ~ 1.0）
     */
    public static void setMasterVolume(float volume) {
        masterVolume = MathUtils.clamp(volume, 0f, 1f);
        updateMusicVolume();
    }

    /**
     * 设置音乐音量。
     *
     * @param volume 音量（0.0 ~ 1.0）
     */
    public static void setMusicVolume(float volume) {
        musicVolume = MathUtils.clamp(volume, 0f, 1f);
        updateMusicVolume();
    }

    /**
     * 设置音效音量。
     *
     * @param volume 音量（0.0 ~ 1.0）
     */
    public static void setSfxVolume(float volume) {
        sfxVolume = MathUtils.clamp(volume, 0f, 1f);
    }

    private static void updateMusicVolume() {
        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume * masterVolume);
        }
    }

    /**
     * 释放所有音效资源。
     */
    public static void dispose() {
        stopMusic();
        for (Sound sound : soundCache.values()) {
            sound.dispose();
        }
        soundCache.clear();
    }
}
