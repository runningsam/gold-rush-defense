package code123.games.crystal.entities;

import code123.games.crystal.animation.AnimatedEntity;
import code123.games.crystal.AssetManager;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class AnimatedEnemy extends Enemy {
    private AnimatedEntity animation;
    private String currentAnimation = "idle";
    
    public AnimatedEnemy(Array<Vector2> path) {
        super("animated", path);
        
        // 创建动画实体
        animation = AssetManager.getInstance()
            .getSkeletonManager()
            .createAnimatedEntity("enemy");
            
        // 设置初始动画
        animation.playAnimation("idle", true);
        animation.setScale(0.5f); // 根据需要调整缩放
    }
    
    @Override
    public void update(float delta) {
        super.update(delta);
        
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
    
    @Override
    public void render(SpriteBatch batch) {
        // 渲染骨骼动画而不是精灵
        AssetManager.getInstance()
            .getSkeletonManager()
            .render(batch, animation);
    }
} 