package cn.tofucat.donkey.screen;

import cn.tofucat.donkey.utils.DisposeUtils;
import cn.tofucat.donkey.utils.Log;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ObjectSet;

/**
 * 屏幕基类，提供统一的资源管理和释放能力。
 *
 * <p>使用示例：
 * <pre>
 * public class GameScreen extends ScreenAbstract {
 *     private Texture texture;
 *     private SpriteBatch batch;
 *
 *     &#64;Override
 *     public void show() {
 *         texture = new Texture("ui/bg.png");
 *         batch = new SpriteBatch();
 *         track(texture);  // 自动管理释放
 *         track(batch);
 *     }
 * }
 * </pre>
 *
 * @author zzzxb
 * @since 2026/8/15
 */
public abstract class ScreenAbstract extends ScreenAdapter {

    /**
     * 需要自动释放的资源列表
     */
    private final ObjectSet<Disposable> trackedResources = new ObjectSet<>();

    /**
     * 屏幕名称（用于日志）
     */
    private String screenName;

    /**
     * 构造方法，自动获取类名作为屏幕名称。
     */
    public ScreenAbstract() {
        this.screenName = getClass().getSimpleName();
    }

    /**
     * 构造方法，指定屏幕名称。
     *
     * @param screenName 屏幕名称
     */
    public ScreenAbstract(String screenName) {
        this.screenName = screenName;
    }

    /**
     * 跟踪资源，在屏幕销毁时自动释放。
     *
     * @param resource 要跟踪的资源
     * @param <T>      资源类型
     * @return 资源本身（支持链式调用）
     */
    protected <T extends Disposable> T track(T resource) {
        if (resource != null) {
            trackedResources.add(resource);
        }
        return resource;
    }

    /**
     * 从跟踪列表中移除资源（不再自动释放）。
     *
     * @param resource 要移除的资源
     * @param <T>      资源类型
     * @return 资源本身
     */
    protected <T extends Disposable> T untrack(T resource) {
        if (resource != null) {
            trackedResources.remove(resource);
        }
        return resource;
    }

    /**
     * 释放单个资源（使用 DisposeUtils）。
     *
     * @param resource 要释放的资源
     * @param <T>      资源类型
     */
    protected <T extends Disposable> void dispose(T resource) {
        DisposeUtils.dispose(resource);
    }

    /**
     * 释放多个资源。
     *
     * @param resources 要释放的资源数组
     */
    protected void dispose(Disposable... resources) {
        for (Disposable resource : resources) {
            DisposeUtils.dispose(resource);
        }
    }

    /**
     * 释放所有被跟踪的资源。
     */
    protected void disposeAllTracked() {
        for (Disposable resource : trackedResources) {
            DisposeUtils.dispose(resource);
        }
        trackedResources.clear();
    }

    /**
     * 获取当前已跟踪资源数量。
     *
     * @return 资源数量
     */
    protected int getTrackedCount() {
        return trackedResources.size;
    }

    /**
     * 检查屏幕是否已释放。
     */
    private boolean disposed = false;

    protected boolean isDisposed() {
        return disposed;
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }
        disposed = true;
        Log.debug("ScreenAbstract", "整在释放资源...({}个)", trackedResources.size);
        disposeAllTracked();
        Log.debug("ScreenAbstract", "资源释放完成");
    }
}
