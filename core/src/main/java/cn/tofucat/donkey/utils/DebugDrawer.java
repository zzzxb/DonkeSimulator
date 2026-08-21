package cn.tofucat.donkey.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

/**
 * 调试绘制工具，用于可视化碰撞盒、路径等调试信息。
 *
 * <p>使用示例：
 * <pre>
 * DebugDrawer.enable();
 * DebugDrawer.drawRect(batch, 10, 10, 100, 100);
 * DebugDrawer.drawCircle(batch, 50, 50, 20);
 * </pre>
 *
 * @author zzzxb
 * @since 2026/8/15
 */
public class DebugDrawer {
    private static final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private static boolean enabled = false;
    private static Color defaultColor = Color.RED;

    private DebugDrawer() {}

    /**
     * 启用调试绘制。
     */
    public static void enable() {
        enabled = true;
    }

    /**
     * 禁用调试绘制。
     */
    public static void disable() {
        enabled = false;
    }

    /**
     * 切换调试绘制开关。
     */
    public static void toggle() {
        enabled = !enabled;
    }

    /**
     * 检查调试绘制是否启用。
     *
     * @return true 如果启用
     */
    public static boolean isEnabled() {
        return enabled;
    }

    /**
     * 设置默认颜色。
     *
     * @param color 颜色
     */
    public static void setDefaultColor(Color color) {
        defaultColor = color;
    }

    /**
     * 绘制矩形边框。
     *
     * @param batch SpriteBatch 实例
     * @param x     矩形 X 坐标
     * @param y     矩形 Y 坐标
     * @param w     矩形宽度
     * @param h     矩形高度
     */
    public static void drawRect(SpriteBatch batch, float x, float y, float w, float h) {
        drawRect(batch, x, y, w, h, defaultColor);
    }

    /**
     * 绘制矩形边框。
     *
     * @param batch SpriteBatch 实例
     * @param rect  矩形
     */
    public static void drawRect(SpriteBatch batch, Rectangle rect) {
        drawRect(batch, rect.x, rect.y, rect.width, rect.height, defaultColor);
    }

    /**
     * 绘制矩形边框。
     *
     * @param batch SpriteBatch 实例
     * @param x     矩形 X 坐标
     * @param y     矩形 Y 坐标
     * @param w     矩形宽度
     * @param h     矩形高度
     * @param color 颜色
     */
    public static void drawRect(SpriteBatch batch, float x, float y, float w, float h, Color color) {
        if (!enabled) return;
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(x, y, w, h);
        shapeRenderer.end();
        batch.begin();
    }

    /**
     * 绘制圆形边框。
     *
     * @param batch  SpriteBatch 实例
     * @param x      圆心 X 坐标
     * @param y      圆心 Y 坐标
     * @param radius 半径
     */
    public static void drawCircle(SpriteBatch batch, float x, float y, float radius) {
        drawCircle(batch, x, y, radius, defaultColor);
    }

    /**
     * 绘制圆形边框。
     *
     * @param batch  SpriteBatch 实例
     * @param x      圆心 X 坐标
     * @param y      圆心 Y 坐标
     * @param radius 半径
     * @param color  颜色
     */
    public static void drawCircle(SpriteBatch batch, float x, float y, float radius, Color color) {
        if (!enabled) return;
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.circle(x, y, radius);
        shapeRenderer.end();
        batch.begin();
    }

    /**
     * 绘制圆形边框。
     *
     * @param batch  SpriteBatch 实例
     * @param center 圆心
     * @param radius 半径
     */
    public static void drawCircle(SpriteBatch batch, Vector2 center, float radius) {
        drawCircle(batch, center.x, center.y, radius, defaultColor);
    }

    /**
     * 绘制线段。
     *
     * @param batch SpriteBatch 实例
     * @param x1    起点 X
     * @param y1    起点 Y
     * @param x2    终点 X
     * @param y2    终点 Y
     */
    public static void drawLine(SpriteBatch batch, float x1, float y1, float x2, float y2) {
        drawLine(batch, x1, y1, x2, y2, defaultColor);
    }

    /**
     * 绘制线段。
     *
     * @param batch SpriteBatch 实例
     * @param x1    起点 X
     * @param y1    起点 Y
     * @param x2    终点 X
     * @param y2    终点 Y
     * @param color 颜色
     */
    public static void drawLine(SpriteBatch batch, float x1, float y1, float x2, float y2, Color color) {
        if (!enabled) return;
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.line(x1, y1, x2, y2);
        shapeRenderer.end();
        batch.begin();
    }

    /**
     * 绘制填充矩形。
     *
     * @param batch SpriteBatch 实例
     * @param x     矩形 X 坐标
     * @param y     矩形 Y 坐标
     * @param w     矩形宽度
     * @param h     矩形高度
     * @param color 颜色
     */
    public static void drawFilledRect(SpriteBatch batch, float x, float y, float w, float h, Color color) {
        if (!enabled) return;
        batch.end();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(x, y, w, h);
        shapeRenderer.end();
        batch.begin();
    }

    /**
     * 释放 ShapeRenderer 资源。
     */
    public static void dispose() {
        shapeRenderer.dispose();
    }
}
