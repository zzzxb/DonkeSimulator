package cn.tofucat.donkey.utils;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

/**
 * 相机控制器，提供平滑缩放、移动、震动等镜头效果。
 *
 * <p>使用示例：
 * <pre>
 * OrthographicCamera camera = new OrthographicCamera(800, 480);
 * CameraController controller = new CameraController(camera);
 *
 * // 每帧更新
 * controller.update(delta);
 *
 * // 平滑拉近
 * controller.smoothZoomIn(0.5f, 0.5f);
 * </pre>
 *
 * @author zzzxb
 * @since 2026/8/15
 */
public class CameraController {

    /**
     * 缓动函数类型。
     */
    public enum EaseType {
        LINEAR,
        EASE_IN,
        EASE_OUT,
        EASE_IN_OUT,
        EASE_OUT_BACK
    }

    private final OrthographicCamera camera;

    /** 最小缩放值（最近），默认 0.11f */
    private float zoomMin = 0.11f;
    /** 最大缩放值（最远），默认 2.0f */
    private float zoomMax = 2.0f;

    /** 缩放起始值 */
    private float zoomStart = 1.0f;
    /** 缩放目标值 */
    private float zoomEnd = 1.0f;
    /** 缩放动画持续时间（秒） */
    private float zoomDuration = 0.5f;
    /** 缩放动画已耗时（秒） */
    private float zoomTimer = 0f;
    /** 是否正在执行缩放动画 */
    private boolean isZooming = false;
    /** 缩放缓动类型 */
    private EaseType zoomEaseType = EaseType.EASE_IN_OUT;
    /** 缩放完成回调 */
    private Runnable onZoomComplete;

    private final Vector2 moveStart = new Vector2();
    private final Vector2 moveEnd = new Vector2();
    private float moveDuration = 0.5f;
    private float moveTimer = 0f;
    private boolean isMoving = false;
    private EaseType moveEaseType = EaseType.EASE_IN_OUT;
    private Runnable onMoveComplete;

    private float shakeIntensity = 0f;
    private float shakeDuration = 0f;
    private float shakeTimer = 0f;
    private boolean isShaking = false;
    /** 震动开始时的相机位置，用于震动结束后恢复 */
    private final Vector3 shakeOrigin = new Vector3();

    private boolean boundsEnabled = false;
    private float boundsMinX = Float.NEGATIVE_INFINITY;
    private float boundsMaxX = Float.POSITIVE_INFINITY;
    private float boundsMinY = Float.NEGATIVE_INFINITY;
    private float boundsMaxY = Float.POSITIVE_INFINITY;

    private Vector2 followTarget = null;
    private float followSpeed = 5.0f;
    private boolean isFollowing = false;
    private float oldX;
    private float oldY;

    /**
     * 构造相机控制器。
     *
     * @param camera 要控制的相机
     */
    public CameraController(OrthographicCamera camera) {
        this.camera = camera;
        this.shakeOrigin.set(camera.position);
    }

    /**
     * 立即切换到最近距离（最小缩放值）。
     *
     * @return this
     */
    public CameraController zoomToNearest() {
        camera.zoom = zoomMin;
        camera.update();
        return this;
    }

    /**
     * 立即切换到最远距离（最大缩放值）。
     *
     * @return this
     */
    public CameraController zoomToFarthest() {
        camera.zoom = zoomMax;
        camera.update();
        return this;
    }

    /**
     * 立即缩放到指定值。
     *
     * @param targetZoom 目标缩放值，自动约束在 [zoomMin, zoomMax] 范围内
     * @return this
     */
    public CameraController zoomTo(float targetZoom) {
        camera.zoom = MathUtils.clamp(targetZoom, zoomMin, zoomMax);
        camera.update();
        return this;
    }

    /**
     * 步进式拉近镜头（立即生效，无动画）。
     *
     * @param speed 步进速度
     * @return this
     */
    public CameraController zoomIn(float speed) {
        camera.zoom = Math.max(zoomMin, camera.zoom - speed);
        camera.update();
        return this;
    }

    /**
     * 步进式拉远镜头（立即生效，无动画）。
     *
     * @param speed 步进速度
     * @return this
     */
    public CameraController zoomOut(float speed) {
        camera.zoom = Math.min(zoomMax, camera.zoom + speed);
        camera.update();
        return this;
    }

    /**
     * 平滑拉近镜头。
     *
     * @param targetZoom 目标缩放值，越小越近，自动受 {@link #setZoomLimits} 约束
     * @param duration   动画持续时间（秒）
     * @return this
     */
    public CameraController smoothZoomIn(float targetZoom, float duration) {
        return smoothZoomTo(Math.max(targetZoom, zoomMin), duration);
    }

    /**
     * 平滑拉远镜头。
     *
     * @param targetZoom 目标缩放值，越大越远，自动受 {@link #setZoomLimits} 约束
     * @param duration   动画持续时间（秒）
     * @return this
     */
    public CameraController smoothZoomOut(float targetZoom, float duration) {
        return smoothZoomTo(Math.min(targetZoom, zoomMax), duration);
    }

    /**
     * 平滑缩放到指定值。
     *
     * @param targetZoom 目标缩放值，自动约束在 [zoomMin, zoomMax] 范围内
     * @param duration   动画持续时间（秒）
     * @return this
     */
    public CameraController smoothZoomTo(float targetZoom, float duration) {
        float clamped = MathUtils.clamp(targetZoom, zoomMin, zoomMax);
        this.zoomStart = camera.zoom;
        this.zoomEnd = clamped;
        this.zoomDuration = Math.max(duration, 0.05f);
        this.zoomTimer = 0f;
        this.isZooming = true;
        return this;
    }

    /**
     * 设置缩放范围限制。
     *
     * @param min 最小缩放值（最近），不得小于 0.01
     * @param max 最大缩放值（最远）
     * @return this
     */
    public CameraController setZoomLimits(float min, float max) {
        this.zoomMin = Math.max(min, 0.01f);
        this.zoomMax = Math.max(max, this.zoomMin);
        return this;
    }

    /**
     * 设置缩放动画的缓动类型。
     *
     * @param easeType 缓动类型
     * @return this
     */
    public CameraController setZoomEase(EaseType easeType) {
        this.zoomEaseType = easeType;
        return this;
    }

    /**
     * 注册缩放完成回调。
     *
     * @param callback 回调任务
     * @return this
     */
    public CameraController onZoomComplete(Runnable callback) {
        this.onZoomComplete = callback;
        return this;
    }

    /**
     * 停止缩放动画。
     *
     * @return this
     */
    public CameraController stopZoom() {
        this.isZooming = false;
        return this;
    }

    /**
     * 立即移动到目标位置（无动画）。
     *
     * @param x 目标 X 坐标
     * @param y 目标 Y 坐标
     * @return this
     */
    public CameraController moveTo(float x, float y) {
        camera.position.set(x, y, 0);
        applyBounds();
        camera.update();
        return this;
    }

    /**
     * 平滑移动到目标位置。
     *
     * @param x        目标 X 坐标
     * @param y        目标 Y 坐标
     * @param duration 动画持续时间（秒）
     * @return this
     */
    public CameraController smoothMoveTo(float x, float y, float duration) {
        this.moveStart.set(camera.position.x, camera.position.y);
        this.moveEnd.set(x, y);
        this.moveDuration = Math.max(duration, 0.05f);
        this.moveTimer = 0f;
        this.isMoving = true;
        return this;
    }

    /**
     * 设置移动动画的缓动类型。
     *
     * @param easeType 缓动类型
     * @return this
     */
    public CameraController setMoveEase(EaseType easeType) {
        this.moveEaseType = easeType;
        return this;
    }

    /**
     * 注册移动完成回调。
     *
     * @param callback 回调任务
     * @return this
     */
    public CameraController onMoveComplete(Runnable callback) {
        this.onMoveComplete = callback;
        return this;
    }

    /**
     * 停止移动动画。
     *
     * @return this
     */
    public CameraController stopMove() {
        this.isMoving = false;
        return this;
    }

    /**
     * 开始跟随目标。
     *
     * @param target    目标位置
     * @param lerpSpeed 跟随速度，值越大跟踪越快
     * @return this
     */
    public CameraController follow(Vector2 target, float lerpSpeed) {
        this.followTarget = target;
        this.followSpeed = Math.max(lerpSpeed, 0.1f);
        this.isFollowing = true;
        return this;
    }

    /**
     * 停止跟随。
     *
     * @return this
     */
    public CameraController stopFollow() {
        this.isFollowing = false;
        this.followTarget = null;
        return this;
    }

    /**
     * 触发镜头震动。
     *
     * @param intensity 震动幅度（像素）
     * @param duration  震动持续时间（秒）
     * @return this
     */
    public CameraController shake(float intensity, float duration) {
        this.shakeOrigin.set(camera.position);
        this.shakeIntensity = Math.max(intensity, 0.1f);
        this.shakeDuration = Math.max(duration, 0.05f);
        this.shakeTimer = 0f;
        this.isShaking = true;
        return this;
    }

    /**
     * 停止震动。
     *
     * @return this
     */
    public CameraController stopShake() {
        this.isShaking = false;
        if (!isMoving && !isFollowing) {
            camera.position.set(shakeOrigin);
            camera.update();
        }
        return this;
    }

    /**
     * 启用视野边界限制，防止相机移出指定范围。
     *
     * @param minX 最小 X 边界
     * @param maxX 最大 X 边界
     * @param minY 最小 Y 边界
     * @param maxY 最大 Y 边界
     * @return this
     */
    public CameraController enableBounds(float minX, float maxX, float minY, float maxY) {
        this.boundsEnabled = true;
        this.boundsMinX = minX;
        this.boundsMaxX = maxX;
        this.boundsMinY = minY;
        this.boundsMaxY = maxY;
        return this;
    }

    /**
     * 禁用视野边界限制。
     *
     * @return this
     */
    public CameraController disableBounds() {
        this.boundsEnabled = false;
        return this;
    }

    /**
     * 每帧更新控制器状态。
     *
     * @param delta 帧间隔时间（秒）
     */
    public void update(float delta) {
        updateZoom(delta);
        updateMove(delta);
        updateFollow(delta);
        updateShake(delta);

        if (!isShaking) {
            applyBounds();
        }
        oldX = camera.position.x;
        oldY = camera.position.y;
        camera.update();
    }

    public float getOldX() {
        return oldX;
    }

    public float getOldY() {
        return oldY;
    }

    private void updateZoom(float delta) {
        if (!isZooming) {
            return;
        }

        zoomTimer += delta;
        float progress = Math.min(zoomTimer / zoomDuration, 1.0f);
        float eased = applyEase(progress, zoomEaseType);

        camera.zoom = zoomStart + (zoomEnd - zoomStart) * eased;

        if (progress >= 1.0f) {
            isZooming = false;
            camera.zoom = zoomEnd;
            if (onZoomComplete != null) {
                onZoomComplete.run();
            }
        }
    }

    private void updateMove(float delta) {
        if (!isMoving) {
            return;
        }

        moveTimer += delta;
        float progress = Math.min(moveTimer / moveDuration, 1.0f);
        float eased = applyEase(progress, moveEaseType);

        float x = moveStart.x + (moveEnd.x - moveStart.x) * eased;
        float y = moveStart.y + (moveEnd.y - moveStart.y) * eased;
        camera.position.set(x, y, 0);

        if (progress >= 1.0f) {
            isMoving = false;
            camera.position.set(moveEnd.x, moveEnd.y, 0);
            if (onMoveComplete != null) {
                onMoveComplete.run();
            }
        }
    }

    private void updateFollow(float delta) {
        if (!isFollowing || followTarget == null) {
            return;
        }

        float lerpFactor = 1f - (float) Math.exp(-followSpeed * delta);
        float newX = camera.position.x + (followTarget.x - camera.position.x) * lerpFactor;
        float newY = camera.position.y + (followTarget.y - camera.position.y) * lerpFactor;
        camera.position.set(newX, newY, 0);
    }

    private void updateShake(float delta) {
        if (!isShaking) {
            return;
        }

        shakeTimer += delta;
        float progress = shakeTimer / shakeDuration;

        if (progress >= 1.0f) {
            isShaking = false;
            camera.position.set(shakeOrigin);
            return;
        }

        float intensity = shakeIntensity * (1f - progress);
        float offsetX = (MathUtils.random(-1f, 1f)) * intensity;
        float offsetY = (MathUtils.random(-1f, 1f)) * intensity;

        camera.position.set(
            shakeOrigin.x + offsetX,
            shakeOrigin.y + offsetY,
            0
        );
    }

    private void applyBounds() {
        if (!boundsEnabled) {
            return;
        }

        float halfW = camera.viewportWidth * camera.zoom / 2;
        float halfH = camera.viewportHeight * camera.zoom / 2;

        float clampedX = MathUtils.clamp(camera.position.x, boundsMinX + halfW, boundsMaxX - halfW);
        float clampedY = MathUtils.clamp(camera.position.y, boundsMinY + halfH, boundsMaxY - halfH);

        camera.position.x = clampedX;
        camera.position.y = clampedY;
    }

    private float applyEase(float t, EaseType type) {
        switch (type) {
            case LINEAR:
                return t;
            case EASE_IN:
                return t * t;
            case EASE_OUT:
                return 1f - (1f - t) * (1f - t);
            case EASE_IN_OUT:
                return t < 0.5f ? 2f * t * t : 1f - (float) Math.pow(-2f * t + 2f, 2f) / 2f;
            case EASE_OUT_BACK:
                float c1 = 1.70158f;
                float c3 = c1 + 1f;
                return 1f + c3 * (float) Math.pow(t - 1f, 3f) + c1 * (float) Math.pow(t - 1f, 2f);
            default:
                return t;
        }
    }

    public boolean isZooming() {
        return isZooming;
    }

    public boolean isMoving() {
        return isMoving;
    }

    public boolean isShaking() {
        return isShaking;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public float getZoomProgress() {
        return isZooming ? Math.min(zoomTimer / zoomDuration, 1.0f) : 1.0f;
    }

    public float getCurrentZoom() {
        return camera.zoom;
    }

    public float getZoomMax() {
        return zoomMax;
    }

    public float getZoomMin() {
        return zoomMin;
    }

    public Vector2 getPosition() {
        return new Vector2(camera.position.x, camera.position.y);
    }

    /**
     * 停止所有动画（缩放、移动、震动、跟随）。
     *
     * @return this
     */
    public CameraController stopAll() {
        stopZoom();
        stopMove();
        stopShake();
        stopFollow();
        return this;
    }

    /**
     * 重置相机到初始位置和缩放值（1.0）。
     *
     * @return this
     */
    public CameraController reset() {
        stopAll();
        camera.position.set(shakeOrigin);
        camera.zoom = 1.0f;
        camera.update();
        return this;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }
}
