package code123.games.crystal.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;  // 使用更新的GL30
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class MagicCircleEffect extends Effect {
    private Vector2 center;
    private float radius;
    private float maxRadius;
    private float alpha = 0.8f;
    private static final float EXPAND_SPEED = 1.5f;  // 光环扩张速度

    public MagicCircleEffect(Vector2 center, float maxRadius, float duration) {
        super(center, duration);
        this.center = center.cpy();
        this.radius = 0;  // 从0开始扩张
        this.maxRadius = maxRadius;
    }

    @Override
    public void update(float delta) {
        timer -= delta;
        
        // 扩张光环
        radius = Math.min(maxRadius, radius + maxRadius * EXPAND_SPEED * delta);
        
        // 淡出效果
        alpha = Math.max(0, timer / duration);
        
        if (timer <= 0) {
            isFinished = true;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
        
        batch.end();
        
        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);  // 添加混合模式
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.7f, 0.3f, 0.9f, alpha);
        
        for (float r = radius; r > 0; r -= 5) {
            shapeRenderer.circle(center.x, center.y, r);
        }
        
        shapeRenderer.end();
        Gdx.gl.glDisable(GL30.GL_BLEND);
        
        batch.begin();
    }
} 