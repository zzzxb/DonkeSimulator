package cn.tofucat.donkey.utils;

import com.badlogic.gdx.math.Vector2;

import java.util.LinkedList;
import java.util.Queue;

/**
 * 相机动画序列，支持按顺序执行多个相机动作。
 *
 * <p>使用示例：
 * <pre>
 * CameraSequence seq = new CameraSequence(controller);
 * seq.thenZoomIn(0.5f, 1.0f)
 *    .thenMoveTo(400, 300, 1.0f)
 *    .thenShake(10f, 0.5f)
 *    .thenZoomOut(2.0f, 1.0f)
 *    .start();
 * </pre>
 *
 * @author zzzxb
 * @since 2026/8/15
 */
public class CameraSequence {

    private final CameraController controller;
    private final Queue<Step> steps = new LinkedList<>();
    private boolean isRunning = false;
    private Step currentStep = null;
    private Runnable onComplete;

    /** 单个动画步骤 */
    private static abstract class Step {
        abstract void execute(CameraController controller);
        abstract boolean isComplete(CameraController controller);
        abstract void update(CameraController controller, float delta);
        abstract void onStart(CameraController controller);
    }

    public CameraSequence(CameraController controller) {
        this.controller = controller;
    }

    /**
     * 添加平滑拉近步骤。
     */
    public CameraSequence thenZoomIn(float targetZoom, float duration) {
        steps.add(new ZoomStep(targetZoom, duration, true));
        return this;
    }

    /**
     * 添加平滑拉远步骤。
     */
    public CameraSequence thenZoomOut(float targetZoom, float duration) {
        steps.add(new ZoomStep(targetZoom, duration, false));
        return this;
    }

    /**
     * 添加平滑移动步骤。
     */
    public CameraSequence thenMoveTo(float x, float y, float duration) {
        steps.add(new MoveStep(x, y, duration));
        return this;
    }

    /**
     * 添加震动步骤。
     */
    public CameraSequence thenShake(float intensity, float duration) {
        steps.add(new ShakeStep(intensity, duration));
        return this;
    }

    /**
     * 添加延迟步骤。
     */
    public CameraSequence thenDelay(float duration) {
        steps.add(new DelayStep(duration));
        return this;
    }

    /**
     * 添加自定义步骤。
     */
    public CameraSequence then(Runnable action, float duration) {
        steps.add(new CustomStep(action, duration));
        return this;
    }

    /**
     * 注册序列完成回调。
     */
    public CameraSequence onComplete(Runnable callback) {
        this.onComplete = callback;
        return this;
    }

    /**
     * 开始执行序列。
     */
    public CameraSequence start() {
        if (!steps.isEmpty()) {
            isRunning = true;
            nextStep();
        }
        return this;
    }

    /**
     * 停止序列。
     */
    public void stop() {
        isRunning = false;
        currentStep = null;
        steps.clear();
    }

    /**
     * 每帧更新，必须在 render 中调用。
     */
    public void update(float delta) {
        if (!isRunning || currentStep == null) {
            return;
        }

        currentStep.update(controller, delta);

        if (currentStep.isComplete(controller)) {
            nextStep();
        }
    }

    private void nextStep() {
        if (steps.isEmpty()) {
            isRunning = false;
            currentStep = null;
            if (onComplete != null) {
                onComplete.run();
            }
            return;
        }

        currentStep = steps.poll();
        currentStep.onStart(controller);
        currentStep.execute(controller);
    }

    public boolean isRunning() {
        return isRunning;
    }

    // ==================== 步骤实现 ====================

    private static class ZoomStep extends Step {
        private final float targetZoom;
        private final float duration;
        private final boolean isIn;
        private boolean started = false;

        ZoomStep(float targetZoom, float duration, boolean isIn) {
            this.targetZoom = targetZoom;
            this.duration = duration;
            this.isIn = isIn;
        }

        @Override
        void onStart(CameraController controller) {
            // 不做特殊处理
        }

        @Override
        void execute(CameraController controller) {
            if (isIn) {
                controller.smoothZoomIn(targetZoom, duration);
            } else {
                controller.smoothZoomOut(targetZoom, duration);
            }
            started = true;
        }

        @Override
        boolean isComplete(CameraController controller) {
            return started && !controller.isZooming();
        }

        @Override
        void update(CameraController controller, float delta) {
            // 不需要额外更新，由 controller 自己处理
        }
    }

    private static class MoveStep extends Step {
        private final float x, y;
        private final float duration;
        private boolean started = false;

        MoveStep(float x, float y, float duration) {
            this.x = x;
            this.y = y;
            this.duration = duration;
        }

        @Override
        void onStart(CameraController controller) {}

        @Override
        void execute(CameraController controller) {
            controller.smoothMoveTo(x, y, duration);
            started = true;
        }

        @Override
        boolean isComplete(CameraController controller) {
            return started && !controller.isMoving();
        }

        @Override
        void update(CameraController controller, float delta) {}
    }

    private static class ShakeStep extends Step {
        private final float intensity;
        private final float duration;
        private boolean started = false;

        ShakeStep(float intensity, float duration) {
            this.intensity = intensity;
            this.duration = duration;
        }

        @Override
        void onStart(CameraController controller) {}

        @Override
        void execute(CameraController controller) {
            controller.shake(intensity, duration);
            started = true;
        }

        @Override
        boolean isComplete(CameraController controller) {
            return started && !controller.isShaking();
        }

        @Override
        void update(CameraController controller, float delta) {}
    }

    private static class DelayStep extends Step {
        private final float duration;
        private float timer = 0f;

        DelayStep(float duration) {
            this.duration = duration;
        }

        @Override
        void onStart(CameraController controller) {
            timer = 0f;
        }

        @Override
        void execute(CameraController controller) {}

        @Override
        boolean isComplete(CameraController controller) {
            return timer >= duration;
        }

        @Override
        void update(CameraController controller, float delta) {
            timer += delta;
        }
    }

    private static class CustomStep extends Step {
        private final Runnable action;
        private final float duration;
        private float timer = 0f;
        private boolean executed = false;

        CustomStep(Runnable action, float duration) {
            this.action = action;
            this.duration = duration;
        }

        @Override
        void onStart(CameraController controller) {
            timer = 0f;
            executed = false;
        }

        @Override
        void execute(CameraController controller) {
            if (action != null) {
                action.run();
            }
            executed = true;
        }

        @Override
        boolean isComplete(CameraController controller) {
            return executed && timer >= duration;
        }

        @Override
        void update(CameraController controller, float delta) {
            timer += delta;
        }
    }
}
