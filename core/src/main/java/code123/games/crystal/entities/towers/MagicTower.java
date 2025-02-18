package code123.games.crystal.entities.towers;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import code123.games.crystal.entities.Tower;
import code123.games.crystal.entities.Enemy;
import code123.games.crystal.effects.ProjectileEffect;
import code123.games.crystal.effects.MagicCircleEffect;
import code123.games.crystal.GameWorld;

public class MagicTower extends Tower {
    private float splashRadius = 50f;  // 溅射范围

    public MagicTower(Vector2 position) {
        super(position);
        this.range = 150f;
        this.damage = 20f;  // 降低单体伤害，因为有范围效果
        this.attackSpeed = 1.0f;
        this.cost = 150;
        
        initSprite("magic");  // 修改为与atlas中匹配的名称
        System.out.println("MagicTower created at " + position + ", sprite: " + (sprite != null));  // 添加日志
    }

    @Override
    public void update(float delta, Array<Enemy> enemies) {
        attackTimer -= delta;
        
        if (canAttack()) {
            for (Enemy enemy : enemies) {
                if (isInRange(enemy)) {
                    attack(enemy);
                    attackTimer = 1f / attackSpeed;
                    break;
                }
            }
        }
    }

    @Override
    protected void attack(Enemy enemy) {
        if (enemy != null) {
            createMagicEffect(enemy);
            
            // 对主要目标造成全额伤害
            enemy.takeDamage(damage);
            
            // 对范围内的其他敌人造成溅射伤害
            Vector2 targetPos = enemy.getPosition();
            for (Enemy otherEnemy : GameWorld.getInstance().getEnemies()) {
                if (otherEnemy != enemy && !otherEnemy.isDead() && 
                    targetPos.dst(otherEnemy.getPosition()) <= splashRadius) {
                    float splashDamage = damage * 0.5f;
                    otherEnemy.takeDamage(splashDamage);
                }
            }
        }
    }

    private void createMagicEffect(Enemy target) {
        // 创建主要的魔法弹效果
        ProjectileEffect mainEffect = new ProjectileEffect(
            new Vector2(position.x + 16, position.y + 32),
            target,
            "magic_bolt",
            damage
        );
        GameWorld.addEffect(mainEffect);
        
        // 创建魔法光环效果
        createMagicCircle(target.getPosition());
    }

    private void createMagicCircle(Vector2 center) {
        // 创建魔法光环效果（这需要一个新的效果类）
        MagicCircleEffect circleEffect = new MagicCircleEffect(
            center,
            splashRadius,
            0.5f  // 持续时间
        );
        GameWorld.addEffect(circleEffect);
    }
}
