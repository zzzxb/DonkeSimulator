package cn.tofucat.donkey.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;

/**
 *
 * @author zzzxb
 * 2026/8/14
 */
public class DisposeUtils {

    public static <T extends Disposable> void dispose(T t) {
        dispose(t, t.getClass().getSimpleName());
    }

    public static <T extends Disposable> void dispose(T t, String desc) {
        if (t != null) {
            t.dispose();
            Gdx.app.log("dispose", desc);
        }
    }

}
