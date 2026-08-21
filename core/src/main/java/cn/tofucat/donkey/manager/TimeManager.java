package cn.tofucat.donkey.manager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 计时器管理器，提供延迟执行、定时任务等功能。
 *
 * <p>使用示例：
 * <pre>
 * TimeManager.delay(2.0f, () -> System.out.println("2秒后执行"));
 * TimeManager.interval(0.5f, () -> System.out.println("每0.5秒执行"));
 * </pre>
 *
 * @author zzzxb
 * @since 2026/8/15
 */
public class TimeManager {

    private static final List<TimerTask> tasks = new ArrayList<>();

    private TimeManager() {}

    /**
     * 延迟执行任务。
     *
     * @param seconds  延迟秒数
     * @param callback 要执行的任务
     * @return 创建的 TimerTask 对象，可用于取消
     */
    public static TimerTask delay(float seconds, Runnable callback) {
        TimerTask task = new TimerTask(seconds, callback, false);
        tasks.add(task);
        return task;
    }

    /**
     * 定时重复执行任务。
     *
     * @param seconds  间隔秒数
     * @param callback 要执行的任务
     * @return 创建的 TimerTask 对象，可用于取消
     */
    public static TimerTask interval(float seconds, Runnable callback) {
        TimerTask task = new TimerTask(seconds, callback, true);
        tasks.add(task);
        return task;
    }

    /**
     * 每帧更新计时器，必须在 render 中调用。
     *
     * @param delta 帧间隔时间
     */
    public static void update(float delta) {
        Iterator<TimerTask> iterator = tasks.iterator();
        while (iterator.hasNext()) {
            TimerTask task = iterator.next();
            task.update(delta);
            if (task.isDone() && !task.isRepeat()) {
                iterator.remove();
            }
        }
    }

    /**
     * 取消所有任务。
     */
    public static void clear() {
        tasks.clear();
    }

    /**
     * 获取当前任务数量。
     *
     * @return 任务数量
     */
    public static int taskCount() {
        return tasks.size();
    }

    /**
     * 计时任务类。
     */
    public static class TimerTask {
        private float remaining;
        private final Runnable callback;
        private final boolean repeat;
        private boolean done = false;
        private boolean cancelled = false;

        TimerTask(float seconds, Runnable callback, boolean repeat) {
            this.remaining = seconds;
            this.callback = callback;
            this.repeat = repeat;
        }

        void update(float delta) {
            if (done || cancelled) {
                return;
            }
            remaining -= delta;
            if (remaining <= 0) {
                callback.run();
                if (repeat) {
                    remaining = 1.0f; // 重置间隔
                } else {
                    done = true;
                }
            }
        }

        /**
         * 取消任务。
         */
        public void cancel() {
            this.cancelled = true;
            this.done = true;
        }

        public boolean isDone() {
            return done || cancelled;
        }

        public boolean isRepeat() {
            return repeat;
        }

        public float getRemaining() {
            return remaining;
        }
    }
}
