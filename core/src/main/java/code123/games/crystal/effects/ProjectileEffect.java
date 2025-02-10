package code123.games.crystal.effects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import code123.games.crystal.AssetManager;
import code123.games.crystal.entities.Enemy;

public class ProjectileEffect extends Effect {
    private static final float FADE_TIME = 0.2f;  // 淡入淡出时间
    private Vector2 start;
    private Vector2 target;
    private Vector2 current;
    private Sprite sprite;
    private float speed = 400f; // 增加速度
    private Enemy targetEnemy;
    private float damage;  // 添加伤害值
    private boolean hasDamaged;  // 是否已经造成伤害
    private float alpha = 0f;  // 初始透明度为0

    public ProjectileEffect(Vector2 start, Enemy enemy, String type, float damage) {
        super(start, 2f);
        this.start = start.cpy();
        this.current = start.cpy();  // 先初始化current
        this.targetEnemy = enemy;
        this.target = predictTargetPosition(enemy);  // 再预测目标位置
        this.damage = damage;
        this.hasDamaged = false;
        
        // 根据类型加载不同的精灵
        sprite = AssetManager.getInstance().createSprite("effects", type);
        System.out.println("ProjectileEffect created: type=" + type + ", sprite=" + (sprite != null));
        
        if (sprite != null) {
            sprite.setOriginCenter();
            sprite.setAlpha(alpha);  // 设置初始透明度
        }
    }

    private Vector2 predictTargetPosition(Enemy enemy) {
        // 计算到目标的距离
        float distanceToTarget = current.dst(enemy.getPosition());
        
        // 计算子弹飞行时间
        float timeToTarget = distanceToTarget / speed;
        
        // 获取敌人速度
        Vector2 enemyVelocity = enemy.getVelocity();
        
        // 预测位置
        Vector2 predictedPos = enemy.getPosition().cpy().add(
            enemyVelocity.x * timeToTarget,
            enemyVelocity.y * timeToTarget
        );
        
        return predictedPos;
    }

    @Override
    public void update(float delta) {
        timer -= delta;
        
        // 淡入效果
        if (alpha < 1f) {
            alpha = Math.min(1f, alpha + delta / FADE_TIME);
        }
        
        if (!targetEnemy.isDead()) {
            // 持续更新目标位置
            target = predictTargetPosition(targetEnemy);
            
            // 计算方向
            Vector2 dir = target.cpy().sub(current).nor();
            
            // 移动
            current.add(dir.scl(speed * delta));
            
            // 检查是否接近目标
            float distToTarget = current.dst(target);
            if (distToTarget < 30f) {  // 开始淡出
                alpha = Math.max(0f, distToTarget / 30f);
            }
            
            // 检查是否击中目标
            if (distToTarget < 5f && !hasDamaged) {
                targetEnemy.takeDamage(damage);
                hasDamaged = true;
                isFinished = true;
            }
            
            // 更新精灵位置和旋转
            if (sprite != null) {
                sprite.setPosition(current.x - sprite.getWidth()/2, 
                                 current.y - sprite.getHeight()/2);
                sprite.setRotation(dir.angleDeg());
                sprite.setAlpha(alpha);  // 更新透明度
            }
        } else {
            isFinished = true;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        if (sprite != null) {
            Color oldColor = batch.getColor();
            batch.setColor(1, 1, 1, alpha);  // 设置批处理的透明度
            sprite.draw(batch);
            batch.setColor(oldColor);  // 恢复原来的颜色
        }
    }
} 