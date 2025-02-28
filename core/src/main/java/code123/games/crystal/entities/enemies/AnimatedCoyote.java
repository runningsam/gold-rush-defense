package code123.games.crystal.entities.enemies;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import code123.games.crystal.animation.AnimatedEntity;
import code123.games.crystal.AssetManager;
import code123.games.crystal.entities.Enemy;
import com.badlogic.gdx.Gdx;

public class AnimatedCoyote extends Enemy {
    private AnimatedEntity animation;
    private String currentAnimation = "idle";
    private boolean useAnimation = false;  // 添加标志来跟踪是否使用动画

    public AnimatedCoyote(Array<Vector2> path) {
        super("coyote", path);
        this.health = 120;
        this.maxHealth = 120;
        this.speed = 95f;
        this.reward = 15;

        // 创建动画实体
        animation = AssetManager.getInstance()
            .getSkeletonManager()
            .createAnimatedEntity("coyote");

        if (animation == null) {
            Gdx.app.error("AnimatedCoyote", "Failed to create animation, falling back to sprite");
            useAnimation = false;
        } else {
            useAnimation = true;
            // 设置初始动画
            animation.playAnimation("idle", true);
        }
    }

    @Override
    public void update(float delta) {
        super.update(delta);

        if (useAnimation && animation != null) {
            // 更新动画位置和状态
            animation.setPosition(position.x, position.y);
            animation.update(delta);

            // 根据移动状态切换动画
            String newAnimation = velocity.len() > 1 ? "walk" : "idle";
            if (!newAnimation.equals(currentAnimation)) {
                currentAnimation = newAnimation;
                animation.playAnimation(currentAnimation, true);
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (useAnimation && animation != null) {
            // 渲染骨骼动画
            AssetManager.getInstance()
                .getSkeletonManager()
                .render(batch, animation);
        } else {
            // 如果动画创建失败，回退到使用普通精灵渲染
            super.render(batch);
        }

        // 渲染生命值条
        float healthPercentage = health / maxHealth;
        float barWidth = 32;
        float barHeight = 4;
        float barY = position.y + 32 + 2;  // 32是假设的动画高度

        // 绘制血条背景（黑色）
        batch.setColor(0, 0, 0, 0.8f);
        batch.draw(AssetManager.getInstance().getPixel(),
            position.x - barWidth/2,
            barY,
            barWidth,
            barHeight);

        // 绘制当前血量（红色）
        batch.setColor(1, 0, 0, 0.8f);
        batch.draw(AssetManager.getInstance().getPixel(),
            position.x - barWidth/2,
            barY,
            barWidth * healthPercentage,
            barHeight);

        // 重置颜色
        batch.setColor(1, 1, 1, 1);
    }
}
